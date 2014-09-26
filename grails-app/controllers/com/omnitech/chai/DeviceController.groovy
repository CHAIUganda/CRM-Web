package com.omnitech.chai

import com.omnitech.chai.model.Device
import com.omnitech.chai.util.ChaiUtils

import static com.omnitech.chai.util.ChaiUtils.extractId
import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional

/**
 * DeviceController
 * A controller class handles incoming web requests and performs actions such as redirects, rendering views and so on.
 */
class DeviceController {

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def userService

	def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        def page = userService.listDevices(params)
        respond page.content, model: [deviceInstanceCount: page.totalElements]
    }

	def list(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        def page = userService.listDevices(params)
        respond page.content, model: [deviceInstanceCount: page.totalElements]
    }

    def show() {
        def id = extractId(params)
        if (id == -1) {
            notFound(); return
        }
        respond userService.findDevice(id)
    }

    def create() {
        respond ChaiUtils.bind(new Device(), params)
    }

    def save(Device deviceInstance) {
        if (deviceInstance == null) {
            notFound()
            return
        }

        if (deviceInstance.hasErrors()) {
            respond deviceInstance.errors, view:'create'
            return
        }

        userService.saveDevice deviceInstance

        request.withFormat {
            form {
                flash.message = message(code: 'default.created.message', args: [message(code: 'Device.label', default: 'Device'), deviceInstance.id])
                redirect action: 'show', id: deviceInstance.id
            }
            '*' { respond deviceInstance, [status: CREATED] }
        }
    }

    def edit() {
        def id = extractId(params)

        if (id == -1) {
            notFound(); return
        }
        def deviceInstance = userService.findDevice(id)
        respond deviceInstance
    }

    @Transactional
    def update(Device deviceInstance) {
        if (deviceInstance == null) {
            notFound()
            return
        }

        if (deviceInstance.hasErrors()) {
            respond deviceInstance.errors, view:'edit'
            return
        }

        userService.saveDevice deviceInstance

        request.withFormat {
            form {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'Device.label', default: 'Device'), deviceInstance.id])
                redirect action: 'show', id: deviceInstance.id
            }
            '*'{ respond deviceInstance, [status: OK] }
        }
    }

    @Transactional
    def delete() {

        def id = extractId(params)

        if (id == -1) {
            notFound(); return
        }

        userService.deleteDevice id

        request.withFormat {
            form {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'Device.label', default: 'Device'), id])
                redirect action:"index", method:"GET"
            }
            '*'{ render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'Device.label', default: 'Device'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}
