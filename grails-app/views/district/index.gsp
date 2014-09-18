
<%@ page import="com.omnitech.mis.District" %>
<!DOCTYPE html>
<html>

<head>
	<meta name="layout" content="kickstart" />
	<g:set var="entityName" value="${message(code: 'district.label', default: 'District')}" />
	<title><g:message code="default.index.label" args="[entityName]" /></title>
</head>

<body>

<section id="index-district" class="first">

	<table class="table table-bordered margin-top-medium">
		<thead>
			<tr>
			
				<g:sortableColumn property="name" title="${message(code: 'district.name.label', default: 'Name')}" />
			
				<g:sortableColumn property="dateCreated" title="${message(code: 'district.dateCreated.label', default: 'Date Created')}" />
			
				<g:sortableColumn property="lastUpdated" title="${message(code: 'district.lastUpdated.label', default: 'Last Updated')}" />

                <th>Action</th>
			</tr>
		</thead>
		<tbody>
		<g:each in="${districtInstanceList}" status="i" var="districtInstance">
			<tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
			
				<td> ${fieldValue(bean: districtInstance, field: "name")} </td>
			
				<td><g:formatDate date="${districtInstance.dateCreated}"  format="dd-MMM-yyyy"/></td>
			
				<td><g:formatDate date="${districtInstance.lastUpdated}" format="dd-MMM-yyyy"/></td>

                <td><g:link action="edit" id="${districtInstance.id}"><i
                        class="glyphicon glyphicon-pencil"></i></g:link></td>
			</tr>
		</g:each>
		</tbody>
	</table>
	<div>
		<bs:paginate total="${districtInstanceCount}" />
	</div>
</section>

</body>

</html>
