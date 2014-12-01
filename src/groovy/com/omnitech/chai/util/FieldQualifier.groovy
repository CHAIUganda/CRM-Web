package com.omnitech.chai.util

/**
 * Created by kay on 12/1/14.
 */
class FieldQualifier {

    Map<String, List<Map>> filters = [:]

    FieldQualifier() {
    }

    FieldQualifier(Map<String, List<Map>> filters) {
        this.filters = filters
    }

    boolean isAllowed(Class klass, String fieldName) {
        if(checkDenied(klass,fieldName)){
            return false
        }


    }

    private boolean checkAllowed(Class klass, String fieldName){
        def allAllowedEntries = filters.allow
        def deniedEntry = allAllowedEntries?.find { stringsMatch(it['class'].toString(), klass.simpleName) }

    }

    private boolean checkDenied(Class klass, String fieldName) {
        def allDeniedEntries = filters.deny
        def deniedEntry = allDeniedEntries?.find { stringsMatch(it['class'].toString(), klass.simpleName) }

        //if there are no patterns then all fields are denied
        def patterns = deniedEntry.patterns as List<String>

        if (!patterns) {
            return true
        }

        //if the field name matches any of the patterns then we are denied
        return patterns.any { stringsMatch(it, fieldName) }
    }

    private static boolean stringsMatch(String pattern, String right) {
        def patternRegex = pattern.replace('*', '.*')
        return right.matches(patternRegex)
    }
}
