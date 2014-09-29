package com.omnitech.chai.util

import com.omnitech.chai.model.AbstractEntity
import org.apache.commons.logging.LogFactory
import org.grails.databinding.SimpleDataBinder
import org.grails.databinding.SimpleMapDataBindingSource
import org.springframework.data.neo4j.repository.GraphRepository

/**
 * Created by kay on 9/24/14.
 */
class ModelFunctions {

    private static def log = LogFactory.getLog(ModelFunctions.class)

    static Long extractId(Map params) {
        Long id = -1
        try {
            id = (params.id as Long) ?: -1
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
        def neoEntity = entity
        if (entity.id) {
            neoEntity = repo.findOne(entity.id)
            bind(neoEntity, entity.properties)
        }
        repo.save(neoEntity)
    }

    static def setProperty(Object object, String propertyName, def value) {
        if (object?.hasProperty(propertyName)) {
            object."$propertyName" = value
        }
        return object
    }

}
