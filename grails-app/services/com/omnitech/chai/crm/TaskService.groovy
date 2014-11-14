package com.omnitech.chai.crm

import com.omnitech.chai.model.Customer
import com.omnitech.chai.model.DetailerTask
import com.omnitech.chai.model.Task
import com.omnitech.chai.util.ModelFunctions
import com.omnitech.chai.util.PageUtils
import com.omnitech.chai.util.ReflectFunctions
import org.neo4j.cypherdsl.grammar.Match
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.neo4j.support.Neo4jTemplate
import org.springframework.data.neo4j.transaction.Neo4jTransactional

import static com.omnitech.chai.model.Relations.*
import static org.neo4j.cypherdsl.CypherQuery.*

@Neo4jTransactional
class TaskService {

    def taskRepository
    def userRepository
    @Autowired
    Neo4jTemplate neo
    def customerRepository
    def detailerTaskRepository
    def neoSecurityService

    /* Tasks */

    List<Task> listAllTasks() { taskRepository.findAll().collect() }

    Page<Task> listTasks(Map params) { ModelFunctions.listAll(taskRepository, params) }

    Page<Task> listTasksByStatus(String status, Map params) {

        def resultQuery = getTaskQuery(status).returns(identifier('task'))
        PageUtils.addPagination(resultQuery, params, Task)

        def countyQuery = getTaskQuery(status).returns(count(identifier('task')))
        log.trace("listTasksByStatus: countQuery: $countyQuery")
        log.trace("listTasksByStatus: dataQuery: $resultQuery")

        ModelFunctions.query(neo, resultQuery, countyQuery, params,Task)
    }

    private static getTaskQuery(String status) {
        def query = match(node('task').label(Task.simpleName))
                .where(identifier('task').string('status').eq(status))
        return query
    }

    Task findTask(Long id) { taskRepository.findOne(id) }

    Task findTask(String uuid) { taskRepository.findByUuid(uuid) }

    def <T extends Task> T saveTask(T task) { ModelFunctions.saveGenericEntity(neo, task) }

    void deleteTask(Long id) { taskRepository.delete(id) }

    Page<Task> searchTasks(String search, Map params) {
        ModelFunctions.searchAll(neo, Task, ModelFunctions.getWildCardRegex(search), params)
    }

    /* Detailer Tasks*/
    DetailerTask findDetailerTask(Long id) { neo.findOne(id,DetailerTask) }

    List<Task> findAllTaskForUser(Long userId, String status, Map params) {
        def task = 'task'
        def query = mathQueryForUserTasks(userId)
                .where(
                identifier(task).property('status').eq(status)
                        .and(node('u').out(ASSIGNED_TASK).node(task)
                        .or(not(node(task).in(ASSIGNED_TASK).node())
                ))).returns(distinct(identifier(task)))

        query = PageUtils.addPagination(query, params, Task)

        log.trace("Tasks for user: [$query]")
        taskRepository.query(query, [:]).collect()
    }


    Match mathQueryForUserTasks(Long userId) {
        start(nodesById('u', userId))
                .match(node('u').out(USER_TERRITORY).node('ut')
                .in(SC_IN_TERRITORY).node('sc')
                .out(HAS_PARISH).node('p')
                .out(HAS_VILLAGE).node('v')
                .in(CUST_IN_VILLAGE).node('c')
                .out(CUST_TASK).node('task'))

    }

    def autoGenerateTasks() {
        customerRepository.findAll().each {
            def task = generateCustomerTask(it)
            if (task) taskRepository.save(task)
        }
    }

    Task generateCustomerTask(Customer customer) {
        def segment = customer.segment
        if (!segment) return null

        def prevTask = taskRepository.findLastTask(customer.id)

        def spaceBtnVisits = segment.spaceBetweenVisits
        def newTask = new Task(customer: customer, description: "Go Check on [$customer.outletName]", dueDate: new Date())
        if (prevTask?.completionDate) {
            //if we have a previous task set the date *n* days after previous task
            def daysBtnDates = new Date() - prevTask.completionDate
            if (daysBtnDates < spaceBtnVisits)
                newTask.dueDate = prevTask.completionDate + segment.spaceBetweenVisits
        }

        return newTask

    }

    /**
     * This updates a given task and if its a basic task it projects it to the required Type.
     * This method also makes sure that other relationships are not modified
     */
    DetailerTask completeDetailTask(DetailerTask detailerTask) {
        def neoTask = taskRepository.findByUuid(detailerTask.uuid)

        //task could have been deleted
        if (!neoTask) return null

        neoTask = neo.projectTo(neoTask, DetailerTask)
        def detailFields = ReflectFunctions.findAllBasicFields(DetailerTask)
        //Do not change original task data
        detailFields.with {
            removeAll(ReflectFunctions.findAllBasicFields(Task))
        }
        ModelFunctions.bind(neoTask, detailerTask.properties, detailFields)
        neoTask.completedBy(neoSecurityService.currentUser)

        saveTask(neoTask)
    }

}