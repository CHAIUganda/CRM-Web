package com.omnitech.chai.crm

import com.omnitech.chai.model.Customer
import com.omnitech.chai.model.CustomerSegment
import com.omnitech.chai.util.ChaiUtils
import org.springframework.data.neo4j.transaction.Neo4jTransactional

/**
 * Created by kay on 10/22/14.
 */
@Neo4jTransactional
class SegmentationService {

    def customerRepository
    def customerSegmentRepository

    void runSegmentationRoutine() {
        def segments = customerSegmentRepository.findAll().collect() as List<CustomerSegment>

        customerRepository.findAll().each { c ->
            segments.each { s ->
                gradeCustomer(c, s)
            }
        }

    }

    def gradeCustomer(Customer c, CustomerSegment cs) {

        def script = cs.getSegmentationScript()
        if (script) {
            def shell = getShell(customer: c, segment: cs)

            def result = ChaiUtils.execSilently("Failed to execute segmentation script on customer") {
                shell.evaluate(script)
            }

            if (result == true) {
                c.segment = cs
                customerRepository.save(c)
            }

        }

    }


    GroovyShell getShell(Map params = [:]) {
        if (params)
            new GroovyShell(this.class.classLoader, getParamBinding(params))
        else
            new GroovyShell(this.class.classLoader)
    }

    Binding getParamBinding(Map params = [:]) { return new Binding(params) }

}
