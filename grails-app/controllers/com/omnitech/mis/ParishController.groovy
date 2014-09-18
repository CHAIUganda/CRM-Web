package com.omnitech.mis


import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional

/**
 * ParishController
 * A controller class handles incoming web requests and performs actions such as redirects, rendering views and so on.
 */
@Transactional(readOnly = true)
class ParishController {

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond Parish.list(params), model: [parishInstanceCount: Parish.count()]
    }

    def list(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond Parish.list(params), model: [parishInstanceCount: Parish.count()]
    }

    def show(Parish parishInstance) {
        respond parishInstance
    }

    def create() {
        respond new Parish(params)
    }

    @Transactional
    def save(Parish parishInstance) {
        if (parishInstance == null) {
            notFound()
            return
        }

        if (parishInstance.hasErrors()) {
            respond parishInstance.errors, view: 'create'
            return
        }

        parishInstance.save flush: true

        request.withFormat {
            form {
                flash.message = message(code: 'default.created.message', args: [message(code: 'parishInstance.label', default: 'Parish'), parishInstance.id])
                redirect parishInstance
            }
            '*' { respond parishInstance, [status: CREATED] }
        }
    }

    def edit(Parish parishInstance) {
        respond parishInstance
    }

    @Transactional
    def update(Parish parishInstance) {
        if (parishInstance == null) {
            notFound()
            return
        }

        if (parishInstance.hasErrors()) {
            respond parishInstance.errors, view: 'edit'
            return
        }

        parishInstance.save flush: true

        request.withFormat {
            form {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'Parish.label', default: 'Parish'), parishInstance.id])
                redirect parishInstance
            }
            '*' { respond parishInstance, [status: OK] }
        }
    }

    @Transactional
    def delete(Parish parishInstance) {

        if (parishInstance == null) {
            notFound()
            return
        }

        parishInstance.delete flush: true

        request.withFormat {
            form {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'Parish.label', default: 'Parish'), parishInstance.id])
                redirect action: "index", method: "GET"
            }
            '*' { render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'parishInstance.label', default: 'Parish'), params.id])
                redirect action: "index", method: "GET"
            }
            '*' { render status: NOT_FOUND }
        }
    }
}
