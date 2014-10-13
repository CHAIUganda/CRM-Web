package com.omnitech.chai

import com.omnitech.chai.model.Territory
import com.omnitech.chai.util.ModelFunctions
import com.omnitech.chai.util.ModelFunctions
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.neo4j.support.Neo4jTemplate

import static com.omnitech.chai.util.ModelFunctions.extractId
import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional

/**
 * TerritoryController
 * A controller class handles incoming web requests and performs actions such as redirects, rendering views and so on.
 */
class TerritoryController {

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def  regionService

	def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        def page = regionService.listTerritorys(params)
        respond page.content, model: [territoryInstanceCount: page.totalElements]
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
}
