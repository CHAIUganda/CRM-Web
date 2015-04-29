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

    @Deprecated
    static Map taskToJsonMap(Task task) {
        def map = ReflectFunctions.extractProperties(task)
        if (!(map.lat && map.lng)) {
            map.lat = task.customer.lat
            map.lng = task.customer.lng
        }
        map.description = "$task.description - (${ChaiUtils.fromNow(task.dueDate)})"
        if (task.dueDate) {
            //used in the js to get the icon color
            map.dueDays = ChaiUtils.dayDiffFomNow(task.dueDate)
            map.dueDateText = ChaiUtils.formatDate(task.dueDate)
        }

        if (task.customer?.segment)
            map.segment = task.customer.segment.name

        if (task.customer) {
            map.customer = task.customer.outletName
            map.customerDescription = task.customer.descriptionOfOutletLocation
        }

        map.assignedUser = task.territoryUser()?.findResults { it }?.collect { it.username }?.toString();
        map.type = 'task'

        return map
    }

    static TaskDTO taskToTaskDTO(Task task) {

        TaskDTO dto = ModelFunctions.bind(new TaskDTO(),task.properties,true)
        dto.subCountyId = task.customer?.subCounty?.id
        dto.customer = task.customer.name

        if (!(dto.lat && dto.lng)) {
            dto.lat = task.customer.lat
            dto.lng = task.customer.lng
        }
        dto.description = "$task.description - (${ChaiUtils.fromNow(task.dueDate)})"

        if (task.customer?.segment)
            dto.segment = task.customer.segment.name

        if (task.customer) {
            dto.customer = task.customer.outletName
            dto.customerDescription = task.customer.descriptionOfOutletLocation
        }
        return dto
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
