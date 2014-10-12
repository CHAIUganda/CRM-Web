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
 * Depending on the performance of the DB this class might have to be modified in future so that in searches on only
 * a fee selected properties provided in a white or black list
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

    static StringBuilder getPlainQuery(Class aClass, boolean count) {
        def cypher = new StringBuilder()

        cypher << getMatchStatement(aClass) << '\n'

        findNodeFields(aClass)?.each { cypher << getMatchStatement(aClass, getEntityName(aClass), it) }

        cypher << getWithStatement(aClass)
        cypher << 'WHERE ' << getFilterQuery(aClass) << '\n'

        if (count)
            cypher << 'return count(' << getEntityName(aClass) << ')\n'
        else
            cypher << 'return ' << getEntityName(aClass) << '\n'
    }

    @CompileStatic
    private static String getEntityName(Class enclosingClass, Field field = null) {
        if (field) {
            return "${enclosingClass.simpleName}_${field.name}"
        }
        return enclosingClass.simpleName.toLowerCase()
    }

    static StringBuilder getNonPaginatedQuery(Class aClass) {
        getPlainQuery(aClass, false)
    }

    static StringBuilder getCountQuery(Class aClass) {
        getPlainQuery(aClass, true)
    }

    static StringBuilder getPaginatedQuery(Class aClass, Map params) {
        def cypher = getPlainQuery(aClass, false)

        def pageRequest = PageUtils.create(params)
        cypher << 'order by ' << (pageRequest.sort?.toString() ?: "${getEntityName(aClass)}.id desc") << '\n'
        cypher << 'skip ' << pageRequest.offset << '\n'
        cypher << 'limit ' << pageRequest.pageSize << '\n'
    }

    static StringBuilder getWithStatement(Class aClass) {
        def entityName = getEntityName(aClass)
        def nodes = [] << entityName
        findNodeFields(aClass).each {
            visitNodeFields(aClass, entityName, it) { Class enclosingClass, String fieldNameForEnclosingClass, Field right ->
                nodes << getEntityName(enclosingClass, right)
            }
        }
        def withStatement = new StringBuilder()
        def statement = nodes.unique().join(',')
        withStatement << 'WITH ' << statement << '\n'
        return withStatement
    }

    static String getFilterQuery(Class aClass) {
        def entityName = getEntityName(aClass)
        def filterFields = findAllFilterFields(aClass, entityName)
        findNodeFields(aClass).each {
            visitNodeFields(aClass, entityName, it) { Class enclosingClass, String fieldNameForEnclosingClass, Field right ->
                filterFields.addAll(findAllFilterFields(right.type, getEntityName(enclosingClass, right)))
            }
        }
        return filterFields.collect { "$it =~ {search}" }.join(' or ')
    }

    static List<String> findAllFilterFields(Class aClass, String nodeName) {
        findAllPersistentFields(aClass).findAll { isProcessableField(it) }.collect { createToStringFunction(it,nodeName) }
    }

    static String getMatchStatement(Class enclosingClass, String leftFieldName, Field right) {
        def arrow = getAssocArrow(right)
        def fieldNodeName = getEntityName(enclosingClass, right)

        def query = new StringBuilder()
        query << ' optional match (' << leftFieldName << ')' << arrow << '(' << fieldNodeName << ':' << right.type.simpleName << ')\n'

        //todo for now we do not support recursive references hence the { it.type != left }
        def nodes = findNodeFields(right.type).findAll { it.type != enclosingClass }

        if (!nodes) return query
        nodes.each {
            query << getMatchStatement(right.type, fieldNodeName, it)
        }
        return query
    }

    static void visitNodeFields(Class enclosingClass, String fieldNameForEnclosingClass, Field right, Closure visitor) {
        def fieldNodeName = getEntityName(enclosingClass, right)

        visitor(enclosingClass, fieldNameForEnclosingClass, right)

        //todo for now we do not support recursive references hence the { it.type != left }
        def nodes = findNodeFields(right.type).findAll { it.type != enclosingClass }

        nodes?.each {
            visitNodeFields(right.type, fieldNodeName, it, visitor)
        }
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
        "MATCH (${getEntityName(aClass)}:${aClass.simpleName})"
    }


    private static String createToStringFunction(Field field, nodeName) {
        return Number.isAssignableFrom(field.type) ? "str(${nodeName}.$field.name)" : "${nodeName}.$field.name"
    }

    static List<Field> findNodeFields(Class aClass) {
        findAllPersistentFields(aClass).findAll { it.type.isAnnotationPresent(NodeEntity) }
    }

    static isProcessableField(Field field) {
        CharSequence.isAssignableFrom(field.type) || Number.isAssignableFrom(field.type)
    }

    static def findResultsOnFields(Class aClass, Closure transform) {
        def extractFields = { Class klass ->
            if (klass.isAnnotationPresent(NodeEntity))
                findAllPersistentFields(klass)
            else Collections.EMPTY_LIST
        }
        ReflectFunctions.findResultsOnFields(aClass, transform, extractFields, [])
    }
}
