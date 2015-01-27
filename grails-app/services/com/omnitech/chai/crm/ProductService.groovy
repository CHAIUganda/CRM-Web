package com.omnitech.chai.crm

import com.omnitech.chai.model.Product
import com.omnitech.chai.model.ProductGroup
import com.omnitech.chai.model.User
import com.omnitech.chai.util.ModelFunctions
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.neo4j.support.Neo4jTemplate
import org.springframework.data.neo4j.transaction.Neo4jTransactional

import static com.omnitech.chai.model.Role.ADMIN_ROLE_NAME
import static com.omnitech.chai.model.Role.SUPER_ADMIN_ROLE_NAME

/**
 * ProducService
 * A service class encapsulates the core business logic of a Grails application
 */
@Neo4jTransactional
class ProductService {

    def productRepository
    def productGroupRepository
    def territoryRepository
    @Autowired
    Neo4jTemplate neo


    Page<Product> listProducts(Map params) { ModelFunctions.listAll(productRepository, params) }

    List<Product> listAllProducts() { productRepository.findAll().collect() }

    Product findProduct(Long id) {
        productRepository.findOne(id)
    }

    Product findProductByUuid(String uuid) {
        productRepository.findByUuid(uuid)
    }

    Product saveProduct(Product product, List territoryIds) {
        def territories = territoryIds.collect { territoryRepository.findOne(it as Long) }
        product.territories = territories
        ModelFunctions.saveEntity(productRepository, product)
    }

    void deleteProduct(Long id) {
        productRepository.delete(id)
    }

    /* ProductGroups */

    List<ProductGroup> listAllProductGroups() { productGroupRepository.findAll().collect() }

    Page<ProductGroup> listProductGroups(Map params) { ModelFunctions.listAll(productGroupRepository, params) }

    ProductGroup findProductGroup(Long id) { productGroupRepository.findOne(id) }

    ProductGroup saveProductGroup(ProductGroup productGroup) {
        ModelFunctions.saveEntity(productGroupRepository, productGroup)
    }

    void deleteProductGroup(Long id) { productGroupRepository.delete(id) }

    Page<ProductGroup> searchProductGroups(String search, Map params) {
        ModelFunctions.searchAll(neo, ProductGroup, ModelFunctions.getWildCardRegex(search), params)
    }

    List<Product> findAllProductsForUser(User user) {
        if (user.hasRole(SUPER_ADMIN_ROLE_NAME, ADMIN_ROLE_NAME)) {
            return listAllProducts()
        }
        return productRepository.findAllByUser(user.id).collect()
    }

}
