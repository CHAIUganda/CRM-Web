package com.omnitech.chai.model

import org.springframework.data.neo4j.annotation.Fetch
import org.springframework.data.neo4j.annotation.NodeEntity
import org.springframework.data.neo4j.annotation.RelatedToVia

/**
 * Created by kay on 12/23/2014.
 */

@NodeEntity
interface Sale {
    Set<LineItem> getLineItems()
    String getComment()
}

@NodeEntity
class DirectSale extends Task implements Sale {

    Integer howManyZincInStock;
    Integer howManyOrsInStock;
    String pointOfSaleMaterial;
    String recommendationNextStep;
    String recommendationLevel;
    String governmentApproval;
    Date dateOfSale;

    @Fetch
    @RelatedToVia
    Set<LineItem> lineItems = new HashSet()
    String comment

    def beforeSave() {
        super.beforeSave()
        this.status = STATUS_COMPLETE
        description = "Direct Sale [$customer.outletName]"
    }

    static constraints = {
        importFrom(Task)
    }
}

@NodeEntity
class SaleOrder extends Order implements Sale {

    Integer howManyZincInStock;
    Integer howManyOrsInStock;
    String pointOfSaleMaterial;
    String recommendationNextStep;
    String recommendationLevel;
    String governmentApproval;
    Date dateOfSale;

    def beforeSave() {
        super.beforeSave()
        this.status = STATUS_COMPLETE
    }

    static constraints = {
        importFrom(Task)
    }
}



