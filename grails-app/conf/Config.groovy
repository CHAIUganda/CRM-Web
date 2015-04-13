import org.apache.log4j.PatternLayout
import org.apache.log4j.RollingFileAppender

//import grails.plugin.springsecurity.SpringSecurityUtils

// locations to search for config files that get merged into the main config;
// config files can be ConfigSlurper scripts, Java properties files, or classes
// in the classpath in ConfigSlurper format

grails.config.locations = ["file:${userHome}/.grails/apps-config/${appName}/config.groovy"]

// if (System.properties["${appName}.config.location"]) {
//    grails.config.locations << "file:" + System.properties["${appName}.config.location"]
// }

grails.project.groupId = com.omnitech.chai // change this to alter the default package name and Maven publishing destination

// The ACCEPT header will not be used for content negotiation for user agents containing the following strings (defaults to the 4 major rendering engines)
grails.mime.disable.accept.header.userAgents = ['Gecko', 'WebKit', 'Presto', 'Trident']
grails.mime.types = [ // the first one is the default format
    all:           '*/*', // 'all' maps to '*' or the first available format in withFormat
    atom:          'application/atom+xml',
    css:           'text/css',
    csv:           'text/csv',
    form:          'application/x-www-form-urlencoded',
    html:          ['text/html','application/xhtml+xml'],
    js:            'text/javascript',
    json:          ['application/json', 'text/json'],
    multipartForm: 'multipart/form-data',
    rss:           'application/rss+xml',
    text:          'text/plain',
    hal:           ['application/hal+json','application/hal+xml'],
    xml:           ['text/xml', 'application/xml']
]

// URL Mapping Cache Max Size, defaults to 5000
//grails.urlmapping.cache.maxsize = 1000

// What URL patterns should be processed by the resources plugin
grails.resources.adhoc.patterns = ['/images/*', '/css/*', '/js/*', '/plugins/*']
//edit: add bootstrap and kickstart to allow the loading of glyph-icons
grails.resources.adhoc.includes = ['/images/**', '/css/**', '/js/**', '/plugins/**', '/kickstart/**', '/bootstrap/**','/ts/**']

// Legacy setting for codec used to encode data with ${}
grails.views.default.codec = "html"

// The default scope for controllers. May be prototype, session or singleton.
// If unspecified, controllers are prototype scoped.
grails.controllers.defaultScope = 'singleton'

// GSP settings
grails {
    views {
        gsp {
            encoding = 'UTF-8'
            htmlcodec = 'xml' // use xml escaping instead of HTML4 escaping
            codecs {
                expression = 'html' // escapes values inside ${}
                scriptlet = 'html' // escapes output from scriptlets in GSPs
                taglib = 'none' // escapes output from taglibs
                staticparts = 'none' // escapes output from static template parts
            }
        }
        // escapes all not-encoded output at final stage of outputting
        // filteringCodecForContentType.'text/html' = 'html'
    }
}


grails.converters.encoding = "UTF-8"
// scaffolding templates configuration
grails.scaffolding.templates.domainSuffix = 'Instance'

// Set to false to use the new Grails 1.2 JSONBuilder in the render method
grails.json.legacy.builder = false
// enabled native2ascii conversion of i18n properties files
grails.enable.native2ascii = true
// packages to include in Spring bean scanning
grails.spring.bean.packages = []
// whether to disable processing of multi part requests
grails.web.disable.multipart=false

// request parameters to mask when logging exceptions
grails.exceptionresolver.params.exclude = ['password']

// configure auto-caching of queries by default (if false you can cache individual queries with 'cache: true')
grails.hibernate.cache.queries = false

// configure passing transaction's read-only attribute to Hibernate session, queries and criterias
// set "singleSession = false" OSIV mode in hibernate configuration after enabling
grails.hibernate.pass.readonly = false
// configure passing read-only to OSIV session by default, requires "singleSession = false" OSIV mode
grails.hibernate.osiv.readonly = false

environments {
    development {
        grails.logging.jul.usebridge = true
    }
    production {
        grails.logging.jul.usebridge = false
        // TODO: grails.serverURL = "http://www.changeme.com"
    }
}

