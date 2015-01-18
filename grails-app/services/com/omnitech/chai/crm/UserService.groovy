package com.omnitech.chai.crm

import com.omnitech.chai.model.Device
import com.omnitech.chai.model.RequestMap
import com.omnitech.chai.model.Role
import com.omnitech.chai.model.User
import com.omnitech.chai.repositories.DeviceRepository
import com.omnitech.chai.repositories.RequestMapRepository
import com.omnitech.chai.repositories.RoleRepository
import com.omnitech.chai.repositories.UserRepository
import com.omnitech.chai.util.ModelFunctions
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.neo4j.support.Neo4jTemplate
import org.springframework.data.neo4j.transaction.Neo4jTransactional

/**
 * UserService
 * A service class encapsulates the core business logic of a Grails application
 */
@Neo4jTransactional
class UserService {

    UserRepository userRepository
    RoleRepository roleRepository
    DeviceRepository deviceRepository
    def territoryRepository
    RequestMapRepository requestMapRepository
    def neoSecurityService
    @Autowired
    Neo4jTemplate neo

    Page<User> list(Map params) {
        ModelFunctions.listAll(neo,User, params,User)
    }

    List<User> listAllUsers(Map params) { userRepository.findAll().collect() }

    User findUser(Long id) {
        userRepository.findOne(id)
    }

    User findUserByName(String name) { userRepository.findByUsername(name) }

    Role findRole(Long id) {
        roleRepository.findOne(id)
    }

    RequestMap findRequestMap(Long id) {
        requestMapRepository.findOne(id)
    }

    List<Role> listAllRoles() {
        roleRepository.findAll().collect() as List
    }

    List<RequestMap> listAllRequestMaps() {
        requestMapRepository.findAll().collect() as List
    }

    void deleteUser(Long id) {
        userRepository.delete(id)
    }

    User saveUser(User user) {
        ModelFunctions.saveEntity(userRepository, user)
    }

    Role saveRole(Role role) {
        ModelFunctions.saveEntity(roleRepository, role)
    }

    Role findRoleByAuthority(String authority) {
        roleRepository.findByAuthority(authority)
    }

    RequestMap saveRequestMap(RequestMap requestMap) {
        neoSecurityService.clearCachedRequestmaps()
        ModelFunctions.saveEntity(requestMapRepository, requestMap)
    }


    void deleteRequestMap(Long id) {
        requestMapRepository.delete(id)
    }

    def deleteRole(Long id) {
        neoSecurityService.clearCachedRequestmaps()
        roleRepository.delete(id)
    }

    Page<Device> listDevices(Map params) {
        ModelFunctions.listAll(deviceRepository, params)
    }

    /**
     * Return a list of all available devices including the one assigned to this device
     * @param userId current user id. -1 if u do not wish to provide it
     * @return
     */
    List<Device> listAllFreeDevices(Long userId = -1) {
        if (userId < 0)
            return deviceRepository.findAllFreeDevices().collect()
        return deviceRepository.findAllFreeDevices(userId).collect()
    }


    Device findDevice(Long id) {
        deviceRepository.findOne(id)
    }

    Device saveDevice(Device device) {
        ModelFunctions.saveEntity(deviceRepository, device)
    }

    void deleteDevice(Long id) {
        deviceRepository.delete(id)
    }


    User saveUserWithRoles(User user, List roleIds, Long deviceId) {

        def roles = roleIds?.collect { roleRepository.findOne(it as Long) } ?: []
        def neoUser = user.id ? userRepository.findOne(user.id) : user
        def device = deviceId ? deviceRepository.findOne(deviceId) : null




        if (user.password != neoUser.password || !user.id) {
            def newPass = neoSecurityService.encodePassword(user.password)
            user.password = newPass
        }

        ModelFunctions.bind(neoUser, user.properties)
        neoUser.roles = roles
        neoUser.device = device

        userRepository.save(neoUser)
    }

    User mapUserToTerritories(Long userId, List territoryIds){

        def user = findUser(userId)

        def territories =  territoryIds.collect{territoryRepository.findOne(it as Long)}

        //first delete old references
        territories.each {
            it.supervisor = null
            territoryRepository.save(it)
        }
        user.supervisedTerritories = new HashSet(territories)
        saveUser(user)
    }

    Page<User> searchUsers(String search, Map params) {
        ModelFunctions.searchAll(neo, User, ModelFunctions.getWildCardRegex(search), params)
    }

    Page<Device> searchDevices(String search, Map params) {
        ModelFunctions.searchAll(neo, Device, ModelFunctions.getWildCardRegex(search), params)
    }
}
