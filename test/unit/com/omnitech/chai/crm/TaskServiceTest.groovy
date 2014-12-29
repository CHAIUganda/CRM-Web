package com.omnitech.chai.crm

import com.omnitech.chai.model.Customer
import com.omnitech.chai.model.CustomerSegment
import com.omnitech.chai.model.Task
import com.omnitech.chai.repositories.TaskRepository
import spock.lang.Specification

/**
 * Created by kay on 10/28/14.
 */
class TaskServiceTest extends Specification {


    TaskService service = new TaskService()

    void setup() {
        TaskRepository taskRepository = Mock()

    }

    def "if the previous task was completed so many days ago then the next task should be now"() {
        def c = new Customer(segment: new CustomerSegment(callFrequency: 3))
        TaskRepository repo = Mock(TaskRepository)
        service.taskRepository = repo

        when:
        def newTask = service.generateCustomerDetailingTask(c)

        then:
        1 * repo.findLastTask(_) >> new Task(completionDate: new Date() - 60)
        (newTask.dueDate - new Date()) == 0
    }

    def "if the previous task was completed recently then the next task can have a date that is based on the previous completion date"() {
        def c = new Customer(segment: new CustomerSegment(callFrequency: 3))
        TaskRepository repo = Mock(TaskRepository)
        service.taskRepository = repo

        when:
        def newTask = service.generateCustomerDetailingTask(c)

        then:
        1 * repo.findLastTask(_) >> new Task(completionDate: new Date() - 15)
        (newTask.dueDate - new Date()) == 5
    }
}
