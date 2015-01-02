package com.omnitech.chai

import com.omnitech.chai.model.ReportGroup
import com.omnitech.chai.util.ModelFunctions
import grails.transaction.Transactional

import static com.omnitech.chai.util.ModelFunctions.extractId
import static org.springframework.http.HttpStatus.*

/**
 * ReportGroupController
 * A controller class handles incoming web requests and performs actions such as redirects, rendering views and so on.
 */
class ReportGroupController {

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def reportService

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        def page = reportService.listReportGroups(params)
        respond page.content, model: [reportGroupInstanceCount: page.totalElements]
    }

    def search(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        if (params.term) {
            redirect(action: 'search', id: params.term)
            return
        }
        def page = reportService.searchReportGroups(params.id, params)
        respond page.content, view: 'index', model: [reportGroupInstanceCount: page.totalElements]
    }

    def show() {
        def id = extractId(params)
        if (id == -1) {
            notFound(); return
        }
        respond reportService.findReportGroup(id)
    }

    def create() {
        respond ModelFunctions.bind(new ReportGroup(), params), model: [reportGroups: reportService.listAllReportGroups()]
    }

    def save(ReportGroup reportGroupInstance) {
        if (reportGroupInstance == null) {
            notFound()
            return
        }

        if (reportGroupInstance.hasErrors()) {
            respond reportGroupInstance.errors, view: 'create'
            return
        }

        reportService.saveReportGroup reportGroupInstance

        request.withFormat {
            form {
                flash.message = message(code: 'default.created.message', args: [message(code: 'ReportGroup.label', default: 'ReportGroup'), reportGroupInstance.id])
                redirect action: 'show', id: reportGroupInstance.id
            }
            '*' { respond reportGroupInstance, [status: CREATED] }
        }
    }

    def edit() {
        def id = extractId(params)

        if (id == -1) {
            notFound(); return
        }
        def reportGroupInstance = reportService.findReportGroup(id)
        respond reportGroupInstance, model: [reportGroups: reportService.listAllReportGroups()]
    }

    @Transactional
    def update(ReportGroup reportGroupInstance) {
        if (reportGroupInstance == null) {
            notFound()
            return
        }

        if (reportGroupInstance.hasErrors()) {
            respond reportGroupInstance.errors, view: 'edit'
            return
        }

        reportService.saveReportGroup reportGroupInstance

        request.withFormat {
            form {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'ReportGroup.label', default: 'ReportGroup'), reportGroupInstance.id])
                redirect action: 'show', id: reportGroupInstance.id
            }
            '*' { respond reportGroupInstance, [status: OK] }
        }
    }

    @Transactional
    def delete() {

        def id = extractId(params)

        if (id == -1) {
            notFound(); return
        }

        reportService.deleteReportGroup id

        request.withFormat {
            form {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'ReportGroup.label', default: 'ReportGroup'), id])
                redirect action: "index", method: "GET"
            }
            '*' { render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'ReportGroup.label', default: 'ReportGroup'), params.id])
                redirect action: "index", method: "GET"
            }
            '*' { render status: NOT_FOUND }
        }
    }
}
