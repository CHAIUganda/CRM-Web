package com.omnitech.chai.queries

import com.omnitech.chai.model.Task
import com.omnitech.chai.util.PageUtils
import org.neo4j.cypherdsl.grammar.Execute
import org.neo4j.cypherdsl.grammar.Match
import org.neo4j.cypherdsl.grammar.ReturnNext
import org.neo4j.cypherdsl.grammar.WithNext
import org.slf4j.LoggerFactory

import static com.omnitech.chai.model.Relations.*
import static org.neo4j.cypherdsl.CypherQuery.*

/**
 * Created by kay on 11/12/14.
 */
//@CompileStatic
class TaskQuery {

    private static def log = LoggerFactory.getLogger(TaskQuery)

    static WithNext userTasksQuery(Long userId, String status, Class taskType, String filter, Map sortParams) {
        def task = taskType.simpleName.toLowerCase()
        def query = _userTasksQuery(userId, status, taskType, filter).with(distinct(identifier(task)), identifier('di'), identifier('detailerStocks'))
        PageUtils.addPagination(query, sortParams, taskType)
        query.returns(identifier(task), identifier('detailerStocks'))

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
            print "Using status"
            def statusFilter = identifier(varName).property('status').eq(status)

            if (search) {
                statusFilter = statusFilter.and(searchFilter())
            }

            query.where(statusFilter)
        } else if (search) {
            query.where(searchFilter())
        }
        if (search) {
            query.where(searchFilter())
        }
        //add district path
        query.match(node('sc').in(HAS_SUB_COUNTY).node('di')).optional()
        query.match(node('customer').in(IN_SEGMENT).node('segment')).optional()
        print query
        return query
    }

    static <T extends Task> Match mathQueryForUserTasks(Long userId, Class<T> taskType) {
        print "Searching task: " + taskType.simpleName + " User ID: " + userId
        def varName = taskType.simpleName.toLowerCase()
        start(nodesById('u', userId))
                .match(node('u').out(USER_TERRITORY, SUPERVISES_TERRITORY).node('ut')
                .in(SC_IN_TERRITORY).node('sc')
                .in(CUST_IN_SC).node('customer')
                .out(CUST_TASK).node(varName).label(taskType.simpleName)
                .out(HAS_DETAILER_STOCK).node('detailerStocks')
                )
                
    }

    static Match getTaskQuery(String status, Class<? extends Task> taskType) {
        def startPath = node('task').label(taskType.simpleName)
        if (status) startPath = startPath.values(value('status', status))

        def query = match(startPath)
                .match(node('task').in(CUST_TASK).node('customer'))
                .match(node('customer').out(CUST_IN_SC).node('sc'))
                .match(node('sc').in(HAS_SUB_COUNTY).node('di'))

        return query
    }
}
