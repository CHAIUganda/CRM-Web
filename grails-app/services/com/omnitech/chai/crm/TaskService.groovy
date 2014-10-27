package com.omnitech.chai.crm

import com.omnitech.chai.model.Customer
import com.omnitech.chai.model.Task
import com.omnitech.chai.util.ModelFunctions
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.neo4j.support.Neo4jTemplate
import org.springframework.data.neo4j.transaction.Neo4jTransactional

@Neo4jTransactional
class TaskService {

    def taskRepository
    @Autowired
    Neo4jTemplate neo
    def customerRepository

    /* Tasks */

    List<Task> listAllTasks() { taskRepository.findAll().collect() }

    Page<Task> listTasks(Map params) { ModelFunctions.listAll(taskRepository, params) }

    Task findTask(Long id) { taskRepository.findOne(id) }

    Task saveTask(Task task) { ModelFunctions.saveEntity(taskRepository, task) }

    void deleteTask(Long id) { taskRepository.delete(id) }

    Page<Task> searchTasks(String search, Map params) {
        ModelFunctions.searchAll(neo, Task, ModelFunctions.getWildCardRegex(search), params)
    }

    def autoGenerateTasks() {
        customerRepository.findAll().each {
            def task = generateCustomerTask(it)
            if (task) taskRepository.save(task)
        }
    }

    private Task generateCustomerTask(Customer customer) {
        def segment = customer.segment
        if(!segment) return null

        def prevTask = taskRepository.findLastTask(customer.id)

        def newTask = new Task(customer: customer,description: "Go Check on [$customer.outletName]",dueDate: new Date())
        if(prevTask?.completionDate)  {
           //if we have a previous task set the date *n* days after previous task
            newTask.dueDate = prevTask.completionDate + segment.spaceBetweenVisits
        }

       return newTask

    }



    Date getDateSinceLastVisit(Customer c) {

    }
}