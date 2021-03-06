package com.omnitech.chai.crm

import com.omnitech.chai.exception.ImportException
import com.omnitech.chai.model.*
import com.omnitech.chai.util.ModelFunctions
import com.omnitech.chai.util.PageUtils
import fuzzycsv.FuzzyCSV
import fuzzycsv.Record
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.neo4j.support.Neo4jTemplate
import org.springframework.data.neo4j.transaction.Neo4jTransactional
import secondstring.PhraseHelper

import static com.omnitech.chai.model.Relations.SUPERVISES_TERRITORY
import static com.omnitech.chai.model.Relations.USER_TERRITORY
import static com.omnitech.chai.util.ChaiUtils.prop
import static com.omnitech.chai.util.ModelFunctions.getOrCreate
import static fuzzycsv.RecordFx.fn
import static org.neo4j.cypherdsl.CypherQuery.*

/**
 * Created by kay on 9/29/14.
 */
@Neo4jTransactional
class RegionService {

    def districtRepository
    def regionRepository
    def subCountyRepository
    def parishRepository
    def villageRepository
    def territoryRepository
    def userRepository
    @Autowired
    Neo4jTemplate neo

    /* Districts */

    List<District> listAllDistricts() { districtRepository.findAll().collect() }

    District findDistrict(Long id) { districtRepository.findOne(id) }

    District saveDistrict(District district) { ModelFunctions.saveEntity(districtRepository, district) }

    void deleteDistrict(Long id) { districtRepository.delete(id) }

    List<District> listAllDistrictWithSubCounties() { districtRepository.listAllDistrictsWithSubCounties().collect() }

    Page<District> searchDistricts(String search, Map params) {
        ModelFunctions.searchAll(neo, District, ModelFunctions.getWildCardRegex(search), params)
    }

    List<District> findAllDistrictsForUser(Long userId) { districtRepository.findAllForUser(userId).collect() }

    /*  Regions */

    List<Region> listAllRegions() { regionRepository.findAll().collect() }

    List<Region> findAllRegionsForUser(Long id) { regionRepository.findAllRegionsForUser(id).collect() }

    Region findRegion(Long id) { regionRepository.findOne(id) }

    Region saveRegion(Region region) { ModelFunctions.saveEntity(regionRepository, region) }

    void deleteRegion(Long id) { regionRepository.delete(id) }

    Region getOrCreateRegion(String regionName) {
        getOrCreate(
                { regionRepository.findByName(regionName) },
                { regionRepository.save(new Region(name: regionName)) }
        )
    }

    District getOrCreateDistrict(Region region, String dName) {
        getOrCreate(
                { districtRepository.findByRegionAndName(region.id, dName) },
                { districtRepository.save(new District(name: dName, region: region)) }
        )
    }

    SubCounty getOrCreateSubCounty(District district, String name) {
        getOrCreate(
                { subCountyRepository.findByDistrictAndName(district.id, name) },
                { subCountyRepository.save(new SubCounty(district: district, name: name)) }
        )
    }

    Parish getOrCreateParish(String name) {
        getOrCreate(
                { parishRepository.findByName(name) },
                { parishRepository.save(new Parish(name: name)) }
        )
    }

    Village getOrCreateVillage(String name) {
        getOrCreate(
                { villageRepository.findByName(name) },
                { villageRepository.save(new Village(name: name)) }
        )
    }

    Territory getOrCreateTerrioty(String name) {
        getOrCreate(
                { territoryRepository.findByName(name) },
                { territoryRepository.save(new Territory(name: name)) }
        )
    }

    /* SubCounty */

    Page<SubCounty> listSubCountys(Map params) { ModelFunctions.listAll(subCountyRepository, params) }

    List<SubCounty> listAllSubCountys() { subCountyRepository.findAll().collect() }

    SubCounty findSubCounty(Long id) { subCountyRepository.findOne(id) }

