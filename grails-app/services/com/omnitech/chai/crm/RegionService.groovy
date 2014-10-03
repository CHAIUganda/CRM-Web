package com.omnitech.chai.crm

import com.omnitech.chai.model.*
import com.omnitech.chai.util.ModelFunctions
import org.springframework.data.domain.Page
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

    /* Districts */

    List<District> listAllDistricts() { districtRepository.findAll().collect() }

    District findDistrict(Long id) { districtRepository.findOne(id) }

    District saveDistrict(District district) { ModelFunctions.saveEntity(districtRepository, district) }

    void deleteDistrict(Long id) { districtRepository.delete(id) }

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

}