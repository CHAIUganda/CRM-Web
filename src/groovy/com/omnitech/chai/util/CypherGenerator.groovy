package com.omnitech.chai.util

import groovy.transform.CompileStatic
import org.neo4j.graphdb.Direction
import org.springframework.data.neo4j.annotation.NodeEntity
import org.springframework.data.neo4j.annotation.RelatedTo

import java.lang.reflect.Field

import static com.omnitech.chai.util.ReflectFunctions.findAllPersistentFields

/**
 * A very basic Search Query Generator for traversing simple hierarchies.
 * This generates a cypher query which searches on all properties of the domain object including its children.
 * Depending on the performance of the DB this class might have to be modified in future so that it searches on only
 * a few selected properties provided in a white or black list
 */
class CypherGenerator {


    static StringBuilder getNonPaginatedQuery(Class aClass) {
        new CypherGenImpl(aClass).getPlainQuery(false)
    }

    static StringBuilder getCountQuery(Class aClass) {
        new CypherGenImpl(aClass).getPlainQuery(true)
    }

    static StringBuilder getPaginatedQuery(Class aClass, Map params) {
        new CypherGenImpl(aClass).getPaginatedQuery(params)
    }

}


