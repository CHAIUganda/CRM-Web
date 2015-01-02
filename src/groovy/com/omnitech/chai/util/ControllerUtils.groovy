package com.omnitech.chai.util

import com.omnitech.chai.model.Task
import com.omnitech.chai.util.ChaiUtils
import com.omnitech.chai.util.ReflectFunctions

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
            map.dueDays = task.dueDate - new Date()
            map.dueDateText = ChaiUtils.formatDate(task.dueDate)
        }

        if (task.customer?.segment)
            map.segment = task.customer.segment.name

        if (task.customer) {
            map.customer = task.customer.outletName
            map.customerDescription = task.customer.descriptionOfOutletLocation
        }

        map.assignedUser = task.territoryUser()?.collect { it.username }?.toString();

        return map
    }
}
