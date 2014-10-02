package com.omnitech.chai.model

import org.springframework.data.neo4j.annotation.NodeEntity
import org.springframework.data.neo4j.annotation.RelatedTo

import javax.validation.constraints.NotNull

/**
 * Created by kay on 9/24/14.
 */
@NodeEntity
class Territory extends AbstractEntity {

    @NotNull
    String name

    @RelatedTo(type = Relations.HAS_CUSTOMER)
    Set<Customer> cutomers

}
