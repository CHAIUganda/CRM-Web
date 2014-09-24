package com.omnitech.chai.repositories

import com.omnitech.chai.model.Role
import org.springframework.data.neo4j.annotation.Query
import org.springframework.data.neo4j.repository.GraphRepository

/**
 * Created by kay on 9/23/14.
 */
interface RoleRepository extends GraphRepository<Role> {



}
