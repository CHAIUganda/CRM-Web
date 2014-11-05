package com.omnitech.chai.crm

import com.omnitech.chai.exception.ImportException
import com.omnitech.chai.model.Customer
import com.omnitech.chai.model.CustomerContact
import com.omnitech.chai.model.CustomerSegment
import com.omnitech.chai.util.ModelFunctions
import com.xlson.groovycsv.CsvParser
import com.xlson.groovycsv.PropertyMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.neo4j.support.Neo4jTemplate
import org.springframework.data.neo4j.transaction.Neo4jTransactional

import static com.omnitech.chai.model.Relations.*
import static com.omnitech.chai.util.ChaiUtils.execSilently
import static org.neo4j.cypherdsl.CypherQuery.*

/**
 * Created by kay on 10/2/14.
 */
class CustomerService {

    def customerRepository
    def regionService
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

    @Neo4jTransactional
    List<Customer> findAllCustomersByUser(long userId, Map params) {
        def customer = Customer.simpleName.toLowerCase()
        def exec = start(nodesById('u', userId))
                .match(node('u').out(USER_TERRITORY).node('t').in(SC_IN_TERRITORY).node('sc').out(HAS_PARISH).node('p').out(HAS_VILLAGE).node('v').in(CUST_IN_VILLAGE).node(customer)
        ).returns(identifier(customer))
        ModelFunctions.query(customerRepository, exec, params, Customer).collect()
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

    @Neo4jTransactional
    def processCustomers(String s) {
        def csv = CsvParser.parseCsv(s)

        csv.eachWithIndex { PropertyMapper record, idx ->
            try {
                processRecord(record, idx)
            } catch (Throwable ex) {
                def e = new ImportException("Error on Record[${idx + 2}]: $ex.message".toString())
                e.stackTrace = ex.stackTrace
                throw e
            }
        }
    }

    private processRecord(PropertyMapper mapper, int idx) {
        String regionName = prop(mapper, idx, 'Region name')
        def region = regionService.getOrCreateRegion(regionName)

        String districtName = prop(mapper, idx, 'District name')
        def district = regionService.getOrCreateDistrict(region, districtName)

        String subCountyName = prop(mapper, idx, 'Sub-county Name')
        def subCounty = regionService.getOrCreateSubCounty(district, subCountyName)

        String parishName = prop(mapper, idx, 'Parish Name')
        def parish = regionService.getOrCreateParish(subCounty, parishName)

        String villageName = prop(mapper, idx, 'Village name')
        def village = regionService.getOrCreateVillage(parish, villageName)

        def customer = new Customer(
                descriptionOfOutletLocation: prop(mapper, idx, 'EA name'),
                outletName: prop(mapper, idx, 'Name of the outlet / facility',true, '(NO NAME) OUTLET'),
        )

        customer.outletType = prop(mapper, idx, 'Outlet type', false).replaceFirst(/\d\s*\-\s*/, '')//1 - DrugShop
        def lat = prop(mapper, idx, 'GPS Latitude', false)
        def lng = prop(mapper, idx, 'GPS Longitude', false)

        execSilently("Converting Lat[$lat] to GPS") {
            customer.lat = lat?.replace('S', '-')?.replace('N','')?.toDouble()
        }
        execSilently("Converting Lng[$lng] GPS") {
            customer.lng = lng?.replace('E', '')?.replace('W','-')?.toDouble()
        }

        def customerContact = new CustomerContact(
                contact: prop(mapper, idx, 'Provider/Owner Contact Number', false),
                name: prop(mapper, idx, 'Provider/Owner Name', false),
        )

        customer.village = village
        customer.customerContacts = [customerContact] as Set

        customerRepository.save(customer)
    }

    private
    static String prop(PropertyMapper mapper, int idx, String name, boolean required = true, String defaultValue = null) {
        assert mapper.columns.containsKey(name), "Record ${idx + 2} should have a [$name]"
        def value = mapper.propertyMissing(name)?.toString()?.trim()
        if (required && !value) {
            if (defaultValue) return defaultValue
            throw new ImportException("Record [${idx + 2}] has an Empty Cell[$name] that is Required")
        }
        return value
    }


}

