package com.omnitech.chai

import com.omnitech.chai.model.User
import grails.transaction.Transactional

import static org.springframework.http.HttpStatus.*

/**
 * UserController
 * A controller class handles incoming web requests and performs actions such as redirects, rendering views and so on.
 */
class UserController {

    def userService

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        def page = userService.list(params)
        respond page.content, model: [userInstanceCount: page.totalElements]
    }

    def list(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        def page = userService.list(params)
        respond page.content, model: [userInstanceCount: page.totalElements]
    }

    def show() {
        respond userService.findUser(params.id as Long)
    }

    def create() {
        respond new User(params), model: [rolez: userService.listAllRoles()]
    }

    def save(User userInstance) {
        if (userInstance == null) {
            notFound()
            return
        }

        if (userInstance.hasErrors()) {
            respond userInstance.errors, view: 'create', model: [rolez: userService.listAllRoles()]
            return
        }

        userService.saveUserWithRoles userInstance, getRoleIds()

        request.withFormat {
            form {
                flash.message = message(code: 'default.created.message', args: [message(code: 'userInstance.label', default: 'User'), userInstance.id])
                redirect(action: 'show', id: userInstance.id)
            }
            '*' { respond userInstance, [status: CREATED] }
        }
    }

    def edit() {
        def user = userService.findUser(params.id as Long)
        respond user, model: [rolez: userService.listAllRoles()]
    }


    def update(User userInstance) {
        println("Updatingng..............")
        if (userInstance == null) {
            notFound()
            return
        }

        if (userInstance.hasErrors()) {
            respond userInstance.errors, view: 'edit', model: [rolez: userService.listAllRoles()]
            return
        }

        userService.saveUserWithRoles userInstance, getRoleIds()

        request.withFormat {
            form {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'User.label', default: 'User'), userInstance.id])
                redirect(action: 'show', id: userInstance.id)
            }
            '*' { respond userInstance, [status: OK] }
        }
    }

    private List getRoleIds() {
        params.rolez instanceof String ? [params.rolez] : params.rolez
    }

    @Transactional
    def delete() {

        if (params.id == null) {
            notFound()
            return
        }
        Long longId
        try {
            longId = params.id as Long
        } catch (Exception x) {
            log.error("could not convert [$params.id] to number", x)
            notFound()
            return
        }
        userService.deleteUser(longId)
        request.withFormat {
            form {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'User.label', default: 'User'), longId])
                redirect(action: 'show', id: longId)
            }
            '*' { render status: NO_CONTENT }
        }


    }

    protected void notFound() {
        request.withFormat {
            form {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'userInstance.label', default: 'User'), params.id])
                redirect action: "index", method: "GET"
            }
            '*' { render status: NOT_FOUND }
        }
    }
}
