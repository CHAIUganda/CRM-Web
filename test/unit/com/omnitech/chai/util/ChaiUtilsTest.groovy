package com.omnitech.chai.util

import spock.lang.Specification

/**
 * Created by kay on 10/21/14.
 */
class ChaiUtilsTest extends Specification {
    def "MillisecondsToStr"() {

        expect:
        str == ChaiUtils.fromNow(dateFromNow)

        where:
        str                 | dateFromNow
        'now'               | new Date()
        '23 hours'          | new Date() + 1
        '1 day ago'         | new Date() - 1
        '1 day'             | new Date() + 2
        '5 days'            | new Date() + 6
        '6 days ago'        | new Date() - 6
        '6 days'            | new Date() + 7
        '1 week and 4 days' | new Date() + 12
        '1 month'           | new Date() + 30
        '1 month'           | new Date() + 40
        '2 months'          | new Date() + 50
        '1 year'            | new Date() + 368
        '1 year'            | new Date() + 400
        '2 years'           | new Date() + 690
        '2 years ago'       | new Date() - 690

   new Date().
    }

    def 'test round to nearest'() {
        expect:
        round == ChaiUtils.roundToNearest(value, nearest)
        where:
        round | value | nearest
        100   | 80    | 100
        0     | 40    | 100
        100   | 120   | 100
        200   | 160   | 100
        64    | 58    | 32

    }
}
