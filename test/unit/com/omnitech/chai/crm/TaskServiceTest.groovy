package com.omnitech.chai.crm

import com.omnitech.chai.model.*
import com.omnitech.chai.repositories.CustomerRepository
import com.omnitech.chai.repositories.TaskRepository
import org.springframework.data.neo4j.support.Neo4jTemplate
import spock.lang.Specification

/**
 * Created by kay on 10/28/14.
 */
class TaskServiceTest extends Specification {


    TaskService service = new TaskService()

    void setup() {
        TaskRepository taskRepository = Mock()

    }

    def "if the previous task was completed so many days ago then the next task should be null"() {
        def c = new Customer(segment: new CustomerSegment(callFrequency: 3))
        TaskRepository repo = Mock(TaskRepository)
        service.taskRepository = repo

        when:
        def newTask = service.generateCustomerDetailingTask(c)

        then:
        1 * repo.findLastTask(_) >> new Task(completionDate: new Date() - 60)
        newTask == null
    }

    def "if the previous task was completed recently then the next task can have a date that is based on the previous completion date"() {
        def c = new Customer(segment: new CustomerSegment(callFrequency: 3))
        TaskRepository repo = Mock(TaskRepository)
        service.taskRepository = repo

        when:
        def newTask = service.generateCustomerDetailingTask(c)

        then:
        1 * repo.findLastTask(_) >> new Task(completionDate: new Date() - 15,status: Task.STATUS_COMPLETE)
        (newTask.dueDate - new Date()) == 5
    }

    def "test that a complete task is marked as complete"() {
        def taskRepo = service.taskRepository = Mock(TaskRepository)
        def neo = service.neo = Mock(Neo4jTemplate)
        def sec = service.neoSecurityService = Mock(NeoSecurityService)
        def mobileTask = new DetailerTask(status: 'new', uuid: 'myuuid', description: 'my description',
                lat: 1f,
                lng: 30f,
                detailerStocks: [
                        new DetailerStock(brand: 'brand', stockLevel: 64)
                ])
        def neoTask = new DetailerTask(status: 'new', uuid: 'myuuid', description: 'my description')
        def user = new User()

        when:
        service.completeDetailTask(mobileTask, 'customer')

        then:
        1 * taskRepo.findByUuid('myuuid') >> neoTask
        1 * neo.projectTo(neoTask, DetailerTask) >> neoTask
        1 * neo.save(neoTask) >> neoTask
        1 * sec.currentUser >> user

        assert neoTask.isComplete()
        assert !neoTask.isAdhock
        assert neoTask.description == 'my description'
        assert neoTask.lat == 1f
        assert neoTask.lng == 30f
        assert neoTask.detailerStocks.size() >= 1
        assert neoTask.completedBy == user
        assert neoTask.cancelledBy == null
    }

    def "test that a canceled task is cancelled"() {
        def taskRepo = service.taskRepository = Mock(TaskRepository)
        def neo = service.neo = Mock(Neo4jTemplate)
        def sec = service.neoSecurityService = Mock(NeoSecurityService)
        def mobileTask = new DetailerTask(status: Task.STATUS_CANCELLED, uuid: 'myuuid', description: 'Could not Find Customer',
                lat: 1f,
                lng: 30f,
                detailerStocks: [
                        new DetailerStock(brand: 'brand', stockLevel: 64)
                ])
        def neoTask = new DetailerTask(status: 'new', uuid: 'myuuid', description: 'my description')
        def user = new User()

        when:
        service.completeDetailTask(mobileTask, 'customer')

        then:
        1 * taskRepo.findByUuid('myuuid') >> neoTask
        1 * neo.projectTo(neoTask, DetailerTask) >> neoTask
        1 * neo.save(neoTask) >> neoTask
        1 * sec.currentUser >> user

        assert neoTask.isCancelled()
        assert !neoTask.isAdhock
        assert neoTask.description == 'Could not Find Customer'
        assert neoTask.lat == 1f
        assert neoTask.lng == 30f
        assert neoTask.detailerStocks.size() >= 1
        assert neoTask.completedBy == null
        assert neoTask.cancelledBy == user
    }

