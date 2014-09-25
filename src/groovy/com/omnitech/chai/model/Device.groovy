package com.omnitech.chai.model

import org.springframework.data.neo4j.annotation.GraphId
import org.springframework.data.neo4j.annotation.NodeEntity

import javax.validation.constraints.NotNull

@NodeEntity
class Device {
    @GraphId
    Long id

    @NotNull
    String imei

    @NotNull
    String model

}
