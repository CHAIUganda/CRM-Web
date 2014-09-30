
<%@ page import="com.omnitech.chai.model.Village" %>
<!DOCTYPE html>
<html>

<head>
	<meta name="layout" content="kickstart" />
	<g:set var="entityName" value="${message(code: 'village.label', default: 'Village')}" />
	<title><g:message code="default.show.label" args="[entityName]" /></title>
</head>

<body>

<section id="show-village" class="first">

	<table class="table">
		<tbody>
		
			<tr class="prop">
				<td valign="top" class="name"><g:message code="village.name.label" default="Name" /></td>
				
				<td valign="top" class="value">${fieldValue(bean: villageInstance, field: "name")}</td>
				
			</tr>
		
			<tr class="prop">
				<td valign="top" class="name"><g:message code="village.parish.label" default="Parish" /></td>
				
				<td valign="top" class="value"><g:link controller="parish" action="show" id="${villageInstance?.parish?.id}">${villageInstance?.parish?.encodeAsHTML()}</g:link></td>
				
			</tr>
		
		</tbody>
	</table>
</section>

</body>

</html>
