package com.omnitech.chai.rest

import static org.springframework.http.HttpStatus.OK

/**
 * Created by kay on 10/29/14.
 */
class PlaceController {


    static namespace = 'rest'
    static responseFormats = ['json', 'xml']
    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]
    def regionService
    def neoSecurityService

    //id,name,uuid
    def regions() {
        def userId = neoSecurityService.currentUser.id
        def regions = regionService.findAllRegionsForUser(userId).collect { [id: it.id, name: it.name, uuid: it.uuid] }
        respond regions, [status: OK]
    }

    //id,name,uuid,regionId
    def districts() {
        def districts = regionService.findAllDistrictsForUser(neoSecurityService.currentUser.id).collect {
            [id: it.id, name: it.name, uuid: it.uuid, 'region.id': it.region.id]
        }
        respond districts, [status: OK]
    }

    //id,name,uuid,districtId
    def subCounties() {
        def subCounties = regionService.findAllSubCountiesForUser(neoSecurityService.currentUser.id).collect {
            [id: it.id, name: it.name, uuid: it.uuid, 'district.id': it.district.id]
        }
        respond subCounties, [status: OK]
    }

    //id,name,uuid,subCountyId
    def parishes() {
        def subCounties = regionService.findAllParishesForUser(neoSecurityService.currentUser.id).collect {
            [id: it.id, name: it.name, uuid: it.uuid, 'subCountyId': it.subCounty.id]
        }
        respond subCounties, [status: OK]
    }


}
