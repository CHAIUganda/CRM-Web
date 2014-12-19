package com.omnitech.chai.rest

import com.omnitech.chai.model.DetailerTask
import com.omnitech.chai.model.Task
import com.omnitech.chai.model.User
import com.omnitech.chai.util.ChaiUtils
import com.omnitech.chai.util.ModelFunctions
import com.omnitech.chai.util.ReflectFunctions
import grails.converters.JSON
import org.springframework.http.HttpStatus

import static com.omnitech.chai.model.Role.getDETAILER_ROLE_NAME
import static com.omnitech.chai.model.Role.getSALES_ROLE_NAME

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
        def tasks = taskService.findAllTaskForUser(user.id, Task.STATUS_NEW, params,Task)
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
        if (user.hasRole(DETAILER_ROLE_NAME)) {
            doDetailerUpdate(user)
        } else if (user.hasRole(SALES_ROLE_NAME)) {
            doOrderUpdate(user)
        } else {
            renderError("You Do No Have Appropriate Roles To Perform This Task",HttpStatus.FORBIDDEN)
        }
    }

    private void doDetailerUpdate(User user) {
        def json = request.JSON as Map
        def detailerInfo = (json.get('detailers') as List)?.get(0) as Map
        println(json.inspect())
        def task = ModelFunctions.createObj(DetailerTask, json)
        if (detailerInfo) {
            detailerInfo.remove('id')
            ModelFunctions.bind(task, detailerInfo)
        }
        task.lng = ChaiUtils.execSilently('Converting long to float') { detailerInfo['longitude'] as Float }
        task.lat = ChaiUtils.execSilently('Converting lat to float') { detailerInfo['latitude'] as Float }
        task.uuid = json.uuid
        if (!task.uuid) {
            response.status = HttpStatus.BAD_REQUEST.value()
            render { [status: HttpStatus.BAD_REQUEST.reasonPhrase, message: "You Did Not Provide The Task ID"] }
            return
        }
        taskService.completeDetailTask(task)
        log.debug("Resp:${user}   - OK")
        render([status: HttpStatus.OK.reasonPhrase, message: 'Success'] as JSON)
    }

    private void doOrderUpdate(User user) {

    }

    private def renderError(String error, HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR) {
        response.status = status.value()
        render([status: 'error', message: error] as JSON)
    }
}
