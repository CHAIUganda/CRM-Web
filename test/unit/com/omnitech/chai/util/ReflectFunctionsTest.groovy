package com.omnitech.chai.util

import com.omnitech.chai.model.DetailerTask
import com.omnitech.chai.model.Task
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

    def 'test get class hierachy'() {
        when:
        def h = ReflectFunctions.getClassHierarchy(FooBar)

        then:
        h.size() == 2
        h*.simpleName == ['FooBar', 'Foo']

        when:
        h = ReflectFunctions.getClassHierarchy(Foo)

        then:
        h.size() == 1
        h*.simpleName == ['Foo']
    }

    def 'test get concrete annotation class'() {
        when:
        def results = ReflectFunctions.findAllClassesWithAnnotation(DetailerTask, NodeEntity)

        then:
        [DetailerTask, Task].every { results.contains(it) }
    }

    def 'test find all basic types'() {
        when:
        def result = ReflectFunctions.findAllBasicFields(FooBar)

        then:
        result == ['description', 'name', 'id']
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




