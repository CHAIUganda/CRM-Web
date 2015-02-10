package com.omnitech.chai.model

import grails.validation.Validateable
import org.springframework.data.neo4j.annotation.*

import javax.validation.constraints.NotNull

@RelationshipEntity(type = 'HAS_PRODUCT')
@Validateable
class LineItem extends AbstractEntity {

    @StartNode
    HasLineItem hasLineItem

    @Fetch
    @EndNode
    Product product

    @NotNull
    Double quantity

    @NotNull
    Double unitPrice

    Double getLineCost() { quantity * (unitPrice ?: product.unitPrice ?: 0) }

    Double getUnitPrice() { unitPrice ?: product.unitPrice }

    static constraints = {
        product nullable: false
        hasLineItem nullable: false
        quantity min: 1d
        unitPrice min: 1d
    }
}

@RelationshipEntity(type = 'STOCK_PRODUCT')
@Validateable
class StockLine extends AbstractEntity {

    @StartNode
    StockInfo stockInfo

    @Fetch
    @EndNode
    Product product

    Double quantity

    static constraints = {
        product nullable: false
        stockInfo nullable: false
        quantity min: 1d
    }
}

/**
 * Implemented by any class that has LineItems. E.g A sale or an order
 */
@NodeEntity
interface HasLineItem {
    Set<LineItem> getLineItems()

    Double totalCost()
}