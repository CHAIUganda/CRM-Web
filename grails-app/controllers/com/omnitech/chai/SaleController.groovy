package com.omnitech.chai

import com.omnitech.chai.model.DetailerTask
import com.omnitech.chai.model.Sale
import com.omnitech.chai.model.Task
import com.omnitech.chai.util.ReflectFunctions
import grails.converters.JSON
import grails.transaction.Transactional
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.neo4j.support.Neo4jTemplate

import static com.omnitech.chai.util.ControllerUtils.taskToJsonMap
import static com.omnitech.chai.util.ModelFunctions.extractId
import static org.springframework.http.HttpStatus.*

/**
 * Created by kay on 12/10/2014.
 */
class SaleController {

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def taskService
    def productService
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
        Page<Sale> page = taskService.loadPageData(max, params, Sale)
        render(view: '/order/index', model: [taskInstanceList: page.content, taskInstanceCount: page.totalElements, users: userService.listAllUsers([:])])
    }

    def map(Integer max) {
        def page = taskService.loadPageData(max, params, Sale)
        def mapData = page.content.collect { Task task ->
            return taskToJsonMap(task)
        } as JSON
        def jsonMapString = mapData.toString(true)
        render(view: '/task/map', model: [taskInstanceList: page.content, taskInstanceCount: page.totalElements, users: userService.listAllUsers([:]), mapData: jsonMapString])
    }

    def export() {
        render status: INTERNAL_SERVER_ERROR, text: 'Not Implemented Yet'
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

        if (task.type == DetailerTask.simpleName) {
            task = taskService.findDetailerTask(task.id)
        }

        txHelperService.doInTransaction {
            neo.fetch(task.territoryUser())
        }
        render view: '/order/show', model: [taskInstance: task]
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
