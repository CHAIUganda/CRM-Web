package com.omnitech.chai.util

import org.neo4j.graphdb.Direction
import org.springframework.data.neo4j.annotation.RelatedTo

import java.lang.reflect.Field

import static com.omnitech.chai.util.ReflectFunctions.findAllPersistentFields
import static com.omnitech.chai.util.ReflectFunctions.findNodeFields

/**
 * A very basic Search Query Generator
 */
class CypherGenerator {

    Class aClass

    CypherGenerator() {}

    CypherGenerator(Class aClass) {
        this.aClass = aClass
    }

    /**
     match (c:Customer)
     optional match (c) --> (s:SubCounty)
     optional match (s) <-- (d:District)
     return c,s,d
     */

    static String getMatchStatement2(Class aClass) {
        def cypher = new StringBuilder()

        cypher << getMatchStatement(aClass) << '\n'

        cypher << 'where ' << getFilterQuery(aClass) << '\n'

        findNodeFields(aClass)?.each { cypher << getMatchStatement(aClass, it) }

        cypher << 'return ' << aClass.simpleName.toLowerCase()
    }


    static String getMatchStatement(Class left, Field right) {
        def arrow = getAssocArrow(right)
        def fieldTypeName = right.type.simpleName

        def query = new StringBuilder()
        query << ' optional match (' << left.simpleName.toLowerCase() << ')' << arrow << '(' << fieldTypeName.toLowerCase() << ':' << fieldTypeName << ')\n'
        query << 'where ' << getFilterQuery(right.type) << '\n'

        def nodes = findNodeFields(right.type)

        if (!nodes) return query
        nodes.each {
            query << getMatchStatement(right.type, it)
        }
        return query
    }

    static String getAssocArrow(Field field) {
        if (!field.isAnnotationPresent(RelatedTo)) return '-->'

        def annotation = field.getAnnotation(RelatedTo)
        switch (annotation.direction()) {
            case Direction.INCOMING: return '<--'
            case Direction.BOTH: return '--'
            default: return '-->'
        }
    }

    static String getMatchStatement(Class aClass) {
        "MATCH (${aClass.simpleName.toLowerCase()}:${aClass.simpleName})"
    }


    static String getFilterQuery(Class aClass) {
        def className = aClass.simpleName.toLowerCase()

        def fieldNames = findAllPersistentFields(aClass).findAll {
            CharSequence.isAssignableFrom(it.type) || Number.isAssignableFrom(it.type)
        }.collect {
            Number.isAssignableFrom(it.type) ? "str(${className}.$it.name)" : "${className}.$it.name"
        }

        def condition = fieldNames.collect { "$it =~ '.*t.*'" }.join(' or ')

        return condition

    }
}
