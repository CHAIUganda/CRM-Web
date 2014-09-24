package com.omnitech.chai.util

import org.grails.databinding.SimpleDataBinder
import org.grails.databinding.SimpleMapDataBindingSource

/**
 * Created by kay on 9/24/14.
 */
class ChaiUtils {
    static Long extractId(Map params) {
        Long id = -1
        try {
            id = (params.id as Long) ?: -1
        } catch (Exception x) {
        }
        return id
    }

    static <T> T bind(def obj, Map properties) {
        SimpleDataBinder binder = new SimpleDataBinder();
        binder.bind(obj, new SimpleMapDataBindingSource(properties))
        return (T) obj
    }
}
