package com.omnitech.chai.model

import org.springframework.data.neo4j.annotation.GraphId
import org.springframework.data.neo4j.annotation.GraphProperty

import java.text.DateFormat
import java.text.SimpleDateFormat

/**
 * Created by kay on 9/28/14.
 */

class AbstractEntity {
    static DateFormat format = new SimpleDateFormat('yyyy-MM-dd hh:mm:ss')
    @GraphId
    Long id
    String uuid
    @GraphProperty(propertyType = Long.class)
    Date dateCreated
    @GraphProperty(propertyType = Long.class)
    Date lastUpdated
    String _dateCreated
    String _dateLastUpdated

    Date getDateCreated() {
        return dateCreated
    }

    private boolean leaveUuidIntact = false

    void setUuid(String uuid) {
        if (leaveUuidIntact && this.uuid != null) {
            return
        }
        this.uuid = uuid
    }

    def denyUuidAlter() {
        leaveUuidIntact = true
        return this
    }

    def allowUuidAlter() {
        leaveUuidIntact = false
        return this
    }

    boolean getLeaveUuidIntact() {
        return leaveUuidIntact
    }

    void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated
        if (dateCreated)
            this._dateCreated = format.format(dateCreated)
    }

    Date getLastUpdated() {
        return lastUpdated
    }

    void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated
        if (lastUpdated)
            this._dateLastUpdated = format.format(lastUpdated)
    }
}
