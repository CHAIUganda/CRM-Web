package com.omnitech.chai.model

import grails.validation.Validateable
import org.neo4j.graphdb.Direction
import org.springframework.data.neo4j.annotation.Fetch
import org.springframework.data.neo4j.annotation.Indexed
import org.springframework.data.neo4j.annotation.NodeEntity
import org.springframework.data.neo4j.annotation.RelatedTo

import javax.validation.constraints.NotNull

/**
 * Created by kay on 9/24/14.
 */
@NodeEntity
@Validateable
class Territory extends AbstractEntity {

    @NotNull
    @Indexed(unique = true)
    String name

    @RelatedTo(type = Relations.SC_IN_TERRITORY, direction = Direction.INCOMING)
    Set<SubCounty> subCounties

    @RelatedTo(type = Relations.USER_TERRITORY, direction = Direction.INCOMING)
    Set<User> territoryUsers

    @Fetch
    @RelatedTo(type = Relations.SUPERVISES_TERRITORY, direction = Direction.INCOMING)
    User supervisor

    static constraints = {
        name blank: false
    }

    String toString() { name }

}
