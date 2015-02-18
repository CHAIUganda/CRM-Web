package com.omnitech.chai.crm

import com.omnitech.chai.model.Order
import com.omnitech.chai.util.ModelFunctions
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.neo4j.support.Neo4jTemplate
import org.springframework.data.neo4j.transaction.Neo4jTransactional

/**
 * Created by kay on 12/11/2014.
 */
@Neo4jTransactional
class OrderService {

    def orderRepository
    @Autowired
    Neo4jTemplate neo

    /* Orders */
    Order findOrder(Long id) { orderRepository.findOne(id) }

    Order findOrder(String uuid) { orderRepository.findByUuidImpl(uuid) }

    Order saveOrder(Order order) { ModelFunctions.saveEntity(orderRepository, order) }
}
