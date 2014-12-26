package com.omnitech.chai.model

import org.springframework.data.neo4j.annotation.Fetch
import org.springframework.data.neo4j.annotation.NodeEntity
import org.springframework.data.neo4j.annotation.RelatedToVia

/**
 * Created by kay on 12/23/2014.
 */

@NodeEntity
interface Sale extends HasLineItem {
    String getComment()
}

@NodeEntity
class DirectSale extends Task implements Sale {

    Integer howManyZincInStock;
    Integer howManyOrsInStock;
    String pointOfSaleMaterial;
    String recommendationNextStep;
    String recommendationLevel;
    Boolean governmentApproval;
    Date dateOfSale;

    @Fetch
    @RelatedToVia
    Set<LineItem> lineItems = new HashSet()
    String comment

    def beforeSave() {
        super.beforeSave()

        if (!dateOfSale)
            dateOfSale = new Date()

        if (!this.dueDate)
            this.dueDate = dateOfSale

        this.status = STATUS_COMPLETE
        description = "Direct Sale [$customer.outletName]"
    }

    static constraints = {
        importFrom(Task)
        lineItems minSize: 1
    }
}

@NodeEntity
class SaleOrder extends Order implements Sale {

    Integer howManyZincInStock;
    Integer howManyOrsInStock;
    String pointOfSaleMaterial;
    String recommendationNextStep;
    String recommendationLevel;
    Boolean governmentApproval;
    Date dateOfSale;

    def beforeSave() {
        super.beforeSave()
        this.status = STATUS_COMPLETE
    }

    static constraints = {
        importFrom(Task)
    }
}



