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
                            possibleValues  : months.keySet() as List]),

                new Filter([fieldName       : 'product',
                            fieldDescription: 'Product',
                            fieldType       : List,
                            possibleValues  : products])
        ]

    }


    def startDate = new GregorianCalendar()
    startDate.set(MONTH, months.get(params.month))
    startDate.set(DAY_OF_MONTH, startDate.getActualMinimum(DAY_OF_MONTH))

    def endDate = new GregorianCalendar()
    endDate.set(MONTH, months.get(params.month))
    endDate.set(DAY_OF_MONTH, startDate.getActualMaximum(DAY_OF_MONTH))

    //the average number of products sold with in a given months, total number of products and orders taken but only within that territory


    def query = neo.query('''match (us:User)-[:COMPLETED_TASK]->(sa:Sale)-[si:HAS_PRODUCT]->(pr{name:{product}})
where (sa.dateCreated >= {startDate})  and (sa.dateCreated <= {endDate})
match sa<-[:CUST_TASK]-cu-[:CUST_IN_SC]->sc<-[:HAS_SUB_COUNTY]-di
optional match us<-[ORDER_TAKEN_BY]-(od)<-[:CUST_TASK]-cu
where (od.dateCreated >= {startDate})  and (od.dateCreated <= {endDate})
optional match od-[oi:HAS_PRODUCT]-(p{name:{product}})
with pr,sa,di,us,cu,sum(si.quantity) as productsSold,length(filter(o in collect(oi) where o.dropSample = true)) as samples
return us.username as USER,pr.name as PRODUCT,di.name as DISTRICT,avg(productsSold) as AVERAGE_PRODUCTS,sum(productsSold) as TOTAL_SOLD,sum(samples) as SAMPLES_DROPPED
order by USER''', [product: params.product, startDate: startDate.timeInMillis, endDate: endDate.timeInMillis])

    def data = tbl(toCSV(query.collect {
        it
    }, 'USER', 'PRODUCT', 'DISTRICT', 'AVERAGE_PRODUCTS', 'TOTAL_SOLD', 'SAMPLES_DROPPED'))

    CSVGraph g = new CSVGraph('CHAI CRM', 'Product Report', 'http://23.239.27.196:8080/web-crm/', '/image', data.csv)
            .setChart(cht.barChart())
            .setChartLabelTilt(true)
            .setShowColumnLines(true)
            .setColors(Colors.warm.values())

    g.getColumn('AVERAGE_PRODUCTS')

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