package com.omnitech.chai.repositories.impl

import com.omnitech.chai.model.DetailerStock
import com.omnitech.chai.model.DetailerTask
import com.omnitech.chai.model.Order
import com.omnitech.chai.model.Sale
import com.omnitech.chai.repositories.CategoryBrandResult
import com.omnitech.chai.repositories.DetailerStockRepository
import com.omnitech.chai.repositories.ProductRepository
import com.omnitech.chai.util.ExportUtil
import com.omnitech.chai.util.ReflectFunctions
import fuzzycsv.FuzzyCSV
import groovy.transform.CompileStatic
import org.neo4j.cypherdsl.expression.Expression
import org.neo4j.cypherdsl.grammar.Match

import static com.omnitech.chai.model.Relations.*
import static com.omnitech.chai.queries.TaskQuery.mathQueryForUserTasks
import static grails.util.GrailsNameUtils.getNaturalName
import static org.neo4j.cypherdsl.CypherQuery.*
import static org.neo4j.cypherdsl.CypherQuery.as as az

/**
 * Created by kay on 3/19/2015.
 */
interface CustomTaskRepository {

    List exportAllTasks(Class type)

}

class TaskRepositoryImpl extends CustomRepositoryBase implements CustomTaskRepository {

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
        customizeExportQuery(query, fields, labels, type.newInstance())

    }

    def exportTasks(Long userId, Class type) {
        def nodeName = type.simpleName.toLowerCase()
        def query = mathQueryForUserTasks(userId, type)
                .with(distinct(identifier(nodeName)))
                .match(node(nodeName).in(CUST_TASK).node('c').out(CUST_IN_SC).node('sc'))
                .match(node('sc').in(HAS_SUB_COUNTY).node('d')).optional()
                .match(node('c').out(CUST_IN_VILLAGE).node('v')).optional()
                .match(node(nodeName).in(COMPLETED_TASK, CANCELED_TASK).node('u')).optional()
        def fields = essentialExportTaskClauses()
        def labels = ['DISTRICT', 'SUBCOUNTY', 'VILLAGE', 'OUTLET NAME', 'OUTLET TYPE', 'CANCELED_OR_COMPLETED BY']
        customizeExportQuery(query, fields, labels, type.newInstance())
    }

    private static List essentialExportTaskClauses() {
        [az(identifier('d').property('name'), 'DISTRICT'),
         az(identifier('sc').property('name'), 'SUBCOUNTY'),
         az(identifier('v').property('name'), 'VILLAGE'),
         az(identifier('c').property('outletName'), 'OUTLET NAME'),
         az(identifier('c').property('outletType'), 'OUTLET TYPE'),
         az(identifier('u').property('username'), 'CANCELED_OR_COMPLETED BY')]
    }

    private def customizeExportQuery(Match query,
                                     List<Expression> queryReturnFields,
                                     List<String> queryReturnLabels,
                                     Order order) {
        queryReturnLabels << 'ORDER TAKEN BY'
        def varName = Order.simpleName.toLowerCase()
        query.match(node(varName).out(ORDER_TAKEN_BY).node('takenBy')).optional()
                .match(node(varName).out(HAS_PRODUCT).as('li').node('p')).optional()
        queryReturnFields << az(identifier('takenBy').property('username'), 'ORDER TAKEN BY')

        query.returns(queryReturnFields)

        def stringQuery = query.toString()

        stringQuery = addRepeatElementStatements(stringQuery, bean(ProductRepository).findAll()) { p ->
            def productName = p.name.toUpperCase() + '-' + (p.unitOfMeasure ?: '')
            queryReturnLabels << productName
            return "sum (case when id(p) = $p.id then li.quantity else null end) as `$productName`"

        }

        return export(stringQuery, queryReturnLabels, Order)
    }

    private def export(String query, List<String> queryReturnLabels, Class type) {
        queryReturnLabels.removeAll('COMMENT', 'IS ADHOCK', 'WKT')
        def results = neo.query(query, Collections.EMPTY_MAP).collect()
        def data = FuzzyCSV.toCSV(results, *queryReturnLabels)
        data = ExportUtil.fixDates(type, data)
        return data
    }

    private def customizeExportQuery(Match query,
                                     List<Expression> queryReturnFields,
                                     List<String> queryReturnLabels, Sale order) {

    }

    private def customizeExportQuery(Match query,
                                     List<Expression> queryReturnFields,
                                     List<String> queryReturnLabels,
                                     DetailerTask task) {


        def stockNode = nodeName(DetailerStock)
        query.match(node(nodeName(DetailerTask)).out(HAS_DETAILER_STOCK).node(stockNode)).optional()

        def (fieldLabels, returnFields) = getClassExportFields(task.getClass())

        queryReturnFields.addAll(returnFields)
        queryReturnLabels.addAll(fieldLabels)

        def queryString = query.returns(queryReturnFields).toString()

        //add stock quantity
        ['stockLevel', 'buyingPrice', 'sellingPrice'].each { String property ->
            def fieldLabel = getNaturalName(property)
            queryString = addRepeatElementStatements(queryString, bean(DetailerStockRepository).findAllCategoriesAndBrands()) {CategoryBrandResult d ->
                def aliasName = "$d.category-$d.brand-($fieldLabel)"
                queryReturnLabels << aliasName
                return "sum (case when ${stockNode}.category = '$d.category' and ${stockNode}.brand = '$d.brand' then ${stockNode}.$property else null end) as `$aliasName`"
            }
        }



        export(queryString, queryReturnLabels, task.getClass())
    }


    static getClassExportFields(Class aClass) {
        def varName = aClass.simpleName.toLowerCase()
        def returnFields = [], fieldLabels = []
        ReflectFunctions.findAllBasicFields(aClass).each {
            if (['lastUpdated', 'dateCreated', 'id'].contains(it)) return
            def fieldAlias = getNaturalName(it).toUpperCase()
            returnFields << az(identifier(varName).property(it), fieldAlias)
            fieldLabels << fieldAlias
        }
        [fieldLabels, returnFields]
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
                                            def order) {
        throw new UnsupportedOperationException("Export of $order is not supported")
    }

    @CompileStatic
    private static String nodeName(Class aClass) { aClass.simpleName.toLowerCase() }


}
