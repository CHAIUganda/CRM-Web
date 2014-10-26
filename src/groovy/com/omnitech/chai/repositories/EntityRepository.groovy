package com.omnitech.chai.repositories

import com.omnitech.chai.model.*
import org.springframework.data.neo4j.annotation.Query
import org.springframework.data.neo4j.repository.GraphRepository
import org.springframework.data.repository.query.Param

interface ProductRepository extends GraphRepository<Product> {}

interface RoleRepository extends GraphRepository<Role> {}

interface RegionRepository extends GraphRepository<Region> {}

interface SubCountyRepository extends GraphRepository<SubCounty> {}

interface ParishRepository extends GraphRepository<Parish> {}

interface VillageRepository extends GraphRepository<Village> {}

interface CustomerRepository extends GraphRepository<Customer> {}

interface CustomerContactRepository extends GraphRepository<CustomerContact> {}

interface TerritoryRepository extends GraphRepository<Territory> {}

interface TaskRepository extends GraphRepository<Task> {}
interface CustomerSegmentRepository extends GraphRepository<CustomerSegment> {}

interface ProductGroupRepository extends GraphRepository<ProductGroup> {}
interface SettingRepository extends GraphRepository<Setting> {}

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

}

interface RequestMapRepository extends GraphRepository<RequestMap> {
    @Query("match (a:Role) where a.configAttribute = {attrib} return a")
    RequestMap findByConfigAtrribLike(@Param('attrib') String attrib)
}