    def "test that an ad hoc task is uploaded"() {
        def taskRepo = service.taskRepository = Mock(TaskRepository)
        def neo = service.neo = Mock(Neo4jTemplate)
        def sec = service.neoSecurityService = Mock(NeoSecurityService)
        def customerRepo = service.customerRepository = Mock(CustomerRepository)
        def mobileTask = new DetailerTask(status: Task.STATUS_COMPLETE, uuid: 'myuuid', description: 'my description',
                lat: 1f,
                lng: 30f,
                clientRefId: 'clientRefId',
                detailerStocks: [
                        new DetailerStock(brand: 'brand', stockLevel: 64)
                ])
        def user = new User()
        def customer = new Customer(outletName: 'Cust')

        when:
        service.completeAdhocDetailTask(mobileTask, 'customer')

        then:
        1 * taskRepo.findByClientRefId('clientRefId') >> null
        1 * customerRepo.findByUuid('customer') >> customer
        1 * neo.save(mobileTask) >> mobileTask
        1 * sec.currentUser >> user


        assert mobileTask.isComplete()
        assert mobileTask.isAdhock
        assert mobileTask.customer == customer
        assert mobileTask.description == 'Ad hoc Detailing[Cust]'
        assert mobileTask.lat == 1f
        assert mobileTask.lng == 30f
        assert mobileTask.detailerStocks.size() >= 1
        assert mobileTask.leaveUuidIntact
        assert mobileTask.completedBy == user
        assert mobileTask.cancelledBy == null
    }

    def "test duplicate ad hoc detailing is rejected"() {
        def taskRepo = service.taskRepository = Mock(TaskRepository)
        def neo = service.neo = Mock(Neo4jTemplate)
        def sec = service.neoSecurityService = Mock(NeoSecurityService)
        def customerRepo = service.customerRepository = Mock(CustomerRepository)
        def mobileTask = new DetailerTask(status: Task.STATUS_COMPLETE, uuid: 'myuuid', description: 'my description',
                lat: 1f,
                lng: 30f,
                clientRefId: 'clientRefId',
                detailerStocks: [
                        new DetailerStock(brand: 'brand', stockLevel: 64)
                ])

        when:
        service.completeAdhocDetailTask(mobileTask, 'customer')

        then:
        1 * taskRepo.findByClientRefId('clientRefId') >> new DetailerTask()
        0 * customerRepo.findByUuid('customer')
        0 * neo.save(mobileTask)
        0 * sec.currentUser

        thrown(IllegalArgumentException)
    }

    def "test absent customer ad hoc task is ignored"() {
        def taskRepo = service.taskRepository = Mock(TaskRepository)
        def neo = service.neo = Mock(Neo4jTemplate)
        def sec = service.neoSecurityService = Mock(NeoSecurityService)
        def customerRepo = service.customerRepository = Mock(CustomerRepository)
        def mobileTask = new DetailerTask(status: Task.STATUS_COMPLETE, uuid: 'myuuid', description: 'my description',
                lat: 1f,
                lng: 30f,
                clientRefId: 'clientRefId',
                detailerStocks: [
                        new DetailerStock(brand: 'brand', stockLevel: 64)
                ])
        def user = new User()

        when:
        service.completeAdhocDetailTask(mobileTask, 'customer')

        then:
        1 * taskRepo.findByClientRefId('clientRefId') >> null
        1 * customerRepo.findByUuid('customer') >> null
        0 * neo.save(mobileTask) >> mobileTask
        0 * sec.currentUser >> user

    }

