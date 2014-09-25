package com.omnitech.chai.model

import org.springframework.data.neo4j.annotation.GraphId
import org.springframework.data.neo4j.annotation.NodeEntity

import javax.validation.constraints.NotNull

/**
 * Created by kay on 9/25/14.
 */
@NodeEntity
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
