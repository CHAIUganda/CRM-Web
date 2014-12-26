package com.omnitech.chai.rest

import com.omnitech.chai.model.DirectSale
import com.omnitech.chai.model.LineItem
import com.omnitech.chai.util.ChaiUtils
import com.omnitech.chai.util.ModelFunctions
import grails.validation.ValidationException

import static org.springframework.http.HttpStatus.BAD_REQUEST


/**
 * SaleController
 * A controller class handles incoming web requests and performs actions such as redirects, rendering views and so on.
 */
class SaleController {

    static namespace = 'rest'
    static responseFormats = ['json', 'xml']
    def productService
    def taskService

    def directSale() {

        try {
            def json = request.JSON as Map
            def sale = toDirectSale(json)
            taskService.saveTask(sale)
            render 'Success'
        } catch (Exception x) {
            x.printStackTrace()
            log.error('Error while performs direct sale: ', x)
            render status: BAD_REQUEST, text: ChaiUtils.getBestMessage(x)
        }
    }

    DirectSale toDirectSale(Map map) {
        def dupeMap = new HashMap(map)
        dupeMap.remove('salesDatas')
        def ds = ModelFunctions.createObj(DirectSale, dupeMap)
        ds.lineItems = map.salesDatas.collect { toLineItem(it, ds) }
        return ds
    }

    LineItem toLineItem(Map map, DirectSale directSale) {

        def product = productService.findProduct(map.productId as String)

        def lineItem = new LineItem(
                product: product,
                hasLineItem: directSale,
                quantity: map.quantity as Double,
                unitPrice: map.price as Double)

        if (!lineItem.validate())
            throw new ValidationException("Error Validating LineItem", lineItem.errors)

        return lineItem

    }


}
