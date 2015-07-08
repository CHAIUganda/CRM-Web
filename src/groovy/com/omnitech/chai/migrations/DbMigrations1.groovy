package com.omnitech.chai.migrations

import com.omnitech.chai.crm.MigrationService
import com.omnitech.chai.crm.UserService
import com.omnitech.chai.model.RequestMap
import com.omnitech.chai.model.Role
import com.omnitech.chai.model.Territory
import com.omnitech.chai.model.User
import com.omnitech.chai.repositories.RequestMapRepository
import grails.plugin.springsecurity.SpringSecurityService
import grails.util.Holders
import org.neo4j.graphdb.GraphDatabaseService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.neo4j.support.Neo4jTemplate

import static com.omnitech.chai.model.Role.*

/**
 * Created by kay on 5/12/2015.
 */



class DbMigrations1 implements IMigration{

    @Autowired
    SpringSecurityService springSecurityService
    @Autowired
    GraphDatabaseService graphDatabaseService
    @Autowired
    UserService userService
    @Autowired
    MigrationService migrationService
    @Autowired
    Neo4jTemplate neo
    @Autowired
    RequestMapRepository requestMapRepository

    DbMigrations1() {
        Holders.applicationContext.autowireCapableBeanFactory.autowireBean(this);
    }

    final List<ChangeSet> migrations() {
        MigrationDSL.make {

            changeSet(id: 'insert-bootstrap-data') {
                test {
                    def userCount = neo.count(User.class)
                    println("User count: $userCount")
                    neo.count(User.class) != 0
                 }
                update { insertBootStrapData() }
            }

            changeSet(id: 'insert-essential-roles') {
                test {
                    def userCount = neo.count(User.class)
                    println("User count: $userCount")
                    neo.count(User.class) != 0
                }
                update { insertEssentialRoles() }
            }
        }
    }

    private void insertBootStrapData() {
        //pass salt start u = node(1) set u.password = "$2a$10$.J1svR3w6dQTJqsspc2.0.GJuNdZcB5Xuz892wgMCAHNPT0KpQnmu"
        /*
        println("Inserting bootstrapped data")
        
        def territory = neo.save new Territory(name: 'Root Territory')

        def roleSuper = neo.save new Role(authority: 'ROLE_SUPER_ADMIN')
        def roleSaler = neo.save new Role(authority: SALES_ROLE_NAME)
        def roleDetailer = neo.save new Role(authority: DETAILER_ROLE_NAME)
        neo.save new User(username: 'root',
                password: springSecurityService.encodePassword('pass'),
                dateCreated: new Date(),
                lastUpdated: new Date(),
                territory: territory,
                roles: [roleSuper]
        )

        neo.save new User(username: 'detailer1',
                password: springSecurityService.encodePassword('pass'),
                territory: territory,
                roles: [roleSuper, roleDetailer]
        )

        neo.save new User(
                username: 'sales1',
                password: springSecurityService.encodePassword('pass'),
                territory: territory,
                roles: [roleSuper, roleSaler]
        )
        */
        neo.save new RequestMap(url: '/**', configAttribute: 'ROLE_SUPER_ADMIN')
        for (String url in [
                '/login/*', '/logout/*', '/**/js/**', '/**/css/**',
                '/**/images/**', '/**/favicon.ico']) {
            neo.save new RequestMap(url: url, configAttribute: 'permitAll')
        }
    }


    private insertEssentialRoles() {
        [SALES_ROLE_NAME, DETAILER_ROLE_NAME, DETAILING_SUPERVISOR_ROLE_NAME, SALES_SUPERVISOR_ROLE_NAME, ADMIN_ROLE_NAME].each {
            userService.findRoleByAuthority(it) ?: userService.saveRole(new Role(authority: it))
        }

        //Inserting default mapping
        def mappings = [
                //Mobile
                '/rest/**'                          : [DETAILER_ROLE_NAME, SALES_ROLE_NAME],

                //Detailing
                '/detailerTask/index'               : [DETAILER_ROLE_NAME, DETAILING_SUPERVISOR_ROLE_NAME],
                '/detailerTask/show/*'              : [DETAILER_ROLE_NAME, DETAILING_SUPERVISOR_ROLE_NAME],
                '/detailerTask/map'                 : [DETAILER_ROLE_NAME, DETAILING_SUPERVISOR_ROLE_NAME],
                '/detailerTask/search/**'           : [DETAILER_ROLE_NAME, DETAILING_SUPERVISOR_ROLE_NAME],
                '/detailerTask/searchMap/**'        : [DETAILER_ROLE_NAME, DETAILING_SUPERVISOR_ROLE_NAME],
                '/detailerTask/**'                  : [DETAILING_SUPERVISOR_ROLE_NAME],

                // Orders
                '/call/index'                       : [SALES_ROLE_NAME, SALES_SUPERVISOR_ROLE_NAME],
                '/call/show/*'                      : [SALES_ROLE_NAME, SALES_SUPERVISOR_ROLE_NAME],
                '/call/map'                         : [SALES_ROLE_NAME, SALES_SUPERVISOR_ROLE_NAME],
                '/call/search/**'                   : [SALES_ROLE_NAME, SALES_SUPERVISOR_ROLE_NAME],
                '/call/searchMap/**'                : [SALES_ROLE_NAME, SALES_SUPERVISOR_ROLE_NAME],
                '/call/**'                          : [SALES_SUPERVISOR_ROLE_NAME],

                //Sales
                '/sale/index'                       : [SALES_ROLE_NAME, SALES_SUPERVISOR_ROLE_NAME],
                '/sale/show/*'                      : [SALES_ROLE_NAME, SALES_SUPERVISOR_ROLE_NAME],
                '/sale/map'                         : [SALES_ROLE_NAME, SALES_SUPERVISOR_ROLE_NAME],
                '/sale/search'                      : [SALES_ROLE_NAME, SALES_SUPERVISOR_ROLE_NAME],
                '/sale/searchMap'                   : [SALES_ROLE_NAME, SALES_SUPERVISOR_ROLE_NAME],
                '/sale/**'                          : [SALES_SUPERVISOR_ROLE_NAME],

                //Customers
                '/customer/**'                      : [SALES_SUPERVISOR_ROLE_NAME, DETAILING_SUPERVISOR_ROLE_NAME],
                '/task/updateTaskDate'              : [SALES_SUPERVISOR_ROLE_NAME, DETAILING_SUPERVISOR_ROLE_NAME],

                //Task Generation
                '/taskSetting/generationDetailer'   : [DETAILING_SUPERVISOR_ROLE_NAME],
                '/taskSetting/generateDetailerTasks': [DETAILING_SUPERVISOR_ROLE_NAME],

                //Reports
                '/report/index'                     : [SALES_SUPERVISOR_ROLE_NAME],
                '/report/getReport/**'              : [SALES_SUPERVISOR_ROLE_NAME],

                //HomePages
                '/home/**'                          : ['IS_AUTHENTICATED_REMEMBERED'],
                '/'                                 : ['IS_AUTHENTICATED_REMEMBERED']

        ]


        mappings.each { url, config ->
            def requestMap = requestMapRepository.findByUrl(url) ?: new RequestMap(url: url)

            def oldConfig = requestMap.configAttribute
            requestMap.addConfigValue(*config)
            requestMapRepository.save(requestMap)
            if (oldConfig != requestMap.configAttribute)
                println("Added RequestMap: $requestMap")
        }

    }

}
