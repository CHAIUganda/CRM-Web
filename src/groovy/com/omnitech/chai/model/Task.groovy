package com.omnitech.chai.model

import org.neo4j.graphdb.Direction
import org.springframework.data.neo4j.annotation.GraphId
import org.springframework.data.neo4j.annotation.NodeEntity
import org.springframework.data.neo4j.annotation.RelatedTo

/**
 * Created by kay on 9/24/14.
 */
@NodeEntity
class Task extends AbstractEntity  {

    static String PENDING = 'new', COMPLETE = 'complete'



    String description

    String type
    String status

    @RelatedTo(type = 'HAS_TASK',direction = Direction.INCOMING)
    Interaction task
}
