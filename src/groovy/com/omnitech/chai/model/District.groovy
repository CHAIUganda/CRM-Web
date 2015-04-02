package com.omnitech.chai.model

import grails.validation.Validateable
import org.neo4j.graphdb.Direction
import org.springframework.data.neo4j.annotation.Fetch
import org.springframework.data.neo4j.annotation.Indexed
import org.springframework.data.neo4j.annotation.NodeEntity
import org.springframework.data.neo4j.annotation.RelatedTo
import org.springframework.util.Assert

import javax.validation.constraints.NotNull

/**
 * Created by kay on 9/29/14.
 */
@Validateable
@NodeEntity
class District extends AbstractEntity {


    @Indexed(unique = true)
    String name

    @Fetch
    @RelatedTo(type = Relations.HAS_DISTRICT, direction = Direction.INCOMING)
    @NotNull
    Region region

    @RelatedTo(type = Relations.HAS_SUB_COUNTY)
    Set<SubCounty> subCounties


    static constraints = {
        name blank: false
        region blank: false
    }

    String toString() {
        "$name"
    }

    def beforeDelete(){
        Assert.state subCounties?.size() == 0, 'Cannot Delete a District With SubCounties'
    }

}
