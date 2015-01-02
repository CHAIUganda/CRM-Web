
<%@ page import="com.omnitech.chai.model.Report" %>
<!DOCTYPE html>
<html>

<head>
	<meta name="layout" content="kickstart" />
	<g:set var="entityName" value="${message(code: 'report.label', default: 'Report')}" />
	<title><g:message code="default.show.label" args="[entityName]" /></title>
</head>

<body>

<section id="show-report" class="first">

	<table class="table">
		<tbody>
		
			<tr class="prop">
				<td valign="top" class="name"><g:message code="report._dateCreated.label" default="Date Created" /></td>
				
				<td valign="top" class="value">${fieldValue(bean: reportInstance, field: "_dateCreated")}</td>
				
			</tr>
		
			<tr class="prop">
				<td valign="top" class="name"><g:message code="report._dateLastUpdated.label" default="Date Last Updated" /></td>
				
				<td valign="top" class="value">${fieldValue(bean: reportInstance, field: "_dateLastUpdated")}</td>
				
			</tr>
		
			<tr class="prop">
				<td valign="top" class="name"><g:message code="report.dateCreated.label" default="Date Created" /></td>
				
				<td valign="top" class="value"><g:formatDate date="${reportInstance?.dateCreated}" /></td>
				
			</tr>
		
			<tr class="prop">
				<td valign="top" class="name"><g:message code="report.fields.label" default="Fields" /></td>
				
				<td valign="top" class="value">${fieldValue(bean: reportInstance, field: "fields")}</td>
				
			</tr>
		
			<tr class="prop">
				<td valign="top" class="name"><g:message code="report.group.label" default="Group" /></td>
				
				<td valign="top" class="value"><g:link controller="reportGroup" action="show" id="${reportInstance?.group?.id}">${reportInstance?.group?.encodeAsHTML()}</g:link></td>
				
			</tr>
		
			<tr class="prop">
				<td valign="top" class="name"><g:message code="report.lastUpdated.label" default="Last Updated" /></td>
				
				<td valign="top" class="value"><g:formatDate date="${reportInstance?.lastUpdated}" /></td>
				
			</tr>
		
			<tr class="prop">
				<td valign="top" class="name"><g:message code="report.name.label" default="Name" /></td>
				
				<td valign="top" class="value">${fieldValue(bean: reportInstance, field: "name")}</td>
				
			</tr>
		
			<tr class="prop">
				<td valign="top" class="name"><g:message code="report.script.label" default="Script" /></td>
				
				<td valign="top" class="value">${fieldValue(bean: reportInstance, field: "script")}</td>
				
			</tr>
		
			<tr class="prop">
				<td valign="top" class="name"><g:message code="report.type.label" default="Type" /></td>
				
				<td valign="top" class="value">${fieldValue(bean: reportInstance, field: "type")}</td>
				
			</tr>
		
			<tr class="prop">
				<td valign="top" class="name"><g:message code="report.uuid.label" default="Uuid" /></td>
				
				<td valign="top" class="value">${fieldValue(bean: reportInstance, field: "uuid")}</td>
				
			</tr>
		
		</tbody>
	</table>
</section>

</body>

</html>
