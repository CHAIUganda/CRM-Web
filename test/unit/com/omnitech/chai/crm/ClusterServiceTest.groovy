package com.omnitech.chai.crm

import com.omnitech.chai.model.*
import com.omnitech.chai.repositories.DetailerTaskRepository
import com.omnitech.chai.repositories.TaskRepository
import org.apache.commons.math3.ml.clustering.CentroidCluster
import spock.lang.Specification

/**
 * Created by kay on 12/21/2014.
 */
class ClusterServiceTest extends Specification {

    ClusterService service = new ClusterService()


    def "ClusterAndGeneratesTasks"() {

        DetailerTaskRepository detailerRepository = Mock(DetailerTaskRepository)
        TaskRepository taskRepository = Mock(TaskRepository)
        service.detailerTaskRepository = detailerRepository
        service.taskRepository = taskRepository

        def territory = new Territory(id: 4)


        when:
        def clusters = service.clusterAndGeneratesTasks(territory,
                ClusterService.TASKS_PER_DAY,
                ClusterService.NUMBER_OF_USERS, DetailerTask)

        then:
        1 * detailerRepository.findAllInTerritory(4) >> generateDummyTasks()
        52 * taskRepository.save(_ as Task)
        clusters.size() == 5


    }

    def generateDummyTasks() {

        def tasks = []
        10.times { tasks << new Task(customer: customer(12, 5)) }
        7.times { tasks << new Task(customer: customer(4, 5)) }
        7.times { tasks << new Task(customer: customer(10, 5)) }
        7.times { tasks << new Task(customer: customer(23, 5)) }
        7.times { tasks << new Task(customer: customer(3, 5)) }
        7.times { tasks << new Task(customer: customer(12, 16)) }
        7.times { tasks << new Task(customer: customer(30, 30)) }

        return tasks


    }

    Customer customer(x, y) {
        new Customer(wkt: "point($x,$y)", lat: x as float, lng: y as float)
    }

}


