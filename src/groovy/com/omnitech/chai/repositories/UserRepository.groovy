package com.omnitech.chai.repositories

import com.omnitech.chai.model.User
import org.springframework.data.neo4j.repository.GraphRepository

/**
 * Created by kay on 9/23/14.
 */
interface UserRepository extends GraphRepository<User> {

    User findByUsername(String username)



}
