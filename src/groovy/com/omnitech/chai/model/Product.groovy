package com.omnitech.chai.model

import grails.validation.Validateable
import org.springframework.data.neo4j.annotation.NodeEntity

/**
 * Created by kay on 9/25/14.
 */
@NodeEntity
@Validateable
class Product extends AbstractEntity {

    String name
    String unitOfMeasure
    String formulation
    Double unitPrice


    static constraints = {
        name blank: false
        unitOfMeasure blank: false
        unitPrice nullable: false
        formulation blank: false
    }

}
