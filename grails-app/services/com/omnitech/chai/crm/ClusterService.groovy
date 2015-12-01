package com.omnitech.chai.crm

import com.omnitech.chai.model.LatLng
import com.omnitech.chai.model.Task
import com.omnitech.chai.util.SimpleClusterer
import com.omnitech.chai.util.ChaiSalesClusterer
import org.apache.commons.math3.ml.clustering.CentroidCluster
import org.apache.commons.math3.ml.clustering.Clusterable
import org.apache.commons.math3.ml.clustering.KMeansPlusPlusClusterer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.neo4j.support.Neo4jTemplate
import static com.omnitech.chai.util.ChaiUtils.*
import static java.util.Calendar.MONDAY

/**
 * Created by kay on 12/19/2014.
 */
class ClusterService {

    public static final float MAXIMUM_THRESHOLD = 0.4f
    public static final float MINIMUM_THRESHOLD = 0.3f
    public static final int MAXIMUM_RECLUSTERS = 100

    def taskRepository
    def detailerTaskRepository
    @Autowired
    Neo4jTemplate neo
    static int TASKS_PER_DAY = 10
    static int NUMBER_OF_USERS = 1


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


    static List<CentroidCluster<LocatableTask>> assignDueDateToClusters(List<CentroidCluster<LocatableTask>> clusters, Date startDate, List<Integer> workDays, boolean flowIntoNextWeek = true) {

        def nextAvailableDate = startDate
        def nextMonday = nextDayOfWeek(startDate, MONDAY).time
        def assignedClusters = []

        for (CentroidCluster<LocatableTask> entry in clusters) {

            nextAvailableDate = getNextWorkDay(workDays, nextAvailableDate)

            if (!flowIntoNextWeek && (nextMonday - nextAvailableDate) <= 0) {
                break;
            }

            entry.getPoints().each { locatableTask ->
                log.trace("Setting next date: $nextAvailableDate - [$locatableTask.task.description]")
                locatableTask.task.setDueDate(nextAvailableDate)
            }

            assignedClusters << entry
            nextAvailableDate = ++nextAvailableDate
        }

        return assignedClusters
    }

    List<CentroidCluster<LocatableTask>> assignDueDates(List<Task> tasks, Date startDate, List<Integer> allowedDays, int tasksPerDay) {

        List<LocatableTask> locatableTasks = tasks.findResults { Task t ->
            if (t.isLocatable()) {
                return new LocatableTask(task: t)
            }
            return null
        } as List<LocatableTask>

        List<CentroidCluster<LocatableTask>> clusters = time("Clustering [${locatableTasks.size()}]...") {
            getClusters2(locatableTasks, tasksPerDay, allowedDays)
        }

        clusters.each {CentroidCluster<LocatableTask> a ->
            print a
        }
        time("Sorting Generated Clusters [${clusters?.size()}]") {
            clusters.sort { CentroidCluster<LocatableTask> a, CentroidCluster<LocatableTask> b ->
                def dis = distanceBetweenPoints(a.center.point, b.center.point)
                return dis
            }
        }
        

        def assignedClusters = time("Assigning DueDates TO Clusters [${clusters?.size()}]") {
            assignDueDateToClusters(clusters, startDate, allowedDays, false)
        }
        return assignedClusters
    }

    //20 is a magic number to reduce the number of clusters
    static List getClusters2(List<LocatableTask> locatableTasks, int tasksPerDay, List<Integer> allowedDays) {
        new ChaiSalesClusterer(tasksPerDay: tasksPerDay, locatableTasks: locatableTasks, numberOfDays: allowedDays.size()).cluster()
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
