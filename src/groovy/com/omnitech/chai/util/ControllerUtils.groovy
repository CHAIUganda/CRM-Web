package com.omnitech.chai.util

import com.omnitech.chai.model.Customer
import com.omnitech.chai.model.Task

/**
 * Created by kay on 12/28/2014.
 */
class ControllerUtils {


    static Map taskToJsonMap(Task task) {
        def map = ReflectFunctions.extractProperties(task)
        if (!(map.lat && map.lng)) {
            map.lat = task.customer.lat
            map.lng = task.customer.lng
        }
        map.description = "$task.description - (${ChaiUtils.fromNow(task.dueDate)})"
        if (task.dueDate) {
            map.dueDays = task.dueDate - new Date() - 1
            map.dueDateText = ChaiUtils.formatDate(task.dueDate)
        }

        if (task.customer?.segment)
            map.segment = task.customer.segment.name

        if (task.customer) {
            map.customer = task.customer.outletName
            map.customerDescription = task.customer.descriptionOfOutletLocation
        }

        map.assignedUser = task.territoryUser()?.findResults {it}?.collect { it.username }?.toString();
        map.type = 'task'

        return map
    }

    static Map customerToJsonMap(Customer customer) {
        def map = ReflectFunctions.extractProperties(customer)
        map.description = customer.outletName
        map.type = 'customer'
        map.segment = customer.segment?.name
        map.dueDays = 100
        return map

    }
}
