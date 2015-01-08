package com.omnitech.chai.util

import grails.validation.ValidationException
import groovy.time.TimeCategory
import groovy.transform.CompileStatic
import org.slf4j.LoggerFactory

import java.text.DateFormat
import java.text.SimpleDateFormat

/**
 * Created by kay on 9/29/14.
 */
class ChaiUtils {
    static log = LoggerFactory.getLogger(ChaiUtils)

    private static DateFormat format = new SimpleDateFormat('yyyy-MM-dd')

    static String formatDate(Date date) {
        if (date) return format.format(date)
        return null
    }

    static injectUtilityMethods() {
        String.metaClass.toLongSafe = {
            def _delegate = delegate
            return execSilently { Long.valueOf(_delegate) }
        }

        String.metaClass.removeExtraSpace = {
            return ChaiUtils.cleanUp.call(delegate)
        }

        String.metaClass.removeAllSpace = {
            return delegate.toString().replaceAll(/\s+/, '')
        }

        String.metaClass.removeNonAscII = {
            return delegate.toString().replaceAll(/[^\u0020-\u007F \\ @,\/.#\n*\-\u0024'()\+{}]+/, '')
        }
    }

    static def cleanUp = { string -> string.toString().replaceAll(/\s+/, ' ').trim()}.memoizeBetween(0,100)

    static execSilently(String error, Closure code) {
        try {
            return code.call()
        } catch (Exception ex) {
            log.error(error, ex)
        }
        return null
    }

    static execSilently(Closure code) {
        execSilently('Unknown Error', code)
    }

    static String truncateString(def s, Number num) {
        if (s == null) return ''

        def temp = s.toString()

        if (num >= temp.size())
            return temp
        return temp[0..num - 3] + '...'
    }

    private static def numberEnding(Number number) {
        def end = (number < 0) ? ' ago' : ''
        def absNum = number.abs()
        return (absNum > 1) ? "s$end" : end;
    }

//    @CompileStatic
    @SuppressWarnings("GroovyAssignabilityCheck")
    static String fromNow(Date date) {
        if (!date) return ''
        def d = TimeCategory.minus(date, new Date())
        if (d.days) {
            int days = d.days

            long years = Math.round(days / 365)
            if (years) {
                return "${years.abs()} year${numberEnding years}"
            }

            long months = (long) (days / 30)
            if (months) {
                def roundMonth = Math.round(days / 30)
                return "${roundMonth.abs()} month${numberEnding roundMonth}"
            }

            long weeks = (long) (days / 7)
            if (weeks) {
                if (weeks == 1) {
                    days -= (weeks * 7)
                    return "${weeks.abs()} week${numberEnding weeks} and $days day${numberEnding days}"
                }
                return "${weeks.abs()} week${numberEnding weeks}"
            }
            return "${days.abs()} day${numberEnding days}"
        }

        if (d.hours) {
            return "${d.hours.abs()} hour${numberEnding d.hours}"
        }

        if (d.minutes) {
            return "${d.minutes.abs()} minute${numberEnding d.minutes}"
        }

        return "now"

    }

    @CompileStatic
    static long roundToNearest(double value, long nearest) {
        double newValue = value + (nearest / 2)
        return ((long) (newValue) / nearest) * nearest
    }

    static String splitCamelCase(String s) {
        return s.replaceAll(
                String.format("%s|%s|%s",
                        "(?<=[A-Z])(?=[A-Z][a-z])",
                        "(?<=[^A-Z])(?=[A-Z])",
                        "(?<=[A-Za-z])(?=[^A-Za-z])"
                ),
                " "
        );
    }

    static String getBestMessage(Throwable x) {

        if (x instanceof ValidationException) {
            return ValidationException.formatErrors(x.errors)
        }

        def message = "${x.getClass().simpleName}: $x.message"

        if (message) return message
        return "Technical Error Please Contact Sytem Admin: $x"
    }

}
