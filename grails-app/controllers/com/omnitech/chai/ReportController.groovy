package com.omnitech.chai

import com.omnitech.chai.model.Report
import com.omnitech.chai.util.GroupFlattener
import com.omnitech.chai.util.ModelFunctions
import com.omnitech.chai.util.ServletUtil
import grails.transaction.Transactional
import net.sf.dynamicreports.jasper.builder.JasperReportBuilder

import static com.omnitech.chai.util.ModelFunctions.extractId
import static org.springframework.http.HttpStatus.*

/**
 * ReportController
 * A controller class handles incoming web requests and performs actions such as redirects, rendering views and so on.
 */
class ReportController {

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def reportService

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        def page = reportService.listReports(params)
        def content = new GroupFlattener(leaves: page.content).normalize()
        [reportInstanceList: content, reportInstanceCount: page.totalElements]
    }

    def search(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        if (params.term) {
            redirect(action: 'search', id: params.term)
            return
        }
        def page = reportService.searchReports(params.id, params)
        respond page.content, view: 'index', model: [reportInstanceCount: page.totalElements]
    }

    def show() {
        def id = extractId(params)
        if (id == -1) {
            notFound(); return
        }
        respond reportService.findReport(id)
    }

    def conditionGroup() {
        render(template: 'conditionGroup')
    }

    def reportWiz() {
        def id = extractId(params)
        if (id == -1) {
            notFound(); return
        }
        [reportInstance: reportService.findReport(id)]
    }

    def download(Long id) {
        log.debug("downloading params: $params")
        def columns = params.cols as String
        def filter = params.filter ? params.filter : 'true'
        def report = reportService.findReport(id)
        def reportBuilder = reportService.buildReport(id, columns, filter)
        ServletUtil.setAttachment(response,"${report.name}.pdf")
        reportBuilder.toPdf(response.outputStream)
    }

    def create() {
        respond ModelFunctions.bind(new Report(), params), model: [reportGroups: reportService.listAllReportGroups()]
    }

    def save(Report reportInstance) {
        println(params)
        if (reportInstance == null) {
            notFound()
            return
        }

        if (reportInstance.hasErrors()) {
            respond reportInstance.errors, view: 'create', model: [reportGroups: reportService.listAllReportGroups()]
            return
        }

        reportService.saveReport reportInstance

        request.withFormat {
            form {
                flash.message = message(code: 'default.created.message', args: [message(code: 'Report.label', default: 'Report'), reportInstance.id])
                redirect action: 'show', id: reportInstance.id
            }
            '*' { respond reportInstance, [status: CREATED] }
        }
    }

    def edit() {
        def id = extractId(params)

        if (id == -1) {
            notFound(); return
        }
        def reportInstance = reportService.findReport(id)
        respond reportInstance, model: [reportGroups: reportService.listAllReportGroups()]
    }

    @Transactional
    def update(Report reportInstance) {
        if (reportInstance == null) {
            notFound()
            return
        }

        if (reportInstance.hasErrors()) {
            respond reportInstance.errors, view: 'edit', model: [reportGroups: reportService.listAllReportGroups()]
            return
        }

        reportService.saveReport reportInstance

        request.withFormat {
            form {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'Report.label', default: 'Report'), reportInstance.id])
                redirect action: 'show', id: reportInstance.id
            }
            '*' { respond reportInstance, [status: OK] }
        }
    }

    @Transactional
    def delete() {

        def id = extractId(params)

        if (id == -1) {
            notFound(); return
        }

        reportService.deleteReport id

        request.withFormat {
            form {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'Report.label', default: 'Report'), id])
                redirect action: "index", method: "GET"
            }
            '*' { render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'Report.label', default: 'Report'), params.id])
                redirect action: "index", method: "GET"
            }
            '*' { render status: NOT_FOUND }
        }
    }
}
