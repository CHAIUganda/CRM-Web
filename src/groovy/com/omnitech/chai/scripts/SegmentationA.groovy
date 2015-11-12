package com.omnitech.chai.scripts

import com.omnitech.chai.model.Customer
import com.omnitech.chai.model.DetailerTask
import com.omnitech.chai.reports.ReportContext
import com.omnitech.chai.util.ChaiUtils

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
def diarrheaMarket = task?.diarrheaPatientsInFacility

def clinicFilter = { Customer c -> ['Health Centre', 'Hospital', 'Clinic', 'Private Clinic', 'Public Health Center', 'Public Hospital', 'Private Hospital'].contains(c.outletType) }
def drugShopFilter = { Customer c -> ['Drug Shop'].contains(c.outletType) }
def pharmacyFilter = { Customer c -> ['Pharmacy', 'Retail Pharmacy', 'Wholesale Pharmacy'].contains(c.outletType) }

// ******************************************
// FOOTFALL SCORE
// ******************************************
def footFallScore = calcScore(customer, 0.15, [
        (clinicFilter)  : { intRangeScore footFall, [10, 4, 0] },
        (pharmacyFilter): { intRangeScore footFall, [10, 4, 0] },
        (drugShopFilter): { intRangeScore footFall, [10, 4, 0] },
])
println("$customer.outletName($customer.outletType) FOOTFALL: $footFall : Weighted Score = $footFallScore : Raw = ${footFallScore / 0.15}")

// ******************************************
// LOCATION
// ******************************************
def locationScore = calcScore(customer, 0.1, { objRangeScore customer.split?.toLowerCase(), ['rural', 'urban'] })
println("$customer.outletName($customer.outletType) LOCATION: $customer.split : Weighted Score = $locationScore : RealScore = ${locationScore / 0.1}")

// ******************************************
// STOCK LEVEL OF ORS
// ******************************************

def orsStockLevel = task?.detailerStocks?.findAll { it.category == 'ors' }?.collect { it.stockLevel }?.sum()
def orsStockScore = calcScore(customer, 0.25, [
        (clinicFilter)  : { intRangeInverse orsStockLevel, [0, 16] },
        (pharmacyFilter): { intRangeInverse orsStockLevel, [0, 16] },
        (drugShopFilter): { intRangeInverse orsStockLevel, [0, 16] }
])
println("$customer.outletName($customer.outletType) LEVEL OF ORS: $orsStockLevel : Weighted Score = $orsStockScore : RealScore = ${orsStockScore / 0.25}")

// ******************************************
// STOCK LEVEL OF ZINC
// ******************************************
def zincStockLevel = task?.detailerStocks?.findAll { it.category == 'zinc' }?.collect { it.stockLevel }?.sum()
def zincStockScore = calcScore(customer, 0.15, [
        (clinicFilter)  : { intRangeInverse zincStockLevel, [0, 16] },
        (pharmacyFilter): { intRangeInverse zincStockLevel, [0, 16] },
        (drugShopFilter): { intRangeInverse zincStockLevel, [0, 16] }
])
println("$customer.outletName($customer.outletType) LEVEL OF ZINC: $zincStockLevel : Weighted Score = $zincStockScore : RealScore = ${zincStockScore / 0.15}")

// ******************************************
// PRICE OF ORS
// ******************************************

def orsAveragePriceList = task?.detailerStocks?.findAll { it.category == 'ors' }?.collect { it.sellingPrice }

def orsPriceCount
if (orsAveragePriceList != null) {
    orsPriceCount = orsAveragePriceList.size()
} else {
    orsPriceCount = 0
}

def orsAveragePrice
if (orsPriceCount == 0) {
    orsAveragePrice = 0
} else {
    orsAveragePrice = orsAveragePriceList.sum()/orsPriceCount
}

def orsPriceScore = calcScore(customer, 0.10, [
        (clinicFilter)  : { intRangeInverse orsAveragePrice, [0, 400, 600] },
        (pharmacyFilter): { intRangeInverse orsAveragePrice, [0, 400, 600] },
        (drugShopFilter): { intRangeInverse orsAveragePrice, [0, 400, 600] }
])
println("$customer.outletName($customer.outletType) PRICE OF ORS: $orsAveragePrice : Weighted Score = $orsPriceScore : RealScore = ${orsPriceScore / 0.1}")

// ******************************************
// PRICE OF ZINC
// ******************************************

def zincAveragePriceList = task?.detailerStocks?.findAll { it.category == 'zinc' }?.collect { it.sellingPrice }

def zincPriceCount
if (zincAveragePriceList != null) {
    zincPriceCount = zincAveragePriceList.size()
} else {
    zincPriceCount = 0
}

def zincAveragePrice
if (zincPriceCount == 0) {
    zincAveragePrice = 0
} else {
    zincAveragePrice = zincAveragePriceList.sum()/orsPriceCount
}

def zincPriceScore = calcScore(customer, 0.15, [
        (clinicFilter)  : { intRangeInverse zincAveragePrice, [0, 2000, 3000] },
        (pharmacyFilter): { intRangeInverse zincAveragePrice, [0, 2000, 3000] },
        (drugShopFilter): { intRangeInverse zincAveragePrice, [0, 2000, 3000] }
])
println("$customer.outletName($customer.outletType) PRICE OF ZINC: $zincAveragePrice : Weighted Score = $zincPriceScore : RealScore = ${zincPriceScore / 0.15}")

// ************************************
if (!task) {
    return 1.5
}


// ******************************************
// EDUCATION LEVEL
// ******************************************

def levels = ['high', 'medium', 'low']

def diaKnowledge = objRangeScore(cleanUpWeight(task.whatYouKnowAbtDiarrhea), levels)
def diaEffectsKnowledge = objRangeScore(cleanUpWeight(task.diarrheaEffectsOnBody), levels)
def orsKnowledge = objRangeScore(cleanUpWeight(task.knowledgeAbtOrsAndUsage), levels)
def zincKnowledge = objRangeScore(cleanUpWeight(task.knowledgeAbtZincAndUsage), levels)
def antiBioticKnowledge = objRangeScore(cleanUpWeight(task.whyNotUseAntibiotics), levels)

def totalKnowledge = (diaKnowledge + diaEffectsKnowledge + orsKnowledge + zincKnowledge + antiBioticKnowledge)

def knowledgeScore = calcScore(customer, 0.1, { intRangeInverse totalKnowledge, [0, 11, 15] })

def knowledgeList = []
task.with {
    knowledgeList << [whatYouKnowAbtDiarrhea, diarrheaEffectsOnBody, knowledgeAbtOrsAndUsage, knowledgeAbtZincAndUsage, whatYouKnowAbtDiarrhea]

}
println("$customer.outletName($customer.outletType) KNOWLEDGE: $knowledgeList TT:Knowlege: $totalKnowledge : Weighted Score = $knowledgeScore : RealScore = ${knowledgeScore / 0.1}")


def score = footFallScore + locationScore + knowledgeScore + orsStockScore + zincStockScore + orsPriceScore + zincPriceScore
println(score)
return score

//inverse score
///fixed weight cleanup