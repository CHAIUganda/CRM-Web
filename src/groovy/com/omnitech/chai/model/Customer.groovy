package com.omnitech.chai.model

import org.springframework.data.neo4j.annotation.GraphId
import org.springframework.data.neo4j.annotation.Indexed
import org.springframework.data.neo4j.annotation.NodeEntity

/**
 * Created by kay on 9/21/14.
 */
@NodeEntity
public class Customer {
    @GraphId
    Long id;
    String firstName, lastName;
    @Indexed(unique = true)
    String emailAddress;
//    @RelatedTo(type = "ADDRESS")
//    Set<Address> addresses = new HashSet<Address>();
}