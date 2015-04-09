package com.omnitech.chai.scripts

import com.omnitech.chai.model.Customer

import java.util.regex.Pattern

/**
 * Created by kay on 10/24/14.
 */
class ScripHelpers {


    static int getProductCount(String products) {
        def lProducts = products?.toLowerCase()
        if (!lProducts) return 0
        if (lProducts.contains('small')) return 5
        if (lProducts.contains('medium')) return 25
        if (lProducts.contains('big') || lProducts.contains('large')) return 55
        return 0
    }

    static String cleanUpWeight(String products) {
        def lProducts = products?.toLowerCase()
        if (lProducts.contains('high')) return 'high'
        if (lProducts.contains('medium')) return 'medium'
        if (lProducts.contains('low')) return 'low'
        return null
    }

    @Deprecated
    static int getTurnOver(String products) {
        def map = ['less than 50,000 UGX': 2500, '50,000-150,000 UGX': 100000, '150,000 - 300,000 UGX': 235000, 'greater than 300,000 UGX': 350000]
        return map[products] ?: 0
    }


    static def calcScore(Customer c, double weight, def functions) {
        if (functions instanceof Closure) {
            return (functions.call(c) * weight)
        }
        def func = getMatchedFunc(c, functions as Map)
        return (func(c) as Float) * weight
    }

    static Closure getMatchedFunc(Customer customer, Map<Object, Closure> functions) {

        def entry = functions.find { pattern, func ->

            if (pattern instanceof Pattern) {
                if (pattern.matcher(customer.outletType)) {
                    return true
                }
            }

            if (pattern instanceof String) {
                return customer.outletType == pattern
            }

            if (pattern instanceof Closure) {
                return pattern(customer)
            }

            return false
        }
        return entry.value
    }

    /**
     *    If used in normal mode then [50,30,0]  is read as
     *    between 0-29 = 1, 30-49 = 2, 50 and above = 3
     *
     *    If used as inverse then it is read as
     *    below 0 = 3, below 30 = 2, below 50 and above = 1
     * @param value
     * @param ranges
     * @param invert
     * @return
     */
    static int intRangeScore(def value, List ranges, boolean invert = false) {
        def size = ranges.size()

        for (int i = 0; i < size; i++) {
            def val = ranges[i]
            if (i == 0 && value >= val) {
                if (invert) return 1
                return size
            }
            if (value >= val) {
                def finalScore = (size == i + 1) ? 1 : size - 1
                if (invert) return size - 1
                return finalScore
            }
        }
        if (invert) return size
        return 0
    }

    static int intRangeInverse(def value, List ranges) {
        def size = ranges.size()
        def score = size + 1
        for (int i = 0; i < size; i++) {
            def val = ranges[i]
            if (value < val) return score
            score = score - 1
        }
        return score
    }

    static int objRangeScore(def value, List ranges) {
        def size = ranges.size()
        def score = ranges.indexOf(value)
        if (score != -1) return size - score
        return 0
    }


}
