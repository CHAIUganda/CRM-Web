package com.omnitech.chai

/**
 * Customer
 * A domain class describes the data object and it's mapping to the database
 */
class Customer {

    String id
    String name
    String contact1
    String contact2
    String emailAddress

    float lat
    float lng

    Date dateCreated
    Date lastUpdated

    static belongsTo = [territory: Territory]

    static mapping = {
        id(generator: "com.omnitech.mis.utils.MyIdGenerator", type: "string", length: 32)
    }

    static constraints = {
    }


    @Override
    public String toString() {
        return "${name}";
    }
}
