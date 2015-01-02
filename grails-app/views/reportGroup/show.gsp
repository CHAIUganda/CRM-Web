
<%@ page import="com.omnitech.chai.model.ReportGroup" %>
<!DOCTYPE html>
<html>

<head>
	<meta name="layout" content="kickstart" />
	<g:set var="entityName" value="${message(code: 'reportGroup.label', default: 'ReportGroup')}" />
	<title><g:message code="default.show.label" args="[entityName]" /></title>
</head>

<body>

<section id="show-reportGroup" class="first">

	<table class="table">
		<tbody>
		
			<tr class="prop">
				<td valign="top" class="name"><g:message code="reportGroup._dateCreated.label" default="Date Created" /></td>
				
				<td valign="top" class="value">${fieldValue(bean: reportGroupInstance, field: "_dateCreated")}</td>
				
			</tr>
		
			<tr class="prop">
				<td valign="top" class="name"><g:message code="reportGroup._dateLastUpdated.label" default="Date Last Updated" /></td>
				
				<td valign="top" class="value">${fieldValue(bean: reportGroupInstance, field: "_dateLastUpdated")}</td>
				
			</tr>
		
			<tr class="prop">
				<td valign="top" class="name"><g:message code="reportGroup.dateCreated.label" default="Date Created" /></td>
				
				<td valign="top" class="value"><g:formatDate date="${reportGroupInstance?.dateCreated}" /></td>
				
			</tr>
		
			<tr class="prop">
				<td valign="top" class="name"><g:message code="reportGroup.lastUpdated.label" default="Last Updated" /></td>
				
				<td valign="top" class="value"><g:formatDate date="${reportGroupInstance?.lastUpdated}" /></td>
				
			</tr>
		
			<tr class="prop">
				<td valign="top" class="name"><g:message code="reportGroup.name.label" default="Name" /></td>
				
				<td valign="top" class="value">${fieldValue(bean: reportGroupInstance, field: "name")}</td>
				
			</tr>
		
			<tr class="prop">
				<td valign="top" class="name"><g:message code="reportGroup.parent.label" default="Parent" /></td>
				
				<td valign="top" class="value"><g:link controller="reportGroup" action="show" id="${reportGroupInstance?.parent?.id}">${reportGroupInstance?.parent?.encodeAsHTML()}</g:link></td>
				
			</tr>
		
			<tr class="prop">
				<td valign="top" class="name"><g:message code="reportGroup.uuid.label" default="Uuid" /></td>
				
				<td valign="top" class="value">${fieldValue(bean: reportGroupInstance, field: "uuid")}</td>
				
			</tr>
		
		</tbody>
	</table>
</section>

</body>

</html>
