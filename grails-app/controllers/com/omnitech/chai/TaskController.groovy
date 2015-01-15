package com.omnitech.chai

import com.omnitech.chai.model.DetailerTask
import com.omnitech.chai.model.Task
import com.omnitech.chai.util.ChaiUtils
import com.omnitech.chai.util.ModelFunctions
import com.omnitech.chai.util.ReflectFunctions
import com.omnitech.chai.util.ServletUtil
import fuzzycsv.FuzzyCSV
import grails.converters.JSON
import grails.transaction.Transactional
import grails.util.GrailsNameUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.neo4j.support.Neo4jTemplate
import org.springframework.http.HttpStatus

import java.text.SimpleDateFormat

import static com.omnitech.chai.util.ControllerUtils.taskToJsonMap
import static com.omnitech.chai.util.ModelFunctions.extractId
import static org.springframework.http.HttpStatus.*

/**
 * TaskController
 * A controller class handles incoming web requests and performs actions such as redirects, rendering views and so on.
 */
class TaskController {

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE", updateTaskDate: 'POST']

    def taskService
    def userService
    def regionService
    def customerService
    def txHelperService
    def clusterService
    @Autowired
    Neo4jTemplate neo

    def index(Integer max) {
        if (params.remove('ui') == 'map') {
            redirect(action: 'map', params: params)
            return
        }
        Page<Task> page = taskService.loadPageData(max, params, Task)
        [taskInstanceList: page.content, taskInstanceCount: page.totalElements, users: userService.listAllUsers([:])]
    }

    def map(Integer max) {
        def page = taskService.loadPageData(max, params, Task)
        def mapData = page.content.collect { task ->
            return taskToJsonMap(task)
        } as JSON
        def jsonMapString = mapData.toString(true)
        [taskInstanceList: page.content, taskInstanceCount: page.totalElements, users: userService.listAllUsers([:]), mapData: jsonMapString]
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
            render status: HttpStatus.INTERNAL_SERVER_ERROR, text: ChaiUtils.getBestMessage(x)
        }


    }


    def export() {
        def user = params.user ? userService.findUserByName(params.user) : null
        def exportFields = ['DISTRICT', 'SUBCOUNTY', 'VILLAGE', 'OUTLET NAME', 'OUTLET TYPE']
        def fields = ReflectFunctions.findAllBasicFields(DetailerTask).reverse()
        fields.removeAll('_dateLastUpdated', '_dateCreated')
        exportFields.addAll(fields.collect { GrailsNameUtils.getNaturalName(it).toUpperCase() })
        if (user) {
            def data = taskService.exportTasksForUser(user.id)
            def csvData = FuzzyCSV.toCSV(data, *exportFields)
            ServletUtil.exportCSV(response, "Tasks-${params.user}.csv", csvData)
        } else {
            def data = taskService.exportAllTasks()
            def csvData = FuzzyCSV.toCSV(data, *exportFields)
            ServletUtil.exportCSV(response, "Tasks-All.csv", csvData)
        }


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

    def search(Integer max) {
        params.max = Math.min(max ?: 50, 100)
        if (params.term) {
            redirect(action: 'search', id: params.term)
            return
        }

        if (params.remove('ui') == 'map') {
            redirect(action: 'searchMap', params: params, id: params.term)
            return
        }

        def page = taskService.searchTasks(params.id, params)
        txHelperService.doInTransaction {
            page.content.each { neo.fetch(it.territoryUser()) }
        }
        respond page.content, view: 'index', model: [taskInstanceCount: page.totalElements, users: userService.listAllUsers([:])]
    }

    def searchMap(Integer max) {
        def page = taskService.searchTasks(params.id, params)
        txHelperService.doInTransaction {
            page.content.each { neo.fetch(it.territoryUser()) }
        }

        def mapData = page.content.collect { ReflectFunctions.extractProperties(it) } as JSON
        def jsonMapString = mapData.toString(true)
        respond page.content, view: 'map', model: [taskInstanceCount: page.totalElements, users: userService.listAllUsers([:]), mapData: jsonMapString]
    }

    def show() {
        def id = extractId(params)
        if (id == -1) {
            notFound(); return
        }

        def task = taskService.findTask(id)

        txHelperService.doInTransaction {
            neo.fetch(task.territoryUser())
        }
        [taskInstance: task]
    }

    def create() {
        respond ModelFunctions.bind(new Task(), params), model: [users: userService.listAllUsers(), customers: customerService.listAllCustomers()]
    }

    def save(Task taskInstance) {
        if (taskInstance == null) {
            notFound()
            return
        }

        if (taskInstance.hasErrors()) {
            respond taskInstance.errors, view: 'create'
            return
        }

        taskService.saveTask taskInstance

        request.withFormat {
            form {
                flash.message = message(code: 'default.created.message', args: [message(code: 'Task.label', default: 'Task'), taskInstance.id])
                redirect action: 'show', id: taskInstance.id
            }
            '*' { respond taskInstance, [status: CREATED] }
        }
    }

    def edit() {
        def id = extractId(params)

        if (id == -1) {
            notFound(); return
        }
        def taskInstance = taskService.findTask(id)
        txHelperService.doInTransaction {
            neo.fetch(taskInstance.territoryUser())
        }
        [taskInstance: taskInstance, users: userService.listAllUsers(), customers: customerService.listAllCustomers()]
    }

    def update(Task taskInstance) {
        if (taskInstance == null) {
            notFound()
            return
        }

        if (taskInstance.hasErrors()) {
            respond taskInstance.errors, view: 'edit'
            return
        }

        taskService.saveTask taskInstance

        request.withFormat {
            form {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'Task.label', default: 'Task'), taskInstance.id])
                redirect action: 'show', id: taskInstance.id
            }
            '*' { respond taskInstance, [status: OK] }
        }
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
