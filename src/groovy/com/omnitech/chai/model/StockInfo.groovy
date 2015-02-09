package com.omnitech.chai.model

import org.springframework.data.neo4j.annotation.NodeEntity

/**
 * Created by kay on 2/9/2015.
 */
@NodeEntity
interface StockInfo {
    Set<StockLine> getStockLines()

    Customer getCustomer()
}
