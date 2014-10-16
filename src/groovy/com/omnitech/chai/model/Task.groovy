package com.omnitech.chai.model

import grails.validation.Validateable
import org.neo4j.graphdb.Direction
import org.springframework.data.neo4j.annotation.Fetch
import org.springframework.data.neo4j.annotation.NodeEntity
import org.springframework.data.neo4j.annotation.RelatedTo

/**
 * Created by kay on 9/24/14.
 */
@NodeEntity
@Validateable
class Task extends AbstractEntity {

    final static String STATUS_NEW = 'new', STATUS_COMPLETE = 'complete'

    String description
    protected String type = Task.simpleName
    String status = STATUS_NEW

    @Fetch
    @RelatedTo(type = Relations.ASSIGNED_TASK, direction = Direction.INCOMING)
    User assignedTo

    @Fetch
    @RelatedTo(type = Relations.COMPLETED_TASK, direction = Direction.INCOMING)
    private User completedBy

    @Fetch
    @RelatedTo(type = Relations.CUST_TASK, direction = Direction.INCOMING)
    private Customer customer

    Task completedBy(User user) {
        setCompletedBy(user)
        status = STATUS_COMPLETE
        return this
    }

    Task assignedTO(User user) {
        setAssignedTo(user)
        return this
    }

    private void setCompletedBy(User user) {
        this.@assignedTo == null
        this.@completedBy = user
    }

    private void setAssignedTo(User assignedTo) {
        this.@completedBy = null
        this.assignedTo = assignedTo
    }

    User getCompletedBy() { completedBy }

    User getAssignedTo() { assignedTo }

    static constraints = {
        description blank: false
        status blank: false
    }


}
