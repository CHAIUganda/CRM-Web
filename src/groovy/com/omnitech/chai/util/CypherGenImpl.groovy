package com.omnitech.chai.util

import groovy.transform.CompileStatic
import org.neo4j.graphdb.Direction
import org.springframework.data.neo4j.annotation.NodeEntity
import org.springframework.data.neo4j.annotation.RelatedTo

import java.lang.reflect.Field

import static com.omnitech.chai.util.ReflectFunctions.findAllPersistentFields

/**
 * Created by kay on 12/1/14.
 */
class CypherGenImpl {

    int nestLevel = Integer.MAX_VALUE
    Map filters = []
    Class aClass

    CypherGenImpl() {
    }

    CypherGenImpl(Class aClass) {
        this.aClass = aClass
    }

    CypherGenImpl(Class aClass,int nestLevel, List<String> filters ) {
        this.nestLevel = nestLevel
        this.filters = filters
        this.aClass = aClass
    }

    StringBuilder getNonPaginatedQuery() { getPlainQuery(false) }

    StringBuilder getCountQuery() { getPlainQuery(true) }

    StringBuilder getPaginatedQuery(Map params) {
        def cypher = getPlainQuery(false)

        def pageRequest = PageUtils.create(params)

        def sort = pageRequest.sort?.toString()?.replace(':', '')
        cypher << 'order by ' << (sort ? "${getEntityName(aClass)}.$sort" : "${getEntityName(aClass)}.id desc") << '\n'
        cypher << 'skip ' << pageRequest.offset << '\n'
        cypher << 'limit ' << pageRequest.pageSize << '\n'
    }

    StringBuilder getPlainQuery(boolean count) {
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

    private StringBuilder getOptionalMatchStatement(Class aClass) {
        def cypher = new StringBuilder()
        findNodeFields(aClass)?.each {
            visitNodeFields(aClass, getEntityName(aClass), it, 0) { Class enclosingClass, String fieldNameForEnclosingClass, Field right ->
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


    private StringBuilder getWithStatement(Class aClass) {
        def entityName = getEntityName(aClass)
        def nodes = [] << entityName
        findNodeFields(aClass).each {
            visitNodeFields(aClass, entityName, it, 0) { Class enclosingClass, String fieldNameForEnclosingClass, Field right ->
                nodes << getEntityName(enclosingClass, right, fieldNameForEnclosingClass)
            }
        }
        return new StringBuilder() << 'WITH ' << nodes.unique().join(',') << '\n'
    }

    private String getFilterQuery(Class aClass) {
        def entityName = getEntityName(aClass)
        def filterFields = findAllFilterFields(aClass, entityName)
        findNodeFields(aClass).each {
            visitNodeFields(aClass, entityName, it, 0) { Class enclosingClass, String fieldNameForEnclosingClass, Field right ->
                filterFields.addAll(findAllFilterFields(right.type, getEntityName(enclosingClass, right, fieldNameForEnclosingClass)))
            }
        }
        return filterFields.collect { "$it =~ {search}" }.join(' or ')
    }

    private static List<String> findAllFilterFields(Class aClass, String nodeName) {
        findAllPersistentFields(aClass).findAll { isSearchAbleField(it) }.collect {
            createToStringFunction(it, nodeName)
        }
    }


    private void visitNodeFields(Class enclosingClass, String fieldNameForEnclosingClass, Field right, int visitedGrannies, Closure visitor) {

        if (visitedGrannies >= this.nestLevel) return

        def fieldNodeName = getEntityName(enclosingClass, right, fieldNameForEnclosingClass)
        visitor(enclosingClass, fieldNameForEnclosingClass, right)

        //todo for now we do not support recursive references hence the { it.type != left }

        def nodes = findNodeFields(right.type).findAll { it.type != enclosingClass }
        nodes?.each {
            visitNodeFields(right.type, fieldNodeName, it, visitedGrannies + 1, visitor)
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

    private static isSearchAbleField(Field field) {
        CharSequence.isAssignableFrom(field.type) || Number.isAssignableFrom(field.type)
    }


}