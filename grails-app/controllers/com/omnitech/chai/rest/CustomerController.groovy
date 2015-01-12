package com.omnitech.chai.rest

import com.omnitech.chai.model.Customer
import com.omnitech.chai.model.SubCounty
import com.omnitech.chai.model.User
import com.omnitech.chai.util.ChaiUtils
import com.omnitech.chai.util.ModelFunctions
import com.omnitech.chai.util.ReflectFunctions
import grails.converters.JSON
import grails.validation.ValidationException
import org.springframework.http.HttpStatus

import static com.omnitech.chai.util.ReflectFunctions.extractProperties
import static org.springframework.http.HttpStatus.BAD_REQUEST

/**
 * Created by kay on 10/29/14.
 */
class CustomerController {

    static namespace = 'rest'
    static responseFormats = ['json', 'xml']
    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]
    def customerService
    def neoSecurityService
    def regionService

    def list(Integer max) {
        params.max = Math.min(max ?: 10, 2000)
        def user = neoSecurityService.currentUser as User
        log.debug("Req:${user}  - CustomerList: $params")
        def customers = customerService.findAllCustomersByUser(user.id, params)
        def customerMaps = customers.collect { customerToMap(it) }
        log.debug("Resp:${user}  - ${customerMaps.size()} Customers")
        respond customerMaps
    }

    Map customerToMap(Customer customer) {
        def cMap = extractProperties(customer)
        def contacts = customer?.customerContacts?.collect { extractProperties(it) }
        cMap['villageName'] = customer?.village?.name
        cMap['subcountyId'] = customer?.subCounty?.id
        cMap['parishName'] = customer?.parish?.name

        //todo to fix on upload
        cMap['longitude'] = customer.lng
        cMap['latitude'] = customer.lat
        cMap.with {
            remove('lat')
            remove('lng')
            remove('segmentScore')
            remove('wkt')
            remove('pictureURL')
        }
        if (contacts) {
            cMap['customerContacts'] = contacts
        }
        return cMap
    }

    def update() {
        handleSafely {
            def json = request.JSON as Map

            println(json.inspect())

            def customer = ModelFunctions.createObj(Customer, json) as Customer

//        if (!customer.validate()) {
//            response.status = HttpStatus.BAD_REQUEST.value()
//            render(customer.errors as JSON)
//            return
//        }

            def subCountyUuid = json['subcountyUuid'] as String
            assert subCountyUuid, "You did not supply a subcounty uuid"
            def subCounty = regionService.findSubCounty(subCountyUuid)

            assert subCounty, "The Sub County ID You Supplied Does Not Exist In The DB"

            customer.uuid = json.uuid
            customer = _updateVillage(json.uuid, customer, subCounty)
            customer.lng = ChaiUtils.execSilently('Converting long to float') { json['longitude'] as Float }
            customer.lat = ChaiUtils.execSilently('Converting lat to float') { json['latitude'] as Float }
            customer.customerContacts?.each { it.id = null }
            customerService.saveCustomer(customer)

        }

    }

    private Customer _updateVillage(String customerId, Customer customer, SubCounty subCounty) {
//        def neoCustomer = customerService.findCustomer(customer.id)
        def neoCustomer = customerService.findCustomer(customerId)
        if (!neoCustomer) {
            customer.subCounty = subCounty
            customer.id = null
            return customer
        }
        def whiteList = ReflectFunctions.findAllBasicFields(Customer)
        whiteList.removeAll(ModelFunctions.META_FIELDS)
        ModelFunctions.bind(neoCustomer, customer.properties, whiteList)
    }

    def renderError(String error) {
        response.status = HttpStatus.INTERNAL_SERVER_ERROR.value()
        render([status: 'error', message: error] as JSON)
    }

    def searchByName(String id) {

        String term = id ?: params.term
        if (!term) {
            respond([])
            return
        }

        respond customerService
                .searchCustomers(term, [sort: 'outletName'])
                .content
                .collect {
            [id        : it.id,
             district  : it.subCounty.district.name,
             outletName: it.outletName,
             contact   : it.customerContacts?.iterator()?.next()?.contact
            ]
        }
    }


    private def handleSafely(def func) {
        try {
            func()
            render([status: HttpStatus.OK.reasonPhrase, message: "Success"] as JSON)
        } catch (ValidationException x) {
            def ms = new StringBuilder()
            x.errors.allErrors.each {
                ms << message(error: it)
            }
            log.error("** Error while handling request: $ms \n $params", x)
            render(status: BAD_REQUEST, text: [status: BAD_REQUEST.reasonPhrase, message: ms] as JSON)
        } catch (Throwable x) {
            log.error("Error while handling request: \n $params", x)
            render(status: BAD_REQUEST, text: [status: BAD_REQUEST.reasonPhrase, message: ChaiUtils.getBestMessage(x)] as JSON)
        }
    }

}