package com.omnitech.chai.model

import grails.validation.Validateable
import org.springframework.data.neo4j.annotation.Indexed
import org.springframework.data.neo4j.annotation.NodeEntity

@NodeEntity
@Validateable
class Role extends AbstractEntity {

    @Indexed(unique = true)
    String authority

    static constraints = {
        authority blank: false, unique: true
    }

    @Override    // Override toString for a nicer / more descriptive UI
    public String toString() {
        return "${authority}";
    }
}
