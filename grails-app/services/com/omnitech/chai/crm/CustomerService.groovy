package com.omnitech.chai.crm

import com.omnitech.chai.exception.ImportException
import com.omnitech.chai.model.*
import com.omnitech.chai.util.ModelFunctions
import com.omnitech.chai.util.PageUtils
import com.omnitech.chai.util.ReflectFunctions
import fuzzycsv.FuzzyCSV
import fuzzycsv.Record
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.neo4j.support.Neo4jTemplate
import org.springframework.data.neo4j.transaction.Neo4jTransactional

import static com.omnitech.chai.model.Relations.*
import static com.omnitech.chai.util.ChaiUtils.execSilently
import static com.omnitech.chai.util.ChaiUtils.prop
import static fuzzycsv.RecordFx.fn
import static org.neo4j.cypherdsl.CypherQuery.*

/**
 * Created by kay on 10/2/14.
 */
@Neo4jTransactional
class CustomerService {

    def customerRepository
    def regionService
    def customerContactRepository
    def customerSegmentRepository
    def wholeSalerRepository
    def subCountyRepository
    @Autowired
    Neo4jTemplate neo

    /* Customers */

    Page<Customer> listCustomers(Map params) { ModelFunctions.listAll(neo, Customer, params, Customer) }

    List<Customer> listAllCustomers() { customerRepository.findAll().collect() }

    Page<Customer> listCustomersInCtx(Long userId, Map params) {
        def customer = Customer.simpleName.toLowerCase()
        def startQuery = {
            start(nodesById('u', userId))
                    .match(node('u').out(USER_TERRITORY, SUPERVISES_TERRITORY)
                    .node('t').in(SC_IN_TERRITORY)
                    .node('sc').in(CUST_IN_SC).node(customer))
        }
        def q = startQuery().returns(distinct(identifier(customer)))
        PageUtils.addPagination(q, params, Customer)
        def cq = startQuery().returns(count(distinct(identifier(customer))))
        ModelFunctions.query(neo, q, cq, params, Customer)
    }

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
                [class: Customer.simpleName, patterns: ['outletName', 'keyWholeSalerName', 'descriptionOfOutletLocation', 'split', 'subCounty', 'village', 'parish']],
                [class: Village.simpleName, patterns: ['name']],
                [class: Parish.simpleName, patterns: ['name']],
                [class: SubCounty.simpleName, patterns: ['name', 'district']],
                [class: District.simpleName, patterns: ['name']]
        ]]
        ModelFunctions.searchAll(neo, Customer, ModelFunctions.getWildCardRegex(search), params, Integer.MAX_VALUE, filters)
    }

    @Neo4jTransactional
    List<Customer> findAllCustomersByUser(long userId, Boolean isActive, Map params) {
        def customer = Customer.simpleName.toLowerCase()
        def exec = start(nodesById('u', userId))
                .match(node('u').out(USER_TERRITORY).node('t').in(SC_IN_TERRITORY).node('sc').in(CUST_IN_SC).node(customer))
                .where(identifier('u').property('isActive').eq(isActive)
                .or(not(has(identifier('u').property('isActive'))))
        ).returns(distinct(identifier(customer)))
        log.trace("findAllCustomersByUser $exec")
        ModelFunctions.query(customerRepository, exec, params, Customer).collect()
    }

    Page<Customer> findCustomersBySegment(Long segmentId, Map params) {

        def customers = start(nodesById('segment', segmentId))
                .match(node('segment').out(IN_SEGMENT).node('customer'))
                .returns(identifier('customer'))

        customerRepository.query(customers, Collections.EMPTY_MAP)

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

        //Processing Regions
        String regionName = prop(record, 'Region')
        def region = regionService.getOrCreateRegion(regionName)

        String districtName = prop(record, 'District')
        def district = regionService.getOrCreateDistrict(region, districtName)

        String subCountyName = prop(record, 'subCounty', true, "$districtName-DummySubCounty")
        def subCounty = regionService.getOrCreateSubCounty(district, subCountyName)

        String parishName = prop(record, 'parish', true, "$subCountyName-DummyParish")
        def parish = regionService.getOrCreateParish(parishName)

        String villageName = prop(record, 'village', true, "$parishName-DummyVillage")
        def village = regionService.getOrCreateVillage(villageName)
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
                networkOrAssociationName: prop(record, 'networkOrAssociationName', false)

        )

        customer.village = village
        customer.parish = parish
        customer.subCounty = subCounty
        customer.customerContacts = [customerContact] as Set

        //add other customer contact if existent.
        //todo automate this
        if (prop(record, 'surname2', false) || prop(record, 'firstName2', false)) {
            def customerContact2 = new CustomerContact(
                    title: prop(record, 'title2', false),
                    firstName: prop(record, 'firstName2', false),
                    surname: prop(record, 'surname2', false),
                    contact: prop(record, 'contact2', false),
                    gender: prop(record, 'gender2', false)?.toLowerCase(),
                    role: prop(record, 'role2', false),
                    qualification: prop(record, 'qualification2', false),
                    networkOrAssociationName: prop(record, 'networkOrAssociationName2', false)

            )
            customer.customerContacts << customerContact2
        }

        customerRepository.save(customer)
    }

    // WholeSalers
    List<WholeSaler> listAllWholeSalers() { wholeSalerRepository.findAll().collect() }

    Page<WholeSaler> listWholeSalers(Map params) { ModelFunctions.listAll(wholeSalerRepository, params) }

    WholeSaler findWholeSaler(Long id) { wholeSalerRepository.findOne(id) }

    WholeSaler findWholeSaler(String uuid) { wholeSalerRepository.findByUuid(uuid) }

    WholeSaler saveWholeSaler(WholeSaler wholeSaler) { ModelFunctions.saveEntity(wholeSalerRepository, wholeSaler) }

    void deleteWholeSaler(Long id) { wholeSalerRepository.delete(id) }

    @Neo4jTransactional
    Page<WholeSaler> searchWholeSalers(String search, Map params) {
        ModelFunctions.searchAll(neo, WholeSaler, ModelFunctions.getWildCardRegex(search), params)
    }

    @Neo4jTransactional
    void mapWholeSalerToSubs(long id, long districtId, List<Long> scIds) {
        def wholeSaler = wholeSalerRepository.findOne(id)
        def scToBeMapped = scIds?.collect { subCountyRepository.findOne(it as Long) }

        assert scToBeMapped.every { it.district.id == districtId }

        neo.fetch(wholeSaler.subCounties)
        wholeSaler.subCounties.each {
            if (it.district.id == districtId && !scIds.contains(it.id)) {
                it.wholeSaler = null
                subCountyRepository.save(it)
            }
        }

        scToBeMapped.each {
            it.wholeSaler = wholeSaler
            subCountyRepository.save(it)
        }
    }


}

