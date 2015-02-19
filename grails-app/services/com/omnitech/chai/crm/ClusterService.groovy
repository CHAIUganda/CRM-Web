package com.omnitech.chai.crm

import com.omnitech.chai.model.*
import com.omnitech.chai.util.ChaiUtils
import org.apache.commons.math3.ml.clustering.CentroidCluster
import org.apache.commons.math3.ml.clustering.Clusterable
import org.apache.commons.math3.ml.clustering.KMeansPlusPlusClusterer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.neo4j.support.Neo4jTemplate
import org.springframework.data.neo4j.transaction.Neo4jTransactional

import static com.omnitech.chai.util.ChaiUtils.getNextWorkDay
import static java.util.Calendar.*

/**
 * Created by kay on 12/19/2014.
 */
class ClusterService {

    def territoryRepository
    def taskRepository
    def detailerTaskRepository
    def orderRepository
    @Autowired
    Neo4jTemplate neo
    static int TASKS_PER_DAY = 10
    static int NUMBER_OF_USERS = 1


    static public double distanceBetweenPoints(LatLng pointA, LatLng pointB) {
        distanceBetweenPoints([pointA.lat, pointA.lng] as double[], [pointA.lat, pointB.lng] as double[])
    }


    static public double distanceBetweenPoints(double[] pointA, double[] pointB) {
        // Setup the inputs to the formula
        double R = 6371009d; // average radius of the earth in metres
        double dLng = Math.toRadians(pointB[0] - pointA[0]);
        double dLat = Math.toRadians(pointB[1] - pointA[1]);
        double latA = Math.toRadians(pointA[0]);
        double latB = Math.toRadians(pointB[0]);

        // The actual haversine formula. a and c are well known value names in the formula.
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.sin(dLng / 2) * Math.sin(dLng / 2) *
                Math.cos(latA) * Math.cos(latB);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c;

        return distance;
    }


    @Neo4jTransactional
    void scheduleDetailerTasks() {
        territoryRepository.findAll().each {
            clusterAndGeneratesTasks(it, TASKS_PER_DAY, NUMBER_OF_USERS, DetailerTask)
        }
    }

    @Neo4jTransactional
    void scheduleOrders() {
        territoryRepository.findAll().each {
            clusterAndGeneratesTasks(it, TASKS_PER_DAY, NUMBER_OF_USERS, Order)
        }
    }

    List<CentroidCluster<LocatableTask>> clusterAndGeneratesTasks(Territory territory, int tasksPerDay, int numberOfUsers, Class type) {

        def locatableTasks = getLocatableTasks(territory, type)
        if (!locatableTasks) return []
        def clusters = getClusters(locatableTasks, tasksPerDay, numberOfUsers)

        assignDueDateToClusters(clusters, new Date(), [MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY])

        clusters.each { c -> c.getPoints().each { lt -> taskRepository.save(lt.task) } }
        return clusters
    }


    static assignDueDateToClusters(List<CentroidCluster<LocatableTask>> clusters, Date startDate, List<Integer> workDays) {
        def nextDate = startDate
        for (CentroidCluster<LocatableTask> entry in clusters) {

            nextDate = getNextWorkDay(workDays, nextDate)


            entry.getPoints().each { locatableTask ->
                println("Setting next date: $nextDate - [$locatableTask.task.description]")
                locatableTask.task.setDueDate(nextDate)
            }

            nextDate = ++nextDate

        }
    }


    List<CentroidCluster<LocatableTask>> assignDueDates(List<Task> tasks, Date startDate, List<Integer> allowedDays, int tasksPerDay) {

        List<LocatableTask> locatableTasks = tasks.findResults { Task t ->
            if (t.isLocatable()) {
                return new LocatableTask(task: t)
            }
            return null
        } as List<LocatableTask>

        def (List<CentroidCluster<LocatableTask>> clusters, List other) = getClusters2(locatableTasks, tasksPerDay, 20, 0.3f, true)


        clusters.sort { CentroidCluster<LocatableTask> a, CentroidCluster<LocatableTask> b ->
            def dis = distanceBetweenPoints(
                    [getLat: { a.center.point[0] }, getLng: { a.center.point[1] }] as LatLng,
                    [getLat: { b.center.point[0] }, getLng: { b.center.point[1] }] as LatLng)
            return dis
        }

        assignDueDateToClusters(clusters, startDate, allowedDays)
        return clusters
    }


