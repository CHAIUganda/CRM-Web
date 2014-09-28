package com.omnitech.chai.model

import com.omnitech.chai.util.ChaiUtils
import grails.validation.Validateable
import org.springframework.data.neo4j.annotation.*

import javax.persistence.PrePersist
import javax.persistence.PreUpdate

@Validateable
@NodeEntity
class User extends AbstractEntity {

    @Indexed(unique = true)
    String username
    String password
    boolean enabled = true
    boolean accountExpired
    boolean accountLocked
    boolean passwordExpired


    static constraints = {
        username blank: false
        password blank: false
    }


    @RelatedTo(type = 'HAS_ROLE')
    @Fetch
    Set<Role> roles


    @RelatedTo(type = 'HAS_INTERACTION')
    Set<Interaction> tasks

    @RelatedTo(type = 'HAS_DEVICE')
    Device device


    User() {}

    User(Map params) {
        ChaiUtils.bind(this, params)
    }


    boolean hasRole(Role r) {
        return roles?.any { r.authority == it.authority }
    }

    @PreUpdate
    @PrePersist
    public void updateTimeStamps() {
        lastUpdated = new Date();
        if (dateCreated == null) {
            dateCreated = new Date();
        }
    }

    @Override
    public String toString() {
        return "${username}";
    }
}
