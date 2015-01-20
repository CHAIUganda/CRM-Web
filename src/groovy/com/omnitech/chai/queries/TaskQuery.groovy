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
class TaskQuery {

    private static def log = LoggerFactory.getLogger(TaskQuery)

    static ReturnNext userTasksQuery(Long userId, String status, Class taskType) {
        def task = taskType.simpleName.toLowerCase()
        def query = _userTasksQuery(userId, status, taskType).returns(distinct(identifier(task)))
        return query
    }

    static ReturnNext userTasksCountQuery(Long userId, String status, Class taskType) {
        def task = taskType.simpleName.toLowerCase()
        def query = _userTasksQuery(userId, status, taskType)
                .returns(count(distinct(identifier(task))))
        return query
    }

    private static Match _userTasksQuery(Long userId, String status, Class taskType) {
        def task = taskType.simpleName.toLowerCase()
        def query = mathQueryForUserTasks(userId, taskType)
        if (status) {
            query.where(identifier(task).property('status').eq(status))
        }
        return query
    }

    static <T extends Task> Match mathQueryForUserTasks(Long userId, Class<T> taskType) {
        def varName = taskType.simpleName.toLowerCase()
        start(nodesById('u', userId))
                .match(node('u').out(USER_TERRITORY, SUPERVISES_TERRITORY).node('ut')
                .in(SC_IN_TERRITORY).node('sc')
                .in(CUST_IN_SC).node('c')
                .out(CUST_TASK).node(varName).label(taskType.simpleName))

    }



}
