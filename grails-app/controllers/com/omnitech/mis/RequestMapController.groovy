package com.omnitech.mis


import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional

/**
 * RequestMapController
 * A controller class handles incoming web requests and performs actions such as redirects, rendering views and so on.
 */
@Transactional(readOnly = true)
class RequestMapController {

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

	def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond RequestMap.list(params), model:[requestMapInstanceCount: RequestMap.count()]
    }

	def list(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond RequestMap.list(params), model:[requestMapInstanceCount: RequestMap.count()]
    }

    def show(RequestMap requestMapInstance) {
        respond requestMapInstance
    }

    def create() {
        respond new RequestMap(params)
    }

    @Transactional
    def save(RequestMap requestMapInstance) {
        if (requestMapInstance == null) {
            notFound()
            return
        }

        if (requestMapInstance.hasErrors()) {
            respond requestMapInstance.errors, view:'create'
            return
        }

        requestMapInstance.save flush:true

        request.withFormat {
            form {
                flash.message = message(code: 'default.created.message', args: [message(code: 'requestMapInstance.label', default: 'RequestMap'), requestMapInstance.id])
                redirect requestMapInstance
            }
            '*' { respond requestMapInstance, [status: CREATED] }
        }
    }

    def edit(RequestMap requestMapInstance) {
        respond requestMapInstance
    }

    @Transactional
    def update(RequestMap requestMapInstance) {
        if (requestMapInstance == null) {
            notFound()
            return
        }

        if (requestMapInstance.hasErrors()) {
            respond requestMapInstance.errors, view:'edit'
            return
        }

        requestMapInstance.save flush:true

        request.withFormat {
            form {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'RequestMap.label', default: 'RequestMap'), requestMapInstance.id])
                redirect requestMapInstance
            }
            '*'{ respond requestMapInstance, [status: OK] }
        }
    }

    @Transactional
    def delete(RequestMap requestMapInstance) {

        if (requestMapInstance == null) {
            notFound()
            return
        }

        requestMapInstance.delete flush:true

        request.withFormat {
            form {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'RequestMap.label', default: 'RequestMap'), requestMapInstance.id])
                redirect action:"index", method:"GET"
            }
            '*'{ render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'requestMapInstance.label', default: 'RequestMap'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}
