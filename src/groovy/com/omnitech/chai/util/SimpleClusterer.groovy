package com.omnitech.chai.util

import com.omnitech.chai.crm.ClusterService
import com.omnitech.chai.crm.LocatableTask
import groovy.transform.CompileStatic
import org.apache.commons.math3.ml.clustering.CentroidCluster
import org.apache.commons.math3.ml.clustering.Clusterable
import org.apache.commons.math3.ml.clustering.KMeansPlusPlusClusterer

import static com.omnitech.chai.util.ChaiUtils.getLog
import static com.omnitech.chai.util.ChaiUtils.roundUpward
import static com.omnitech.chai.util.ChaiUtils.time

/**
 * Created by kay on 4/27/2015.
 */
class SimpleClusterer {

    public static final float MAXIMUM_THRESHOLD = 0.4f
    public static final float MINIMUM_THRESHOLD = 0.3f
    public static final int MAXIMUM_RECLUSTERS = 100


    List<LocatableTask> locatableTasks
    int minimumSize
    int maximumNumberOfTask
    int tasksPerDay
    private int clusteringCount = 0

    List<CentroidCluster<LocatableTask>> cluster() {

        initMinAndMax()

        def (List<CentroidCluster<LocatableTask>> x, mp) = getClusters2(locatableTasks, 20)


        int sum = 0
        x.eachWithIndex { CentroidCluster<LocatableTask> entry, int i ->
            sum = sum + entry.points.size()
            println("Cluster($i). Points: ${entry.points.size()} ${entry.points.point}")
        }
        println("All Clustered Tasks: $sum")

        return x
    }

    SimpleClusterer initMinAndMax() {
        maximumNumberOfTask = roundUpward((tasksPerDay + (tasksPerDay * MAXIMUM_THRESHOLD)), 1)
        minimumSize = roundUpward((tasksPerDay - (tasksPerDay * MINIMUM_THRESHOLD)), 1)
        return this
    }

    //20 is a magic number to reduce the number of clusters
    List getClusters2(List<LocatableTask> locatableTasks, int _magicMaxTasksPerDay, boolean processMissedPoint = false) {

        //Start with may be creating same GPS points
        def optimumClusters = mayBeCreateEqualCluster(locatableTasks)

        removeTasksInCluster(locatableTasks, optimumClusters)

        def taskSize = locatableTasks.size()

        def numberOfClusters = calculateBestNumberOfCluster(taskSize, _magicMaxTasksPerDay)

        def clusterer = new KMeansPlusPlusClusterer<LocatableTask>(numberOfClusters as int, 500);
        List<CentroidCluster<LocatableTask>> clusters = time("Actual Clustering nth($clusteringCount) time. " +
                "Tasks:[${locatableTasks.size()}]" +
                " Unique:[$taskSize] expectedClusters = [$numberOfClusters] ") {
            clusterer.cluster(locatableTasks);
        }

        clusteringCount = ++clusteringCount


        clusters.removeAll { it.points.size() == 0 }

        //if clusters are small return immediately or if maximum re-cluster count is reached
        if (clusters.size() == 1) return [clusters, []]



        List<LocatableTask> missedPoints = []


        for (cluster in clusters) {
            if (cluster.points.size() > maximumNumberOfTask) {
                def (subClusters, mp) = getClusters2(cluster.points, maximumNumberOfTask)
                missedPoints.addAll(mp)
                optimumClusters.addAll(subClusters)
            } else if (cluster.points.size() < minimumSize) {
                missedPoints.addAll(cluster.points)
            } else {
                optimumClusters << cluster
            }
        }

        if (processMissedPoint && missedPoints) {

            def (otherOptimumClusters, List<LocatableTask> mp) = getClusters2(missedPoints, maximumNumberOfTask)
            optimumClusters.addAll(otherOptimumClusters)


            dissolveSmallPoints(optimumClusters, mp)

            //go through each on for the clusters generate one more time and make sure none passes the maximum threshhold
            def reClusteredTasks = []
            def newSmallClusters = []
            optimumClusters.each { sc ->
                if (sc.points.size() > maximumNumberOfTask) {
                    def (smallerSubClusters, otherMissedPoints) = getClusters2(sc.points, maximumNumberOfTask, true)
                    newSmallClusters.addAll(smallerSubClusters)
                    dissolveSmallPoints(newSmallClusters, otherMissedPoints)
                    reClusteredTasks << sc
                }
            }
            optimumClusters.removeAll(reClusteredTasks)
            optimumClusters.addAll(newSmallClusters)

        }

        return [optimumClusters, missedPoints]
    }

    private static void removeTasksInCluster(List<LocatableTask> tasks, List<CentroidCluster<LocatableTask>> clusters) {
        tasks.removeAll { thisTask -> clusters.any { it.points.contains(thisTask) } }
    }


    List<CentroidCluster<LocatableTask>> mayBeCreateEqualCluster(List<LocatableTask> tasks) {
        def groups = tasks.groupBy { it.point as List }

        List<CentroidCluster<LocatableTask>> optimalGroups = []
        groups.each { k, v ->
            if (isOptimalSize(v)) {
                optimalGroups << createCluster(v)
            }

            if (v.size() > maximumNumberOfTask) {
                def clusters = attempToCreateClusters(v)
                optimalGroups.addAll(clusters)
            }
        }
        return optimalGroups
    }


    @CompileStatic
    List<CentroidCluster> attempToCreateClusters(List<LocatableTask> tasks) {

        def taskSize = tasks.size()

        //find best possible collate value
        def bestCollateValue = (minimumSize..maximumNumberOfTask).min { Integer it -> taskSize.mod(it) }



        def listOfLists = tasks.collate(bestCollateValue)

        def optimalLists = listOfLists.findAll { List it -> isOptimalSize(it) }

        return optimalLists.collect { List it -> createCluster(it) }

    }


    private static CentroidCluster createCluster(List<LocatableTask> tasks) {
        def cluster = new CentroidCluster(tasks[0])
        tasks.each { cluster.addPoint(it) }
        return cluster
    }

    private boolean isOptimalSize(List tasks) {
        return tasks.size() >= minimumSize && tasks.size() <= maximumNumberOfTask
    }

    private
    static void dissolveSmallPoints(List<CentroidCluster<LocatableTask>> optimumClusters, List<Clusterable> points) {
        for (point in points) {
            def closestCluster = optimumClusters.min { CentroidCluster a ->

                def distance = ClusterService.distanceBetweenPoints([point.lat, point.lng] as double[], a.center.point)
                log.trace("t[${point.task.description}] ->[${a.points[0].task.description}] = $distance")
                return distance
            }
            log.trace("t[${point.task.description}] is clossest to : [${closestCluster.points[0].task.description}]")
            closestCluster.addPoint(point)
        }
    }

    static int calculateBestNumberOfCluster(int taskSize, int absoluteTaskPerDay) {
        return roundUpward(taskSize, absoluteTaskPerDay) / absoluteTaskPerDay

    }

    static int howManyUniquePointsDoWeHave(List<Clusterable> locatableTasks) {
        return locatableTasks.collect { it.point }.unique().size()
    }


}
