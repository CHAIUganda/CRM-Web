package chai.crm

import com.omnitech.chai.model.SalesCall
import com.omnitech.chai.model.Territory

class TaskGenerationJob {

    def taskService
    def regionService
    def customerService

    static triggers = {
        //execute every sunday at midnight
//        cron name: 'taskGenerator', startDelay: 10000, cronExpression: '0 0 0 ? * 1'
        //every day at 12am
        cron name: 'taskGenerator', startDelay: 10000, cronExpression: '0 0 0 1/1 * ? *'
//        cron name: 'taskGenerator', startDelay: 10000, cronExpression: '0 0/1 * 1/1 * ? *'
    }

    def execute() {
        println("#######################################################\n\n\t\tGenerating tasks for sales territories\n\n##########################################")
        def territories = regionService.findAllTerritoriesByType(Territory.TYPE_SALES)
        def segments = customerService.listAllCustomerSegments()

        segments.each { it.numberOfTasks = 2000 }

        taskService.generateTasks(territories, segments, (new Date() + 1095), [Calendar.MONDAY], 10, SalesCall, false)
        println("######################\nFinished Generating tasks\n##################################")
    }
}
