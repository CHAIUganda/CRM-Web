package com.omnitech.chai.repositories.impl

import com.omnitech.chai.util.ReflectFunctions
import grails.util.Holders
import groovy.transform.CompileStatic
import org.springframework.data.neo4j.support.Neo4jTemplate

import javax.persistence.PersistenceContext

import static grails.util.GrailsNameUtils.getNaturalName
import static org.neo4j.cypherdsl.CypherQuery.as as az
import static org.neo4j.cypherdsl.CypherQuery.identifier

/**
 * Created by kay on 3/19/2015.
 */
abstract class AbstractChaiRepository {

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

    static getClassExportFields(Class aClass, String alias = null) {
        def varName = alias ?: aClass.simpleName.toLowerCase()
        def returnFields = [], fieldLabels = []
        ReflectFunctions.findAllBasicFields(aClass).each {
            if (['lastUpdated', 'dateCreated', 'id'].contains(it)) return
            def fieldAlias = getNaturalName(it).toUpperCase()
            returnFields << az(identifier(varName).property(it), fieldAlias)
            fieldLabels << fieldAlias
        }
        [fieldLabels, returnFields]
    }

    @CompileStatic
     static String nodeName(Class aClass) { aClass.simpleName.toLowerCase() }

}
