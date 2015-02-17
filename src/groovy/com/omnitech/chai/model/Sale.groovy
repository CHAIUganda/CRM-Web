package com.omnitech.chai.model

import org.springframework.data.neo4j.annotation.*

/**
 * Created by kay on 12/23/2014.
 */

@NodeEntity
@QueryResult
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
    @GraphProperty(propertyType = Long.class)
    Date dateOfSale;

    @Fetch
    @RelatedToVia
    Set<LineItem> lineItems = new HashSet()
    String comment

    String clientRefId

    def beforeSave() {
        super.beforeSave()

        if (!dateOfSale)
            dateOfSale = new Date()

        if (!this.dueDate)
            this.dueDate = dateOfSale

        this.status = STATUS_COMPLETE
        description = "Adhoc Sale [$customer.outletName]"
    }

    static constraints = {
        importFrom(Task)
        lineItems minSize: 1
    }

    @Override
    Double totalCost() {
        return lineItems?.sum { it.lineCost } as Double
    }
}

class DirectSaleWithStock extends DirectSale implements StockInfo {
    @Fetch
    @RelatedToVia
    Set<StockLine> stockLines = new HashSet()
}

@NodeEntity
class SaleOrder extends Order implements Sale {

    Integer howManyZincInStock;
    Integer howManyOrsInStock;
    String pointOfSaleMaterial;
    String recommendationNextStep;
    String recommendationLevel;
    Boolean governmentApproval;
    @GraphProperty(propertyType = Long.class)
    Date dateOfSale;

    def beforeSave() {
        super.beforeSave()
        this.status = STATUS_COMPLETE
    }

    static constraints = {
        importFrom(Task)
    }
}

class SaleOrderWithStock extends SaleOrder implements StockInfo {
    @Fetch
    @RelatedToVia
    Set<StockLine> stockLines = new HashSet()
}



