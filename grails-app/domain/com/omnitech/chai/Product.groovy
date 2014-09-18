package com.omnitech.chai

class Product {

    String id
    String name
    String metric


    Date dateCreated
    Date lastUpdated

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
