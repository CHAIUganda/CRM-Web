package com.omnitech.mis

class Role {

    String id
	String authority

    Date dateCreated
    Date lastUpdated

    static auditable = true

    static mapping = {
        id(generator: "com.omnitech.mis.utils.MyIdGenerator", type: "string", length: 32, class: String)
        cache true
    }

	static constraints = {
		authority blank: false, unique: true
	}

    @Override    // Override toString for a nicer / more descriptive UI
    public String toString() {
        return "${authority}";
    }
}
