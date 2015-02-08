package com.omnitech.chai

import com.omnitech.chai.model.Customer
import com.omnitech.chai.model.LineItem
import com.omnitech.chai.model.Order
import com.omnitech.chai.util.ChaiUtils
import com.omnitech.chai.util.ModelFunctions

import static com.omnitech.chai.util.ModelFunctions.extractId
import static org.springframework.http.HttpStatus.BAD_REQUEST
import static org.springframework.http.HttpStatus.OK

/**
 * Created by kay on 12/10/2014.
 */
class CallController extends TaskController {

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def productService
    def orderService

    def index(Integer max) {
        super.index max, Order, [view: '/call/index']
    }

    def map(Integer max) {
        super.map max, Order, [view: '/task/map']
    }

    def export() {
        super.export Order
    }

    def search(Integer max) {
        super.search max, Order, [view: '/call/index']
    }

    def searchMap(Integer max) {
        super.searchMap max, Order, [view: '/task/map']
    }

    def show() {
        super.show view: 'show'
    }

    def create() {
        respond(view: 'create', model: [taskInstance: ModelFunctions.createObj(Order, params)])
    }

    def saveOrUpdate() {
        try {
            def json = request.JSON as Map
            def orderInstance = toOrder(json)
            orderService.saveOrder orderInstance
            render status: OK, text: 'Success'
        } catch (Throwable x) {
            log.error('Error while saving order', x)
            render status: BAD_REQUEST, text: ChaiUtils.getBestMessage(x)
        }
    }

    def saveOrUpdateCall() {
        try {
            Order order

            if (params.id) {
                order = orderService.findOrder(params.id as Long)
                ModelFunctions.bind(order, params)
            } else {
                order = ModelFunctions.createObj(Order, params)
                order.takenBy = neoSecurityService.currentUser
            }


            Long customerId = "$params.customerId".toLongSafe()
            assert customerId, 'You Did Not Select A Customer'

            def customer = customerService.findCustomer(customerId)

            if (!customer) {
                render view: 'create', model: [taskInstance: order]
                return
            }

            assert (order.dueDate - new Date()) >= 0, 'Due Date Has To Be A Future Date'

            order.customer = customer
            orderService.saveOrder order
            redirect(action: 'show', id: order.id)
        } catch (Throwable x) {
            log.error('Error while saving order', x)
            flash.error = ChaiUtils.getBestMessage(x)
            render view: 'create', model: [taskInstance: params]
        }
    }

    private Order toOrder(Map orderMap, boolean copyLineItems) {
        def customer = customerService.findCustomer(orderMap.customerId)

        assert customer, 'customer should exist in the DB'

        Order order = new Order()
        if (orderMap.id) {
            order = orderService.findOrder(orderMap.id)
            assert order, 'order should exist in database'
        }

        order.customer = customer
        order.comment = orderMap.comment

        if (copyLineItems) {
            def lineItems = (orderMap.lineItems as List<Map>).collect {
                toLineItem(it, order)
            }
            order.lineItems = lineItems
        }
        return order
    }

    def createTaskJson() {
        super.createTaskJson { Customer c, Date dueDate -> return Order.create(c, dueDate) }
    }

    private LineItem toLineItem(Map map, Order order) {
        def product = productService.findProduct(map.productId as Long)
        assert product, 'product should exist in data base'
        assert map.quantity, 'quantity should not be null'
        return new LineItem(product: product, hasLineItem: order, quantity: map.quantity as Double, unitPrice: map.unitPrice as Double)
    }

    def edit() {
        def id = extractId(params)

        if (id == -1) {
            notFound(); return
        }
        def taskInstance = taskService.findOrder(id)
        render(view: 'create', model: [taskInstance: taskInstance])
    }


}
