package com.omnitech.chai.model

/**
 * SubCounty
 */
class SubCounty {

    String id
    String name
    Date dateCreated
    Date lastUpdated

    static auditable = true

    static belongsTo = [district:District]
    static hasMany = [parishes:Parish]



    static constraints = {
        name blank: false
    }

    /*
     * Methods of the Domain Class
     */

    @Override    // Override toString for a nicer / more descriptive UI
    public String toString() {
        return "${name}";
    }
}
