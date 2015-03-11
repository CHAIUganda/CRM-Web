package com.omnitech.chai.rest

import com.omnitech.chai.crm.NeoSecurityService
import com.omnitech.chai.crm.TaskService
import com.omnitech.chai.model.DetailerTask
import com.omnitech.chai.model.Role
import com.omnitech.chai.model.Task
import com.omnitech.chai.model.User
import grails.test.mixin.TestFor
import spock.lang.Specification

import static com.omnitech.chai.model.Role.DETAILER_ROLE_NAME

/**
 * Created by kay on 3/5/2015.
 */
@TestFor(TaskController)
class TaskControllerTest extends Specification {

    User user = new User(username: 'testing', roles: [

    ])
    NeoSecurityService sec
    TaskService taskService


    def setup() {
        sec = controller.neoSecurityService = Mock(NeoSecurityService)
        taskService = controller.taskService = Mock(TaskService)
        user = new User(username: 'testing', roles: [
                new Role(authority: DETAILER_ROLE_NAME)
        ])
    }

    def "test cancellation of a det task"() {

        def json = '''{"isDirty":null,"status":"cancelled","isAdhock":null,"lastUpdated":1425301071000,"type":"DetailerTask","customerId":"f3997b14-6fb9-4b88-a30b-ef8156b0b98f","dateScheduled":null,"description":"Detailing [Good Care Drug Shop](hjjh)","priority":null,"dateCreated":1425301071000,"uuid":"5f5b5abb-8d0f-4280-8c34-35c600efff1a","dueDate":1425243600000,"clientRefId":"5f5b5abb-8d0f-4280-8c34-35c600efff1a","completionDate":null}'''

        when:
        request.json = json
        controller.update()

        then:
        sec.currentUser >> user
        1 * taskService.completeDetailTask({ DetailerTask task ->
            assert !task.detailerStocks
            assert task.id == null
            task.status == Task.STATUS_CANCELLED && task.uuid == '5f5b5abb-8d0f-4280-8c34-35c600efff1a'
        }, "f3997b14-6fb9-4b88-a30b-ef8156b0b98f")

        response.contentAsString == '{"status":"OK","message":"Success"}'


    }

    def "test that complete detailer task is submitted"() {

        def dateOfSurvey = Date.parse("yyyy-MM-dd", '2015-02-1').time

        def json = """{"isDirty":null,
"status":"new",
"isAdhock":null,"lastUpdated":1425301071000,"type":"DetailerTask","customerId":"f3997b14-6fb9-4b88-a30b-ef8156b0b98f","dateScheduled":null,"description":"Detailing [Good Care Drug Shop](hjjh)","priority":null,"dateCreated":1425301071000,"uuid":"5f5b5abb-8d0f-4280-8c34-35c600efff1a","dueDate":1425243600000,
"clientRefId":"5f5b5abb-8d0f-4280-8c34-35c600efff1a",
"completionDate":null,
"longitude" : 31.1,
"latitude" : 1.3,
"detailers": [
{"heardAboutDiarrheaTreatmentInChildren" : "high points", "dateOfSurvey": $dateOfSurvey}
]
}""".toString()

        when:
        request.json = json
        controller.update()

        then:
        sec.currentUser >> user
        0 * taskService.completeAdhocDetailTask(_, _)
        1 * taskService.completeDetailTask({ DetailerTask task ->
            assert !task.detailerStocks
            assert task.id == null
            assert !task.isAdhock
            assert task.heardAboutDiarrheaTreatmentInChildren == 'high points'
            assert task.completionDate.format('yyy-MM-dd') == '2015-02-01'
            task.uuid == '5f5b5abb-8d0f-4280-8c34-35c600efff1a'
        }, "f3997b14-6fb9-4b88-a30b-ef8156b0b98f")

        response.contentAsString == '{"status":"OK","message":"Success"}'
    }

    def "test ad hoc detailing task"() {

        def dateOfSurvey = Date.parse("yyyy-MM-dd", '2015-02-1').time
        def json = """{"isDirty":null,
"status":"new",
"isAdhock":true,"lastUpdated":1425301071000,"type":"DetailerTask","customerId":"f3997b14-6fb9-4b88-a30b-ef8156b0b98f","dateScheduled":null,"description":"Detailing [Good Care Drug Shop](hjjh)","priority":null,"dateCreated":1425301071000,"uuid":"5f5b5abb-8d0f-4280-8c34-35c600efff1a","dueDate":1425243600000,"clientRefId":"5f5b5abb-8d0f-4280-8c34-35c600efff1a",
"completionDate":null,
"longitude" : 31.1,
"latitude" : 1.3,
"detailers": [
{"heardAboutDiarrheaTreatmentInChildren" : "high points","dateOfSurvey": $dateOfSurvey}
]
}""".toString()

        when:
        request.json = json
        controller.update()

        then:
        sec.currentUser >> user
        0 * taskService.completeDetailTask(_, _)
        1 * taskService.completeAdhocDetailTask({ DetailerTask task ->
            assert !task.detailerStocks
            assert task.id == null
            assert task.heardAboutDiarrheaTreatmentInChildren == 'high points'
            task.uuid == '5f5b5abb-8d0f-4280-8c34-35c600efff1a'
        }, "f3997b14-6fb9-4b88-a30b-ef8156b0b98f")

        response.contentAsString == '{"status":"OK","message":"Success"}'

    }

    def "test detailing task with no detailing info is rejected"() {
        def json = """{"isDirty":null,
"status":"new",
"isAdhock":true,"lastUpdated":1425301071000,"type":"DetailerTask","customerId":"f3997b14-6fb9-4b88-a30b-ef8156b0b98f","dateScheduled":null,"description":"Detailing [Good Care Drug Shop](hjjh)","priority":null,"dateCreated":1425301071000,"uuid":"5f5b5abb-8d0f-4280-8c34-35c600efff1a","dueDate":1425243600000,"clientRefId":"5f5b5abb-8d0f-4280-8c34-35c600efff1a",
"completionDate":null,
"longitude" : 31.1,
"latitude" : 1.3,
}""".toString()

        when:
        request.json = json
        controller.update()

        then:
        sec.currentUser >> user
        0 * taskService.completeDetailTask(_, _)
        0 * taskService.completeAdhocDetailTask(_, _)

        assert response.contentAsString.contains('{"status":"Bad Request"')

    }
}
