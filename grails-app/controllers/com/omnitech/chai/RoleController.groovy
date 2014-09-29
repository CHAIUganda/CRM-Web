package com.omnitech.chai

import com.omnitech.chai.model.Role
import com.omnitech.chai.util.ModelFunctions

import static ModelFunctions.extractId
import static org.springframework.http.HttpStatus.*

/**
 * RoleController
 * A controller class handles incoming web requests and performs actions such as redirects, rendering views and so on.
 */

class RoleController {

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]
    def userService

    def index() {
        def roles = userService.listAllRoles()
        respond roles
    }

    def list() {
        def roles = userService.listAllRoles()
        respond roles
    }

    def show() {
        long id = extractId(params)
        if (!id == -1) {
            notFound(); return
        }
        respond userService.findRole(id)
    }



    def create(Role role) {
        respond role
    }


    def save(Role roleInstance) {
        if (roleInstance == null) {
            notFound()
            return
        }

        if (roleInstance.hasErrors()) {
            respond roleInstance.errors, view: 'create'
            return
        }

        userService.saveRole roleInstance

        request.withFormat {
            form {
                flash.message = message(code: 'default.created.message', args: [message(code: 'roleInstance.label', default: 'Role'), roleInstance.id])
                redirect(action: 'show', id: roleInstance.id)
            }
            '*' { respond roleInstance, [status: CREATED] }
        }
    }

    def edit() {
        def id = extractId(params)

        if (id == -1) {
            notFound(); return
        }
        def roleInstance = userService.findRole(id)
        respond roleInstance
    }


    def update(Role roleInstance) {
        if (roleInstance == null) {
            notFound()
            return
        }

        if (roleInstance.hasErrors()) {
            respond roleInstance.errors, view: 'edit'
            return
        }

        userService.saveRole roleInstance


        request.withFormat {
            form {
                flash.message = message(code: 'default.created.message', args: [message(code: 'roleInstance.label', default: 'Role'), roleInstance.id])
                redirect(action: 'show', id: roleInstance.id)
            }
            '*' { respond roleInstance, [status: CREATED] }
        }
    }


    def delete() {

        def id = extractId(params)

        if (id == -1) {
            notFound(); return
        }

        userService.deleteRole id

        request.withFormat {
            form {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'Role.label', default: 'Role'), id])
                redirect action: "index", method: "GET"
            }
            '*' { render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'roleInstance.label', default: 'Role'), params.id])
                redirect action: "index", method: "GET"
            }
            '*' { render status: NOT_FOUND }
        }
    }
}
