package com.omnitech.chai.crm

import com.omnitech.chai.model.Order
import com.omnitech.chai.util.ModelFunctions
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.neo4j.support.Neo4jTemplate

/**
 * Created by kay on 12/11/2014.
 */
class OrderService {

    def orderRepository
    @Autowired
    Neo4jTemplate neo

    /* Orders */

    List<Order> listAllOrders() { orderRepository.findAll().collect() }

    Page<Order> listOrders(Map params) { ModelFunctions.listAll(orderRepository, params) }

    Order findOrder(Long id) { orderRepository.findOne(id) }

    Order findOrder(String uuid) { orderRepository.findByUuid(id) }

    Order saveOrder(Order order) { ModelFunctions.saveEntity(orderRepository, order) }

    void deleteOrder(Long id) { orderRepository.delete(id) }

    Page<Order> searchOrders(String search, Map params) {
        ModelFunctions.searchAll(neo, Order, ModelFunctions.getWildCardRegex(search), params)
    }
}