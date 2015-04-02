package com.omnitech.chai.model

import com.omnitech.chai.crm.MigrationService
import grails.validation.Validateable
import org.neo4j.graphdb.Direction
import org.springframework.data.neo4j.annotation.Fetch
import org.springframework.data.neo4j.annotation.Indexed
import org.springframework.data.neo4j.annotation.NodeEntity
import org.springframework.data.neo4j.annotation.RelatedTo
import org.springframework.util.Assert

import static com.omnitech.chai.util.ChaiUtils.bean

@Validateable
@NodeEntity
class SubCounty extends AbstractEntity {

    @Indexed
    String name

    @Fetch
    @RelatedTo(type = Relations.HAS_SUB_COUNTY, direction = Direction.INCOMING)
    District district

    @Fetch
    @RelatedTo(type = Relations.SC_IN_TERRITORY)
    Set<Territory> territory

    @Fetch
    @RelatedTo(type = Relations.WHOLE_SALER_SC, direction = Direction.INCOMING)
    WholeSaler wholeSaler

    String getDescription() {
        "$district:$name"
    }

    String toString() { name }

    static constraints = {
        name blank: false
    }


    def beforeDelete() {
        def migrationService = bean(MigrationService)
        Assert.state migrationService.test("START sc=node($id) MATCH sc<-[:CUST_IN_SC]-(c) return count(c) = 0 as answer"), "SubCounty [$name] Cannot Be Deleted Because It Has Customers"
    }

}
