package com.omnitech.chai.queries

import com.omnitech.chai.model.Order
import com.omnitech.chai.model.Task
import com.omnitech.chai.util.PageUtils
import com.omnitech.chai.util.ReflectFunctions
import org.neo4j.cypherdsl.grammar.Execute
import org.neo4j.cypherdsl.grammar.Match
import org.neo4j.cypherdsl.grammar.ReturnNext
import org.neo4j.cypherdsl.grammar.WithNext
import org.slf4j.LoggerFactory

import static com.omnitech.chai.model.Relations.*
import static grails.util.GrailsNameUtils.getNaturalName
import static org.neo4j.cypherdsl.CypherQuery.*
import static org.neo4j.cypherdsl.CypherQuery.as as az

/**
 * Created by kay on 11/12/14.
 */
//@CompileStatic
class TaskQuery {

    private static def log = LoggerFactory.getLogger(TaskQuery)

    static WithNext userTasksQuery(Long userId, String status, Class taskType, String filter, Map sortParams) {
        def task = taskType.simpleName.toLowerCase()
        def query = _userTasksQuery(userId, status, taskType, filter).with(distinct(identifier(task)), identifier('di'))
        PageUtils.addPagination(query, sortParams, taskType)
        query.returns(identifier(task))

        return query
    }

    static ReturnNext userTasksCountQuery(Long userId, String status, Class taskType, String filter) {
        def task = taskType.simpleName.toLowerCase()
        def query = _userTasksQuery(userId, status, taskType, filter)
                .returns(count(distinct(identifier(task))))
        return query
    }

    static List filterAllTasksQuery(String filter, Class taskType) {
        def varName = taskType.simpleName.toLowerCase()
        def query = {
            def q = match(node(varName).label(taskType.simpleName).in(CUST_TASK).node('customer'))
            if (filter) {
                q.match(node('customer').out(CUST_IN_SC).node('sc').in(HAS_SUB_COUNTY).node('district'))
                        .where(identifier(varName).string('description').regexp(filter)
                        .or(identifier('customer').string('outletName').regexp(filter))
                        .or(identifier('customer').string('tradingCenter').regexp(filter))
                        .or(identifier('district').string('name').regexp(filter)))
            }

            return q
        }

        Execute cq = query().returns(count(distinct(identifier(varName))))
        Execute q = query().returns(distinct(identifier(varName)))
        [q, cq]
    }

    private static Match _userTasksQuery(Long userId, String status, Class taskType, String search) {
        def varName = taskType.simpleName.toLowerCase()
        def query = mathQueryForUserTasks(userId, taskType)


        def searchFilter = {
            identifier(varName).string('description').regexp(search)
                    .or(identifier('customer').string('outletName').regexp(search))
                    .or(identifier('customer').string('tradingCenter').regexp(search))
        }

        if (status) {

            def statusFilter = identifier(varName).property('status').eq(status)

            if (search) {
                statusFilter = statusFilter.and(searchFilter())
            }

            query.where(statusFilter)
        } else if (search) {
            query.where(searchFilter())
        }

        //add district path
        query.match(node('sc').in(HAS_SUB_COUNTY).node('di')).optional()

        return query
    }

    static <T extends Task> Match mathQueryForUserTasks(Long userId, Class<T> taskType) {
        def varName = taskType.simpleName.toLowerCase()
        start(nodesById('u', userId))
                .match(node('u').out(USER_TERRITORY, SUPERVISES_TERRITORY).node('ut')
                .in(SC_IN_TERRITORY).node('sc')
                .in(CUST_IN_SC).node('customer')
                .out(CUST_TASK).node(varName).label(taskType.simpleName))


    }

    static Match getTaskQuery(String status, Class<? extends Task> taskType) {
        def startPath = node('task').label(taskType.simpleName)
        if (status) startPath = startPath.values(value('status', status))

        def query = match(startPath)
                .match(node('task').in(CUST_TASK).node('customer')).optional()
                .match(node('customer').out(CUST_IN_SC).node('sc')).optional()
                .match(node('sc').in(HAS_SUB_COUNTY).node('di')).optional()

        return query
    }

    static def exportTasks(Long userId, Class type) {
        def varName = type.simpleName.toLowerCase()
        def query = mathQueryForUserTasks(userId, type)
                .with(distinct(identifier(varName)))
                .match(node(varName).in(CUST_TASK).node('c').out(CUST_IN_SC).node('sc'))
                .match(node('sc').in(HAS_SUB_COUNTY).node('d')).optional()
                .match(node('c').out(CUST_IN_VILLAGE).node('v')).optional()
                .match(node('c').out(CUST_IN_PARISH).node('p')).optional()
                .match(node(varName).in(COMPLETED_TASK,CANCELED_TASK).node('u')).optional()

        def fields = [az(identifier('d').property('name'), 'DISTRICT'),
                      az(identifier('sc').property('name'), 'SUBCOUNTY'),
                      az(identifier('v').property('name'), 'VILLAGE'),
                      az(identifier('c').property('outletName'), 'OUTLET NAME'),
                      az(identifier('c').property('outletType'), 'OUTLET TYPE'),
                      az(identifier('u').property('username'), 'CANCELED_OR_COMPLETED BY')]

        if (type.isAssignableFrom(Order)) {
            query.match(node(varName).out(ORDER_TAKEN_BY).node('takenBy')).optional()
            fields << az(identifier('takenBy').property('username'), 'ORDER TAKEN BY')
        }

        ReflectFunctions.findAllBasicFields(type).each {
            if (['lastUpdated', 'dateCreated', 'id'].contains(it)) return
            fields << az(identifier(varName).property(it), getNaturalName(it).toUpperCase())
        }

        query.returns(*fields)

        log.trace("Query: exportTasksForUser(): [$query]")

        return query
    }


}
