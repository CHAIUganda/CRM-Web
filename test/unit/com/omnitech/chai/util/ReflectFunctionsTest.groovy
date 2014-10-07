package com.omnitech.chai.util

import org.springframework.data.annotation.Transient
import org.springframework.data.neo4j.annotation.NodeEntity
import spock.lang.Specification

import static com.omnitech.chai.util.ReflectFunctions.findAllFieldSearchableFields
import static com.omnitech.chai.util.ReflectFunctions.findAllPersistentFields

/**
 * Created by kay on 10/6/14.
 */
class ReflectFunctionsTest extends Specification {

    def "FindAllFields"() {

        when:
        def rs = findAllPersistentFields(Foo)
        then:
        rs.size() == 2
        rs*.name == ['name', 'id']

        when:
        rs = findAllPersistentFields(FooBar)
        then:
        rs.size() == 4
        rs*.name == ['description', 'foo', 'name', 'id']
    }

    def 'test find annotation on concrete class'() {
        when:
        def cc = ReflectFunctions.findConcreteClassWithAnnotaion(FooBar,NodeEntity)

        then:
        cc == Foo
    }
}

@NodeEntity
class Foo {
    String name
    String id
    @Transient
    String transientName
    transient String transientName2
    Set<String> strings
    static String someStatic
}

class FooBar extends Foo {
    String description
    Foo foo
    transient String transientName3

    String getFooBar() { "" }
}


