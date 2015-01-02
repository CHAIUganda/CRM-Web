
<%@ page import="com.omnitech.chai.model.Report" %>
<!DOCTYPE html>
<html>

<head>
    <meta name="layout" content="kickstart"/>
    <g:set var="entityName" value="${message(code: 'report.label', default: 'Report')}"/>
    <title><g:message code="default.list.label" args="[entityName]"/></title>
</head>

<body>

<section id="list-report" class="first">

    <table class="table table-bordered margin-top-medium">
        <thead>
        <tr>
            
            <g:sortableColumn property="_dateCreated"
                              title="${message(code: 'report._dateCreated.label', default: 'Date Created')}"/>
            
            <g:sortableColumn property="_dateLastUpdated"
                              title="${message(code: 'report._dateLastUpdated.label', default: 'Date Last Updated')}"/>
            
            <g:sortableColumn property="dateCreated"
                              title="${message(code: 'report.dateCreated.label', default: 'Date Created')}"/>
            
            <g:sortableColumn property="fields"
                              title="${message(code: 'report.fields.label', default: 'Fields')}"/>
            
            <th><g:message code="report.group.label" default="Group"/></th>
            
            <g:sortableColumn property="lastUpdated"
                              title="${message(code: 'report.lastUpdated.label', default: 'Last Updated')}"/>
            
        </tr>
        </thead>
        <tbody>
        <g:each in="${reportInstanceList}" status="i" var="reportInstance">
            <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                
                <td><g:link action="show"
                            id="${reportInstance.id}">${fieldValue(bean: reportInstance, field: "_dateCreated")}</g:link></td>
                
                <td>${fieldValue(bean: reportInstance, field: "_dateLastUpdated")}</td>
                
                <td><g:formatDate date="${reportInstance.dateCreated}"/></td>
                
                <td>${fieldValue(bean: reportInstance, field: "fields")}</td>
                
                <td>${fieldValue(bean: reportInstance, field: "group")}</td>
                
                <td><g:formatDate date="${reportInstance.lastUpdated}"/></td>
                
            </tr>
        </g:each>
        </tbody>
    </table>

    <div>
        <bs:paginate total="${reportInstanceCount}"/>
    </div>
</section>

</body>

</html>