// log4j configuration
log4j = {
    // Example of changing the log pattern for the default console appender:
    //
    def logLayoutPattern = new PatternLayout("%d [%t] %-5p %c %x - %m%n")
    def myAppName = "chai-crm"
    appenders {
        console name: 'stdout', layout: pattern(conversionPattern: '%c{2} %m%n')

        appender new RollingFileAppender(
                name: "logFile",
                maxFileSize: '20MB',
                file: "/tmp/logs/${myAppName}/${myAppName}.log",
                layout: logLayoutPattern)

        appender new RollingFileAppender(
                name: "smsRollingFile",
                maxFileSize: '10MB',
                file: "/tmp/logs/${myAppName}/smstrace.log",
                layout: logLayoutPattern)
    }

    debug logFile: ["grails.app.services", "grails.app.controllers", "com.omnitech", "groovy.sql.Sql", "org.omnitech.sms"]

    error  'org.codehaus.groovy.grails.web.servlet',        // controllers
           'org.codehaus.groovy.grails.web.pages',          // GSP
           'org.codehaus.groovy.grails.web.sitemesh',       // layouts
           'org.codehaus.groovy.grails.web.mapping.filter', // URL mapping
           'org.codehaus.groovy.grails.web.mapping',        // URL mapping
           'org.codehaus.groovy.grails.commons',            // core / classloading
           'org.codehaus.groovy.grails.plugins',            // plugins
           'org.codehaus.groovy.grails.orm.hibernate',      // hibernate integration
           'org.springframework',
           'org.hibernate',
           'net.sf.ehcache.hibernate'
}

//gorm configs
grails.gorm.failOnError = false
grails.gorm.default.mapping = {
    version false
}
//Audit log plugin config
auditLog {
    verbose = true
    logIds = true
    tablename = 'audit_log' // table name for audit logs.
    largeValueColumnTypes = false // use large column db types for oldValue/newValue.
    TRUNCATE_LENGTH = 20000
    cacheDisabled = true
    replacementPatterns = ["com.omnitech.mis": ""] // replace with empty string.
    actorClosure = { request, session ->
        // SpringSecurity Core 1.1.2
        if (request.applicationContext.springSecurityService.principal instanceof String) {
            return request.applicationContext.springSecurityService.principal
        }
        def username = request.applicationContext.springSecurityService.principal?.username
        if (SpringSecurityUtils.isSwitched()) {
            username = SpringSecurityUtils.switchedUserOriginalUsername + " AS " + username
        }
        return username
    }
}

// Added by the Spring Security Core plugin:
grails.plugin.springsecurity.userLookup.userDomainClassName = 'com.omnitech.chai.model.User'
//grails.plugin.springsecurity.userLookup.authorityJoinClassName = 'com.omnitech.mis.UserRole'
grails.plugin.springsecurity.authority.className = 'com.omnitech.chai.model.Role'
grails.plugin.springsecurity.requestMap.className = 'com.omnitech.chai.model.RequestMap'
grails.plugin.springsecurity.securityConfigType = 'Requestmap'
grails.plugin.springsecurity.logout.postOnly = false
grails.plugin.springsecurity.useBasicAuth = true
grails.plugin.springsecurity.basic.realmName = "CHAI CRM"
grails.plugin.springsecurity.adh.errorPage = null
grails.plugin.springsecurity.filterChain.chainMap = [
        '/rest/**': 'JOINED_FILTERS,-exceptionTranslationFilter',
        '/**': 'JOINED_FILTERS,-basicAuthenticationFilter,-basicExceptionTranslationFilter'
]

/*grails.plugin.springsecurity.controllerAnnotations.staticRules = [
	'/':                              ['permitAll'],
	'/index':                         ['permitAll'],
	'/index.gsp':                     ['permitAll'],
	'*//**//*js*//**':                      ['permitAll'],
	'*//**//*css*//**':                     ['permitAll'],
	'*//**//*images*//**':                  ['permitAll'],
	'*//**//*favicon.ico':                ['permitAll']
]*/

deploy.server = 'dev'
