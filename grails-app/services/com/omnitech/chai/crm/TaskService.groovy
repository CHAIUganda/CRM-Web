package com.omnitech.chai.crm

import com.omnitech.chai.model.*
import com.omnitech.chai.queries.TaskQuery
import com.omnitech.chai.util.ModelFunctions
import com.omnitech.chai.util.PageUtils
import com.omnitech.chai.util.ReflectFunctions
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.neo4j.support.Neo4jTemplate
import org.springframework.data.neo4j.transaction.Neo4jTransactional
import org.springframework.security.access.AccessDeniedException

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
    def directSaleRepository
    def orderRepository
    def neoSecurityService
    def userService

    /* Tasks */

    List<Task> listAllTasks() { taskRepository.findAll().collect() }

    def <T extends Task> Page<T> listTasks(Class<T> taskType, Map params) {
        ModelFunctions.listAll(neo, taskType, params, Task)
    }

    def <T extends Task> Page<T> listTasksByStatus(String status, Map params, Class<T> taskType) {

        def resultQuery = getTaskQuery(status, taskType).returns(identifier('task'))
        PageUtils.addPagination(resultQuery, params, Task)

        def countyQuery = getTaskQuery(status, taskType).returns(count(identifier('task')))
        log.trace("listTasksByStatus: countQuery: $countyQuery")
        log.trace("listTasksByStatus: dataQuery: $resultQuery")

        ModelFunctions.query(neo, resultQuery, countyQuery, params, Task)
    }

    def <T extends Task> Page<T> loadPageData(Integer max, Map params, Class<T> taskType) {
        params.max = Math.min(max ?: 50, 100)
        if (!params.sort) {
            params.sort = 'dueDate'
        }

        Page<T> page

        def user = params.user ? userRepository.findByUsername(params.user) : null
        if (user) {
            def status = params.status ?: Task.STATUS_NEW
            params.status = status
            page = findAllTasksForUser(user.id, status, params, taskType)
        } else {
            if (params.status) {
                page = listTasksByStatus(params.status as String, params, taskType)
            } else {
                page = listTasks(taskType, params)
            }
        }

        page.content.each { neo.fetch(it.territoryUser()) }

        return page as Page<T>
    }

    def <T extends Task> Page<T> loadSuperVisorUserData(Integer max, Map params, Class<T> taskType, Long supervisorUserId) {
        params.max = Math.min(max ?: 50, 100)
        if (!params.sort) {
            params.sort = 'dueDate'
        }

        def user = params.user ? userRepository.findByUsername(params.user) : null

        if (user && !isAllowedToViewUserTasks(user)) {
            throw new AccessDeniedException('You Cannot View This Users Tasks')
        }

        String status = params.status

        Page<T> page
        if (user) {
            page = findAllTasksForUser(user.id, status, params, taskType)
        } else {
            page = findAllTasksForUser(supervisorUserId, status, params, taskType)
        }

        page.content.each { neo.fetch(it.territoryUser()) }

        return page as Page<T>
    }

    //todo optimise this with query
    private boolean isAllowedToViewUserTasks(User otherUser) {
        def currentUser = neoSecurityService.currentUser

        if(currentUser.hasRole(Role.SUPER_ADMIN_ROLE_NAME,Role.ADMIN_ROLE_NAME)){
            return true
        }

        if (currentUser.hasRole(Role.DETAILER_ROLE_NAME)) {
            return userService.listUsersForUser(currentUser.id, Role.DETAILER_ROLE_NAME).any {
                otherUser.id == it.id
            }
        }

        if (currentUser.hasRole(Role.SALES_ROLE_NAME)) {
            return userService.listUsersForUser(currentUser.id, Role.SALES_ROLE_NAME).any {
                otherUser.id == it.id
            }
        }


        return false;

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

    //todo add optimisation to remove count
    def <T extends Task> Page<T> findAllTasksForUser(Long userId, String status, Map params, Class<T> taskType) {
        def query = TaskQuery.userTasksQuery(userId, status, taskType)
        def countQuery = TaskQuery.userTasksCountQuery(userId, status, taskType)
        query = PageUtils.addPagination(query, params, taskType)
        log.trace("Tasks for user: [$query]")
        ModelFunctions.query(neo, query, countQuery, params, taskType)
    }

    void updateTaskDate(Long taskId, Date date) {
        def task = findTask(taskId)
        task.dueDate = date
        saveTask(task)
    }

    List<Map> exportTasksForUser(Long userId) {
        def task = 'task'
        def query = TaskQuery.mathQueryForUserTasks(userId, Task)
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

    def autoGenerateTasks() {
        customerRepository.findAll().each {
            def task = generateCustomerDetailingTask(it)
            if (task) taskRepository.save(task)
        }
    }

    def autoGenerateTasks(Territory territory) {
        customerRepository.findByTerritory(territory.id).each {
            def task = generateCustomerDetailingTask(it)
            if (task) taskRepository.save(task)
        }
    }

    Task generateCustomerDetailingTask(Customer customer) {
        def segment = customer.segment
        if (!segment) return null

        def prevTask = taskRepository.findLastTask(customer.id)

        if (prevTask?.status == Task.STATUS_NEW) return null

        def spaceBtnVisits = segment.spaceBetweenVisits
        def newTask = new DetailerTask(customer: customer, description: "Go Check on [$customer.outletName]", dueDate: new Date())
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

    void generateSalesTasks(Territory territory) {
        customerRepository.findByTerritory(territory.id).each {
            def newTask = new Order(customer: it, dueDate: new Date())
            if (newTask) taskRepository.save(newTask)
        }
    }

    /* Orders */

    List<Order> listAllOrders() { orderRepository.findAll().collect() }

    Page<Order> listOrders(Map params) { ModelFunctions.listAll(neo, Order, params, Order) }

    Order findOrder(Long id) { orderRepository.findOne(id) }

    Order findOrder(String uuid) {
        orderRepository.findByUuidImpl(uuid)
    }

    Order findOrderByClientRefId(String refId) {
        orderRepository.findByClientRefId(refId)
    }


    DirectSale findDirectSaleByClientRefId(String refId) {
        directSaleRepository.findByClientRefId(refId)
    }

    void deleteOrder(Long id) { orderRepository.delete(id) }

    Page<Order> searchOrders(String search, Map params) {
        ModelFunctions.searchAll(neo, Order, ModelFunctions.getWildCardRegex(search), params)
    }

}