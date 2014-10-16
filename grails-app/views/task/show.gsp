
<%@ page import="com.omnitech.chai.model.Task" %>
<!DOCTYPE html>
<html>

<head>
	<meta name="layout" content="kickstart" />
	<g:set var="entityName" value="${message(code: 'task.label', default: 'Task')}" />
	<title><g:message code="default.show.label" args="[entityName]" /></title>
</head>

<body>

<section id="show-task" class="first">

	<table class="table">
		<tbody>
		
			<tr class="prop">
				<td valign="top" class="name"><g:message code="task.dateCreated.label" default="Date Created" /></td>
				
				<td valign="top" class="value"><g:formatDate date="${taskInstance?.dateCreated}" /></td>
				
			</tr>
		
			<tr class="prop">
				<td valign="top" class="name"><g:message code="task.description.label" default="Description" /></td>
				
				<td valign="top" class="value">${fieldValue(bean: taskInstance, field: "description")}</td>
				
			</tr>
		
			<tr class="prop">
				<td valign="top" class="name"><g:message code="task.lastUpdated.label" default="Last Updated" /></td>
				
				<td valign="top" class="value"><g:formatDate date="${taskInstance?.lastUpdated}" /></td>
				
			</tr>
		
			<tr class="prop">
				<td valign="top" class="name"><g:message code="task.status.label" default="Status" /></td>
				
				<td valign="top" class="value">${fieldValue(bean: taskInstance, field: "status")}</td>
				
			</tr>
		
			<tr class="prop">
				<td valign="top" class="name"><g:message code="task.uuid.label" default="Uuid" /></td>
				
				<td valign="top" class="value">${fieldValue(bean: taskInstance, field: "uuid")}</td>
				
			</tr>
		
		</tbody>
	</table>
</section>

</body>

</html>
