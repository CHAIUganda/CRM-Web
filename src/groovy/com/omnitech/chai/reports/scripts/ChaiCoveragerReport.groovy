package com.omnitech.chai.reports.scripts

import com.omnitech.chai.reports.ReportContext
import csvgraphs.CSVGraph
import csvgraphs.Colors
import filterreport.Filter
import org.springframework.data.neo4j.support.Neo4jTemplate

import static fuzzycsv.FuzzyCSV.toCSV
import static fuzzycsv.FuzzyCSVTable.tbl
import static java.util.Calendar.*
import static net.sf.dynamicreports.report.builder.DynamicReports.cht

/**
 * Created by kay on 1/10/2015.
 */

//=IF(B5=1,CONCATENATE("DET",ROW()-1),"NONE")
//def ctx = new ClassPathXmlApplicationContext('applicationContext.xml')
//def neo = ctx.getBean(Neo4jTemplate)

neo = neo as Neo4jTemplate
ReportContext rc = reportContext

doInTransaction(neo) {

    def products = rc.userProducts.collect { it.name }
    def months = ['JAN': JANUARY,
                  'FEB': FEBRUARY,
                  'MAR': MARCH,
                  'APR': APRIL,
                  'MAY': MAY,
                  'JUN': JUNE,
                  'JUL': JULY,
                  'AUG': AUGUST,
                  'SEP': SEPTEMBER,
                  'OCT': OCTOBER,
                  'NOV': NOVEMBER,
                  'DEC': DECEMBER]


    if (action == 'p_get_filter') {

//        ['JAN', 'FEB', 'MAR', 'MAY', 'JUN', 'JUL', 'AUG', 'SEP', 'OCT', 'NOV', 'DEC']

        return [
                new Filter([fieldName       : 'month',
                            fieldDescription: 'Month',
                            fieldType       : List,
                            possibleValues  : months.keySet() as List])
        ]

    }


    def endDate = new GregorianCalendar()
    endDate.set(MONTH, months.get(params.month))
    endDate.set(DAY_OF_MONTH, endDate.getActualMaximum(DAY_OF_MONTH))



    def query = neo.query('''match (r:Role{authority:'ROLE_DETAILER'})<-[:HAS_ROLE]-(usr:User)-[:USER_TERRITORY]->(t)<-[:SC_IN_TERRITORY]-(sc)<-[:CUST_IN_SC]-(cm),
sc<-[:HAS_SUB_COUNTY]-d
optional match cm-[:CUST_TASK]->(ts:DetailerTask{status:'complete'}) where ts.completionDate <= {endDate}
with usr,d,cm,head(collect(ts)) as tm
with usr.username as USER,d.name as DISTRICT,
\tcount(distinct cm) as CUSTOMERS,
\tsum(CASE WHEN not(tm is null) THEN 1 else 0 END ) as COVERED
return USER,DISTRICT,CUSTOMERS,COVERED,round(toFloat(COVERED)/toFloat(CUSTOMERS)*100) as PERCENTAGE_COVERED
order by USER''', [endDate: endDate.timeInMillis])

    def data = tbl(toCSV(query.collect {
        it
    }, 'USER', 'DISTRICT', 'CUSTOMERS', 'COVERED', 'PERCENTAGE_COVERED'))

    CSVGraph g = new CSVGraph('CHAI CRM', "Coverage Report [$params.month ${endDate.get(YEAR)}]", 'http://23.239.27.196:8080/web-crm/', '/image', data.csv)
            .setChart(cht.barChart())
            .setChartLabelTilt(true)
            .setShowColumnLines(true)
            .setColors(Colors.warm.values())
            .setShowChart(false)

    g.labelMap = [
            PERCENTAGE_COVERED : 'PERCENTAGE_COVERED (%)'
    ]

//    g.getColumn('PERCENTAGE_COVERED').setPattern('$#%')

    g.report.groupBy(g.getColumn('USER'))//.show()


}


def doInTransaction(Neo4jTemplate neo, Closure code) {
    def service = neo.graphDatabaseService
//    def tx = service.beginTx()
    try {
        code.call()
    } finally {
//        tx.close()
    }
}