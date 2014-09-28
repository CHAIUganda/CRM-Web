package com.omnitech.chai.model

import grails.validation.Validateable
import org.springframework.data.neo4j.annotation.GraphId
import org.springframework.data.neo4j.annotation.Indexed
import org.springframework.data.neo4j.annotation.NodeEntity

@NodeEntity
@Validateable
class Device extends AbstractEntity{


    @Indexed(unique = true)
    String imei
    String model

    static constraints = {
        imei blank: false
        model blank: false
    }

    String toString() {
        "$model - $imei"
    }

}
