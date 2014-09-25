package com.omnitech.chai.model

import org.springframework.data.neo4j.annotation.GraphId
import org.springframework.data.neo4j.annotation.NodeEntity

import javax.validation.constraints.NotNull

/**
 * Created by kay on 9/24/14.
 */
@NodeEntity
class Territory {

    @GraphId
    Long id

    @NotNull
    String name

}
