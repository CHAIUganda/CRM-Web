package com.omnitech.chai.repositories.impl

import com.omnitech.chai.model.*
import com.omnitech.chai.repositories.CategoryBrandResult
import com.omnitech.chai.repositories.DetailerStockRepository
import com.omnitech.chai.repositories.ProductRepository
import com.omnitech.chai.repositories.dto.TaskDTO
import com.omnitech.chai.util.ModelFunctions
import org.neo4j.cypherdsl.expression.Expression
import org.neo4j.cypherdsl.grammar.Match
import org.springframework.data.domain.Page
import org.springframework.data.neo4j.support.Neo4jTemplate

import static com.omnitech.chai.model.Relations.*
import static com.omnitech.chai.queries.TaskQuery.mathQueryForUserTasks
import static com.omnitech.chai.util.ChaiUtils.bean
import static com.omnitech.chai.util.PageUtils.addPagination
import static grails.util.GrailsNameUtils.getNaturalName
import static java.util.Collections.EMPTY_MAP
import static org.neo4j.cypherdsl.CypherQuery.*
import static org.neo4j.cypherdsl.CypherQuery.as as az

/**
 * Created by kay on 3/19/2015.
 */
interface ITaskRepository {

    List exportAllTasks(Class type)

    List exportAllTasks(Long userId, Class type)

    Page<TaskDTO> findAllTasks(Class<TaskDTO> type, Map params)

    Page<TaskDTO> findAllTasksForUser(Long userId, Class<TaskDTO> type, Map params)

}

class TaskRepositoryImpl extends AbstractChaiRepository implements ITaskRepository {

    @Override
    List exportAllTasks(Class type) {
        def nodeName = type.simpleName.toLowerCase()
        def query = match(node(nodeName).label(type.simpleName)
                .in(CUST_TASK).node('c')
                .out(CUST_IN_SC).node('sc')
                .in(HAS_SUB_COUNTY).node('d'))
                .match(node('c').out(CUST_IN_VILLAGE).node('v')).optional()
                .match(node(nodeName).in(COMPLETED_TASK, CANCELED_TASK).node('u')).optional()
        def fields = essentialExportTaskClauses()
        def labels = ['DISTRICT', 'SUBCOUNTY', 'VILLAGE', 'OUTLET NAME', 'OUTLET TYPE', 'CANCELED_OR_COMPLETED BY']
        def className = type.simpleName
        def products = bean(ProductRepository).findAll()
        "customizeExportQuery$className"(query, fields, labels, type, products)

    }

    List exportAllTasks(Long userId, Class type) {
        def nodeName = type.simpleName.toLowerCase()
        def query = mathQueryForUserTasks(userId, type)
                .with(distinct(identifier(nodeName)))
                .match(node(nodeName).in(CUST_TASK).node('c').out(CUST_IN_SC).node('sc'))
                .match(node('sc').in(HAS_SUB_COUNTY).node('d')).optional()
                .match(node('c').out(CUST_IN_VILLAGE).node('v')).optional()
                .match(node(nodeName).in(COMPLETED_TASK, CANCELED_TASK).node('u')).optional()
        def fields = essentialExportTaskClauses()
        def labels = ['DISTRICT', 'SUBCOUNTY', 'VILLAGE', 'OUTLET NAME', 'OUTLET TYPE', 'CANCELED_OR_COMPLETED BY']
        def className = type.simpleName
        def products = bean(ProductRepository).findAllByUser(userId)
        "customizeExportQuery$className"(query, fields, labels, type, products)
    }

    private static List essentialExportTaskClauses() {
        [az(identifier('d').property('name'), 'DISTRICT'),
         az(identifier('sc').property('name'), 'SUBCOUNTY'),
         az(identifier('v').property('name'), 'VILLAGE'),
         az(identifier('c').property('outletName'), 'OUTLET NAME'),
         az(identifier('c').property('outletType'), 'OUTLET TYPE'),
         az(identifier('u').property('username'), 'CANCELED_OR_COMPLETED BY')]
    }

