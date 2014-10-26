package com.omnitech.chai.scripts

import spock.lang.Specification

import static com.omnitech.chai.scripts.ScripHelpers.getRangeScore

/**
 * Created by kay on 10/26/14.
 */
class ScripHelpersTest extends Specification {
    def "GetRangeScore"() {

        when:
        def val = getRangeScore(30, [50, 15, 0])
        then:
        val == 2

        when:
        val = getRangeScore(60, [50, 15, 0])
        then:
        val == 3

        when:
        val = getRangeScore(7, [50, 15, 0])
        then:
        val == 1
    }
}
