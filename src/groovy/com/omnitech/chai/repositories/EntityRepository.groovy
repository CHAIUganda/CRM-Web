package com.omnitech.chai.repositories

import com.omnitech.chai.model.*
import com.omnitech.chai.repositories.impl.ICustomerRepository
import com.omnitech.chai.repositories.impl.ITaskRepository
import groovy.transform.CompileStatic
import org.springframework.data.neo4j.annotation.Query
import org.springframework.data.neo4j.annotation.QueryResult
import org.springframework.data.neo4j.annotation.ResultColumn
import org.springframework.data.neo4j.repository.CypherDslRepository
import org.springframework.data.neo4j.repository.GraphRepository
import org.springframework.data.repository.query.Param

interface UuidRepository<T> {


}

interface ProductRepository extends GraphRepository<Product> {
    Product findByUuid(String uuid)

    @Query('start u = node({userId}) match u-[:USER_TERRITORY|SUPERVISES_TERRITORY]->(t)<-[:PROD_IN_TERRITORY]-(p) return distinct p')
    Iterable<Product> findAllByUser(@Param('userId') Long userId)
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

    @Query('start u = node({userId}) match u-[:USER_TERRITORY]->(t)<-[:SC_IN_TERRITORY]-(sc)<-[:CUST_IN_SC]-(c)-[:CUST_IN_PARISH]->(p) return distinct p')
    Iterable<Parish> findAllForUser(@Param('userId') Long userId)
}

interface VillageRepository extends GraphRepository<Village> {

    Village findByUuid(String uuid)

    Village findByName(String name)

    @Query('start u = node({userId}) match u-[:USER_TERRITORY]->(t)<-[:SC_IN_TERRITORY]-(sc)<-[:CUST_IN_SC]-(c)-[:CUST_IN_VILLAGE]->(p) return distinct p')
    Iterable<Village> findAllForUser(@Param('userId') Long userId)

}

interface CustomerRepository extends GraphRepository<Customer>, CypherDslRepository<Customer> , ICustomerRepository{

    Customer findByUuid(String uuid)

    @Query('start t=node({territoryId}) MATCH (t)<-[:`SC_IN_TERRITORY`]-(sc)<-[:CUST_IN_SC]-(c) RETURN c')
    Iterable<Customer> findByTerritory(@Param('territoryId') Long territoryId)

    @Query('MATCH n-[rel:IN_SEGMENT]->r WHERE id(n)={node_id} DELETE rel')
    void deleteSegment(@Param('node_id') Long node_id)

    @Query('MATCH (a:`Customer`),(b:`CustomerSegment`) WHERE id(a) = {node_id} AND id(b) = {segment_id} CREATE (a)-[r:IN_SEGMENT]->(b)')
    void addSegment(@Param('node_id') Long node_id, @Param('segment_id') Long segment_id)

    @Query('match n where id(n) = {node_id} set n.segmentScore = {segmentScore}')
    void updateWithQuery(@Param('node_id') Long node_id, @Param('segmentScore') Double segmentScore)

    @Query("""start t = node({territoryId}), cs = node({segmentId})
match (cs)<-[:IN_SEGMENT]-(customer)-[:CUST_IN_SC]->(sc)-[:SC_IN_TERRITORY]->(t)
where not(customer-[:CUST_TASK]->(:DetailerTask{status:'new'}))
with customer optional match customer-[:CUST_TASK]-(o:DetailerTask)
return customer,max(o.completionDate) as completionDate
order by completionDate desc
limit {limit}""")
    Iterable<CustomerWithLastTaskDate> findAllWithoutNewDetailingTasks(
            @Param('territoryId') Long territoryId,
            @Param('segmentId') Long segmentId,
            @Param('limit') Integer limit)

    @Query("""start t = node({territoryId}), cs = node({segmentId})
match (cs)<-[:IN_SEGMENT]-(customer)-[:CUST_IN_SC]->(sc)-[:SC_IN_TERRITORY]->(t)
where not(customer-[:CUST_TASK]->(:Order{status:'new'}))
with customer optional match customer-[:CUST_TASK]-(o:Order)
return customer,max(o.completionDate) as completionDate
order by completionDate desc
limit {limit}""")
    Iterable<CustomerWithLastTaskDate> findAllWithoutNewOrderTasks(
            @Param('territoryId') Long territoryId,
            @Param('segmentId') Long segmentId,
            @Param('limit') Integer limit)

