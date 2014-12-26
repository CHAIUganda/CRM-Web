package com.omnitech.chai.rest

import com.omnitech.chai.crm.CustomerService
import com.omnitech.chai.crm.NeoSecurityService
import com.omnitech.chai.crm.ProductService
import com.omnitech.chai.crm.TaskService
import com.omnitech.chai.model.Customer
import com.omnitech.chai.model.DirectSale
import com.omnitech.chai.model.Order
import com.omnitech.chai.model.Product
import com.omnitech.chai.model.SaleOrder
import com.omnitech.chai.model.User
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
    }

    void "test valid JSON Request"() {

        def data = '''{
  "customerId" : "cccc",
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

        def directSaleValidator = { DirectSale ds ->
            assert ds.howManyZincInStock == 5
            assert ds.howManyOrsInStock == 3
            assert ds.pointOfSaleMaterial == 'counting,ors'
            assert ds.recommendationNextStep == 'start recommending ors'
            assert ds.recommendationLevel == 'Level 2'
            assert ds.governmentApproval
//            assert ds.dateOfSale != null

            assert ds.lineItems.size() == 2
            assert ds.lineItems[0].quantity == 2
            assert ds.lineItems[0].unitPrice == 300

            assert ds.lineItems[1].quantity == 5
            assert ds.lineItems[1].unitPrice == 454

            assert ds.isComplete()
            assert ds.completedBy
            assert ds.customer

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
        response.contentAsString == 'Success'

    }

    void "test invalid Line Item Request"() {

        def data = '''{
  "howManyZincInStock": 5,
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
        1 * productService.findProductByUuid('xxxx-xxxx') >> new Product()
        0 * taskService.saveTask(_)
        response.contentAsString ==~ ".*is less than minimum value.*"

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
            assert sale.lineItems[0].quantity == 2
            assert sale.lineItems[0].unitPrice == 300

            assert sale.lineItems[1].quantity == 5
            assert sale.lineItems[1].unitPrice == 454

            assert sale.isComplete()
            assert sale.completedBy
            assert sale.customer
            assert sale.comment

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
        1 * taskService.findOrder('oooo') >> new Order(customer: new Customer(),comment: "SSjj")
        1 * taskService.saveTask({ directSaleValidator(it) })
        response.contentAsString == 'Success'

    }


}
