package com.omnitech.chai.util

import com.omnitech.chai.crm.LocatableTask
import com.omnitech.chai.model.Customer
import com.omnitech.chai.model.DetailerTask
import com.xlson.groovycsv.CsvParser
import spock.lang.Specification

/**
 * Created by kay on 4/28/2015.
 */
class SimpleClustererTest extends Specification {

    def Double 'test 1 '() {

        def tasks = loadTasks()
        def clusterer = new SimpleClusterer(locatableTasks: tasks, tasksPerDay: 8)
        when:
        def clusters = clusterer.cluster()
        then:
        clusters.size() > 0

    }


    List<LocatableTask> loadTasks() {
        def csv = getClass().getResource('/CustomerWithGPS.csv').text

        CsvParser parser = new CsvParser()

        def iter = parser.parse(csv)

        iter.collect {
            new LocatableTask(task: new DetailerTask(customer: new Customer(outletName: it.outletName as String, lat: it.lat as Double, lng: it.lng as Double)))
        }
    }

}
