package com.omnitech.chai.crm

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.neo4j.support.Neo4jTemplate

import static fuzzycsv.FuzzyCSV.*
import static fuzzycsv.FuzzyCSVTable.tbl

/**
 * Created by kay on 1/15/2015.
 */
class DashBoardService {

    @Autowired
    Neo4jTemplate template

    //to do remove productivity
    List<Map> detailingReport(Date startDate, Date endDate) {
        def query = '''match (r:Role{authority:'ROLE_DETAILER'})<-[:HAS_ROLE]-(u:User)-[:USER_TERRITORY]->(t)<-[:SC_IN_TERRITORY]-(sc)<-[:CUST_IN_SC]-(cm)
optional match cm-[:CUST_TASK]->(ts:DetailerTask)
where (ts.dateCreated >= {startDate})  and (ts.dateCreated <= {endDate})
with u.username as username,t,
  collect(distinct cm) as customers ,
  collect(ts) as allTasks
with username,t.name as territory,
  length(filter(cm in customers where cm.dateCreated >= {startDate} and cm.dateCreated <= {endDate}) ) as numCustomers,
  length(allTasks) as numAll,
  length(filter(tsk in allTasks where (tsk.status = 'complete'))) as numComplete,
  length(filter(tsk in allTasks where (tsk.status = 'cancelled'))) as numCancelled
where numAll <> 0
with username,territory,numAll,numComplete,numCancelled, numCustomers,
  round(tofloat(numComplete + numCancelled)/tofloat(numAll) * 100.0) as covered,
  round(tofloat(numComplete)/tofloat(numAll) * 100.0) as productivity
optional match (u:User{username:username})<-[:ORDER_TAKEN_BY]-(o:Order)
return username,territory,numAll,numComplete,numCancelled,numCustomers,covered,productivity,count(o) as orders
order by covered desc'''

        return template.query(query, [startDate: startDate.time, endDate: endDate.time]).collect()

    }


    List<Map> detailingReport(Long userId, Date startDate, Date endDate) {
        def query = '''start loggedIn = node({userId})
match loggedIn-[:USER_TERRITORY|SUPERVISES_TERRITORY]->(t)
match (t)<-[:USER_TERRITORY]-(u)-[:HAS_ROLE]->(r:Role{authority:'ROLE_DETAILER'})
match (t)<-[:SC_IN_TERRITORY]-(sc)<-[:CUST_IN_SC]-(cm)
optional match cm-[:CUST_TASK]->(ts:DetailerTask)
where (ts.dateCreated >= {startDate})  and (ts.dateCreated <= {endDate})
with u.username as username,t,
  collect(distinct cm) as customers ,
  collect(ts) as allTasks
with username,t.name as territory,
  length(filter(cm in customers where cm.dateCreated >= {startDate} and cm.dateCreated <= {endDate}) ) as numCustomers,
  length(allTasks) as numAll,
  length(filter(tsk in allTasks where (tsk.status = 'complete'))) as numComplete,
  length(filter(tsk in allTasks where (tsk.status = 'cancelled'))) as numCancelled
where numAll <> 0
with username,territory,numAll,numComplete,numCancelled, numCustomers,
  round(tofloat(numComplete + numCancelled)/tofloat(numAll) * 100.0) as covered,
  round(tofloat(numComplete)/tofloat(numAll) * 100.0) as productivity
optional match (u:User{username:username})<-[:ORDER_TAKEN_BY]-(o:Order)
return username,territory,numAll,numComplete,numCancelled,numCustomers,covered,productivity,count(o) as orders
order by covered desc'''

        return template.query(query, [userId: userId, startDate: startDate.time, endDate: endDate.time]).collect()

    }

