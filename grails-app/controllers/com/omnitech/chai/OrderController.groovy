package com.omnitech.chai

import com.omnitech.chai.model.DetailerTask
import com.omnitech.chai.model.LineItem
import com.omnitech.chai.model.Order
import com.omnitech.chai.model.Task
import com.omnitech.chai.util.ChaiUtils
import com.omnitech.chai.util.ModelFunctions
import com.omnitech.chai.util.ReflectFunctions
import com.omnitech.chai.util.ServletUtil
import fuzzycsv.FuzzyCSV
import grails.converters.JSON
import grails.transaction.Transactional
import grails.util.GrailsNameUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.neo4j.support.Neo4jTemplate

import static com.omnitech.chai.util.ModelFunctions.extractId
import static org.springframework.http.HttpStatus.*

/**
 * Created by kay on 12/10/2014.
 */
class OrderController {

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def orderService
    def taskService
    def productService
    def userService
    def regionService
    def customerService
    def txHelperService
    @Autowired
    Neo4jTemplate neo

    def index(Integer max) {
        if (params.remove('ui') == 'map') {
            redirect(action: 'map', params: params)
            return
        }
        Page<Task> page = taskService.loadPageData(max,params,Order)
        [taskInstanceList: page.content, taskInstanceCount: page.totalElements, users: userService.listAllUsers([:])]
    }

    def map(Integer max) {
        def page = taskService.loadPageData(max, params, Order)
        def mapData = page.content.collect {
            def map = ReflectFunctions.extractProperties(it)
            if (!(map.lat && map.lng)) {
                map.lat = it.customer.lat
                map.lng = it.customer.lng
            }
            return map
        } as JSON
        def jsonMapString = mapData.toString(true)
        [taskInstanceList: page.content, taskInstanceCount: page.totalElements, users: userService.listAllUsers([:]), mapData: jsonMapString]
    }

    def export() {
        def user = params.user ? userService.findUserByName(params.user) : null
        def exportFields = ['DISTRICT', 'SUBCOUNTY', 'VILLAGE', 'OUTLET NAME', 'OUTLET TYPE']
        def fields = ReflectFunctions.findAllBasicFields(DetailerTask).reverse()
        fields.removeAll('_dateLastUpdated', '_dateCreated')
        exportFields.addAll(fields.collect { GrailsNameUtils.getNaturalName(it).toUpperCase() })
        if (user) {
            def data = taskService.exportTasksForUser(user.id)
            def csvData = FuzzyCSV.toCSV(data, *exportFields)
            ServletUtil.exportCSV(response, "Tasks-${params.user}.csv", csvData)
        } else {
            def data = taskService.exportAllTasks()
            def csvData = FuzzyCSV.toCSV(data, *exportFields)
            ServletUtil.exportCSV(response, "Tasks-All.csv", csvData)
        }


    }

    def search(Integer max) {
        params.max = Math.min(max ?: 50, 100)
        if (params.term) {
            redirect(action: 'search', id: params.term)
            return
        }

        if (params.remove('ui') == 'map') {
            redirect(action: 'searchMap', params: params, id: params.term)
            return
        }

        def page = taskService.searchTasks(params.id, params)
        txHelperService.doInTransaction {
            page.content.each { neo.fetch(it.territoryUser()) }
        }
        respond page.content, view: 'index', model: [taskInstanceCount: page.totalElements, users: userService.listAllUsers([:])]
    }

    def searchMap(Integer max) {
        def page = taskService.searchTasks(params.id, params)
        txHelperService.doInTransaction {
            page.content.each { neo.fetch(it.territoryUser()) }
        }

        def mapData = page.content.collect { ReflectFunctions.extractProperties(it) } as JSON
        def jsonMapString = mapData.toString(true)
        respond page.content, view: 'map', model: [taskInstanceCount: page.totalElements, users: userService.listAllUsers([:]), mapData: jsonMapString]
    }

    def show() {
        def id = extractId(params)
        if (id == -1) {
            notFound(); return
        }

        def task = taskService.findTask(id)

        if (task.type == DetailerTask.simpleName) {
            task = taskService.findDetailerTask(task.id)
        }

        txHelperService.doInTransaction {
            neo.fetch(task.territoryUser())
        }
        [taskInstance: task]
    }

    def create() {
        def products = productService.listAllProducts()
        respond(ModelFunctions.bind(new Order(), params), model: [users: userService.listAllUsers(), products: products, jsProducts: (products as JSON).toString()])
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

    private Order toOrder(Map orderMap) {
        def customer = customerService.findCustomer(orderMap.customerId)

        assert customer, 'customer should exist in the DB'

        Order order = new Order()
        if (orderMap.id) {
            order = orderService.findOrder(orderMap.id)
            assert order, 'order should exist in database'
        }

        order.customer = customer

        def lineItems = (orderMap.lineItems as List<Map>).collect {
            toLineItem(it, order)
        }
        order.lineItems = lineItems
        order.comment = orderMap.comment

        return order
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
        def taskInstance = taskService.findTask(id)
        txHelperService.doInTransaction {
            neo.fetch(taskInstance.territoryUser())
        }
        respond taskInstance, model: [users: userService.listAllUsers(), customers: customerService.listAllCustomers()]
    }

    @Transactional
    def delete() {

        def id = extractId(params)

        if (id == -1) {
            notFound(); return
        }

        taskService.deleteTask id

        request.withFormat {
            form {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'Task.label', default: 'Task'), id])
                redirect action: "index", method: "GET"
            }
            '*' { render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'Task.label', default: 'Task'), params.id])
                redirect action: "index", method: "GET"
            }
            '*' { render status: NOT_FOUND }
        }
    }

}
