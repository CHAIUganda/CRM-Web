package com.omnitech.chai.repositories.impl

import grails.util.Holders
import org.springframework.data.neo4j.support.Neo4jTemplate

import javax.persistence.PersistenceContext

/**
 * Created by kay on 3/19/2015.
 */
class CustomRepositoryBase {

    @PersistenceContext
    Neo4jTemplate neo4jTemplate

    Neo4jTemplate getNeo() {
        if (!neo4jTemplate) {
            neo4jTemplate = Holders.applicationContext.getBean(Neo4jTemplate)
        }
        return neo4jTemplate
    }

    def <T> T bean(Class<T> c) {
        return Holders.applicationContext.getBean(c)
    }

}
