package com.omnitech.chai.crm

import com.omnitech.chai.model.*
import com.omnitech.chai.util.ModelFunctions
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.neo4j.support.Neo4jTemplate
import org.springframework.data.neo4j.transaction.Neo4jTransactional

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

    List<District> listAllDistrictWithSubCounties(){districtRepository.listAllDistrictsWithSubCounties().collect()}

    /*  Regions */

    List<Region> listAllRegions() { regionRepository.findAll().collect() }

    Region findRegion(Long id) { regionRepository.findOne(id) }

    Region saveRegion(Region region) { ModelFunctions.saveEntity(regionRepository, region) }

    void deleteRegion(Long id) { regionRepository.delete(id) }

    /* SubCounty */

    Page<SubCounty> listSubCountys(Map params) { ModelFunctions.listAll(subCountyRepository, params) }

    List<SubCounty> listAllSubCountys() { subCountyRepository.findAll().collect() }

    SubCounty findSubCounty(Long id) { subCountyRepository.findOne(id) }

    SubCounty saveSubCounty(SubCounty subCounty) { ModelFunctions.saveEntity(subCountyRepository, subCounty) }

    void deleteSubCounty(Long id) { subCountyRepository.delete(id) }

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

    void mapTerritoryToSubs(long id, List<Long> scIds) {
        def territory = territoryRepository.findOne(id)
        def subCounties = scIds.collect { subCountyRepository.findOne(it as Long) }

        subCounties.each {
            it.territory = territory
            subCountyRepository.save(it)
        }

        /// get unique districts for the new SubCounties
        def uniqueDistricts = subCounties.collect { it.district }.unique { it.id }

        //remove all subCounties that belong to those districts
        def subCountitesDistrict = territory.subCounties.findAll { sc ->
            uniqueDistricts.any {
                it.id == sc.district.id
            }
        }

        //find all unMapped subcounties and persist them
        subCountitesDistrict.findAll { sc -> scIds.every { sc.id != it } }.each {
            it.territory = null
            subCountyRepository.save(it)
        }
    }

}
