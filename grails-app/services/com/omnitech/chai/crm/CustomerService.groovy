package com.omnitech.chai.crm

import com.omnitech.chai.model.Customer
import com.omnitech.chai.util.ModelFunctions
import org.springframework.data.domain.Page

/**
 * Created by kay on 10/2/14.
 */
class CustomerService {

    def customerRepository
    def customerContactRepository

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
}

