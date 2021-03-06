
<%@ page import="com.omnitech.chai.model.SubCounty" %>
<!DOCTYPE html>
<html>

<head>
    <meta name="layout" content="kickstart" />
    <g:set var="entityName" value="${message(code: 'subCounty.label', default: 'SubCounty')}" />
    <title><g:message code="default.index.label" args="[entityName]" /></title>
    <r:require module="dataTable"/>
</head>

<body>

<section id="index-subCounty" class="first">

    <table class="table table-bordered margin-top-medium">
        <thead>
        <tr>

            <g:sortableColumn property="name" title="${message(code: 'subCounty.name.label', default: 'Name')}" />

            <g:sortableColumn property="district" title="${message(code: 'subCounty.district.label', default: 'District')}" />

            <th>Territory</th>

            <g:sortableColumn property="dateCreated" title="${message(code: 'subCounty.dateCreated.label', default: 'Date Created')}" />

            <g:sortableColumn property="lastUpdated" title="${message(code: 'subCounty.lastUpdated.label', default: 'Last Updated')}" />

            <th>Action </th>
        </tr>
        </thead>
        <tbody>
        <g:each in="${subCountyInstanceList}" status="i" var="subCountyInstance">
            <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">

                <td><g:link action="show" id="${subCountyInstance.id}">${fieldValue(bean: subCountyInstance, field: "name")}</g:link></td>

                <td>${fieldValue(bean: subCountyInstance, field: "district")}</td>

                <td>${fieldValue(bean: subCountyInstance, field: "territory")}</td>

                <td>${fieldValue(bean: subCountyInstance, field: "dateCreated")}</td>

                <td>${fieldValue(bean: subCountyInstance, field: "lastUpdated")}</td>
                <td>
                    <g:link action="edit" id="${subCountyInstance.id}"><i
                            class="glyphicon glyphicon-pencil"></i></g:link>
                </td>
            </tr>
        </g:each>
        </tbody>
    </table>
    <div>
        <div>
            <bs:paginate total="${subCountyInstanceCount}"
                         id="${params.action == 'search' ? (params.term ?: params.id) : null}"/>
        </div>
    </div>
</section>

</body>

</html>
