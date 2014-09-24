package com.omnitech.chai.repositories

import com.omnitech.chai.model.RequestMap
import org.springframework.data.neo4j.annotation.Query
import org.springframework.data.neo4j.repository.GraphRepository
import org.springframework.data.repository.query.Param

/**
 * Created by kay on 9/23/14.
 */
interface RequestMapRepository extends GraphRepository<RequestMap> {
    @Query("match (a:Role) where a.configAttribute = {self} return a")
    RequestMap findByConfigAtrribLike(@Param('self') String attrib)
}
