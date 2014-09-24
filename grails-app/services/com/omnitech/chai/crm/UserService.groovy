package com.omnitech.chai.crm

import com.omnitech.chai.model.User
import com.omnitech.chai.repositories.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.neo4j.transaction.Neo4jTransactional

/**
 * UserService
 * A service class encapsulates the core business logic of a Grails application
 */
@Neo4jTransactional
class UserService {

    @Autowired
    UserRepository userRepository
    def save(User user) {



    }
}
