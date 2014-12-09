package com.omnitech.chai

import com.omnitech.chai.model.Village
import com.omnitech.chai.util.ModelFunctions
import grails.transaction.Transactional

import static ModelFunctions.extractId
import static org.springframework.http.HttpStatus.*

/**
 * VillageController
 * A controller class handles incoming web requests and performs actions such as redirects, rendering views and so on.
 */
class VillageController {

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def regionService

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        def page = regionService.listVillages(params)
        respond page.content, model: [villageInstanceCount: page.totalElements]
    }

    def show() {
        def id = extractId(params)
        if (id == -1) {
            notFound(); return
        }
        respond regionService.findVillage(id)
    }

    def create() {
        respond ModelFunctions.bind(new Village(), params)
    }

    def save(Village villageInstance) {
        if (villageInstance == null) {
            notFound()
            return
        }

        if (villageInstance.hasErrors()) {
            respond villageInstance.errors, view: 'create'
            return
        }

        regionService.saveVillage villageInstance

        request.withFormat {
            form {
                flash.message = message(code: 'default.created.message', args: [message(code: 'Village.label', default: 'Village'), villageInstance.id])
                redirect action: 'show', id: villageInstance.id
            }
            '*' { respond villageInstance, [status: CREATED] }
        }
    }

    def edit() {
        def id = extractId(params)

        if (id == -1) {
            notFound(); return
        }
        def villageInstance = regionService.findVillage(id)
        respond villageInstance
    }

    @Transactional
    def update(Village villageInstance) {
        if (villageInstance == null) {
            notFound()
            return
        }

        if (villageInstance.hasErrors()) {
            respond villageInstance.errors, view: 'edit'
            return
        }

        regionService.saveVillage villageInstance

        request.withFormat {
            form {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'Village.label', default: 'Village'), villageInstance.id])
                redirect action: 'show', id: villageInstance.id
            }
            '*' { respond villageInstance, [status: OK] }
        }
    }

    @Transactional
    def delete() {

        def id = extractId(params)

        if (id == -1) {
            notFound(); return
        }

        regionService.deleteVillage id

        request.withFormat {
            form {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'Village.label', default: 'Village'), id])
                redirect action: "index", method: "GET"
            }
            '*' { render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'Village.label', default: 'Village'), params.id])
                redirect action: "index", method: "GET"
            }
            '*' { render status: NOT_FOUND }
        }
    }

}
