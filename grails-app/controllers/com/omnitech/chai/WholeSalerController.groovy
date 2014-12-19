package com.omnitech.chai

import com.omnitech.chai.crm.TxHelperService
import com.omnitech.chai.model.Territory
import com.omnitech.chai.model.WholeSaler
import com.omnitech.chai.util.ModelFunctions
import grails.converters.JSON
import grails.transaction.Transactional
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page

import static com.omnitech.chai.util.ModelFunctions.extractId
import static org.springframework.http.HttpStatus.*

/**
 * WholeSalerController
 * A controller class handles incoming web requests and performs actions such as redirects, rendering views and so on.
 */
class WholeSalerController {

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def customerService
    def regionService
    @Autowired
    TxHelperService tx

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        def page = customerService.listWholeSalers(params)
        respond page.content, model: pageModel(page)
    }

    private Map pageModel(Page<WholeSaler> page) {
        def districts = regionService.listAllDistrictWithSubCounties()?.sort{it.name}
        return [wholeSalerInstanceCount: page.totalElements, districts: districts]
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

    def findMappedSubCounties() {
        def districtId = extractId(params, 'district')
        def territoryId = extractId(params, 'territory')

        if (districtId == -1 || territoryId == -1) {
            render '[]'
            return
        }

        def district = regionService.findDistrict(districtId)
        def wholeSaler = customerService.findWholeSaler(territoryId)
        tx.doInTransaction {
            neo.fetch(district.subCounties)
            neo.fetch(wholeSaler.subCounties)
        }
        def subcouties = district.subCounties.collect { sc ->
            [id       : sc.id, name: sc.name,
             mapped   : wholeSaler.subCounties.any { sc.id == it.id },
             territory: sc.wholeSaler?.name]
        }

        render subcouties as JSON

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
