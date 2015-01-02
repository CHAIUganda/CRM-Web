package com.omnitech.chai.crm

import com.omnitech.chai.model.Report
import com.omnitech.chai.model.ReportGroup
import com.omnitech.chai.util.ModelFunctions
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.neo4j.support.Neo4jTemplate
import org.springframework.data.neo4j.transaction.Neo4jTransactional

@Neo4jTransactional
class ReportService {

    def reportRepository
    def reportGroupRepository
    @Autowired
    Neo4jTemplate neo

    /* Reports */

    List<Report> listAllReports() { reportRepository.findAll().collect() }

    Page<Report> listReports(Map params) { ModelFunctions.listAll(reportRepository, params) }

    Report findReport(Long id) { reportRepository.findOne(id) }

    Report findReport(String uuid) { reportRepository.findByUuid(uuid) }

    Report saveReport(Report report) { reportRepository.save report }

    void deleteReport(Long id) { reportRepository.delete(id) }

    Page<Report> searchReports(String search, Map params) {
        ModelFunctions.searchAll(neo, Report, ModelFunctions.getWildCardRegex(search), params)
    }

    /* ReportGroups */

    List<ReportGroup> listAllReportGroups() { reportGroupRepository.findAll().collect() }

    Page<ReportGroup> listReportGroups(Map params) { ModelFunctions.listAll(reportGroupRepository, params) }

    ReportGroup findReportGroup(Long id) { reportGroupRepository.findOne(id) }

    ReportGroup findReportGroup(String uuid) { reportGroupRepository.findByUuid(uuid) }

    ReportGroup saveReportGroup(ReportGroup reportGroup) { reportGroupRepository.save reportGroup }

    void deleteReportGroup(Long id) { reportGroupRepository.delete(id) }

    Page<ReportGroup> searchReportGroups(String search, Map params) {
        ModelFunctions.searchAll(neo, ReportGroup, ModelFunctions.getWildCardRegex(search), params)
    }
}