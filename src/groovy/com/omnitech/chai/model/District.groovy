package com.omnitech.chai.model

/**
 * District
 */
class District {

    String id
    String name
    Date dateCreated
    Date lastUpdated


    static hasMany = [subcounties: SubCounty]


    @Override
    public String toString() {
        return "${name}";
    }
}
