package com.omnitech.chai.model

import com.omnitech.chai.util.GroupNode
import grails.validation.Validateable
import org.springframework.data.neo4j.annotation.Indexed
import org.springframework.data.neo4j.annotation.NodeEntity

/**
 * Created by kay on 10/22/14.
 */
@Validateable
@NodeEntity
class CustomerSegment extends AbstractEntity implements GroupNode {

    @Indexed(unique = true)
    String name
    String segmentationScript
    String taskGeneratorScript
    Integer callFrequency


    static constraints = {
        name blank: false
        callFrequency nullable: false, min: 1
    }

    @Override
    GroupNode getParent() { return null }
}
