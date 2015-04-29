package com.omnitech.chai.repositories.dto

import com.omnitech.chai.model.Task
import com.omnitech.chai.util.ChaiUtils
import groovy.transform.CompileStatic
import org.springframework.data.neo4j.annotation.QueryResult
import org.springframework.data.neo4j.annotation.ResultColumn

/**
 * Created by kay on 4/23/2015.
 */
@CompileStatic
@QueryResult
class TaskDTO {
    @ResultColumn('id')
    Long id
    @ResultColumn('description')
    String description
    @ResultColumn('dueDate')
    Date dueDate
    @ResultColumn('completionDate')
    Date completionDate
    @ResultColumn('status')
    String status

    List<String> assignedUser
    @ResultColumn('district')
    String district
    @ResultColumn('dateCreated')
    Date dateCreated
    @ResultColumn('lat')
    Float lat
    @ResultColumn('lng')
    Float lng
    @ResultColumn('wkt')
    String wkt

    @ResultColumn('cLat')
    Float cLat
    @ResultColumn('cLng')
    Float cLng
    @ResultColumn('cWkt')
    String cWkt
    @ResultColumn('customer')
    String customer
    @ResultColumn('segment')
    String segment

    @ResultColumn('customerId')
    Long customerId
    @ResultColumn('customerDescription')
    String customerDescription



    @ResultColumn('subCountyId')
    Long subCountyId

    Float getLat() { lat ?: cLat }

    Float getLng() { lng ?: cLng }


    String getStatusMessage() {
        if (isComplete()) {
            return "Completed: ${ChaiUtils.fromNow(completionDate)}"
        }
        if (isCancelled()) {
            return "Canceled: ${ChaiUtils.fromNow(completionDate)}"
        }
        return "Due: ${ChaiUtils.fromNow(dueDate)}"
    }

    boolean isComplete() { status == Task.STATUS_COMPLETE }

    boolean isCancelled() { status == Task.STATUS_CANCELLED }

    boolean isOverDue() {
        if (!dueDate || isComplete()) return false
        return new Date().after(dueDate)
    }

}
