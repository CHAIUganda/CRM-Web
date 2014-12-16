package com.omnitech.chai.repositories

import com.omnitech.chai.model.*
import org.springframework.data.neo4j.annotation.Query
import org.springframework.data.neo4j.repository.CypherDslRepository
import org.springframework.data.neo4j.repository.GraphRepository
import org.springframework.data.repository.query.Param

interface UuidRepository<T> {


}

interface ProductRepository extends GraphRepository<Product> {
    Product findByUuid(String uuid)
}

interface RoleRepository extends GraphRepository<Role> {
    Role findByUuid(String uuid)

    Role findByAuthority(String name)
}

interface RegionRepository extends GraphRepository<Region> {

    Region findByUuid(String uuid)

    Region findByName(String name)

    @Query('start u = node({userId}) match u-[:USER_TERRITORY]->(t)<-[:SC_IN_TERRITORY]-(sc)<-[:HAS_SUB_COUNTY]-(d)<-[:HAS_DISTRICT]-(r) return distinct r')
    Iterable<Region> findAllRegionsForUser(@Param('userId') Long userId)

}

interface SubCountyRepository extends GraphRepository<SubCounty> {

    SubCounty findByUuid(String uuid)

    @Query('start r=node({districtId}) match r-[:HAS_SUB_COUNTY]->(s) where s.name =~ {name} return s')
    SubCounty findByDistrictAndName(@Param('districtId') Long districtId, @Param('name') String name)

    @Query('start u = node({userId}) match u-[:USER_TERRITORY]->(t)<-[:SC_IN_TERRITORY]-(sc) return distinct sc')
    Iterable<SubCounty> findAllForUser(@Param('userId') Long userId)
}

interface ParishRepository extends GraphRepository<Parish> {

    Parish findByUuid(String uuid)

    Parish findByName(String uuid)

    @Query('start r=node({subCountyId}) match r-[:HAS_PARISH]->(s) where s.name =~ {name} return s')
    Parish findBySubCountyAndName(@Param('subCountyId') Long subCountyId, @Param('name') String name)

    @Query('start u = node({userId}) match u-[:USER_TERRITORY]->(t)<-[:SC_IN_TERRITORY]-(sc)<-[:CUST_IN_SC]-(c)-[:CUST_IN_PARISH]->(p) return distinct p')
    Iterable<Parish> findAllForUser(@Param('userId') Long userId)
}

interface VillageRepository extends GraphRepository<Village> {

    Village findByUuid(String uuid)

    Village findByName(String name)

    @Query('start r=node({parishId}) match r-[:HAS_VILLAGE]->(v) where v.name =~ {name} return v')
    Village findByParishAndName(@Param('parishId') Long parishId, @Param('name') String name)

    @Query('start u = node({userId}) match u-[:USER_TERRITORY]->(t)<-[:SC_IN_TERRITORY]-(sc)<-[:CUST_IN_SC]-(c)-[:CUST_IN_VILLAGE]->(p) return distinct p')
    Iterable<Village> findAllForUser(@Param('userId') Long userId)

}

interface CustomerRepository extends GraphRepository<Customer>, CypherDslRepository<Customer> {

    Customer findByUuid(String uuid)

}

interface CustomerContactRepository extends GraphRepository<CustomerContact> {
    CustomerContact findByUuid(String uuid)
}

interface TerritoryRepository extends GraphRepository<Territory> {
    Territory findByUuid(String uuid)
}

interface TaskRepository extends GraphRepository<Task>, CypherDslRepository<Task> {

    Task findByUuid(String uuid)

    @Query('start c = node({customerId}) match c -[:CUST_TASK]-> (t) return t order by t.dateCreated desc limit 1')
    Task findLastTask(@Param('customerId') Long customerId)

}

interface CustomerSegmentRepository extends GraphRepository<CustomerSegment> {

    CustomerSegment findByUuid(String uuid)
}

interface ProductGroupRepository extends GraphRepository<ProductGroup> {

    ProductGroup findByUuid(String uuid)
}

interface DetailerTaskRepository extends GraphRepository<DetailerTask> {
    DetailerTask findByUuid(String uuid)
}

interface OrderRepository extends GraphRepository<Order> {
    Order findByUuid(String uuid)
}

interface SettingRepository extends GraphRepository<Setting> {
    Setting findByUuid(String uuid)

    Setting findByName(String name)
}

interface UserRepository extends GraphRepository<User> {

    User findByUuid(String uuid)

    User findByUsername(String username)
}

interface DeviceRepository extends GraphRepository<Device> {

    Device findByUuid(String uuid)

    @Query('start n = node({userId}) Match n-[:HAS_DEVICE]->d return d AS model UNION MATCH (d:Device) WHERE NOT (()-[:HAS_DEVICE ]->(d)) RETURN d AS model')
    Iterable<Device> findAllFreeDevices(@Param('userId') Long userId)

    @Query('MATCH (d:Device) WHERE NOT (()-[:HAS_DEVICE ]->(d)) RETURN d AS model')
    Iterable<Device> findAllFreeDevices()
}

interface DistrictRepository extends GraphRepository<District> {

    District findByUuid(String uuid)

    @Query('match (d:District)-[:HAS_SUB_COUNTY]->(s) return distinct d')
    Set<District> listAllDistrictsWithSubCounties()

    @Query('start r=node({regionId}) match r-[:HAS_DISTRICT]->(d) where d.name =~ {name} return d')
    District findByRegionAndName(@Param('regionId') Long regionId, @Param('name') String name)

    @Query('start u = node({userId}) match u-[:USER_TERRITORY]->(t)<-[:SC_IN_TERRITORY]-(sc)<-[:HAS_SUB_COUNTY]-(d) return distinct d')
    Iterable<District> findAllForUser(@Param('userId') Long userId)

}

interface RequestMapRepository extends GraphRepository<RequestMap> {
    @Query("match (a:Role) where a.configAttribute = {attrib} return a")
    RequestMap findByConfigAtrribLike(@Param('attrib') String attrib)
}

