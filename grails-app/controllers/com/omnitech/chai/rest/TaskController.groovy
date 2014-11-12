package com.omnitech.chai.rest

import com.omnitech.chai.model.DetailerTask
import com.omnitech.chai.model.Task
import com.omnitech.chai.model.User
import com.omnitech.chai.util.ModelFunctions
import com.omnitech.chai.util.ReflectFunctions
import grails.converters.JSON
import org.springframework.http.HttpStatus

/**
 * Created by kay on 10/29/14.
 */
class TaskController {

    static namespace = 'rest'
    static responseFormats = ['json', 'xml']
    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]


    def taskService
    def neoSecurityService

    def list(Integer max) {
        params.max = Math.min(max ?: 10, 2000)
        def user = neoSecurityService.currentUser as User
        log.debug("Req:${user}  - TaskList: $params")
        def tasks = taskService.findAllTaskForUser(user.id,Task.STATUS_COMPLETE,params)
        def taskMaps = tasks.collect {
            def map = ReflectFunctions.extractProperties(it)
            map['customerId'] = it.customer.id
            return map
        }
        log.debug("Resp:${user} - ${taskMaps?.size()} Tasks...")
        respond taskMaps
    }

    def update() {
        def user = neoSecurityService.currentUser
        log.debug("Req:${user}   - Update Task")
        def json = request.JSON as Map
        def detailerInfo = (json.get('detailers') as List)?.get(0) as Map
        println(json.inspect())
        def task = ModelFunctions.createObj(DetailerTask, json)
        if (detailerInfo) {
            detailerInfo.remove('id')
            ModelFunctions.bind(task, detailerInfo)
        }
        taskService.completeDetailTask(task)
        log.debug("Resp:${user}   - OK")
        render([status: HttpStatus.OK.reasonPhrase, message: 'Success'] as JSON)
    }

}
