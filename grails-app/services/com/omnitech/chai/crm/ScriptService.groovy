package com.omnitech.chai.crm

import net.sf.dynamicreports.jasper.builder.JasperReportBuilder
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.neo4j.support.Neo4jTemplate
import org.springframework.data.neo4j.transaction.Neo4jTransactional

@Neo4jTransactional
class ScriptService implements InitializingBean {

    @Autowired
    Neo4jTemplate neo
    def reportContextService
    Binding binding
    def segmentationService

    def evaluate(String script, Map params = [:]) {

        try {
            def result = evalImpl(script,params)
            return result;
        } catch (Exception e) {
            log.error("Error while executing KPI Script", e)
            return "Error: ${e.message}"
        }

    }


    JasperReportBuilder buildReport(String script, Map params = [:]) {
        def result = evalImpl(script,params)
        return result as JasperReportBuilder
    }

    private def evalImpl(String script, Map params) {
        def shell = getShell(params)
        if (script.startsWith('file:')) {
            return shell.evaluate(new File(script.replaceFirst('file:', '')))
        } else {
            return shell.evaluate(script)
        }
    }

    @Override
    void afterPropertiesSet() {
        binding = new Binding();
        binding.log = log
        binding.neo = neo
        binding.reportContext = reportContextService
    }

    GroovyShell getShell(Map params = [:]) {
        if (params)
            new GroovyShell(this.class.classLoader, getParamBinding(params))
        else
            new GroovyShell(this.class.classLoader, binding)
    }


    Binding getParamBinding(Map params = [:]) {
        Binding bind = new Binding(binding.getVariables())
        params.each { key, entry ->
            bind."$key" = entry
        }
        return bind
    }
}
