package com.omnitech.chai.model

import org.neo4j.graphdb.Direction
import org.springframework.data.neo4j.annotation.GraphId
import org.springframework.data.neo4j.annotation.Indexed
import org.springframework.data.neo4j.annotation.NodeEntity
import org.springframework.data.neo4j.annotation.RelatedTo

import javax.validation.constraints.NotNull

/**
 * Created by kay on 9/21/14.
 */
@NodeEntity
public class Customer {
    @GraphId
    Long id;

    @NotNull
    String firstName

    @NotNull
    String lastName;

    @Indexed(unique = true)
    String emailAddress;

    String phone1

    String phone2

    @RelatedTo(type = 'HAS_CUSTOMER', direction = Direction.INCOMING)
    Interaction interaction

    @RelatedTo(type = 'HAS_TERRITORY')
    Territory territory

}