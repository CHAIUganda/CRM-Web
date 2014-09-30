package com.omnitech.chai

import com.omnitech.chai.model.District
import com.omnitech.chai.model.Region
import com.omnitech.chai.util.ModelFunctions
import grails.transaction.Transactional

import static ModelFunctions.extractId
import static org.springframework.http.HttpStatus.*

/**
 * DistrictController
 * A controller class handles incoming web requests and performs actions such as redirects, rendering views and so on.
 */
class DistrictController {

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def regionService

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        def districts = regionService.listAllDistricts()
        respond districts, model: [districtInstanceCount: districts.size()]
    }

    def list(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        def districts = regionService.listAllDistricts()
        respond districts, model: [districtInstanceCount: districts.size()]
    }

    def show() {
        def id = extractId(params)
        if (id == -1) {
            notFound(); return
        }
        respond regionService.findDistrict(id)
    }

    def create() {
        respond ModelFunctions.bind(new District(), params), model: [regions: regionService.listAllRegions()]
    }

    def save(District districtInstance) {

        if (districtInstance == null) {
            notFound()
            return
        }

        bindRegion(districtInstance)

        if (districtInstance.hasErrors()) {
            respond districtInstance.errors, view: 'create'
            return
        }

        regionService.saveDistrict districtInstance

        request.withFormat {
            form {
                flash.message = message(code: 'default.created.message', args: [message(code: 'District.label', default: 'District'), districtInstance.id])
                redirect action: 'show', id: districtInstance.id
            }
            '*' { respond districtInstance, [status: CREATED] }
        }
    }

    def edit() {
        def id = extractId(params)

        if (id == -1) {
            notFound(); return
        }
        def districtInstance = regionService.findDistrict(id)
        respond districtInstance, model: [regions: regionService.listAllRegions()]
    }

    @Transactional
    def update(District districtInstance) {
        if (districtInstance == null) {
            notFound()
            return
        }

        if (districtInstance.hasErrors()) {
            respond districtInstance.errors, view: 'edit'
            return
        }

        regionService.saveDistrict districtInstance

        request.withFormat {
            form {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'District.label', default: 'District'), districtInstance.id])
                redirect action: 'show', id: districtInstance.id
            }
            '*' { respond districtInstance, [status: OK] }
        }
    }

    @Transactional
    def delete() {

        def id = extractId(params)

        if (id == -1) {
            notFound(); return
        }

        regionService.deleteDistrict id

        request.withFormat {
            form {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'District.label', default: 'District'), id])
                redirect action: "index", method: "GET"
            }
            '*' { render status: NO_CONTENT }
        }
    }

    Region bindRegion(District district) {
        def regionId = params.'region.id' as String

        def region = { if (regionId) return regionService.findRegion(regionId.toLongSafe()) }()

        if (!region) district.errors.rejectValue('region', 'Region cannot be Empty')

        region
    }

    protected void notFound() {
        request.withFormat {
            form {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'District.label', default: 'District'), params.id])
                redirect action: "index", method: "GET"
            }
            '*' { render status: NOT_FOUND }
        }
    }
}
