package com.omnitech.chai

import com.omnitech.chai.model.Region
import com.omnitech.chai.util.ModelFunctions
import org.codehaus.groovy.grails.web.binding.DataBindingUtils

import static ModelFunctions.extractId
import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional

class RegionController {

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def  regionService

	def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        def page = regionService.listAllRegions()
        respond page, model: [regionInstanceCount: page.size()]
    }

	def list(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        def page = regionService.listAllRegions()
        respond page, model: [regionInstanceCount: page.size()]
    }

    def show() {
        def id = extractId(params)
        if (id == -1) {
            notFound(); return
        }
        DataBindingUtils.bindObjectToDomainInstance()
        respond regionService.findRegion(id)
    }

    def create() {
        respond ModelFunctions.bind(new Region(), params)
    }

    def save(Region regionInstance) {
        if (regionInstance == null) {
            notFound()
            return
        }

        if (regionInstance.hasErrors()) {
            respond regionInstance.errors, view:'create'
            return
        }

        regionService.saveRegion regionInstance

        request.withFormat {
            form {
                flash.message = message(code: 'default.created.message', args: [message(code: 'Region.label', default: 'Region'), regionInstance.id])
                redirect action: 'show', id: regionInstance.id
            }
            '*' { respond regionInstance, [status: CREATED] }
        }
    }

    def edit() {
        def id = extractId(params)

        if (id == -1) {
            notFound(); return
        }
        def regionInstance = regionService.findRegion(id)
        respond regionInstance
    }

    @Transactional
    def update(Region regionInstance) {
        if (regionInstance == null) {
            notFound()
            return
        }

        if (regionInstance.hasErrors()) {
            respond regionInstance.errors, view:'edit'
            return
        }

        regionService.saveRegion regionInstance

        request.withFormat {
            form {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'Region.label', default: 'Region'), regionInstance.id])
                redirect action: 'show', id: regionInstance.id
            }
            '*'{ respond regionInstance, [status: OK] }
        }
    }

    @Transactional
    def delete() {

        def id = extractId(params)

        if (id == -1) {
            notFound(); return
        }

        regionService.deleteRegion id

        request.withFormat {
            form {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'Region.label', default: 'Region'), id])
                redirect action:"index", method:"GET"
            }
            '*'{ render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'Region.label', default: 'Region'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}
