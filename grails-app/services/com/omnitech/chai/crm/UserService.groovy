package com.omnitech.chai.crm

import com.omnitech.chai.model.Device
import com.omnitech.chai.model.RequestMap
import com.omnitech.chai.model.Role
import com.omnitech.chai.model.User
import com.omnitech.chai.repositories.DeviceRepository
import com.omnitech.chai.repositories.RequestMapRepository
import com.omnitech.chai.repositories.RoleRepository
import com.omnitech.chai.repositories.UserRepository
import com.omnitech.chai.util.PageUtils
import org.codehaus.groovy.runtime.InvokerHelper
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
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
    RequestMapRepository requestMapRepository

    Page<User> list(Map params) {
        def request = PageUtils.create(params)
        new PageImpl<User>(userRepository.findAll(request).content, request, userRepository.count())
    }

    User findUser(Long id) {
        userRepository.findOne(id)
    }

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
        userRepository.save(user)
    }

    Role saveRole(Role role) {
        roleRepository.save(role)
    }

    RequestMap saveRequestMap(RequestMap requestMap) {
        requestMapRepository.save(requestMap)
    }


    void deleteRequestMap(Long id) {
        requestMapRepository.delete(id)
    }

    def deleteRole(Long id) {
        roleRepository.delete(id)
    }

    Page<Device> listDevices(Map params) {
        def pageRequest = PageUtils.create(params)
        def p = deviceRepository.findAll(pageRequest)
        return new PageImpl<Device>(p.content, pageRequest, deviceRepository.count())
    }


    Device findDevice(Long id) {
        deviceRepository.findOne(id)
    }

    Device saveDevice(Device device) {
        deviceRepository.save(device)
    }

    void deleteDevice(Long id) {
        deviceRepository.delete(id)
    }


    User saveUserWithRoles(User user, List roleIds) {

        def roles = roleIds?.collect { roleRepository.findOne(it as Long) }  ?: []

        def neoUser
        if (user.id) neoUser = userRepository.findOne(user.id)
        else neoUser = user

        InvokerHelper.setProperties(neoUser, user.properties)
        neoUser.roles = roles

        userRepository.save(neoUser)
    }


}
