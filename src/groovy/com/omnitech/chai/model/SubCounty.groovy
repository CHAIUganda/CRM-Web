package com.omnitech.chai.model

import grails.validation.Validateable
import org.neo4j.graphdb.Direction
import org.springframework.data.neo4j.annotation.Fetch
import org.springframework.data.neo4j.annotation.Indexed
import org.springframework.data.neo4j.annotation.NodeEntity
import org.springframework.data.neo4j.annotation.RelatedTo

@Validateable
@NodeEntity
class SubCounty extends AbstractEntity {

    @Indexed(unique = true)
    String name

    @Fetch
    @RelatedTo(type = Relations.HAS_SUB_COUNTY, direction = Direction.INCOMING)
    District district

    @RelatedTo(type = Relations.HAS_PARISH)
    Set<Parish> parishes

    String getDescription() {
        "$district:$name"
    }

    String toString() { name }

    static constraints = {
        name blank: false
    }

}