    SubCounty findSubCounty(String uuid) { subCountyRepository.findByUuid(uuid) }

    SubCounty saveSubCounty(SubCounty subCounty) { ModelFunctions.saveEntity(subCountyRepository, subCounty) }

    void deleteSubCounty(Long id) { subCountyRepository.delete(id) }

    Page<SubCounty> searchSubCountys(String search, Map params) {
        ModelFunctions.searchAll(neo, SubCounty, ModelFunctions.getWildCardRegex(search), params)
    }

    List<SubCounty> findAllSubCountiesForUser(Long userId) { subCountyRepository.findAllForUser(userId).collect() }

    /* Parish */

    Page<Parish> listParishs(Map params) { ModelFunctions.listAll(parishRepository, params) }

    List<Parish> listAllParishs() { parishRepository.findAll().collect() }

    Parish findParish(Long id) { parishRepository.findOne(id) }

    Parish saveParish(Parish parish) { ModelFunctions.saveEntity(parishRepository, parish) }

    void deleteParish(Long id) { parishRepository.delete(id) }

    List<Parish> findAllParishesForUser(Long userId) { parishRepository.findAllForUser(userId).collect() }

    /* Village */

    Page<Village> listVillages(Map params) { ModelFunctions.listAll(villageRepository, params) }

    List<Village> listAllVillages() { villageRepository.findAll().collect() }

    Village findVillage(Long id) { villageRepository.findOne(id) }

    Village saveVillage(Village village) { ModelFunctions.saveEntity(villageRepository, village) }

    void deleteVillage(Long id) { villageRepository.delete(id) }

    List<Village> findAllVillagesForUser(Long userId) { villageRepository.findAllForUser(userId).collect() }

    /* Territory */

    Page<Territory> listTerritorys(Map params) { ModelFunctions.listAll(territoryRepository, params) }

    List<Territory> listAllTerritorys() { territoryRepository.findAll().collect()?.sort { it.name } }

    Page<Territory> findTerritoriesForUser(User user, Map params) {

        if (user.hasRole(Role.ADMIN_ROLE_NAME, Role.SUPER_ADMIN_ROLE_NAME)) {
            return listTerritorys(params)
        }

        def query = {
            start(nodesById('user', user.id))
                    .match(node('user').out(SUPERVISES_TERRITORY, USER_TERRITORY).node('territory'))

        }

        def q = query().returns(distinct(identifier('territory')))
        def cq = query().returns(count(distinct(identifier('territory'))))
        PageUtils.addPagination(q, params, Territory)
        ModelFunctions.query(neo, q, cq, params, Territory)
    }

    Territory findTerritory(Long id) { territoryRepository.findOne(id) }

    Territory saveTerritory(Territory territory) { ModelFunctions.saveEntity(territoryRepository, territory) }

    void deleteTerritory(Long id) { territoryRepository.delete(id) }

    Page<Territory> searchTerritorys(String search, Map params) {
        ModelFunctions.searchAll(neo, Territory, ModelFunctions.getWildCardRegex(search), params)
    }

    void mapTerritoryToSubs(long id, long districtId, List<Long> scIds) {
        def territory = territoryRepository.findOne(id)
        def scToBeMapped = scIds?.collect { subCountyRepository.findOne(it as Long) }

        assert scToBeMapped.every { it.district.id == districtId }

        neo.fetch(territory.subCounties)
//        territory.subCounties.each {
//            if (it.district.id == districtId && !scIds.contains(it.id)) {
//                it.territory = null
//                subCountyRepository.save(it)
//            }
//        }

        if (!territory.subCounties) {
            territory.subCounties = new HashSet<SubCounty>()
        }

        territory.subCounties.removeAll(territory.subCounties.findAll { it.district?.id == districtId })
        territory.subCounties.addAll(scToBeMapped)
        territoryRepository.save(territory)
    }

