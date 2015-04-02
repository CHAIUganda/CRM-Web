package com.omnitech.chai

import com.omnitech.chai.model.SubCounty
import com.omnitech.chai.util.ModelFunctions
import grails.transaction.Transactional

import static com.omnitech.chai.util.ModelFunctions.extractId
import static org.springframework.http.HttpStatus.*

/**
 * SubCountyController
 * A controller class handles incoming web requests and performs actions such as redirects, rendering views and so on.
 */
class SubCountyController {

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def regionService

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        def page = regionService.listSubCountys(params)
        respond page.content, model: [subCountyInstanceCount: page.totalElements]
    }

    def search(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        if (params.term) {
            redirect(action: 'search', id: params.term)
            return
        }
        def page = regionService.searchSubCountys(params.id, params)
        respond page.content, view: 'index', model: [subCountyInstanceCount: page.totalElements]
    }

    def show() {
        def id = extractId(params)
        if (id == -1) {
            notFound(); return
        }
        respond regionService.findSubCounty(id)
    }

    def create() {
        respond ModelFunctions.bind(new SubCounty(), params), model: getPageModel()
    }

    def save(SubCounty subCountyInstance) {
        if (subCountyInstance == null) {
            notFound()
            return
        }

        if (subCountyInstance.hasErrors()) {
            respond subCountyInstance.errors, view: 'create', model: getPageModel()
            return
        }

        regionService.saveSubCounty subCountyInstance

        request.withFormat {
            form {
                flash.message = message(code: 'default.created.message', args: [message(code: 'SubCounty.label', default: 'SubCounty'), subCountyInstance.id])
                redirect action: 'show', id: subCountyInstance.id
            }
            '*' { respond subCountyInstance, [status: CREATED] }
        }
    }

    def edit() {
        def id = extractId(params)

        if (id == -1) {
            notFound(); return
        }
        def subCountyInstance = regionService.findSubCounty(id)
        respond subCountyInstance, model: getPageModel()
    }

    @Transactional
    def update(SubCounty subCountyInstance) {
        if (subCountyInstance == null) {
            notFound()
            return
        }

        if (subCountyInstance.hasErrors()) {
            respond subCountyInstance.errors, view: 'edit', model: getPageModel()
            return
        }

        regionService.saveSubCounty subCountyInstance

        request.withFormat {
            form {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'SubCounty.label', default: 'SubCounty'), subCountyInstance.id])
                redirect action: 'show', id: subCountyInstance.id
            }
            '*' { respond subCountyInstance, [status: OK] }
        }
    }

    @Transactional
    def delete() {

        def id = extractId(params)

        if (id == -1) {
            notFound(); return
        }
        try {
            regionService.deleteSubCounty id
        } catch (Exception x) {
            flash.error = x.message
            redirect action: 'show', id: id
            return
        }
        request.withFormat {
            form {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'SubCounty.label', default: 'SubCounty'), id])
                redirect action: "index", method: "GET"
            }
            '*' { render status: NO_CONTENT }
        }
    }

    Map getPageModel() {
        return [districts: regionService.listAllDistricts()]
    }

    protected void notFound() {
        request.withFormat {
            form {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'SubCounty.label', default: 'SubCounty'), params.id])
                redirect action: "index", method: "GET"
            }
            '*' { render status: NOT_FOUND }
        }
    }
}
