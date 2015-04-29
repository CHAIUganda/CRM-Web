package com.omnitech.chai

import com.omnitech.chai.model.CustomerSegment
import com.omnitech.chai.model.DetailerTask
import com.omnitech.chai.model.Role
import com.omnitech.chai.model.SalesCall
import com.omnitech.chai.model.Task
import com.omnitech.chai.repositories.TaskRepository
import com.omnitech.chai.repositories.impl.ITaskRepository
import com.omnitech.chai.repositories.impl.TaskRepositoryImpl
import com.omnitech.chai.util.ControllerUtils
import grails.converters.JSON
import org.springframework.util.Assert

import static com.omnitech.chai.util.ChaiUtils.bean
import static com.omnitech.chai.util.ChaiUtils.time

/**
 * Created by kay on 1/23/2015.
 */
class TaskSettingController {

    def regionService
    def customerService
    def taskService
    def neoSecurityService
    def txHelperService

    def generationDetailer() {
        def (territories, segments) = getPageModel()
        render view: 'generationDetailer', model: [territories: territories, segments: segments, taskType: 'Detailing Tasks']
    }

    def generationOrder() {
        def (territories, segments) = getPageModel()
        render view: 'generationDetailer', model: [territories: territories, segments: segments, taskType: 'Sale Calls']
    }

    def generateDetailerTasks() {
        def (msgs, tasks) = generateTasks(DetailerTask, true)
        flash.message = "Generated Tasks ${msgs.join(',')}"
        renderOnMap(tasks,DetailerTask)
    }

    def generateOrderTasks() {
        def (msgs, tasks) = generateTasks(SalesCall, false)
        flash.message = "Generated Tasks ${msgs.join(',')}"
        renderOnMap(tasks,SalesCall)

    }

    private def renderOnMap(List<Task> tasks,Class taskType) {

        def roleNeeded = taskType == DetailerTask ? Role.DETAILER_ROLE_NAME : Role.SALES_ROLE_NAME
        def dtos = tasks.collect { ControllerUtils.taskToTaskDTO(it) }

        time("Loading Territory Users") {
            txHelperService.doInTransaction {
                //todo generally controllers should not access repositories
                def _getTerritoryUsers = { Long tid -> bean(TaskRepository).getTerritoryUsers(tid, roleNeeded) }.memoize()
                dtos.each { it.assignedUser = _getTerritoryUsers.call(it.subCountyId) }
            }
        }

        def taskData  = dtos.collect {ControllerUtils.taskToJsonMap(it)}

        def mapData = taskData as JSON
        def jsonMapString = mapData.toString(true)

        //for pagination
        params.max = Integer.MAX_VALUE
        render(view: '/task/map', model: [taskInstanceList : tasks,
                                          taskInstanceCount: tasks.size(),
                                          users            : [],
                                          mapData          : jsonMapString,
                                          no_mapsubmenu    : true,
                                          no_pagination    : true])
    }

    def handleException(IllegalArgumentException ex) {
        flash.error = ex.message
        if (actionName == 'generateDetailerTasks')
            redirect action: 'generationDetailer', params: params
        else
            redirect action: 'generationOrder', params: params
        log.error('Error while generating tasks', ex)
    }

    private def getPageModel() {
        def user = neoSecurityService.currentUser
        def territories = regionService.findTerritoriesForUser(user, [max: 2000]).collect().sort { it.name }
        def segments = customerService.listAllCustomerSegments()
        [territories, segments]
    }

    private def generateTasks(Class taskType, boolean cluster) {

        println(params)
        def workDays = params.workDays instanceof String ? [params.workDays] : params.workDays
        workDays = workDays.collect { it as Integer }
        Assert.notNull workDays, 'Please Set Work Days'
        Assert.notEmpty workDays, 'Please Set Work Days'

        Assert.notNull params.startDate, 'Please Set A Start Date'
        def startDate = Date.parse('yyyy-MM-dd', params.startDate as String)
        Assert.isTrue((startDate - new Date()) >= 0, "Start Date[$params.startDate] Should Be Greater Than Today")

        Assert.notNull params.avgTasksPerDay, 'Please Set Avg Number Of Tasks Per Day'
        def tasksPerDay = params.avgTasksPerDay as Integer
        Assert.isTrue tasksPerDay <= 30, 'Average Number Of Tasks Is To High(above 30)'

        def segments = extractSegments()

        def territories = extractTerritories()

        time("Genearting Task for $territories") {
            taskService.generateTasks(territories, segments, startDate, workDays, tasksPerDay, taskType, cluster)
        }
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

        segments.each { String dbId, v ->
            if (v && !(params["segments.${dbId}_all"])) {
                def endsWithAll = dbId.endsWith('all')
                if (endsWithAll) {
                    dbId = dbId.split(/_/)[0]
                }
                def cs = customerService.findCustomerSegment(dbId as Long)
                cs.numberOfTasks = endsWithAll ? 2000 : v as Integer

                if (cs.numberOfTasks > 0)
                    neoSegments << cs
            }
        }

        return neoSegments
    }
}
