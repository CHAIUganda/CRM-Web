package com.omnitech.chai.rest

import com.omnitech.chai.util.ReflectFunctions

/**
 * Created by kay on 10/31/14.
 */
class ProductController {

    static namespace = 'rest'
    static responseFormats = ['json', 'xml']
    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def productService

    def listProductGroups() {
        def groups = productService.listAllProductGroups().collect { ReflectFunctions.extractProperties(it) }
        respond groups
    }

    def listProducts() {
        def products = productService.listAllProducts().collect {
            def map = ReflectFunctions.extractProperties(it)
            map.groupId = it?.group?.id
            return map
        }
        respond products
    }

}