    @Query("""start t = node({territoryId}), cs = node({segmentId})
match (cs)<-[:IN_SEGMENT]-(customer)-[:CUST_IN_SC]->(sc)-[:SC_IN_TERRITORY]->(t)
where not(customer-[:CUST_TASK]->(:SalesCall{status:'new'}))
with customer optional match customer-[:CUST_TASK]-(o:SalesCall)
return customer,max(o.completionDate) as completionDate
order by completionDate desc
limit {limit}""")
    Iterable<CustomerWithLastTaskDate> findAllWithoutSalesCalls(
            @Param('territoryId') Long territoryId,
            @Param('segmentId') Long segmentId,
            @Param('limit') Integer limit)

    @Query('start c = node({customerId}) match (c)-[:CUST_TASK]->(s:Sale)-[li:HAS_PRODUCT]-() with s, sum(li.quantity * li.unitPrice) as saleValue return avg(saleValue)')
    Double averageSalesValue(@Param('customerId')Long customerId)

    @Query('''start t = node({territoryId})
match (t)<-[:SC_IN_TERRITORY]-(sc)<-[:CUST_IN_SC]-(c)
optional match c-[:CUST_TASK]->(o:DetailerTask)
with c ,max(o.completionDate) as completionDate
optional match c-[:CUST_TASK]->(o:DetailerTask{completionDate: completionDate})
return c as customer, o as task''')
    Iterable<CustomerWithLastTask> findAllCustomersAlongWithLastTask(@Param('territoryId')Long territoryId)


}

@QueryResult
@CompileStatic
class CustomerWithLastTaskDate {

    @ResultColumn('customer')
    Customer customer
    @ResultColumn('completionDate')
    Date completionDate

}

@QueryResult
@CompileStatic
class CustomerWithLastTask {
    @ResultColumn('customer')
    Customer customer
    @ResultColumn('task')
    DetailerTask task
}


interface CustomerContactRepository extends GraphRepository<CustomerContact> {
    CustomerContact findByUuid(String uuid)
}

interface TerritoryRepository extends GraphRepository<Territory> {
    Territory findByUuid(String uuid)

    Territory findByName(String name)

    @Query('match (t:Territory{type:{type}}) return t')
    Iterable<Territory> findAllByType(@Param('type') String type)
}

interface TaskRepository extends GraphRepository<Task>, CypherDslRepository<Task>, ITaskRepository {

    Task findByUuid(String uuid)

    Task findByClientRefId(String clientRefId)

    @Query('start c = node({customerId}) match c -[:CUST_TASK]-> (t) return t order by t.dateCreated desc limit 1')
    Task findLastTask(@Param('customerId') Long customerId)

    @Query('start t=node({territoryId}) MATCH (t)<-[:`SC_IN_TERRITORY`]-(sc)<-[:CUST_IN_SC]-(c)-[:CUST_TASK]->(ts) RETURN ts')
    Iterable<Task> findAllTasksInTerritory(@Param('territoryId') Long territoryId)

    @Query("start c = node({customerId}) match c-[:CUST_TASK]-(t:{taskType}{status:'new'}) delete t")
    void deleteOldSalesCall(@Param('customerId') Long customerId, @Param('taskType') String type)
}

interface CustomerSegmentRepository extends GraphRepository<CustomerSegment> {

    CustomerSegment findByUuid(String uuid)
}

interface ProductGroupRepository extends GraphRepository<ProductGroup> {

    ProductGroup findByUuid(String uuid)
}

interface DetailerTaskRepository extends GraphRepository<DetailerTask> {
    DetailerTask findByUuid(String uuid)

    @Query('start t=node({territoryId}) MATCH (t)<-[:`SC_IN_TERRITORY`]-(sc)<-[:CUST_IN_SC]-(c)-[:CUST_TASK]->(ts:DetailerTask) RETURN ts')
    Iterable<DetailerTask> findAllInTerritory(@Param('territoryId') Long territoryId)
}

