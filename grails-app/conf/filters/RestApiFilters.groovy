package filters

import org.springframework.http.HttpStatus

/**
 * RestApiFilters
 * A filters class is used to execute code before and after a controller action is executed and also after a view is rendered
 */
class RestApiFilters {

    def userService
    def neoSecurityService

    def filters = {
        restApi(uri: '/rest/**', action: '*') {
            before = {

                def user = neoSecurityService.currentUser
                def deviceImei = request.getHeader('device-imei')

                def imeiOk = deviceImei && user?.device?.imei == deviceImei
                if (!imeiOk) {
                    response.sendError(HttpStatus.UNAUTHORIZED.value(), "You Are Not Allowed To Use This Device")
                }
                return imeiOk
            }
            after = { Map model ->

            }
            afterView = { Exception e ->

            }
        }
    }
}
