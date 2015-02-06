package com.omnitech.chai

import com.omnitech.chai.util.ChaiUtils
import grails.converters.JSON
import grails.validation.ValidationException

import static org.springframework.http.HttpStatus.BAD_REQUEST

/**
 * Created by kay on 2/5/2015.
 */
class BaseController {

    protected def handleSafely(def func) {
        try {
            def msg = func()
            render(msg ?: 'Success')
        } catch (ValidationException x) {
            def ms = new StringBuilder()
            x.errors.allErrors.each {
                ms << message(error: it)
            }
            log.error("** Error while handling request: $ms \n $params", x)
            render(status: BAD_REQUEST, text: [status: BAD_REQUEST.reasonPhrase, message: ms] as JSON)
        } catch (Throwable x) {
            log.error("Error while handling request: \n $params", x)
            render(status: BAD_REQUEST, text: [status: BAD_REQUEST.reasonPhrase, message: ChaiUtils.getBestMessage(x)] as JSON)
        }
    }
}
