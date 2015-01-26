package com.omnitech.chai.model

import com.omnitech.chai.util.GroupNode
import grails.validation.Validateable
import org.springframework.data.annotation.Transient
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

    @Transient
    Integer numberOfTasks


    static constraints = {
        name blank: false
        callFrequency nullable: false, min: 0
    }

    @Override
    GroupNode getParent() { return null }

    int getSpaceBetweenVisits() { callFrequency == 0 ? 0 : 60 / callFrequency }

    String toString(){name}

    boolean shouldGenerateTask(Date lastTaskDate){
        if (!lastTaskDate) return true
        def daysBetweenVisits = getSpaceBetweenVisits()
        def daysSinceLastVisit = new Date() - lastTaskDate
        return daysSinceLastVisit < daysBetweenVisits
    }
}
