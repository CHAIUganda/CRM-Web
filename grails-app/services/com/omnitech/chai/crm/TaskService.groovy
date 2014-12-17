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
import static grails.util.GrailsNameUtils.getNaturalName
import static org.neo4j.cypherdsl.CypherQuery.*
import static org.neo4j.cypherdsl.CypherQuery.as as az

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

    Page<Task> listTasks(Map params) { ModelFunctions.listAll(neo, Task, params) }

    def <T extends Task> Page<T> listTasksByStatus(String status, Map params, Class<T> taskType) {

        def resultQuery = getTaskQuery(status, taskType).returns(identifier('task'))
        PageUtils.addPagination(resultQuery, params, Task)

        def countyQuery = getTaskQuery(status, taskType).returns(count(identifier('task')))
        log.trace("listTasksByStatus: countQuery: $countyQuery")
        log.trace("listTasksByStatus: dataQuery: $resultQuery")

        ModelFunctions.query(neo, resultQuery, countyQuery, params, taskType)
    }

    private static getTaskQuery(String status, Class<? extends Task> taskType) {
        def query = match(node('task').label(taskType.simpleName))
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

    DetailerTask findDetailerTask(Long id) { neo.findOne(id, DetailerTask) }

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

    List<Map> exportTasksForUser(Long userId) {
        def task = 'task'
        def query = mathQueryForUserTasks(userId)
                .match(node('sc').in(HAS_SUB_COUNTY).node('d')).optional()
                .match(node('c').out(CUST_IN_VILLAGE).node('v')).optional()
                .match(node('c').out(CUST_IN_PARISH).node('p')).optional()


        def fields = [az(identifier('d').property('name'), 'DISTRICT'),
                      az(identifier('sc').property('name'), 'SUBCOUNTY'),
                      az(identifier('v').property('name'), 'VILLAGE'),
                      az(identifier('c').property('outletName'), 'OUTLET NAME'),
                      az(identifier('c').property('outletType'), 'OUTLET TYPE')]

        ReflectFunctions.findAllBasicFields(DetailerTask).each {
            if ('_dateLastUpdated' == it || it == '_dateCreated') return
            fields << az(identifier(task).property(it), getNaturalName(it).toUpperCase())
        }
        query.returns(*fields)

        //District,Subcounty,Village,Customer Name, outletType,
        // All other fields
        log.trace("findAllTasksForUser(): [$query]")

        neo.query(query.toString(), [:]).collect()
    }

    List<Map> exportAllTasks() {
        def query = match(
                node('task').label(Task.simpleName)
                        .in(CUST_TASK).node('c')
                        .out(CUST_IN_SC).node('sc')
                        .in(HAS_SUB_COUNTY).node('d'))
                .match(node('c').out(CUST_IN_VILLAGE).node('v')).optional()
                .match(node('c').out(CUST_IN_PARISH).node('p')).optional()


        def fields = [az(identifier('d').property('name'), 'DISTRICT'),
                      az(identifier('sc').property('name'), 'SUBCOUNTY'),
                      az(identifier('v').property('name'), 'VILLAGE'),
                      az(identifier('c').property('outletName'), 'OUTLET NAME'),
                      az(identifier('c').property('outletType'), 'OUTLET TYPE')]

        ReflectFunctions.findAllBasicFields(DetailerTask).each {
            if ('_dateLastUpdated' == it || it == '_dateCreated') return
            fields << az(identifier('task').property(it), getNaturalName(it).toUpperCase())
        }
        query.returns(*fields)
        //District,Subcounty,Village,Customer Name, outletType,
        // All other fields
        log.trace("exportAllTasks(): [$query]")

        neo.query(query.toString(), [:]).collect()
    }


    Match mathQueryForUserTasks(Long userId) {
        start(nodesById('u', userId))
                .match(node('u').out(USER_TERRITORY).node('ut')
                .in(SC_IN_TERRITORY).node('sc')
                .in(CUST_IN_SC).node('c')
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
        //copy longitude and latitude
        neoTask.lat = detailerTask.lat
        neoTask.lng = detailerTask.lng

        neoTask.completedBy(neoSecurityService.currentUser)

        saveTask(neoTask)
    }

}