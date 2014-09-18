package com.omnitech.mis

class User {

	transient springSecurityService

    String id
	String username
	String password
	boolean enabled = true
	boolean accountExpired
	boolean accountLocked
	boolean passwordExpired

    Date dateCreated
    Date lastUpdated

    static auditable = true


    static transients = ['springSecurityService']

    static mapping = {
        id(generator: "com.omnitech.mis.utils.MyIdGenerator", type: "string", length: 32, class: String)
        password column: 'password'
    }

    static constraints = {
		username blank: false, unique: true
		password blank: false,password:true
	}

	Set<Role> getAuthorities() {
		UserRole.findAllByUser(this).collect { it.role } as Set
	}

    boolean hasRole(Role r){
        if(id == null)
            return false
        return authorities.any {r.authority == it.authority}
    }

	def beforeInsert() {
		encodePassword()
	}

	def beforeUpdate() {
		if (isDirty('password')) {
			encodePassword()
		}
	}

	protected void encodePassword() {
		password = springSecurityService.encodePassword(password)
	}

    @Override    // Override toString for a nicer / more descriptive UI
    public String toString() {
        return "${username}";
    }
}
