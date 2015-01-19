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
        def reportInfo = Collections.EMPTY_LIST
        def salesInfo = Collections.EMPTY_LIST

        //admins
        if (user.hasRole(ADMIN_ROLE_NAME,
                SUPER_ADMIN_ROLE_NAME)) {
            reportInfo = dashBoardService.detailingReport()
        }

        if (!reportInfo && user.hasRole(DETAILER_ROLE_NAME,
                DETAILING_SUPERVISOR_ROLE_NAME)) {
            reportInfo = dashBoardService.detailingReport(user.id)
        }



        [reportInfo: reportInfo, salesInfo: salesInfo]
    }
}
