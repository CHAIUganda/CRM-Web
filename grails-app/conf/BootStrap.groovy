import com.omnitech.chai.model.*
import com.omnitech.chai.util.ChaiUtils
import grails.plugin.springsecurity.ReflectionUtils
import org.springframework.beans.factory.config.BeanDefinition
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider
import org.springframework.core.type.filter.AnnotationTypeFilter
import org.springframework.data.neo4j.annotation.NodeEntity

import static com.omnitech.chai.model.Role.DETAILER_ROLE_NAME
import static com.omnitech.chai.model.Role.SALES_ROLE_NAME

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
        //insertProductsAndGroups()
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
                setting = new Setting(name: Setting.SEGMENTATION_SCRIPT, value: '2.5')
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
            def coPackOfOrsAndZinc = neo.save new ProductGroup(name: 'Co-pack of ORS and Zinc')
            def oralRehydrationSaltsOrs = neo.save new ProductGroup(name: 'Oral Rehydration salts (ORS)')
            def zincTabletsOrSyrup = neo.save new ProductGroup(name: 'Zinc tablets or syrup')
            def antibioticAmoxicillinTabletsSyrup = neo.save new ProductGroup(name: 'Antibiotic: Amoxicillin (tablets/syrup)')
            def antibioticCoTrimoxaleTabletsSyrup = neo.save new ProductGroup(name: 'Antibiotic: Co-trimoxale (tablets/syrup)')
            def antibioticMetronidazoleTabletsSyrup = neo.save new ProductGroup(name: 'Antibiotic: Metronidazole (tablets/syrup)')
            def antibioticOtherTabletsSyrup = neo.save new ProductGroup(name: 'Antibiotic: Other (tablets/syrup)')
            def antiMotility = neo.save new ProductGroup(name: 'Anti-motility')
            def artemisininCombinationTherapiesActs = neo.save new ProductGroup(name: 'Artemisinin combination therapies (ACTs)')
            def otherAntiMalarialsThatAreNotActs = neo.save new ProductGroup(name: 'Other anti-malarials that are not ACTs')
            def paracetamolOrFeverReducer = neo.save new ProductGroup(name: 'Paracetamol or fever reducer')
            def intravenousFluidsOrIvDrip = neo.save new ProductGroup(name: 'Intravenous fluids or IV drip')
            def injectionAntibioticOrNonAntibiotic = neo.save new ProductGroup(name: 'Injection (antibiotic or non-antibiotic)')
            def homeRemedyCoconutWaterJuiceEtc = neo.save new ProductGroup(name: 'Home remedy (coconut water, juice, etc.)')
            def herbs = neo.save new ProductGroup(name: 'Herbs')
            def rdt = neo.save new ProductGroup(name: 'RDT')


            neo.save new Product(name: 'Oralyte Co-Pack', group: coPackOfOrsAndZinc)
            neo.save new Product(name: 'RestORS + ZinKID Co-Pack', group: coPackOfOrsAndZinc)
            neo.save new Product(name: 'Oralyte + DT Zinc-20 in transparent blister', group: coPackOfOrsAndZinc)
            neo.save new Product(name: 'Other (specify)', group: coPackOfOrsAndZinc)
            neo.save new Product(name: 'Oralyte (Plain)', group: oralRehydrationSaltsOrs)
            neo.save new Product(name: 'Oralyte (Orange)', group: oralRehydrationSaltsOrs)
            neo.save new Product(name: 'RestORS', group: oralRehydrationSaltsOrs)
            neo.save new Product(name: 'Revive', group: oralRehydrationSaltsOrs)
            neo.save new Product(name: 'Lyfe', group: oralRehydrationSaltsOrs)
            neo.save new Product(name: 'Peditral (Orange)', group: oralRehydrationSaltsOrs)
            neo.save new Product(name: 'Peditral (Lemon / Lime)', group: oralRehydrationSaltsOrs)
            neo.save new Product(name: 'ORS BP', group: oralRehydrationSaltsOrs)
            neo.save new Product(name: 'Other (specify)', group: oralRehydrationSaltsOrs)
            neo.save new Product(name: 'ZinKID', group: zincTabletsOrSyrup)
            neo.save new Product(name: 'Zincocet-DT', group: zincTabletsOrSyrup)
            neo.save new Product(name: 'DT Zinc-20', group: zincTabletsOrSyrup)
            neo.save new Product(name: 'FDC: Zinc Sulfate Tablets USP 20 mg', group: zincTabletsOrSyrup)
            neo.save new Product(name: 'Medicamen: Zinc Sulfate Tablets USP 20 mg', group: zincTabletsOrSyrup)
            neo.save new Product(name: 'ZinkFant', group: zincTabletsOrSyrup)
            neo.save new Product(name: 'PedZink', group: zincTabletsOrSyrup)
            neo.save new Product(name: 'Zincos', group: zincTabletsOrSyrup)
            neo.save new Product(name: 'Squire Zinc', group: zincTabletsOrSyrup)
            neo.save new Product(name: 'Other (specify)', group: zincTabletsOrSyrup)
            neo.save new Product(name: 'OralZinc', group: zincTabletsOrSyrup)
            neo.save new Product(name: 'Other (specify)', group: zincTabletsOrSyrup)
            neo.save new Product(name: 'Apmod 100/270, (18-36)kg, (6-13)Yrs, Yellow, 3 Tabs', group: artemisininCombinationTherapiesActs)
            neo.save new Product(name: 'Apmod 100/270, above 36kg, 14Yrs plus, Green, 6 Tabs', group: artemisininCombinationTherapiesActs)
            neo.save new Product(name: 'Apmod 50/135, (9-17)kg, (1-5)Yrs, Blue, 3 Tabs', group: artemisininCombinationTherapiesActs)
            neo.save new Product(name: 'Apmod Age2', group: artemisininCombinationTherapiesActs)
            neo.save new Product(name: 'Apmod Age3', group: artemisininCombinationTherapiesActs)
            neo.save new Product(name: 'Apmod Age4', group: artemisininCombinationTherapiesActs)
            neo.save new Product(name: 'ARCO', group: artemisininCombinationTherapiesActs)
            neo.save new Product(name: 'Artefan 20/120', group: artemisininCombinationTherapiesActs)
            neo.save new Product(name: 'Artefan 20/120 5-14kg', group: artemisininCombinationTherapiesActs)
            neo.save new Product(name: 'Artefan 20/120 15-24kg', group: artemisininCombinationTherapiesActs)
            neo.save new Product(name: 'Artefan 20/120 25-34kg', group: artemisininCombinationTherapiesActs)
            neo.save new Product(name: 'Artefan 20/120 35+ kg Adults', group: artemisininCombinationTherapiesActs)
            neo.save new Product(name: 'Artefan 40/240', group: artemisininCombinationTherapiesActs)
            neo.save new Product(name: 'Artefan 80/480', group: artemisininCombinationTherapiesActs)
            neo.save new Product(name: 'Artemether + Lumefantrine <3 years', group: artemisininCombinationTherapiesActs)
            neo.save new Product(name: 'Artemether + Lumefantrine 3-8 years', group: artemisininCombinationTherapiesActs)
            neo.save new Product(name: 'Artemether + Lumefantrine 9-14 years', group: artemisininCombinationTherapiesActs)
            neo.save new Product(name: 'Artemether + Lumefantrine >14 years', group: artemisininCombinationTherapiesActs)
            neo.save new Product(name: 'Artequin', group: artemisininCombinationTherapiesActs)
            neo.save new Product(name: 'Artequin 300/375 CHILD', group: artemisininCombinationTherapiesActs)
            neo.save new Product(name: 'Artequin 600/750', group: artemisininCombinationTherapiesActs)
            neo.save new Product(name: 'Artequin Paedriatic', group: artemisininCombinationTherapiesActs)
            neo.save new Product(name: 'Artesunate + Amodiaquine Adult', group: artemisininCombinationTherapiesActs)
            neo.save new Product(name: 'Artesunate 50mg & Amodiaquine Hydrochloride 200mg', group: artemisininCombinationTherapiesActs)
            neo.save new Product(name: 'Artesun-Plus Adult >14 years', group: artemisininCombinationTherapiesActs)
            neo.save new Product(name: 'Azunate -50', group: artemisininCombinationTherapiesActs)
            neo.save new Product(name: 'Azunate -100', group: artemisininCombinationTherapiesActs)
            neo.save new Product(name: 'CACH-ART 20/120', group: artemisininCombinationTherapiesActs)
            neo.save new Product(name: 'Coartem 20/120', group: artemisininCombinationTherapiesActs)
            neo.save new Product(name: 'Coartem 20/120 5-15 kg', group: artemisininCombinationTherapiesActs)
            neo.save new Product(name: 'Coartem 20/120 15-25 kg', group: artemisininCombinationTherapiesActs)
            neo.save new Product(name: 'Coartem 20/120 25-35 kg', group: artemisininCombinationTherapiesActs)
            neo.save new Product(name: 'Coartem 20/120 35 kg+', group: artemisininCombinationTherapiesActs)
            neo.save new Product(name: 'Coartem DISPERSIBLE 5-15kg', group: artemisininCombinationTherapiesActs)
            neo.save new Product(name: 'Coartem DISPERSIBLE 15-25kg', group: artemisininCombinationTherapiesActs)
            neo.save new Product(name: 'Co-Artesiane Pediatric ', group: artemisininCombinationTherapiesActs)
            neo.save new Product(name: 'Co-Malartem', group: artemisininCombinationTherapiesActs)
            neo.save new Product(name: 'Co-Mether', group: artemisininCombinationTherapiesActs)
            neo.save new Product(name: 'DIPI (Adult) 16 years and above', group: artemisininCombinationTherapiesActs)
            neo.save new Product(name: 'Duo-Cotecxin 40/320', group: artemisininCombinationTherapiesActs)
            neo.save new Product(name: 'Duo-Cotecxcin 40/320, Blue/White 9 Tabs', group: artemisininCombinationTherapiesActs)
            neo.save new Product(name: 'Duo-Cotecxin Infant', group: artemisininCombinationTherapiesActs)
            neo.save new Product(name: 'Duo-Cotecxin Children 5-20kg', group: artemisininCombinationTherapiesActs)
            neo.save new Product(name: 'Falcimon Kit', group: artemisininCombinationTherapiesActs)
            neo.save new Product(name: 'Falcimon Kit (For Children)', group: artemisininCombinationTherapiesActs)
            neo.save new Product(name: 'Falcimon Kit (For Adults)', group: artemisininCombinationTherapiesActs)
            neo.save new Product(name: 'Lonart', group: artemisininCombinationTherapiesActs)
            neo.save new Product(name: 'Lonart Forte', group: artemisininCombinationTherapiesActs)
            neo.save new Product(name: 'Lonart Paediatric', group: artemisininCombinationTherapiesActs)
            neo.save new Product(name: 'Lonart-DS', group: artemisininCombinationTherapiesActs)
            neo.save new Product(name: 'Lumaren', group: artemisininCombinationTherapiesActs)
            neo.save new Product(name: 'Lumartem 5 TO <15kg', group: artemisininCombinationTherapiesActs)
            neo.save new Product(name: 'Lumartem 15 TO <25kg', group: artemisininCombinationTherapiesActs)
            neo.save new Product(name: 'Lumartem 25 TO <35kg', group: artemisininCombinationTherapiesActs)
            neo.save new Product(name: 'Lumartem 35kg and above', group: artemisininCombinationTherapiesActs)
            neo.save new Product(name: 'Lumether', group: artemisininCombinationTherapiesActs)
            neo.save new Product(name: 'P-Alaxin', group: artemisininCombinationTherapiesActs)
            neo.save new Product(name: 'Ridmal 40/320', group: artemisininCombinationTherapiesActs)
            neo.save new Product(name: 'Winthrop Infant 2 - 11 months', group: artemisininCombinationTherapiesActs)
            neo.save new Product(name: 'Winthrop Toddler 1 - 5 years', group: artemisininCombinationTherapiesActs)
            neo.save new Product(name: 'Winthrop Child 6 - 13 years ', group: artemisininCombinationTherapiesActs)
            neo.save new Product(name: 'Winthrop Adult  +14 years', group: artemisininCombinationTherapiesActs)
            neo.save new Product(name: 'Other (specify)', group: artemisininCombinationTherapiesActs)
            neo.save new Product(name: 'Agogyl - 200mg strength ', group: antibioticAmoxicillinTabletsSyrup)
            neo.save new Product(name: 'Agoxin - 250mg strength', group: antibioticAmoxicillinTabletsSyrup)
            neo.save new Product(name: 'Amoxicillin - 100mg strength', group: antibioticAmoxicillinTabletsSyrup)
            neo.save new Product(name: 'Amoxicillin - 250mg strength', group: antibioticAmoxicillinTabletsSyrup)
            neo.save new Product(name: 'Asomycin - 200mg strength', group: antibioticAmoxicillinTabletsSyrup)
            neo.save new Product(name: 'Asomycin - 250mg strength', group: antibioticAmoxicillinTabletsSyrup)
            neo.save new Product(name: 'Cadila - 250mg strength', group: antibioticAmoxicillinTabletsSyrup)
            neo.save new Product(name: 'Capsel - 250mg strength', group: antibioticAmoxicillinTabletsSyrup)
            neo.save new Product(name: 'Capsules - 250mg strength', group: antibioticAmoxicillinTabletsSyrup)
            neo.save new Product(name: 'Co-Amoxicillin - 250mg strength', group: antibioticAmoxicillinTabletsSyrup)
            neo.save new Product(name: 'Cotrim - 200mg strength', group: antibioticAmoxicillinTabletsSyrup)
            neo.save new Product(name: 'Cyrup - 152mg strength', group: antibioticAmoxicillinTabletsSyrup)
            neo.save new Product(name: 'Elymox - 250mg strength', group: antibioticAmoxicillinTabletsSyrup)
            neo.save new Product(name: 'Hyphen - 100mg strength', group: antibioticAmoxicillinTabletsSyrup)
            neo.save new Product(name: 'Hyphen - 200mg strength', group: antibioticAmoxicillinTabletsSyrup)
            neo.save new Product(name: 'Hyphen - 250mg strength', group: antibioticAmoxicillinTabletsSyrup)
            neo.save new Product(name: 'Kpi - 250mg strength', group: antibioticAmoxicillinTabletsSyrup)
            neo.save new Product(name: 'Lonart Tablets - 140mg strength', group: antibioticAmoxicillinTabletsSyrup)
            neo.save new Product(name: 'Moxileb - 250mg strength', group: antibioticAmoxicillinTabletsSyrup)
            neo.save new Product(name: 'Panamax - 250mg strength', group: antibioticAmoxicillinTabletsSyrup)
            neo.save new Product(name: 'Promax - 250mg strength', group: antibioticAmoxicillinTabletsSyrup)
            neo.save new Product(name: 'Spamox - 100mg strength', group: antibioticAmoxicillinTabletsSyrup)
            neo.save new Product(name: 'Spamox - 250mg strength', group: antibioticAmoxicillinTabletsSyrup)
            neo.save new Product(name: 'Tetraren - 250mg strength', group: antibioticAmoxicillinTabletsSyrup)
            neo.save new Product(name: 'Unixil - 250mg strength', group: antibioticAmoxicillinTabletsSyrup)
            neo.save new Product(name: 'Z-Mox - 250mg strength', group: antibioticAmoxicillinTabletsSyrup)
            neo.save new Product(name: 'Other (specify)', group: antibioticAmoxicillinTabletsSyrup)
            neo.save new Product(name: 'Agocillin 100ml - 125mg strength ', group: antibioticAmoxicillinTabletsSyrup)
            neo.save new Product(name: 'Agoxin - 100ml - 125mg/5ml strength', group: antibioticAmoxicillinTabletsSyrup)
            neo.save new Product(name: 'Agoxin - Amoxicillin suspension 125 mg strength', group: antibioticAmoxicillinTabletsSyrup)
            neo.save new Product(name: 'Amoxicillin - 100ml - 125mg/5ml strength', group: antibioticAmoxicillinTabletsSyrup)
            neo.save new Product(name: 'Amoxicillin - 100ml - 250mg/5ml strength', group: antibioticAmoxicillinTabletsSyrup)
            neo.save new Product(name: 'Amoxicillin - Suspension 60 ml ', group: antibioticAmoxicillinTabletsSyrup)
            neo.save new Product(name: 'Ampiclox - 100 ml - 125mg/ml strength', group: antibioticAmoxicillinTabletsSyrup)
            neo.save new Product(name: 'Asomycin - 60ml - 125mg/5ml strength', group: antibioticAmoxicillinTabletsSyrup)
            neo.save new Product(name: 'Cadila - 100ml - 125mg/5ml strength', group: antibioticAmoxicillinTabletsSyrup)
            neo.save new Product(name: 'Camox - 100 ml - 125mg/5ml strength', group: antibioticAmoxicillinTabletsSyrup)
            neo.save new Product(name: 'Carisale Syrup', group: antibioticAmoxicillinTabletsSyrup)
            neo.save new Product(name: 'Co-Amoxicillin - 100ml - 125mg/5ml strength', group: antibioticAmoxicillinTabletsSyrup)
            neo.save new Product(name: 'Digoxin Syrup - 100 ml - 125mg/5ml strength', group: antibioticAmoxicillinTabletsSyrup)
            neo.save new Product(name: 'Duramox 125mg syrup ', group: antibioticAmoxicillinTabletsSyrup)
            neo.save new Product(name: 'Duramox 250mg syrup ', group: antibioticAmoxicillinTabletsSyrup)
            neo.save new Product(name: 'Hyphen - 100ml - 125mg/5ml strength', group: antibioticAmoxicillinTabletsSyrup)
            neo.save new Product(name: 'Kpi - 100ml - 125mg/5ml strength', group: antibioticAmoxicillinTabletsSyrup)
            neo.save new Product(name: 'K-Trim - 60ml - 125 mg strength', group: antibioticAmoxicillinTabletsSyrup)
            neo.save new Product(name: 'Moxacil - 100ml - 125mg/5ml strength', group: antibioticAmoxicillinTabletsSyrup)
            neo.save new Product(name: 'Moxileb - 100ml - 125mg/5ml strength', group: antibioticAmoxicillinTabletsSyrup)
            neo.save new Product(name: 'Penamox syrup', group: antibioticAmoxicillinTabletsSyrup)
            neo.save new Product(name: 'Renetrim - 60ml - 125mg/5ml strength', group: antibioticAmoxicillinTabletsSyrup)
            neo.save new Product(name: 'Renetrim - 60ml - 240mg/5ml strength', group: antibioticAmoxicillinTabletsSyrup)
            neo.save new Product(name: 'Spamox - 60ml - 125mg/5ml strength', group: antibioticAmoxicillinTabletsSyrup)
            neo.save new Product(name: 'Spamox - 100ml - 125mg/5ml strength', group: antibioticAmoxicillinTabletsSyrup)
            neo.save new Product(name: 'Trimoxol Syrup - 100ml - 240mg', group: antibioticAmoxicillinTabletsSyrup)
            neo.save new Product(name: 'Unixil - 100ml - 125mg/5ml strength', group: antibioticAmoxicillinTabletsSyrup)
            neo.save new Product(name: 'Unixil - 60ml - 240mg/5ml strength', group: antibioticAmoxicillinTabletsSyrup)
            neo.save new Product(name: 'Unixil Suspension 100ml - 125mg/5ml strength', group: antibioticAmoxicillinTabletsSyrup)
            neo.save new Product(name: 'Unixil Suspension 100ml - 240mg/5ml strength', group: antibioticAmoxicillinTabletsSyrup)
            neo.save new Product(name: 'Other (specify)', group: antibioticAmoxicillinTabletsSyrup)
            neo.save new Product(name: 'Agotrim - 400mg strength', group: antibioticOtherTabletsSyrup)
            neo.save new Product(name: 'Agoxin -  250mg strength', group: antibioticOtherTabletsSyrup)
            neo.save new Product(name: 'Alprim - 80mg strength', group: antibioticOtherTabletsSyrup)
            neo.save new Product(name: 'Alprim - 480mg strength', group: antibioticOtherTabletsSyrup)
            neo.save new Product(name: 'Asmox - 480mg strength', group: antibioticOtherTabletsSyrup)
            neo.save new Product(name: 'Astrim - 80mg strength', group: antibioticOtherTabletsSyrup)
            neo.save new Product(name: 'Astrim - 250mg strength', group: antibioticOtherTabletsSyrup)
            neo.save new Product(name: 'Astrim - 400mg strength', group: antibioticOtherTabletsSyrup)
            neo.save new Product(name: 'Astrim - 480mg strength', group: antibioticOtherTabletsSyrup)
            neo.save new Product(name: 'Batrim - 480mg strength', group: antibioticOtherTabletsSyrup)
            neo.save new Product(name: 'Bisepton - 480mg strength', group: antibioticOtherTabletsSyrup)
            neo.save new Product(name: 'Cotrimoxazole - 100mg strength', group: antibioticCoTrimoxaleTabletsSyrup)
            neo.save new Product(name: 'Cotrimoxazole - 200mg strength', group: antibioticCoTrimoxaleTabletsSyrup)
            neo.save new Product(name: 'Cotrimoxazole - 250mg strength', group: antibioticCoTrimoxaleTabletsSyrup)
            neo.save new Product(name: 'Cotrimoxazole - 480mg strength', group: antibioticCoTrimoxaleTabletsSyrup)
            neo.save new Product(name: 'K - Trim - 100ml - 125mg/5ml strength', group: antibioticCoTrimoxaleTabletsSyrup)
            neo.save new Product(name: 'Kam Cotri - 250mg strength', group: antibioticOtherTabletsSyrup)
            neo.save new Product(name: 'Kam Cotri - 280mg strength', group: antibioticOtherTabletsSyrup)
            neo.save new Product(name: 'Kam Cotri - 400mg strength', group: antibioticOtherTabletsSyrup)
            neo.save new Product(name: 'Kam Cotri - 480mg strength', group: antibioticOtherTabletsSyrup)
            neo.save new Product(name: 'KPI - 480mg strength', group: antibioticOtherTabletsSyrup)
            neo.save new Product(name: 'Megatrim - 480mg strength', group: antibioticOtherTabletsSyrup)
            neo.save new Product(name: 'Renetrim - 80mg strength', group: antibioticOtherTabletsSyrup)
            neo.save new Product(name: 'Renetrim - 250mg strength', group: antibioticOtherTabletsSyrup)
            neo.save new Product(name: 'Renetrim - 400mg strength', group: antibioticOtherTabletsSyrup)
            neo.save new Product(name: 'Renetrim - 480mg strength', group: antibioticOtherTabletsSyrup)
            neo.save new Product(name: 'Septrin - 250mg strength', group: antibioticOtherTabletsSyrup)
            neo.save new Product(name: 'Septrin - 480mg strength', group: antibioticOtherTabletsSyrup)
            neo.save new Product(name: 'Sulfran- 200mg strength', group: antibioticOtherTabletsSyrup)
            neo.save new Product(name: 'Sulfran - 480mg strength', group: antibioticOtherTabletsSyrup)
            neo.save new Product(name: 'Trimago - 80mg strength', group: antibioticOtherTabletsSyrup)
            neo.save new Product(name: 'Trimago - 120mg strength', group: antibioticOtherTabletsSyrup)
            neo.save new Product(name: 'Trimago - 200mg strength', group: antibioticOtherTabletsSyrup)
            neo.save new Product(name: 'Trimago - 240mg strength', group: antibioticOtherTabletsSyrup)
            neo.save new Product(name: 'Trimago - 250mg strength', group: antibioticOtherTabletsSyrup)
            neo.save new Product(name: 'Trimago - 400mg strength', group: antibioticOtherTabletsSyrup)
            neo.save new Product(name: 'Trimago - 480mg strength', group: antibioticOtherTabletsSyrup)
            neo.save new Product(name: 'Trimoprim - 480mg strength', group: antibioticOtherTabletsSyrup)
            neo.save new Product(name: 'Trimoxol - 250mg strength', group: antibioticOtherTabletsSyrup)
            neo.save new Product(name: 'Trimoxol - 480mg strength', group: antibioticOtherTabletsSyrup)
            neo.save new Product(name: 'Unisten - 20mg strength', group: antibioticOtherTabletsSyrup)
            neo.save new Product(name: 'Unitrim - 200mg strength', group: antibioticOtherTabletsSyrup)
            neo.save new Product(name: 'Unitrim - 240mg strength', group: antibioticOtherTabletsSyrup)
            neo.save new Product(name: 'Unitrim - 250mg strength', group: antibioticOtherTabletsSyrup)
            neo.save new Product(name: 'Unitrim - 480mg strength', group: antibioticOtherTabletsSyrup)
            neo.save new Product(name: 'Other (specify)', group: antibioticOtherTabletsSyrup)
            neo.save new Product(name: 'Agoxin - 100ml - 250mg/5ml strength', group: antibioticOtherTabletsSyrup)
            neo.save new Product(name: 'Alprim - 60ml - 240mg/5ml strength', group: antibioticOtherTabletsSyrup)
            neo.save new Product(name: 'Alprim - 100 ml - 125mg/5ml strength', group: antibioticOtherTabletsSyrup)
            neo.save new Product(name: 'Alprim - 100ml - 200mg /5ml strength', group: antibioticOtherTabletsSyrup)
            neo.save new Product(name: 'Amoxyl syrup - 100ml - 240mg/5ml strength', group: antibioticOtherTabletsSyrup)
            neo.save new Product(name: 'Cotrimoxazole Suspension - 60ml - 125mg/5ml strength', group: antibioticCoTrimoxaleTabletsSyrup)
            neo.save new Product(name: 'K - Trim - 60ml - 240mg/5ml strength', group: antibioticCoTrimoxaleTabletsSyrup)
            neo.save new Product(name: 'K - Trim suspension - 60ml - 240mg/5ml strength', group: antibioticCoTrimoxaleTabletsSyrup)
            neo.save new Product(name: 'K - Trim suspension - 100ml - 240mg/5ml strength', group: antibioticCoTrimoxaleTabletsSyrup)
            neo.save new Product(name: 'KPI - 60ml - 125mg/5ml strength', group: antibioticOtherTabletsSyrup)
            neo.save new Product(name: 'L Trim - suspension - 60ml - 125mg/5ml strength', group: antibioticOtherTabletsSyrup)
            neo.save new Product(name: 'L-Trim - 60ml - 125mg/5ml strength', group: antibioticOtherTabletsSyrup)
            neo.save new Product(name: 'L-Trim - 60ml - 240mg/5ml strength', group: antibioticOtherTabletsSyrup)
            neo.save new Product(name: 'L-Trim - 100ml - 240mg/5ml strength', group: antibioticOtherTabletsSyrup)
            neo.save new Product(name: 'Maxotrim - 100ml - 240mg/5ml strength', group: antibioticOtherTabletsSyrup)
            neo.save new Product(name: 'Metrim - 60ml - 240mg/5ml strength', group: antibioticOtherTabletsSyrup)
            neo.save new Product(name: 'Metrogyl - 100ml - 200mg/5ml strength', group: antibioticOtherTabletsSyrup)
            neo.save new Product(name: 'Metronidazoles suspension - 100ml - 125mg/5ml strength', group: antibioticMetronidazoleTabletsSyrup)
            neo.save new Product(name: 'Metronidazoles suspension - 100ml - 200mg/5ml strength', group: antibioticMetronidazoleTabletsSyrup)
            neo.save new Product(name: 'Paediatric Suspension - 60ml - 240mg/5ml strength', group: antibioticOtherTabletsSyrup)
            neo.save new Product(name: 'Renetrim Syrup - 60ml - 125mg/5ml strength', group: antibioticOtherTabletsSyrup)
            neo.save new Product(name: 'Renetrim Syrup - 60ml - 240mg/5ml strength', group: antibioticOtherTabletsSyrup)
            neo.save new Product(name: 'Smartrim - 60ml - 240mg/5ml strength', group: antibioticOtherTabletsSyrup)
            neo.save new Product(name: 'Sulfran suspension - 60ml - 240mg/5ml strength', group: antibioticOtherTabletsSyrup)
            neo.save new Product(name: 'Trimago Suspension - 60ml - 125mg/5ml strength', group: antibioticOtherTabletsSyrup)
            neo.save new Product(name: 'Trimago Syrup - 1000ml - 240mg/5ml strength', group: antibioticOtherTabletsSyrup)
            neo.save new Product(name: 'Trimoxol - 60ml - 125mg/5ml strength', group: antibioticOtherTabletsSyrup)
            neo.save new Product(name: 'Trimoxol Suspension - 60ml - 240mg/5ml strength', group: antibioticOtherTabletsSyrup)
            neo.save new Product(name: 'Unitrim - 60ml - 125mg/5ml strength', group: antibioticOtherTabletsSyrup)
            neo.save new Product(name: 'Unitrim Suspension - 60ml - 240mg/5ml strength', group: antibioticOtherTabletsSyrup)
            neo.save new Product(name: 'Unitrim Suspension - 60ml - 125mg/5ml strength', group: antibioticOtherTabletsSyrup)
            neo.save new Product(name: 'Unitrim Suspension - 100ml - 240mg/5ml strength', group: antibioticOtherTabletsSyrup)
            neo.save new Product(name: 'Other (specify)', group: antibioticOtherTabletsSyrup)
            neo.save new Product(name: 'Astel Malaria Test Pf', group: rdt)
            neo.save new Product(name: 'Other (specify)', group: rdt)
        }
    }

}
