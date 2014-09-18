package com.omnitech.mis


import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional

/**
 * SubCountyController
 * A controller class handles incoming web requests and performs actions such as redirects, rendering views and so on.
 */
@Transactional(readOnly = true)
class SubCountyController {

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond SubCounty.list(params), model: [subCountyInstanceCount: SubCounty.count()]
    }

    def list(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond SubCounty.list(params), model: [subCountyInstanceCount: SubCounty.count()]
    }

    def show(SubCounty subCountyInstance) {
        respond subCountyInstance
    }

    def create() {
        respond new SubCounty(params)
    }

    @Transactional
    def save(SubCounty subCountyInstance) {
        if (subCountyInstance == null) {
            notFound()
            return
        }

        if (subCountyInstance.hasErrors()) {
            respond subCountyInstance.errors, view: 'create'
            return
        }

        subCountyInstance.save flush: true

        request.withFormat {
            form {
                flash.message = message(code: 'default.created.message', args: [message(code: 'subCountyInstance.label', default: 'SubCounty'), subCountyInstance.id])
                redirect subCountyInstance
            }
            '*' { respond subCountyInstance, [status: CREATED] }
        }
    }

    def edit(SubCounty subCountyInstance) {
        respond subCountyInstance
    }

    @Transactional
    def update(SubCounty subCountyInstance) {
        if (subCountyInstance == null) {
            notFound()
            return
        }

        if (subCountyInstance.hasErrors()) {
            respond subCountyInstance.errors, view: 'edit'
            return
        }

        subCountyInstance.save flush: true

        request.withFormat {
            form {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'SubCounty.label', default: 'SubCounty'), subCountyInstance.id])
                redirect subCountyInstance
            }
            '*' { respond subCountyInstance, [status: OK] }
        }
    }

    @Transactional
    def delete(SubCounty subCountyInstance) {

        if (subCountyInstance == null) {
            notFound()
            return
        }

        subCountyInstance.delete flush: true

        request.withFormat {
            form {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'SubCounty.label', default: 'SubCounty'), subCountyInstance.id])
                redirect action: "index", method: "GET"
            }
            '*' { render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'subCountyInstance.label', default: 'SubCounty'), params.id])
                redirect action: "index", method: "GET"
            }
            '*' { render status: NOT_FOUND }
        }
    }
}
