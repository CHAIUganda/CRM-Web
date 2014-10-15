package com.omnitech.chai.model

import grails.validation.Validateable
import org.neo4j.graphdb.Direction
import org.springframework.data.neo4j.annotation.Fetch
import org.springframework.data.neo4j.annotation.Indexed
import org.springframework.data.neo4j.annotation.NodeEntity
import org.springframework.data.neo4j.annotation.RelatedTo

@NodeEntity
@Validateable
class Device extends AbstractEntity {


    @Indexed(unique = true)
    String imei
    String model

    @Fetch
    @RelatedTo(type = 'HAS_DEVICE',direction = Direction.INCOMING)
    User user

    static constraints = {
        imei blank: false
        model blank: false
    }

    String toString() {
        "$model - $imei"
    }

}
