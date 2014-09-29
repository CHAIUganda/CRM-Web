package com.omnitech.chai

import com.omnitech.chai.model.RequestMap
import com.omnitech.chai.util.ModelFunctions
import grails.transaction.Transactional

import static com.omnitech.chai.util.ModelFunctions.extractId
import static org.springframework.http.HttpStatus.*

/**
 * RequestMapController
 * A controller class handles incoming web requests and performs actions such as redirects, rendering views and so on.
 */
class RequestMapController {

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]
    def userService

    def index() {
        def maps = userService.listAllRequestMaps()
        respond maps
    }

    def list() {
        def maps = userService.listAllRequestMaps()
        respond maps
    }

    def show() {
        def id = extractId(params)
        if (id == -1) {
            notFound(); return
        }
        respond userService.findRequestMap(id)
    }

    def create() {
        respond ModelFunctions.bind(new RequestMap(), params)
    }

    @Transactional
    def save(RequestMap requestMapInstance) {
        if (requestMapInstance == null) {
            notFound()
            return
        }



        if (requestMapInstance.hasErrors()) {
            respond requestMapInstance.errors, view: 'create'
            return
        }

        userService.saveRequestMap requestMapInstance

        request.withFormat {
            form {
                flash.message = message(code: 'default.created.message', args: [message(code: 'requestMapInstance.label', default: 'RequestMap'), requestMapInstance.id])
                redirect action: 'show', id: requestMapInstance.id
            }
            '*' { respond requestMapInstance, [status: CREATED] }
        }
    }

    def edit() {
        def id = extractId(params)

        if (id == -1) {
            notFound(); return
        }
        def map = userService.findRequestMap(id)
        respond map
    }


    def update(RequestMap requestMapInstance) {
        if (requestMapInstance == null) {
            notFound()
            return
        }

        if (requestMapInstance.hasErrors()) {
            respond requestMapInstance.errors, view: 'edit'
            return
        }

        userService.saveRequestMap requestMapInstance

        request.withFormat {
            form {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'RequestMap.label', default: 'RequestMap'), requestMapInstance.id])
                redirect action: 'show', id: requestMapInstance.id
            }
            '*' { respond requestMapInstance, [status: OK] }
        }
    }

    @Transactional
    def delete(RequestMap requestMapInstance) {

        def id = extractId(params)

        if (id == -1) {
            notFound(); return
        }

        if (requestMapInstance == null) {
            notFound()
            return
        }

        userService.deleteRequestMap id

        request.withFormat {
            form {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'RequestMap.label', default: 'RequestMap'), requestMapInstance.id])
                redirect action: "index", method: "GET"
            }
            '*' { render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'requestMapInstance.label', default: 'RequestMap'), params.id])
                redirect action: "index", method: "GET"
            }
            '*' { render status: NOT_FOUND }
        }
    }
}
