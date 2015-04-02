package com.omnitech.chai.repositories.impl

import com.omnitech.chai.model.*
import com.omnitech.chai.repositories.dto.CustomerDTO
import com.omnitech.chai.util.ModelFunctions
import org.neo4j.cypherdsl.grammar.Match
import org.springframework.data.domain.Page
import org.springframework.data.neo4j.support.Neo4jTemplate

import static com.omnitech.chai.model.Relations.*
import static com.omnitech.chai.util.ChaiUtils.bean
import static com.omnitech.chai.util.ModelFunctions.extractId
import static com.omnitech.chai.util.PageUtils.addPagination
import static org.neo4j.cypherdsl.CypherQuery.*
import static org.neo4j.cypherdsl.CypherQuery.as as az

/**
 * Created by kay on 3/20/2015.
 */

interface ICustomerRepository {

    List<List> exportAllCustomers()

    Page<CustomerDTO> findAllCustomersForPage(Map params)

    Page<CustomerDTO> findAllCustomersForPage(Long userId, Map params)

}

class CustomerRepositoryImpl extends AbstractChaiRepository implements ICustomerRepository {

    @Override
    List<List> exportAllCustomers() {

        def nodeName = nodeName(Customer)
        def query = match(node(nodeName).label(Customer.simpleName)
                .out(CUST_IN_SC).node('sc')
                .in(HAS_SUB_COUNTY).node('d'))
                .match(node(nodeName).out(CUST_IN_VILLAGE).node('v')).optional()
                .match(node(nodeName).out(IN_SEGMENT).node('seg')).optional()

        def fields = [az(identifier('d').property('name'), 'DISTRICT'),
                      az(identifier('sc').property('name'), 'SUBCOUNTY'),
                      az(identifier('v').property('name'), 'VILLAGE'),
                      az(identifier('seg').property('name'), 'SEGMENT'),
        ]

        def labels = ['DISTRICT', 'SUBCOUNTY', 'VILLAGE', 'SEGMENT']

        def (customerLabels, customerFields) = getClassExportFields(Customer)

        labels.addAll(customerLabels)
        fields.addAll(customerFields)

        query.returns(*fields)

        export(query.toString(), labels, Customer)
    }

    Page<CustomerDTO> findAllCustomersForPage(Map params) {
        def _query = {
            Match m = match(node(cName).label(Customer.simpleName).out(CUST_IN_SC).node(sName).in(HAS_SUB_COUNTY).node(dName))
            mayBeAddSearchCriteria(m, params)
            addOtherRelevantFields(m, params)
            return m
        }
        def q = addPagination(_query().returns(customerReturnFieldsClause()), params, null)
        def cq = _query().returns(count(distinct(identifier(cName))))
        ModelFunctions.query(bean(Neo4jTemplate), q, cq, params, CustomerDTO)
    }


    Page<CustomerDTO> findAllCustomersForPage(Long userId, Map params) {
        def _query = {
            def m = start(nodesById('u', userId))
                    .match(node('u').out(USER_TERRITORY, SUPERVISES_TERRITORY).node('ut')
                    .in(SC_IN_TERRITORY).node(sName)
                    .in(CUST_IN_SC).node(cName))
                    .match(node(sName).in(HAS_SUB_COUNTY).node(dName))

            mayBeAddSearchCriteria(m, params)
            addOtherRelevantFields(m, params)
            return m
        }

        def q = addPagination(_query().returns(customerReturnFieldsClause()), params, null)
        def cq = _query().returns(count(distinct(identifier(cName))))
        ModelFunctions.query(bean(Neo4jTemplate), q, cq, params, CustomerDTO)
    }

    private static addOtherRelevantFields(Match m, Map params) {
        if (params.segment) matchSegment(m, params, true)

        if (params.detTerritory || params.salTerritory) {
            if (params.detTerritory) matchTerritory(m, extractId(params, 'detTerritory'))
            if (params.salTerritory) matchTerritory(m, extractId(params, 'salTerritory'))
        }

        m.match(node(cName).out(CUST_TASK).node(tName)).optional()
        if (!params.segment) matchSegment(m, params, false)

    }

    private static customerReturnFieldsClause() {
        [distinct(az(id(cName), 'id')),
         az(identifier(cName).property('outletName'), 'outletName'),
         az(identifier(cName).property('outletType'), 'outletType'),
         az(identifier(cName).property('outletSize'), 'outletSize'),
         az(identifier(cName).property('dateCreated'), 'dateCreated'),
         az(identifier(cName).property('isActive'), 'isActive'),
         az(identifier(dName).property('name'), 'district'),
         az(identifier(segName).property('name'), 'segment'),
         az(max(identifier(tName).property('completionDate')), 'lastVisit')]
    }

    private static matchSegment(Match m, Map params, boolean ignoreIfNotInFilter) {
        if (params.segment)
            m.match(node(cName).out(IN_SEGMENT).node(segName).values(value('name', params.segment as String)))
        else if (!ignoreIfNotInFilter) {
            m.match(node(cName).out(IN_SEGMENT).node(segName)).optional()
        }
    }

    private static matchTerritory(Match m, Long territoryId) {
        m.match(node(sName).out(SC_IN_TERRITORY).node(terName)).where(id(terName).eq(territoryId))
    }

    private static mayBeAddSearchCriteria(Match m, Map params) {
        if (params.search) {
            def search = ModelFunctions.getWildCardRegex(params.search as String)
            m.where(and(identifier(cName).property('outletName').regexp(search)
                    .or(identifier(cName).property('outletType').regexp(search))
                    .or(identifier(cName).property('outletSize').regexp(search))
                    .or(identifier(dName).property('name').regexp(search)))
            )
        }

        if (params.active) {
            def isActive = Boolean.parseBoolean(params.active as String)
            if (isActive)
                m.where(not(identifier(cName).property('isActive').eq(false)))
            else
                m.where(identifier(cName).property('isActive').eq(false))
        }

    }

    private static def cName = nodeName(Customer)
    private static def dName = nodeName(District)
    private static def sName = nodeName(SubCounty)
    private static def tName = nodeName(Task)
    private static def segName = nodeName(CustomerSegment)
    private static def terName = nodeName(Territory)

}
