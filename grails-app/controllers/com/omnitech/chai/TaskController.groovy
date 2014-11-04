package com.omnitech.chai

import com.omnitech.chai.model.Task
import com.omnitech.chai.util.ModelFunctions
import grails.transaction.Transactional

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

    def index(Integer max) {
        params.max = Math.min(max ?: 50, 100)
        if(!params.sort) {
            params.sort = 'dueDate'
        }
        def page = taskService.listTasks(params)
        respond page.content, model: [taskInstanceCount: page.totalElements,users: userService.listAllUsers([:])]
    }

    def search(Integer max) {
        params.max = Math.min(max ?: 50, 100)
        if (params.term) {
            redirect(action: 'search', id: params.term)
            return
        }
        def page = taskService.searchTasks(params.id, params)
        respond page.content, view: 'index', model: [taskInstanceCount: page.totalElements,users: userService.listAllUsers([:])]
    }

    def show() {
        def id = extractId(params)
        if (id == -1) {
            notFound(); return
        }
        respond taskService.findTask(id)
    }

    def create() {
        respond ModelFunctions.bind(new Task(), params), model: [users: userService.listAllUsers(), customers: customerService.listAllCustomers() ]
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
        respond taskInstance ,model: [users: userService.listAllUsers(), customers: customerService.listAllCustomers() ]
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
