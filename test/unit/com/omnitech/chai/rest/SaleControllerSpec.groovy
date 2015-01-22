package com.omnitech.chai.rest

import com.omnitech.chai.crm.CustomerService
import com.omnitech.chai.crm.NeoSecurityService
import com.omnitech.chai.crm.ProductService
import com.omnitech.chai.crm.TaskService
import com.omnitech.chai.model.*
import grails.test.mixin.TestFor
import spock.lang.Specification

/**
 * See the API for {@link grails.test.mixin.web.ControllerUnitTestMixin} for usage instructions
 */
@TestFor(SaleController)
class SaleControllerSpec extends Specification {

    def setup() {
    }

    def cleanup() {
        controller.with {
            taskService = null
            customerService = null
            productService = null
        }

    }

    void "test valid JSON Sale Request"() {

        def data = '''{
  "customerId" : "cccc",
   "clientRefId":"clientRefId1",
  "howManyZincInStock": 5,
  "howManyOrsInStock": 3,
  "pointOfSaleMaterial": "counting,ors",
  "recommendationNextStep": "start recommending ors",
  "recommendationLevel": "Level 2",
  "governmentApproval": true,
  "dateOfSale": "2013-01-3 04:05:40",
  "adhockSalesDatas": [
    {
      "quantity": 5,
      "price": 454,
      "productId": "xxxx-xxxx"
    },
    {
      "quantity": 2,
      "price": 300,
      "productId": "yyyy-yyyyy"
    }
  ]
}'''

        def directSaleValidator = { DirectSale ds ->
            assert ds.howManyZincInStock == 5
            assert ds.howManyOrsInStock == 3
            assert ds.pointOfSaleMaterial == 'counting,ors'
            assert ds.recommendationNextStep == 'start recommending ors'
            assert ds.recommendationLevel == 'Level 2'
            assert ds.governmentApproval
//            assert ds.dateOfSale != null

            assert ds.lineItems.size() == 2
            assert ds.lineItems.find { it.quantity == 2 }
            assert ds.lineItems.find { it.unitPrice == 300 }

            assert ds.lineItems.find { it.quantity == 5 }
            assert ds.lineItems.find { it.unitPrice == 454 }

            assert ds.isComplete()
            assert ds.completedBy
            assert ds.customer
            assert ds.id == null

            return true
        }

        ProductService productService = Mock()
        TaskService taskService = Mock()
        NeoSecurityService securityService = Mock()
        CustomerService customerService = Mock()

        controller.productService = productService
        controller.taskService = taskService
        controller.neoSecurityService = securityService
        controller.customerService = customerService


        when:
        request.json = data
        controller.directSale()

        then:
        1 * productService.findProductByUuid('xxxx-xxxx') >> new Product()
        1 * productService.findProductByUuid('yyyy-yyyyy') >> new Product()
        1 * securityService.currentUser >> new User()
        1 * taskService.saveTask({ directSaleValidator(it) })
        1 * customerService.findCustomer('cccc') >> new Customer()
        response.contentAsString == '{"status":"OK","message":"Success"}'

    }

    void "test invalid Line Item Request"() {

        def data = '''{
  "howManyZincInStock": 5,
   "clientRefId":"clientRefId1",
  "howManyOrsInStock": 3,
  "pointOfSaleMaterial": "counting,ors",
  "recommendationNextStep": "start recommending ors",
  "recommendationLevel": "Level 2",
  "governmentApproval": true,
  "dateOfSale": "2013-01-3 04:05:40",
  "adhockSalesDatas": [
    {
      "quantity": 0,
      "price": 454,
      "productId": "xxxx-xxxx"
    },
    {
      "quantity": 2,
      "price": 300,
      "productId": "yyyy-yyyyy"
    }
  ]
}'''

        ProductService productService = Mock()
        TaskService taskService = Mock()
        NeoSecurityService securityService = Mock()

        controller.productService = productService
        controller.taskService = taskService
        controller.neoSecurityService = securityService


        when:
        request.json = data
        controller.directSale()

        then:
        1 * taskService.findDirectSaleByClientRefId('clientRefId1') >> null
        1 * productService.findProductByUuid('xxxx-xxxx') >> new Product()
        0 * taskService.saveTask(_)
        response.contentAsString ==~ ".*is less than minimum value.*"

    }

    void "test duplicate Direct Sale Request"() {

        def data = '''{
  "howManyZincInStock": 5,
   "clientRefId":"clientRefId1",
  "howManyOrsInStock": 3,
  "pointOfSaleMaterial": "counting,ors",
  "recommendationNextStep": "start recommending ors",
  "recommendationLevel": "Level 2",
  "governmentApproval": true,
  "dateOfSale": "2013-01-3 04:05:40",
  "salesDatas": [
    {
      "quantity": 0,
      "price": 454,
      "productId": "xxxx-xxxx"
    },
    {
      "quantity": 2,
      "price": 300,
      "productId": "yyyy-yyyyy"
    }
  ]
}'''

        ProductService productService = Mock()
        TaskService taskService = Mock()
        NeoSecurityService securityService = Mock()

        controller.productService = productService
        controller.taskService = taskService
        controller.neoSecurityService = securityService


        when:
        request.json = data
        controller.directSale()

        then:
        1 * taskService.findDirectSaleByClientRefId('clientRefId1') >> new DirectSale()
        response.contentAsString ==~ ".*Duplicate.*"

    }

