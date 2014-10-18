package com.omnitech.chai.model

import com.omnitech.chai.util.GroupNode
import grails.validation.Validateable
import org.neo4j.graphdb.Direction
import org.springframework.data.neo4j.annotation.Fetch
import org.springframework.data.neo4j.annotation.NodeEntity
import org.springframework.data.neo4j.annotation.RelatedTo

/**
 * Created by kay on 10/17/14.
 */

@NodeEntity
@Validateable
class ProductGroup extends AbstractEntity implements GroupNode {

    String name

    @Fetch
    @RelatedTo(type = Relations.GRP_HAS_GRP, direction = Direction.INCOMING)
    ProductGroup parent

    static constraints = {
        name blank: false
    }

    String toString() { name }


}
