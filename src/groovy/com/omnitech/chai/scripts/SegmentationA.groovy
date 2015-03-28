package com.omnitech.chai.scripts

import com.omnitech.chai.model.Customer
import com.omnitech.chai.model.DetailerTask
import com.omnitech.chai.reports.ReportContext
import com.omnitech.chai.util.ChaiUtils

import static com.omnitech.chai.model.Customer.getSTRUCT_SEMI_PERMANENT
import static com.omnitech.chai.model.Customer.getTYPE_CLINIC
import static com.omnitech.chai.scripts.ScripHelpers.*

//Customer cust =  customer as Customer
//remove this in lower IDEA version <property name="dynamic.classpath" value="true" />
ChaiUtils.injectUtilityMethods()
//Customer customer = new Customer(
//        numberOfCustomersPerDay: 5,
//        turnOver: '75,000',
//        numberOfProducts: '10-30',
//        split: 'Urban',
//        visibleEquipment: 'equip1,equip2',
//        buildingStructure: STRUCT_SEMI_PERMANENT,
//        outletType: TYPE_CLINIC,
//        outletName: 'NoName',
//        childrenUnder5yrsPerDay: 50
//)

ReportContext rc = reportContext
DetailerTask task = task
//println("*************Segmenting: ************** $customer")


def footFall = customer.numberOfCustomersPerDay
def products = getProductCount(customer.numberOfProducts)
def diarrheaMarket = customer.numberOfCustomersPerDay

def clinicFilter = { Customer c -> ['Health Centre', 'Hospital', 'Clinic', 'Private Clinic', 'Public Health Center', 'Public Hospital', 'Private Hospital'].contains(c.outletType) }
def drugShopFilter = { Customer c -> ['Drug Shop'].contains(c.outletType) }
def pharmacyFilter = { Customer c -> ['Pharmacy', 'Retail Pharmacy', 'Wholesale Pharmacy'].contains(c.outletType) }

// ******************************************
// FOOTFALL SCORE
// ******************************************
def footFallScore = calcScore(customer, 0.05, [
        (clinicFilter)  : { intRangeScore footFall, [15, 5, 0] },
        (pharmacyFilter): { intRangeScore footFall, [50, 10, 0] },
        (drugShopFilter): { intRangeScore footFall, [15, 5, 0] },
])
println("$customer.outletName($customer.outletType) FOOTFALL: $footFall : Weighted Score = $footFallScore : Raw = ${footFallScore / 0.05}")

// ******************************************
// DIARRHEA MARKET
// ******************************************
def diarrheaMarketScore = calcScore(customer, 0.05, [
        (clinicFilter)  : { intRangeScore diarrheaMarket, [10, 5, 0] },
        (pharmacyFilter): { intRangeScore diarrheaMarket, [23, 9, 0] },
        (drugShopFilter): { intRangeScore diarrheaMarket, [10, 5, 0] }
])
println("$customer.outletName($customer.outletType) CHILDREN SERVED: $diarrheaMarketScore : Weighted Score = $diarrheaMarketScore : RealScore = ${diarrheaMarketScore / 0.05}")


// ******************************************
// PRODUCT SCORE
// ******************************************
def productScore = calcScore(customer, 0.05, [
        (clinicFilter)  : { intRangeScore products, [50, 10, 0] },
        (pharmacyFilter): { intRangeScore products, [50, 10, 0] },
        (drugShopFilter): { intRangeScore products, [50, 10, 0] }
])
println("$customer.outletName($customer.outletType) PRODUCT SCORE: $products : Weighted Score = $productScore : RealScore = ${productScore / 0.05}")

// ******************************************
// LOCATION
// ******************************************
def locationScore = calcScore(customer, 0.1, { objRangeScore customer.split?.toLowerCase(), ['urban', 'rural'] })
println("$customer.outletName($customer.outletType) LOCATION: $customer.split : Weighted Score = $locationScore : RealScore = ${locationScore / 0.05}")

////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////// VARIABLE SCORES /////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////

