package com.omnitech.chai.model

import grails.validation.Validateable
import org.springframework.data.neo4j.annotation.Indexed
import org.springframework.data.neo4j.annotation.NodeEntity
import org.springframework.data.neo4j.annotation.RelatedTo

@NodeEntity
@Validateable
class Region extends AbstractEntity {

    @Indexed(unique = true)
    String name

    @RelatedTo(type = Relations.HAS_DISTRICT)
    Set<District> districts

    static constraints = {
        name blank: false
    }

    String toString(){
        "$name"
    }
}
