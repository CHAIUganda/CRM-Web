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

    boolean isAllowed(String className, String fieldName) {
        if (!filters) return true

        if (checkIsDenied(className, fieldName)) {
            return false
        }

        return checkIsAllowed(className, fieldName)
    }

    private boolean checkIsAllowed(String klass, String fieldName) {
        def allAllowedEntries = filters.allow

        //if there are no whiteList entries then all are allowed
        if (!allAllowedEntries) {
            return true
        }

        def allowedEntry = allAllowedEntries?.find { stringsMatch(it['class'].toString(), klass) }

        // if there are no entries for this class then this field is not allowed
        if (!allowedEntry) {
            return false
        }

        def patterns = allowedEntry.patterns as List<String>

        //if there are no patterns for this class then this field allowed
        if (!patterns) {
            return true
        }

        return patterns.any { stringsMatch(it, fieldName) }


    }

    private boolean checkIsDenied(String klass, String fieldName) {
        def allDeniedEntries = filters.deny
        def deniedEntry = allDeniedEntries?.find { stringsMatch(it['class'].toString(), klass) }

        if (!deniedEntry) return false

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

    static boolean isAllowed(Map pattern, Class klass, String fieldName) {
        isAllowed(pattern, klass.simpleName, fieldName)
    }

    static boolean isAllowed(Map pattern, String klass, String fieldName) {
        return new FieldQualifier(pattern).isAllowed(klass, fieldName)
    }
}
