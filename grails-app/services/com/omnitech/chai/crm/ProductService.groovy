package com.omnitech.chai.crm

import com.omnitech.chai.model.Product
import com.omnitech.chai.repositories.ProductRepository
import com.omnitech.chai.util.ModelFunctions
import org.springframework.data.domain.Page
import org.springframework.data.neo4j.transaction.Neo4jTransactional

/**
 * ProducService
 * A service class encapsulates the core business logic of a Grails application
 */
@Neo4jTransactional
class ProductService {

    ProductRepository productRepository

    Page<Product> listProducts(Map params) {
        ModelFunctions.listAll(productRepository, params)
    }

    Product findProduct(Long id) {
        productRepository.findOne(id)
    }

    Product saveProduct(Product product) {
        ModelFunctions.saveEntity(productRepository, product)
    }

    void deleteProduct(Long id) {
        productRepository.delete(id)
    }

}
