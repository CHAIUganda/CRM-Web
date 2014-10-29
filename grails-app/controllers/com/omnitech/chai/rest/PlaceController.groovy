package com.omnitech.chai.rest

import com.omnitech.chai.model.Region
import grails.transaction.Transactional

import static org.springframework.http.HttpStatus.*

/**
 * Created by kay on 10/29/14.
 */
class PlaceController {


    static responseFormats = ['json', 'xml']
    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]
    def regionService

    //id,name,uuid
    def regions(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        def regions = regionService.listAllRegions().collect { [id: it.id, name: it.name, uuid: it.uuid] }
        respond regions, [status: OK]
    }

    //id,name,uuid
    def districts(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        def districts = regionService.listAllDistricts().collect { [id: it.id, name: it.name, uuid: it.uuid] }
        respond districts, [status: OK]
    }

    //id,name,uuid
    def subCounties(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        def subCounties = regionService.listAllSubCountys().collect { [id: it.id, name: it.name, uuid: it.uuid] }
        respond subCounties, [status: OK]
    }


    @Transactional
    def save(Region region) {
        if (region == null) {
            render status: NOT_FOUND
            return
        }

        region.validate()
        if (region.hasErrors()) {
            render status: NOT_ACCEPTABLE
            return
        }

        region.save flush: true
        respond region, [status: CREATED]
    }

    @Transactional
    def update(Region region) {
        if (region == null) {
            render status: NOT_FOUND
            return
        }

        region.validate()
        if (region.hasErrors()) {
            render status: NOT_ACCEPTABLE
            return
        }

        region.save flush: true
        respond region, [status: OK]
    }

    @Transactional
    def delete(Region region) {

        if (region == null) {
            render status: NOT_FOUND
            return
        }

        region.delete flush: true
        render status: NO_CONTENT
    }


}