    private def customizeExportQueryOrder(Match query,
                                          List<Expression> queryReturnFields,
                                          List<String> queryReturnLabels,
                                          Class<Order> task,
                                          Iterable<Product> products) {
        queryReturnLabels << 'ORDER TAKEN BY'
        def varName = nodeName(task)

        query.match(node(varName).out(ORDER_TAKEN_BY).node('takenBy')).optional()
                .match(node(varName).out(HAS_PRODUCT).as('li').node('p')).optional()
        queryReturnFields << az(identifier('takenBy').property('username'), 'ORDER TAKEN BY')

        def (fieldLabels, returnFields) = getClassExportFields(task)

        queryReturnFields.addAll(returnFields)
        queryReturnLabels.addAll(fieldLabels)

        query.returns(queryReturnFields)

        def stringQuery = query.toString()

        ['quantity'].each { String property ->
            def fieldLabel = getNaturalName(property)
            stringQuery = addRepeatElementStatements(stringQuery, bean(ProductRepository).findAll()) { p ->
                def aliasName = p.name.toUpperCase() + (p.unitOfMeasure ? "-$p.unitOfMeasure" : '') + "($fieldLabel)"
                queryReturnLabels << aliasName
                def expr = "collect (case when id(p) = $p.id then li.$property else null end)"
                return "head($expr)  as `$aliasName`"
            }
        }


        return export(stringQuery, queryReturnLabels, Order)
    }


    private def customizeExportQuerySale(Match query,
                                         List<Expression> queryReturnFields,
                                         List<String> queryReturnLabels,
                                         Class<Sale> task,
                                         Iterable<Product> products) {

        def varName = nodeName(task)

        query.match(node(varName).out(HAS_PRODUCT).as('li').node('p')).optional()
                .match(node(varName).out(STOCK_PRODUCT).as(nodeName(StockLine)).node('p2')).optional()

        //queryReturnLabels << 'ORDER TAKEN BY'
        //.match(node(varName).out(ORDER_TAKEN_BY).node('takenBy')).optional()
        //queryReturnFields << az(identifier('takenBy').property('username'), 'ORDER TAKEN BY')

        def (fieldLabels, returnFields) = getClassExportFields(DirectSale, varName)

        queryReturnFields.addAll(returnFields)
        queryReturnLabels.addAll(fieldLabels)

        query.returns(queryReturnFields)

        def stringQuery = query.toString()

        //selling information
        ['quantity', 'unitPrice'].each { String property ->
            def fieldLabel = getNaturalName(property)
            stringQuery = addRepeatElementStatements(stringQuery, bean(ProductRepository).findAll()) { p ->
                def aliasName = p.name.toUpperCase() + (p.unitOfMeasure ? "-$p.unitOfMeasure" : '') + "(SALE: $fieldLabel)"
                queryReturnLabels << aliasName
                def expr = "collect (case when id(p) = $p.id then li.$property else null end)"
                return "head($expr)  as `$aliasName`"
            }
        }

        //stock information
        ['quantity'].each { String property ->
            def fieldLabel = getNaturalName(property)
            stringQuery = addRepeatElementStatements(stringQuery, bean(ProductRepository).findAll()) { p ->
                def aliasName = p.name.toUpperCase() + (p.unitOfMeasure ? "-$p.unitOfMeasure" : '') + "(STOCK: $fieldLabel)"
                queryReturnLabels << aliasName
                def expr = "collect (case when id(p2) = $p.id then stockline.$property else null end)"
                return "head($expr)  as `$aliasName`"
            }
        }


        return export(stringQuery, queryReturnLabels, DirectSale)

    }

    private def customizeExportQueryDetailerTask(Match query,
                                                 List<Expression> queryReturnFields,
                                                 List<String> queryReturnLabels,
                                                 Class<DetailerTask> task,
                                                 Iterable<Product> products) {


        def stockNode = nodeName(DetailerStock)
        query.match(node(nodeName(DetailerTask)).out(HAS_DETAILER_STOCK).node(stockNode)).optional()

        def (fieldLabels, returnFields) = getClassExportFields(task)

        queryReturnFields.addAll(returnFields)
        queryReturnLabels.addAll(fieldLabels)

        def queryString = query.returns(queryReturnFields).toString()

        //add stock quantity
        def categoriesAndBrands = bean(DetailerStockRepository).findAllCategoriesAndBrands()
        ['stockLevel', 'buyingPrice', 'sellingPrice'].each { String property ->
            def fieldLabel = getNaturalName(property)
            queryString = addRepeatElementStatements(queryString, categoriesAndBrands) { CategoryBrandResult d ->
                def aliasName = "$d.category-$d.brand-($fieldLabel)"
                queryReturnLabels << aliasName
                return "sum (case when ${stockNode}.category = '$d.category' and ${stockNode}.brand = '$d.brand' then ${stockNode}.$property else null end) as `$aliasName`"
            }
        }



        export(queryString, queryReturnLabels, task)
    }


    private static
    def <T> String addRepeatElementStatements(String stringQuery, Iterable<T> items, Closure<String> getExpression) {
        def queries = items.collect { def p -> getExpression(p) }
        def joinedExpressions = queries.join(',')
        if (joinedExpressions)
            stringQuery = "$stringQuery, $joinedExpressions"
        return stringQuery
    }


