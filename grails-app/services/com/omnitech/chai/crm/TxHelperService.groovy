package com.omnitech.chai.crm

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.neo4j.support.Neo4jTemplate
import org.springframework.data.neo4j.transaction.Neo4jTransactional

/**
 * Created by kay on 9/23/14.
 */
@Neo4jTransactional
class TxHelperService {

    @Autowired
    Neo4jTemplate neo

    def doInTransaction(Closure code) {
        code.delegate = this
        code()
    }

}
