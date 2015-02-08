package com.omnitech.chai

import com.omnitech.chai.util.ChaiUtils
import grails.converters.JSON
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.neo4j.support.Neo4jTemplate
import org.springframework.http.HttpStatus

import java.text.SimpleDateFormat

import static org.springframework.http.HttpStatus.NOT_FOUND

/**
 * TaskController
 * A controller class handles incoming web requests and performs actions such as redirects, rendering views and so on.
 */
class TaskController {

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE", updateTaskDate: 'POST']

    def taskService
    def userService
    def regionService
    def customerService
    def clusterService
    @Autowired
    Neo4jTemplate neo


    def updateTaskDate() {
        try {
            def json = request.JSON as Map
            SimpleDateFormat df = new SimpleDateFormat('yyyy-MM-dd')
            Date date = df.parse(json.date)
            Long taskId = json.taskId as Long
            taskService.updateTaskDate(taskId, date)
            render "Success"
        } catch (Exception x) {
            log.error("Error Updating Task Date:", x)
            render([status: HttpStatus.INTERNAL_SERVER_ERROR, text: ChaiUtils.getBestMessage(x)] as JSON)
        }


    }

    def cluster() {
        clusterService.scheduleDetailerTasks()
        flash.message = "Done Clustering"
        redirect(action: 'index')
    }

    def clusterOrders() {
        clusterService.scheduleOrders()
        flash.message = "Done Clustering"
        redirect(action: 'index')
    }

    def autoSales() {
        def territorys = regionService.listAllTerritorys()[0..1]
        territorys.each {
            log.debug "generationg tasks for $it"
            taskService.generateSalesTasks(it)
        }
        flash.message = 'Tasks Have Been Generated'
        redirect action: 'index'
    }

    protected void notFound() {
        request.withFormat {
            form {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'Task.label', default: 'Task'), params.id])
                redirect action: "index", method: "GET"
            }
            '*' { render status: NOT_FOUND }
        }
    }


}
