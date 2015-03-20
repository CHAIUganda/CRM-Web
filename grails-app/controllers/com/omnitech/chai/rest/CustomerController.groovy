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
        def customers = customerService.findAllCustomersByUser(user.id,true, params)
        def customerMaps = customers.collect { customerToMap(it) }
        log.debug("Resp:${user}  - ${customerMaps.size()} Customers")
        respond customerMaps
    }

    Map customerToMap(Customer customer) {
        def cMap = extractProperties(customer)
        def contacts = customer?.customerContacts?.collect { extractProperties(it) }
        cMap['villageName'] = customer?.village?.name
        cMap['subcountyId'] = customer?.subCounty?.uuid
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
            log.debug("updating ")
            def json = request.JSON as Map

            println(json.inspect())

            //todo maybe validate
            def customer = ModelFunctions.createObj(Customer, json) as Customer

            def subCountyUuid = json['subcountyId'] as String
            assert subCountyUuid, "You did not supply a subcounty uuid"
            def subCounty = regionService.findSubCounty(subCountyUuid)

            assert subCounty, "The Sub County ID You Supplied Does Not Exist In The DB"

            customer.uuid = json.uuid
            customer = _updateVillage(json.uuid, customer, subCounty)
            customer.lng = ChaiUtils.execSilently('Converting long to float') { json['longitude'] as Float }
            customer.lat = ChaiUtils.execSilently('Converting lat to float') { json['latitude'] as Float }
            customer.customerContacts?.each { it.id = null }
            customerService.saveCustomer(customer)
            render([status: HttpStatus.OK.reasonPhrase, message: "Success"] as JSON)
        }

    }

    /**
     * Binds copies only the neccessary properties so that other relationships do not die
     * @param customerId
     * @param customer
     * @param subCounty
     * @return
     */
    private Customer _updateVillage(String customerId, Customer customer, SubCounty subCounty) {
        def neoCustomer = customerService.findCustomer(customerId)
        if (!neoCustomer) {
            customer.subCounty = subCounty
            customer.id = null
            customer.denyUuidAlter()
            log.debug("adding new customer with uuid [$customer.uuid] Customer [$customer.outletName]")
            return customer
        }
        def whiteList = ReflectFunctions.findAllBasicFields(Customer)
        whiteList.removeAll(ModelFunctions.META_FIELDS)
        whiteList.remove('id')


        ModelFunctions.bind(neoCustomer, customer.properties, whiteList)
        neoCustomer.customerContacts = customer.customerContacts
        neoCustomer
    }

    private def handleSafely(def func) {
        try {
            func()
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