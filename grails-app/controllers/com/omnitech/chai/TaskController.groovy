package com.omnitech.chai

import com.omnitech.chai.model.Task
import com.omnitech.chai.util.ChaiUtils
import com.omnitech.chai.util.ModelFunctions
import com.omnitech.chai.util.ReflectFunctions
import com.omnitech.chai.util.ServletUtil
import fuzzycsv.FuzzyCSV
import grails.converters.JSON
import grails.util.GrailsNameUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.neo4j.support.Neo4jTemplate
import org.springframework.http.HttpStatus
import org.springframework.security.access.AccessDeniedException

import java.text.SimpleDateFormat

import static com.omnitech.chai.util.ControllerUtils.customerToJsonMap
import static com.omnitech.chai.util.ControllerUtils.taskToJsonMap
import static com.omnitech.chai.util.ModelFunctions.extractId
import static org.springframework.http.HttpStatus.*

/**
 * TaskController
 * A controller class handles incoming web requests and performs actions such as redirects, rendering views and so on.
 */
class TaskController extends BaseController {

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE", updateTaskDate: 'POST']

    def taskService
    def userService
    def regionService
    def customerService
    def txHelperService
    def neoSecurityService
    @Autowired
    Neo4jTemplate neo

    protected def index(Integer max, Class<? extends Task> type, Map otherParams) {
        def user = neoSecurityService.currentUser
        if (params.remove('ui') == 'map') {
            redirect(action: 'map', params: params)
            return
        }
        def (page, users) = taskService.loadPageDataForUser(user, type, params, max, null)
        assert otherParams.view, 'View not specified for this action'
        render(view: otherParams.view, model: [taskInstanceList: page.content, taskInstanceCount: page.totalElements, users: users, taskRole: otherParams.taskRole])
    }

    protected def map(Integer max, Class<? extends Task> type, Map otherParams) {

        def user = neoSecurityService.currentUser
        def (page, users) = taskService.loadPageDataForUser(user, type, params, max, null)
        def taskData = page.content.collect { Task task ->
            return taskToJsonMap(task)
        }


        if (params.user) {
            def userInContext = userService.findUserByName(params.user)
            //todo use a query for this section to improve performance
            def customers = customerService.findAllCustomersByUser(userInContext.id, true, [max: 2000])
            customers.removeAll { c ->
                page.any { it?.customer?.id == c.id }
            }
            def customerData = customers.findAll { it.wkt != null }.collect { customerToJsonMap(it) }
            taskData.addAll(customerData)
        }

        def mapData = taskData as JSON

        def jsonMapString = mapData.toString(true)
        assert otherParams.view, 'View not specified for this action'
        render(view: otherParams.view, model: [taskInstanceList: page.content, taskInstanceCount: page.totalElements, users: users, mapData: jsonMapString])
    }

    protected def export(Class<? extends Task> type) {
        def user = params.user ? userService.findUserByName(params.user) : null
        def exportFields = ['DISTRICT', 'SUBCOUNTY', 'VILLAGE', 'OUTLET NAME', 'OUTLET TYPE']
        def fields = ReflectFunctions.findAllBasicFields(type).reverse()
        fields.removeAll('lastUpdated', 'dateCreated')
        exportFields.addAll(fields.collect { GrailsNameUtils.getNaturalName(it).toUpperCase() })
        if (user) {
            def data = taskService.exportTasksForUser(user.id, type)
            def csvData = FuzzyCSV.toCSV(data, *exportFields)
            ServletUtil.exportCSV(response, "Tasks-${params.user}.csv", csvData)
        } else {
            def data = taskService.exportAllTasks()
            def csvData = FuzzyCSV.toCSV(data, *exportFields)
            ServletUtil.exportCSV(response, "Tasks-All.csv", csvData)
        }
    }

    protected def search(Integer max, Class<? extends Task> taskType, Map otherParams) {
        def user = neoSecurityService.currentUser
        params.max = Math.min(max ?: 50, 100)
        if (params.term) {
            redirect(action: 'search', id: params.term)
            return
        }

        if (params.remove('ui') == 'map') {
            redirect(action: 'searchMap', params: params, id: params.term)
            return
        }

        def searchTerm = ModelFunctions.getWildCardRegex(params.id as String)
        def (page, users) = taskService.loadPageDataForUser(user, taskType, params, max, searchTerm)
        assert otherParams.view, 'View not specified in action'
        render view: otherParams.view, model: [taskInstanceList: page, taskInstanceCount: page.totalElements, users: users,taskRole: otherParams.taskRole]
    }

