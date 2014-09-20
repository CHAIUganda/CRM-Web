package com.omnitech.chai

class Relationship {

    String id
    String name

    Date dateCreated
    Date lastUpdated

    static hasMany = [userEnterpriseRelationShips: UserEnterpriseRelationShip]


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
