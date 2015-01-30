package com.omnitech.chai.util

import com.omnitech.chai.model.DetailerTask
import com.omnitech.chai.model.User
import org.neo4j.graphdb.DynamicLabel
import org.springframework.data.neo4j.support.Neo4jTemplate
import spock.lang.Specification

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

        when: 'leave uuid intact is true and uuid is set do not alter the uuid'
        u.uuid = 'xxPP'
        u.denyUuidAlter()
        ModelFunctions.setPropertyIfNull(u,'uuid','NewValue')
        then:
        u.uuid == 'xxPP'

        when: 'leave uuid intact is true and uuid is null alter the uuid'
        u.allowUuidAlter()
        u.uuid = null
        u.denyUuidAlter()
        ModelFunctions.setPropertyIfNull(u,'uuid','NewValue')
        then:
        u.uuid == 'NewValue'

    }

    def 'test addDynamicLabels() adds Labels when a node exists in the DB'() {
        Neo4jTemplate neo = Mock()
        org.neo4j.graphdb.Node node = Mock()

        DetailerTask task = new DetailerTask(id: 787)

        when:
        def rt = ModelFunctions.addInheritanceLabelToNode(neo, task)

        then:
        1 * neo.getNode(787) >> node
        1 * node.getLabels() >> [DynamicLabel.label('Task')]
        1 * node.addLabel(DynamicLabel.label(DetailerTask.simpleName))
        task.is(rt)
    }

    def 'test addDynamicLabels() adds Labels Plus Concrete labels when a node exists in the DB'() {
        Neo4jTemplate neo = Mock()
        org.neo4j.graphdb.Node node = Mock()

        DetailerTask task = new DetailerTask(id: 787)

        when:
        def rt = ModelFunctions.addInheritanceLabelToNode(neo, task)

        then:
        1 * neo.getNode(787) >> node
        1 * node.getLabels() >> [DynamicLabel.label('Task'),DynamicLabel.label('_Task')]
        1 * node.addLabel(DynamicLabel.label(DetailerTask.simpleName))
        1 * node.addLabel(DynamicLabel.label('_DetailerTask'))
        1 * node.removeLabel(DynamicLabel.label('_Task'))
        task.is(rt)
    }


    def 'test addDynamicLabels() doest not add Labels if node does not exist in DB'() {
        Neo4jTemplate neo = Mock()
        org.neo4j.graphdb.Node node = Mock()

        DetailerTask task = new DetailerTask(id: 787)

        when:
        def rt = ModelFunctions.addInheritanceLabelToNode(neo, task)

        then:
        neo.getNode(787) >> null
        0 * node.getLabels() >> [DynamicLabel.label('Task')]
        0 * node.addLabel(DynamicLabel.label(DetailerTask.simpleName))
        task.is(rt)
    }

    def 'test addDynamicLabels() doest not add Labels if there are no Multiple Node Entities'() {
        Neo4jTemplate neo = Mock()
        org.neo4j.graphdb.Node node = Mock()

        User user = new User(id: 787)

        when:
        def rt = ModelFunctions.addInheritanceLabelToNode(neo, user)

        then:
        0 * neo.getNode(787)
        0 * node.getLabels() >> [DynamicLabel.label('Task')]
        0 * node.addLabel(DynamicLabel.label(DetailerTask.simpleName))
        user.is(rt)
    }


}
