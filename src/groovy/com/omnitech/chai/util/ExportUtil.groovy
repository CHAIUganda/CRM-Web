package com.omnitech.chai.util

import com.omnitech.chai.model.AbstractEntity
import fuzzycsv.Record
import groovy.transform.CompileStatic

import java.lang.reflect.Field

import static fuzzycsv.FuzzyCSVTable.tbl
import static fuzzycsv.RecordFx.fn
import static grails.util.GrailsNameUtils.getNaturalName

/**
 * Created by kay on 3/11/2015.
 */
class ExportUtil {

    @CompileStatic
    static def fixDates(Class exportClass, List<List<String>> csv) {

        def dateFields = ReflectFunctions.findAllFields(exportClass).findAll { Field f ->
            //ignore these since they were already handled by _dateCreated and _dateLastUpdated
            f.type == Date && !['lastUpdated', 'dateCreated'].contains(f.name)
        }.collect { Field f ->
            getNaturalName(f.name).toUpperCase()
        }


        def table = tbl(csv)
        dateFields.each { String dateField ->
            if (table.header.contains(dateField)) {
                table = table.transform(dateField, fn { Record r ->
                    def property = r.propertyMissing(dateField)
                    if (property instanceof Long) {
                        return AbstractEntity.format.format(new Date(property))
                    }
                    return property
                })
            }
        }

        return table.csv

    }

}
