package com.omnitech.chai.model

import com.omnitech.chai.util.GroupNode
import grails.validation.Validateable
import org.neo4j.graphdb.Direction
import org.springframework.data.neo4j.annotation.Fetch
import org.springframework.data.neo4j.annotation.NodeEntity
import org.springframework.data.neo4j.annotation.RelatedTo

/**
 * Created by kay on 12/30/2014.
 */
@NodeEntity
@Validateable
class ReportGroup extends AbstractEntity implements GroupNode {

    String name

    @Fetch
    @RelatedTo(type = Relations.REPORT_GRP_GRP, direction = Direction.INCOMING)
    ReportGroup parent

    @RelatedTo(type = Relations.REPORT_GRP_REPORT)
    Set<Report> reports

    static constraints = {
        name blank: false
    }

    String toString(){name}

}

