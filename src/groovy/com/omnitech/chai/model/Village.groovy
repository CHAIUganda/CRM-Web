package com.omnitech.chai.model

import grails.validation.Validateable
import org.neo4j.graphdb.Direction
import org.springframework.data.neo4j.annotation.Indexed
import org.springframework.data.neo4j.annotation.NodeEntity
import org.springframework.data.neo4j.annotation.RelatedTo

/**
 * Created by kay on 9/29/14.
 */
@Validateable
@NodeEntity
class Village extends AbstractEntity {

    @Indexed(unique = true)
    String name

    @RelatedTo(type = Relations.HAS_VILLAGE, direction = Direction.INCOMING)
    Parish parish

    static constraints = {
        name blank: false
    }


}
