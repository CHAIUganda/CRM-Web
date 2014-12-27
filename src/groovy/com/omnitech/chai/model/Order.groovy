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
class Order extends Task implements HasLineItem {

    @Fetch
    @RelatedToVia
    Set<LineItem> lineItems = new HashSet()
    String comment

    def beforeSave() {
        super.beforeSave()
        description = "Go Sale to [$customer.outletName]"
    }

    static constraints = {
        importFrom(Task)
    }

    @Override
    Double totalCost() {
        return lineItems?.sum {it.lineCost} as Double
    }


}
