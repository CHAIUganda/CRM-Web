
<%@ page import="com.omnitech.chai.model.ReportGroup" %>
<!DOCTYPE html>
<html>

<head>
    <meta name="layout" content="kickstart" />
    <g:set var="entityName" value="${message(code: 'reportGroup.label', default: 'ReportGroup')}" />
    <title><g:message code="default.index.label" args="[entityName]" /></title>
</head>

<body>

<section id="index-reportGroup" class="first">

    <table class="table table-bordered margin-top-medium">
        <thead>
        <tr>
            
            <g:sortableColumn property="_dateCreated" title="${message(code: 'reportGroup._dateCreated.label', default: 'Date Created')}" />
            
            <g:sortableColumn property="_dateLastUpdated" title="${message(code: 'reportGroup._dateLastUpdated.label', default: 'Date Last Updated')}" />
            
            <g:sortableColumn property="dateCreated" title="${message(code: 'reportGroup.dateCreated.label', default: 'Date Created')}" />
            
            <g:sortableColumn property="lastUpdated" title="${message(code: 'reportGroup.lastUpdated.label', default: 'Last Updated')}" />
            
            <g:sortableColumn property="name" title="${message(code: 'reportGroup.name.label', default: 'Name')}" />
            
            <th><g:message code="reportGroup.parent.label" default="Parent" /></th>
            
            <td>
                Action
            </td>
        </tr>
        </thead>
        <tbody>
        <g:each in="${reportGroupInstanceList}" status="i" var="reportGroupInstance">
            <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                
                <td><g:link action="show" id="${reportGroupInstance.id}">${fieldValue(bean: reportGroupInstance, field: "_dateCreated")}</g:link></td>
                
                <td>${fieldValue(bean: reportGroupInstance, field: "_dateLastUpdated")}</td>
                
                <td><g:formatDate date="${reportGroupInstance.dateCreated}" format="dd-MMM-yyyy" /></td>
                
                <td><g:formatDate date="${reportGroupInstance.lastUpdated}" format="dd-MMM-yyyy" /></td>
                
                <td>${fieldValue(bean: reportGroupInstance, field: "name")}</td>
                
                <td>${fieldValue(bean: reportGroupInstance, field: "parent")}</td>
                
                <td>
                    <g:link action="edit" id="${reportGroupInstance.id}"><i
                            class="glyphicon glyphicon-pencil"></i></g:link>
                    <g:link action="delete" id="${reportGroupInstance.id}"><i
                            class="glyphicon glyphicon-remove"></i></g:link>
                </td>
            </tr>
        </g:each>
        </tbody>
    </table>
    <div>
        <bs:paginate total="${reportGroupInstanceCount}"
                     id="${params.action == 'search' ? (params.term ?: params.id) : null}"/>
    </div>
</section>

</body>

</html>