    void "test valid JSON SaleOrder Request"() {

        def data = '''{
  "customerId" : "cccc",
  "orderId":"oooo",
  "howManyZincInStock": 5,
  "howManyOrsInStock": 3,
  "pointOfSaleMaterial": "counting,ors",
  "recommendationNextStep": "start recommending ors",
  "recommendationLevel": "Level 2",
  "governmentApproval": true,
  "dateOfSale": "2013-01-3 04:05:40",
  "salesDatas": [
    {
      "quantity": 5,
      "price": 454,
      "productId": "xxxx-xxxx"
    },
    {
      "quantity": 2,
      "price": 300,
      "productId": "yyyy-yyyyy"
    }
  ]
}'''

        def directSaleValidator = { SaleOrder sale ->
            assert sale.howManyZincInStock == 5
            assert sale.howManyOrsInStock == 3
            assert sale.pointOfSaleMaterial == 'counting,ors'
            assert sale.recommendationNextStep == 'start recommending ors'
            assert sale.recommendationLevel == 'Level 2'
            assert sale.governmentApproval
//            assert ds.dateOfSale != null

            assert sale.lineItems.size() == 2
            assert sale.lineItems.find { it.quantity == 2 }
            assert sale.lineItems.find { it.unitPrice == 300 }

            assert sale.lineItems.find { it.quantity == 5 }
            assert sale.lineItems.find { it.unitPrice == 454 }

            assert sale.isComplete()
            assert sale.completedBy
            assert sale.customer
            assert sale.comment
            assert sale.id == 5

            return true
        }

        ProductService productService = Mock()
        TaskService taskService = Mock()
        NeoSecurityService securityService = Mock()

        controller.productService = productService
        controller.taskService = taskService
        controller.neoSecurityService = securityService


        when:
        request.json = data
        controller.saleOrder()

        then:
        1 * productService.findProductByUuid('xxxx-xxxx') >> new Product()
        1 * productService.findProductByUuid('yyyy-yyyyy') >> new Product()
        1 * securityService.currentUser >> new User()
        1 * taskService.findOrder('oooo') >> new Order(customer: new Customer(), comment: "SSjj", id: 5)
        1 * taskService.saveTask({ directSaleValidator(it) })
        response.contentAsString == '{"status":"OK","message":"Success"}'

    }

    void "test valid JSON placeOrder Request"() {
        def data = '''{
  "customerId" : "cccc",
  "orderId":"oooo",
  "clientRefId":"clientRefId1",
  "dateOfSale": "2013-01-3 04:05:40",
  "comment":"Some weird comment",
  "orderDatas": [
    {
      "quantity": 5,
      "price": 454,
      "productId": "xxxx-xxxx"
    },
    {
      "quantity": 2,
      "price": 300,
      "productId": "yyyy-yyyyy"
    }
  ]
}'''

        def validateOrder = { Order order ->

            assert order.comment == 'Some weird comment'
            assert order.lineItems.size() == 2
            assert order.lineItems.find { it.quantity == 2 }
            assert order.lineItems.find { it.unitPrice == 300 }

            assert order.lineItems.find { it.quantity == 5 }
            assert order.lineItems.find { it.unitPrice == 454 }

            assert !order.isComplete()
            assert !order.completedBy
            assert order.customer

            return true
        }

        ProductService productService = Mock()
        TaskService taskService = Mock()
        NeoSecurityService securityService = Mock()
        CustomerService customerService = Mock()

        controller.productService = productService
        controller.taskService = taskService
        controller.neoSecurityService = securityService
        controller.customerService = customerService


        when:
        request.json = data
        controller.placeOrder()

        then:
        1 * productService.findProductByUuid('xxxx-xxxx') >> new Product()
        1 * productService.findProductByUuid('yyyy-yyyyy') >> new Product()
        1 * securityService.currentUser >> new User()
        1 * taskService.findOrderByClientRefId('clientRefId1') >> null
        1 * customerService.findCustomer('cccc') >> new Customer()
        1 * taskService.saveTask({ validateOrder(it) })
        response.contentAsString == '{"status":"OK","message":"Success"}'
    }

    void "test duplicate placeOrder Request"() {
        def data = '''{
  "customerId" : "cccc",
  "orderId":"oooo",
  "clientRefId":"clientRefId1",
  "dateOfSale": "2013-01-3 04:05:40",
  "comment":"Some weird comment",
  "salesDatas": [
    {
      "quantity": 5,
      "price": 454,
      "productId": "xxxx-xxxx"
    },
    {
      "quantity": 2,
      "price": 300,
      "productId": "yyyy-yyyyy"
    }
  ]
}'''

        TaskService taskService = Mock()

        controller.taskService = taskService


        when:
        request.json = data
        controller.placeOrder()

        then:
        1 * taskService.findOrderByClientRefId('clientRefId1') >> new Order()
        response.contentAsString.contains 'Duplicate Order'
    }


}
