package com.omnitech.chai.util

import com.omnitech.chai.exception.ImportException
import fuzzycsv.Record
import grails.util.Holders
import grails.validation.ValidationException
import groovy.time.TimeCategory
import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.text.DateFormat
import java.text.SimpleDateFormat

import static java.util.Calendar.DAY_OF_WEEK

/**
 * Created by kay on 9/29/14.
 */
class ChaiUtils {
    private static Logger log = LoggerFactory.getLogger(ChaiUtils)

    private static DateFormat format = new SimpleDateFormat('yyyy-MM-dd')


    static def <T> T bean(Class<T> c) {
        return Holders.applicationContext.getBean(c)
    }

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

    static def cleanUp = { string -> string.toString().replaceAll(/\s+/, ' ').trim() }.memoizeBetween(0, 100)

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
                    def string = "${weeks.abs()} week${numberEnding weeks}"
                    if (days) {
                        string = "$string and $days day${numberEnding days}"
                    }
                    return string
                }
                return "${weeks.abs()} week${numberEnding weeks}"
            }
//          return "${days.abs()}day(s)${d.hours ? ' ' + d.hours.abs() + 'h' : ''}${d.days < 0 ? ' ago' : ''}"
            if (d.hours) {
                return "${days.abs() + (days < 0 ? 0 : 1)} day${numberEnding(days + (days < 0 ? 0 : 1))}"
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

    static int dayDiff(Date now, Date then) {
        return then - now
    }

    static int dayDiffFomNow(Date then) {
        return dayDiff(new Date(), then)
    }

    @CompileStatic
    static long roundToNearest(double value, long nearest) {
        double newValue = value + (nearest / 2)
        return ((long) (newValue) / nearest) * nearest
    }

    @CompileStatic
    static long roundUpward(double value, long nearest) {
        if (value % nearest == 0) return value
        return value + (nearest - (long) (value % nearest))
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

    static Date getNextWorkDay(List<Integer> workDays, Date startDate) {
        while (!workDays.contains(startDate[DAY_OF_WEEK])) {
            startDate = ++startDate
        }
        return startDate
    }

    @CompileStatic
    public static Calendar nextDayOfWeek(Date startDate, int dow) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(startDate);
        int diff = dow - cal.get(DAY_OF_WEEK);
        if (!(diff > 0)) {
            diff += 7;
        }
        cal.add(Calendar.DAY_OF_MONTH, diff);
        return cal;
    }

    @CompileStatic
    public static Calendar previousDayOfWeek(Date startDate, int dow) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(startDate);
        int diff = dow - cal.get(DAY_OF_WEEK);
        if (diff >= 0) {
            diff -= 7;
        }
        cal.add(Calendar.DAY_OF_MONTH, diff);
        return cal;
    }


    static ThreadLocal<Integer> indent = new ThreadLocal<Integer>() {
        @Override
        protected Integer initialValue() {
            return 0
        }
    }

    @CompileStatic
    static def <T> T time(String name = "", Closure<T> worker) {
        if(!log.isDebugEnabled()) {
            return worker.call()
        }
        def padding = '    ' * indent.get()
        indent.set(++indent.get())
        log.debug "$padding ##### BenchmarkStart: {$name}"
        def start = System.currentTimeMillis()
        try {
            def rt = worker.call()
            def stop = System.currentTimeMillis()

            def time = TimeCategory.minus(new Date(stop), new Date(start))
            log.debug "$padding ###### Completed in {$name} in ${time}".toString()

            return rt
        } finally {
            indent.set(--indent.get())
        }

    }

    static String getBestMessage(Throwable x) {

        if (x instanceof ValidationException) {
            return ValidationException.formatErrors(x.errors)
        }

        if (x instanceof AssertionError) {
            def message = x.message
            if (message?.contains('Expression:'))
                return message.substring(0, message.indexOf('Expression:'))
        }

        def message = "${x.getClass().simpleName}: $x.message"

        if (message) return message
        return "Technical Error Please Contact Sytem Admin: $x"
    }

    /**
     * Safely retrieves a value from a record
     * @return
     */
    static String prop(Record mapper, String name, boolean required = true, String defaultValue = null) {
        if (required) {
            assert mapper.derivedHeaders.contains(name), "Record ${mapper.idx()} should have a [$name]"
        } else if (!mapper.derivedHeaders.contains(name)) {
            return null
        }

        def value = mapper.propertyMissing(name)?.toString()?.trim()
        if (required && !value) {
            if (defaultValue) return defaultValue
            throw new ImportException("Record [${mapper.idx()}] has an Empty Cell[$name] that is Required")
        }
        return value.removeExtraSpace()
    }


}
