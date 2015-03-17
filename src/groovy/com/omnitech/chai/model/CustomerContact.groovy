package com.omnitech.chai.model

import grails.validation.Validateable
import org.neo4j.graphdb.Direction
import org.springframework.data.neo4j.annotation.NodeEntity
import org.springframework.data.neo4j.annotation.RelatedTo

@NodeEntity
@Validateable
class CustomerContact extends AbstractEntity {

    String title
    String firstName
    String names
    String contact


    String gender
    String role
    String qualification
    String networkOrAssociationName



    static constraints = {
        gender          blank: false, inList: ['male', 'female']
        role            blank: false
        qualification   blank: false
        title           blank: false

    }

}
