package com.omnitech.chai


class Territory {


    String id
    String name


    float lat
    float lng

    Date dateCreated
    Date lastUpdated

    static hasMany = [customers: Customer]

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
