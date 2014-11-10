package com.omnitech.chai.util

import com.omnitech.chai.model.AbstractEntity
import groovy.transform.CompileStatic
import org.springframework.data.annotation.Transient
import org.springframework.validation.Errors

import java.lang.annotation.Annotation
import java.lang.reflect.Field
import java.lang.reflect.Modifier

/**
 * Created by kay on 10/6/14.
 */
@CompileStatic
class ReflectFunctions {


    public static final List<Class> BASIC_TYPES = [
            String,
            Boolean,
            Byte,
            Short,
            Integer,
            Long,
            Float,
            Double,
            Character,
            Date
    ]


    static List<Field> findAllFields(Class aClass) { return findAllFieldsImpl(aClass) }

    private static Closure<List<Field>> findAllFieldsImpl = { Class aClass ->
        List<Field> rt = []
        getClassHierarchy(aClass).each { Class innerClass -> rt.addAll innerClass.declaredFields }
        return rt
    }.memoizeBetween(50, 50)

    /**
     * Finds all fields we need to persist for spring data
     * excluding all transient,collections and static
     */
    static List<Field> findAllPersistentFields(Class aClass) {
        findAllFields(aClass).findAll {Field it -> isPersistent(it) } as List
    }

    /**
     * Returns true is this field is neither static,transient,collection or an error.
     */
    static boolean isPersistent(Field field) {
        !Modifier.isStatic(field.modifiers) &&
                !Modifier.isTransient(field.modifiers) &&
                !Collection.isAssignableFrom(field.type) &&
                !field.isAnnotationPresent(Transient) &&
                !Errors.isAssignableFrom(field.type)
    }

    /**
     * Returns a list of all super classes for this passed class
     */
    static List<Class> getClassHierarchy(Class aClass) {
        List<Class> classes = [aClass]
        while (aClass.superclass != Object) {
            aClass = aClass.superclass
            classes << aClass
        }
        return classes
    }

    static <T> List<T> findResultsOnFields(Class aClass,
                                           Closure<T> transform,
                                           Closure<List<Field>> extractFields,
                                           List visited) {
        if (visited.contains(aClass)) return []

        def results = []
        for (Field field in extractFields(aClass)) {
            def result = transform(aClass, field)

            //todo for now we do not support classes referencing each other
            if (aClass == field.type) continue

            if (result != null) results.add(result)

            if (extractFields(field.type)) {
                def fields = findResultsOnFields(field.type, transform, extractFields, visited)
                results.addAll(fields)
            }
            visited.add(field.type)
        }

        return results
    }

    static Map extractProperties(Object object) {
        def fields = findAllPersistentFields(object.class).findAll { Field it ->
            isPersistent(it) && !AbstractEntity.isAssignableFrom(it.type)
        }
        def values = fields.collectEntries { Field it -> [it.name, getValue(it, object)] }
        return values
    }

    static List<Class> findAllClassesWithAnnotation(Class klass, Class<? extends Annotation> annotation) {
        getClassHierarchy(klass).findAll { Class klass1 ->
            klass1.getDeclaredAnnotations().any { Annotation annot -> annot.annotationType() == annotation }
        }  as List
    }

    static Object getValue(Field field, def object) {
        def accessible = field.isAccessible()
        try {
            if (!accessible) field.setAccessible(true)
//            if (Date.isAssignableFrom(field.type))
//                return ChaiUtils.formatDate(field.get(object) as Date)
            return field.get(object)

        } finally {
            if (!accessible) field.setAccessible(accessible)
        }

    }

    /**
     * Returns all persistent basic fields that can be directly persisted in a node
     * @param klass Class to inspect
     * @return List of all persistent fields
     */
    static List<String> findAllBasicFields(Class klass) {
        findAllPersistentFields(klass).findResults { Field field ->
            if (BASIC_TYPES.contains(field.type)) return field.name
            return null
        } as List<String>
    }
}
