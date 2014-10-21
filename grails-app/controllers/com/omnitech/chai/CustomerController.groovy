package com.omnitech.chai

import com.omnitech.chai.model.Customer
import com.omnitech.chai.model.CustomerContact
import com.omnitech.chai.util.ModelFunctions
import grails.converters.JSON
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
    def regionService

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        def page = customerService.listCustomers(params)
        respond page.content, model: [customerInstanceCount: page.totalElements]
    }

    def search(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        if (params.term) {
            redirect(action: 'search', id: params.term)
            return
        }
        def page = customerService.searchCustomers(params.id, params)
        respond page.content, view: 'index', model: [customerInstanceCount: page.totalElements]
    }


    def show() {
        def id = extractId(params)
        if (id == -1) {
            notFound(); return
        }
        respond customerService.findCustomer(id)
    }

    def create() {
        def customer = new Customer()
        respond ModelFunctions.bind(customer, params), model: getPageModel(customer.copyToContacts2LazyList())
    }

    def save(Customer customerInstance) {
        if (customerInstance == null) {
            notFound()
            return
        }

        if (customerInstance.hasErrors()) {
            respond customerInstance.errors, view: 'create', model: getPageModel(customerInstance.copyToContacts2LazyList())
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
        respond customerInstance, model: getPageModel(customerInstance.copyToContacts2LazyList())
    }

    def update(Customer customerInstance) {
        if (customerInstance == null) {
            notFound()
            return
        }

        if (customerInstance.hasErrors()) {
            respond customerInstance.errors, view: 'edit', model: getPageModel(customerInstance.copyToContacts2LazyList())
            return
        }

        //reset the set just in case there are some contacts were deleted
        customerInstance.customerContacts = new HashSet<>()

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

    private Map getPageModel(List<CustomerContact> contacts) {
        def subCountys = regionService.listAllSubCountys()
        subCountys = subCountys.sort { it.description }
        if (contacts)
            return [jsonContacts: (contacts as JSON).toString(true), subCounties: subCountys]
        return [jsonContacts: '[]', subCounties: subCountys]
    }
}
