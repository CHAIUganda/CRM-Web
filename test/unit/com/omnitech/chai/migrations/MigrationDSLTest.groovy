package com.omnitech.chai.migrations

import spock.lang.Specification

/**
 * Created by kay on 3/12/2015.
 */
class MigrationDSLTest extends Specification {

    def "test make"() {

        when:
        def changes = MigrationDSL.make {
            changeSet(id: 'some Id', desc: 'Will Clean all crap') {
                test 'match(t:Task) return t'
                update 'create task that is funny'
            }
            changeSet(id: 'some Id2', desc: 'Will Clean all crap2') {
                test 'match(t:Task) return t2'
                update 'create task that is funny2'
            }
        }

        then:
        changes.size() == 2
        changes[0].id == 'some Id'
        changes[0].description == 'Will Clean all crap'
        changes[0].testCypher == 'match(t:Task) return t'
        changes[0].updateCypher == 'create task that is funny'

        changes[1].id == 'some Id2'
        changes[1].description == 'Will Clean all crap2'
        changes[1].testCypher == 'match(t:Task) return t2'
        changes[1].updateCypher == 'create task that is funny2'

    }

    def "test duplicates are rejected"() {

        when:
        MigrationDSL.make {
            changeSet(id: 'some Id', desc: 'Will Clean all crap') {
                test 'match(t:Task) return t'
                update 'create task that is funny'
            }
            changeSet(id: 'some Id', desc: 'Will Clean all crap2') {
                test 'match(t:Task) return t2'
                update 'create task that is funny2'
            }
        }

        then:
        thrown(AssertionError)

    }

    def "test blank ids are rejected"() {

        when:
        MigrationDSL.make {
            changeSet(desc: 'Will Clean all crap') {
                test 'match(t:Task) return t'
                update 'create task that is funny'
            }
        }

        then:
        thrown(AssertionError)

    }

    def "test blank updates are rejected"() {

        when:
        MigrationDSL.make {
            changeSet(id: 'some id', desc: 'Will Clean all crap') {
                test 'match(t:Task) return t'
            }
        }

        then:
        thrown(AssertionError)

    }

    def "test update closure"() {

        when:
        def changes = MigrationDSL.make {
            changeSet(id: 'some id', desc: 'Will Clean all crap') {
                test {
                    "testClosure"
                }
                update {
                    "updateClosure"
                }

                test 'dsd'
                update 'ldksdl'
            }
        }

        then:
        changes[0].id == 'some id'
        changes[0].description == 'Will Clean all crap'
        changes[0].testCypher == 'dsd'
        changes[0].updateCypher == 'ldksdl'
        changes[0].updateClosure() == "updateClosure"
        changes[0].testClosure() == "testClosure"

    }

}