interface OrderRepository extends GraphRepository<Order> {
    @Query('match (o:Order) where o.uuid = {uuid} return o')
    Order findByUuidImpl(@Param('uuid') String uuid)

    Order findByClientRefId(String clientRefId)

    @Query('start t=node({territoryId}) MATCH (t)<-[:`SC_IN_TERRITORY`]-(sc)<-[:CUST_IN_SC]-(c)-[:CUST_TASK]->(ts:Order) RETURN ts')
    Iterable<Order> findAllInTerritory(@Param('territoryId') Long territoryId)
}

interface SalesCallRepository extends GraphRepository<SalesCall> {
    @Query('match (o:SalesCall) where o.uuid = {uuid} return o')
    SalesCall findByUuidImpl(@Param('uuid') String uuid)

    @Query('start t=node({territoryId}) MATCH (t)<-[:`SC_IN_TERRITORY`]-(sc)<-[:CUST_IN_SC]-(c)-[:CUST_TASK]->(ts:SalesCall) RETURN ts')
    Iterable<SalesCall> findAllInTerritory(@Param('territoryId') Long territoryId)
}

interface DirectSaleRepository extends GraphRepository<DirectSale> {
    @Query('match (o:DirectSale) where o.uuid = {uuid} return o')
    DirectSale findByUuidImpl(@Param('uuid') String uuid)

    DirectSale findByClientRefId(String clientRefId)
}

interface SettingRepository extends GraphRepository<Setting> {
    Setting findByUuid(String uuid)

    Setting findByName(String name)
}

interface UserRepository extends GraphRepository<User> {

    User findByUuid(String uuid)

    User findByUsername(String username)

    @Query('match (u:User)-[:HAS_ROLE]-> (r {authority: {role}}) return u')
    Iterable<User> listAllByRole(@Param('role') String role)
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

    District findByName(String name)

    @Query('start u = node({userId}) match u-[:USER_TERRITORY|SUPERVISES_TERRITORY]->(t)<-[:SC_IN_TERRITORY]-(sc)<-[:HAS_SUB_COUNTY]-(d) return distinct d')
    Iterable<District> findAllForUser(@Param('userId') Long userId)

}

interface RequestMapRepository extends GraphRepository<RequestMap> {
    @Query("match (a:Role) where a.configAttribute = {attrib} return a")
    RequestMap findByConfigAtrribLike(@Param('attrib') String attrib)

    RequestMap findByUrl(String url)
}

interface WholeSalerRepository extends GraphRepository<WholeSaler> {
    District findByUuid(String uuid)
}

interface ReportRepository extends GraphRepository<Report> {
    Report findByUuid(String uuid)
}

interface ReportGroupRepository extends GraphRepository<ReportGroup> {
    ReportGroup findByUuid(String uuid)
}

interface DbChangeSetRepository extends GraphRepository<DbChangeSet> {
    DbChangeSet findByUuid(String uuid)
    DbChangeSet findByChangeId(String uuid)
}

interface DetailerStockRepository extends GraphRepository<DetailerStock> {
    //warning please change the limit in future to something you see fit. 8 was set to improve performance
    @Query("match (d:DetailerStock) with distinct d.category as category , d.brand as brand return category,brand order by category,brand")
    Iterable<CategoryBrandResult> findAllCategoriesAndBrands()

    @Query("match (d:DetailerStock) return distinct d.category as category order by category")
    Iterable<CategoryResult> findAllCategories()
}

interface MalariaStockRepository extends GraphRepository<DetailerMalariaStock> {
    @Query("match (d:DetailerMalariaStock) return distinct d.category as category , d.brand as brand order by category, brand")
    Iterable<CategoryBrandResult> findAllCategoriesAndBrands()

    @Query("match (d:DetailerMalariaStock) return distinct d.category as category order by category")
    Iterable<CategoryResult> findAllCategories()
}

@CompileStatic
@QueryResult
class CategoryBrandResult {
    @ResultColumn('category')
    String category
    @ResultColumn('brand')
    String brand
}

@CompileStatic
@QueryResult
class CategoryResult {
    @ResultColumn('category')
    String category
}

