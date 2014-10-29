package com.omnitech.chai.crm

import com.omnitech.chai.model.Customer
import com.omnitech.chai.model.CustomerSegment
import com.omnitech.chai.util.ModelFunctions
import com.omnitech.chai.util.PageUtils
import org.codehaus.groovy.runtime.DefaultGroovyMethods
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.neo4j.support.Neo4jTemplate
import org.springframework.data.neo4j.transaction.Neo4jTransactional

import static com.omnitech.chai.model.Relations.*
import static java.util.Collections.EMPTY_MAP
import static org.neo4j.cypherdsl.CypherQuery.*

/**
 * Created by kay on 10/2/14.
 */
class CustomerService {

    def customerRepository
    def customerContactRepository
    def customerSegmentRepository
    @Autowired
    Neo4jTemplate neo

    /* Customers */

    Page<Customer> listCustomers(Map params) { ModelFunctions.listAll(customerRepository, params) }

    @Neo4jTransactional
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
        ModelFunctions.searchAll(neo, Customer, ModelFunctions.getWildCardRegex(search), params)
    }

    Page<Customer> findCustomersByUser(long userId, Map params) {
        def exec = start(nodesById('u', userId))
                .match(node('u').out(USER_TERRITORY).node('t').in(SC_IN_TERRITORY).node('sc').in(BELONGS_TO_SC).node('c')
        ).returns(identifier('c'))
        println exec.toString()
        customerRepository.query(exec, EMPTY_MAP, PageUtils.create(params))
    }

    /* CustomerSegments */

    List<CustomerSegment> listAllCustomerSegments() { customerSegmentRepository.findAll().collect() }

    Page<CustomerSegment> listCustomerSegments(Map params) { ModelFunctions.listAll(customerSegmentRepository, params) }

    CustomerSegment findCustomerSegment(Long id) { customerSegmentRepository.findOne(id) }

    CustomerSegment saveCustomerSegment(CustomerSegment customerSegment) {
        ModelFunctions.saveEntity(customerSegmentRepository, customerSegment)
    }

    void deleteCustomerSegment(Long id) { customerSegmentRepository.delete(id) }

    @Neo4jTransactional
    Page<CustomerSegment> searchCustomerSegments(String search, Map params) {
        ModelFunctions.searchAll(neo, CustomerSegment, ModelFunctions.getWildCardRegex(search), params)
    }
}

