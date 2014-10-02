package com.omnitech.chai

import com.omnitech.chai.model.Customer
import com.omnitech.chai.util.ModelFunctions
import grails.transaction.Transactional

import static com.omnitech.chai.util.ModelFunctions.extractId
import static org.springframework.http.HttpStatus.*

/**
 * CustomerController
 * A controller class handles incoming web requests and performs actions such as redirects, rendering views and so on.
 */
class CustomerController {

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def customerService

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        def page = customerService.listCustomers(params)
        respond page.content, model: [customerInstanceCount: page.totalElements]
    }

    def list(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        def page = customerService.listCustomers(params)
        respond page.content, model: [customerInstanceCount: page.totalElements]
    }

    def show() {
        def id = extractId(params)
        if (id == -1) {
            notFound(); return
        }
        respond customerService.findCustomer(id)
    }

    def create() {
        respond ModelFunctions.bind(new Customer(), params)
    }

    def save(Customer customerInstance) {
        if (customerInstance == null) {
            notFound()
            return
        }

        if (customerInstance.hasErrors()) {
            respond customerInstance.errors, view: 'create'
            return
        }

        customerService.saveCustomer customerInstance

        request.withFormat {
            form {
                flash.message = message(code: 'default.created.message', args: [message(code: 'Customer.label', default: 'Customer'), customerInstance.id])
                redirect action: 'show', id: customerInstance.id
            }
            '*' { respond customerInstance, [status: CREATED] }
        }
    }

    def edit() {
        def id = extractId(params)

        if (id == -1) {
            notFound(); return
        }
        def customerInstance = customerService.findCustomer(id)
        respond customerInstance
    }

    @Transactional
    def update(Customer customerInstance) {
        if (customerInstance == null) {
            notFound()
            return
        }

        if (customerInstance.hasErrors()) {
            respond customerInstance.errors, view: 'edit'
            return
        }

        customerService.saveCustomer customerInstance

        request.withFormat {
            form {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'Customer.label', default: 'Customer'), customerInstance.id])
                redirect action: 'show', id: customerInstance.id
            }
            '*' { respond customerInstance, [status: OK] }
        }
    }

    @Transactional
    def delete() {

        def id = extractId(params)

        if (id == -1) {
            notFound(); return
        }

        customerService.deleteCustomer id

        request.withFormat {
            form {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'Customer.label', default: 'Customer'), id])
                redirect action: "index", method: "GET"
            }
            '*' { render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'Customer.label', default: 'Customer'), params.id])
                redirect action: "index", method: "GET"
            }
            '*' { render status: NOT_FOUND }
        }
    }
}
