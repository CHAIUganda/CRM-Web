package com.omnitech.chai.rest

import com.omnitech.chai.model.Customer
import com.omnitech.chai.model.User
import com.omnitech.chai.util.ModelFunctions
import com.omnitech.chai.util.ReflectFunctions
import grails.converters.JSON
import org.springframework.http.HttpStatus

/**
 * Created by kay on 10/29/14.
 */
class CustomerController {

    static namespace = 'rest'
    static responseFormats = ['json', 'xml']
    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]
    def customerService
    def neoSecurityService

    def list(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        def user = neoSecurityService.currentUser as User
        def customers = customerService.findAllCustomersByUser(user.id, params)
        def customerMaps = customers.collect { customerToMap(it) }
        respond customerMaps
    }

    Map customerToMap(Customer customer) {
        def cMap = ReflectFunctions.extractProperties(customer)
        def contacts = customer?.customerContacts?.collect { ReflectFunctions.extractProperties(it) }
        cMap['villageId'] = customer?.village?.id

        //todo to fix on upload
        cMap['longitude'] = customer.lng
        cMap['latitude'] = customer.lat
        cMap.with {
            remove('lat')
            remove('lng')
            remove('segmentScore')
            remove('wkt')
            remove('pictureURL')
        }
        if (contacts) {
            cMap['customerContacts'] = contacts
        }
        return cMap
    }

    def update() {
        def json = request.JSON as Map

        def customer = ModelFunctions.bind(Customer, json) as Customer

        if (!customer.validate()) {
            response.status = HttpStatus.BAD_REQUEST.value()
            render(customer.errors as JSON)
        }

        customerService.saveCustomer(customer)

        respond([status: HttpStatus.OK])
    }

    def renderError(String error) {
        response.status = HttpStatus.INTERNAL_SERVER_ERROR.value()
        render([status: 'error', message: error] as JSON)
    }



}
