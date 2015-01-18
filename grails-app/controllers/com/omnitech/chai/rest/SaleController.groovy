package com.omnitech.chai.rest

import com.omnitech.chai.model.*
import com.omnitech.chai.util.ChaiUtils
import com.omnitech.chai.util.ModelFunctions
import com.omnitech.chai.util.ReflectFunctions
import grails.converters.JSON
import grails.validation.ValidationException
import org.springframework.http.HttpStatus

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

            assert json.clientRefId, 'ClientRefId Should exist in the request'
            def dupeSale = taskService.findDirectSaleByClientRefId(json.clientRefId)

            assert !dupeSale, 'Duplicate Sale'

            def sale = toDirectSale(json)
            //explicitly remove the id
            sale.id = null
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

            def saleOrder = toOrder(json, SaleOrder)
            bindSaleOrderToDbInstance(saleOrder, order)
            saleOrder.completedBy(neoSecurityService.currentUser)
            taskService.saveTask(saleOrder)
        }
    }

    def placeOrder() {
        handleSafely {
            def json = request.JSON as Map
            json.remove('id')

            assert json.clientRefId, 'ClientRefId Should exist in the request'
            def dupeOrder = taskService.findOrderByClientRefId(json.clientRefId)

            assert !dupeOrder, 'Duplicate Order'
            def order = toOrder(json, Order)

            order.customer = customerService.findCustomer(json.customerId as String)
            assert order.customer, "Customer Has To Exist In the System [$json.customerId]"

            order.takenBy = neoSecurityService.currentUser
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
        dupeMap.remove('adhockSalesDatas')
        def ds = ModelFunctions.createObj(DirectSale, dupeMap)
        ds.lineItems = map.adhockSalesDatas.collect { toLineItem(it, ds) }
        ds.customer = customerService.findCustomer(map.customerId as String)
        assert ds.customer, "Customer Has To Exist In the System [$map.customerId]"
        return ds
    }

    private <T extends Order> T toOrder(Map map, Class<T> typeOfOrder) {
        def dupeMap = new HashMap(map)
        dupeMap.remove('salesDatas')
        dupeMap.remove('orderDatas')
        def saleOrder = ModelFunctions.createObj(typeOfOrder, dupeMap)
        if (typeOfOrder == Order)
            saleOrder.lineItems = map.orderDatas.collect { toLineItem(it, saleOrder) }
        else
            saleOrder.lineItems = map.salesDatas.collect { toLineItem(it, saleOrder) }

        if (map.deliveryDate) {
            ChaiUtils.execSilently { saleOrder.dueDate = new Date(map.deliveryDate as Long) }
        }

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
