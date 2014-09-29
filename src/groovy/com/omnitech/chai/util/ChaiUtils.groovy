package com.omnitech.chai.util

import org.apache.commons.logging.LogFactory
import org.grails.databinding.SimpleDataBinder
import org.grails.databinding.SimpleMapDataBindingSource

/**
 * Created by kay on 9/24/14.
 */
class ChaiUtils {

    private static def log = LogFactory.getLog(ChaiUtils.class)

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

    static def setProperty(Object object, String propertyName, def value) {
        if (object?.hasProperty(propertyName)) {
            object."$propertyName" = value
        }
        return object
    }

    static injectUtilityMethods() {
        String.metaClass.toLongSafe = {
            def _delegate = delegate
            return execSilently { Long.valueOf(_delegate) }
        }
    }

    static execSilently(String error, Closure code) {
        try {
            return code.call()
        } catch (Exception ex) {
            log.error(error, ex)
        }
        return null
    }

    static execSilently(Closure code) {
        execSilently('Unkown Error', code)
    }


}
