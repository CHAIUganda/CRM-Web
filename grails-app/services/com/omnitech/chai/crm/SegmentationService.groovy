package com.omnitech.chai.crm

import com.omnitech.chai.model.CustomerSegment
import com.omnitech.chai.model.Setting
import com.omnitech.chai.model.Territory
import com.omnitech.chai.repositories.CustomerWithLastTask
import com.omnitech.chai.util.ChaiUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.neo4j.support.Neo4jTemplate

/**
 * Created by kay on 10/22/14.
 */

class SegmentationService {

    def customerRepository
    def customerSegmentRepository
    def settingRepository
    def reportContextService
    def territoryRepository
    def txHelperService
    @Autowired
    Neo4jTemplate neo

    void runSegmentationRoutine() {
        def segments = txHelperService.doInTransaction {
            customerSegmentRepository.findAll().collect() as List<CustomerSegment>
        }

        def segmentSetting = settingRepository.findByName(Setting.SEGMENTATION_SCRIPT)

        if (!segmentSetting) throw new IllegalStateException("Cannot run segmentation job with out the segmentation script")


        def segmentScript = compileScript(segmentSetting.value)



        def territories = txHelperService.doInTransaction {
            territoryRepository.findAllByType(Territory.TYPE_DETAILING).collect()
        }

        territories.each { Territory t ->
            txHelperService.doInTransaction {
                log.info("########################### Segmenting customers in Territory[$t]...")
                int count = 0
                customerRepository.findAllCustomersAlongWithLastTask(t.id).each { c ->
                    gradeCustomer(segmentScript, c, segments)
                    count = count + 1
                }
                log.info("Segmented [$count] Customers.. And Commiting Transaction")
            }
        }
    }

    def gradeCustomer(Script segmentScript, CustomerWithLastTask custWithTask, List<CustomerSegment> cSs) {

        def customerScore = getCustomerScore(segmentScript, custWithTask)
        log.info("CustomerScore[$custWithTask.customer] = $customerScore")
        cSs.each { cs ->
            def script = cs.getSegmentationScript()

            if (script) {
                def result = ChaiUtils.execSilently("Failed to execute segmentation script on customer") {
                    evaluateScript(script, [customer: custWithTask.customer, segment: cs, customerScore: customerScore])
                }

                if (result == true && custWithTask.customer.uuid) {
                    log.info("CustomerScore [$custWithTask.customer] Score [$customerScore] ---> $cs.name")
                    //custWithTask.customer.segment = cs
                    //custWithTask.customer.segmentScore = customerScore
                    //customerRepository.save(custWithTask.customer)
                    customerRepository.updateWithQuery(custWithTask.customer.id, customerScore)
                    customerRepository.deleteSegment(custWithTask.customer.id)
                    customerRepository.addSegment(custWithTask.customer.id, cs.id)
                }
            }
        }


    }

    Double getCustomerScore(Script script, CustomerWithLastTask customer) {
        script.setBinding(new Binding([customer: customer.customer, task: customer.task, reportContext: reportContextService]))
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

    def evaluateScript(String script, Map params) {
        def compiledScript = compileScript.call(script)
        compiledScript.setBinding(new Binding(params))
        return compiledScript.run()
    }

    Closure<Script> compileScript = { String script ->
        getShell().parse(script)
    }.memoizeBetween(10, 20)

}
