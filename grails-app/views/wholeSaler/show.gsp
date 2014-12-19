
<%@ page import="com.omnitech.chai.model.WholeSaler" %>
<!DOCTYPE html>
<html>

<head>
	<meta name="layout" content="kickstart" />
	<g:set var="entityName" value="${message(code: 'wholeSaler.label', default: 'WholeSaler')}" />
	<title><g:message code="default.show.label" args="[entityName]" /></title>
</head>

<body>

<section id="show-wholeSaler" class="first">

	<table class="table">
		<tbody>
		
			<tr class="prop">
				<td valign="top" class="name"><g:message code="wholeSaler.contact.label" default="Contact" /></td>
				
				<td valign="top" class="value">${fieldValue(bean: wholeSalerInstance, field: "contact")}</td>
				
			</tr>
		
			<tr class="prop">
				<td valign="top" class="name"><g:message code="wholeSaler.name.label" default="Name" /></td>
				
				<td valign="top" class="value">${fieldValue(bean: wholeSalerInstance, field: "name")}</td>
				
			</tr>
		
		</tbody>
	</table>
</section>

</body>

</html>
