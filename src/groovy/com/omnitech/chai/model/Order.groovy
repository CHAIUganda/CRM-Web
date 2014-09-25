package com.omnitech.chai.model

import org.neo4j.graphdb.Direction
import org.springframework.data.neo4j.annotation.Fetch
import org.springframework.data.neo4j.annotation.GraphId
import org.springframework.data.neo4j.annotation.NodeEntity
import org.springframework.data.neo4j.annotation.RelatedTo
import org.springframework.data.neo4j.annotation.RelatedToVia

/**
 * Created by kay on 9/25/14.
 */
@NodeEntity
class Order {

    static String PENDING = 'new', COMPLETE = 'complete'

    @GraphId
    Long id

    String status = PENDING

    @Fetch
    @RelatedToVia
    Set<LineItem> lineItems = new HashSet()

    @RelatedTo(type = 'HAS_ORDER',direction = Direction.INCOMING)
    Interaction interaction
}
