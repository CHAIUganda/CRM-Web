package com.omnitech.chai.model

import grails.validation.Validateable
import org.codehaus.groovy.runtime.InvokerHelper
import org.springframework.data.neo4j.annotation.*

import javax.persistence.PrePersist
import javax.persistence.PreUpdate
import javax.validation.constraints.NotNull

@Validateable
@NodeEntity
class User {

    @GraphId
    Long id

    @NotNull
    @Indexed(unique = true)
    String username

    @NotNull
    String password


    boolean enabled = true
    boolean accountExpired
    boolean accountLocked
    boolean passwordExpired


    @RelatedTo(type = 'HAS_ROLE')
    @Fetch
    Set<Role> roles


    Date dateCreated
    Date lastUpdated

    User() {}

    User(Map params) {
        InvokerHelper.setProperties(this, params)
    }


    boolean hasRole(Role r) {
        return roles?.any { r.authority == it.authority }
    }

    @PreUpdate
    @PrePersist
    public void updateTimeStamps() {
        lastUpdated = new Date();
        if (dateCreated==null) {
            dateCreated = new Date();
        }
    }

    @Override
    public String toString() {
        return "${username}";
    }
}
