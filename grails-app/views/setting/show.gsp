
<%@ page import="com.omnitech.chai.model.Setting" %>
<!DOCTYPE html>
<html>

<head>
	<meta name="layout" content="kickstart" />
	<g:set var="entityName" value="${message(code: 'setting.label', default: 'Setting')}" />
	<title><g:message code="default.show.label" args="[entityName]" /></title>
</head>

<body>

<section id="show-setting" class="first">

	<table class="table">
		<tbody>
		
			<tr class="prop">
				<td valign="top" class="name"><g:message code="setting.dateCreated.label" default="Date Created" /></td>
				
				<td valign="top" class="value"><g:formatDate date="${settingInstance?.dateCreated}" /></td>
				
			</tr>
		
			<tr class="prop">
				<td valign="top" class="name"><g:message code="setting.lastUpdated.label" default="Last Updated" /></td>
				
				<td valign="top" class="value"><g:formatDate date="${settingInstance?.lastUpdated}" /></td>
				
			</tr>
		
			<tr class="prop">
				<td valign="top" class="name"><g:message code="setting.name.label" default="Name" /></td>
				
				<td valign="top" class="value">${fieldValue(bean: settingInstance, field: "name")}</td>
				
			</tr>
		
			<tr class="prop">
				<td valign="top" class="name"><g:message code="setting.uuid.label" default="Uuid" /></td>
				
				<td valign="top" class="value">${fieldValue(bean: settingInstance, field: "uuid")}</td>
				
			</tr>
		
			<tr class="prop">
				<td valign="top" class="name"><g:message code="setting.value.label" default="Value" /></td>
				
				<td valign="top" class="value">${fieldValue(bean: settingInstance, field: "value")}</td>
				
			</tr>
		
		</tbody>
	</table>
</section>

</body>

</html>
