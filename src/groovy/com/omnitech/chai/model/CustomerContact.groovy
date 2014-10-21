package com.omnitech.chai.model

import grails.validation.Validateable
import org.neo4j.graphdb.Direction
import org.springframework.data.neo4j.annotation.NodeEntity
import org.springframework.data.neo4j.annotation.RelatedTo

@NodeEntity
@Validateable
class CustomerContact extends AbstractEntity {

    String name
    String contact
    String typeOfContact
    String gender
    String role
    String qualification
    String networkOrAssociation
    Integer graduationYear

    static constraints = {
        name            blank: false
        contact         blank: false
        typeOfContact   blank: false, inList: ['key', 'ordinary']
        gender          blank: false, inList: ['male', 'female']
        role            blank: false
        graduationYear  min: 1950, max: 2015, nullable: true
        qualification   blank: false

    }

}
