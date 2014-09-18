package com.omnitech.chai

/**
 * Sale
 * A domain class describes the data object and it's mapping to the database
 */
class Sale {

    String id
    String name

    /* Automatic timestamping of GORM */
    Date dateCreated
    Date lastUpdated


    Product product
    Promotion promotion

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
