
<%@ page import="com.omnitech.mis.SubCounty" %>
<!DOCTYPE html>
<html>

<head>
	<meta name="layout" content="kickstart" />
	<g:set var="entityName" value="${message(code: 'subCounty.label', default: 'SubCounty')}" />
	<title><g:message code="default.index.label" args="[entityName]" /></title>
</head>

<body>

<section id="index-subCounty" class="first">

	<table class="table table-bordered margin-top-medium">
		<thead>
			<tr>

                <g:sortableColumn property="name" title="${message(code: 'subCounty.name.label', default: 'Name')}" />

                <th><g:message code="subCounty.district.label" default="District" /></th>

                <g:sortableColumn property="dateCreated" title="${message(code: 'subCounty.dateCreated.label', default: 'Date Created')}" />

				<g:sortableColumn property="lastUpdated" title="${message(code: 'subCounty.lastUpdated.label', default: 'Last Updated')}" />

                <th>Action</th>
			</tr>
		</thead>
		<tbody>
		<g:each in="${subCountyInstanceList}" status="i" var="subCountyInstance">
			<tr class="${(i % 2) == 0 ? 'odd' : 'even'}">

                <td>${fieldValue(bean: subCountyInstance, field: "district")}</td>

                <td>${fieldValue(bean: subCountyInstance, field: "name")}</td>

                <td><g:formatDate date="${subCountyInstance.dateCreated}" format="dd-MMM-yyyy"/></td>

				<td><g:formatDate date="${subCountyInstance.lastUpdated}" format="dd-MMM-yyyy"/></td>

                <td><g:link action="edit" id="${subCountyInstance.id}"><i
                        class="glyphicon glyphicon-pencil"></i></g:link></td>

			</tr>
		</g:each>
		</tbody>
	</table>
	<div>
		<bs:paginate total="${subCountyInstanceCount}" />
	</div>
</section>

</body>

</html>
