package com.omnitech.chai

import com.omnitech.chai.model.DetailerTask
import com.omnitech.chai.model.Task
import com.omnitech.chai.util.ModelFunctions
import com.omnitech.chai.util.ReflectFunctions
import com.omnitech.chai.util.ServletUtil
import fuzzycsv.FuzzyCSV
import grails.converters.JSON
import grails.transaction.Transactional
import grails.util.GrailsNameUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.neo4j.support.Neo4jTemplate

import static com.omnitech.chai.util.ModelFunctions.extractId
import static org.springframework.http.HttpStatus.*

/**
 * TaskController
 * A controller class handles incoming web requests and performs actions such as redirects, rendering views and so on.
 */
class TaskController {

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def taskService
    def userService
    def regionService
    def customerService
    def txHelperService
    @Autowired
    Neo4jTemplate neo

    def index(Integer max) {
        if (params.remove('ui') == 'map') {
            redirect(action: 'map', params: params)
            return
        }
        Page<Task> page = loadPageData(max)
        respond page.content, model: [taskInstanceCount: page.totalElements, users: userService.listAllUsers([:])]
    }

    def map(Integer max) {
        def page = loadPageData(max)
        def mapData = page.content.collect { ReflectFunctions.extractProperties(it) } as JSON
        def jsonString = mapData.toString(true)
        respond page.content, model: [taskInstanceCount: page.totalElements, users: userService.listAllUsers([:]), mapData: jsonString]
    }

    private Page<Task> loadPageData(Integer max) {
        params.max = Math.min(max ?: 50, 100)
        if (!params.sort) {
            params.sort = 'dueDate'
        }

        Page<Task> page = null

        def user = params.user ? userService.findUserByName(params.user) : null
        if (user) {
            def status = params.status ?: Task.STATUS_NEW
            params.status = status
            def tasks = taskService.findAllTaskForUser(user.id, status, params)
            page = new PageImpl<Task>(tasks)
        } else {
            if (params.status) {
                page = taskService.listTasksByStatus(params.status, params)
            } else {
                page = taskService.listTasks(params)
            }
        }

        txHelperService.doInTransaction {
            page.content.each { neo.fetch(it.territoryUser()) }
        }
        return page
    }


    def export() {
        def user = params.user ? userService.findUserByName(params.user) : null
        def exportFields = ['DISTRICT', 'SUBCOUNTY', 'VILLAGE', 'OUTLET NAME', 'OUTLET TYPE']
        def fields = ReflectFunctions.findAllBasicFields(DetailerTask).reverse()
        fields.removeAll('_dateLastUpdated', '_dateCreated')
        exportFields.addAll(fields.collect { GrailsNameUtils.getNaturalName(it).toUpperCase() })
        if (user) {
            def data = taskService.exportTasksForUser(user.id)
            def csvData = FuzzyCSV.toCSV(data, * exportFields)
            ServletUtil.exportCSV(response, "Tasks-${params.user}.csv", csvData)
        } else {
            def data = taskService.exportAllTasks()
            def csvData = FuzzyCSV.toCSV(data, * exportFields)
            ServletUtil.exportCSV(response, "Tasks-All.csv", csvData)
        }


    }

    def search(Integer max) {
        params.max = Math.min(max ?: 50, 100)
        if (params.term) {
            redirect(action: 'search', id: params.term)
            return
        }
        def page = taskService.searchTasks(params.id, params)
        txHelperService.doInTransaction {
            page.content.each { neo.fetch(it.territoryUser()) }
        }
        respond page.content, view: 'index', model: [taskInstanceCount: page.totalElements, users: userService.listAllUsers([:])]
    }

    def show() {
        def id = extractId(params)
        if (id == -1) {
            notFound(); return
        }

        def task = taskService.findTask(id)

        if (task.type == DetailerTask.simpleName) {
            task = taskService.findDetailerTask(task.id)
        }

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
        respond taskInstance, model: [users: userService.listAllUsers(), customers: customerService.listAllCustomers()]
    }

    @Transactional
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
