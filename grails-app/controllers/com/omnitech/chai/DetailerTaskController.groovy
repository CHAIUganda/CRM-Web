package com.omnitech.chai

import com.omnitech.chai.model.Customer
import com.omnitech.chai.model.DetailerTask
import com.omnitech.chai.util.ModelFunctions

import static com.omnitech.chai.model.Role.DETAILER_ROLE_NAME
import static com.omnitech.chai.model.Role.SALES_ROLE_NAME

/**
 * DetailerTaskController
 * A controller class handles incoming web requests and performs actions such as redirects, rendering views and so on.
 */
class DetailerTaskController extends TaskController {

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]


    def index(Integer max) {
        super.index max, DetailerTask, [view: '/task/index', taskRole: DETAILER_ROLE_NAME]
    }

    def map(Integer max) {
        super.map max, DetailerTask, [view: '/task/map']
    }

    def export() {
        super.export DetailerTask
    }

    def search(Integer max) {
        super.search max, DetailerTask, [view: '/task/index', taskRole: SALES_ROLE_NAME]
    }

    def searchMap(Integer max) {
        super.searchMap max, DetailerTask, [view: '/task/map', taskRole: SALES_ROLE_NAME]
    }

    def show() {
        super.show view: '/task/show', taskRole: SALES_ROLE_NAME
    }

    def create() {
        render view: '/task/create', model: [taskInstance: ModelFunctions.createObj(DetailerTask, params), customers: customerService.listAllCustomers()]
    }

    def save(DetailerTask taskInstance) {
        super.save taskInstance
    }

    def createTaskJson() {
        super.createTaskJson { Customer c, Date dueDate -> return DetailerTask.create(c, dueDate) }
    }

    def edit() {
        super.edit view: '/task/create'
    }

    def deleteAll() {
        super.deleteAll()
    }

}
