package com.omnitech.chai.model

import com.omnitech.chai.util.ModelFunctions
import grails.validation.Validateable
import org.springframework.data.neo4j.annotation.Fetch
import org.springframework.data.neo4j.annotation.Indexed
import org.springframework.data.neo4j.annotation.NodeEntity
import org.springframework.data.neo4j.annotation.RelatedTo

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


    @RelatedTo(type = Relations.HAS_ROLE)
    @Fetch
    Set<Role> roles

    @Fetch
    @RelatedTo(type = 'HAS_DEVICE')
    Device device

    @Fetch
    @RelatedTo(type = Relations.USER_TERRITORY)
    Territory territory

    @RelatedTo(type = Relations.SUPERVISES_TERRITORY)
    Set<Territory> supervisedTerritories


    User() {}

    User(Map params) {
        ModelFunctions.bind(this, params)
    }


    boolean hasRole(Role r) {
        return roles?.any { r.authority == it.authority }
    }

    boolean hasRole(String... authorities) {
        authorities.any { authority -> roles?.any { authority == it.authority } }
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
