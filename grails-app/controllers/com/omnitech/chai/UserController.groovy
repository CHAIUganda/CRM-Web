package com.omnitech.chai

import com.omnitech.chai.model.User
import grails.converters.JSON
import grails.transaction.Transactional

import static com.omnitech.chai.util.ModelFunctions.extractId
import static org.springframework.http.HttpStatus.*

/**
 * UserController
 * A controller class handles incoming web requests and performs actions such as redirects, rendering views and so on.
 */
class UserController {

    def userService
    def regionService

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index(Integer max) {
        params.max = Math.min(max ?: 50, 100)
        def page = userService.list(params)

        def territorys = regionService.listAllTerritorys()?.sort { it.name }
        respond page.content, model: [userInstanceCount: page.totalElements,
                                      territories      : territorys]
    }

    def search(Integer max) {
        params.max = Math.min(max ?: 50, 100)
        if (params.term) {
            redirect(action: 'search', id: params.term)
            return
        }
        def page = userService.searchUsers(params.id, params)
        respond page.content, view: 'index', model: [userInstanceCount: page.totalElements]
    }

    def show() {
        long id = extractId(params)
        if (!id == -1) {
            notFound(); return
        }
        respond userService.findUser(id)
    }

    def create() {
        def territories = regionService.listAllTerritorys()?.sort { it.name }
        respond new User(params), model: [rolez      : userService.listAllRoles(),
                                          devices    : userService.listAllFreeDevices(),
                                          territories: territories]
    }

    def save(User userInstance) {
        if (userInstance == null) {
            notFound()
            return
        }

        if (userInstance.hasErrors()) {
            respond userInstance.errors, view: 'create', model: [rolez: userService.listAllRoles(), devices: userService.listAllFreeDevices(userInstance.id ?: -1)]
            return
        }

        def dbUser = userService.saveUserWithRoles userInstance, getRoleIds(), ('' + params.dvc).toLongSafe()

        def territoryIds = getTerritoryIds()
        if (territoryIds)
            userService.mapUserToTerritories dbUser.id, territoryIds

        request.withFormat {
            form {
                flash.message = message(code: 'default.created.message', args: [message(code: 'userInstance.label', default: 'User'), userInstance.id])
                redirect(action: 'show', id: userInstance.id)
            }
            '*' { respond userInstance, [status: CREATED] }
        }
    }

    def userAsJson() {
        def id = extractId(params)
        if (id == -1) {
            render Collections.EMPTY_MAP as JSON
            return
        }

        def user = userService.findUser(id)

        if (!user) {
            render Collections.EMPTY_MAP as JSON

        } else {
            render([id: user.id, username: user.username] as JSON)
        }
    }

    def edit() {
        def id = extractId(params)

        if (id == -1) {
            notFound(); return
        }
        def user = userService.findUser(id)
        def territories = regionService.listAllTerritorys()?.sort { it.name }
        respond user, model: [rolez      : userService.listAllRoles(),
                              devices    : userService.listAllFreeDevices(id),
                              territories: territories]
    }


    def update(User userInstance) {
        if (userInstance == null) {
            notFound()
            return
        }

        if (userInstance.hasErrors()) {
            respond userInstance.errors, view: 'edit', model: [rolez: userService.listAllRoles(), devices: userService.listAllFreeDevices(userInstance.id)]
            return
        }

        def dbUser = userService.saveUserWithRoles userInstance, getRoleIds(), ('' + params.dvc).toLongSafe()

        def territoryIds = getTerritoryIds()
        if (territoryIds)
            userService.mapUserToTerritories dbUser.id, territoryIds



        request.withFormat {
            form {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'User.label', default: 'User'), userInstance.id])
                redirect(action: 'show', id: userInstance.id)
            }
            '*' { respond userInstance, [status: OK] }
        }
    }

    private List getRoleIds() {
        params.rolez instanceof String ? [params.rolez] : params.rolez
    }

    private List getTerritoryIds() {
        params.territories instanceof String ? [params.territories] : params.territories
    }

    @Transactional
    def delete() {

        if (params.id == null) {
            notFound()
            return
        }
        Long longId
        try {
            longId = params.id as Long
        } catch (Exception x) {
            log.error("could not convert [$params.id] to number", x)
            notFound()
            return
        }
        userService.deleteUser(longId)
        request.withFormat {
            form {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'User.label', default: 'User'), longId])
                redirect(action: 'index')
            }
            '*' { render status: NO_CONTENT }
        }


    }

    protected void notFound() {
        request.withFormat {
            form {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'userInstance.label', default: 'User'), params.id])
                redirect action: "index", method: "GET"
            }
            '*' { render status: NOT_FOUND }
        }
    }
}
