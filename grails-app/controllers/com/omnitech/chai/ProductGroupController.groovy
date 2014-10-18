package com.omnitech.chai

import com.omnitech.chai.model.ProductGroup
import com.omnitech.chai.util.ModelFunctions
import grails.transaction.Transactional

import static com.omnitech.chai.util.ModelFunctions.extractId
import static org.springframework.http.HttpStatus.*

/**
 * ProductGroupController
 * A controller class handles incoming web requests and performs actions such as redirects, rendering views and so on.
 */
class ProductGroupController {

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def productService

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        def page = productService.listProductGroups(params)
        respond page.content, model: [productGroupInstanceCount: page.totalElements]
    }

    def search(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        if (params.term) {
            redirect(action: 'search', id: params.term)
            return
        }
        def page = productService.searchProductGroups(params.id, params)
        respond page.content, view: 'index', model: [productGroupInstanceCount: page.totalElements]
    }

    def show() {
        def id = extractId(params)
        if (id == -1) {
            notFound(); return
        }
        respond productService.findProductGroup(id)
    }

    def create() {
        respond ModelFunctions.bind(new ProductGroup(), params), model: [otherGroups: productService.listAllProductGroups()]
    }

    def save(ProductGroup productGroupInstance) {
        if (productGroupInstance == null) {
            notFound()
            return
        }

        if (productGroupInstance.hasErrors()) {
            respond productGroupInstance.errors, view: 'create'
            return
        }

        productService.saveProductGroup productGroupInstance

        request.withFormat {
            form {
                flash.message = message(code: 'default.created.message', args: [message(code: 'ProductGroup.label', default: 'ProductGroup'), productGroupInstance.id])
                redirect action: 'show', id: productGroupInstance.id
            }
            '*' { respond productGroupInstance, [status: CREATED] }
        }
    }

    def edit() {
        def id = extractId(params)

        if (id == -1) {
            notFound(); return
        }
        def productGroupInstance = productService.findProductGroup(id)
        def otherGroups = productService.listAllProductGroups().findAll {
            it.id != productGroupInstance.id
        }
        respond productGroupInstance, model: [otherGroups: otherGroups]
    }

    @Transactional
    def update(ProductGroup productGroupInstance) {
        if (productGroupInstance == null) {
            notFound()
            return
        }

        if (productGroupInstance.hasErrors()) {
            respond productGroupInstance.errors, view: 'edit'
            return
        }

        productService.saveProductGroup productGroupInstance

        request.withFormat {
            form {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'ProductGroup.label', default: 'ProductGroup'), productGroupInstance.id])
                redirect action: 'show', id: productGroupInstance.id
            }
            '*' { respond productGroupInstance, [status: OK] }
        }
    }

    @Transactional
    def delete() {

        def id = extractId(params)

        if (id == -1) {
            notFound(); return
        }

        productService.deleteProductGroup id

        request.withFormat {
            form {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'ProductGroup.label', default: 'ProductGroup'), id])
                redirect action: "index", method: "GET"
            }
            '*' { render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'ProductGroup.label', default: 'ProductGroup'), params.id])
                redirect action: "index", method: "GET"
            }
            '*' { render status: NOT_FOUND }
        }
    }
}
