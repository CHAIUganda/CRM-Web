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

    //id,name,uuid
    def regions() {
        def regions = regionService.listAllRegions().collect { [id: it.id, name: it.name, uuid: it.uuid] }
        respond regions, [status: OK]
    }

    //id,name,uuid
    def districts() {
        def districts = regionService.listAllDistricts().collect { [id: it.id, name: it.name, uuid: it.uuid] }
        respond districts, [status: OK]
    }

    //id,name,uuid
    def subCounties() {
        def subCounties = regionService.listAllSubCountys().collect { [id: it.id, name: it.name, uuid: it.uuid] }
        respond subCounties, [status: OK]
    }
}
