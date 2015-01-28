package com.omnitech.chai

import com.omnitech.chai.model.Customer
import com.omnitech.chai.model.CustomerContact
import com.omnitech.chai.model.Role
import com.omnitech.chai.util.GroupFlattener
import com.omnitech.chai.util.ModelFunctions
import grails.converters.JSON
import grails.transaction.Transactional
import org.springframework.web.multipart.commons.CommonsMultipartFile

import static com.omnitech.chai.util.ModelFunctions.extractId
import static org.springframework.http.HttpStatus.*

/**
 * CustomerController
 * A controller class handles incoming web requests and performs actions such as redirects, rendering views and so on.
 */
class CustomerController {

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE", autoSegment: "POST"]

    def customerService
    def regionService
    def segmentationService
    def taskService
    def neoSecurityService

    def index(Integer max) {
        params.max = Math.min(max ?: 50, 100)
        def page
        if (neoSecurityService.currentUser.hasRole(Role.SUPER_ADMIN_ROLE_NAME))
            page = customerService.listCustomers(params)
        else
            page = customerService.listCustomersInCtx(neoSecurityService.currentUser.id, params)

        def content = new GroupFlattener(leaves: page.content).normalize()
        [customerInstanceList: content, customerInstanceCount: page.totalElements]
    }

    def search(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        if (params.term) {
            redirect(action: 'search', id: params.term)
            return
        }
        def page = customerService.searchCustomers(params.id, params)
        def content = new GroupFlattener(leaves: page.content).normalize()
        render view: 'index', model: [customerInstanceList: content, customerInstanceCount: page.totalElements]
    }


    def show() {
        def id = extractId(params)
        if (id == -1) {
            notFound(); return
        }
        [customerInstance: customerService.findCustomer(id)]
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

    def importCustomers() {
        CommonsMultipartFile f = request.getFile('myFile')
        if (!f || f.empty) {
            flash.message = 'file cannot be empty'
            redirect action: 'index'
            return
        }

        try {
            customerService.processCustomers(f.inputStream.text)
            flash.message = 'Customer uploaded Successfully'
        } catch (Throwable ex) {
            flash.message = "$ex.message"
            log.error("Error while importing Customers", ex)
        }

        redirect action: 'index'
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

    def autoSegment() {
        segmentationService.runSegmentationRoutine()
        def territorys = regionService.listAllTerritorys()
        territorys.each {
            log.debug "generationg tasks for $it"
//            taskService.autoGenerateTasks(it)
//                    taskService.autoGenerateTasks()
        }
        flash.message = 'Running auto Segmentation'
        redirect action: 'index'
    }

    def autoTasks() {
        def territorys = regionService.listAllTerritorys()
        territorys.each {
            log.debug "generationg tasks for $it"
            taskService.autoGenerateTasks(it)
            //        taskService.autoGenerateTasks()
        }
        flash.message = 'Tasks Have Been Generated'
        redirect action: 'index'
    }

    def searchByName(String id) {

        String term = id ?: params.term
        if (!term) {
            respond([])
            return
        }

        render  customerService
                .searchCustomers(term, [sort: 'outletName',max:10])
                .content
                .collect {
            [id        : it.id,
             district  : it.subCounty.district.name,
             outletName: it.outletName,
             contact   : it.customerContacts?.iterator()?.next()?.contact
            ]
        }       as JSON
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
