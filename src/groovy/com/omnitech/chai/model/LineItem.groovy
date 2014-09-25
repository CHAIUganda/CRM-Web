package com.omnitech.chai.model

import org.springframework.data.neo4j.annotation.EndNode
import org.springframework.data.neo4j.annotation.Fetch
import org.springframework.data.neo4j.annotation.RelationshipEntity
import org.springframework.data.neo4j.annotation.StartNode

import javax.validation.constraints.NotNull


@RelationshipEntity(type = 'HAS_PRODUCT')
class LineItem {

    @StartNode
    Order order

    @Fetch
    @EndNode
    Product product

    @NotNull
    Double quantity

    @NotNull
    Double unitPrice
}
