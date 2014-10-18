package com.omnitech.chai.util

import org.slf4j.LoggerFactory

/**
 * Created by kay on 9/29/14.
 */
class ChaiUtils {
    static log = LoggerFactory.getLogger(ChaiUtils)

    static injectUtilityMethods() {
        String.metaClass.toLongSafe = {
            def _delegate = delegate
            return execSilently { Long.valueOf(_delegate) }
        }
    }

    static execSilently(String error, Closure code) {
        try {
            return code.call()
        } catch (Exception ex) {
            log.error(error, ex)
        }
        return null
    }

    static execSilently(Closure code) {
        execSilently('Unkown Error', code)
    }

    static String truncateString(def s,Number num){
        if (s==null)  return ''

        def temp = s.toString()

        if (num >= temp.size() )
            return  temp
        return temp[0..num-3]+'...'
    }
}
