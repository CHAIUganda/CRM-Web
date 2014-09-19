package com.omnitech.chai

import com.omnitech.mis.User

class Task {

    String id
    String name
    String status

    User user

    Date dateCreated
    Date lastUpdated

    static hasOne = [promotion: Promotion, sale: Sale]

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
