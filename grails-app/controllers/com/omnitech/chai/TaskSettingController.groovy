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

    def generation() {
        def territories = regionService.listAllTerritorys()
        def segments = customerService.listAllCustomerSegments()
        render view: 'generation', model: [territories: territories, segments: segments]
    }

    def generateTasks() {

        println(params)


        def taskType = params.taskType == 'Detailing' ? DetailerTask : Order

        def workDays = params.workDays instanceof String ? [params.workDays] : params.workDays
        workDays = workDays.collect { it as Integer }
        Assert.notNull workDays, 'Please Set Work Days'
        Assert.notEmpty workDays, 'Please Set Work Days'

        Assert.notNull params.startDate, 'Please Set A Start Date'
        def startDate = Date.parse('yyyy-MM-dd', params.startDate as String)
        Assert.isTrue((startDate - new Date()) >= 0, "Start Date[$params.startDate] Should Be Greater Than Today")

        def segments = getSegments()

        def territories = getTerritories()




        taskService.generateTasks(territories, segments, startDate, workDays, 15)

        flash.message = "Generation Is Done"

        redirect action: 'generation'

    }

    private def getTerritories() {

        def territoryIds = getTerritoryIds()

        if (!territoryIds) {
            throw new IllegalArgumentException('Please Select Territories')
        }

        def territories = territoryIds.collect { regionService.findTerritory(it as Long) }

        return territories
    }

    private def getTerritoryIds() {
        params.territories instanceof String ? [params.territories] : params.territories
    }

    private List<CustomerSegment> getSegments() {
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

    def handleException(IllegalArgumentException ex) {
        flash.error = ex.message
        redirect action: 'generation'
    }

    def handleException(AssertionError ex) {
        flash.error = ex.message
        redirect action: 'generation'
    }
}
