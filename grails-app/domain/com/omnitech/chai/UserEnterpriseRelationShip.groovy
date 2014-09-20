package com.omnitech.chai

import com.omnitech.mis.User

class UserEnterpriseRelationShip {

    String id
    String name

    Date dateCreated
    Date lastUpdated

    Relationship relationship
    Enterprise enterprise

    static belongsTo = [user: User]

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
