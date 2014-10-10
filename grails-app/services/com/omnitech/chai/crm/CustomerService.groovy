package com.omnitech.chai.crm

import com.omnitech.chai.model.Customer
import com.omnitech.chai.util.CypherGenerator
import com.omnitech.chai.util.ModelFunctions
import com.omnitech.chai.util.PageUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.neo4j.support.Neo4jTemplate
import org.springframework.data.neo4j.transaction.Neo4jTransactional

/**
 * Created by kay on 10/2/14.
 */
class CustomerService {

    def customerRepository
    def customerContactRepository
    @Autowired
    Neo4jTemplate neo

    /* Customers */

    Page<Customer> listCustomers(Map params) { ModelFunctions.listAll(customerRepository, params) }

    List<Customer> listAllCustomers() { customerRepository.findAll().collect() }

    Customer findCustomer(Long id) { customerRepository.findOne(id) }

    Customer saveCustomer(Customer customer) {
        ModelFunctions.saveEntity(customerRepository, customer) { Customer cust ->
            cust.customerContacts?.each { customerContactRepository.delete(it) }
        }
    }

    void deleteCustomer(Long id) { customerRepository.delete(id) }

    @Neo4jTransactional
    Page<Customer> searchCustomers(String search, Map params) {
        search = "(?i).*${search}.*".toString()
        def query = CypherGenerator.getPaginatedQuery(Customer, params).toString()
        def count = CypherGenerator.getCountQuery(Customer).toString()
        def customerSize = neo.query(count, [search: search]).to(Long).single()
        def data = neo.query(query, [search: search]).to(Customer).as(List).collect()
        return new PageImpl<Customer>(data, PageUtils.create(params), customerSize)
    }
}

