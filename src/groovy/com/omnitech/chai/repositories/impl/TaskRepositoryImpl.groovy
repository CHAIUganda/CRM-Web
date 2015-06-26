package com.omnitech.chai.repositories.impl

import com.omnitech.chai.model.*
import com.omnitech.chai.repositories.CategoryBrandResult
import com.omnitech.chai.repositories.DetailerStockRepository
import com.omnitech.chai.repositories.MalariaStockRepository
import com.omnitech.chai.repositories.ProductRepository
import com.omnitech.chai.util.ChaiUtils
import org.neo4j.cypherdsl.expression.Expression
import org.neo4j.cypherdsl.grammar.Match

import static com.omnitech.chai.model.Relations.*
import static com.omnitech.chai.queries.TaskQuery.mathQueryForUserTasks
import static com.omnitech.chai.util.ChaiUtils.bean
import static grails.util.GrailsNameUtils.getNaturalName
import static org.neo4j.cypherdsl.CypherQuery.*
import static org.neo4j.cypherdsl.CypherQuery.as as az

/**
 * Created by kay on 3/19/2015.
 */
interface ITaskRepository {
    List exportAllTasks(Class type)
    List exportAllTasks(Long userId, Class type)
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
            queryString = addRepeatElementStatements(queryString, categoriesAndBrands) {CategoryBrandResult d ->
                def aliasName = "$d.category-$d.brand-($fieldLabel)"
                queryReturnLabels << aliasName
                return "sum (case when ${stockNode}.category = '$d.category' and ${stockNode}.brand = '$d.brand' then ${stockNode}.$property else null end) as `$aliasName`"
            }
        }
        export(queryString, queryReturnLabels, task)
    }

    private def customizeExportQueryMalariaDetails(Match query, List<Expression> queryReturnFields, List<String> queryReturnLabels,
        Class<MalariaDetails> task, Iterable<Product> products) {

        def stockNode = nodeName(DetailerMalariaStock)
        query.match(node(nodeName(MalariaDetails)).out(HAS_DETAILER_STOCK).node(stockNode)).optional()

        def (fieldLabels, returnFields) = getClassExportFields(task)

        queryReturnFields.addAll(returnFields)
        queryReturnLabels.addAll(fieldLabels)

        def queryString = query.returns(queryReturnFields).toString()

        def categoriesAndBrands = bean(MalariaStockRepository).findAllCategoriesAndBrands()
        // Add Stock level
        def fieldLabel = "stockLevel"
        queryString = addRepeatElementStatements(queryString, categoriesAndBrands) {CategoryBrandResult d ->
            def aliasName = "$d.category-$d.brand-($fieldLabel)"
            queryReturnLabels << aliasName
            return "sum (case when ${stockNode}.category = '$d.category' and ${stockNode}.brand = '$d.brand' then ${stockNode}.$fieldLabel else null end) as `$aliasName`"
        }

        // Add buying Price
        categoriesAndBrands = bean(MalariaStockRepository).findAllCategoriesAndBrands()
        fieldLabel = "buyingPrice"
        queryString = addRepeatElementStatements(queryString, categoriesAndBrands) {CategoryBrandResult d ->
            def aliasName = "$d.category-$d.brand-($fieldLabel)"
            queryReturnLabels << aliasName
            return "sum (case when ${stockNode}.category = '$d.category' and ${stockNode}.brand = '$d.brand' then ${stockNode}.$fieldLabel else null end) as `$aliasName`"
        }

        // Add selling Price
        categoriesAndBrands = bean(MalariaStockRepository).findAllCategoriesAndBrands()
        fieldLabel = "sellingPrice"
        queryString = addRepeatElementStatements(queryString, categoriesAndBrands) {CategoryBrandResult d ->
            def aliasName = "$d.category-$d.brand-($fieldLabel)"
            queryReturnLabels << aliasName
            return "sum (case when ${stockNode}.category = '$d.category' and ${stockNode}.brand = '$d.brand' then ${stockNode}.$fieldLabel else null end) as `$aliasName`"
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


}