    void importTerritories(String s) {
        def csv = FuzzyCSV.parseCsv(s)
        csv = csv.collect { it as List<String> }


        def districts = listAllDistricts()
        districts.each {
            log.trace("neo.fetch(it.subCounties) $it")
            neo.fetch(it.subCounties)
        }
        def fuzzyEngine = PhraseHelper.train(districts?.collect { it.name })

        FuzzyCSV.map(csv, fn { Record record ->
            try {
                processRecord(record, fuzzyEngine, districts)
            } catch (Throwable ex) {
                def e = new ImportException("Error on Record[${record.idx()}]: $ex.message".toString())
                e.stackTrace = ex.stackTrace
                log.error("Error:..... $e.message")
                throw e
            }
        })
    }

    void processRecord(Record record, PhraseHelper fuzzyEngine, List<District> districts) {

        def modified = false
        def territoryName = prop(record, 'territory').trim()
        def territory = getOrCreateTerrioty(territoryName)
        if (territory.id) {
            neo.fetch territory.subCounties
        }

        def commaSeparatedDistrictNames = prop(record, 'districts')

        assert commaSeparatedDistrictNames, 'You Should Provide Districts'

        def districtNames = commaSeparatedDistrictNames.split(',').collect { it.removeExtraSpace() }
        for (unVerifiedDistrict in districtNames) {

            def districtName = fuzzyEngine.bestInternalHit(unVerifiedDistrict, 90)

            if (!districtName) {
                log.error "$unVerifiedDistrict should exist in the database. Did you mean ${fuzzyEngine.internalHits(commaSeparatedDistrictNames, 70)}"
                continue
            }

            assert districtName, "$unVerifiedDistrict should exist in the database. Did you mean ${fuzzyEngine.internalHits(commaSeparatedDistrictNames, 70)}"

            def district = districts.find { it.name == districtName }

            def commaSepSubCounties = prop(record, "$unVerifiedDistrict-subCounties", false)


            if (!commaSepSubCounties) {
                modified = true
                mapTerritoryToSubs(territory.id, district.id, district.subCounties.collect { it.id })
//                territory.subCounties.addAll(district.subCounties)
            } else {
                def unVerifiedScNames = commaSepSubCounties.split(',')
                def engine = getFuzzyEngine.call(district)
                for (unVerifiedSc in unVerifiedScNames) {
                    def hit = engine.bestInternalHit(unVerifiedSc, 80)
                    SubCounty scObj
                    if (hit) {
                        scObj = district.subCounties.find { it.name == hit }
                    } else {
                        scObj = getOrCreateSubCounty(district, unVerifiedSc)
                    }

                    modified = true
                    mapTerritoryToSubs(territory.id, district.id, [scObj.id])
//                    territory.subCounties.add(scObj)
                }
            }
        }

        if (!modified && !territory.subCounties)
            deleteTerritory(territory.id)


    }

    def importDistricts(String s) {
        s = s.toUpperCase()
        def csv = FuzzyCSV.parseCsv(s)
        csv = csv.collect { it as List<String> }

        FuzzyCSV.map(csv, fn { Record record ->
            //Processing Regions
            String regionName = prop(record, 'REGION', false)
            Region region
            if (regionName)
                region = getOrCreateRegion(regionName.toUpperCase())
            else
                region = getOrCreateRegion('DEFAULT REGION')

            String districtName = prop(record, 'DISTRICT')
            def district = getOrCreate(
                    { districtRepository.findByName(districtName) },
                    { districtRepository.save(new District(region: region, name: districtName)) })


            String subCountyName = prop(record, 'SUBCOUNTY')
            getOrCreateSubCounty(district, subCountyName)
        })
    }


    def getFuzzyEngine = { District district ->
        PhraseHelper.train(district.subCounties.collect { it.name })
    }.memoizeBetween(0, 100)

    List<Territory> findAllTerritoriesByType(String s) {
        territoryRepository.findAllByType(s).collect()
    }
}

