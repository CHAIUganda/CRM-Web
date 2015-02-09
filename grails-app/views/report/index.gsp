<%@ page import="com.omnitech.chai.model.Report" %>
<!DOCTYPE html>
<html>

<head>
    <meta name="layout" content="kickstart"/>
    <g:set var="entityName" value="${message(code: 'report.label', default: 'Report')}"/>
    <title><g:message code="default.index.label" args="[entityName]"/></title>
    <r:require module="jqueryTreeTable"/>
</head>

<body>

<section id="index-report" class="first">

    <table class="table table-bordered margin-top-medium treetable" id="tree-table">
        <thead>
        <tr>

            <g:sortableColumn property="name"
                              title="${message(code: 'report.name.label', default: 'Name')}"/>

            <g:sortableColumn property="dateCreated"
                              title="${message(code: 'report.dateCreated.label', default: 'Date Created')}"/>

            <g:sortableColumn property="lastUpdated"
                              title="${message(code: 'report.lastUpdated.label', default: 'Last Updated')}"/>

        </tr>
        </thead>
        <tbody>
        <g:each in="${reportInstanceList}" status="i" var="reportInstance">

            <g:if test="${reportInstance.isHead()}">
                <tr data-tt-id="${reportInstance.id}" class="${(i % 2) == 0 ? 'even' : 'odd'}">
            </g:if>
            <g:else>
                <tr data-tt-id="${reportInstance.id}" data-tt-parent-id="${reportInstance.parentId}"  class="${(i % 2) == 0 ? 'even' : 'odd'}">
            </g:else>

            <g:if test="${reportInstance.isGroup()}">
                <td>
                    <strong>${fieldValue(bean: reportInstance, field: "name")}</strong>
                </td>
                <td></td>

                <td></td>
            </g:if>
            <g:else>

                <td>
                    <g:if test="${reportInstance.element.type == Report.TYPE_DYNAMIC}">
                        <g:link action="reportWiz"
                                id="${reportInstance.id}">${fieldValue(bean: reportInstance, field: "name")}</g:link>
                    </g:if>

                    <g:elseif test="${reportInstance.element.type == Report.TYPE_SIMPLE_FILTER}">
                        <g:link action="simpleFilterWiz"
                                id="${reportInstance.id}">${fieldValue(bean: reportInstance, field: "name")}</g:link>
                    </g:elseif>

                    <g:elseif test="${reportInstance.element.type == Report.TYPE_STATIC}">
                        <g:link action="report"
                                id="${reportInstance.id}">${fieldValue(bean: reportInstance, field: "name")}</g:link>
                    </g:elseif>

                </td>

                <td><g:formatDate date="${reportInstance.dateCreated}" format="dd-MMM-yyyy"/></td>

                <td><g:formatDate date="${reportInstance.lastUpdated}" format="dd-MMM-yyyy"/></td>

            </g:else>


            </tr>
        </g:each>
        </tbody>
    </table>

    <div>
        <bs:paginate total="${reportInstanceCount}"
                     id="${params.action == 'search' ? (params.term ?: params.id) : null}"/>
    </div>
</section>
<g:javascript>
    $("#tree-table").treetable({expandable: true, initialState: "expanded"});
</g:javascript>
</body>

</html>
