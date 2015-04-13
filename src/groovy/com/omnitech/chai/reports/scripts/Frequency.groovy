//CYPHER 2.0 START u=node(284082) MATCH (u)-[:USER_TERRITORY|SUPERVISES_TERRITORY]->(ut)<-[:SC_IN_TERRITORY]-(subcounty)<-[:CUST_IN_SC]-(customer) MATCH (subcounty)<-[:HAS_SUB_COUNTY]-(district) WHERE (customer.outletName=~"(?i).*(\\Qkabarole\\E).*" or customer.outletType=~"(?i).*(\\Qkabarole\\E).*" or customer.outletSize=~"(?i).*(\\Qkabarole\\E).*" or district.name=~"(?i).*(\\Qkabarole\\E).*") OPTIONAL MATCH (customer)-[:CUST_TASK]->(task) OPTIONAL MATCH (customer)-[:IN_SEGMENT]->(customersegment) RETURN DISTINCT id(customer) AS id,customer.outletName AS outletName,customer.outletType AS outletType,customer.outletSize AS outletSize,customer.dateCreated AS dateCreated,customer.isActive AS isActive,district.name AS district,customersegment.name AS segment,max(task.completionDate) AS lastVisit SKIP 0 LIMIT 50 CYPHER 2.0 START u=node(284082) MATCH (u)-[:USER_TERRITORY|SUPERVISES_TERRITORY]->(ut)<-[:SC_IN_TERRITORY]-(subcounty)<-[:CUST_IN_SC]-(customer) MATCH (subcounty)<-[:HAS_SUB_COUNTY]-(district) WHERE customer.outletName+customer.outletType+customer.outletSize+district.name=~"(?i).*(\\Qkabarole\\E).*" OPTIONAL MATCH (customer)-[:CUST_TASK]->(task) OPTIONAL MATCH (customer)-[:IN_SEGMENT]->(customersegment) RETURN DISTINCT id(customer) AS id,customer.outletName AS outletName,customer.outletType AS outletType,customer.outletSize AS outletSize,customer.dateCreated AS dateCreated,customer.isActive AS isActive,district.name AS district,customersegment.name AS segment,max(task.completionDate) AS lastVisit SKIP 0 LIMIT 50 MATCH (customer:Customer)-[:CUST_IN_SC]->(subcounty)<-[:HAS_SUB_COUNTY]-(district) OPTIONAL MATCH (customer)-[:CUST_TASK]->(task) OPTIONAL MATCH (customer)-[:IN_SEGMENT]->(customersegment) RETURN DISTINCT id(customer) AS id,customer.outletName AS outletName, customer.outletType AS outletType,customer.outletSize AS outletSize ,customer.dateCreated AS dateCreated, customer.isActive AS isActive, district.name AS district, customersegment.name AS segment, max(task.completionDate) AS lastVisit SKIP 0 LIMIT 50
package com.omnitech.chai.reports.scripts

import com.omnitech.chai.crm.CustomerService
import com.omnitech.chai.crm.NeoSecurityService
import com.omnitech.chai.crm.RegionService
import com.omnitech.chai.util.ServletUtil
import csvgraphs.CSVGraph
import filterreport.Filter
import net.sf.dynamicreports.report.constant.PageType
import org.springframework.util.Assert

import static com.omnitech.chai.util.ChaiUtils.bean
import static fuzzycsv.FuzzyCSV.toCSV
import static fuzzycsv.FuzzyCSVTable.tbl
import static fuzzycsv.RecordFx.fn

def currentUser = bean(NeoSecurityService).currentUser
def districts = bean(RegionService).findAllDistrictsForUser(currentUser.id)
def segments = bean(CustomerService).listAllCustomerSegments()

if (action == 'p_get_filter') {
    return [
            new Filter([fieldName: 'district', fieldDescription: 'District', fieldType: "selectMany", possibleValues: districts]),
            new Filter([fieldName: 'segment', fieldDescription: 'Segments', fieldType: "selectMany", possibleValues: segments]),
    ]
}

//SEGMENTS
def selectedDistrictNames = ServletUtil.extractList(params, 'district', "Please Select Districts")
def selectedDistrictIds = districts.findAll { selectedDistrictNames.contains(it.name) }.collect { it.id }
Assert.notEmpty selectedDistrictIds, "Please Select Districts"

//SEGMENTS
def selectedSegmentNames = ServletUtil.extractList(params, 'segment', "Please Select a Segment")
def segmentIds = segments.findAll { selectedSegmentNames.contains(it.name) }.collect { it.id }
Assert.notEmpty segmentIds, "Please Select Some Segments"



def query = """MATCH (district:District) WHERE any( x IN $selectedDistrictIds WHERE x = id(district))
 MATCH    district-[:HAS_SUB_COUNTY]->subcounty<-[:CUST_IN_SC]-customer-[:IN_SEGMENT]->segment
    WHERE any( x IN $segmentIds WHERE x = id(segment))
 OPTIONAL MATCH customer-[:CUST_TASK]->(task{status:"complete"})
 WITH
     district,
     subcounty,
     customer,
     segment,
     task
     ORDER BY task.completionDate
WITH
    district,
    subcounty,
    segment,
    customer,
    collect(task) as tasks
WITH
    district,
    subcounty,
    customer,
    segment,
    tasks[0] as lastVisit,
    tasks[1] as secondlastvisit,
    tasks[2] as thirdlastvisit
RETURN
    customer.outletName             as outletName,
    district.name                     as district,
    subcounty.name                     as subcounty,
    customer.outletType             as outletType,
    segment.name                     as segment,
    lastVisit.completionDate         as lastVisit,
    secondlastvisit.completionDate     as secondlastvisit,
    thirdlastvisit.completionDate     as thirdlastvisit
"""


println query
def results = neo.query(query, Collections.EMPTY_MAP)


List<Map> mapList = results.collect()

def csv = tbl(toCSV(mapList, 'outletName', 'district', 'subcounty', 'outletType', 'lastVisit', 'secondlastvisit', 'thirdlastvisit'))

csv = csv.transform('lastVisit', fn { if (it.lastVisit) new Date(it.lastVisit) })
        .transform('secondlastvisit', fn { if (it.secondlastvisit) new Date(it.secondlastvisit) })
        .transform('thirdlastvisit', fn { if (it.thirdlastvisit) new Date(it.thirdlastvisit) })


CSVGraph g = new CSVGraph('CHAI', 'Frequency', 'http://23.239.27.196:8080/web-crm/', 'image', csv.csv)
        .setShowColumnLines(true)
        .setShowChart(false)

g.labelMap = [
        outletName     : 'Outlet Name',
        outletType     : 'Outlet Type',
        district       : 'District',
        subcounty      : 'Sub County',
        lastVisit      : 'Last Visit',
        secondlastvisit: 'Second Last Visit',
        thirdlastvisit : 'Third Last Visit',
]


g.report.setPageFormat(PageType.A3)


