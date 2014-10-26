package com.omnitech.chai.model

import grails.validation.Validateable
import org.springframework.data.neo4j.annotation.Indexed
import org.springframework.data.neo4j.annotation.NodeEntity

/**
 * Created by kay on 10/26/14.
 */
@NodeEntity
@Validateable
class Setting extends AbstractEntity {

    @Indexed(unique = true)
    String name
    String value

    static constraints = {
        name blank: false
        value blank: false
    }
}
