package com.omnitech.chai

import com.omnitech.chai.model.WholeSaler
import com.omnitech.chai.util.ModelFunctions
import grails.transaction.Transactional

import static com.omnitech.chai.util.ModelFunctions.extractId
import static org.springframework.http.HttpStatus.*

/**
 * WholeSalerController
 * A controller class handles incoming web requests and performs actions such as redirects, rendering views and so on.
 */
class WholeSalerController {

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def customerService

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        def page = customerService.listWholeSalers(params)
        respond page.content, model: [wholeSalerInstanceCount: page.totalElements]
    }

    def search(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        if (params.term) {
            redirect(action: 'search', id: params.term)
            return
        }
        def page = customerService.searchWholeSalers(params.id, params)
        respond page.content, view: 'index', model: [wholeSalerInstanceCount: page.totalElements]
    }

    def show() {
        def id = extractId(params)
        if (id == -1) {
            notFound(); return
        }
        respond customerService.findWholeSaler(id)
    }

    def create() {
        respond ModelFunctions.bind(new WholeSaler(), params)
    }

    def save(WholeSaler wholeSalerInstance) {
        if (wholeSalerInstance == null) {
            notFound()
            return
        }

        if (wholeSalerInstance.hasErrors()) {
            respond wholeSalerInstance.errors, view: 'create'
            return
        }

        customerService.saveWholeSaler wholeSalerInstance

        request.withFormat {
            form {
                flash.message = message(code: 'default.created.message', args: [message(code: 'WholeSaler.label', default: 'WholeSaler'), wholeSalerInstance.id])
                redirect action: 'show', id: wholeSalerInstance.id
            }
            '*' { respond wholeSalerInstance, [status: CREATED] }
        }
    }

    def edit() {
        def id = extractId(params)

        if (id == -1) {
            notFound(); return
        }
        def wholeSalerInstance = customerService.findWholeSaler(id)
        respond wholeSalerInstance
    }

    @Transactional
    def update(WholeSaler wholeSalerInstance) {
        if (wholeSalerInstance == null) {
            notFound()
            return
        }

        if (wholeSalerInstance.hasErrors()) {
            respond wholeSalerInstance.errors, view: 'edit'
            return
        }

        customerService.saveWholeSaler wholeSalerInstance

        request.withFormat {
            form {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'WholeSaler.label', default: 'WholeSaler'), wholeSalerInstance.id])
                redirect action: 'show', id: wholeSalerInstance.id
            }
            '*' { respond wholeSalerInstance, [status: OK] }
        }
    }

    @Transactional
    def delete() {

        def id = extractId(params)

        if (id == -1) {
            notFound(); return
        }

        customerService.deleteWholeSaler id

        request.withFormat {
            form {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'WholeSaler.label', default: 'WholeSaler'), id])
                redirect action: "index", method: "GET"
            }
            '*' { render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'WholeSaler.label', default: 'WholeSaler'), params.id])
                redirect action: "index", method: "GET"
            }
            '*' { render status: NOT_FOUND }
        }
    }
}
