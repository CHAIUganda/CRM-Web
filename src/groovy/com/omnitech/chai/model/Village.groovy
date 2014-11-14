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
@Validateable
@NodeEntity
class Village extends AbstractEntity {

    @Indexed
    String name

    @Fetch
    @RelatedTo(type = Relations.HAS_VILLAGE, direction = Direction.INCOMING)
    Parish parish

    static constraints = {
        name blank: false
    }


    String toString() { name }
}
