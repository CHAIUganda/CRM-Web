package com.omnitech.chai.crm

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.neo4j.support.Neo4jTemplate

/**
 * Created by kay on 1/15/2015.
 */
class DashBoardService {

    @Autowired
    Neo4jTemplate template

    List<Map> detailingReport() {
        def query = '''match (r:Role{authority:'ROLE_DETAILER'})<-[:HAS_ROLE]-(u:User)-[:USER_TERRITORY]->(t)<-[:SC_IN_TERRITORY]-(sc)<-[:CUST_IN_SC]-(cm)-[:CUST_TASK]->(ts:DetailerTask)
with u.username as username,t,
  collect(ts) as allTasks
with username,t, allTasks,
  filter(tsk in allTasks where (tsk.status = 'complete')) as doneTasks,
  filter(tsk in allTasks where (tsk.status = 'cancelled')) as cancelledTasks
with username,t,
  length(allTasks) as numAll,
  length(doneTasks) as numComplete,
  length(cancelledTasks)  as numCancelled
with username,t.name as territory, numAll, numComplete , numCancelled
where numAll <> 0
return username,territory,numAll,numComplete,numCancelled,
  round(tofloat(numComplete + numCancelled)/tofloat(numAll) * 100.0) as covered,
  round(tofloat(numComplete)/tofloat(numAll) * 100.0) as productivity
order by covered desc'''

        return template.query(query, Collections.EMPTY_MAP).collect()

    }


    List<Map> detailingReport(Long userId) {
        def query = '''start loggedIn = node({userId})
match loggedIn-[:USER_TERRITORY|SUPERVISES_TERRITORY]->
(t)<-[:USER_TERRITORY]-(u)-[:HAS_ROLE]->(r:Role{authority:'ROLE_DETAILER'})
match (t)<-[:SC_IN_TERRITORY]-(sc)<-[:CUST_IN_SC]-(cm)-[:CUST_TASK]->(ts:DetailerTask)
with u.username as username,t,
  collect(ts) as allTasks
with username,t, allTasks,
  filter(tsk in allTasks where (tsk.status = 'complete')) as doneTasks,
  filter(tsk in allTasks where (tsk.status = 'cancelled')) as cancelledTasks
with username,t,
  length(allTasks) as numAll,
  length(doneTasks) as numComplete,
  length(cancelledTasks)  as numCancelled
with username,t.name as territory, numAll, numComplete , numCancelled
where numAll <> 0
return username,territory,numAll,numComplete,numCancelled,
  round(tofloat(numComplete + numCancelled)/tofloat(numAll) * 100.0) as covered,
  round(tofloat(numComplete)/tofloat(numAll) * 100.0) as productivity
order by covered desc'''

        return template.query(query, [userId: userId]).collect()

    }

    List<Map> salesReport() {
        def query = '''match (r:Role{authority:'ROLE_SALES'})<-[:HAS_ROLE]-(u:User)-[:USER_TERRITORY]->(t)<-[:SC_IN_TERRITORY]-(sc)<-[:CUST_IN_SC]-(cm)-[:CUST_TASK]->(ts:Order)
with u.username as username,t,
  collect(ts) as allTasks
with username,t, allTasks,
  filter(tsk in allTasks where (tsk.status = 'complete')) as doneTasks,
  filter(tsk in allTasks where (tsk.status = 'cancelled')) as cancelledTasks
with username,t,
  length(allTasks) as numAll,
  length(doneTasks) as numComplete,
  length(cancelledTasks)  as numCancelled
with username,t.name as territory, numAll, numComplete , numCancelled
where numAll <> 0
return username,territory,numAll,numComplete,numCancelled,
  round(tofloat(numComplete + numCancelled)/tofloat(numAll) * 100.0) as covered,
  round(tofloat(numComplete)/tofloat(numAll) * 100.0) as productivity
order by covered desc'''

        return template.query(query, Collections.EMPTY_MAP).collect()

    }

    List<Map> salesReport(Long userId) {
        def query = '''start loggedIn = node({userId})
match loggedIn-[:USER_TERRITORY|SUPERVISES_TERRITORY]->
(t)<-[:USER_TERRITORY]-(u)-[:HAS_ROLE]->(r:Role{authority:'ROLE_SALES'})
match (t)<-[:SC_IN_TERRITORY]-(sc)<-[:CUST_IN_SC]-(cm)-[:CUST_TASK]->(ts:Order)
with u.username as username,t,
  collect(ts) as allTasks
with username,t, allTasks,
  filter(tsk in allTasks where (tsk.status = 'complete')) as doneTasks,
  filter(tsk in allTasks where (tsk.status = 'cancelled')) as cancelledTasks
with username,t,
  length(allTasks) as numAll,
  length(doneTasks) as numComplete,
  length(cancelledTasks)  as numCancelled
with username,t.name as territory, numAll, numComplete , numCancelled
where numAll <> 0
return username,territory,numAll,numComplete,numCancelled,
  round(tofloat(numComplete + numCancelled)/tofloat(numAll) * 100.0) as covered,
  round(tofloat(numComplete)/tofloat(numAll) * 100.0) as productivity
order by covered desc'''

        return template.query(query, [userId: userId]).collect()

    }

}
