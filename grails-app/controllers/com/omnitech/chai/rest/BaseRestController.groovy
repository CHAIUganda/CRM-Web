package com.omnitech.chai.rest

import com.omnitech.chai.model.Task

/**
 * Created by kay on 3/2/2015.
 */
class BaseRestController {

    def neoSecurityService

    protected updateCompletionInfo(Task task) {

        def user = neoSecurityService.currentUser
        def completeTimeStamp = request.JSON.completionDate

        if (completeTimeStamp) {
            try {
                def completeDate = new Date(completeTimeStamp as Long)
                task.setCompletionDate(completeDate)
            } catch (Exception x) {
                log.warn("Erratic completion date..[$completeTimeStamp] going to set default date..!!")
                task.setCompletionDate(new Date())
            }
        }

        if (task.isCancelled()) {
            task.cancelledBy(user)
        } else {
            task.completedBy(user)
        }
    }
}
