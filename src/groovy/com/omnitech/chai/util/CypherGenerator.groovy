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
 * a few selected properties provided in a white or black list
 */
class CypherGenerator {


    static StringBuilder getNonPaginatedQuery(Class aClass) { getPlainQuery(aClass, false) }

    static StringBuilder getCountQuery(Class aClass) { getPlainQuery(aClass, true) }

    static StringBuilder getPaginatedQuery(Class aClass, Map params) {
        def cypher = getPlainQuery(aClass, false)

        def pageRequest = PageUtils.create(params)

        def sort = pageRequest.sort?.toString()?.replace(':', '')
        cypher << 'order by ' << (sort ? "${getEntityName(aClass)}.$sort" : "${getEntityName(aClass)}.id desc") << '\n'
        cypher << 'skip ' << pageRequest.offset << '\n'
        cypher << 'limit ' << pageRequest.pageSize << '\n'
    }

    static StringBuilder getPlainQuery(Class aClass, boolean count) {
        def cypher = new StringBuilder()
        cypher << 'MATCH (' << getEntityName(aClass) << ':' << aClass.simpleName << ')\n'
        cypher << getOptionalMatchStatement(aClass)
        cypher << getWithStatement(aClass)
        cypher << 'WHERE ' << getFilterQuery(aClass) << '\n'

        if (count)
            cypher << 'return count(' << getEntityName(aClass) << ')\n'
        else
            cypher << 'return ' << getEntityName(aClass) << '\n'
    }

    private static StringBuilder getOptionalMatchStatement(Class aClass) {
        def cypher = new StringBuilder()
        findNodeFields(aClass)?.each {
            visitNodeFields(aClass, getEntityName(aClass), it) { Class enclosingClass, String fieldNameForEnclosingClass, Field right ->
                cypher << ' optional match (' << fieldNameForEnclosingClass << ')'
                cypher << getAssocArrow(right)
                cypher << '(' << getEntityName(enclosingClass, right, fieldNameForEnclosingClass) << ':' << right.type.simpleName << ')\n'
            }
        }
        return cypher
    }

    //mainly used by tests
    static boolean inTests = false;

    @CompileStatic
    private static String getEntityName(Class enclosingClass, Field field = null, String nodeReferencing = null) {
        if (field) {
            def longName = "${nodeReferencing}_${enclosingClass.simpleName}_${field.name}"
            def shortName = inTests ? '' : "_${UUID.uniqueId(longName)}"
            return "${enclosingClass.simpleName}_${field.name}$shortName"
        }
        return enclosingClass.simpleName.toLowerCase()
    }


    private static StringBuilder getWithStatement(Class aClass) {
        def entityName = getEntityName(aClass)
        def nodes = [] << entityName
        findNodeFields(aClass).each {
            visitNodeFields(aClass, entityName, it) { Class enclosingClass, String fieldNameForEnclosingClass, Field right ->
                nodes << getEntityName(enclosingClass, right, fieldNameForEnclosingClass)
            }
        }
        return new StringBuilder() << 'WITH ' << nodes.unique().join(',') << '\n'
    }

    private static String getFilterQuery(Class aClass) {
        def entityName = getEntityName(aClass)
        def filterFields = findAllFilterFields(aClass, entityName)
        findNodeFields(aClass).each {
            visitNodeFields(aClass, entityName, it) { Class enclosingClass, String fieldNameForEnclosingClass, Field right ->
                filterFields.addAll(findAllFilterFields(right.type, getEntityName(enclosingClass, right, fieldNameForEnclosingClass)))
            }
        }
        return filterFields.collect { "$it =~ {search}" }.join(' or ')
    }

    private static List<String> findAllFilterFields(Class aClass, String nodeName) {
        findAllPersistentFields(aClass).findAll { isProcessableField(it) }.collect {
            createToStringFunction(it, nodeName)
        }
    }

    private
    static void visitNodeFields(Class enclosingClass, String fieldNameForEnclosingClass, Field right, Closure visitor) {
        def fieldNodeName = getEntityName(enclosingClass, right, fieldNameForEnclosingClass)
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
        def type = annotation.type()
        switch (annotation.direction()) {
            case Direction.INCOMING: return "<-[:$type]-"
            case Direction.BOTH: return "-[:$type]-"
            default: return "-[:$type]->"
        }
    }


    private static String createToStringFunction(Field field, nodeName) {
        return Number.isAssignableFrom(field.type) ? "str(${nodeName}.$field.name)" : "${nodeName}.$field.name"
    }

    static List<Field> findNodeFields(Class aClass) {
        findAllPersistentFields(aClass).findAll { it.type.isAnnotationPresent(NodeEntity) }
    }

    private static isProcessableField(Field field) {
        CharSequence.isAssignableFrom(field.type) || Number.isAssignableFrom(field.type)
    }

}
