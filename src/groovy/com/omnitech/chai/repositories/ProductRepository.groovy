package com.omnitech.chai.repositories

import com.omnitech.chai.model.Product
import org.springframework.data.neo4j.repository.GraphRepository

/**
 * Created by kay on 9/25/14.
 */
interface ProductRepository extends GraphRepository<Product> {
}
