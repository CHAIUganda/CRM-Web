package com.omnitech.chai

import static com.omnitech.chai.model.Role.*

/**
 * Created by kay on 1/14/2015.
 */
class HomeController {

    def dashBoardService
    def neoSecurityService

    def index(Integer max) {

        def user = neoSecurityService.currentUser
        def detailingInfo = Collections.EMPTY_LIST
        def salesInfo = Collections.EMPTY_LIST

        //admins
        if (user.hasRole(ADMIN_ROLE_NAME, SUPER_ADMIN_ROLE_NAME)) {
            detailingInfo = dashBoardService.detailingReport()
            salesInfo = dashBoardService.salesReport()
        }

        if (!detailingInfo && user.hasRole(DETAILER_ROLE_NAME, DETAILING_SUPERVISOR_ROLE_NAME)) {
            detailingInfo = dashBoardService.detailingReport(user.id)
        }

        if (!salesInfo && user.hasRole(SALES_SUPERVISOR_ROLE_NAME, SALES_ROLE_NAME)) {
            salesInfo = dashBoardService.salesReport(user.id)
        }

        [detailingInfo: detailingInfo, salesInfo: salesInfo]
    }
}
