package com.omnitech.chai

import com.omnitech.chai.model.Setting
import com.omnitech.chai.util.ModelFunctions
import grails.transaction.Transactional

import static com.omnitech.chai.util.ModelFunctions.extractId
import static org.springframework.http.HttpStatus.*

/**
 * SettingController
 * A controller class handles incoming web requests and performs actions such as redirects, rendering views and so on.
 */
class SettingController {

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def settingService

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        def page = settingService.listSettings(params)
        respond page.content, model: [settingInstanceCount: page.totalElements]
    }

    def search(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        if (params.term) {
            redirect(action: 'search', id: params.term)
            return
        }
        def page = settingService.searchSettings(params.id, params)
        respond page.content, view: 'index', model: [settingInstanceCount: page.totalElements]
    }

    def show() {
        def id = extractId(params)
        if (id == -1) {
            notFound(); return
        }
        respond settingService.findSetting(id)
    }

    def create() {
        respond ModelFunctions.bind(new Setting(), params)
    }

    def save(Setting settingInstance) {
        if (settingInstance == null) {
            notFound()
            return
        }

        if (settingInstance.hasErrors()) {
            respond settingInstance.errors, view: 'create'
            return
        }

        settingService.saveSetting settingInstance

        request.withFormat {
            form {
                flash.message = message(code: 'default.created.message', args: [message(code: 'Setting.label', default: 'Setting'), settingInstance.id])
                redirect action: 'show', id: settingInstance.id
            }
            '*' { respond settingInstance, [status: CREATED] }
        }
    }

    def edit() {
        def id = extractId(params)

        if (id == -1) {
            notFound(); return
        }
        def settingInstance = settingService.findSetting(id)
        respond settingInstance
    }

    @Transactional
    def update(Setting settingInstance) {
        if (settingInstance == null) {
            notFound()
            return
        }

        if (settingInstance.hasErrors()) {
            respond settingInstance.errors, view: 'edit'
            return
        }

        settingService.saveSetting settingInstance

        request.withFormat {
            form {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'Setting.label', default: 'Setting'), settingInstance.id])
                redirect action: 'show', id: settingInstance.id
            }
            '*' { respond settingInstance, [status: OK] }
        }
    }

    @Transactional
    def delete() {

        def id = extractId(params)

        if (id == -1) {
            notFound(); return
        }

        settingService.deleteSetting id

        request.withFormat {
            form {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'Setting.label', default: 'Setting'), id])
                redirect action: "index", method: "GET"
            }
            '*' { render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'Setting.label', default: 'Setting'), params.id])
                redirect action: "index", method: "GET"
            }
            '*' { render status: NOT_FOUND }
        }
    }
}
