package com.omnitech.chai.rest

import com.omnitech.chai.model.*
import com.omnitech.chai.util.ChaiUtils
import com.omnitech.chai.util.ModelFunctions
import com.omnitech.chai.util.ReflectFunctions
import grails.converters.JSON
import grails.validation.ValidationException

import static org.springframework.http.HttpStatus.BAD_REQUEST

/**
 * SaleController
 * A controller class handles incoming web requests and performs actions such as redirects, rendering views and so on.
 */
class SaleController {

    static namespace = 'rest'
    static responseFormats = ['json', 'xml']
//    static allowedMethods = [directSale: "POST", update: "PUT", delete: "DELETE"]
    def productService
    def taskService
    def neoSecurityService
    def customerService

    def directSale() {

        handleSafely {
            def json = request.JSON as Map
            def sale = toDirectSale(json)
            sale.completedBy(neoSecurityService.currentUser)
            taskService.saveTask(sale)
        }

    }

    def saleOrder() {

        handleSafely {
            def json = request.JSON as Map

            //find original order
            def order = taskService.findOrder(json.orderId as String)
            assert order, "Order should exist in the database"

            def saleOrder = toOrder(json,SaleOrder)
            bindSaleOrderToDbInstance(saleOrder, order)
            saleOrder.completedBy(neoSecurityService.currentUser)
            taskService.saveTask(saleOrder)
        }
    }

    def placeOrder() {
        handleSafely {
            def json = request.JSON as Map

            assert json.clientRefId, 'ClientRefId Should exist in the request'
            def dupeOrder = taskService.findOrderByClientRefId(json.clientRefId)

            assert !dupeOrder, 'Duplicate Order'
            def order = toOrder(json,Order)

            order.customer = customerService.findCustomer(json.customerId as String)
            assert order.customer, "Customer Has To Exist In the System [$json.customerId]"

            taskService.saveTask(order)
        }
    }

    private static void bindSaleOrderToDbInstance(SaleOrder saleOrder, Order order) {
        //copy original order props to saleOrder
        def whiteList = ReflectFunctions.findAllFields(Task).collect { it.name }
        whiteList << 'comment'
        ModelFunctions.bind(saleOrder, order.properties, whiteList)
        //Explicitly copy the ID ModelFunctions ignores this
        saleOrder.id = order.id
    }

    private DirectSale toDirectSale(Map map) {
        def dupeMap = new HashMap(map)
        dupeMap.remove('salesDatas')
        def ds = ModelFunctions.createObj(DirectSale, dupeMap)
        ds.lineItems = map.salesDatas.collect { toLineItem(it, ds) }
        ds.customer = customerService.findCustomer(map.customerId as String)
        assert ds.customer, "Customer Has To Exist In the System [$map.customerId]"
        return ds
    }

    private <T extends Order> T toOrder(Map map, Class<T> typeOfOrder) {
        def dupeMap = new HashMap(map)
        dupeMap.remove('salesDatas')
        def saleOrder = ModelFunctions.createObj(typeOfOrder, dupeMap)
        saleOrder.lineItems = map.salesDatas.collect { toLineItem(it, saleOrder) }
        return saleOrder
    }

    private LineItem toLineItem(Map map, HasLineItem directSale) {

        def product = productService.findProductByUuid(map.productId as String)

        assert product, "Product with id [$map.productId] Should Exist In the DB"

        def lineItem = new LineItem(
                product: product,
                hasLineItem: directSale,
                quantity: map.quantity as Double,
                unitPrice: map.price as Double)

        if (!lineItem.validate())
            throw new ValidationException("Error Validating LineItem", lineItem.errors)

        return lineItem

    }

    private def handleSafely(def func) {
        try {
            func()
            render 'Success'
        } catch (ValidationException x) {
            def ms = new StringBuilder()
            x.errors.allErrors.each {
                ms << message(error: it)
            }
            log.error("** Error while handling request: $ms \n $params", x)
            render(status: BAD_REQUEST, text: [status: BAD_REQUEST.reasonPhrase, message: ms] as JSON)
        } catch (Throwable x) {
            x.printStackTrace()
            log.error("Error while handling request: \n $params", x)
            render(status: BAD_REQUEST, text: [status: BAD_REQUEST.reasonPhrase, text: ChaiUtils.getBestMessage(x)] as JSON)
        }
    }


}