    private static def customizeExportQuery(Match query,
                                            List<Expression> queryReturnFields,
                                            List<String> queryReturnLabels,
                                            def order,
                                            Iterable<Product> products) {
        throw new UnsupportedOperationException("Export of $order is not supported")
    }

    def Page<TaskDTO> findAllTasks(Class<TaskDTO> type, Map params) {

        def label = type.simpleName
        def taskName = nodeName(type)
        def roleNeeded = type == DetailerTask ? Role.DETAILER_ROLE_NAME : Role.SALES_ROLE_NAME
        def territoryType = type == DetailerTask ? Territory.TYPE_DETAILING : Territory.TYPE_SALES

        def _query = {
            def q = match(node(taskName).label(label).values(value('status', params.status ?: 'new'))
                    .in(CUST_TASK).node(cName)
                    .out(CUST_IN_SC).node(sName)
                    .in(HAS_SUB_COUNTY).node(dName))
                    .match(node(sName).out(SC_IN_TERRITORY).node(terName).values(value('type', territoryType))).optional()
            mayBeAddSearchCriteria(taskName, q, params)
            return q
        }

        def q = addPagination(_query().returns(taskFieldsClause(taskName)), params, null)
        def cq = _query().returns(count(distinct(identifier(taskName))))

        def results = ModelFunctions.query(bean(Neo4jTemplate), q, cq, params, TaskDTO)
        def _getTerritoryUsers = { Long tid -> getTerritoryUsers(tid, roleNeeded) }.memoize()
        results.each { it.assignedUser = _getTerritoryUsers.call(it.territoryId) }

        return results
    }

    def Page<TaskDTO> findAllTasksForUser(Long userId, Class<TaskDTO> type, Map params) {

        def label = type.simpleName
        def taskName = nodeName(type)
        def roleNeeded = type == DetailerTask ? Role.DETAILER_ROLE_NAME : Role.SALES_ROLE_NAME
        def territoryType = type == DetailerTask ? Territory.TYPE_DETAILING : Territory.TYPE_SALES

        def _query = {

            def q = start(nodesById('myUser', userId))
                    .match(node('myUser').out(USER_TERRITORY, SUPERVISES_TERRITORY).node(terName).values(value('type', territoryType))
                    .in(SC_IN_TERRITORY).node(sName)
                    .in(CUST_IN_SC).node(cName)
                    .out(CUST_TASK).node(taskName).label(label).values(value('status', params.status ?: 'new')))
                    .match(node(sName).in(HAS_SUB_COUNTY).node(dName))
            mayBeAddSearchCriteria(taskName, q, params)
            return q
        }

        def q = addPagination(_query().returns(taskFieldsClause(taskName)), params, null)
        def cq = _query().returns(count(distinct(identifier(taskName))))

        def results = ModelFunctions.query(bean(Neo4jTemplate), q, cq, params, TaskDTO)
        def _getTerritoryUsers = { Long tid -> getTerritoryUsers(tid, roleNeeded) }.memoize()
        results.each { it.assignedUser = _getTerritoryUsers.call(it.territoryId) }
        return results
    }

    private static mayBeAddSearchCriteria(String taskName, Match m, Map params) {
        if (params.search) {
            def search = ModelFunctions.getWildCardRegex(params.search as String)
            m.where(and(identifier(taskName).property('description').regexp(search)
                    .or(identifier(dName).property('name').regexp(search)))
            )
        }
    }

    private static taskFieldsClause(String taskName) {
        [distinct(az(id(taskName), 'id')),
         az(identifier(taskName).property('description'), 'description'),
         az(identifier(taskName).property('dueDate'), 'dueDate'),
         az(identifier(taskName).property('completionDate'), 'completionDate'),
         az(identifier(taskName).property('status'), 'status'),
         az(identifier(cName).property('outletName'), 'customer'),
         az(identifier(dName).property('name'), 'district'),
         az(id(terName), 'territoryId')]
    }

    private List<String> getTerritoryUsers(Long territoryId, String roleNeeded) {
        if (!territoryId) return null
        def q = start(nodesById(terName, territoryId))
                .match(node(terName)
                .in(USER_TERRITORY).node(uName)
                .out(HAS_ROLE).node(rName).values(value('authority', roleNeeded)))
                .returns(az(distinct(identifier(uName).property('username')), 'username'))

        def results = bean(Neo4jTemplate).query(q.toString(), EMPTY_MAP).collect { it.username }
        return results
    }


    private static def cName = nodeName(Customer)
    private static def dName = nodeName(District)
    private static def uName = nodeName(User)
    private static def sName = nodeName(SubCounty)
    private static def rName = nodeName(Role)
    private static def terName = nodeName(Territory)

}
