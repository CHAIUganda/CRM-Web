package com.omnitech.chai

import com.omnitech.mis.User

class Sale {

    String id
    String name

    Date dateCreated
    Date lastUpdated

    Product product
    Customer customer
    User user


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
