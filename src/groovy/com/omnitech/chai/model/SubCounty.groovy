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

    @Indexed
    String name

    @Fetch
    @RelatedTo(type = Relations.HAS_SUB_COUNTY, direction = Direction.INCOMING)
    District district

    @RelatedTo(type = Relations.HAS_PARISH)
    Set<Parish> parishes

    @Fetch
    @RelatedTo(type = Relations.SC_IN_TERRITORY)
    Territory territory

    @Fetch
    @RelatedTo(type = Relations.WHOLE_SALER_SC,direction = Direction.INCOMING)
    WholeSaler wholeSaler

    String getDescription() {
        "$district:$name"
    }

    String toString() { name }

    static constraints = {
        name blank: false
    }

}
