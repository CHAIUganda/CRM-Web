package com.omnitech.chai

import com.omnitech.chai.model.Product
import com.omnitech.chai.util.GroupFlattener
import com.omnitech.chai.util.ModelFunctions
import grails.transaction.Transactional
import org.springframework.data.neo4j.transaction.Neo4jTransactional

import static ModelFunctions.extractId
import static org.springframework.http.HttpStatus.*

/**
 * ProductController
 * A controller class handles incoming web requests and performs actions such as redirects, rendering views and so on.
 */
class ProductController {

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def productService
    def regionService
    def txHelperService

    def index(Integer max) {
        params.max = Math.min(max ?: 100, 100)
        def products = productService.listAllProducts()
        def content = new GroupFlattener(leaves: products).normalize()
        [productInstanceList: content, productInstanceCount: products.size(), layout_nosearchtext: true]
    }

    def show() {
        def id = extractId(params)
        if (id == -1) {
            notFound(); return
        }

        def product = productService.findProduct(id)
        txHelperService.doInTransaction { neo.fetch(product.territories) }
        respond product
    }

    def create() {
        respond ModelFunctions.bind(new Product(), params), model: getPageModel()
    }

    @Neo4jTransactional
    def save(Product productInstance) {
        if (productInstance == null) {
            notFound()
            return
        }

        if (productInstance.hasErrors()) {
            respond productInstance.errors, view: 'create', model: getPageModel()
            return
        }

        productService.saveProduct productInstance, getTerritoryIds()

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
        txHelperService.doInTransaction {
            neo.fetch(productInstance.territories)
        }
        respond productInstance, model: getPageModel()
    }

    @Neo4jTransactional
    def update(Product productInstance) {
        if (productInstance == null) {
            notFound()
            return
        }

        if (productInstance.hasErrors()) {
            respond productInstance.errors, view: 'edit', model: getPageModel()
            return
        }

        productService.saveProduct productInstance, getTerritoryIds()

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

    private Map getPageModel() {
        [productGroups: productService.listAllProductGroups(), territories: regionService.listAllTerritorys()]
    }

    private List getTerritoryIds() {
        params.territoriz instanceof String ? [params.territoriz] : params.territoriz
    }
}
