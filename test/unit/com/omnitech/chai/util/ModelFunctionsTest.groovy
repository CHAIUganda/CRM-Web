package com.omnitech.chai.util

import com.omnitech.chai.model.*
import spock.lang.Specification

import static com.omnitech.chai.util.ModelFunctions.getSearchQuery

/**
 * Created by kay on 9/29/14.
 */
class ModelFunctionsTest extends Specification {
    def "ExtractId"() {
        expect:
        ModelFunctions.extractId(params) == rt
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
        ModelFunctions.bind(copy, u.properties)
        then:
        copy.uuid == null

        when:
        u = new User(username: 'pass', uuid: 'ppppp')
        copy = new User(username: 'dsdsd')
        ModelFunctions.bind(copy, u.properties, true)

        then:
        copy.uuid == u.uuid

    }

    def "SetPropertyIfNull"() {
        when:
        def u = new User(username: 'pass', uuid: 'ppppp')
        then:
        u.password == null

        when: 'a null property is set'
        ModelFunctions.setPropertyIfNull(u, 'password', 'pass1')
        then: 'then the value should be reset'
        u.password == 'pass1'

        when: 'a not null property is set'
        ModelFunctions.setPropertyIfNull(u, 'password', 'XXXXX')
        then: 'then the value should not change'
        u.password == 'pass1'

        when: 'a not null property is sett'
        ModelFunctions.setProperty(u, 'password', 'XXXX')
        then: 'then the value should change'
        u.password == 'XXXX'

        when: 'a non existent property is set'
        ModelFunctions.setPropertyIfNull(u, 'fakeProperty', 'value')
        then: noExceptionThrown()

        when: 'a non existent property is set'
        ModelFunctions.setProperty(u, 'fakeProperty', 'value')
        then: noExceptionThrown()
    }


    def 'test get Assoc arrow'() {

        def field = ReflectFunctions.findAllFields(Customer).find { it.type == SubCounty }

        when:
        def arrow = ModelFunctions.getAssocArrow(field)

        then:
        arrow == '-->'

        when:
        field = ReflectFunctions.findAllFields(District).find { it.type == Region }
        arrow = ModelFunctions.getAssocArrow(field)

        then:
        arrow == '<--'
    }

    def 'test generation'() {
        when:
        def query = ModelFunctions.getMatchStatement2(Customer)

        then:
        notThrown(Exception)
        println query
    }

}
