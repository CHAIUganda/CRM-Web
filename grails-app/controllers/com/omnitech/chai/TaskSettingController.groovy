package com.omnitech.chai

import com.omnitech.chai.model.CustomerSegment
import com.omnitech.chai.model.DetailerTask
import com.omnitech.chai.model.Order
import org.springframework.util.Assert

/**
 * Created by kay on 1/23/2015.
 */
class TaskSettingController {

    def regionService
    def customerService
    def taskService
    def neoSecurityService

    def generationDetailer() {
        def (territories, segments) = getPageModel()
        render view: 'generationDetailer', model: [territories: territories, segments: segments, taskType: 'Detailing Tasks']
    }

    def generationOrder() {
        def (territories, segments) = getPageModel()
        render view: 'generationDetailer', model: [territories: territories, segments: segments, taskType: 'Sale Calls']
    }

    def generateDetailerTasks() {
        def msgs = generateTasks(DetailerTask)
        flash.message = "Generated Tasks ${msgs.join(',')}"
        redirect action: 'generationDetailer'
    }

    def generateOrderTasks() {
        def msgs = generateTasks(Order)
        flash.message = "Generated Tasks ${msgs.join(',')}"
        redirect action: 'generationOrder'
    }

    def handleException(IllegalArgumentException ex) {
        flash.error = ex.message
        if (actionName == 'generateDetailerTasks')
            redirect action: 'generationDetailer'
        else
            redirect action: 'generationOrder'
    }

    private def getPageModel() {
        def user = neoSecurityService.currentUser
        def territories = regionService.findTerritoriesForUser(user, [max: 2000])
        def segments = customerService.listAllCustomerSegments()
        [territories, segments]
    }

    private def generateTasks(Class taskType) {

        println(params)
        def workDays = params.workDays instanceof String ? [params.workDays] : params.workDays
        workDays = workDays.collect { it as Integer }
        Assert.notNull workDays, 'Please Set Work Days'
        Assert.notEmpty workDays, 'Please Set Work Days'

        Assert.notNull params.startDate, 'Please Set A Start Date'
        def startDate = Date.parse('yyyy-MM-dd', params.startDate as String)
        Assert.isTrue((startDate - new Date()) >= 0, "Start Date[$params.startDate] Should Be Greater Than Today")

        def segments = extractSegments()

        def territories = extractTerritories()

        taskService.generateTasks(territories, segments, startDate, workDays, 15, taskType)
    }

    private def extractTerritories() {

        def territoryIds = extractTerritoryIds()

        if (!territoryIds) {
            throw new IllegalArgumentException('Please Select Territories')
        }

        def territories = territoryIds.collect {
            def tId = it as Long
            regionService.findTerritory(tId)
        }

        return territories
    }

    private def extractTerritoryIds() {
        params.territories instanceof String ? [params.territories] : params.territories
    }

    private List<CustomerSegment> extractSegments() {
        def segments = params.segments as Map

        if (!segments || segments.every { !it.value }) {
            throw new IllegalArgumentException("Please Specify Number of Tasks In Segments")
        }


        def neoSegments = []

        segments.each { dbId, v ->
            if (v) {
                def cs = customerService.findCustomerSegment(dbId as Long)
                cs.numberOfTasks = v as Integer
                neoSegments << cs
            }
        }

        return neoSegments
    }
}
