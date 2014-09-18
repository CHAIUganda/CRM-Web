package com.omnitech.mis

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

    static mapping = {
        id(generator: "com.omnitech.mis.utils.MyIdGenerator", type: "string", length: 32)
    }

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
