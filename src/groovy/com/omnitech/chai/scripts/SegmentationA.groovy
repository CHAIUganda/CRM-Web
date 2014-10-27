package com.omnitech.chai.scripts

import com.omnitech.chai.model.Customer
import com.omnitech.chai.model.CustomerSegment

import static com.omnitech.chai.model.Customer.*
import static com.omnitech.chai.scripts.ScripHelpers.*
Customer cust =  customer as Customer
//Customer cust = new Customer(
//        numberOfCustomersPerDay: 200,
//        turnOver:   '50,000-150,000 UGX',
//        numberOfProducts: '10-30',
//        split: 'urban',
//        equipment: 'equip1,equip2',
//        buildingStructure: STRUCT_SEMI_PERMANENT,
//        outletType: TYPE_CLINIC
//)


def footFall = cust.numberOfCustomersPerDay
def turnOver = getTurnOver(cust.turnOver)
def products = getProductCount(cust.numberOfProducts)


def footFallScore = calcScore(cust, 0.05, [
        "$TYPE_CLINIC": { intRangeScore footFall, [200, 100, 0] },
        "$TYPE_PHARMACY": { intRangeScore footFall, [100, 50, 0] },
        "$TYPE_DRUG_SHOP": { intRangeScore footFall, [75, 25, 0] },
])

def turnOverScore = calcScore(cust, 0.05, [
        "$TYPE_CLINIC": { intRangeScore turnOver, [10460, 5080, 0] },
        "$TYPE_PHARMACY": { intRangeScore turnOver, [31680, 13825, 0] },
        "$TYPE_DRUG_SHOP": { intRangeScore turnOver, [5630, 2750, 0] },
])

def productScore = calcScore(cust, 0.05, [
        "$TYPE_CLINIC": { intRangeScore products, [50, 15, 0] },
        "$TYPE_PHARMACY": { intRangeScore products, [75, 25, 0] },
        "$TYPE_DRUG_SHOP": { intRangeScore products, [30, 10, 0] }
])

def professionalism = calcScore(cust, 0.05, {
    def equipment = cust?.equipment?.split(',')?.size() ?: 0
    intRangeScore equipment, [3, 2, 1]
})

def buildingStructure = calcScore(cust, 0.05, {
    objRangeScore cust.buildingStructure, [STRUCT_PERMANENT, STRUCT_SEMI_PERMANENT, STRUCT_NON_PERMANENT]
})

def location = calcScore(cust, 0.05, { objRangeScore cust.split, ['urban', 'rural'] })

def score = footFallScore + turnOverScore + productScore + professionalism + location + buildingStructure
println(score)
return score























