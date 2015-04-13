package com.omnitech.chai.reports.scripts

import com.omnitech.chai.crm.NeoSecurityService
import com.omnitech.chai.crm.RegionService
import com.omnitech.chai.util.ChaiUtils
import csvgraphs.CSVGraph
import filterreport.Filter
import fuzzycsv.FuzzyCSVTable
import fuzzycsv.RecordFx
import net.sf.dynamicreports.report.constant.PageOrientation
import net.sf.dynamicreports.report.constant.PageType
import org.springframework.util.Assert

import static com.omnitech.chai.util.ChaiUtils.bean
import static fuzzycsv.FuzzyCSV.toCSV
import static fuzzycsv.FuzzyCSVTable.tbl
import static java.util.Calendar.MONDAY

def currentUser = bean(NeoSecurityService).currentUser
def territories = bean(RegionService).findTerritoriesForUser(currentUser, [max: 2000])

if (action == 'p_get_filter') {
    return [
            new Filter([fieldName: 'territory', fieldDescription: 'Select A Territory', fieldType: List, possibleValues: territories.content]),
            new Filter([fieldName: 'week', fieldDescription: 'Select Any Date In A Week', fieldType: Date]),
    ]
}

//Extract Dates
Assert.notNull params.week, "Please Select A Date"
//def today = Date.parse('yyyy-MMM-dd', '2015-MAR-13')
def today = Date.parse('yyyy-MM-dd', params.week)
def monday = ChaiUtils.previousDayOfWeek(today, MONDAY).time
def endOfDay = { Date d -> new Date((d + 1).time - 1) }
//END DATES

//EXTRACT TERRITORY
Assert.notNull params.territory, "Please Select A Territory"
def selectedTerritories = territories.findAll { it.name == params.territory }.collect { it.id }


def query = { Long startDate, Long endDate, String prefix ->
    return """
MATCH (territory:Territory) WHERE any( x IN $selectedTerritories WHERE x = id(territory))
MATCH territory<-[:SC_IN_TERRITORY]-subcounty<-[:HAS_SUB_COUNTY]-district
MATCH subcounty<-[:CUST_IN_SC]-customer-[:CUST_TASK]->(task:DetailerTask)
    WHERE task.dueDate >= $startDate and task.dueDate <= $endDate
MATCH customer-[:IN_SEGMENT]->segment
RETURN
    customer.outletName as ${prefix}_outletName,
    segment.name as ${prefix}_segment,
    district.name as ${prefix}_district,
    subcounty.name as ${prefix}_subcounty,
    task.status as ${prefix}_status
order by ${prefix}_district asc,${prefix}_subcounty asc"""
}

List<FuzzyCSVTable> datas = []
def days = []

(0..6).each {
    def startDate = monday + it
    def endDate = endOfDay(startDate)
    def prefix = startDate.format('EEE')
    days << prefix

    println("getting report for [$startDate] to [$endDate]")
    def result = neo.query(query(startDate.time, endDate.time, prefix), Collections.EMPTY_MAP)
    List<Map> resultMap = result.collect()
    def resultCsv = tbl toCSV(resultMap, "${prefix}_district", "${prefix}_subcounty", "${prefix}_segment", "${prefix}_outletName", "${prefix}_status")
    datas << resultCsv
}


datas = datas.collect {
    it.addColumn(RecordFx.fn('idx') { r -> r.idx() })
}


def finalCsv = datas[0]




datas[1..-1].each { FuzzyCSVTable a ->
    finalCsv = finalCsv.fullJoin(a, 'idx')
}

//Column Groups
def groups = [:]

days.each {
    groups[it] = ["${it}_district", "${it}_subcounty", "${it}_segment", "${it}_outletName", "${it}_status"]
}

// labels
def labels = [
        username : "Username",
        district : "District",
        subcounty: "SubCounty",
        segment  : "Segment",
        idx      : "No.",
]
days.each {
    labels."${it}_status" = "Status"
    labels."${it}_outletName" = "Outlet"
    labels."${it}_district" = "DISTR"
    labels."${it}_subcounty" = "SC"
    labels."${it}_segment" = "Segment"
}



def g = new CSVGraph('CHAI CRM', "Call Plan For[${monday.format('EEE yyyy-MMM-dd')}] to ${(monday + 6).format('EEE yyyy-MMM-dd')}", 'http://23.239.27.196:8080/web-crm/', 'image', finalCsv.csv)
g.headings =[
        "Dates: From[${monday.format('EEE yyyy-MMM-dd')}] to ${(monday + 6).format('EEE yyyy-MMM-dd')}" : "" ,
        "Territory: $params.territory" : ""
]
g.labelMap = labels
g.titleGroup(groups)
        .setShowChart(false)
        .setShowColumnLines(true)



g.report.setPageFormat(PageType.A1, PageOrientation.LANDSCAPE)//.show()