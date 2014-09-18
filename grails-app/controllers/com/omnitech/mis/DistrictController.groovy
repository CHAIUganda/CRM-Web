package com.omnitech.mis


import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional

/**
 * DistrictController
 * A controller class handles incoming web requests and performs actions such as redirects, rendering views and so on.
 */
@Transactional(readOnly = true)
class DistrictController {

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond District.list(params), model: [districtInstanceCount: District.count()]
    }

    def list(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond District.list(params), model: [districtInstanceCount: District.count()]
    }

    def show(District districtInstance) {
        respond districtInstance
    }

    def create() {
        respond new District(params)
    }

    @Transactional
    def save(District districtInstance) {
        if (districtInstance == null) {
            notFound()
            return
        }

        if (districtInstance.hasErrors()) {
            respond districtInstance.errors, view: 'create'
            return
        }

        districtInstance.save flush: true

        request.withFormat {
            form {
                flash.message = message(code: 'default.created.message', args: [message(code: 'districtInstance.label', default: 'District'), districtInstance.id])
                redirect districtInstance
            }
            '*' { respond districtInstance, [status: CREATED] }
        }
    }

    def edit(District districtInstance) {
        respond districtInstance
    }

    @Transactional
    def update(District districtInstance) {
        if (districtInstance == null) {
            notFound()
            return
        }

        if (districtInstance.hasErrors()) {
            respond districtInstance.errors, view: 'edit'
            return
        }

        districtInstance.save flush: true

        request.withFormat {
            form {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'District.label', default: 'District'), districtInstance.id])
                redirect districtInstance
            }
            '*' { respond districtInstance, [status: OK] }
        }
    }

    @Transactional
    def delete(District districtInstance) {

        if (districtInstance == null) {
            notFound()
            return
        }

        districtInstance.delete flush: true

        request.withFormat {
            form {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'District.label', default: 'District'), districtInstance.id])
                redirect action: "index", method: "GET"
            }
            '*' { render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'districtInstance.label', default: 'District'), params.id])
                redirect action: "index", method: "GET"
            }
            '*' { render status: NOT_FOUND }
        }
    }
}
