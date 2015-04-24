package com.omnitech.chai.util

import com.omnitech.chai.model.Customer
import com.omnitech.chai.model.Task
import com.omnitech.chai.repositories.dto.TaskDTO

/**
 * Created by kay on 12/28/2014.
 */
class ControllerUtils {


    static Map taskToJsonMap(TaskDTO task) {
        def map = task.properties
        map.description = "$task.description - (${ChaiUtils.fromNow(task.dueDate)})"
        if (task.dueDate) {
            //used in the js to get the icon color
            map.dueDays = ChaiUtils.dayDiffFomNow(task.dueDate)
            map.dueDateText = ChaiUtils.formatDate(task.dueDate)
        }
        map.assignedUser = task.assignedUser?.toString();
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
