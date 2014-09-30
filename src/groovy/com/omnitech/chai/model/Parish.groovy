package com.omnitech.chai.model

import grails.validation.Validateable
import org.neo4j.graphdb.Direction
import org.springframework.data.neo4j.annotation.Fetch
import org.springframework.data.neo4j.annotation.Indexed
import org.springframework.data.neo4j.annotation.NodeEntity
import org.springframework.data.neo4j.annotation.RelatedTo

/**
 * Created by kay on 9/29/14.
 */
@NodeEntity
@Validateable
class Parish extends AbstractEntity {

    @Indexed(unique = true)
    String name

    @Fetch
    @RelatedTo(type = Relations.HAS_PARISH, direction = Direction.INCOMING)
    SubCounty subCounty

    @RelatedTo(type = Relations.HAS_VILLAGE)
    Set<Village> villages

    static constraints = {
        name blank: false
    }

    String toString() { name }
}
