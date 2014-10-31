package com.omnitech.chai.crm

import com.omnitech.chai.model.*
import com.omnitech.chai.util.ModelFunctions
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.neo4j.support.Neo4jTemplate
import org.springframework.data.neo4j.transaction.Neo4jTransactional

import static com.omnitech.chai.util.ModelFunctions.getOrCreate

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
    @Autowired
    Neo4jTemplate neo

    /* Districts */

    List<District> listAllDistricts() { districtRepository.findAll().collect() }

    District findDistrict(Long id) { districtRepository.findOne(id) }

    District saveDistrict(District district) { ModelFunctions.saveEntity(districtRepository, district) }

    void deleteDistrict(Long id) { districtRepository.delete(id) }

    List<District> listAllDistrictWithSubCounties() { districtRepository.listAllDistrictsWithSubCounties().collect() }

    /*  Regions */

    List<Region> listAllRegions() { regionRepository.findAll().collect() }

    Region findRegion(Long id) { regionRepository.findOne(id) }

    Region saveRegion(Region region) { ModelFunctions.saveEntity(regionRepository, region) }

    void deleteRegion(Long id) { regionRepository.delete(id) }

    Region getOrCreateRegion(String regionName) {
        def region = regionRepository.findByName(regionName)
        if (region) {
            region = new Region(name: regionName)
            regionRepository.save(region)
        }
        return region
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

    Parish getOrCreateParish(SubCounty sc, String name) {
        getOrCreate(
                { parishRepository.findBySubCountyAndName(sc.id, name) },
                { parishRepository.save(new Parish(subCounty: sc, name: name)) }
        )
    }

    Village getOrCreateVillage(Parish parish, String name) {
        getOrCreate(
                { villageRepository.findByParishAndName(parish.id, name) },
                { villageRepository.save(new Village(parish: parish, name: name)) }
        )
    }

    /* SubCounty */

    Page<SubCounty> listSubCountys(Map params) { ModelFunctions.listAll(subCountyRepository, params) }

    List<SubCounty> listAllSubCountys() { subCountyRepository.findAll().collect() }

    SubCounty findSubCounty(Long id) { subCountyRepository.findOne(id) }

    SubCounty saveSubCounty(SubCounty subCounty) { ModelFunctions.saveEntity(subCountyRepository, subCounty) }

    void deleteSubCounty(Long id) { subCountyRepository.delete(id) }

    Page<SubCounty> searchSubCountys(String search, Map params) {
        ModelFunctions.searchAll(neo, SubCounty, ModelFunctions.getWildCardRegex(search), params)
    }

    /* Parish */

    Page<Parish> listParishs(Map params) { ModelFunctions.listAll(parishRepository, params) }

    List<Parish> listAllParishs() { parishRepository.findAll().collect() }

    Parish findParish(Long id) { parishRepository.findOne(id) }

    Parish saveParish(Parish parish) { ModelFunctions.saveEntity(parishRepository, parish) }

    void deleteParish(Long id) { parishRepository.delete(id) }

    /* Village */

    Page<Village> listVillages(Map params) { ModelFunctions.listAll(villageRepository, params) }

    List<Village> listAllVillages() { villageRepository.findAll().collect() }

    Village findVillage(Long id) { villageRepository.findOne(id) }

    Village saveVillage(Village village) { ModelFunctions.saveEntity(villageRepository, village) }

    void deleteVillage(Long id) { villageRepository.delete(id) }

    /* Territory */

    Page<Territory> listTerritorys(Map params) { ModelFunctions.listAll(territoryRepository, params) }

    List<Territory> listAllTerritorys() { territoryRepository.findAll().collect() }

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
        territory.subCounties.each {
            if (it.district.id == districtId && !scIds.contains(it.id)) {
                it.territory = null
                subCountyRepository.save(it)
            }
        }

        scToBeMapped.each {
            it.territory = territory
            subCountyRepository.save(it)
        }
    }


}
