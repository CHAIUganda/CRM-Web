grails.servlet.version = "2.5" // Change depending on target container compliance (2.5 or 3.0)
grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"
grails.project.work.dir = "target/work"
grails.project.target.level = 1.6
grails.project.source.level = 1.6
//grails.project.war.file = "target/${appName}-${appVersion}.war"

grails.project.fork = [
        // configure settings for compilation JVM, note that if you alter the Groovy version forked compilation is required
        //  compile: [maxMemory: 256, minMemory: 64, debug: false, maxPerm: 256, daemon:true],

        // configure settings for the test-app JVM, uses the daemon by default
        test: [maxMemory: 768, minMemory: 64, debug: false, maxPerm: 256, daemon: true],
        // configure settings for the run-app JVM
        run: [maxMemory: 768, minMemory: 64, debug: false, maxPerm: 256, forkReserve:false],
        // configure settings for the run-war JVM
        war: [maxMemory: 768, minMemory: 64, debug: false, maxPerm: 256, forkReserve: false],
        // configure settings for the Console UI JVM
        console: [maxMemory: 768, minMemory: 64, debug: false, maxPerm: 256]
]

grails.project.dependency.resolver = "maven" // or ivy
grails.project.dependency.resolution = {
    // inherit Grails' default dependencies
    inherits("global") {
        // specify dependency exclusions here; for example, uncomment this to disable ehcache:
//        excludes 'ehcache'
        excludes "itext"
    }
    log "error" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'
    checksums true // Whether to verify checksums on resolve
    // whether to do a secondary resolve on plugin installation, not advised and here for backwards compatibility
    legacyResolve false


    repositories {
        inherits true // Whether to inherit repository definitions from plugins

        grailsPlugins()
        grailsHome()
        mavenLocal()
        grailsCentral()
        mavenCentral()
        // uncomment these (or add new ones) to enable remote dependency resolution from public Maven repositories
        //mavenRepo "http://repository.codehaus.org"
        //mavenRepo "http://download.java.net/maven/2/"
        //mavenRepo "http://repository.jboss.com/maven2/"
        mavenRepo "http://repo.spring.io/milestone/"
        mavenRepo "https://raw.github.com/neo4j-contrib/m2/master/releases"
        mavenRepo "http://m2.neo4j.org/content/repositories/releases/"
    }

    def gebVersion = "0.9.3"
    def seleniumVersion = "2.43.1"

    dependencies {

        // specify dependencies here under either 'build', 'compile', 'runtime', 'test' or 'provided' scopes e.g.
        compile 'org.springframework.data:spring-data-neo4j:3.1.4.RELEASE'
        compile 'org.springframework.data:spring-data-neo4j-tx:3.1.4.RELEASE'
        runtime 'javax.validation:validation-api:1.1.0.Final'
        runtime 'org.neo4j:neo4j-spatial:0.13-neo4j-2.1.2'
        compile 'org.neo4j:neo4j-cypher-dsl:2.1.4'
        compile 'fuzzy-csv:fuzzycsv:1.0-SNAPSHOT'
        compile 'com.xlson.groovycsv:groovycsv:1.0'
        compile 'org.apache.commons:commons-math3:3.3'
        compile 'org.omnitech:sms-api:1.0-SNAPSHOT'

        runtime('org.neo4j.app:neo4j-server:2.1.2') {
            excludes 'org.slf4j:slf4j-jdk14'
            excludes 'org.slf4j:slf4j-jdk14'
        }

        runtime 'net.sf.ehcache:ehcache:2.8.3'
        runtime 'org.apache.httpcomponents:httpcore:4.3'
        runtime 'org.apache.httpcomponents:httpclient:4.3'
//        compile 'org.hibernate:hibernate-validator:4.2.0.Final'


//      Dynamics
        compile ('net.sourceforge.dynamicreports:dynamicreports-core:3.1.2') {
            excludes 'jdtcore:eclipse'
        }
        build "com.lowagie:itext:2.1.7"
        compile 'csv-graphs:csv-graphs:1.0-SNAPSHOT'
        compile 'filter_report:filter_report:1.0-SNAPSHOT'
        compile 'com.lowagie:itext:2.1.7'


        test("org.seleniumhq.selenium:selenium-firefox-driver:$seleniumVersion")

        test "org.gebish:geb-spock:$gebVersion"
        test "org.gebish:geb-junit4:$gebVersion"
        test "org.seleniumhq.selenium:selenium-firefox-driver:$seleniumVersion"
        test "org.seleniumhq.selenium:selenium-chrome-driver:$seleniumVersion"
        test "org.seleniumhq.selenium:selenium-ie-driver:$seleniumVersion"
        test("org.seleniumhq.selenium:selenium-htmlunit-driver:$seleniumVersion")



    }

    plugins {
        build ":tomcat:7.0.52.1"

        compile ":scaffolding:2.0.2"
        compile ':cache:1.1.7'
        compile ':spring-security-core:2.0-RC2'

        compile ":kickstart-with-bootstrap:1.1.0"
        compile ":angularjs-resources:1.2.15"

        runtime ":jquery:1.11.1"
        runtime ":resources:1.2.7"
        runtime ":jquery-ui:1.10.3"
        compile ':quartz:1.0.2'
        test ":geb:$gebVersion"
        test (":functional-test-development:0.9.4"){
            excludes 'hibernate'
        }
        test ":funky-test-load:0.3.9"
    }
}

//offline mode
//grails --offline run-app
//grails.offline.mode=true
