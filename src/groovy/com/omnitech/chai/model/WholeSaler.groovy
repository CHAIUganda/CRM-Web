package com.omnitech.chai.model

import grails.validation.Validateable
import org.springframework.data.neo4j.annotation.NodeEntity
import org.springframework.data.neo4j.annotation.RelatedTo

/**
 * Created by kay on 12/20/2014.
 */
@Validateable
@NodeEntity
class WholeSaler extends AbstractEntity {

    String name
    String contact


    @RelatedTo(type = Relations.WHOLE_SALER_SC)
    Set<SubCounty> subCounties

    static constraints = {
        name blank: false
        contact blank: false
    }

}
