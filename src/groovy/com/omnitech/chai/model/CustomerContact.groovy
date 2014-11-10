package com.omnitech.chai.model

import grails.validation.Validateable
import org.neo4j.graphdb.Direction
import org.springframework.data.neo4j.annotation.NodeEntity
import org.springframework.data.neo4j.annotation.RelatedTo

@NodeEntity
@Validateable
class CustomerContact extends AbstractEntity {

    //todo remove from form [name,graduationYear,contact,typeOfContact]

    String title
    String firstName
    String surname


    String gender
    String role
    String qualification
    Boolean networkOrAssociation

    //todo add to form



    static constraints = {
        gender          blank: false, inList: ['male', 'female']
        role            blank: false
        qualification   blank: false
        firstName       blank: false
        surname         blank: false
        title           blank: false

    }

}
