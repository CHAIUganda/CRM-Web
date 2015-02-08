package com.omnitech.chai.crm

import com.omnitech.chai.model.*
import com.omnitech.chai.queries.TaskQuery
import com.omnitech.chai.util.ModelFunctions
import com.omnitech.chai.util.PageUtils
import com.omnitech.chai.util.ReflectFunctions
import org.neo4j.cypherdsl.grammar.ReturnNext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.neo4j.support.Neo4jTemplate
import org.springframework.data.neo4j.transaction.Neo4jTransactional
import org.springframework.security.access.AccessDeniedException

import static com.omnitech.chai.model.Relations.*
import static com.omnitech.chai.util.ChaiUtils.getNextWorkDay
import static grails.util.GrailsNameUtils.getNaturalName
import static java.util.Collections.EMPTY_MAP
import static java.util.Collections.max
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
    def clusterService

    /* Tasks */

    def <T extends Task> Page<T> listTasks(Class<T> taskType, Map params) {
        ModelFunctions.listAll(neo, taskType, params, Task)
    }

    def <T extends Task> Page<T> listTasksByStatus(String status, Map params, Class<T> taskType) {

        def resultQuery = getTaskQuery(status, taskType).returns(identifier('task'))
        PageUtils.addSorting(resultQuery, params, Task)

        def countyQuery = getTaskQuery(status, taskType).returns(count(identifier('task')))
        log.trace("listTasksByStatus: countQuery: $countyQuery")
        log.trace("listTasksByStatus: dataQuery: $resultQuery")

        taskRepository.query(resultQuery, countyQuery, EMPTY_MAP, PageUtils.create(params))
    }

    def <T extends Task> Page<T> loadPageData(Integer max, Map params, Class<T> taskType, String filter) {
        params.max = max ?: 50
        if (!params.sort) {
            params.sort = 'dueDate'
        }

        Page<T> page
        def user = params.user ? userRepository.findByUsername(params.user) : null
        if (filter) {
            page = searchTasks(filter, params, taskType)
        } else if (user) {
            def status = params.status ?: Task.STATUS_NEW
            params.status = status
            page = findAllTasksForUser(user.id, status, params, taskType, null)
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

    def <T extends Task> Page<T> loadSuperVisorUserData(Integer max, Map params, Class<T> taskType, Long supervisorUserId, String filter) {
        params.max = max ?: 50
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
            page = findAllTasksForUser(user.id, status, params, taskType, filter)
        } else {
            page = findAllTasksForUser(supervisorUserId, status, params, taskType, filter)
        }

        page.content.each { neo.fetch(it.territoryUser()) }

        return page as Page<T>
    }

    /**
     * Used by task controllers to load page data
     * @param user
     * @param taskType
     * @param params
     * @param max
     * @return
     */
    def List loadPageDataForUser(User user, Class taskType, Map params, Integer max, String filter) {

        def roleNeeded = taskType == DetailerTask ? Role.DETAILER_ROLE_NAME : Role.SALES_ROLE_NAME
        Page page
        def users
        if (user.hasRole(Role.ADMIN_ROLE_NAME, Role.SUPER_ADMIN_ROLE_NAME)) {
            page = loadPageData(max, params, taskType, filter)
            users = userService.listUsersByRole(roleNeeded)
        } else {
            page = loadSuperVisorUserData(max, params, taskType, user.id, filter)
            users = userService.listUsersSupervisedBy(user.id, roleNeeded)
        }

        return [page, users]
    }

    //todo optimise this with query
    private boolean isAllowedToViewUserTasks(User otherUser) {
        def currentUser = neoSecurityService.currentUser

        if (currentUser.hasRole(Role.SUPER_ADMIN_ROLE_NAME, Role.ADMIN_ROLE_NAME)) {
            return true
        }

        if (currentUser.hasRole(Role.DETAILING_SUPERVISOR_ROLE_NAME)) {
            return userService.listUsersSupervisedBy(currentUser.id, Role.DETAILER_ROLE_NAME).any {
                otherUser.id == it.id
            }
        }

        if (currentUser.hasRole(Role.SALES_SUPERVISOR_ROLE_NAME)) {
            return userService.listUsersSupervisedBy(currentUser.id, Role.SALES_ROLE_NAME).any {
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

    def <T extends Task> Page<T> searchTasks(String search, Map params, Class<T> taskType) {
        def (ReturnNext q, ReturnNext cq) = TaskQuery.allTasksQuery(search, taskType)
        PageUtils.addSorting(q, params, taskType)
        taskRepository.query(q, cq, EMPTY_MAP, PageUtils.create(params))
    }

    /* Detailer Tasks*/

    DetailerTask findDetailerTask(Long id) { neo.findOne(id, DetailerTask) }

    //todo add option to remove count
    def <T extends Task> Page<T> findAllTasksForUser(Long userId, String status, Map params, Class<T> taskType, String filter) {
        def query = TaskQuery.userTasksQuery(userId, status, taskType, filter)
        def countQuery = TaskQuery.userTasksCountQuery(userId, status, taskType, filter)
        query = PageUtils.addSorting(query, params, taskType)
        log.trace("Tasks for user: [$query]")

        taskRepository.query(query, countQuery, EMPTY_MAP, PageUtils.create(params))
    }

    void updateTaskDate(Long taskId, Date date) {
        def task = findTask(taskId)
        task.dueDate = date
        saveTask(task)
    }

    List<Map> exportTasksForUser(Long userId, Class taskTpe) {
        def query = TaskQuery.exportTasks(userId,taskTpe)
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

    DetailerTask completeAdhocDetailTask(DetailerTask detailerTask, String customerUuid) {


        def customer = customerRepository.findByUuid(customerUuid)

        if (!customer) {
            log.warn("AdhocDetailCustomer Not Found: [$customerUuid]")
            //customer could have been deleted
            return  null
        }

        detailerTask.customer = customer

        detailerTask.completedBy(neoSecurityService.currentUser)

        saveTask(detailerTask)
    }


    void generateSalesTasks(Territory territory) {
        customerRepository.findByTerritory(territory.id).each {
            def newTask = new Order(customer: it, dueDate: new Date())
            if (newTask) taskRepository.save(newTask)
        }
    }

    def generateTasks(List<Territory> territories,
                      List<CustomerSegment> segments,
                      Date startDate,
                      List<Integer> workDays,
                      int tasksPerDay,
                      Class<? extends Task> taskType) {


        def messages = []
        def allTasks = []

        territories.each { t ->
            def tasks = []

            segments.each { s ->
                log.info("Generating Tasks for: Territory[$t] and Segment[$s]")
                tasks.addAll generateTasks(t, s, startDate, workDays, taskType)
            }

            if (tasks) {
                log.info "***** Clustering: Territory[$t] Tasks[${tasks.size()}]"
                messages << "$t(${tasks.size()})"
                def cluster = clusterService.assignDueDates(tasks, startDate, workDays, tasksPerDay)

                taskRepository.save(tasks)
                allTasks.addAll(tasks)
            } else {
                log.warn "***WARNING:***No Tasks Generated for Territory[$t]"
            }
        }

        return [messages,allTasks]

    }

    List<Task> generateTasks(Territory t, CustomerSegment s, Date startDate, List<Integer> workDays, Class<? extends Task> taskType) {

        def results
        if (taskType == DetailerTask)
            results = customerRepository.findAllWithoutNewDetailingTasks(t.id, s.id, s.numberOfTasks)
        else
            results = customerRepository.findAllWithoutNewOrderTasks(t.id, s.id, s.numberOfTasks)


        def tasks = []
        results.each { r ->
            if (s.shouldGenerateTask(r.completionDate)) {
                if (taskType == DetailerTask)
                    tasks << new DetailerTask(customer: r.customer, description: "Detailing [$r.customer.outletName]", dueDate: getNextWorkDay(workDays, startDate))
                else
                    tasks << new Order(customer: r.customer, dueDate: getNextWorkDay(workDays, startDate))

            }
        }
        log.info("Generated [${tasks.size()}] Tasks for Territory[$t],Segment[$s]")

        return tasks
    }

    /* Orders */

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

}