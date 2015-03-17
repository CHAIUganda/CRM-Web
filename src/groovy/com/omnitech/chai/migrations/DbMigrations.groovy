package com.omnitech.chai.migrations

import com.omnitech.chai.crm.MigrationService
import com.omnitech.chai.crm.TxHelperService
import com.omnitech.chai.model.CustomerSegment
import com.omnitech.chai.model.RequestMap
import com.omnitech.chai.repositories.RequestMapRepository
import grails.util.Holders
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.neo4j.support.Neo4jTemplate

import static com.omnitech.chai.model.Role.DETAILING_SUPERVISOR_ROLE_NAME
import static com.omnitech.chai.model.Role.SALES_SUPERVISOR_ROLE_NAME
import static com.omnitech.chai.util.ModelFunctions.getOrCreate

/**
 * Created by kay on 3/12/2015.
 */
class DbMigrations {

    @Autowired
    Neo4jTemplate neo
    @Autowired
    MigrationService migrationService
    @Autowired
    RequestMapRepository requestMapRepository
    @Autowired
    TxHelperService txHelperService

    DbMigrations() {
        Holders.applicationContext.autowireCapableBeanFactory.autowireBean(this);
    }

    final def migrations() {
        MigrationDSL.make {

            changeSet(id: 'supervisors-delete-tasks', desc: 'Allow Supervisors to Delete Tasks') {
                update {
                    addPermission('/detailerTask/deleteAll': [DETAILING_SUPERVISOR_ROLE_NAME])
                }
            }

            changeSet(id: 'sec-supervisor-report-access') {
                update {
                    addPermission(['/report/index'             : [DETAILING_SUPERVISOR_ROLE_NAME, SALES_SUPERVISOR_ROLE_NAME],
                                   '/report/simpleFilterWiz/**': [DETAILING_SUPERVISOR_ROLE_NAME, SALES_SUPERVISOR_ROLE_NAME],
                                   '/report/getReport/**'      : [DETAILING_SUPERVISOR_ROLE_NAME, SALES_SUPERVISOR_ROLE_NAME],
                                   '/report/download/**'       : [DETAILING_SUPERVISOR_ROLE_NAME, SALES_SUPERVISOR_ROLE_NAME],
                                   '/report/reportWiz/**'      : [DETAILING_SUPERVISOR_ROLE_NAME, SALES_SUPERVISOR_ROLE_NAME]])
                }
            }

            changeSet(id: 'add-default-segment'){
                test "match (c:CustomerSegment) where c.name = 'Default' with count(c) as segments return segments = 0 as answer"
                update {
                    neo.save(new CustomerSegment(name: 'Default', callFrequency: 1, segmentationScript: 'true'))
                }
            }

            changeSet(id: 'rename-CustomerContact.surname-to-names') {
                update 'MATCH (n:CustomerContact) WHERE has(n.`surname`) set n.names = n.surname remove n.surname'
            }
        }
    }


    def addPermission(Map<String, List<String>> permissions) {
        permissions.each { url, config ->
            def requestMap = getOrCreate(
                    { requestMapRepository.findByUrl(url) },
                    { new RequestMap(url: url) }
            )

            def oldConfig = requestMap.configAttribute
            requestMap.addConfigValue(*config)
            requestMapRepository.save(requestMap)
            if (oldConfig != requestMap.configAttribute)
                println("Added RequestMap: $requestMap")
        }
    }


}
