package com.omnitech.chai

import com.omnitech.chai.model.Product
import com.omnitech.chai.util.GroupFlattener
import com.omnitech.chai.util.ModelFunctions
import grails.transaction.Transactional

import static ModelFunctions.extractId
import static org.springframework.http.HttpStatus.*

/**
 * ProductController
 * A controller class handles incoming web requests and performs actions such as redirects, rendering views and so on.
 */
class ProductController {

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def productService

    def index(Integer max) {
        params.max = Math.min(max ?: 100, 100)
        def page = productService.listProducts(params)
        def content = new GroupFlattener(leaves: page.content).normalize()
        [productInstanceList: content, productInstanceCount: page.totalElements]
    }

    def show() {
        def id = extractId(params)
        if (id == -1) {
            notFound(); return
        }
        respond productService.findProduct(id)
    }

    def create() {
        respond ModelFunctions.bind(new Product(), params), model: getPageModel()
    }

    def save(Product productInstance) {
        if (productInstance == null) {
            notFound()
            return
        }

        if (productInstance.hasErrors()) {
            respond productInstance.errors, view: 'create'
            return
        }

        productService.saveProduct productInstance

        request.withFormat {
            form {
                flash.message = message(code: 'default.created.message', args: [message(code: 'Product.label', default: 'Product'), productInstance.id])
                redirect action: 'show', id: productInstance.id
            }
            '*' { respond productInstance, [status: CREATED] }
        }
    }

    def edit() {
        def id = extractId(params)

        if (id == -1) {
            notFound(); return
        }
        def productInstance = productService.findProduct(id)
        respond productInstance, model: getPageModel()
    }

    @Transactional
    def update(Product productInstance) {
        if (productInstance == null) {
            notFound()
            return
        }

        if (productInstance.hasErrors()) {
            respond productInstance.errors, view: 'edit'
            return
        }

        productService.saveProduct productInstance

        request.withFormat {
            form {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'Product.label', default: 'Product'), productInstance.id])
                redirect action: 'show', id: productInstance.id
            }
            '*' { respond productInstance, [status: OK] }
        }
    }

    @Transactional
    def delete() {

        def id = extractId(params)

        if (id == -1) {
            notFound(); return
        }

        productService.deleteProduct id

        request.withFormat {
            form {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'Product.label', default: 'Product'), id])
                redirect action: "index", method: "GET"
            }
            '*' { render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'Product.label', default: 'Product'), params.id])
                redirect action: "index", method: "GET"
            }
            '*' { render status: NOT_FOUND }
        }
    }

    private Map getPageModel() { [productGroups: productService.listAllProductGroups()] }
}
