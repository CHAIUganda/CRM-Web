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
import org.springframework.util.Assert

import static com.omnitech.chai.model.Relations.CUST_TASK
import static com.omnitech.chai.util.ChaiUtils.getNextWorkDay
import static com.omnitech.chai.util.ChaiUtils.time
import static java.util.Collections.EMPTY_MAP
import static java.util.Collections.max
import static org.neo4j.cypherdsl.CypherQuery.*

@Neo4jTransactional
class TaskService {
    def taskRepository
    def userRepository
    @Autowired
    Neo4jTemplate neo
    def customerRepository
    def directSaleRepository
    def orderRepository
    def salesCallRepository
    def neoSecurityService
    def userService
    def clusterService

    /**
     * Used by task controllers to load page data
     * @param loggedInUser
     * @param taskType
     * @param params
     * @param max
     * @return
     */
    def List loadPageDataForUser(User loggedInUser, Class taskType, Map params, Integer max, String filter) {

        def roleNeeded = taskType == DetailerTask || MalariaDetails ? Role.DETAILER_ROLE_NAME : Role.SALES_ROLE_NAME
        Page page
        Iterable<User> users
        User contextUser = null
        if (params.user) {
            contextUser = params.user ? userRepository.findByUsername(params.user) : null
            time("Cheking if User [${loggedInUser}] can view [${contextUser}] Has Access Rights") {
                if (!isAllowedToViewUserTasks(contextUser))
                    throw new AccessDeniedException("You Are Not Allowed To View This Data")
            }
        }

        if (!contextUser && !loggedInUser.hasRole(Role.ADMIN_ROLE_NAME, Role.SUPER_ADMIN_ROLE_NAME)) {
            contextUser = loggedInUser
        }

        time("Preparing to Load Page Data") {
            time("Loading Task Data") {
                if (contextUser) {
                    page = taskRepository.findAllTasksForUser(contextUser.id, taskType, params)
                } else {
                    page = taskRepository.findAllTasks(taskType, params)
                }
            }

            time("Loading Territory User") {
                if (loggedInUser.hasRole(Role.ADMIN_ROLE_NAME, Role.SUPER_ADMIN_ROLE_NAME)) {
                    users = userService.listUsersByRole(roleNeeded)
                } else {
                    users = userService.listUsersSupervisedBy(loggedInUser.id, roleNeeded)
                }
            }
        }
        users = users.collect().sort { it.username }
        return [page, users]
    }

    boolean isAllowedToViewUserTasks(User otherUser) {
        def currentUser = neoSecurityService.currentUser

        if (currentUser.hasRole(Role.SUPER_ADMIN_ROLE_NAME, Role.ADMIN_ROLE_NAME)) {
            return true
        }

        if (currentUser.hasRole(Role.DETAILING_SUPERVISOR_ROLE_NAME)) {
            return taskRepository.canSupervisorViewUserTasks(currentUser.id, otherUser.id, Role.DETAILING_SUPERVISOR_ROLE_NAME)
        }

        if (currentUser.hasRole(Role.SALES_SUPERVISOR_ROLE_NAME)) {
            return taskRepository.canSupervisorViewUserTasks(currentUser.id, otherUser.id, Role.SALES_SUPERVISOR_ROLE_NAME)
        }
        return false;

    }


    Task findTask(Long id) { taskRepository.findOne(id) }

    Task findTask(String uuid) { taskRepository.findByUuid(uuid) }

    def <T extends Task> T saveTask(T task) { ModelFunctions.saveGenericEntity(neo, task) }

    void deleteTask(Long id) {
        taskRepository.delete(id)
    }

    List<Task> deleteTasks(Long[] ids) {
        List<Task> deleted = []
        ids.each { id ->
            def one = taskRepository.findOne(id)
            if (one) {
                taskRepository.delete(id)
                deleted << one
            }
        }
        return deleted
    }

    /* Detailer Tasks*/

    DetailerTask findDetailerTask(Long id) { neo.findOne(id, DetailerTask) }

    //todo add option to remove count
    def <T extends Task> Page<T> findAllTasksForUser(Long userId, String status, Map params, Class<T> taskType, String filter) {
        def query = TaskQuery.userTasksQuery(userId, status, taskType, filter, params)
        def countQuery = TaskQuery.userTasksCountQuery(userId, status, taskType, filter)
        query = PageUtils.addSorting(query, params, taskType)

        // ModelFunctions.query(taskRepository,query,countQuery,params,Task)
        // todo hard code pages coz the page query has already been taken care of in the query
        print query
        taskRepository.query(query, countQuery, EMPTY_MAP, PageUtils.create([max: 2000]))
    }

