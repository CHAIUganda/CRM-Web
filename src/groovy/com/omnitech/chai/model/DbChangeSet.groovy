package com.omnitech.chai.model

import groovy.transform.ToString
import org.springframework.data.neo4j.annotation.Indexed
import org.springframework.data.neo4j.annotation.NodeEntity

/**
 * Created by kay on 3/12/2015.
 */
@NodeEntity
@ToString(includes = ['userId', 'description'],includePackage = false)
class DbChangeSet extends AbstractEntity {
    String description
    @Indexed(unique = true)
    String changeId
    String cypher

    String toString() {
        "$description"
    }

    boolean equals(o) {
        if (this.is(o)) return true
        if (getClass() != o.class) return false

        DbChangeSet that = (DbChangeSet) o

        if (changeId != that.changeId) return false

        return true
    }

    int hashCode() {
        return changeId.hashCode()
    }
}
