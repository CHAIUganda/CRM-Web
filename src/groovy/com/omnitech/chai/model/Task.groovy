package com.omnitech.chai.model

import com.omnitech.chai.crm.TxHelperService
import com.omnitech.chai.util.ChaiUtils
import grails.util.Holders
import grails.validation.Validateable
import org.neo4j.graphdb.Direction
import org.springframework.data.neo4j.annotation.*
import org.springframework.data.neo4j.support.Neo4jTemplate
import org.springframework.data.neo4j.support.index.IndexType

import static com.omnitech.chai.util.ChaiUtils.bean

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

    @GraphProperty(propertyType = Long.class)
    Date dueDate
    @GraphProperty(propertyType = Long.class)
    Date completionDate


    Boolean isAdhock

    @Fetch
    @RelatedTo(type = Relations.ASSIGNED_TASK, direction = Direction.INCOMING)
    User assignedTo

    @Fetch
    @RelatedTo(type = Relations.COMPLETED_TASK, direction = Direction.INCOMING)
    User completedBy

    @Fetch
    @RelatedTo(type = Relations.CANCELED_TASK, direction = Direction.INCOMING)
    User cancelledBy

    @Fetch
    @RelatedTo(type = Relations.CUST_TASK, direction = Direction.INCOMING)
    Customer customer
    Float lat
    Float lng
    @Indexed(indexType = IndexType.POINT, indexName = 'TASK_LOCATION')
    String wkt

    String clientRefId

    Task completedBy(User user) {
        status = STATUS_COMPLETE
        this.@assignedTo == null
        this.@completedBy = user
        if (!completionDate)
            this.completionDate = new Date()
        return this
    }

    Task cancelledBy(User user) {
        status = STATUS_CANCELLED
        this.@cancelledBy = user
        if (!completionDate)
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

    boolean isCancelled() { status == STATUS_CANCELLED }

    String getStatusMessage() {
        if (isComplete()) {
            return "Completed: ${ChaiUtils.fromNow(completionDate)}"
        }
        if (isCancelled()) {
            return "Canceled: ${ChaiUtils.fromNow(completionDate)}"
        }
        return "Due: ${ChaiUtils.fromNow(dueDate)}"
    }

    boolean isOverDue() {
        if (!dueDate || isComplete()) return false
        return new Date().after(dueDate)
    }

    Set<User> territoryUser() {

        String role = Role.SALES_ROLE_NAME
        if (getClass().isAssignableFrom(DetailerTask)) {
            role = Role.DETAILER_ROLE_NAME
        }

        def flatten = this.customer?.subCounty?.territory?.collect { it.territoryUsers }?.flatten()
        flatten?.findAll {
            it?.hasRole(role)
        }?.findResults { it } as Set
    }

    Set<User> loadTerritoryUsers() {
        bean(TxHelperService).doInTransaction {
            neo.fetch(this.customer?.subCounty?.territory)
            customer?.subCounty?.territory?.collect { t ->
                neo.fetch(t?.territoryUsers)
                return t?.territoryUsers
            }?.flatten() as Set
        }
    }

    def territoryUser(String role) {
        this.customer?.subCounty?.territory?.collect { it.territoryUsers }?.flatten()?.findAll {
            it?.hasRole(role)
        }?.findResults { it }
    }


    static constraints = {
        description blank: false
        status blank: false
    }

    def beforeSave() {
        setLocation(lng, lat)
        this.type = getClass().simpleName
    }

    String toString() { description }

    public void setLocation(Float lng, Float lat) {
        this.lat = lat
        this.lng = lng
        if (lat && lng)
            this.wkt = String.format("POINT( %.6f %.6f )", lng, lat);
        else
            this.wkt = null
    }


}
