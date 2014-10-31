package com.omnitech.chai.util

import com.omnitech.chai.model.AbstractEntity
import com.omnitech.chai.model.Customer
import org.apache.commons.logging.LogFactory
import org.grails.databinding.SimpleDataBinder
import org.grails.databinding.SimpleMapDataBindingSource
import org.neo4j.cypherdsl.grammar.ReturnNext
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.neo4j.conversion.Result
import org.springframework.data.neo4j.repository.CypherDslRepository
import org.springframework.data.neo4j.repository.GraphRepository
import org.springframework.data.neo4j.support.Neo4jTemplate

import java.util.regex.Pattern

/**
 * Created by kay on 9/24/14.
 */
class ModelFunctions {

    private static def log = LogFactory.getLog(ModelFunctions.class)


    static Long extractId(Map params, String idField = 'id') {
        Long id = -1
        try {
            id = (params[idField] as Long) ?: -1
        } catch (Exception x) {
        }
        return id
    }

    static <T> T bind(T obj, Map properties, List whiteList, List blackList) {
        SimpleDataBinder binder = new SimpleDataBinder();
        binder.bind(obj, new SimpleMapDataBindingSource(properties), whiteList, blackList)
        return obj
    }

    static <T> T bind(T obj, Map properties, boolean copyMetaInfo = false) {
        if (copyMetaInfo) {
            SimpleDataBinder binder = new SimpleDataBinder();
            binder.bind(obj, new SimpleMapDataBindingSource(properties))
        } else {
            bind(obj, properties, null, ['uuid','lastUpdated','dateCreated'])
        }
        return obj
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

    static <T> Page<T> searchAll(Neo4jTemplate neo, Class<T> aClass, String search, Map params) {
        def query = CypherGenerator.getPaginatedQuery(aClass, params).toString()
        def count = CypherGenerator.getCountQuery(aClass).toString()
        def size = neo.query(count, [search: search]).to(Long).single()
        def data = neo.query(query, [search: search]).to(aClass).as(List).collect()
        return new PageImpl<Customer>(data, PageUtils.create(params), size) as Page<T>
    }

    static <T> Result<T> query(CypherDslRepository<T> repo, ReturnNext execute, Map params, Class container) {
        PageUtils.addPagination(execute, params, container)
        return repo.query(execute, Collections.EMPTY_MAP)
    }


    static String getWildCardRegex(String search) { "(?i).*${Pattern.quote(search)}.*".toString() }

    static def setProperty(Object object, String propertyName, def value) {
        if (object?.hasProperty(propertyName)) {
            object."$propertyName" = value
        }
        return object
    }


}
