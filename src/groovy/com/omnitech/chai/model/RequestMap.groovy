package com.omnitech.chai.model

import com.omnitech.chai.crm.TxHelperService
import com.omnitech.chai.repositories.RequestMapRepository
import grails.validation.Validateable
import org.springframework.data.neo4j.annotation.Indexed
import org.springframework.data.neo4j.annotation.NodeEntity
import org.springframework.http.HttpMethod

import static com.omnitech.chai.util.ChaiUtils.bean

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
        bean(TxHelperService).doInTransaction {
            bean(RequestMapRepository).findAll().collect()
        }
    }

    def beforeSave() {
        if (!configAttribute.contains('ROLE_')) {
            return
        }
        addConfigValue(Role.SUPER_ADMIN_ROLE_NAME)
    }

    void addConfigValue(String... values) {
        def attribs = configAttribute?.split(',') as List ?: []

        for (value in values) {
            if (!attribs.contains(value)) {
                attribs.add(value)
            }
        }

        configAttribute = attribs.collect { it.trim() }.join(',')
    }

    String toString() {
        "$url -> $configAttribute"
    }


}
