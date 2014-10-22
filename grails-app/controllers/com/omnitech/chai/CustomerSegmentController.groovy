package com.omnitech.chai

import com.omnitech.chai.model.CustomerSegment
import com.omnitech.chai.util.ModelFunctions
import grails.transaction.Transactional

import static com.omnitech.chai.util.ModelFunctions.extractId
import static org.springframework.http.HttpStatus.*

/**
 * CustomerSegmentController
 * A controller class handles incoming web requests and performs actions such as redirects, rendering views and so on.
 */
class CustomerSegmentController {

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def customerService

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        def page = customerService.listCustomerSegments(params)
        respond page.content, model: [customerSegmentInstanceCount: page.totalElements]
    }

    def search(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        if (params.term) {
            redirect(action: 'search', id: params.term)
            return
        }
        def page = customerService.searchCustomerSegments(params.id, params)
        respond page.content, view: 'index', model: [customerSegmentInstanceCount: page.totalElements]
    }

    def show() {
        def id = extractId(params)
        if (id == -1) {
            notFound(); return
        }
        respond customerService.findCustomerSegment(id)
    }

    def create() {
        respond ModelFunctions.bind(new CustomerSegment(), params)
    }

    def save(CustomerSegment customerSegmentInstance) {
        if (customerSegmentInstance == null) {
            notFound()
            return
        }

        if (customerSegmentInstance.hasErrors()) {
            respond customerSegmentInstance.errors, view: 'create'
            return
        }

        customerService.saveCustomerSegment customerSegmentInstance

        request.withFormat {
            form {
                flash.message = message(code: 'default.created.message', args: [message(code: 'CustomerSegment.label', default: 'CustomerSegment'), customerSegmentInstance.id])
                redirect action: 'show', id: customerSegmentInstance.id
            }
            '*' { respond customerSegmentInstance, [status: CREATED] }
        }
    }

    def edit() {
        def id = extractId(params)

        if (id == -1) {
            notFound(); return
        }
        def customerSegmentInstance = customerService.findCustomerSegment(id)
        respond customerSegmentInstance
    }

    @Transactional
    def update(CustomerSegment customerSegmentInstance) {
        if (customerSegmentInstance == null) {
            notFound()
            return
        }

        if (customerSegmentInstance.hasErrors()) {
            respond customerSegmentInstance.errors, view: 'edit'
            return
        }

        customerService.saveCustomerSegment customerSegmentInstance

        request.withFormat {
            form {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'CustomerSegment.label', default: 'CustomerSegment'), customerSegmentInstance.id])
                redirect action: 'show', id: customerSegmentInstance.id
            }
            '*' { respond customerSegmentInstance, [status: OK] }
        }
    }

    @Transactional
    def delete() {

        def id = extractId(params)

        if (id == -1) {
            notFound(); return
        }

        customerService.deleteCustomerSegment id

        request.withFormat {
            form {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'CustomerSegment.label', default: 'CustomerSegment'), id])
                redirect action: "index", method: "GET"
            }
            '*' { render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'CustomerSegment.label', default: 'CustomerSegment'), params.id])
                redirect action: "index", method: "GET"
            }
            '*' { render status: NOT_FOUND }
        }
    }
}
