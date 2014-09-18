package com.omnitech.chai


class Promotion {

    String id
    String name

    Date startDate
    Date endDate

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
