package com.omnitech.chai.util

import spock.lang.Specification

/**
 * Created by kay on 10/18/14.
 */
class GroupFlattenerTest extends Specification {
    def "Normalize"() {
        String.metaClass.patchUp = { Number max ->
            return ChaiUtils.truncateString(delegate, max).padRight(max)
        }

        def r1 = new SomeLeaf(id: 'u1', name: 'u1')
        def r2 = new SomeLeaf(id: 'u2', name: 'u2')
        def r3 = new SomeLeaf(id: 'p3', name: 'p3')
        def r4 = new SomeLeaf(id: 'p4', name: 'p4')
        def r5 = new SomeLeaf(id: 't5', name: 't5')
        def r6 = new SomeLeaf(id: 't6', name: 't6')
        def r11 = new SomeLeaf(id: 'ext1', name: 'ext1')
        def r22 = new SomeLeaf(id: 'ext2', name: 'ext2')

        def userGroup = new SomeGroup(id: 'otherusers', name: 'Other Users')
        [r1, r2]*.setParent(userGroup)


        def permanentGroup = new SomeGroup(name: 'Permanent', id: 'perm')
        [r3, r4]*.setParent(permanentGroup)

        def tempGroup = new SomeGroup(name: 'Temporary Staff', id: 'temp')
        [r5, r6]*.setParent(tempGroup)


        def employees = new SomeGroup(name: 'Employees', id: 'employees')
        [permanentGroup, tempGroup]*.setParent(employees)

        def reports = new SomeGroup(name: 'Over All', id: 'sd')
        [userGroup, employees]*.setParent(reports)

        def userGroup1 = new SomeGroup(id: 'External', name: 'External')
        [r11, r22]*.setParent(userGroup1)


        def normalizer = new GroupFlattener(leaves: [r5, r2, r6, r11, r3, r4, r1, r22]);
        when:
        def records = normalizer.normalize()
//        records.each { println it.toRowString() }

        then:
        records.collect { it.name } == 'Over All\nEmployees\nTemporary Staff\nt5\nt6\nPermanent\np3\np4\nOther Users\nu2\nu1\nExternal\next1\next2'.split('\n')*.trim()
    }
}

class SomeGroup implements GroupNode {
    String id
    String name
    SomeGroup parent
}

class SomeLeaf implements LeafNode {
    String id
    String name
    SomeGroup parent

}


