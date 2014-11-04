package com.omnitech.chai.rest

import com.omnitech.chai.model.Parish
import com.omnitech.chai.model.SubCounty
import com.omnitech.chai.util.ModelFunctions
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
        def userId = neoSecurityService.currentUser.id
        def regions = regionService.findAllRegionsForUser(userId).collect { [id: it.id, name: it.name, uuid: it.uuid] }
        respond regions
    }

    //id,name,uuid,regionId
    def districts() {
        def districts = regionService.findAllDistrictsForUser(neoSecurityService.currentUser.id).collect {
            [id: it.id, name: it.name, uuid: it.uuid, 'regionId': it.region.id]
        }
        respond districts
    }

    //id,name,uuid,districtId
    def subCounties() {
        def subCounties = regionService.findAllSubCountiesForUser(neoSecurityService.currentUser.id).collect {
            [id: it.id, name: it.name, uuid: it.uuid, 'districtId': it.district.id]
        }
        respond subCounties
    }

    def updateSubCounty() {
        def json = request.JSON as Map
        def id = ModelFunctions.extractId(json, 'districtId')
        def district = id != -1 ? regionService.findDistrict(id) : null
        if (id == -1) {
            response.status = HttpStatus.BAD_REQUEST.value()
            render([status: 'error', message: 'You Did Not Specify a Valid District'] as JSON)
            return
        }

        def subCounty = ModelFunctions.bind(new SubCounty(), json)
        subCounty.district = district
        regionService.saveSubCounty(subCounty)
        respond status: HttpStatus.OK
    }

    def updateParish() {
        def json = request.JSON as Map
        def subCounty = extractAndLoadParent('subCountyId', json) { Long id -> regionService.findSubCounty(id) }
        if (!subCounty) {
            renderError('You Did Not Specify a Valid Parish')
            return
        }
        Parish parish = ModelFunctions.bind(Parish, json)
        parish.subCounty =  subCounty
        regionService.saveParish(parish)
        respond status: HttpStatus.OK
    }

    //id,name,uuid,subCountyId
    def parishes() {
        def parishes = regionService.findAllParishesForUser(neoSecurityService.currentUser.id).collect {
            [id: it.id, name: it.name, uuid: it.uuid, 'subCountyId': it.subCounty.id]
        }
        respond parishes
    }

    //id,name,uuid,parishId
    def villages() {
        def villages = regionService.findAllVillagesForUser(neoSecurityService.currentUser.id).collect {
            [id: it.id, name: it.name, uuid: it.uuid, 'parishId': it.parish.id]
        }
        respond villages
    }

    def renderError(String error) {
        response.status = HttpStatus.INTERNAL_SERVER_ERROR.value()
        render([status: 'error', message: error] as JSON)
    }


}
