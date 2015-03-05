
<%@ page import="com.omnitech.chai.model.CustomerSegment" %>
<!DOCTYPE html>
<html>

<head>
    <meta name="layout" content="kickstart" />
    <g:set var="entityName" value="${message(code: 'customerSegment.label', default: 'CustomerSegment')}" />
    <title><g:message code="default.index.label" args="[entityName]" /></title>
</head>

<body>

<section id="index-customerSegment" class="first">

    <table class="table table-bordered margin-top-medium">
        <thead>
        <tr>


            <g:sortableColumn property="name" title="${message(code: 'customerSegment.name.label', default: 'Name')}" />

            <g:sortableColumn property="callFrequency" title="${message(code: 'customerSegment.callFrequency.label', default: 'Call Frequency')}" />

            <g:sortableColumn property="callFrequency" title="${message(code: 'customerSegment.daysInPeriod.label', default: 'In Days')}" />

            <g:sortableColumn property="segmentationScript" title="${message(code: 'customerSegment.segmentationScript.label', default: 'Segmentation Script')}" />

            <g:sortableColumn property="dateCreated"
                              title="${message(code: 'customerSegment.dateCreated.label', default: 'Date Created')}"/>

            <g:sortableColumn property="lastUpdated"
                              title="${message(code: 'customerSegment.lastUpdated.label', default: 'Last Updated')}"/>
            <td>
                Action
            </td>
        </tr>
        </thead>
        <tbody>
        <g:each in="${customerSegmentInstanceList}" status="i" var="customerSegmentInstance">
            <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">

                <td><g:link action="show"
                            id="${customerSegmentInstance.id}">${fieldValue(bean: customerSegmentInstance, field: "name")}</g:link></td>


                <td>${fieldValue(bean: customerSegmentInstance, field: "callFrequency")} times in</td>

                <td>${fieldValue(bean: customerSegmentInstance, field: "daysInPeriod")} days</td>

                <td>${fieldValue(bean: customerSegmentInstance, field: "segmentationScript")}</td>


                <td><g:formatDate date="${customerSegmentInstance.dateCreated}" format="dd-MMM-yyyy"/></td>

                <td><g:formatDate date="${customerSegmentInstance.lastUpdated}" format="dd-MMM-yyyy"/></td>
                <td>
                    <g:link action="edit" id="${customerSegmentInstance.id}"><i
                            class="glyphicon glyphicon-pencil"></i></g:link>
                    <g:link action="delete" id="${customerSegmentInstance.id}"><i
                            class="glyphicon glyphicon-remove"></i></g:link>
                </td>

            </tr>
        </g:each>
        </tbody>
    </table>
    <div>
        <bs:paginate total="${customerSegmentInstanceCount}"
                     id="${params.action == 'search' ? (params.term ?: params.id) : null}"/>
    </div>
</section>

</body>

</html>
