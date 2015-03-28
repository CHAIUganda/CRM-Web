package com.omnitech.chai.scripts

import spock.lang.Specification

import static com.omnitech.chai.scripts.ScripHelpers.intRangeScore

/**
 * Created by kay on 10/26/14.
 */
class ScripHelpersTest extends Specification {
    def "GetRangeScore"() {

        when:
        def val = intRangeScore(30, [50, 15, 0])
        then:
        val == 2

        when:
        val = intRangeScore(60, [50, 15, 0])
        then:
        val == 3

        when:
        val = intRangeScore(7, [50, 15, 0])
        then:
        val == 1

        when:
        val = intRangeScore(5, [15, 5, 0])
        then:
        val == 2

        when:
        val = intRangeScore(15, [15, 5, 0])
        then:
        val == 2

        when:
        val = intRangeScore(0, [15, 5, 0])
        then:
        val ==1
    }
}
