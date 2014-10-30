package com.omnitech.chai.rest

import com.omnitech.chai.model.Customer
import com.omnitech.chai.model.User
import com.omnitech.chai.util.ReflectFunctions

/**
 * Created by kay on 10/29/14.
 */
class RCustomerController {

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
        if (contacts) {
            cMap['customerContacts'] = contacts
        }
        return cMap
    }

}
