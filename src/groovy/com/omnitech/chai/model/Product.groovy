package com.omnitech.chai.model

import grails.validation.Validateable
import org.springframework.data.neo4j.annotation.GraphId
import org.springframework.data.neo4j.annotation.NodeEntity

import javax.validation.constraints.NotNull

/**
 * Created by kay on 9/25/14.
 */
@NodeEntity
@Validateable
class Product {

    @GraphId
    Long id

    @NotNull
    String name

    @NotNull
    String metric

    @NotNull
    Double unitPrice

}