    static List<CentroidCluster<LocatableTask>> getClusters(List<LocatableTask> locatableTasks, int tasksPerDay, int numberOfUsers) {
        def taskSize = locatableTasks.size()
        def numberOfClusters = calculateClustersNeeded(taskSize, tasksPerDay, numberOfUsers)
        def clusterer = new KMeansPlusPlusClusterer<LocatableTask>(numberOfClusters as int, 10000);
        List<CentroidCluster<LocatableTask>> clusters = clusterer.cluster(locatableTasks);
        return clusters
    }
    //20 is a magic number to reduce the number of clusters
    static List getClusters2(List<LocatableTask> locatableTasks, int tasksPerDay, int _magicTasksPerDay, float _percOverheadTaskPerDay, boolean processMissedPoint = false) {
        def taskSize = locatableTasks.size()

        def numberOfClusters = calculateBestNumberOfCluster(taskSize, _magicTasksPerDay)
        def clusterer = new KMeansPlusPlusClusterer<LocatableTask>(numberOfClusters as int, 10000);
        List<CentroidCluster<LocatableTask>> clusters = clusterer.cluster(locatableTasks);

        if (clusters.size() == 1) return [clusters, []]


        def smallClusters = []
        List<LocatableTask> missedPoints = []

        def maximumNumberOfTask = (tasksPerDay + (tasksPerDay * _percOverheadTaskPerDay)).toInteger()
        def minimumSize = (tasksPerDay - (tasksPerDay * 0.2)).toInteger()
        for (cluster in clusters) {
            if (cluster.points.size() > maximumNumberOfTask) {
                def (subClusters, mp) = getClusters2(cluster.points, tasksPerDay, maximumNumberOfTask, 0.3f)
                missedPoints.addAll(mp)
                smallClusters.addAll(subClusters)
            } else if (cluster.points.size() < minimumSize) {
                missedPoints.addAll(cluster.points)
            } else {
                smallClusters << cluster
            }
        }

        if (processMissedPoint && missedPoints) {
            def (smallSubCluster, List<LocatableTask> mp) = getClusters2(missedPoints, tasksPerDay, maximumNumberOfTask, 0.3f)
            smallClusters.addAll(smallSubCluster)
            if (mp) {
                //add remaining tasks to closest clusters
                mp.each { t ->
                    def closestCluster = smallClusters.min { CentroidCluster a ->

                        def distance = distanceBetweenPoints([t.lat, t.lng] as double[], a.center.point)
                        log.trace("t[${t.task.description}] ->[${a.points[0].task.description}] = $distance")
                        return distance
                    }
                    log.trace("t[${t.task.description}] is clossest to : [${closestCluster.points[0].task.description}]")
                    closestCluster.addPoint(t)
                }
            }
        }

        return [smallClusters, missedPoints]
    }

    static int calculateBestNumberOfCluster(int taskSize, int absoluteTaskPerDay) {
        return ChaiUtils.roundUpward(taskSize, absoluteTaskPerDay) / absoluteTaskPerDay

    }


    private List<LocatableTask> getLocatableTasks(Territory territory, Class type) {
        def tasks
        if (type == DetailerTask)
            tasks = detailerTaskRepository.findAllInTerritory(territory.id)
        else
            tasks = orderRepository.findAllInTerritory(territory.id)


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

class LocatableTask implements Clusterable, LatLng {

    Task task

    @Override
    double[] getPoint() {
        def lat = task.customer.lat as double
        def lng = task.customer.lng as double
        return [lat, lng] as double[]
    }

    @Override
    Double getLat() {
        return task.customer.lat as double
    }

    @Override
    Double getLng() {
        return task.customer.lng as double
    }
}
