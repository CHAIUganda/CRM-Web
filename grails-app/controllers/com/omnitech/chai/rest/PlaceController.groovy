package com.omnitech.chai.rest

import com.omnitech.chai.model.Role
import grails.converters.JSON
import org.springframework.http.HttpStatus

import static com.omnitech.chai.util.ModelFunctions.extractAndLoadParent

/**
 * Created by kay on 10/29/14.
 */
class PlaceController {


    static namespace = 'rest'
    static responseFormats = ['json', 'xml']
    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE", updateSubCounty: 'POST']
    def regionService
    def neoSecurityService

    //id,name,uuid
    def regions() {
        log.debug("Req:${neoSecurityService.currentUser}-RegionsList")
        def userId = neoSecurityService.currentUser.id
        def regions = regionService.findAllRegionsForUser(userId).collect { [id: it.id, name: it.name, uuid: it.uuid] }
        log.debug("Resp:${neoSecurityService.currentUser}-${regions?.size()} Regions")
        respond regions
    }

    //id,name,uuid,regionId
    def districts() {
        log.debug("Req:${neoSecurityService.currentUser}-DistrictList")
        def districts = regionService.findAllDistrictsForUser(neoSecurityService.currentUser.id).collect {
            [id: it.id, name: it.name, uuid: it.uuid, 'regionId': it.region.uuid]
        }
        log.debug("Resp:${neoSecurityService.currentUser}-${districts?.size()} Districts")
        respond districts
    }

    //id,name,uuid,districtId
    def subCounties() {
        log.debug("Req:${neoSecurityService.currentUser}-SubCountyList")
        def subCounties = regionService.findAllSubCountiesForUser(neoSecurityService.currentUser.id).collect {
            [id: it.id, name: it.name, uuid: it.uuid, 'districtId': it.district.uuid]
        }
        log.debug("Resp:${neoSecurityService.currentUser}-${subCounties?.size()} SubCounties")
        respond subCounties
    }

    def updateSubCounty() {
        def json = request.JSON as Map
        def district = extractAndLoadParent('districtId', json) { Long id -> regionService.findDistrict(id) }
        if (!district) {
            renderError 'You Did Not Specify a Valid District'
            return
        }
        if (!json.name) {
            renderError 'You Did Not Specify A District Name'
        }
        regionService.getOrCreateSubCounty(district, json.name as String)
        respond status: HttpStatus.OK
    }

    def updateParish() {
        def json = request.JSON as Map
        def subCounty = extractAndLoadParent('subCountyId', json) { Long id -> regionService.findSubCounty(id) }
        if (!subCounty) {
            renderError('You Did Not Specify a Valid SubCounty')
            return
        }

        if (!json.name) {
            renderError('You Did Not Specify A Parish Name')
            return
        }
        regionService.getOrCreateParish(subCounty, json.name as String)
        respond status: HttpStatus.OK
    }

    def updateVillage() {
        def json = request.JSON as Map
        def parish = extractAndLoadParent('parishId', json) { Long id -> regionService.findParish(id) }
        if (!parish) {
            renderError('You Did Not Specify a Valid Parish')
            return
        }

        if (!json.name) {
            renderError('You Did Not Specify A Village Name')
            return
        }
        regionService.getOrCreateVillage(parish, json.name as String)
        respond status: HttpStatus.OK
    }

    //id,name,uuid,subCountyId
    def parishes() {
        log.debug("Req:${neoSecurityService.currentUser}-ParishList")
        def parishes = regionService.findAllParishesForUser(neoSecurityService.currentUser.id).collect {
            [id: it.id, name: it.name, uuid: it.uuid]
        }
        log.debug("Resp:${neoSecurityService.currentUser}-${parishes?.size()} Parishes")
        respond parishes
    }

    //id,name,uuid,parishId
    def villages() {
        log.debug("Req:${neoSecurityService.currentUser}-VillageList")
        def villages = regionService.findAllVillagesForUser(neoSecurityService.currentUser.id).collect {
            [id: it.id, name: it.name, uuid: it.uuid]
        }
        log.debug("Resp:${neoSecurityService.currentUser}-${villages?.size()} Villages")
        respond villages
    }

    def info() {
        def user = neoSecurityService.currentUser
        def role = 'NONE'
        if (user.hasRole(Role.DETAILER_ROLE_NAME)) {
            role = Role.DETAILER_ROLE_NAME
        } else if (user.hasRole(Role.SALES_ROLE_NAME)) {
            role = Role.SALES_ROLE_NAME
        }
        respond([userName: user.username, role: role])
    }

    def renderError(String error) {
        response.status = HttpStatus.INTERNAL_SERVER_ERROR.value()
        render([status: 'error', message: error] as JSON)
    }


}
