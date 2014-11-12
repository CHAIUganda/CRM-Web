package com.omnitech.chai.rest

import com.omnitech.chai.model.DetailerTask
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
        println(params)
        def user = neoSecurityService.currentUser as User
        def tasks = taskService.findAllTaskForUser(user.id)
        def taskMaps = tasks.collect {
            def map = ReflectFunctions.extractProperties(it)
            map['customerId'] = it.customer.id
            return map
        }

        respond taskMaps
    }

    def update() {
        def json = request.JSON as Map
        def detailerInfo = (json.get('detailers') as List)?.get(0) as Map
        println(json.inspect())
        def task = ModelFunctions.createObj(DetailerTask, json)
        if (detailerInfo) {
            detailerInfo.remove('id')
            ModelFunctions.bind(task, detailerInfo)
        }
        taskService.completeDetailTask(task)
        render([status: HttpStatus.OK.reasonPhrase, message: 'Success'] as JSON)
    }

}
