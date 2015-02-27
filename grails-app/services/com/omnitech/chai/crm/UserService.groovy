package com.omnitech.chai.crm

import com.omnitech.chai.exception.ImportException
import com.omnitech.chai.model.*
import com.omnitech.chai.repositories.DeviceRepository
import com.omnitech.chai.repositories.RequestMapRepository
import com.omnitech.chai.repositories.RoleRepository
import com.omnitech.chai.repositories.UserRepository
import com.omnitech.chai.util.ModelFunctions
import fuzzycsv.FuzzyCSV
import fuzzycsv.Record
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.neo4j.support.Neo4jTemplate
import org.springframework.data.neo4j.transaction.Neo4jTransactional

import static com.omnitech.chai.model.Relations.*
import static com.omnitech.chai.util.ChaiUtils.prop
import static fuzzycsv.RecordFx.fn
import static org.neo4j.cypherdsl.CypherQuery.*

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
        ModelFunctions.listAll(neo, User, params, User)
    }

    Page<User> listUsersSupervisedBy(Long supervisorId, String role, Map params = [max: 2000]) {
        def _query = {
            start(nodesById('sup', supervisorId))
                    .match(node('sup').out(SUPERVISES_TERRITORY, USER_TERRITORY)
                    .node('tr').in(USER_TERRITORY)
                    .node('user').out(HAS_ROLE)
                    .node('r').values(value('authority', role)))
        }
        def q = _query().returns(distinct(identifier('user')))
        def cq = _query().returns(count(distinct(identifier('user'))))

        ModelFunctions.query(neo, q, cq, params, User)
    }

    List<User> listUsersByRole(String role) {
        userRepository.listAllByRole(role).collect()
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

    User mapUserToTerritories(Long userId, List territoryIds) {

        territoryIds = territoryIds.collect {it as Long}
        def user = findUser(userId)

        territoryRepository.findAll().each { t ->
            //first delete old references
            t.supervisor.removeAll { it.id == userId }
            if (territoryIds.contains(t.id)) {
                t.supervisor.add(user)
            }
            territoryRepository.save(t)
        }

        return user
    }

    Page<User> searchUsers(String search, Map params) {
        ModelFunctions.searchAll(neo, User, ModelFunctions.getWildCardRegex(search), params)
    }

    Page<Device> searchDevices(String search, Map params) {
        ModelFunctions.searchAll(neo, Device, ModelFunctions.getWildCardRegex(search), params)
    }

    def importUsers(String s) {
        def csv = FuzzyCSV.parseCsv(s)
        csv = csv.collect { it as List<String> }
        FuzzyCSV.map(csv, fn { Record record ->
            try {
                processUserRecord(record)
            } catch (Throwable ex) {
                def e = new ImportException("Error on Record[${record.idx()}]: $ex.message".toString())
                e.stackTrace = ex.stackTrace
                log.error("Error:..... $e.message")
//                throw e
            }
        })
    }

    def processUserRecord(Record record) {
        //username	repName	territory	password	roles	supervisedTerritories	districts	supervisor	supervisorUsername	supervisorPassword


        def username = prop(record, 'username')
        def repName = prop(record, 'repName', false)
        def territoryName = prop(record, 'territory')
        def password = prop(record, 'password')

        def user = findUserByName(username)
        assert !user, "[$username] already exists in the system"

        def territory = territoryRepository.findByName(territoryName)
        assert territory, "Territory [$territoryName] Does not exist in the system"


        def roleNames = prop(record, 'roles')

        List<Role> roles = getRoles(roleNames)

        def newUser = new User(username: username, name: repName, password: neoSecurityService.encodePassword(password), territory: territory, roles: roles as Set)

        saveUser(newUser)

        def supervisorUsername = prop(record, 'supervisorUsername', false)
        if (supervisorUsername) {
            def supPass = prop(record, 'supervisorPassword')
            def supervisorName = prop(record, 'supervisor')
            def supervisorRole = prop(record, 'supervisorRole')
            def supRoles = getRoles(supervisorRole)


            createOrUpdateSuperVisor(supervisorUsername, supervisorName, supPass, supRoles, [territory])
        }
    }

    def createOrUpdateSuperVisor(String supUsername, String supName, String password, List<Role> roles, List<Territory> territories) {

        def sup = findUserByName(supUsername)

        if (!sup) {
            sup = new User(username: supUsername, name: supName, password: neoSecurityService.encodePassword(password), roles: roles as Set, supervisedTerritories: territories, territory: territories[0])

        } else {
            for (t in territories) {
                if (sup.supervisedTerritories.every { it.id != t.id }) {
                    sup.supervisedTerritories.add(t)
                }
            }
        }
        saveUser(sup)
    }

    private List<Role> getRoles(String roleNames) {
        def roles = roleNames.split(',').collect {
            def role = roleRepository.findByAuthority(it.trim())
            assert role, "Role[$it] does not exist in the system"
            return role
        }
        return roles
    }


}
