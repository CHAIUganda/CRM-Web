package com.omnitech.chai.crm

import com.omnitech.chai.exception.ImportException
import com.omnitech.chai.model.*
import com.omnitech.chai.util.ModelFunctions
import com.omnitech.chai.util.ReflectFunctions
import fuzzycsv.FuzzyCSV
import fuzzycsv.Record
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.neo4j.support.Neo4jTemplate
import org.springframework.data.neo4j.transaction.Neo4jTransactional

import static com.omnitech.chai.model.Relations.*
import static com.omnitech.chai.util.ChaiUtils.execSilently
import static fuzzycsv.RecordFx.fn
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

    Customer findCustomer(String uuid) { uuid ? customerRepository.findByUuid(uuid) : null }

    void deleteCustomer(Long id) { customerRepository.delete(id) }

    @Neo4jTransactional
    Page<Customer> searchCustomers(String search, Map params) {
        def customerFields = ReflectFunctions.findAllBasicFields(Customer)
        customerFields.add('village')
        def filters = [allow: [
                [class: Customer.simpleName, patterns: customerFields],
                [class: Village.simpleName, patterns: ['name', 'parish']],
                [class: Parish.simpleName, patterns: ['name', 'subCounty']],
                [class: SubCounty.simpleName, patterns: ['name', 'district']],
                [class: District.simpleName, patterns: ['name', 'region']],
                [class: Region.simpleName, patterns: ['name']]
        ]]
        ModelFunctions.searchAll(neo, Customer, ModelFunctions.getWildCardRegex(search), params, Integer.MAX_VALUE, filters)
    }

    @Neo4jTransactional
    List<Customer> findAllCustomersByUser(long userId, Map params) {
        def customer = Customer.simpleName.toLowerCase()
        def exec = start(nodesById('u', userId))
                .match(node('u').out(USER_TERRITORY).node('t').in(SC_IN_TERRITORY).node('sc').out(HAS_PARISH).node('p').out(HAS_VILLAGE).node('v').in(CUST_IN_VILLAGE).node(customer)
        ).returns(distinct(identifier(customer)))
        log.trace("findAllCustomersByUser $exec")
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
        def csv = FuzzyCSV.parseCsv(s)
        csv = csv.collect { it as List<String> }

        FuzzyCSV.map(csv, fn { Record record ->
            try {
                processRecord(record)
            } catch (Throwable ex) {
                def e = new ImportException("Error on Record[${record.idx()}]: $ex.message".toString())
                e.stackTrace = ex.stackTrace
                throw e
            }
        })
    }

    private processRecord(Record record) {
        String regionName = prop(record, 'Region')
        def region = regionService.getOrCreateRegion(regionName)

        String districtName = prop(record, 'District')
        def district = regionService.getOrCreateDistrict(region, districtName)

        String subCountyName = prop(record, 'subCounty', true, "$districtName-DummySubCounty")
        def subCounty = regionService.getOrCreateSubCounty(district, subCountyName)

        String parishName = prop(record, 'parish', true, "$subCountyName-DummyParish")
        def parish = regionService.getOrCreateParish(subCounty, parishName)

        String villageName = prop(record, 'village', true, "$parishName-DummyVillage")
        def village = regionService.getOrCreateVillage(parish, villageName)
        record as Map



        def customer = ModelFunctions.createObj(Customer, record.toMap())

        def lat = prop(record, 'lat', false)
        def lng = prop(record, 'lng', false)
        def restockFrequency = prop(record, 'restockFrequency', false)

        execSilently("Converting Lat[$lat] to GPS") {
            customer.lat = lat?.replace('S', '-')?.replace('N', '')?.toDouble()
        }
        execSilently("Converting Lng[$lng] GPS") {
            customer.lng = lng?.replace('E', '')?.replace('W', '-')?.toDouble()
        }

        execSilently("Converting Restock Frequency To Int ${restockFrequency}") {
            customer.restockFrequency = restockFrequency?.toInteger()
        }

        def customerContact = new CustomerContact(
                title: prop(record, 'title', false),
                firstName: prop(record, 'firstName', false),
                surname: prop(record, 'surname', false),
                contact: prop(record, 'contact', false),
                gender: prop(record, 'gender', false),
                role: prop(record, 'role', false),
                qualification: prop(record, 'qualification', false),
                networkOrAssociation: execSilently("Converting Network Or Association") { prop(record, 'parish', true) }

        )

        customer.village = village
        customer.customerContacts = [customerContact] as Set

        customerRepository.save(customer)
    }

    private
    static String prop(Record mapper, String name, boolean required = true, String defaultValue = null) {
        if (required) {
            assert mapper.derivedHeaders.contains(name), "Record ${mapper.idx()} should have a [$name]"
        } else if (!mapper.derivedHeaders.contains(name)) {
            return null
        }

        def value = mapper.propertyMissing(name)?.toString()?.trim()
        if (required && !value) {
            if (defaultValue) return defaultValue
            throw new ImportException("Record [${mapper.idx()}] has an Empty Cell[$name] that is Required")
        }
        return value
    }


}

