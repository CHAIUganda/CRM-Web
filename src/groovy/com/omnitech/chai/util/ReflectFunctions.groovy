package com.omnitech.chai.util

import org.springframework.data.annotation.Transient

import java.lang.reflect.Field
import java.lang.reflect.Modifier

/**
 * Created by kay on 10/6/14.
 */
class ReflectFunctions {

    static List<Field> findAllFields(Class aClass) { return findAllFieldsImpl(aClass) }

    private static findAllFieldsImpl = { Class aClass ->
        List<Field> rt = []
        getClassHierarchy(aClass).each { rt.addAll it.declaredFields }
        return rt
    }.memoizeBetween(50, 50)

    /**
     * Finds all fields we need to persist for spring data
     * excluding all transient,collections and static
     * @return
     */
    static List<Field> findAllPersistentFields(Class aClass) {
        findAllFields(aClass).findAll {
            !Modifier.isStatic(it.modifiers) &&
                    !Modifier.isTransient(it.modifiers) &&
                    !Collection.isAssignableFrom(it.type) &&
                    !it.isAnnotationPresent(Transient)
        }
    }

    static List<Class> getClassHierarchy(Class aClass) {
        List<Class> classes = [aClass]
        while (aClass.superclass != Object) {
            aClass = aClass.superclass
            classes << aClass
        }
        return classes
    }
}
