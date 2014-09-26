package com.omnitech.chai.model

import grails.validation.Validateable
import org.springframework.data.neo4j.annotation.GraphId
import org.springframework.data.neo4j.annotation.Indexed
import org.springframework.data.neo4j.annotation.NodeEntity

import javax.validation.constraints.NotNull

@NodeEntity
@Validateable
class Device {
    @GraphId
    Long id

    @Indexed(unique = true)
    @NotNull
    String imei

    @NotNull
    String model

}
