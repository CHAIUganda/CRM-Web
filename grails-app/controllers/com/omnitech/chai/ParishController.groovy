package com.omnitech.chai

import com.omnitech.chai.model.Parish
import com.omnitech.chai.util.ModelFunctions
import grails.transaction.Transactional

import static ModelFunctions.extractId
import static org.springframework.http.HttpStatus.*

/**
 * ParishController
 * A controller class handles incoming web requests and performs actions such as redirects, rendering views and so on.
 */
class ParishController {

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def regionService

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        def page = regionService.listParishs(params)
        respond page.content, model: [parishInstanceCount: page.totalElements]
    }


    def show() {
        def id = extractId(params)
        if (id == -1) {
            notFound(); return
        }
        respond regionService.findParish(id)
    }

    def create() {
        respond ModelFunctions.bind(new Parish(), params)
    }

    def save(Parish parishInstance) {
        if (parishInstance == null) {
            notFound()
            return
        }

        if (parishInstance.hasErrors()) {
            respond parishInstance.errors, view: 'create'
            return
        }

        regionService.saveParish parishInstance

        request.withFormat {
            form {
                flash.message = message(code: 'default.created.message', args: [message(code: 'Parish.label', default: 'Parish'), parishInstance.id])
                redirect action: 'show', id: parishInstance.id
            }
            '*' { respond parishInstance, [status: CREATED] }
        }
    }

    def edit() {
        def id = extractId(params)

        if (id == -1) {
            notFound(); return
        }
        def parishInstance = regionService.findParish(id)
        respond parishInstance
    }

    @Transactional
    def update(Parish parishInstance) {
        if (parishInstance == null) {
            notFound()
            return
        }

        if (parishInstance.hasErrors()) {
            respond parishInstance.errors, view: 'edit'
            return
        }

        regionService.saveParish parishInstance

        request.withFormat {
            form {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'Parish.label', default: 'Parish'), parishInstance.id])
                redirect action: 'show', id: parishInstance.id
            }
            '*' { respond parishInstance, [status: OK] }
        }
    }

    @Transactional
    def delete() {

        def id = extractId(params)

        if (id == -1) {
            notFound(); return
        }

        regionService.deleteParish id

        request.withFormat {
            form {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'Parish.label', default: 'Parish'), id])
                redirect action: "index", method: "GET"
            }
            '*' { render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'Parish.label', default: 'Parish'), params.id])
                redirect action: "index", method: "GET"
            }
            '*' { render status: NOT_FOUND }
        }
    }
}
