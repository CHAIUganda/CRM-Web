package com.omnitech.chai.util

import com.omnitech.chai.crm.ClusterService
import com.omnitech.chai.crm.LocatableTask
import groovy.transform.CompileStatic
import org.apache.commons.math3.ml.clustering.CentroidCluster
import org.apache.commons.math3.ml.clustering.Clusterable
import org.apache.commons.math3.ml.clustering.KMeansPlusPlusClusterer
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import static com.omnitech.chai.util.ChaiUtils.roundUpward
import static com.omnitech.chai.util.ChaiUtils.time

/**
 * Created by Daniel on 12/1/2015.
 */
class ChaiSalesClusterer {
    static Logger log = LoggerFactory.getLogger(ChaiUtils)

    public static final float MAXIMUM_THRESHOLD = 0.4f
    public static final float MINIMUM_THRESHOLD = 0.3f
    public static final int MAXIMUM_RECLUSTERS = 100

    List<LocatableTask> locatableTasks
    int tasksPerDay
    int numberOfDays

    List<CentroidCluster<LocatableTask>> cluster() {
        def (List<CentroidCluster<LocatableTask>> x, mp) = getClusters2(locatableTasks, 20, true)
        return x
    }

    List getClusters2(List<LocatableTask> locatableTasks, int _magicMaxTasksPerDay, boolean processMissedPoint = false) {
        print "Locatable task size: " + locatableTasks.size()
        def clusterer = new KMeansPlusPlusClusterer<LocatableTask>(numberOfDays, 500)

        List<CentroidCluster<LocatableTask>> clusters = clusterer.cluster(locatableTasks)

        List<CentroidCluster<LocatableTask>> optimumClusters = new ArrayList<CentroidCluster<LocatableTask>>()

        HashMap<String, CentroidCluster<LocatableTask>> dailyClusters = new HashMap<String, Integer>()

        for (int i = 0; i < clusters.size(); i++) {
            def dailyCluster = null
            HashMap<String, Integer> taskSegments = new HashMap<String, Integer>()

            for (LocatableTask lt : clusters.get(i).getPoints()){
                def tasksInSegment = taskSegments.get(lt.task.segment)

                if (tasksInSegment > tasksPerDay/4) {
                    continue
                }
                
                if (tasksInSegment == null) {
                    taskSegments.put(lt.task.segment, 1)
                } else {
                    taskSegments.put(lt.task.segment, tasksInSegment + 1)
                }
                print "Tasks in segment: " + lt.task.segment + " " + tasksInSegment + " " + tasksPerDay

                if (dailyCluster == null) {
                    dailyCluster = new CentroidCluster(lt)
                    dailyCluster.addPoint(lt)
                } else {
                    dailyCluster.addPoint(lt)
                }
            }
            if (dailyCluster != null) {
                optimumClusters.add(dailyCluster)
            }
        }

        return [optimumClusters, []]
    }
}
