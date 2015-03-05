package com.omnitech.chai.model

import spock.lang.Specification

/**
 * Created by kay on 3/5/2015.
 */
class CustomerSegmentTest extends Specification {
    def "GetSpaceBetweenVisits"() {

        expect:
        def cs = new CustomerSegment(daysInPeriod: daysInPeriod, callFrequency: callFrequency)
        shouldGenerate == cs.shouldGenerateTask(prvDate)

        where:
        daysInPeriod | callFrequency | prvDate         | shouldGenerate
        60           | 2             | new Date()      | false
        60           | 2             | new Date() - 31 | true
        60           | 2             | new Date() - 29 | false
        60           | 4             | new Date() - 29 | true
        364          | 4             | new Date() - 29 | false
        20           | 0             | new Date() - 29 | false
        0            | 1             | new Date() - 29 | false


    }
}
