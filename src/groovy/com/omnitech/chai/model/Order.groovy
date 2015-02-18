package com.omnitech.chai.model

import grails.validation.Validateable
import org.springframework.data.neo4j.annotation.Fetch
import org.springframework.data.neo4j.annotation.NodeEntity
import org.springframework.data.neo4j.annotation.RelatedTo
import org.springframework.data.neo4j.annotation.RelatedToVia

/**
 * Created by kay on 9/25/14.
 */

class SalesCall extends Task {

    def beforeSave() {
        super.beforeSave()
        if (!description && getClass() == SalesCall) {
            description = "Visit [$customer.outletName]"
        }
    }
}

@NodeEntity
@Validateable
class Order extends SalesCall implements HasLineItem {

    @Fetch
    @RelatedToVia
    Set<LineItem> lineItems = new HashSet()
    String comment
    String clientRefId

    @Fetch
    @RelatedTo(type = Relations.ORDER_TAKEN_BY)
    User takenBy

    def beforeSave() {
        super.beforeSave()
        if (!description && getClass() == Order) {
            description = "Order [$customer.outletName]"
        }
    }

    static constraints = {
        importFrom(Task)
    }

    @Override
    Double totalCost() {
        return lineItems?.sum { it.lineCost } as Double
    }

    static Order create(Customer customer, Date date) {
        new Order(customer: customer, dueDate: date)
    }


}