    void updateTaskDate(Long taskId, Date date) {
        def task = findTask(taskId)
        task.dueDate = date
        saveTask(task)
    }

    List<List> exportTasksForUser(Long userId, Class taskTpe) { taskRepository.exportAllTasks(userId, taskTpe) }

    List exportAllTasks(Class type) { taskRepository.exportAllTasks(type) }

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
    DetailerTask completeDetailTask(DetailerTask remoteTask, String customerUuid) {
        def neoTask = taskRepository.findByUuid(remoteTask.uuid)

        //task could have been deleted
        if (!neoTask) {
            remoteTask.clientRefId = remoteTask.uuid
            return completeAdhocDetailTask(remoteTask, customerUuid)
        }

        neoTask = neo.projectTo(neoTask, DetailerTask)
        def detailFields = ReflectFunctions.findAllBasicFields(DetailerTask)
        detailFields.add('detailerStocks')
        //Do not change original task data
        detailFields.with {
            removeAll(ReflectFunctions.findAllBasicFields(Task))
        }
        ModelFunctions.bind(neoTask, remoteTask.properties, detailFields)
        //copy longitude and latitude
        neoTask.lat = remoteTask.lat
        neoTask.lng = remoteTask.lng
        neoTask.isAdhock = false

        if (remoteTask.isCancelled()) {
            neoTask.description = remoteTask.description
            neoTask.cancelledBy(neoSecurityService.currentUser)
        } else
            neoTask.completedBy(neoSecurityService.currentUser)


        saveTask(neoTask)
    }

    MalariaDetails completeMalariaTask(MalariaDetails remoteTask, String customerUuid) {
        def neoTask = taskRepository.findByUuid(remoteTask.uuid)

        //task could have been deleted
        if (!neoTask) {
            remoteTask.clientRefId = remoteTask.uuid
            return completeAdhocMalariaTask(remoteTask, customerUuid)
        }

        neoTask = neo.projectTo(neoTask, MalariaDetails)
        def detailFields = ReflectFunctions.findAllBasicFields(MalariaDetails)

        //Do not change original task data
        detailFields.with {
            removeAll(ReflectFunctions.findAllBasicFields(Task))
        }

        ModelFunctions.bind(neoTask, remoteTask.properties, detailFields)
        //copy longitude and latitude
        neoTask.lat = remoteTask.lat
        neoTask.lng = remoteTask.lng
        neoTask.isAdhock = false

        if (remoteTask.isCancelled()) {
            neoTask.description = remoteTask.description
            neoTask.cancelledBy(neoSecurityService.currentUser)
        } else
            neoTask.completedBy(neoSecurityService.currentUser)

        saveTask(neoTask)
    }

    DetailerTask completeAdhocDetailTask(DetailerTask detailerTask, String customerUuid) {
        if (detailerTask.isCancelled()) {
            log.warn("Cannot synchronise a cancelled task [${detailerTask.description}]")
            return null
        }

        assertNotDuplicate(detailerTask)

        def customer = customerRepository.findByUuid(customerUuid)

        if (!customer) {
            log.warn("AdhocDetailCustomer Not Found: [$customerUuid]")
            //customer could have been deleted
            return null
        }

        detailerTask.isAdhock = true
        detailerTask.customer = customer
        detailerTask.description = "Ad hoc Detailing[$customer.outletName]"

        detailerTask.completedBy(neoSecurityService.currentUser)
        detailerTask.denyUuidAlter()

        saveTask(detailerTask)
    }

    MalariaDetails completeAdhocMalariaTask(MalariaDetails detailerTask, String customerUuid) {
        if (detailerTask.isCancelled()) {
            log.warn("Cannot synchronise a cancelled task [${detailerTask.description}]")
            return null
        }

        assertNotDuplicate(detailerTask)

        def customer = customerRepository.findByUuid(customerUuid)


        if (!customer) {
            log.warn("AdhocDetailCustomer Not Found: [$customerUuid]")
            //customer could have been deleted
            return null
        }

        detailerTask.isAdhock = true
        detailerTask.customer = customer
        detailerTask.description = "Ad hoc Detailing[$customer.outletName]"

        detailerTask.completedBy(neoSecurityService.currentUser)
        detailerTask.denyUuidAlter()

        saveTask(detailerTask)
    }

