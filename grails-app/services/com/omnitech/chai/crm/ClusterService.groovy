package com.omnitech.chai.crm

import com.omnitech.chai.model.LatLng
import com.omnitech.chai.model.Task
import com.omnitech.chai.model.Territory
import org.apache.commons.math3.ml.clustering.CentroidCluster
import org.apache.commons.math3.ml.clustering.Clusterable
import org.apache.commons.math3.ml.clustering.KMeansPlusPlusClusterer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.neo4j.support.Neo4jTemplate
import org.springframework.data.neo4j.transaction.Neo4jTransactional

import static java.util.Calendar.*

/**
 * Created by kay on 12/19/2014.
 */
class ClusterService {

    def territoryRepository
    def taskRepository
    @Autowired
    Neo4jTemplate neo
    static int TASKS_PER_DAY = 10
    static int NUMBER_OF_USERS = 1


    public double distanceBetweenPoints(LatLng pointA, LatLng pointB) {
        // Setup the inputs to the formula
        double R = 6371009d; // average radius of the earth in metres
        double dLat = Math.toRadians(pointB.getLat() - pointA.getLat());
        double dLng = Math.toRadians(pointB.getLng() - pointA.getLng());
        double latA = Math.toRadians(pointA.getLat());
        double latB = Math.toRadians(pointB.getLat());

        // The actual haversine formula. a and c are well known value names in the formula.
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.sin(dLng / 2) * Math.sin(dLng / 2) *
                Math.cos(latA) * Math.cos(latB);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c;

        return distance;
    }


    @Neo4jTransactional
    void scheduleTasks() {
        territoryRepository.findAll().each {
            clusterAndGeneratesTasks(it, TASKS_PER_DAY, NUMBER_OF_USERS)
        }
    }

    List<CentroidCluster<LocatableTask>> clusterAndGeneratesTasks(Territory territory, int tasksPerDay, int numberOfUsers) {

        def locatableTasks = getLocatableTasks(territory)
        if (!locatableTasks) return []
        def clusters = getClusters(locatableTasks, tasksPerDay, numberOfUsers)

        def nowDate = new Date(), pad = 0
        clusters.eachWithIndex { CentroidCluster<LocatableTask> entry, int i ->

            def dateToSet = nowDate + i
            def dayOfWeek = dateToSet[DAY_OF_WEEK]
            if (dayOfWeek == SATURDAY || dayOfWeek == SUNDAY) {
                dateToSet = dateToSet + (++pad)
            }
            entry.getPoints().each { locatableTask ->
                locatableTask.task.setDueDate(dateToSet)
                taskRepository.save(locatableTask.task)
            }
        }
        return clusters
    }


    static List<CentroidCluster<LocatableTask>> getClusters(List<LocatableTask> locatableTasks, int tasksPerDay, int numberOfUsers) {
        def taskSize = locatableTasks.size()
        def numberOfClusters = calculateClustersNeeded(taskSize, tasksPerDay, numberOfUsers)
        def clusterer = new KMeansPlusPlusClusterer<LocatableTask>(numberOfClusters as int, 10000);
        List<CentroidCluster<LocatableTask>> clusters = clusterer.cluster(locatableTasks);
        return clusters
    }


    private List<LocatableTask> getLocatableTasks(Territory territory) {
        def tasks = taskRepository.findAllTasksInTerritory(territory.id)

        List<LocatableTask> locatableTasks = []

        tasks.each { t ->
            if (t.isLocatable()) locatableTasks << new LocatableTask(task: t)
        }
        return locatableTasks
    }


    static int calculateClustersNeeded(int taskSize, int tasksPerDay, int numberOfUser) {
        taskSize / (tasksPerDay * numberOfUser)
    }

}

class LocatableTask implements Clusterable {

    Task task

    @Override
    double[] getPoint() {
        def lat = task.customer.lat as double
        def lng = task.customer.lng as double
        return [lat, lng] as double[]
    }
}
