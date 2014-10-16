package com.omnitech.chai.model

import grails.validation.Validateable
import org.springframework.data.neo4j.annotation.Fetch
import org.springframework.data.neo4j.annotation.NodeEntity
import org.springframework.data.neo4j.annotation.RelatedToVia

/**
 * Created by kay on 9/25/14.
 */
@NodeEntity
@Validateable
class Order extends Task {

    @Fetch
    @RelatedToVia
    Set<LineItem> lineItems = new HashSet()

    def beforeSave() {
        this.type = Order.simpleName
    }

    static constraints = {
        importFrom(Task)
    }

}
