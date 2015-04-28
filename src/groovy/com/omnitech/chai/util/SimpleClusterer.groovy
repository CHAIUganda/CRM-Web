package com.omnitech.chai.util

import com.omnitech.chai.crm.ClusterService
import com.omnitech.chai.crm.LocatableTask
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

        maximumNumberOfTask = (tasksPerDay + (tasksPerDay * MAXIMUM_THRESHOLD)).toInteger()
        minimumSize = (tasksPerDay - (tasksPerDay * MINIMUM_THRESHOLD)).toInteger()

        def (List<CentroidCluster<LocatableTask>> x, mp) = getClusters2(locatableTasks, 20)


        int sum = 0
        x.eachWithIndex { CentroidCluster<LocatableTask> entry, int i ->
            sum = sum + entry.points.size()
            println("Cluster($i). Points: ${entry.points.size()} ${entry.points.point}")
        }
        println("All Clustered Tasks: $sum")

        return x
    }

    //20 is a magic number to reduce the number of clusters
    List getClusters2(List<LocatableTask> locatableTasks, int _magicMaxTasksPerDay, boolean processMissedPoint = false) {

        def taskSize = locatableTasks.size()

        def numberOfClusters = calculateBestNumberOfCluster(taskSize, _magicMaxTasksPerDay)

        def clusterer = new KMeansPlusPlusClusterer<LocatableTask>(numberOfClusters as int, 1000);
        List<CentroidCluster<LocatableTask>> clusters = time("Actual Clustering nth($clusteringCount) time. " +
                "Tasks:[${locatableTasks.size()}]" +
                " Unique:[$taskSize] expectedClusters = [$numberOfClusters] ") {
            clusterer.cluster(locatableTasks);
        }

        clusteringCount = ++clusteringCount


        clusters.removeAll { it.points.size() == 0 }

        //if clusters are small return immediately or if maximum re-cluster count is reached
        if (clusters.size() == 1) return [clusters, []]



        List<CentroidCluster<LocatableTask>> finalSmallClusters = []
        List<LocatableTask> missedPoints = []


        for (cluster in clusters) {
            if (cluster.points.size() > maximumNumberOfTask) {
                def (subClusters, mp) = getClusters2(cluster.points, maximumNumberOfTask)
                missedPoints.addAll(mp)
                finalSmallClusters.addAll(subClusters)
            } else if (cluster.points.size() < minimumSize) {
                missedPoints.addAll(cluster.points)
            } else {
                finalSmallClusters << cluster
            }
        }

        if (processMissedPoint && missedPoints) {
            def (smallSubCluster, List<LocatableTask> mp) = getClusters2(missedPoints, maximumNumberOfTask)
            finalSmallClusters.addAll(smallSubCluster)
            if (mp) {
                //add remaining tasks to closest clusters
                for (t in mp) {
                    def closestCluster = finalSmallClusters.min { CentroidCluster a ->

                        def distance = ClusterService.distanceBetweenPoints([t.lat, t.lng] as double[], a.center.point)
                        log.trace("t[${t.task.description}] ->[${a.points[0].task.description}] = $distance")
                        return distance
                    }
                    log.trace("t[${t.task.description}] is clossest to : [${closestCluster.points[0].task.description}]")
                    closestCluster.addPoint(t)
                }
            }

            //go through each on for the clusters generate one more time and make sure none passes the maximum threshhold
            def reClusteredTasks = []
            def newSmallClusters = []
            finalSmallClusters.each { sc ->
                if (sc.points.size() > maximumNumberOfTask) {
                    def (smallerSubClusters, otherMissedPoints) = getClusters2(sc.points, maximumNumberOfTask, true)
                    newSmallClusters.addAll(smallerSubClusters)
                    reClusteredTasks << sc
                }
            }
            finalSmallClusters.removeAll(reClusteredTasks)
            finalSmallClusters.addAll(newSmallClusters)

        }

        return [finalSmallClusters, missedPoints]
    }

    static int calculateBestNumberOfCluster(int taskSize, int absoluteTaskPerDay) {
        return roundUpward(taskSize, absoluteTaskPerDay) / absoluteTaskPerDay

    }

    static int howManyUniquePointsDoWeHave(List<Clusterable> locatableTasks) {
        return locatableTasks.collect { it.point }.unique().size()
    }


}
