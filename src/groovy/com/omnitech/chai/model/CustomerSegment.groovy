package com.omnitech.chai.model

import grails.validation.Validateable
import org.springframework.data.neo4j.annotation.NodeEntity

/**
 * Created by kay on 10/22/14.
 */
@Validateable
@NodeEntity
class CustomerSegment extends AbstractEntity {

    String name
    String segmentationScript
    String teskGeneratorScript
    Integer callFrequency


    static constraints = {
        name blank: false
        callFrequency nullable: false, min: 1
    }
}
