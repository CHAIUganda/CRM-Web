package com.omnitech.chai.migrations

import groovy.transform.ToString

/**
 * Created by kay on 3/12/2015.
 */
class MigrationDSL {

    ChangeSet currentChangeSet


    List<ChangeSet> changeSets = []

    static def make(Closure closure) {
        MigrationDSL dsl = new MigrationDSL()
        closure.delegate = dsl
        closure()
        return dsl.changeSets
    }

    def changeSet(Map params, Closure configure) {
        currentChangeSet = new ChangeSet(id: params.id, description: params.desc)
        configure.delegate = this

        assert currentChangeSet.id, "Id Is Required on A Change Set"

        assert changeSets.every {
            it.id != currentChangeSet.id
        }, "You Cannot Have A Change Set With The Same Id ${currentChangeSet.id}"

        configure()

        assert currentChangeSet.updateCypher || currentChangeSet.updateClosure, "Changeset $currentChangeSet.id has no update cypher"

        changeSets << currentChangeSet
    }

    def test(String cypher) {
        currentChangeSet.testCypher = cypher
    }

    def test(Closure closure) {
        currentChangeSet.testClosure = closure
    }

    def update(String cypher) {
        currentChangeSet.updateCypher = cypher
    }

    def update(Closure closure) {
        currentChangeSet.updateClosure = closure
    }

}

@ToString(includes = ["id", "description"],includePackage = false,ignoreNulls = true)
class ChangeSet {

    Closure testClosure
    Closure updateClosure

    String testCypher
    String updateCypher
    String id
    String description
}
