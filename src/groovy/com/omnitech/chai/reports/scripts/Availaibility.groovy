package com.omnitech.chai.reports.scripts

import com.omnitech.chai.crm.NeoSecurityService
import com.omnitech.chai.crm.RegionService
import com.omnitech.chai.reports.ReportContext
import csvgraphs.CSVGraph
import filterreport.Filter
import org.springframework.data.neo4j.support.Neo4jTemplate
import org.springframework.util.Assert

import static com.omnitech.chai.util.ChaiUtils.bean
import static fuzzycsv.FuzzyCSV.toCSV
import static fuzzycsv.FuzzyCSVTable.tbl

def neo = neo as Neo4jTemplate
ReportContext rc = reportContext
def sec = bean(NeoSecurityService)
def currentUser = sec.currentUser

def districts = bean(RegionService).findAllDistrictsForUser(currentUser.id)

if (action == 'p_get_filter') {
    return [
            new Filter([fieldName: 'district', fieldDescription: 'District', fieldType: "selectMany", possibleValues: districts]),
            new Filter([fieldName: 'startDate', fieldDescription: 'Start Date', fieldType: Date]),
            new Filter([fieldName: 'endDate', fieldDescription: 'EndDate Date', fieldType: Date])
    ]
}


Assert.notNull params.district, "Please Select Districts"

def selectedDistrictNames = params.district instanceof String ? [params.district] : params.district
def selectedDistrictIds = districts.findAll { selectedDistrictNames.contains(it.name) }.collect { it.id }

def datePattern = 'yyyy-MM-dd'
Assert.notNull params.startDate, 'Please Select a Start Date'
def startDate = Date.parse(datePattern, params.startDate)

Assert.notNull params.endDate, 'Please Select an End Date'
def endDate = Date.parse(datePattern, params.endDate)


Assert.notEmpty selectedDistrictIds, "Please Select Districts"
Assert.isTrue startDate < endDate, "Please The Start Date[$startDate] Should Be Before End Date[$endDate]"

def query = """
MATCH (district:District) WHERE any( x IN $selectedDistrictIds WHERE x = id(district))
MATCH district-[:HAS_SUB_COUNTY]->subcounty-[:SC_IN_TERRITORY]->territory<-[:PROD_IN_TERRITORY]-product
MATCH subcounty<-[:CUST_IN_SC]-customer
OPTIONAL MATCH customer-[:CUST_TASK]->(stock:StockInfo)-[stockline:STOCK_PRODUCT]-product
    WHERE stock.completionDate >= $startDate.time and stock.completionDate <= $endDate.time
WITH
    district,
    subcounty,
    product,
    count(DISTINCT customer) as numAllCustomers,
    count(DISTINCT CASE WHEN stockline.quantity > 0.0 THEN customer ELSE null END) as numCustomersWithProduct
RETURN
    DISTINCT
    district.name as district,
    subcounty.name as subcounty,
    product.name as product,
    (toFloat(numCustomersWithProduct)/toFloat(numAllCustomers)) * 100.0 as availability
    order by district asc, subcounty asc, product asc"""



println(query)
def results = neo.query(query, Collections.EMPTY_MAP)


List<Map> mapList = results.collect()

def csv = tbl(toCSV(mapList, 'district', 'subcounty', 'product', 'availability'))


def g = new CSVGraph('CHAI', 'Availability', 'htt://23.239.27.196:8080/web-crm/', '/image', csv.csv)
        .setShowChart(false)

g.labelMap = [
        district    : 'District',
        subcounty   : 'SubCounty',
        product     : 'Product',
        availability: 'Availability',
]

g.report
