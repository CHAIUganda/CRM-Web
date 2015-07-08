package com.omnitech.chai.repositories.dto

import groovy.transform.CompileStatic
import org.springframework.data.neo4j.annotation.QueryResult
import org.springframework.data.neo4j.annotation.ResultColumn

/**
 * Created by kay on 3/28/2015.
 */
@QueryResult
@CompileStatic
class CustomerDTO {
    @ResultColumn('id')
    Long id
    @ResultColumn('outletName')
    String outletName
    @ResultColumn('outletType')
    String outletType
    @ResultColumn('outletSize')
    String outletSize
    @ResultColumn('dateCreated')
    Date dateCreated
    @ResultColumn('district')
    String district
    @ResultColumn('segment')
    String segment
    @ResultColumn('isActive')
    Boolean isActive
    String type = 'customer'
}

@QueryResult
@CompileStatic
class CustomerDTOExt extends CustomerDTO {
    @ResultColumn('id')
    Long id
    @ResultColumn('outletName')
    String outletName
    @ResultColumn('outletType')
    String outletType
    @ResultColumn('outletSize')
    String outletSize
    @ResultColumn('dateCreated')
    Date dateCreated
    @ResultColumn('district')
    String district
    @ResultColumn('segment')
    String segment
    @ResultColumn('isActive')
    Boolean isActive
    String type = 'customer'
    @ResultColumn('lastVisit')
    Date lastVisit
}