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


    def init = { servletContext ->
        ChaiUtils.injectUtilityMethods()
        insertBootStrapData()
        createUuidConstraints()
        insertEssentialRoles()
        insertDefaultSegments()
        insertActualProducts()

        //Test Data
        println("Inserting test Data....")

//        def dCsv = new File(/C:\Users\kay\Dropbox\Ongoing Projects\Clinton Health\ImportTemplates\Chai-District-Imports.csv/).text
//        regionService.importDistricts(dCsv)
//        insertTerritories()
//        insertProductsAndGroups()
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
            }
        }
    }

    def requestMapRepository

    private insertEssentialRoles() {
        txHelperService.doInTransaction {

            [SALES_ROLE_NAME,
             DETAILER_ROLE_NAME,
             DETAILING_SUPERVISOR_ROLE_NAME,
             SALES_SUPERVISOR_ROLE_NAME,
             ADMIN_ROLE_NAME].each {
                def role = userService.findRoleByAuthority(it)
                if (!role) {
                    println("Inserting essential role [$it]...")
                    userService.saveRole(new Role(authority: it))
                }
            }
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

                //sales
//                '/taskSetting/generationOrder'      : [SALES_SUPERVISOR_ROLE_NAME],
//                '/taskSetting/generateOrderTasks'   : [SALES_SUPERVISOR_ROLE_NAME],

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

    def settingRepository
    def customerSegmentRepository

    void insertDefaultSegments() {
        txHelperService.doInTransaction {
            def setting = settingRepository.findByName(Setting.SEGMENTATION_SCRIPT)
            if (!setting) {
                println "Inserting default setting into DB..."
                setting = new Setting(name: Setting.SEGMENTATION_SCRIPT, value: 'def scores = [0.5,1,1.5,2]\n' +
                        'return scores[(int)(Math.random() *scores.size()-1)]')
                neo.save(setting)
            }

            def count = customerSegmentRepository.count()
            if (!count) {
                println "Inserting Default Segment..."
//                neo.save(new CustomerSegment(name: 'Default Segment', callFrequency: 2, segmentationScript: 'true'))
                neo.save(new CustomerSegment(name: 'A', callFrequency: 3, segmentationScript: 'customerScore >= 2'))
                neo.save(new CustomerSegment(name: 'B', callFrequency: 2, segmentationScript: 'customerScore >= 1.5 && customerScore < 2'))
                neo.save(new CustomerSegment(name: 'C', callFrequency: 1, segmentationScript: 'customerScore >= 1 && customerScore < 1.5'))
                neo.save(new CustomerSegment(name: 'D', callFrequency: 0, segmentationScript: 'customerScore >= 0 && customerScore < 1'))
            }


        }
    }

    //Testing data
    void insertProductsAndGroups() {
        def count = txHelperService.doInTransaction { neo.count(Product) }
        //do not insert any new products if they exist
        if (count) return

        println("inserting products and groups...")
        def medicines = new ProductGroup(name: 'Medicines')
        def tabs = new ProductGroup(name: 'Tabs', parent: medicines)
        def syrups = new ProductGroup(name: 'Syrups', parent: medicines)
        def detergents = new ProductGroup(name: 'Detergents')

        txHelperService.doInTransaction {
            medicines = neo.save(medicines)
            tabs = neo.save(tabs)
            syrups = neo.save(syrups)
            detergents = neo.save(detergents)
        }


        [
                new Product(name: 'QUININ', unitOfMeasure: 'Tin(s)', formulation: 'Tabs', unitPrice: 15000, group: tabs),
                new Product(name: 'CHLOROQUIN', unitOfMeasure: 'Tin(s)', formulation: 'Tabs', unitPrice: 9800, group: tabs),
                new Product(name: 'FANSIDAR', unitOfMeasure: 'Tin(s)', formulation: 'Tabs', unitPrice: 75000, group: tabs),
                new Product(name: 'IBRUFEN', unitOfMeasure: 'Tin(s)', formulation: 'Tabs', unitPrice: 50000, group: tabs),

                // Syrups
                new Product(name: 'COUGH-SY', unitOfMeasure: 'Bottle(s)', formulation: 'Syrup', unitPrice: 1000, group: syrups),
                new Product(name: 'BHM-SY', unitOfMeasure: 'Bottle(s)', formulation: 'Liquid', unitPrice: 2000, group: syrups),
                new Product(name: 'MORINGA-SY', unitOfMeasure: 'Bottle(s)', formulation: 'Syrup', unitPrice: 2000, group: syrups),

                //soaps
                new Product(name: 'DETTOL', unitOfMeasure: 'Bottle(s)', formulation: 'Liquid', unitPrice: 2000, group: detergents),
                new Product(name: 'JIREH', unitOfMeasure: 'Liter(s)', formulation: 'Gel', unitPrice: 2000, group: detergents)
        ].each { prod -> txHelperService.doInTransaction { neo.save(prod) } }


    }

    void insertActualProducts() {

        def count = txHelperService.doInTransaction { neo.count(Product) }
        //do not insert any new products if they exist
        if (count) return
        txHelperService.doInTransaction {
            def coPackOfOrsAndZinc = neo.save new ProductGroup(name: 'ORS and Zinc')
            neo.save new Product(name: 'ORS', group: coPackOfOrsAndZinc)
            neo.save new Product(name: 'Zinc', group: coPackOfOrsAndZinc)
        }
    }

    def regionService

    void insertTerritories() {
//        def text = new File(/C:\var\code\omni\m4w\waterpoint-importer\src\main\groovy\chai\ImportTerritories.csv/).text
        def text = new File(/C:\Users\kay\Dropbox\Ongoing Projects\Clinton Health\ImportTemplates\ImportTerritories2.csv/).text
        println("Importing territories...")
        regionService.importTerritories(text)
        println('Importing users')
        def users = new File(/C:\Users\kay\Dropbox\Ongoing Projects\Clinton Health\ImportTemplates\ImportTerritories-withSalesReps.csv/).text
        userService.importUsers(users)

    }

    private Object generateUsers() {
        return txHelperService.doInTransaction {
            def roleDetailer = userService.findRoleByAuthority(DETAILER_ROLE_NAME)
            def roleSales = userService.findRoleByAuthority(SALES_ROLE_NAME)
            def roleSuper = userService.findRoleByAuthority('ROLE_SUPER_ADMIN')
            regionService.listAllTerritorys().eachWithIndex { Territory territory, int i ->
                i = i + 1
                def saleUserName = "SAL$i"
                def salesPass = createPassword()

                neo.save new User(
                        username: saleUserName,
                        password: springSecurityService.encodePassword(salesPass),
                        territory: territory,
                        roles: [roleSuper, roleSales]
                )
                println("$territory,$saleUserName,$salesPass")

                def detailerUsername = "DET$i"
                def detailerPass = createPassword()
                neo.save new User(
                        username: detailerUsername,
                        password: springSecurityService.encodePassword(detailerPass),
                        territory: territory,
                        roles: [roleSuper, roleDetailer]
                )
                println("$territory,$detailerUsername,$detailerPass")
            }
        }
    }

    def passwords = ['mamaja', 'datada', 'gagaga', 'gamama', 'gamawa', 'japapa', 'dawaga', 'dagapa', 'tawata', 'tamawa', 'mawapa', 'wadaja', 'tamada', 'matama', 'dawawa', 'wadada', 'matata', 'jamaja', 'wagapa', 'pataga', 'magaja', 'pamapa', 'pawada', 'pawawa', 'pagaja', 'dawata', 'majata', 'mamata', 'dataga', 'dawapa', 'tataja', 'tadata', 'jagawa', 'dajaja', 'japama', 'gatama', 'dagaja', 'papaga', 'mamama', 'damaga', 'dapaja', 'wamaja', 'pagaga', 'mapaja', 'gagawa', 'damada', 'datapa', 'tajada', 'tataga', 'mamawa', 'pajawa', 'jagapa', 'wawaja', 'wamada', 'wapama', 'mataja', 'japawa', 'jawaja', 'wagada', 'jadapa', 'datata', 'tapama', 'wagaga', 'magaga', 'madada', 'mapama', 'damawa', 'wawata', 'wagawa', 'watapa', 'gamada', 'tatawa', 'tagama', 'patapa', 'wamaga', 'jadawa', 'wapapa', 'pagawa', 'wadawa', 'magawa', 'gatawa', 'wajada', 'wajawa', 'wawada', 'gajaga', 'gataga', 'tagapa', 'gapata', 'tadaga', 'dawada', 'damapa', 'dagaga', 'wajaja', 'patata', 'mapawa', 'pajata', 'japaja', 'tagaga', 'dapata', 'tadada']

    String createPassword() {
        passwords[(int) (Math.random() * 100)]
    }

}
