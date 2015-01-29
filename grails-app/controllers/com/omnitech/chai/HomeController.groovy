package com.omnitech.chai

import static com.omnitech.chai.model.Role.*

/**
 * Created by kay on 1/14/2015.
 */
class HomeController {

    def dashBoardService
    def neoSecurityService

    def index(Integer max) {

        def startDate = getDate(params.startDate, new Date() - 30)
        def endDate = getDate(params.endDate, new Date())

        def user = neoSecurityService.currentUser
        def detailingInfo = Collections.EMPTY_LIST
        def salesInfo = Collections.EMPTY_LIST

        //admins
        if (user.hasRole(ADMIN_ROLE_NAME, SUPER_ADMIN_ROLE_NAME)) {
            detailingInfo = dashBoardService.detailingReport(startDate, endDate+1)
            salesInfo = dashBoardService.salesReport(startDate, endDate+1)
        }

        if (!detailingInfo && user.hasRole(DETAILER_ROLE_NAME, DETAILING_SUPERVISOR_ROLE_NAME)) {
            detailingInfo = dashBoardService.detailingReport(user.id, startDate, endDate+1)
        }

        if (!salesInfo && user.hasRole(SALES_SUPERVISOR_ROLE_NAME, SALES_ROLE_NAME)) {
            salesInfo = dashBoardService.salesReport(user.id, startDate, endDate+1)
        }

        [detailingInfo: detailingInfo, salesInfo: salesInfo, startDate: startDate, endDate: endDate]
    }

    private Date getDate(String date, Date defaultValue) {
        if (date) {
            try {
                return Date.parse('yyyy-MM-dd', date)
            } catch (Exception x) {
                log.error("Failed to parse date: $date: $x.message")
            }
        }
        return defaultValue
    }
}
