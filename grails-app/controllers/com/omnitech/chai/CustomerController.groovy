package com.omnitech.chai

import com.omnitech.chai.model.Customer
import com.omnitech.chai.model.CustomerContact
import com.omnitech.chai.util.GroupFlattener
import com.omnitech.chai.util.ModelFunctions
import com.omnitech.chai.util.ServletUtil
import grails.converters.JSON
import grails.transaction.Transactional
import org.springframework.web.multipart.commons.CommonsMultipartFile

import static com.omnitech.chai.model.Territory.TYPE_DETAILING
import static com.omnitech.chai.model.Territory.TYPE_SALES
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
        def user = neoSecurityService.currentUser
        def page = customerService.listCustomersInCtx(neoSecurityService.currentUser, params)
        def segments = customerService.listAllCustomerSegments()

        def territories = regionService.findTerritoriesForUser(user, [max: 2000])
        def detailingTerritories = territories.findAll { it.type == TYPE_DETAILING }
        def salesTerritories = territories.findAll { it.type == TYPE_SALES }
        return [
                customerInstanceList : page.content,
                customerInstanceCount: page.totalElements,
                segments             : segments,
                detailingTerritories : detailingTerritories,
                saleTerritories      : salesTerritories,
        ]
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
        flash.message = 'Segmentation Complete'
        redirect action: 'index'
    }

    def export() {
        def customers = customerService.exportCustomers()
        ServletUtil.exportCSV(response, 'Customers.csv', customers)
    }


    def searchByName(String id) {

        String term = id ?: params.term
        if (!term) {
            respond([])
            return
        }

        render customerService
                .searchCustomers(term, [sort: 'outletName', max: 10])
                .content
                .collect {
            [id        : it.id,
             district  : it.subCounty.district.name,
             outletName: it.outletName,
             contact   : it.customerContacts?.iterator()?.next()?.contact
            ]
        } as JSON
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
