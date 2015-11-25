package com.omnitech.chai.model

import com.omnitech.chai.util.GroupNode
import grails.validation.Validateable
import org.springframework.data.annotation.Transient
import org.springframework.data.neo4j.annotation.Indexed
import org.springframework.data.neo4j.annotation.NodeEntity
import org.springframework.util.Assert

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
    Integer daysInPeriod

    @Transient
    Integer numberOfTasks


    static constraints = {
        name blank: false
        callFrequency nullable: false, min: 0
        daysInPeriod nullable: false, min: 1
    }

    @Override
    GroupNode getParent() { return null }

    int getSpaceBetweenVisits() {
        callFrequency == 0 ? 0 : getDaysInPeriod() / callFrequency
    }

    Integer getDaysInPeriod() {
        return daysInPeriod ?: 60
    }

    String toString(){name}

    boolean shouldGenerateTask(Date lastTaskDate){
        if (!lastTaskDate) return true
        def now = new Date()
        Assert.isTrue(lastTaskDate.before(now), "Last task date should not before today")
        def daysBetweenVisits = getSpaceBetweenVisits()


        def daysSinceLastVisit = now - lastTaskDate
        return daysBetweenVisits != 0 && daysSinceLastVisit >= daysBetweenVisits
    }
}
