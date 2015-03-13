package com.omnitech.chai.crm

import com.omnitech.chai.migrations.ChangeSet
import com.omnitech.chai.model.DbChangeSet
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.neo4j.support.Neo4jTemplate
import org.springframework.data.neo4j.transaction.Neo4jTransactional

import static java.util.Collections.EMPTY_MAP

/**
 * Created by kay on 3/12/2015.
 */
@Neo4jTransactional
class MigrationService {

    def dbChangeSetRepository
    @Autowired
    Neo4jTemplate neo

    def runMigration(List<ChangeSet> changesets) {
        changesets.each {
            def dbChangeSet = execute(it)
            if (dbChangeSet)
                dbChangeSetRepository.save(dbChangeSet)
        }
    }

    def execute(ChangeSet changeSet) {
        def dbChange = toDbChangSet(changeSet)
        def neoChangeSet = dbChangeSetRepository.findByChangeId(dbChange.changeId)
        if (neoChangeSet) {
            log.debug("Ignoring $changeSet cause it already exists in Db")
            return null
        }

        def closureResult = changeSet.testClosure?.call() ?: true
        if (test(changeSet.testCypher) && closureResult) {
            log.info("Changeset: Executing: $changeSet")
            if (changeSet.testCypher)
                neo.query(changeSet.updateCypher, EMPTY_MAP)
            changeSet.updateClosure?.call()
        }

        return dbChange

    }

    def test(String cypher) {
        if (!cypher) {
            return true
        }
        def result = neo.query(cypher, EMPTY_MAP).singleOrNull()

        return result['answer'] == true
    }

    DbChangeSet toDbChangSet(ChangeSet cs) {
        return new DbChangeSet(changeId: cs.id, description: cs.description, cypher: cs.updateCypher)
    }

}
