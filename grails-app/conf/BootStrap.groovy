import com.omnitech.chai.model.RequestMap
import com.omnitech.chai.model.Role
import com.omnitech.chai.model.User
import com.omnitech.chai.util.ChaiUtils
import grails.plugin.springsecurity.ReflectionUtils
import org.springframework.beans.factory.config.BeanDefinition
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider
import org.springframework.core.type.filter.AnnotationTypeFilter
import org.springframework.data.neo4j.annotation.NodeEntity

import static com.omnitech.chai.model.Role.DETAILER_ROLE_NAME
import static com.omnitech.chai.model.Role.getSALES_ROLE_NAME

class BootStrap {

    def springSecurityService
    def txHelperService
    def graphDatabaseService
    def userService

    def init = { servletContext ->
        ChaiUtils.injectUtilityMethods()
        insertBootStrapData()
        createUuidConstraints()
        insertEssentialRoles()
        //override this so that a proper request map is loaded by spring security
        ReflectionUtils.metaClass.static.getRequestMapClass = { RequestMap }
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
                neo.save new User(username: 'root',
                        password: springSecurityService.encodePassword('pass'),
                        dateCreated: new Date(),
                        lastUpdated: new Date(),
                        roles: [
                                new Role(authority: 'ROLE_SUPER_ADMIN')
                        ]
                )

                neo.save new RequestMap(url: '/**', configAttribute: 'ROLE_SUPER_ADMIN')
                neo.save new RequestMap(url: '/**', configAttribute: 'ROLE_SUPER_ADMIN,ROLE_ADMIN')
                for (String url in [
                        '/login/auth', '/**/js/**', '/**/css/**',
                        '/**/images/**', '/**/favicon.ico']) {
                    neo.save new RequestMap(url: url, configAttribute: 'permitAll')
                }
            }
        }
    }

    private insertEssentialRoles() {
        txHelperService.doInTransaction {
            def salesRole = userService.findRoleByAuthority(SALES_ROLE_NAME)
            if (!salesRole) {
                println("Inserting essential role [$SALES_ROLE_NAME]...")
                userService.saveRole(new Role(authority: SALES_ROLE_NAME))
            }

            def detailerRole = userService.findRoleByAuthority(DETAILER_ROLE_NAME)
            if (!detailerRole) {
                println("Inserting essential role [$DETAILER_ROLE_NAME]...")
                userService.saveRole(new Role(authority: DETAILER_ROLE_NAME))
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
        }
    }

    String getSimpleName(String className) { Class.forName(className).simpleName }

    Set<BeanDefinition> getPersistentEntities() {
        def provider = new ClassPathScanningCandidateComponentProvider(false);
        provider.addIncludeFilter(new AnnotationTypeFilter(NodeEntity));
        provider.findCandidateComponents("com.omnitech.chai.model");
    }

}
