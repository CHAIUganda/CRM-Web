package com.omnitech.chai.util

import com.omnitech.chai.model.AbstractEntity
import com.omnitech.chai.model.Customer
import groovy.transform.CompileStatic
import groovy.transform.TypeCheckingMode
import org.apache.commons.logging.LogFactory
import org.grails.databinding.SimpleDataBinder
import org.grails.databinding.SimpleMapDataBindingSource
import org.neo4j.cypherdsl.grammar.ReturnNext
import org.neo4j.graphdb.DynamicLabel
import org.neo4j.graphdb.Label
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.neo4j.annotation.NodeEntity
import org.springframework.data.neo4j.conversion.Result
import org.springframework.data.neo4j.repository.CypherDslRepository
import org.springframework.data.neo4j.repository.GraphRepository
import org.springframework.data.neo4j.support.Neo4jTemplate

import java.util.regex.Pattern

/**
 * Created by kay on 9/24/14.
 */
@CompileStatic
class ModelFunctions {

    private static def log = LogFactory.getLog(ModelFunctions.class)
    final public static def META_FIELDS = Collections.unmodifiableList(['uuid', 'lastUpdated', 'dateCreated'])


    static Long extractId(Map params) {
        extractId(params, 'id')
    }

    static Long extractId(Map params, String idField) {
        Long id = -1
        try {
            id = (params[idField] as Long) ?: -1
        } catch (Exception x) {
        }
        return id
    }

    static <T> T createObj(Class<T> obj, Map properties) {
        def instance = obj.newInstance()
        bind(instance,properties)
    }

    static <T> T bind(T obj, Map properties, List whiteList) {
        bind(obj, properties, whiteList, null)
    }

    static <T> T bind(T obj, Map properties, List whiteList, List blackList) {
        SimpleDataBinder binder = new SimpleDataBinder();
        binder.bind(obj, new SimpleMapDataBindingSource(properties), whiteList, blackList)
        return obj
    }

    static <T> T bind(T obj, Map properties, boolean copyMetaInfo = false) {
        if (copyMetaInfo) {
            bind(obj,properties,null,null)
        } else {
            bind(obj, properties, null, META_FIELDS)
        }
        return obj
    }

    @CompileStatic(TypeCheckingMode.SKIP)
    static def setPropertyIfNull(Object object, String propertyName, def value) {
        if (object?.hasProperty(propertyName)) {
            if (!object.getAt(propertyName)) {
                object."$propertyName" = value
            }
        }
        return object
    }

    static <S extends AbstractEntity> S saveEntity(GraphRepository<S> repo, S entity) {
        saveEntity(repo, entity, null)
    }

    static <S extends AbstractEntity> S saveEntity(GraphRepository<S> repo, S entity, Closure beforeBind) {
        S neoEntity = entity
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

    static <T extends AbstractEntity> T saveGenericEntity(Neo4jTemplate repo, T entity) {
        saveGenericEntity(repo, entity, null)
    }

    static <T extends AbstractEntity> T saveGenericEntity(Neo4jTemplate repo, T entity, Closure beforeBind) {
        def neoEntity = entity
        if (entity.id) {
            def tempNeoEntity = repo.findOne(entity.id, entity.getClass())
            if (tempNeoEntity) {
                beforeBind?.call(tempNeoEntity)
                neoEntity = tempNeoEntity
                addInheritanceLabelToNode(repo, entity)
                bind(neoEntity, entity.properties)
            }
        }
        repo.save(neoEntity)
        return entity
    }

    static <T> Page<T> listAll(GraphRepository<T> repo, Map params) {
        def request = PageUtils.create(params)
        new PageImpl<T>(repo.findAll(request).content, request, repo.count())
    }

    static <T> Page<T> searchAll(Neo4jTemplate neo, Class<T> aClass, String search, Map params) {
        def query = CypherGenerator.getPaginatedQuery(aClass, params).toString()
        def count = CypherGenerator.getCountQuery(aClass).toString()
        def searchParams = [search: search] as Map
        def size = neo.query(count, searchParams).to(Long).single()
        def data = neo.query(query, searchParams).to(aClass).as(List).collect()
        return new PageImpl<Customer>(data, PageUtils.create(params), size) as Page<T>
    }

    static <T> Result<T> query(CypherDslRepository<T> repo, ReturnNext execute, Map params, Class container) {
        PageUtils.addPagination(execute, params, container)
        return repo.query(execute, Collections.EMPTY_MAP)
    }


    static String getWildCardRegex(String search) { "(?i).*${Pattern.quote(search)}.*".toString() }

    @CompileStatic(TypeCheckingMode.SKIP)
    static def setProperty(Object object, String propertyName, def value) {
        if (object?.hasProperty(propertyName)) {
            object."$propertyName" = value
        }
        return object
    }

    static <T> T getOrCreate(Closure<T> getItem, Closure<T> createItem) {
        def item = getItem()
        if (!item) {
            item = createItem()
        }
        return item
    }

    static <T> T extractAndLoadParent(String parentAttr, Map params, Closure<T> getParent) {
        def id = extractId(params, parentAttr)
        if (id == -1) {
            return null
        }
        return getParent(id)
    }


    /**
     * Add a label to a node incase its absent
     * @param neo
     * @param entity
     * @return
     */
    static <T extends AbstractEntity> T addInheritanceLabelToNode(Neo4jTemplate neo, T entity) {

        if (!entity.id) return entity

        def concreteEntities = ReflectFunctions.findAllClassesWithAnnotation(entity.getClass(), NodeEntity)

        if (concreteEntities.size() <= 1) return entity

        def node = neo.getNode(entity.id)

        if (!node) return entity

        List<String> labels = node.getLabels().collect().collect { Label lbl -> lbl.name() }
        def classNames = concreteEntities.collect { Class klass -> klass.simpleName }

        classNames.each { String className ->
            if (!labels.contains(className)) {
                node.addLabel(DynamicLabel.label(className))
            }
        }
        return entity
    }


}
