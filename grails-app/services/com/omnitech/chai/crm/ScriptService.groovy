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
    Binding binding

    def evaluate(String script, Map params = [:]) {

        try {
            def shell = getShell(params)
            def result = shell.evaluate(script)
            return result;
        } catch (Exception e) {
            log.error("Error while executing KPI Script", e)
            return "Error: ${e.message}"
        }

    }


    JasperReportBuilder buildReport(String script) {
        def result = shell.evaluate(script)
        return result as JasperReportBuilder
    }

    @Override
    void afterPropertiesSet() {
        binding = new Binding();
        binding.log = log
        binding.neo = neo
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
