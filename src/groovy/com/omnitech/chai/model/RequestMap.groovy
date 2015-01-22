package com.omnitech.chai.model

import com.omnitech.chai.crm.TxHelperService
import com.omnitech.chai.repositories.RequestMapRepository
import grails.util.Holders
import grails.validation.Validateable
import org.springframework.context.ApplicationContext
import org.springframework.data.neo4j.annotation.Indexed
import org.springframework.data.neo4j.annotation.NodeEntity
import org.springframework.http.HttpMethod

@Validateable
@NodeEntity
class RequestMap extends AbstractEntity {

    @Indexed(unique = true)
    String url
    String configAttribute
    HttpMethod httpMethod

    static constraints = {
        url blank: false, unique: 'httpMethod'
        configAttribute blank: false
        httpMethod nullable: true
    }

    static List<RequestMap> list() {
        ApplicationContext ac = Holders.getApplicationContext()
        def tx = ac.getBean(TxHelperService)
        def repo = ac.getBean(RequestMapRepository)
        def maps = tx.doInTransaction {
            repo.findAll().to(RequestMap).collect()
        }
        maps
    }

    def beforeSave() {

        if(!configAttribute.contains('ROLE_')){
            return
        }
        def attribs = configAttribute?.split(',') as List

        [Role.SUPER_ADMIN_ROLE_NAME].each{ role ->
            if(!attribs.contains(role)){
                attribs.add(role)
            }
        }
        configAttribute = attribs.join(',')
    }
}