    def generateTasks(List<Territory> territories,
                      List<CustomerSegment> segments,
                      Date startDate,
                      List<Integer> workDays,
                      int tasksPerDay,
                      Class<? extends Task> taskType,
                      boolean clusterTasks) {


        def messages = []
        def allTasks = []

        time("Genrating possible tasks for $territories") {
            territories.each { t ->
                def tasks = []
                def maximumTasks = tasksPerDay * workDays.size()

                segments.each { s ->
                    time("Generating Tasks for: Territory[$t] and Segment[$s]") {
                        def ts = generateTasks(t, s, startDate, workDays, taskType)
                        
                        if (ts.size() == 0) {
                            return
                        }

                        for (int i = 0; i < ts.size(); i++) {
                            ts.get(i).segment = s.name
                        }

                        if(!clusterTasks){
                            def max = 0
                            if (maximumTasks/4 > ts.size()) {
                                max = ts.size() - 1
                            } else {
                                max = maximumTasks/4 - 1
                            }

                            tasks.addAll ts[0..max]
                            log.info("Added [${ts[0..max].size()}] Tasks for Territory[$t],Segment[$s]")
                            return
                        }
                        
                        tasks.addAll ts
                        log.info("Added [${ts.size()}] Tasks for Territory[$t],Segment[$s]")
                    }
                }

                if (tasks) {
                    log.info "***** Clustering: Territory[$t] Tasks[${tasks.size()}]"
                    // messages << "$t(${tasks.size()})"
                    if (clusterTasks) {
                        def clusters = clusterService.assignDueDates(tasks, startDate, workDays, tasksPerDay)
                        tasks = clusters.collect { it.points.collect { it.task } }.flatten()

                    } else {
                        // Assign due dates
                        def assignedTasks = 0
                        def nextAvailableDate = startDate
                        tasks.each { task ->
                            if (assignedTasks > (tasksPerDay-1)) {
                                nextAvailableDate++
                                assignedTasks = 0
                            }
                            task.setDueDate(nextAvailableDate)
                            assignedTasks++
                        }

                    }
                    messages << "$t(${tasks.size()})"
                    allTasks.addAll(tasks)
                } else {
                    log.warn "***WARNING:***No Tasks Generated for Territory[$t]"
                }
            }
        }
        print allTasks
        time("Saving Generated Tasks ${allTasks.size()}") { taskRepository.save(allTasks) }

        return [messages, allTasks]

    }

    List<Task> generateTasks(Territory t, CustomerSegment s, Date startDate, List<Integer> workDays, Class<? extends Task> taskType) {

        def results
        if (taskType == DetailerTask)
            results = customerRepository.findAllWithoutNewDetailingTasks(t.id, s.id, s.numberOfTasks)
        else
            results = customerRepository.findAllWithoutSalesCalls(t.id, s.id, s.numberOfTasks)


        def tasks = []
        results.each { r ->
            if (s.shouldGenerateTask(r.completionDate)) {
                if (taskType == DetailerTask)
                    tasks << new DetailerTask(customer: r.customer, description: "Detailing [$r.customer.outletName]", dueDate: getNextWorkDay(workDays, startDate))
                else
                    tasks << new SalesCall(customer: r.customer, dueDate: getNextWorkDay(workDays, startDate))

            }
        }
        log.info("Generated [${tasks.size()}] Tasks for Territory[$t],Segment[$s]")

        return tasks
    }

    /* Orders */

    Order findOrder(Long id) {
        orderRepository.findOne(id)
    }

    SalesCall findOrder(String uuid) {
        salesCallRepository.findByUuidImpl(uuid)
    }

    Order findOrderByClientRefId(String refId) {
        orderRepository.findByClientRefId(refId)
    }

    Task findTaskByClientRefId(String refId) {
        taskRepository.findByClientRefId(refId)
    }

    void assertNotDuplicate(Task task) {
        Assert.notNull(task.clientRefId, "Task: $task Has No Client Ref Id")
        def t = taskRepository.findByClientRefId(task.clientRefId)
        Assert.isNull(t, "Task: $t Already Exists In the System")
    }

    DirectSale findDirectSaleByClientRefId(String refId) {
        directSaleRepository.findByClientRefId(refId)
    }

    def deleteNewConcreteTaskOfType(Customer customerId, Class taskType) {
        def labelName = "_${taskType.simpleName}"
        //start c = node({customerId}) match c-[:CUST_TASK]-(t:{taskType}{status:'new'}) delete t
        def q = start(nodesById('c', customerId.id)).match(node('c').out(CUST_TASK).as('r').node('t').label(labelName).values(value('status', Task.STATUS_NEW)))
                .delete(identifier('r'), identifier('t'))
        log.trace("Query:deleting old calls: $q")
        taskRepository.query(q, EMPTY_MAP)
    }
}