package com.omnitech.chai

import com.omnitech.mis.User

class Device {

    String id
    String name
    String imei
    User user

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
