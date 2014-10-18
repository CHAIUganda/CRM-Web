package com.omnitech.chai.model

import com.omnitech.chai.util.GroupNode
import com.omnitech.chai.util.LeafNode
import grails.validation.Validateable
import org.neo4j.graphdb.Direction
import org.springframework.data.neo4j.annotation.Fetch
import org.springframework.data.neo4j.annotation.NodeEntity
import org.springframework.data.neo4j.annotation.RelatedTo

/**
 * Created by kay on 9/25/14.
 */
@NodeEntity
@Validateable
class Product extends AbstractEntity implements LeafNode {

    String name
    String unitOfMeasure
    String formulation
    Double unitPrice

    @Fetch
    @RelatedTo(type = Relations.GRP_HAS_PRD, direction = Direction.INCOMING)
    ProductGroup group

    static constraints = {
        name blank: false
        unitOfMeasure blank: false
        unitPrice nullable: false
        formulation blank: false
        group nullable: false
    }

    @Override
    GroupNode getParent() { return group }
}
