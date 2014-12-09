package com.omnitech.chai.model

import grails.validation.Validateable
import org.springframework.data.neo4j.annotation.Indexed
import org.springframework.data.neo4j.annotation.NodeEntity

/**
 * Created by kay on 9/29/14.
 */
@NodeEntity
@Validateable
class Parish extends AbstractEntity {

    @Indexed(unique = true)
    String name

    static constraints = {
        name blank: false
    }

    String toString() { name }
}
