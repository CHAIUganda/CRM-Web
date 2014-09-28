package com.omnitech.chai.model

import org.springframework.data.neo4j.annotation.NodeEntity
import org.springframework.data.neo4j.annotation.RelatedTo

import javax.validation.constraints.NotNull

/**
 * This holds any type of interaction between a Customer and a SalesRep
 *
 *  An interaction can have an order, a task
 *
 */
@NodeEntity
class Interaction extends AbstractEntity {


    @NotNull
    String initiationType = "adhoc"

    @RelatedTo(type = 'HAS_TASK')
    Task task

    @RelatedTo(type = 'HAS_ORDER')
    Order order

    @RelatedTo(type = 'HAS_CUSTOMER')
    Customer customer
}