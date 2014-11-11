package com.omnitech.chai.crm

import com.omnitech.chai.model.Customer
import com.omnitech.chai.model.DetailerTask
import com.omnitech.chai.model.Task
import com.omnitech.chai.util.ModelFunctions
import com.omnitech.chai.util.ReflectFunctions
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.neo4j.support.Neo4jTemplate
import org.springframework.data.neo4j.transaction.Neo4jTransactional

@Neo4jTransactional
class TaskService {

    def taskRepository
    def userRepository
    @Autowired
    Neo4jTemplate neo
    def customerRepository
    def detailerTaskRepository
    def neoSecurityService

    /* Tasks */

    List<Task> listAllTasks() { taskRepository.findAll().collect() }

    Page<Task> listTasks(Map params) { ModelFunctions.listAll(taskRepository, params) }

    Task findTask(Long id) { taskRepository.findOne(id) }

    def <T extends Task> T saveTask(T task) { ModelFunctions.saveGenericEntity(neo, task) }

    void deleteTask(Long id) { taskRepository.delete(id) }

    Page<Task> searchTasks(String search, Map params) {
        ModelFunctions.searchAll(neo, Task, ModelFunctions.getWildCardRegex(search), params)
    }

    /* Detailer Tasks*/
    DetailerTask findDetailerTask(Long id) { neo.findOne(id,DetailerTask) }

    List<Task> findAllTaskForUser(Long userId) {
        taskRepository.findAllTaskForUser(userId).collect()
    }

    def autoGenerateTasks() {
        customerRepository.findAll().each {
            def task = generateCustomerTask(it)
            if (task) taskRepository.save(task)
        }
    }

    Task generateCustomerTask(Customer customer) {
        def segment = customer.segment
        if (!segment) return null

        def prevTask = taskRepository.findLastTask(customer.id)

        def spaceBtnVisits = segment.spaceBetweenVisits
        def newTask = new Task(customer: customer, description: "Go Check on [$customer.outletName]", dueDate: new Date())
        if (prevTask?.completionDate) {
            //if we have a previous task set the date *n* days after previous task
            def daysBtnDates = new Date() - prevTask.completionDate
            if (daysBtnDates < spaceBtnVisits)
                newTask.dueDate = prevTask.completionDate + segment.spaceBetweenVisits
        }

        return newTask

    }

    /**
     * This updates a given task and if its a basic task it projects it to the required Type.
     * This method also makes sure that other relationships are not modified
     */
    DetailerTask completeDetailTask(DetailerTask detailerTask) {
        def neoTask = taskRepository.findOne(detailerTask.id)

        //task could have been deleted
        if (!neoTask) return null

        neoTask = neo.projectTo(neoTask, DetailerTask)

        ModelFunctions.bind(neoTask, detailerTask.properties, ReflectFunctions.findAllBasicFields(DetailerTask))
        neoTask.completedBy(neoSecurityService.currentUser)

        saveTask(neoTask)
    }

}