import com.omnitech.chai.model.RequestMap
import com.omnitech.chai.model.Role
import com.omnitech.chai.model.User
import com.omnitech.chai.util.ModelFunctions
import grails.plugin.springsecurity.ReflectionUtils

class BootStrap {

    def springSecurityService
    def txHelperService
    def graphDatabaseService

    def init = { servletContext ->

        ModelFunctions.injectUtilityMethods()

        def numUsers = txHelperService.doInTransaction { neo.count(User.class) }

        if (!numUsers) {
            txHelperService.doInTransaction {
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

        //override this so that a proper request map is loaded by spring security
        ReflectionUtils.metaClass.static.getRequestMapClass = { RequestMap }
    }
    def destroy = {
        graphDatabaseService.shutdown()
    }

    def initdata() {

        def superAdminRole = Role.findByAuthority('ROLE_SUPER_ADMIN') ?: new Role(authority: 'ROLE_SUPER_ADMIN').save(failOnError: true)
        def adminRole = Role.findByAuthority('ROLE_ADMIN') ?: new Role(authority: 'ROLE_ADMIN').save(failOnError: true)

        def superAdminUser = User.findByUsername('super') ?: new User(
                username: 'super',
                password: 'pass',
                enabled: true).save(failOnError: true)

        def adminUser = User.findByUsername('root') ?: new User(
                username: 'root',
                password: 'pass',
                enabled: true).save(failOnError: true)

        if (!superAdminUser.authorities.contains(superAdminRole)) {
            UserRole.create superAdminUser, superAdminRole
        }

        if (!adminUser.authorities.contains(adminRole)) {
            UserRole.create adminUser, adminRole
        }


        for (String url in [
                '/login/auth', '/**/js/**', '/**/css/**',
                '/**/images/**', '/**/favicon.ico']) {
            new RequestMap(url: url, configAttribute: 'permitAll').save()
        }
        new RequestMap(url: '/**', configAttribute: 'ROLE_SUPER_ADMIN,ROLE_ADMIN').save()
//        new RequestMap(url: '/**', configAttribute: 'ROLE_ADMIN').save()
    }
}
