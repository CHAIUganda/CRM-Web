package com.omnitech.chai.rest

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

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        def user = neoSecurityService.currentUser as User
        def page = customerService.findCustomersByUser(user.id, params)
        def customers = page.content.collect {
          ReflectFunctions.extractProperties(it)
        }
        respond customers
    }

}
