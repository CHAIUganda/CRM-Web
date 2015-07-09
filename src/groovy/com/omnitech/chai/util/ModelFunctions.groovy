package com.omnitech.chai.util

import com.omnitech.chai.model.AbstractEntity
import groovy.transform.CompileStatic
import groovy.transform.TypeCheckingMode
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.codehaus.groovy.runtime.DefaultGroovyMethods
import org.grails.databinding.SimpleDataBinder
import org.grails.databinding.SimpleMapDataBindingSource
import org.neo4j.cypherdsl.grammar.Execute
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

import static java.util.Collections.EMPTY_MAP
import static org.neo4j.cypherdsl.CypherQuery.*

/**
 * Created by kay on 9/24/14.
 */
@CompileStatic
class ModelFunctions {

    private static Log log = LogFactory.getLog(ModelFunctions.class)
    final public static List META_FIELDS = Collections.unmodifiableList(['uuid', 'lastUpdated', 'dateCreated'])


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
        bind(instance, properties)
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
            bind(obj, properties, null, null)
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

    //todo since spring data does not use session bound objects like hibernate we should remove or modify this useless method
    static <S extends AbstractEntity> S saveEntity(GraphRepository<S> repo, S entity, Closure beforeBind) {
        S neoEntity = entity
        if (entity.id) {
            def tempNeoEntity = repo.findOne(entity.id)
            if (tempNeoEntity) {
                beforeBind?.call(tempNeoEntity)
                neoEntity = tempNeoEntity
                bind(neoEntity, DefaultGroovyMethods.getProperties(entity))
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
                addInheritanceLabelToNode(repo, entity)
            }
        }
        repo.save(neoEntity)
        return entity
    }

    static <T> Page<T> listAll(GraphRepository<T> repo, Map params) {
        def request = PageUtils.create(params)
        new PageImpl<T>(repo.findAll(request).content, request, repo.count())
    }


    static <T> Page<T> listAll(Neo4jTemplate repo, Class<T> type, Map params, Class rtResult) {
        def nodeName = type.simpleName.toLowerCase()

        def dataQuery = match(node(nodeName).label(type.simpleName)).returns(identifier(nodeName))
        dataQuery = PageUtils.addPagination(dataQuery, params, type)

        def countQuery = match(node(nodeName).label(type.simpleName)).returns(count(identifier(nodeName)))
        return query(repo, dataQuery, countQuery, params, rtResult)
    }

    static <T> Page<T> searchAll(Neo4jTemplate neo, Class<T> aClass, String search, Map params) {
        def query = CypherGenerator.getPaginatedQuery(aClass, params).toString()
        def count = CypherGenerator.getCountQuery(aClass).toString()
        def searchParams = [search: search] as Map
        def size = neo.query(count, searchParams).to(Long).single()
        def data = neo.query(query, searchParams).to(aClass).as(List).collect()
        return new PageImpl<T>(data, PageUtils.create(params), size)
    }

    static <T> Page<T> searchAll(Neo4jTemplate neo, Class<T> aClass, String search, Map params, int level, Map filters) {
        def query = CypherGenerator.getPaginatedQuery(aClass, params, level, filters).toString()
        def count = CypherGenerator.getCountQuery(aClass, level, filters).toString()
        log.trace("**** Query ***** \n$query*** Count ***\n$count****".toString())
        def searchParams = [search: search] as Map
        def size = neo.query(count, searchParams).to(Long).single()
        def data = neo.query(query, searchParams).to(aClass).as(List).collect()
        return new PageImpl<T>(data, PageUtils.create(params), size)
    }

    static <T> Result<T> query(CypherDslRepository<T> repo, ReturnNext execute, Map params, Class container) {
        PageUtils.addPagination(execute, params, container)
        return repo.query(execute, EMPTY_MAP)
    }

    static <T> Page<T> query(Neo4jTemplate neo, Execute query, Execute countQuery, Map params, Class<T> container) {
        log.trace("ModelFx.query List: $query")
        def data = neo.query(query.toString(), EMPTY_MAP).to(container).as(List)
        log.trace("ModelFx.query Count: $countQuery")
        def size = neo.query(countQuery.toString(), EMPTY_MAP).to(Long).single()

        println(query.toString())
        return new PageImpl<T>(data, PageUtils.create(params), size)
    }


    static String getWildCardRegex(String search) {
        search = search.split(/\s+/).collect { String s -> "(${Pattern.quote(s)})" }.join('.*')
        "(?i).*$search.*".toString()
    }

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

    static def addItemIfNotExists(Collection e, def item, Closure equals) {

        if (e.any { equals.call(it) }) {
            e.add(item)
        }

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


        def oldConcreteLabel = labels.find { String s -> s.startsWith('_') }
        def newConcreteLabel = "_${entity.getClass().simpleName}"

        if (newConcreteLabel == oldConcreteLabel) return entity

        //remove old concrete label
        if (oldConcreteLabel) node.removeLabel(DynamicLabel.label(oldConcreteLabel))

        //add new concrete label
        node.addLabel(DynamicLabel.label(newConcreteLabel))

        return entity
    }


}