    def "test cancelled adhoc detailing is ignored"() {
        def taskRepo = service.taskRepository = Mock(TaskRepository)
        def neo = service.neo = Mock(Neo4jTemplate)
        def sec = service.neoSecurityService = Mock(NeoSecurityService)
        def customerRepo = service.customerRepository = Mock(CustomerRepository)
        def mobileTask = new DetailerTask(status: Task.STATUS_CANCELLED, uuid: 'myuuid', description: 'my description',
                lat: 1f,
                lng: 30f,
                clientRefId: 'clientRefId',
                detailerStocks: [
                        new DetailerStock(brand: 'brand', stockLevel: 64)
                ])

        when:
        service.completeAdhocDetailTask(mobileTask, 'customer')

        then:
        0 * taskRepo.findByClientRefId('clientRefId') >> null
        0 * customerRepo.findByUuid('customer') >> null
        0 * neo.save(mobileTask)
        0 * sec.currentUser
    }

    def "test deleted task is converted to ad hoc"() {

        def taskRepo = service.taskRepository = Mock(TaskRepository)
        def neo = service.neo = Mock(Neo4jTemplate)
        def sec = service.neoSecurityService = Mock(NeoSecurityService)
        def customerRepo = service.customerRepository = Mock(CustomerRepository)
        def mobileTask = new DetailerTask(status: Task.STATUS_COMPLETE, uuid: 'myuuid', description: 'my description',
                lat: 1f,
                lng: 30f,
                clientRefId: 'clientRefId',
                detailerStocks: [
                        new DetailerStock(brand: 'brand', stockLevel: 64)
                ])
        def user = new User()
        def customer = new Customer(outletName: 'Cust')

        when:
        service.completeDetailTask(mobileTask, 'customer')

        then:
        1 * taskRepo.findByClientRefId('myuuid') >> null
        1 * taskRepo.findByUuid('myuuid') >> null
        1 * customerRepo.findByUuid('customer') >> customer
        1 * neo.save(mobileTask) >> mobileTask
        1 * sec.currentUser >> user


        assert mobileTask.isComplete()
        assert mobileTask.isAdhock
        assert mobileTask.clientRefId == 'myuuid'
        assert mobileTask.customer == customer
        assert mobileTask.description == 'Ad hoc Detailing[Cust]'
        assert mobileTask.lat == 1f
        assert mobileTask.lng == 30f
        assert mobileTask.detailerStocks.size() >= 1
        assert mobileTask.leaveUuidIntact
        assert mobileTask.completedBy == user
        assert mobileTask.cancelledBy == null

    }

    def "test that absent client ref id is rejected"() {
        def mobileTask = new DetailerTask(status: Task.STATUS_COMPLETE, uuid: 'myuuid', description: 'my description')
        when:
        service.assertNotDuplicate(mobileTask)

        then:
        thrown(IllegalArgumentException)
    }

    def "test that an ad hoc new task is still saved as complete"() {

        def taskRepo = service.taskRepository = Mock(TaskRepository)
        def neo = service.neo = Mock(Neo4jTemplate)
        def sec = service.neoSecurityService = Mock(NeoSecurityService)
        def customerRepo = service.customerRepository = Mock(CustomerRepository)
        def mobileTask = new DetailerTask(status: Task.STATUS_NEW, uuid: 'myuuid', description: 'my description',
                lat: 1f,
                lng: 30f,
                clientRefId: 'clientRefId',
                detailerStocks: [
                        new DetailerStock(brand: 'brand', stockLevel: 64)
                ])
        def user = new User()
        def customer = new Customer(outletName: 'Cust')

        when:
        service.completeAdhocDetailTask(mobileTask, 'customer')

        then:
        1 * taskRepo.findByClientRefId('clientRefId') >> null
        1 * customerRepo.findByUuid('customer') >> customer
        1 * neo.save(mobileTask) >> mobileTask
        1 * sec.currentUser >> user


        assert mobileTask.isComplete()
        assert mobileTask.isAdhock
        assert mobileTask.customer == customer
        assert mobileTask.description == 'Ad hoc Detailing[Cust]'
        assert mobileTask.lat == 1f
        assert mobileTask.lng == 30f
        assert mobileTask.detailerStocks.size() >= 1
        assert mobileTask.leaveUuidIntact
        assert mobileTask.completedBy == user
        assert mobileTask.cancelledBy == null

    }
}
