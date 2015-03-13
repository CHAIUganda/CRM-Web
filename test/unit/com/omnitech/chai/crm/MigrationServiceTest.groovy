package com.omnitech.chai.crm

import com.omnitech.chai.migrations.MigrationDSL
import com.omnitech.chai.model.DbChangeSet
import com.omnitech.chai.repositories.DbChangeSetRepository
import org.springframework.data.neo4j.conversion.Result
import org.springframework.data.neo4j.support.Neo4jTemplate
import spock.lang.Specification

import static java.util.Collections.EMPTY_MAP

/**
 * Created by kay on 3/13/2015.
 */
class MigrationServiceTest extends Specification {

    MigrationService service = new MigrationService()

    def "test run migrations"() {

        def neo = service.neo = Mock(Neo4jTemplate)
        def repo = service.dbChangeSetRepository = Mock(DbChangeSetRepository)

        Result result = Mock()

        def changeSets = MigrationDSL.make {
            changeSet(id: 'my change', desc: 'test migration') {
                test 'test'
                update 'update'
            }
        }

        when:
        service.runMigration(changeSets)

        then:
        1 * repo.findByChangeId('my change') >> null
        1 * neo.query('test', EMPTY_MAP) >> result
        1 * result.singleOrNull() >> [answer: true]
        1 * neo.query('update', EMPTY_MAP)
        1 * repo.save({ DbChangeSet c ->
            c.cypher == 'update' && c.description == 'test migration' && c.changeId == "my change"
        })
    }

    def "test migration with closure and script"() {

        def neo = service.neo = Mock(Neo4jTemplate)
        def repo = service.dbChangeSetRepository = Mock(DbChangeSetRepository)

        Result result = Mock()

        def testClosureExecuted = false
        def testClosure = {
            testClosureExecuted = true
            true
        }

        def updateClosureExecuted = false
        def updateClosure = {
            updateClosureExecuted = true
        }

        def changeSets = MigrationDSL.make {
            changeSet(id: 'my change', desc: 'test migration') {
                test 'test'
                test testClosure
                update 'update'
                update updateClosure
            }
        }

        when:
        service.runMigration(changeSets)

        then:
        1 * repo.findByChangeId('my change') >> null
        1 * neo.query('test', EMPTY_MAP) >> result
        1 * result.singleOrNull() >> [answer: true]
        1 * neo.query('update', EMPTY_MAP)
        1 * repo.save({ DbChangeSet c ->
            c.cypher == 'update' && c.description == 'test migration' && c.changeId == "my change"
        })
        assert testClosureExecuted
        assert updateClosureExecuted
    }

    def "test migration with only closure"() {

        def neo = service.neo = Mock(Neo4jTemplate)
        def repo = service.dbChangeSetRepository = Mock(DbChangeSetRepository)

        def testClosureExecuted = false
        def testClosure = {
            testClosureExecuted = true
            true
        }

        def updateClosureExecuted = false
        def updateClosure = {
            updateClosureExecuted = true
        }

        def changeSets = MigrationDSL.make {
            changeSet(id: 'my change', desc: 'test migration') {
                test testClosure
                update updateClosure
            }
        }

        when:
        service.runMigration(changeSets)

        then:
        1 * repo.findByChangeId('my change') >> null
        0 * neo.query(_, _)
        1 * repo.save({ DbChangeSet c ->
            c.cypher == null && c.description == 'test migration' && c.changeId == "my change"
        })
        assert testClosureExecuted
        assert updateClosureExecuted
    }

    def "test migrations do not run if they already exist with script"() {

        def neo = service.neo = Mock(Neo4jTemplate)
        def repo = service.dbChangeSetRepository = Mock(DbChangeSetRepository)

        def testClosureExecuted = false
        def testClosure = {
            testClosureExecuted = true
            true
        }

        def updateClosureExecuted = false
        def updateClosure = {
            updateClosureExecuted = true
        }

        def changeSets = MigrationDSL.make {
            changeSet(id: 'my change', desc: 'test migration') {
                test testClosure
                test 'test'
                update 'update'
                update updateClosure
            }
        }

        when:
        service.runMigration(changeSets)

        then:
        1 * repo.findByChangeId('my change') >> new DbChangeSet()
        0 * neo.query('test', EMPTY_MAP)
        0 * neo.query('update', EMPTY_MAP)
        0 * repo.save(_)
        assert !testClosureExecuted
        assert !updateClosureExecuted

    }

    def "test migrations do not run if test fails with closure"() {
        def neo = service.neo = Mock(Neo4jTemplate)
        def repo = service.dbChangeSetRepository = Mock(DbChangeSetRepository)

        def testClosureExecuted = false
        def testClosure = {
            testClosureExecuted = true
            false
        }

        def updateClosureExecuted = false
        def updateClosure = {
            updateClosureExecuted = true
        }

        def changeSets = MigrationDSL.make {
            changeSet(id: 'my change', desc: 'test migration') {
                test testClosure
                update 'update'
                update updateClosure
            }
        }

        when:
        service.runMigration(changeSets)

        then:
        1 * repo.findByChangeId('my change') >> new DbChangeSet()
        0 * neo.query(_, EMPTY_MAP)
        0 * neo.query('update', EMPTY_MAP)
        0 * repo.save(_)
        assert !testClosureExecuted
        assert !updateClosureExecuted
    }

    def "test migrations do not run if test fails with closure and script"() {
        def neo = service.neo = Mock(Neo4jTemplate)
        def repo = service.dbChangeSetRepository = Mock(DbChangeSetRepository)

        Result result = Mock()

        def testClosureExecuted = false
        def testClosure = {
            testClosureExecuted = true
            false
        }

        def updateClosureExecuted = false
        def updateClosure = {
            updateClosureExecuted = true
        }

        def changeSets = MigrationDSL.make {
            changeSet(id: 'my change', desc: 'test migration') {
                test testClosure
                test 'test'
                update 'update'
                update updateClosure
            }
        }

        when:
        service.runMigration(changeSets)

        then:
        1 * repo.findByChangeId('my change') >> null
        1 * neo.query('test', EMPTY_MAP) >> result
        1 * result.singleOrNull() >> [answer: false]
        0 * neo.query('update', EMPTY_MAP)
        1 * repo.save({ DbChangeSet c ->
            c.cypher == 'update' && c.description == 'test migration' && c.changeId == "my change"
        })
        assert !updateClosureExecuted
    }

    def "test migrations do not run run if test fails with script true and closure false"() {
        def neo = service.neo = Mock(Neo4jTemplate)
        def repo = service.dbChangeSetRepository = Mock(DbChangeSetRepository)

        Result result = Mock()

        def testClosureExecuted = false
        def testClosure = {
            testClosureExecuted = true
            false
        }

        def updateClosureExecuted = false
        def updateClosure = {
            updateClosureExecuted = true
        }

        def changeSets = MigrationDSL.make {
            changeSet(id: 'my change', desc: 'test migration') {
                test testClosure
                test 'test'
                update 'update'
                update updateClosure
            }
        }

        when:
        service.runMigration(changeSets)

        then:
        1 * repo.findByChangeId('my change') >> null
        1 * neo.query('test', EMPTY_MAP) >> result
        1 * result.singleOrNull() >> [answer: false]
        0 * neo.query('update', EMPTY_MAP)
        1 * repo.save({ DbChangeSet c ->
            c.cypher == 'update' && c.description == 'test migration' && c.changeId == "my change"
        })
        assert !updateClosureExecuted
        assert testClosureExecuted
    }

}
