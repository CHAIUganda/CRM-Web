package com.omnitech.chai.util

import com.omnitech.chai.model.Order
import spock.lang.Specification

/**
 * Created by kay on 3/11/2015.
 */
class ExportUtilTest extends Specification {

    def "FixDates"() {

        def longDate = Date.parse('yyyy-MM-dd', '2014-02-20').time

        def csv = [
                ['NAME', 'DUE DATE'],
                ['Ronald', longDate],
                ['Ronald', null],
                ['Ronald', "dsdsd"],
        ]

        when:
        def fixed = ExportUtil.fixDates(Order, csv)

        then:
        assert fixed.toString() == '[[NAME, DUE DATE], [Ronald, 2014-02-20 12:00:00], [Ronald, null], [Ronald, dsdsd]]'


    }
}
