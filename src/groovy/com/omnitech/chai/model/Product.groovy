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

    @RelatedTo(type = Relations.PROD_IN_TERRITORY)
    Set<Territory> territories

    static constraints = {
        name blank: false
        unitOfMeasure nullable: true
        unitPrice nullable: true
        formulation nullable: true
        group nullable: false
    }

    String toString() { name }

    boolean isSoldInTerritory(Territory t) {
        return territories?.any { it.id == t.id }
    }

    @Override
    GroupNode getParent() { return group }
}
