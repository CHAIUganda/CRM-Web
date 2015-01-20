package com.omnitech.chai

import com.omnitech.chai.model.*
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

import static com.omnitech.chai.util.ControllerUtils.taskToJsonMap
import static com.omnitech.chai.util.ModelFunctions.extractId
import static org.springframework.http.HttpStatus.NOT_FOUND
import static org.springframework.http.HttpStatus.NO_CONTENT

/**
 * DetailerTaskController
 * A controller class handles incoming web requests and performs actions such as redirects, rendering views and so on.
 */
class DetailerTaskController {

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def taskService
    def userService
    def regionService
    def customerService
    def txHelperService
    def neoSecurityService
    @Autowired
    Neo4jTemplate neo

    def index(Integer max) {
        def user = neoSecurityService.currentUser
        if (params.remove('ui') == 'map') {
            redirect(action: 'map', params: params)
            return
        }
        Page<Task> page
        if (user.hasRole(Role.ADMIN_ROLE_NAME, Role.SUPER_ADMIN_ROLE_NAME)) {
            page = taskService.loadPageData(max, params, DetailerTask)
        } else {
            page = taskService.loadSuperVisorUserData(max, params, DetailerTask,user.id)
        }
        render(view: '/task/index', model: [taskInstanceList: page.content, taskInstanceCount: page.totalElements, users: userService.listUsersForUser(user.id,[max:2000],Role.DETAILER_ROLE_NAME)])
    }

    def indexSales(Integer max) {
        if (params.remove('ui') == 'map') {
            redirect(action: 'map', params: params)
            return
        }
        Page<Task> page = taskService.loadPageData(max, params, resolveType())
        [taskInstanceList: page.content, taskInstanceCount: page.totalElements, users: userService.listAllUsers([:])]
    }

    def map(Integer max) {
        def page = taskService.loadPageData(max, params, DetailerTask)
        def mapData = page.content.collect { Task task ->
            return taskToJsonMap(task)
        } as JSON
        def jsonMapString = mapData.toString(true)
        render(view: '/task/map', model: [taskInstanceList: page.content, taskInstanceCount: page.totalElements, users: userService.listAllUsers([:]), mapData: jsonMapString])
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
        respond page.content, view: '/task/index', model: [taskInstanceCount: page.totalElements, users: userService.listAllUsers([:])]
    }

    def searchMap(Integer max) {
        def page = taskService.searchTasks(params.id, params)
        txHelperService.doInTransaction {
            page.content.each { neo.fetch(it.territoryUser()) }
        }

        def mapData = page.content.collect { ReflectFunctions.extractProperties(it) } as JSON
        def jsonMapString = mapData.toString(true)
        respond page.content, view: '/task/map', model: [taskInstanceCount: page.totalElements, users: userService.listAllUsers([:]), mapData: jsonMapString]
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
        render(view: '/task/show', model: [taskInstance: task])
    }

    def create() {
        render(view: '/task/create', model: [taskInstance: ModelFunctions.createObj(DetailerTask, params)])
    }

    def edit() {
        def id = extractId(params)

        if (id == -1) {
            notFound(); return
        }
        def taskInstance = taskService.findOrder(id)
        render(view: '/task/create', model: [taskInstance: taskInstance])
    }

    @Transactional
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

    private Class<Task> resolveType() {
        if (params.lType == 'Sales') {
            return Sale
        }
        return Order
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
