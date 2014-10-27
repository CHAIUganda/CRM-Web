package com.omnitech.chai.crm

import com.omnitech.chai.model.Customer
import com.omnitech.chai.model.CustomerSegment
import com.omnitech.chai.model.Setting
import com.omnitech.chai.util.ChaiUtils
import org.springframework.data.neo4j.transaction.Neo4jTransactional

/**
 * Created by kay on 10/22/14.
 */
@Neo4jTransactional
class SegmentationService {

    def customerRepository
    def customerSegmentRepository
    def settingRepository

    void runSegmentationRoutine() {
        def segments = customerSegmentRepository.findAll().collect() as List<CustomerSegment>

        def segmentSetting = settingRepository.findByName(Setting.SEGMENTATION_SCRIPT)

        if (!segmentSetting) throw new IllegalStateException("Cannot run segmentation job with out the segmentation script")


        def segmentScript = getShell().parse(segmentSetting.value)

        customerRepository.findAll().each { c ->
            segments.each { s ->
                gradeCustomer(segmentScript, c, s)
            }
        }

    }

    def gradeCustomer(Script segmentScript, Customer c, CustomerSegment cs) {


        def customerScore = getCustomerScore(segmentScript, c)
        def script = cs.getSegmentationScript()

        if (script) {
            def shell = getShell(customer: c, segment: cs, customerScore: customerScore)

            def result = ChaiUtils.execSilently("Failed to execute segmentation script on customer") {
                shell.evaluate(script)
            }

            if (result == true) {
                c.segment = cs
                c.segmentScore = customerScore
                customerRepository.save(c)
            }

        }

    }

    Double getCustomerScore(Script script, Customer customer) {
        script.setBinding(new Binding([customer: customer]))
        def rt = script.run() as Double
        return rt
    }


    GroovyShell getShell(Map params = [:]) {
        if (params)
            new GroovyShell(this.class.classLoader, getParamBinding(params))
        else
            new GroovyShell(this.class.classLoader)
    }

    Binding getParamBinding(Map params = [:]) { return new Binding(params) }

}
