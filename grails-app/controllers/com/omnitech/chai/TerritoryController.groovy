package com.omnitech.chai

import com.omnitech.chai.crm.TxHelperService
import com.omnitech.chai.model.Territory
import com.omnitech.chai.util.ModelFunctions
import grails.converters.JSON
import grails.transaction.Transactional
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page

import static com.omnitech.chai.util.ModelFunctions.extractId
import static org.springframework.http.HttpStatus.*

/**
 * TerritoryController
 * A controller class handles incoming web requests and performs actions such as redirects, rendering views and so on.
 */
class TerritoryController {

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE", mapTerritoryToSubCounties: 'POST']

    def  regionService
    @Autowired
    TxHelperService tx

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        def page = regionService.listTerritorys(params)
        respond page.content, model: pageModel(page)
    }

    private Map pageModel(Page<Territory> page) {
        def districts = regionService.listAllDistrictWithSubCounties()
        return [territoryInstanceCount: page.totalElements, districts: districts]
    }

    def search(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        if (params.term) {
            redirect(action: 'search', id: params.term)
            return
        }
        def page = regionService.searchTerritorys(params.id, params)
        respond page.content, view: 'index', model: [territoryInstanceCount: page.totalElements]
    }

    def show() {
        def id = extractId(params)
        if (id == -1) {
            notFound(); return
        }
        respond regionService.findTerritory(id)
    }

    def create() {
        respond ModelFunctions.bind(new Territory(), params)
    }

    def save(Territory territoryInstance) {
        if (territoryInstance == null) {
            notFound()
            return
        }

        if (territoryInstance.hasErrors()) {
            respond territoryInstance.errors, view:'create'
            return
        }

        regionService.saveTerritory territoryInstance

        request.withFormat {
            form {
                flash.message = message(code: 'default.created.message', args: [message(code: 'Territory.label', default: 'Territory'), territoryInstance.id])
                redirect action: 'show', id: territoryInstance.id
            }
            '*' { respond territoryInstance, [status: CREATED] }
        }
    }

    def edit() {
        def id = extractId(params)

        if (id == -1) {
            notFound(); return
        }
        def territoryInstance = regionService.findTerritory(id)
        respond territoryInstance
    }

    @Transactional
    def update(Territory territoryInstance) {
        if (territoryInstance == null) {
            notFound()
            return
        }

        if (territoryInstance.hasErrors()) {
            respond territoryInstance.errors, view:'edit'
            return
        }

        regionService.saveTerritory territoryInstance

        request.withFormat {
            form {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'Territory.label', default: 'Territory'), territoryInstance.id])
                redirect action: 'show', id: territoryInstance.id
            }
            '*'{ respond territoryInstance, [status: OK] }
        }
    }

    @Transactional
    def delete() {

        def id = extractId(params)

        if (id == -1) {
            notFound(); return
        }

        regionService.deleteTerritory id

        request.withFormat {
            form {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'Territory.label', default: 'Territory'), id])
                redirect action:"index", method:"GET"
            }
            '*'{ render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'Territory.label', default: 'Territory'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }

    def territoryAsJson() {
        def id = extractId(params)
        def territory = regionService.findTerritory(id)
        if (territory)
            render([id: territory.id, name: territory.name] as JSON)
        else
            render '{}'

    }

    def findMappedSubCounties() {
        def districtId = extractId(params, 'district')
        def territoryId = extractId(params, 'territory')

        if (districtId == -1 || territoryId == -1) {
            render '[]'
            return
        }

        def district = regionService.findDistrict(districtId)
        def territory = regionService.findTerritory(territoryId)
        tx.doInTransaction {
            neo.fetch(district.subCounties)
            neo.fetch(territory.subCounties)
        }
        def subcouties = district.subCounties.collect { sc ->
            [id: sc.id, name: sc.name,
                    mapped: territory.subCounties.any { sc.id == it.id },
                    territory: sc.territory?.name]
        }

        render subcouties as JSON

    }

    def mapTerritoryToSubCounties() {
        try {
            def data = request.JSON
            def territory = data.territory as Long
            def district = data.district as Long
            def subCounties = data.subCounties as List
            regionService.mapTerritoryToSubs(territory, district, subCounties)
            render 'Success!'
        } catch (Exception x) {
            log.error('Error while processing mapping territory', x)
            render status: BAD_REQUEST, text: x.message
        }
    }
}
