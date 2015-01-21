package com.omnitech.chai.queries

import com.omnitech.chai.model.Task
import org.neo4j.cypherdsl.grammar.Match
import org.neo4j.cypherdsl.grammar.ReturnNext
import org.slf4j.LoggerFactory

import static com.omnitech.chai.model.Relations.*
import static org.neo4j.cypherdsl.CypherQuery.*

/**
 * Created by kay on 11/12/14.
 */
//@CompileStatic
class TaskQuery {

    private static def log = LoggerFactory.getLogger(TaskQuery)

    static ReturnNext userTasksQuery(Long userId, String status, Class taskType, String filter) {
        def task = taskType.simpleName.toLowerCase()
        def query = _userTasksQuery(userId, status, taskType, filter).returns(distinct(identifier(task)))
        return query
    }

    static ReturnNext userTasksCountQuery(Long userId, String status, Class taskType, String filter) {
        def task = taskType.simpleName.toLowerCase()
        def query = _userTasksQuery(userId, status, taskType, filter)
                .returns(count(distinct(identifier(task))))
        return query
    }

    private static Match _userTasksQuery(Long userId, String status, Class taskType, String search) {
        def task = taskType.simpleName.toLowerCase()
        def query = mathQueryForUserTasks(userId, taskType)

        def searchFilter = {
            identifier(task).string('description').regexp(search)
                    .or(identifier('customer').string('outletName').regexp(search))
                    .or(identifier('customer').string('tradingCenter').regexp(search))
        }

        if (status) {

            def statusFilter = identifier(task).property('status').eq(status)

            if (search) {
                statusFilter = statusFilter.and(searchFilter())
            }

            query.where(statusFilter)
        } else if (search) {
            query.where(searchFilter())
        }
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


}
