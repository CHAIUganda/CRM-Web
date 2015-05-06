package com.omnitech.chai.rest

import com.omnitech.chai.model.*
import com.omnitech.chai.util.ChaiUtils
import com.omnitech.chai.util.ModelFunctions
import com.omnitech.chai.util.ReflectFunctions
import grails.converters.JSON

import org.springframework.http.HttpStatus
import org.springframework.util.Assert

import static com.omnitech.chai.model.Role.DETAILER_ROLE_NAME
import static com.omnitech.chai.model.Role.SALES_ROLE_NAME
import static org.springframework.http.HttpStatus.BAD_REQUEST

/**
 * Created by kay on 10/29/14.
 */
class TaskController extends BaseRestController {

    static namespace = 'rest'
    static responseFormats = ['json', 'xml']
    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]


    def taskService

    def list(Integer max) {
        params.max = Math.min(max ?: 10, 2000)
        def user = neoSecurityService.currentUser as User
        log.debug("Req:${user}  - TaskList: $params")
        Class<Task> taskType
        if (user.hasRole(DETAILER_ROLE_NAME))
            taskType = DetailerTask
        else if (user.hasRole(SALES_ROLE_NAME))
            taskType = SalesCall
        else {
            log.warn("user: [$user] has no detailer or sales role.... sending empty list")
            respond([])
            return
        }

        def tasks = taskService.findAllTasksForUser(user.id, Task.STATUS_NEW, params, taskType, null).content
        def taskMaps = tasks.collect {
            def map = ReflectFunctions.extractProperties(it)
            map['customerId'] = it.customer.uuid
            if (Order.isAssignableFrom(it.getClass())) {
                map['lineItems'] = ((Order) it).lineItems.collect {
                    ['productId': it.product.uuid,
                     'quantity' : it.quantity,
                     'unitPrice': it.unitPrice
                    ]

                }
            }
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
            renderError("You Do No Have Appropriate Roles To Perform This Task", HttpStatus.FORBIDDEN)
        }
    }

    def test(){
        
    }

    def malariaUpdate(){
        def user = neoSecurityService.currentUser
        log.debug("Req:${user}   - Update Task")
        if (user.hasRole(DETAILER_ROLE_NAME)) {
            doMalariaUpdate(user)
        } else {
            renderError("You Do No Have Appropriate Roles To Perform This Task", HttpStatus.FORBIDDEN)
        }
    }

    private void doMalariaUpdate(User user) {
        def json = request.JSON as Map
        //todo fixme
        json.clientRefId = json.uuid

        println(request.JSON.toString())
        def task = ModelFunctions.createObj(MalariaDetails, json)


        if (task.isCancelled()) {
            task.uuid = json.uuid
            taskService.completeMalariaTask(task,json.customerId)
            log.debug("Resp:${user}   - OK")
            render([status: HttpStatus.OK.reasonPhrase, message: 'Success'] as JSON)
            return
        }

        Assert.notEmpty((json.get('detailers') as List),"Detailing Task Should Have Detailing Info")

        def detailerInfo = (json.get('detailers') as List)?.get(0) as Map
        if (detailerInfo) {
            detailerInfo.remove('id')
            ModelFunctions.bind(task, detailerInfo)
        }
        task.lng = ChaiUtils.execSilently('Converting long to float') { detailerInfo['longitude'] as Float }
        task.lat = ChaiUtils.execSilently('Converting lat to float') { detailerInfo['latitude'] as Float }
        task.uuid = json.uuid

        if(detailerInfo.dateOfSurvey){
            ChaiUtils.execSilently {task.completionDate = new Date(detailerInfo.dateOfSurvey as Long)}
        }

        if (!task.uuid) {
            response.status = HttpStatus.BAD_REQUEST.value()
            render { [status: HttpStatus.BAD_REQUEST.reasonPhrase, message: "You Did Not Provide The Task ID"] }
            return
        }
        if (task.isAdhock) {
            assert json.customerId, 'Please make sure you specify your customerId'
            updateCompletionInfo(task)
            taskService.completeAdhocMalariaTask(task, json.customerId)
        } else {
            taskService.completeAdhocMalariaTask(task, json.customerId)
        }
        log.debug("Resp:${user}   - OK")
        render([status: HttpStatus.OK.reasonPhrase, message: 'Success'] as JSON)
    }

    private void doDetailerUpdate(User user) {
        def json = request.JSON as Map
        //todo fixme
        json.clientRefId = json.uuid

        println(request.JSON.toString())
        def task = ModelFunctions.createObj(DetailerTask, json)

        if (task.isCancelled()) {
            task.uuid = json.uuid
            taskService.completeDetailTask(task,json.customerId)
            log.debug("Resp:${user}   - OK")
            render([status: HttpStatus.OK.reasonPhrase, message: 'Success'] as JSON)
            return
        }

        Assert.notEmpty((json.get('detailers') as List),"Detailing Task Should Have Detailing Info")

        def detailerInfo = (json.get('detailers') as List)?.get(0) as Map
        if (detailerInfo) {
            detailerInfo.remove('id')
            ModelFunctions.bind(task, detailerInfo)
        }
        task.lng = ChaiUtils.execSilently('Converting long to float') { detailerInfo['longitude'] as Float }
        task.lat = ChaiUtils.execSilently('Converting lat to float') { detailerInfo['latitude'] as Float }
        task.uuid = json.uuid

        if(detailerInfo.dateOfSurvey){
            ChaiUtils.execSilently {task.completionDate = new Date(detailerInfo.dateOfSurvey as Long)}
        }

        if (!task.uuid) {
            response.status = HttpStatus.BAD_REQUEST.value()
            render { [status: HttpStatus.BAD_REQUEST.reasonPhrase, message: "You Did Not Provide The Task ID"] }
            return
        }
        if (task.isAdhock) {
            assert json.customerId, 'Please make sure you specify your customerId'
            updateCompletionInfo(task)
            taskService.completeAdhocDetailTask(task, json.customerId)
        } else {
            taskService.completeDetailTask(task, json.customerId)
        }
        log.debug("Resp:${user}   - OK")
        render([status: HttpStatus.OK.reasonPhrase, message: 'Success'] as JSON)
    }

    private void doOrderUpdate(User user) {
        def json = request.JSON as Map
        Assert.notNull json.uuid, 'Please set the order uuid'
        def uuid = json.uuid as String
        def task = taskService.findTask(uuid)

        if (!task) {
            render([status: HttpStatus.OK.reasonPhrase, message: 'Success'] as JSON)
            return
        }

        if (json.status == Task.STATUS_CANCELLED) {
            if(json.description){
                task.description = json.description
            }
            task.cancelledBy(user)
            taskService.saveTask(task)
        }

        render([status: HttpStatus.OK.reasonPhrase, message: 'Success'] as JSON)
    }

    private def renderError(String error, HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR) {
        response.status = status.value()
        render([status: 'error', message: error] as JSON)
    }

    def handleException(Exception x) {
        log.error("Error while handling request: \n $params", x)
        render(status: BAD_REQUEST, text: [status: BAD_REQUEST.reasonPhrase, message: ChaiUtils.getBestMessage(x)] as JSON)
    }
}
