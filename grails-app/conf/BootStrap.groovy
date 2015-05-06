import com.omnitech.chai.migrations.DbMigrations
import com.omnitech.chai.model.*
import com.omnitech.chai.util.ChaiUtils
import org.springframework.beans.factory.config.BeanDefinition
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider
import org.springframework.core.type.filter.AnnotationTypeFilter
import org.springframework.data.neo4j.annotation.NodeEntity

import static com.omnitech.chai.model.Role.*
import static com.omnitech.chai.util.ModelFunctions.getOrCreate

class BootStrap {

    def springSecurityService
    def txHelperService
    def graphDatabaseService
    def userService
    def migrationService


    def init = { servletContext ->
        TimeZone.setDefault(TimeZone.getTimeZone("GMT+3:00"))
        ChaiUtils.injectUtilityMethods()
        insertBootStrapData()
        createUuidConstraints()
        insertEssentialRoles()

        //Migration code
        def migrations = new DbMigrations()
        migrationService.runMigration(migrations.migrations())
    }


    def destroy = {
        println("*****SHUTTING DOWN GRAPH DB****")
        graphDatabaseService.shutdown()
    }

    private void insertBootStrapData() {
        def numUsers = txHelperService.doInTransaction { neo.count(User.class) }

        if (!numUsers) {
            txHelperService.doInTransaction {

                //pass salt start u = node(1) set u.password = "$2a$10$.J1svR3w6dQTJqsspc2.0.GJuNdZcB5Xuz892wgMCAHNPT0KpQnmu"

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

                neo.save new RequestMap(url: '/**', configAttribute: 'ROLE_SUPER_ADMIN')

                for (String url in [
                        '/login/*', '/logout/*', '/**/js/**', '/**/css/**',
                        '/**/images/**', '/**/favicon.ico']) {
                    neo.save new RequestMap(url: url, configAttribute: 'permitAll')
                }

                neo.findAll(User).each{
                    println it.username
                }
            }
        }
    }

    def requestMapRepository

    private insertEssentialRoles() {
        txHelperService.doInTransaction {

            [SALES_ROLE_NAME, DETAILER_ROLE_NAME, DETAILING_SUPERVISOR_ROLE_NAME, SALES_SUPERVISOR_ROLE_NAME, ADMIN_ROLE_NAME].each {
                def role = userService.findRoleByAuthority(it)
                if (!role) userService.saveRole(new Role(authority: it))
            }
        }

        //Inserting default mapping

        def mappings = [
                //Mobile
                '/rest/**'                          : [DETAILER_ROLE_NAME, SALES_ROLE_NAME],

                //Detailing
                '/detailerTask/index'               : [DETAILER_ROLE_NAME, DETAILING_SUPERVISOR_ROLE_NAME],
                '/detailerTask/malaria'               : [DETAILER_ROLE_NAME, DETAILING_SUPERVISOR_ROLE_NAME],
                '/detailerTask/showMalaria/*'               : [DETAILER_ROLE_NAME, DETAILING_SUPERVISOR_ROLE_NAME],
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
                '/report/getReport'                 : [SALES_SUPERVISOR_ROLE_NAME],

                //HomePages
                '/home/**'                          : ['IS_AUTHENTICATED_REMEMBERED'],
                '/'                                 : ['IS_AUTHENTICATED_REMEMBERED']

        ]

        txHelperService.doInTransaction {
            mappings.each { url, config ->
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

    def createUuidConstraints() {
        txHelperService.doInTransaction {
            getPersistentEntities().each {
                def constrainQuery = "CREATE CONSTRAINT ON (bean:${getSimpleName(it.beanClassName)}) ASSERT bean.uuid IS UNIQUE"
                println("Creating Unique UUID Constraint for $it.beanClassName")
                neo.query(constrainQuery, [:])
            }

            //other constraints
            ["CREATE CONSTRAINT ON (bean:${Order.simpleName}) ASSERT bean.clientRefId IS UNIQUE",
             "CREATE CONSTRAINT ON (bean:${DirectSale.simpleName}) ASSERT bean.clientRefId IS UNIQUE"
            ].each {
                println("Executing: $it")
                neo.query(it, [:])
            }
        }


    }

    String getSimpleName(String className) { Class.forName(className).simpleName }

    Set<BeanDefinition> getPersistentEntities() {
        def provider = new ClassPathScanningCandidateComponentProvider(false);
        provider.addIncludeFilter(new AnnotationTypeFilter(NodeEntity));
        provider.findCandidateComponents("com.omnitech.chai.model");
    }

}
