package com.omnitech.chai.rest

import com.omnitech.chai.model.DetailerTask
import com.omnitech.chai.model.User
import com.omnitech.chai.util.ModelFunctions
import com.omnitech.chai.util.ReflectFunctions
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
        params.max = Math.min(max ?: 10, 100)
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
        def task = ModelFunctions.createObj(DetailerTask, json)
        taskService.completeDetailTask(task)
        respond status: HttpStatus.OK
    }

}
