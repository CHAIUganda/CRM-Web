package com.omnitech.chai.util

import com.omnitech.chai.model.User
import spock.lang.Specification

/**
 * Created by kay on 9/29/14.
 */
class ChaiUtilsTest extends Specification {
    def "ExtractId"() {
        expect:
        ChaiUtils.extractId(params) == rt
        where:
        params    | rt
        [id: '3'] | 3
        [id: 3]   | 3
        [:]       | -1
        [id: 'd'] | -1
    }

    def "Bind"() {
        when:
        def u = new User(username: 'pass', uuid: 'ppppp')
        def copy = new User(username: 'dsdsd')
        ChaiUtils.bind(copy, u.properties)
        then:
        copy.uuid == null

        when:
        u = new User(username: 'pass', uuid: 'ppppp')
        copy = new User(username: 'dsdsd')
        ChaiUtils.bind(copy, u.properties, true)

        then:
        copy.uuid == u.uuid

    }

    def "SetPropertyIfNull"() {
        when:
        def u = new User(username: 'pass', uuid: 'ppppp')
        then:
        u.password == null

        when: 'a null property is set'
        ChaiUtils.setPropertyIfNull(u, 'password', 'pass1')
        then: 'then the value should be reset'
        u.password == 'pass1'

        when: 'a not null property is set'
        ChaiUtils.setPropertyIfNull(u, 'password', 'XXXXX')
        then: 'then the value should not change'
        u.password == 'pass1'

        when: 'a not null property is sett'
        ChaiUtils.setProperty(u, 'password', 'XXXX')
        then: 'then the value should change'
        u.password == 'XXXX'

        when: 'a non existent property is set'
        ChaiUtils.setPropertyIfNull(u, 'fakeProperty', 'value')
        then: noExceptionThrown()

        when: 'a non existent property is set'
        ChaiUtils.setProperty(u, 'fakeProperty', 'value')
        then: noExceptionThrown()
    }

}
