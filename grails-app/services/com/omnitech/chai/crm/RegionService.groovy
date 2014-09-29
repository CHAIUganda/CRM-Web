package com.omnitech.chai.crm

import com.omnitech.chai.model.District
import com.omnitech.chai.model.Region
import com.omnitech.chai.repositories.DistrictRepository
import com.omnitech.chai.repositories.RegionRepository
import com.omnitech.chai.util.ChaiUtils
import org.springframework.data.neo4j.transaction.Neo4jTransactional

/**
 * Created by kay on 9/29/14.
 */
@Neo4jTransactional
class RegionService {

    DistrictRepository districtRepository
    RegionRepository regionRepository


    List<District> listAllDistricts() {
        districtRepository.findAll().collect()
    }

    District findDistrict(Long id) {
        districtRepository.findOne(id)
    }

    District saveDistrict(District district) {
        def neoObj = district
        if (district.id) {
            neoObj = districtRepository.findOne(district.id)
        }
        ChaiUtils.bind(neoObj, district.properties)
        districtRepository.save(neoObj)
    }

    void deleteDistrict(Long id) {
        districtRepository.delete(id)
    }

    /*  Regions */

    List<Region> listAllRegions() {
        regionRepository.findAll().collect()
    }

    Region findRegion(Long id) {
        regionRepository.findOne(id)
    }

    Region saveRegion(Region region) {
        def neoObj = region
        if (region.id) {
            neoObj = regionRepository.findOne(region.id)
            ChaiUtils.bind(neoObj, region.properties)
        }
        regionRepository.save(neoObj)

    }

    void deleteRegion(Long id) {
        regionRepository.delete(id)
    }

}
