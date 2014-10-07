package com.omnitech.chai.util

import com.omnitech.chai.model.AbstractEntity
import org.apache.commons.logging.LogFactory
import org.grails.databinding.SimpleDataBinder
import org.grails.databinding.SimpleMapDataBindingSource
import org.neo4j.graphdb.Direction
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.neo4j.annotation.NodeEntity
import org.springframework.data.neo4j.annotation.RelatedTo
import org.springframework.data.neo4j.repository.GraphRepository

import java.lang.reflect.Field

import static org.neo4j.cypherdsl.CypherQuery.match
import static org.neo4j.cypherdsl.CypherQuery.node

/**
 * Created by kay on 9/24/14.
 */
class ModelFunctions {

    private static def log = LogFactory.getLog(ModelFunctions.class)


    static Long extractId(Map params,String idField = 'id') {
        Long id = -1
        try {
            id = (params[idField] as Long) ?: -1
        } catch (Exception x) {
        }
        return id
    }

    static <T> T bind(def obj, Map properties, boolean copyMetaInfo = false) {
        SimpleDataBinder binder = new SimpleDataBinder();
        if (copyMetaInfo) {
            binder.bind(obj, new SimpleMapDataBindingSource(properties))
        } else {
            def whiteList = properties.keySet() as List
            whiteList.remove('uuid')
            whiteList.remove('lastUpdated')
            whiteList.remove('dateCreated')
            binder.bind(obj, new SimpleMapDataBindingSource(properties), whiteList)
        }
        return (T) obj
    }

    static def setPropertyIfNull(Object object, String propertyName, def value) {
        if (object?.hasProperty(propertyName)) {
            if (!object.getAt(propertyName)) {
                object."$propertyName" = value
            }
        }
        return object
    }

    static <T extends AbstractEntity> T saveEntity(GraphRepository<T> repo, T entity) {
        saveEntity(repo, entity, null)
    }

    static <T extends AbstractEntity> T saveEntity(GraphRepository<T> repo, T entity, Closure beforeBind) {
        def neoEntity = entity
        if (entity.id) {
            def tempNeoEntity = repo.findOne(entity.id)
            if (tempNeoEntity) {
                beforeBind?.call(tempNeoEntity)
                neoEntity = tempNeoEntity
                bind(neoEntity, entity.properties)
            }
        }
        repo.save(neoEntity)
    }

    static <T> Page<T> listAll(GraphRepository<T> repo, Map params) {
        def request = PageUtils.create(params)
        new PageImpl<T>(repo.findAll(request).content, request, repo.count())
    }

    static def setProperty(Object object, String propertyName, def value) {
        if (object?.hasProperty(propertyName)) {
            object."$propertyName" = value
        }
        return object
    }

    /**
     match (c:Customer)
     optional match (c) --> (s:SubCounty)
     optional match (s) <-- (d:District)
     return c,s,d
     */

    static String getMatchStatement2(Class aClass) {
        def match = new StringBuilder() << "${getMatchStatement(aClass)}" << "\n"
        match << 'where ' << getFilterQuery(aClass) << '\n'

        def nodes = ReflectFunctions.findNodeFields(aClass)
        if (!nodes) return match

        nodes.each { match << getMatchStatement(aClass, it) }

        match
    }


    static String getMatchStatement(Class left, Field right) {
        def direction = getAssocArrow(right)
        def fieldTypeName = right.type.simpleName
        def query = new StringBuilder()

        query << " match (${left.simpleName.toLowerCase()})$direction(${fieldTypeName.toLowerCase()}:${fieldTypeName})" << "\n"
        query << 'where ' << getFilterQuery(right.type) << '\n'

        def nodes = ReflectFunctions.findNodeFields(right.type)

        if (!nodes) return query
        nodes.each {
            query << getMatchStatement(right.type, it) << '\n'
        }
        return query
    }

    static String getAssocArrow(Field field) {
        if (field.isAnnotationPresent(RelatedTo)) {
            def annotation = field.getAnnotation(RelatedTo)
            switch (annotation.direction()) {
                case Direction.INCOMING: return '<--'
                case Direction.BOTH: return '--'
            }
        }
        return '-->'
    }

    static String getMatchStatement(Class aClass) {
        "MATCH (${aClass.simpleName.toLowerCase()}:${aClass.simpleName})"
    }


    static String getFilterQuery(Class aClass) {
        def className = aClass.simpleName.toLowerCase()

        def fieldNames = ReflectFunctions.findAllPersistentFields(aClass).findAll {
            !it.type.isAnnotationPresent(NodeEntity)
        }*.name

        def condition = fieldNames.collect { "${className}.${it} = '{search}'" }.join(' or ')

        return condition

    }
}
