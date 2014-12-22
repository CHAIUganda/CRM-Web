package com.omnitech.chai.model

import com.omnitech.chai.util.ChaiUtils
import grails.validation.Validateable
import org.neo4j.graphdb.Direction
import org.springframework.data.neo4j.annotation.Fetch
import org.springframework.data.neo4j.annotation.Indexed
import org.springframework.data.neo4j.annotation.NodeEntity
import org.springframework.data.neo4j.annotation.RelatedTo
import org.springframework.data.neo4j.support.index.IndexType

/**
 * Created by kay on 9/24/14.
 */
@NodeEntity
@Validateable
class Task extends AbstractEntity {

    final static String STATUS_NEW = 'new', STATUS_COMPLETE = 'complete', STATUS_CANCELLED = 'cancelled'

    String description
    protected String type = Task.simpleName
    String status = STATUS_NEW
    Date dueDate
    Date completionDate
    Date systemDueDate

    @Fetch
    @RelatedTo(type = Relations.ASSIGNED_TASK, direction = Direction.INCOMING)
    User assignedTo

    @Fetch
    @RelatedTo(type = Relations.COMPLETED_TASK, direction = Direction.INCOMING)
    User completedBy

    @Fetch
    @RelatedTo(type = Relations.CUST_TASK, direction = Direction.INCOMING)
    Customer customer
    Float lat
    Float lng
    @Indexed(indexType = IndexType.POINT, indexName = 'TASK_LOCATION')
    String wkt

    Task completedBy(User user) {
        status = STATUS_COMPLETE
        this.@assignedTo == null
        this.@completedBy = user
        this.completionDate = new Date()
        return this
    }

    Task assignedTO(User user) {
        this.@completedBy = null
        this.assignedTo = user
        return this
    }

    void setCompletedBy(User user) { completedBy(user) }

    void setAssignedTo(User assignedTo) { assignedTO(assignedTo) }

    User getCompletedBy() { completedBy }

    User getAssignedTo() { assignedTo }

    String getType() { type }

    boolean isLocatable() { customer?.wkt != null }

    boolean isComplete() { status == STATUS_COMPLETE }

    String getStatusMessage() {
        if (isComplete()) {
            return "Completed: ${ChaiUtils.fromNow(completionDate)}"
        }
        return "Due: ${ChaiUtils.fromNow(dueDate)}"
    }

    boolean isOverDue() {
        if (!dueDate || isComplete()) return false
        return new Date().after(dueDate)
    }

    Set<User> territoryUser() {
        this.customer?.subCounty?.territory?.territoryUsers
    }

    static constraints = {
        description blank: false
        status blank: false
    }

    def beforeSave() {
        setLocation(lng, lat)
        this.type = getClass().simpleName
    }

    public void setLocation(Float lng, Float lat) {
        this.lat = lat
        this.lng = lng
        if (lat && lng)
            this.wkt = String.format("POINT( %.6f %.6f )", lng, lat);
        else
            this.wkt = null
    }


}
