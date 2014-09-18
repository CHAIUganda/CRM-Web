package com.omnitech.mis

/**
 * Parish
 * A domain class describes the data object and it's mapping to the database
 */
class Parish {

    String id
    String name

    Date dateCreated
    Date lastUpdated

    static auditable = true

    static belongsTo = [subcounty:SubCounty]

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
