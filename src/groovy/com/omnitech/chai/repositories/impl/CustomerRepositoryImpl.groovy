package com.omnitech.chai.repositories.impl

import com.omnitech.chai.model.Customer

import static com.omnitech.chai.model.Relations.*
import static org.neo4j.cypherdsl.CypherQuery.*
import static org.neo4j.cypherdsl.CypherQuery.as as az

/**
 * Created by kay on 3/20/2015.
 */

interface ICustomerRepository {

    List<List> exportAllCustomers()

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

        def labels = ['DISTRICT', 'SUBCOUNTY', 'VILLAGE','SEGMENT']

        def (custLabels, custFields) = getClassExportFields(Customer)

        labels.addAll(custLabels)
        fields.addAll(custFields)

        query.returns(*fields)

        export(query.toString(),labels,Customer)
    }

}
