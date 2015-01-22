package com.omnitech.chai.model

import grails.validation.Validateable
import org.springframework.data.neo4j.annotation.Indexed
import org.springframework.data.neo4j.annotation.NodeEntity

@NodeEntity
@Validateable
class Role extends AbstractEntity {

    final static String DETAILER_ROLE_NAME = 'ROLE_DETAILER'
    final static String SUPER_ADMIN_ROLE_NAME = 'ROLE_SUPER_ADMIN'
    final static String ADMIN_ROLE_NAME = 'ROLE_ADMIN'
    final static String SALES_ROLE_NAME = 'ROLE_SALES'
    final static String DETAILING_SUPERVISOR_ROLE_NAME = 'ROLE_DETAILING_SUPERVISOR'
    final static String SALES_SUPERVISOR_ROLE_NAME = 'ROLE_SALES_SUPERVISOR'

    @Indexed(unique = true)
    String authority

    static constraints = {
        authority blank: false, unique: true
    }

    @Override
    public String toString() {
        return "${authority}";
    }


}
