package com.omnitech.chai


class Distribution {

    String id
    String name

    Date dateCreated
    Date lastUpdated

    static belongsTo = [ccall: Task]

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
