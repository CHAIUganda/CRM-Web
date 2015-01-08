package com.omnitech.chai.scripts

import com.omnitech.chai.util.ChaiUtils

import static com.omnitech.chai.model.Customer.*
import static com.omnitech.chai.scripts.ScripHelpers.*

//Customer cust =  customer as Customer
//remove this in lower IDEA version <property name="dynamic.classpath" value="true" />
ChaiUtils.injectUtilityMethods()
//Customer customer = new Customer(
//        numberOfCustomersPerDay: 50000,
//        turnOver: '75,000',
//        numberOfProducts: '10-30',
//        split: 'Urban',
//        visibleEquipment: 'equip1,equip2',
//        buildingStructure: STRUCT_SEMI_PERMANENT,
//        outletType: TYPE_CLINIC,
//        childrenUnder5yrsPerDay: 50
//)


def footFall = customer.numberOfCustomersPerDay
def turnOver = customer.turnOver?.replace(',', '')?.removeExtraSpace()?.toLongSafe() ?: 0
def products = getProductCount(customer.numberOfProducts)
def childrenServed = customer.childrenUnder5yrsPerDay
def visibleEquipment = customer.visibleEquipment?.split(',')?.size() ?: 0

// ******************************************
// FOOTFALL SCORE
// ******************************************
def footFallScore = calcScore(customer, 0.05, [
        "$TYPE_CLINIC"       : { intRangeScore footFall, [200000, 100000, 0] },
        "$TYPE_HEALTH_CENTER": { intRangeScore footFall, [200000, 100000, 0] },
        "$TYPE_PHARMACY"     : { intRangeScore footFall, [100000, 50000, 0] },
        "$TYPE_DRUG_SHOP"    : { intRangeScore footFall, [75000, 25000, 0] },
])
println("$customer.outletType FOOTFALL: $footFall : Weighted Score = $footFallScore : Raw = ${footFallScore / 0.05}")

// ******************************************
// CHILDREN SCORE
// ******************************************
def childrenScore = calcScore(customer, 0.05, [
        "$TYPE_CLINIC"       : { intRangeScore childrenServed, [100, 50, 0] },
        "$TYPE_HEALTH_CENTER": { intRangeScore childrenServed, [100, 50, 0] },
        "$TYPE_PHARMACY"     : { intRangeScore childrenServed, [50, 25, 0] },
        "$TYPE_DRUG_SHOP"    : { intRangeScore childrenServed, [38, 12, 0] },
])
println("$customer.outletType CHILDREN SERVED: $childrenServed : Weighted Score = $childrenScore : RealScore = ${childrenScore / 0.05}")

// ******************************************
// TURNOVER SCORE
// *******************************************
def turnOverScore = calcScore(customer, 0.05, [
        "$TYPE_CLINIC"       : { intRangeScore turnOver, [10460, 5080, 0] },
        "$TYPE_HEALTH_CENTER": { intRangeScore turnOver, [10460, 5080, 0] },
        "$TYPE_PHARMACY"     : { intRangeScore turnOver, [31680, 13825, 0] },
        "$TYPE_DRUG_SHOP"    : { intRangeScore turnOver, [5630, 2750, 0] },
])
println("$customer.outletType TURNOVER: $turnOver : Weighted Score = $turnOverScore : RealScore = ${turnOverScore / 0.05}")

// ******************************************
// PRODUCT SCORE
// ******************************************
def productScore = calcScore(customer, 0.05, [
        "$TYPE_CLINIC"       : { intRangeScore products, [30, 10, 0] },
        "$TYPE_HEALTH_CENTER": { intRangeScore products, [30, 10, 0] },
        "$TYPE_PHARMACY"     : { intRangeScore products, [30, 10, 0] },
        "$TYPE_DRUG_SHOP"    : { intRangeScore products, [30, 10, 0] }
])
println("$customer.outletType PRODUCT SCORE: $products : Weighted Score = $productScore : RealScore = ${productScore / 0.05}")

// ******************************************
// PROFESSIONALISM
// ******************************************
def professionalismSore = calcScore(customer, 0.05, { intRangeScore visibleEquipment, [3, 2, 1] })
println("$customer.outletType PROFESSIONALISM: $visibleEquipment : Weighted Score = $professionalismSore : RealScore = ${professionalismSore / 0.05}")

// ******************************************
// BUILDING STRUCTURE
// ******************************************
def buildingStructureScore = calcScore(customer, 0.05,
        {
            objRangeScore customer.buildingStructure, [STRUCT_PERMANENT, STRUCT_SEMI_PERMANENT, STRUCT_NON_PERMANENT]
        }
)
println("$customer.outletType BUILDING STRUCTURE: $customer.buildingStructure : Weighted Score = $buildingStructureScore : RealScore = ${buildingStructureScore / 0.05}")

// ******************************************
// LOCATION
// ******************************************
def locationScore = calcScore(customer, 0.1, { objRangeScore customer.split, ['Urban', 'Rural'] })
println("$customer.outletType LOCATION: $customer.split : Weighted Score = $locationScore : RealScore = ${locationScore / 0.05}")

def score = footFallScore + turnOverScore + productScore + professionalismSore + locationScore + buildingStructureScore + childrenScore
println(score)
return score