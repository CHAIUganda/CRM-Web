package com.omnitech.chai.model

import com.omnitech.chai.util.GroupNode
import com.omnitech.chai.util.LeafNode
import grails.validation.Validateable
import org.neo4j.graphdb.Direction
import org.springframework.data.neo4j.annotation.Fetch
import org.springframework.data.neo4j.annotation.NodeEntity
import org.springframework.data.neo4j.annotation.RelatedTo

@NodeEntity
@Validateable
class Report extends AbstractEntity implements LeafNode {

    String name
    String script
    String fields
    String type


    @Fetch
    @RelatedTo(type = Relations.REPORT_GRP_REPORT, direction = Direction.INCOMING)
    ReportGroup group


    static constraints = {
        name blank: false
        script blank: false
        fields blank: false
        type blank: false
    }

    String toString() { name }

    @Override
    GroupNode getParent() { return group }
}