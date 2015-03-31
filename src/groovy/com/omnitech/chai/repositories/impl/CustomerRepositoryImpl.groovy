package com.omnitech.chai.repositories.impl

import com.omnitech.chai.model.*
import com.omnitech.chai.repositories.dto.CustomerDTO
import com.omnitech.chai.util.ModelFunctions
import com.omnitech.chai.util.PageUtils
import org.springframework.data.domain.Page
import org.springframework.data.neo4j.support.Neo4jTemplate

import static com.omnitech.chai.model.Relations.*
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

        def cName = nodeName(Customer)
        def dName = nodeName(District)
        def sName = nodeName(SubCounty)
        def tName = nodeName(Task)
        def segName = nodeName(CustomerSegment)

        def _query = {
            match(node(cName).label(Customer.simpleName))
                    .match(node(cName).out(CUST_TASK).node(tName)).optional()
                    .match(node(cName).out(CUST_IN_SC).node(sName).in(HAS_SUB_COUNTY).node(dName)).optional()
                    .match(node(cName).out(IN_SEGMENT).node(segName)).optional()

        }

        def q = _query().returns(
                distinct(az(id(cName), 'id')),
                az(identifier(cName).property('outletName'), 'outletName'),
                az(identifier(cName).property('outletType'), 'outletType'),
                az(identifier(cName).property('outletSize'), 'outletSize'),
                az(identifier(cName).property('dateCreated'), 'dateCreated'),
                az(identifier(dName).property('name'), 'district'),
                az(identifier(segName).property('name'), 'segment'),
                az(max(identifier(tName).property('completionDate')), 'lastVisit')
        )
        PageUtils.addPagination(q, params, null)
        def cq = _query().returns(count(distinct(identifier(cName))))
        ModelFunctions.query(bean(Neo4jTemplate), q, cq, params, CustomerDTO)


    }

    Page<CustomerDTO> findAllCustomersForPage(Long userId, Map params) {

        def cName = nodeName(Customer)
        def dName = nodeName(District)
        def sName = nodeName(SubCounty)
        def tName = nodeName(Task)
        def segName = nodeName(CustomerSegment)

        def _query = {
            start(nodesById('u', userId))
                    .match(node('u').out(USER_TERRITORY, SUPERVISES_TERRITORY).node('ut')
                    .in(SC_IN_TERRITORY).node(sName)
                    .in(CUST_IN_SC).node(cName))
                    .match(node(sName).in(HAS_SUB_COUNTY).node(dName)).optional()
                    .match(node(cName).out(CUST_TASK).node(tName)).optional()
                    .match(node(cName).out(IN_SEGMENT).node(segName)).optional()
        }

        def q = _query().returns(
                distinct(az(id(cName), 'id')),
                az(identifier(cName).property('outletName'), 'outletName'),
                az(identifier(cName).property('outletType'), 'outletType'),
                az(identifier(cName).property('outletSize'), 'outletSize'),
                az(identifier(cName).property('dateCreated'), 'dateCreated'),
                az(identifier(dName).property('name'), 'district'),
                az(identifier(segName).property('name'), 'segment'),
                az(max(identifier(tName).property('completionDate')), 'lastVisit')
        )

        q = PageUtils.addPagination(q, params, null)

        def cq = _query().returns(count(distinct(identifier(cName))))

        ModelFunctions.query(bean(Neo4jTemplate), q, cq, params, CustomerDTO)


    }

}
