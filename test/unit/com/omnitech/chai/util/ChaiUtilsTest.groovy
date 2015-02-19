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
        str          | dateFromNow
        'now'        | new Date()
        '23 hours'   | new Date() + 1
        '1 day ago'  | new Date() - 1
        '2 days ago' | new Date() - 2
        '2 days'     | new Date() + 2
        '6 days'     | new Date() + 6
        '6 days ago' | new Date() - 6
        '7 days'     | new Date() + 7
        //todo fix 1  week 1 day
        '1 week' | new Date() + 8
        '1 week and 4 days' | new Date() + 12
        '3 weeks' | new Date() + 28
        '4 weeks' | new Date() + 30
        '1 month' | new Date() + 40
        '2 months' | new Date() + 50
        '1 year' | new Date() + 368
        '1 year' | new Date() + 400
        '2 years' | new Date() + 690
        '2 years ago' | new Date() - 690

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
        2     | 1.6d  | 1

    }

    def 'test round up'() {
        expect:
        round == ChaiUtils.roundUpward(value, nearest)
        where:
        round | value | nearest
        100   | 20    | 100
        0     | 0     | 100
        200   | 120   | 100
        2     | 1.2d  | 1

    }

    def 'test day diff uses absolute date'() {

        def now = new Date()

        when:
        def days = ChaiUtils.dayDiff(now, now + 1)
        then:
        days == 1



        when:
        def atMidnight = new GregorianCalendar()
        atMidnight.setTime(now + 1)
        atMidnight.set(Calendar.HOUR_OF_DAY, 0)
        atMidnight.set(Calendar.MINUTE, 0)
        days = ChaiUtils.dayDiff(now, atMidnight.time)
        then:
        days == 1


    }
}
