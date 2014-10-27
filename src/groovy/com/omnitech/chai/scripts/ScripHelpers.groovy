package com.omnitech.chai.scripts

import com.omnitech.chai.model.Customer

/**
 * Created by kay on 10/24/14.
 */
class ScripHelpers {


    static int getProductCount(String products) {
        def m = ['less than 10': 5, '10-30': 20, 'more than 30': 35]
        return m[products] ?: 0
    }


    static int getTurnOver(String products) {
        def map = ['less than 50,000 UGX': 2500, '50,000-150,000 UGX': 100000, '150,000 - 300,000 UGX': 235000, 'greater than 300,000 UGX': 350000]
        return map[products] ?: 0
    }


    static def calcScore(Customer c, double weight, def functions) {
        if (functions instanceof Closure) {
            return (functions(c) * weight)
        }
        def func = functions.get("$c.outletType")
        return (func(c) as Float) * weight
    }


    static int intRangeScore(def value, List ranges) {
        def size = ranges.size()
        for (int i = 0; i < size; i++) {
            def val = ranges[i]
            if (i == 0 && value > val) return size
            if (value >= val) return (size == i + 1) ? 1 : size - 1
        }
        return 0
    }

    static int objRangeScore(def value, List ranges) {
        def score = ranges.indexOf(value)
        if (score != -1) return score
        return 0
    }


}