    List<Map> salesReport(Date startDate, Date endDate) {
        def query = '''match (r:Role{authority:'ROLE_SALES'})<-[:HAS_ROLE]-(u:User)-[:USER_TERRITORY]->(t)<-[:SC_IN_TERRITORY]-(sc)<-[:CUST_IN_SC]-(cm)
optional match(cm)-[:CUST_TASK]->(ts:Order)
where (ts.dateCreated >= {startDate})  and (ts.dateCreated <= {endDate})
with u.username as username,t,
  collect(distinct cm) as customers ,
  collect(ts) as allTasks
with username,t.name as territory,
  length(filter(cm in customers where cm.dateCreated >= {startDate} and cm.dateCreated <= {endDate}) ) as numCustomers,
  length(allTasks) as numAll,
  length(filter(tsk in allTasks where (tsk.status = 'complete'))) as numComplete,
  length(filter(tsk in allTasks where (tsk.status = 'cancelled'))) as numCancelled
where numAll <> 0
with username,territory,numAll,numComplete,numCancelled,numCustomers,
  round(tofloat(numComplete + numCancelled)/tofloat(numAll) * 100.0) as covered,
  round(tofloat(numComplete)/tofloat(numAll) * 100.0) as productivity
optional match (u:User{username:username})<-[:ORDER_TAKEN_BY]-(o:Order)
return username,territory,numAll,numComplete,numCancelled,numCustomers,covered,productivity,count(o) as orders
order by covered desc'''

        return template.query(query, [startDate: startDate.time, endDate: endDate.time]).collect()

    }

    List<Map> salesReport(Long userId, Date startDate, Date endDate) {
        def query = '''start loggedIn = node({userId})
match loggedIn-[:USER_TERRITORY|SUPERVISES_TERRITORY]->(t)
match (t)<-[:USER_TERRITORY]-(u)-[:HAS_ROLE]->(r:Role{authority:'ROLE_SALES'})
match (t)<-[:SC_IN_TERRITORY]-(sc)<-[:CUST_IN_SC]-(cm)
optional match cm-[:CUST_TASK]->(ts:Order)
where (ts.dateCreated >= {startDate})  and (ts.dateCreated <= {endDate})
with u.username as username,t,
  collect(distinct cm) as customers ,
  collect(ts) as allTasks
with username,t.name as territory,
  length(filter(cm in customers where cm.dateCreated >= {startDate} and cm.dateCreated <= {endDate}) ) as numCustomers,
  length(allTasks) as numAll,
  length(filter(tsk in allTasks where (tsk.status = 'complete'))) as numComplete,
  length(filter(tsk in allTasks where (tsk.status = 'cancelled'))) as numCancelled
where numAll <> 0
with username,territory,numAll,numComplete,numCancelled,numCustomers,
  round(tofloat(numComplete + numCancelled)/tofloat(numAll) * 100.0) as covered,
  round(tofloat(numComplete)/tofloat(numAll) * 100.0) as productivity
optional match (u:User{username:username})<-[:ORDER_TAKEN_BY]-(o:Order)
return username,territory,numAll,numComplete,numCancelled,numCustomers,covered,productivity,count(o) as orders
order by covered desc'''

        return template.query(query, [userId: userId, startDate: startDate.time, endDate: endDate.time]).collect()

    }

    List<Map> tabletReport(Long userId, Class taskType) {

        def query = """start u = node({userId})
match u-[:USER_TERRITORY]->tr<-[:SC_IN_TERRITORY]->sc<-[:CUST_IN_SC]-cu-[:CUST_TASK]-(tsk:$taskType.simpleName)
where (tsk.dateCreated >= {startDate})  and (tsk.dateCreated <= {endDate})
with u,count(cu) as customers,count(distinct tsk) as tasks
 optional match u-[:COMPLETED_TASK]->(ct)
where (ct.dateCreated >= {startDate})  and (ct.dateCreated <= {endDate})
 optional match u<-[:ORDER_TAKEN_BY]-(od)
where (od.dateCreated >= {startDate})  and (od.dateCreated <= {endDate})
return  '{period}' as item, customers as Customers,tasks as Tasks,count(distinct ct) as Complete_Tasks,count(distinct od) as Orders"""

        println(query)

        def now = new Date()
        println("$userId ${now.time}  ${(now - 30).time}")
        def weekData = template.query(query.replace('{period}', 'week'), [userId: userId, startDate: (now - 7).time, endDate: now.time, period: 'week']).collect() as List
        def monthData = template.query(query.replace('{period}', 'month'), [userId: userId, startDate: (now - 30).time, endDate: now.time, period: 'month']).collect() as List

        def weekCsv = toCSV(weekData, 'item', 'Customers', 'Tasks', 'Complete_Tasks', 'Orders')
        def monthCsv = toCSV(monthData, 'item', 'Customers', 'Tasks', 'Complete_Tasks', 'Orders')
        def allCsv = tbl(mergeByAppending(weekCsv, monthCsv)).transpose()

        return toMapList(allCsv.csv)

    }

}