    protected def searchMap(Integer max, Class<? extends Task> taskType, Map otherParams) {
        def user = neoSecurityService.currentUser
        def searchTerm = ModelFunctions.getWildCardRegex(params.id as String)
        def (page, users) = taskService.loadPageDataForUser(user, taskType, params, max, searchTerm)
        def mapData = page.collect { Task t -> taskToJsonMap(t) } as JSON
        def jsonMapString = mapData.toString(true)
        respond page.content, view: '/task/map', model: [taskInstanceCount: page.totalElements, users: users, mapData: jsonMapString]
    }

    protected def show(Map otherParams) {
        def id = extractId(params)
        if (id == -1) {
            notFound(); return
        }

        def task = taskService.findTask(id)

        txHelperService.doInTransaction {
            neo.fetch(task.territoryUser())
        }
        assert otherParams.view, 'View not specified in action'
        render view: otherParams.view, model: [taskInstance: task]
    }

    protected def save(Task taskInstance) {
        if (taskInstance == null) {
            notFound()
            return
        }

        if (taskInstance.hasErrors()) {
            respond taskInstance.errors, view: 'create'
            return
        }

        taskService.saveTask taskInstance

        flash.message = message(code: 'default.created.message', args: [message(code: 'Task.label', default: taskInstance.getClass().simpleName), taskInstance.id])
        redirect action: 'show', id: taskInstance.id

    }

    protected def edit(Map otherParams) {
        def id = extractId(params)

        if (id == -1) {
            notFound(); return
        }
        def taskInstance = taskService.findTask(id)

        assert otherParams.view, 'View not specified in action'
        render view: otherParams.view, model: [taskInstance: taskInstance]
    }

    def delete() {

        def id = extractId(params)

        if (id == -1) {
            notFound(); return
        }

        taskService.deleteTask id

        request.withFormat {
            form {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'Task.label', default: 'Task'), id])
                redirect action: "index", method: "GET"
            }
            '*' { render status: NO_CONTENT }
        }
    }


    def createTaskJson(Closure<Task> create) {
        handleSafely {
            def json = request.JSON as Map

            assert json.customerId, 'Please specify Customer Id'
            def cid = json.customerId as Long
            def customer = customerService.findCustomer(cid)
            assert customer, 'Customer Should Exist In DB'

            def dueDate = Date.parse('yyyy-MM-dd', json.dueDate as String)

            def detailerTask = create(customer, dueDate)
            taskService.saveTask(detailerTask)
            return detailerTask.id
        }
    }


    def updateTaskDate() {
        try {
            def json = request.JSON as Map
            SimpleDateFormat df = new SimpleDateFormat('yyyy-MM-dd')
            Date date = df.parse(json.date)
            Long taskId = json.taskId as Long
            taskService.updateTaskDate(taskId, date)
            render "Success"
        } catch (Exception x) {
            log.error("Error Updating Task Date:", x)
            render([status: HttpStatus.INTERNAL_SERVER_ERROR, text: ChaiUtils.getBestMessage(x)] as JSON)
        }


    }

    def handleException(AccessDeniedException ex) {
        render view: '/login/denied', status: FORBIDDEN
    }

    def cluster() {
        clusterService.scheduleDetailerTasks()
        flash.message = "Done Clustering"
        redirect(action: 'index')
    }

    def clusterOrders() {
        clusterService.scheduleOrders()
        flash.message = "Done Clustering"
        redirect(action: 'index')
    }

    def autoSales() {
        def territorys = regionService.listAllTerritorys()[0..1]
        territorys.each {
            log.debug "generationg tasks for $it"
            taskService.generateSalesTasks(it)
        }
        flash.message = 'Tasks Have Been Generated'
        redirect action: 'index'
    }

    protected void notFound() {
        request.withFormat {
            form {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'Task.label', default: 'Task'), params.id])
                redirect action: "index", method: "GET"
            }
            '*' { render status: NOT_FOUND }
        }
    }


}
