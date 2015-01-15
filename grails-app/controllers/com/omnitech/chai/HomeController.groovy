package com.omnitech.chai

/**
 * Created by kay on 1/14/2015.
 */
class HomeController {

    def dashBoardService

    def index(Integer max) {
        def summary = params.summary ?: 'detailing'
        def reportInfo = Collections.EMPTY_LIST
        if (summary == 'detailing') {
            reportInfo = dashBoardService.detailingReport()
        }
        [reportInfo: reportInfo]
    }
}