if(!task){
    return  1.5
}

def diarrheaRecommendationScore = calcScore(customer, 0.1, {
    objRangeScore task.recommendationLevel?.toLowerCase(), ['yes', 'no']
})

// ******************************************
// EDUCATION LEVEL
// ******************************************

def levels = ['high', 'medium', 'low']

def diaKnowledge = intRangeScore(cleanUpWeight(task.whatYouKnowAbtDiarrhea), levels)
def diaEffectsKnowledge = intRangeScore(cleanUpWeight(task.diarrheaEffectsOnBody), levels)
def orsKnowledge = intRangeScore(cleanUpWeight(task.knowledgeAbtOrsAndUsage), levels)
def zincKnowledge = intRangeScore(cleanUpWeight(task.knowledgeAbtZincAndUsage), levels)
def antiBioticKnowledge = intRangeScore(cleanUpWeight(task.whyNotUseAntibiotics), levels)

def totalKnowledge = (diaKnowledge + diaEffectsKnowledge + orsKnowledge + zincKnowledge + antiBioticKnowledge)

def knowledgeScore = calcScore(customer, 0.1, { intRangeScore totalKnowledge, [11, 9, 5] })

def knowledgeList = []
task.with {
    knowledgeList << [whatYouKnowAbtDiarrhea, diarrheaEffectsOnBody, knowledgeAbtOrsAndUsage, knowledgeAbtZincAndUsage, whatYouKnowAbtDiarrhea]

}
println("$customer.outletName($customer.outletType) KNOWLEDGE: $knowledgeList TT:Knowlege: $totalKnowledge : Weighted Score = $knowledgeScore : RealScore = ${knowledgeScore / 0.2}")

// ******************************************
// STOCK LEVEL OF ORS
// ******************************************

def orsStockLevel = task?.detailerStocks?.findAll { it.category == 'ors' }?.collect { it.stockLevel }?.sum()
def orsStockScore = calcScore(customer, 0.2, [
        (clinicFilter)  : { intRangeScore orsStockLevel, [60, 30, 0] },
        (pharmacyFilter): { intRangeScore orsStockLevel, [200, 50, 0] },
        (drugShopFilter): { intRangeScore orsStockLevel, [25, 5, 0] }
])
println("$customer.outletName($customer.outletType) LEVEL OF ORS: $orsStockLevel : Weighted Score = $orsStockScore : RealScore = ${orsStockScore / 0.2}")

// ******************************************
// STOCK LEVEL OF ZINC
// ******************************************
def zincStockLevel = task?.detailerStocks?.findAll { it.category == 'zinc' }?.collect { it.stockLevel }?.sum()
def zincStockScore = calcScore(customer, 0.2, [
        (clinicFilter)  : { intRangeScore zincStockLevel, [60, 30, 0] },
        (pharmacyFilter): { intRangeScore zincStockLevel, [200, 50, 0] },
        (drugShopFilter): { intRangeScore zincStockLevel, [25, 5, 0] }
])
println("$customer.outletName($customer.outletType) LEVEL OF ORS: $zincStockLevel : Weighted Score = $zincStockScore : RealScore = ${zincStockScore / 0.2}")

// ******************************************
// AVERAGE SALES VALUE
// ******************************************
def averageSalesValue = rc.averageSalesValue(customer)
def salesValueScore = calcScore(customer, 0, [
        (clinicFilter)  : { intRangeScore averageSalesValue, [25000, 7500, 0] },
        (pharmacyFilter): { intRangeScore averageSalesValue, [30000, 10000, 0] },
        (drugShopFilter): { intRangeScore averageSalesValue, [20000, 5000, 0] }
])
println("$customer.outletName($customer.outletType) SALES VALUE: $averageSalesValue : Weighted Score = $salesValueScore : RealScore = ${salesValueScore}/0")



def score = footFallScore + diarrheaMarketScore + productScore + locationScore + diarrheaRecommendationScore + knowledgeScore + orsStockScore + zincStockScore + salesValueScore
println(score)
return score