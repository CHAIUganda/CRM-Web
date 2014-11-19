package com.omnitech.chai.model

import org.springframework.data.neo4j.annotation.GraphId

import java.text.DateFormat
import java.text.SimpleDateFormat

/**
 * Created by kay on 9/28/14.
 */

class AbstractEntity {
    private static DateFormat format = new SimpleDateFormat('yyyy-MM-dd hh:mm:ss')
    @GraphId
    Long id
    String uuid
    Date dateCreated
    Date lastUpdated
    String _dateCreated
    String _dateLastUpdated

    Date getDateCreated() {
        return dateCreated
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
