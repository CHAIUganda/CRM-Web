package com.omnitech.chai.model

import grails.validation.Validateable
import org.springframework.data.neo4j.annotation.Indexed
import org.springframework.data.neo4j.annotation.NodeEntity

@NodeEntity
@Validateable
class Role extends AbstractEntity {

    final static String DETAILER_ROLE_NAME = 'ROLE_DETAILER'
    final static String SALES_ROLE_NAME = 'ROLE_SALES'
    final static String SUPERVISOR_ROLE_NAME = 'ROLE_SUPERVISOR'

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
