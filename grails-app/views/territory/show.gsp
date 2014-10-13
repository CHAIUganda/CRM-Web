
<%@ page import="com.omnitech.chai.model.Territory" %>
<!DOCTYPE html>
<html>

<head>
	<meta name="layout" content="kickstart" />
	<g:set var="entityName" value="${message(code: 'territory.label', default: 'Territory')}" />
	<title><g:message code="default.show.label" args="[entityName]" /></title>
</head>

<body>

<section id="show-territory" class="first">

	<table class="table">
		<tbody>
		
			<tr class="prop">
				<td valign="top" class="name"><g:message code="territory.dateCreated.label" default="Date Created" /></td>
				
				<td valign="top" class="value"><g:formatDate date="${territoryInstance?.dateCreated}" /></td>
				
			</tr>
		
			<tr class="prop">
				<td valign="top" class="name"><g:message code="territory.lastUpdated.label" default="Last Updated" /></td>
				
				<td valign="top" class="value"><g:formatDate date="${territoryInstance?.lastUpdated}" /></td>
				
			</tr>
		
			<tr class="prop">
				<td valign="top" class="name"><g:message code="territory.name.label" default="Name" /></td>
				
				<td valign="top" class="value">${fieldValue(bean: territoryInstance, field: "name")}</td>
				
			</tr>
		
			<tr class="prop">
				<td valign="top" class="name"><g:message code="territory.uuid.label" default="Uuid" /></td>
				
				<td valign="top" class="value">${fieldValue(bean: territoryInstance, field: "uuid")}</td>
				
			</tr>
		
		</tbody>
	</table>
</section>

</body>

</html>
