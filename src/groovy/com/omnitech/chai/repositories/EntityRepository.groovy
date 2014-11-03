package com.omnitech.chai.repositories

import com.omnitech.chai.model.*
import org.springframework.data.neo4j.annotation.Query
import org.springframework.data.neo4j.repository.CypherDslRepository
import org.springframework.data.neo4j.repository.GraphRepository
import org.springframework.data.repository.query.Param

interface ProductRepository extends GraphRepository<Product> {}

interface RoleRepository extends GraphRepository<Role> {}

interface RegionRepository extends GraphRepository<Region> {

    Region findByName(String name)

    @Query('start u = node({userId}) match u-[:USER_TERRITORY]->(t)<-[:SC_IN_TERRITORY]-(sc)<-[:HAS_SUB_COUNTY]-(d)<-[:HAS_DISTRICT]-(r) return distinct r')
    Iterable<Region> findAllRegionsForUser(@Param('userId') Long userId)

}

interface SubCountyRepository extends GraphRepository<SubCounty> {

    @Query('start r=node({districtId}) match r-[:HAS_SUB_COUNTY]->(s) where s.name =~ {name} return s')
    SubCounty findByDistrictAndName(@Param('districtId') Long districtId, @Param('name') String name)
}

interface ParishRepository extends GraphRepository<Parish> {

    @Query('start r=node({subCountyId}) match r-[:HAS_PARISH]->(s) where s.name =~ {name} return s')
    Parish findBySubCountyAndName(@Param('subCountyId') Long subCountyId, @Param('name') String name)
}

interface VillageRepository extends GraphRepository<Village> {

    @Query('start r=node({parishId}) match r-[:HAS_VILLAGE]->(v) where v.name =~ {name} return v')
    Village findByParishAndName(@Param('parishId') Long parishId, @Param('name') String name)

}

interface CustomerRepository extends GraphRepository<Customer>, CypherDslRepository<Customer> {}

interface CustomerContactRepository extends GraphRepository<CustomerContact> {}

interface TerritoryRepository extends GraphRepository<Territory> {}

interface TaskRepository extends GraphRepository<Task> {

    @Query('start c = node({customerId}) match c -[:CUST_TASK]-> (t) return t order by t.dateCreated desc limit 1')
    Task findLastTask(@Param('customerId') Long customerId)

    @Query('START u=node({userId}) MATCH (u)-[:USER_TERRITORY]->(t)<-[:SC_IN_TERRITORY]-(sc)<-[:BELONGS_TO_SC]-(customer)-[:CUST_TASK]-(tsk) WHERE u-[:ASSIGNED_TASK]->(tsk) or NOT(tsk<-[:ASSIGNED_TASK]-()) RETURN distinct tsk')
    Iterable<Task> findAllTaskForUser(@Param("userId") Long userId)

}

interface CustomerSegmentRepository extends GraphRepository<CustomerSegment> {}

interface ProductGroupRepository extends GraphRepository<ProductGroup> {}

interface SettingRepository extends GraphRepository<Setting> {
    Setting findByName(String name)
}

interface UserRepository extends GraphRepository<User> {

    User findByUsername(String username)
}

interface DeviceRepository extends GraphRepository<Device> {

    @Query('start n = node({userId}) Match n-[:HAS_DEVICE]->d return d AS model UNION MATCH (d:Device) WHERE NOT (()-[:HAS_DEVICE ]->(d)) RETURN d AS model')
    Iterable<Device> findAllFreeDevices(@Param('userId') Long userId)

    @Query('MATCH (d:Device) WHERE NOT (()-[:HAS_DEVICE ]->(d)) RETURN d AS model')
    Iterable<Device> findAllFreeDevices()
}

interface DistrictRepository extends GraphRepository<District> {

    @Query('match (d:District)-[:HAS_SUB_COUNTY]->(s) return distinct d')
    Set<District> listAllDistrictsWithSubCounties()

    @Query('start r=node({regionId}) match r-[:HAS_DISTRICT]->(d) where d.name =~ {name} return d')
    District findByRegionAndName(@Param('regionId') Long regionId, @Param('name') String name)

    @Query('start u = node(1) match u-[:USER_TERRITORY]->(t)<-[:SC_IN_TERRITORY]-(sc)<-[:HAS_SUB_COUNTY]-(d) return distinct d')
    Iterable<District> findAllForUser(@Param('userId')Long userId)

}

interface RequestMapRepository extends GraphRepository<RequestMap> {
    @Query("match (a:Role) where a.configAttribute = {attrib} return a")
    RequestMap findByConfigAtrribLike(@Param('attrib') String attrib)
}

