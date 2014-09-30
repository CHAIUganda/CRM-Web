
<%@ page import="com.omnitech.chai.model.Parish" %>
<!DOCTYPE html>
<html>

<head>
	<meta name="layout" content="kickstart" />
	<g:set var="entityName" value="${message(code: 'parish.label', default: 'Parish')}" />
	<title><g:message code="default.show.label" args="[entityName]" /></title>
</head>

<body>

<section id="show-parish" class="first">

	<table class="table">
		<tbody>
		
			<tr class="prop">
				<td valign="top" class="name"><g:message code="parish.name.label" default="Name" /></td>
				
				<td valign="top" class="value">${fieldValue(bean: parishInstance, field: "name")}</td>
				
			</tr>
		
			<tr class="prop">
				<td valign="top" class="name"><g:message code="parish.subCounty.label" default="Sub County" /></td>
				
				<td valign="top" class="value"><g:link controller="subCounty" action="show" id="${parishInstance?.subCounty?.id}">${parishInstance?.subCounty?.encodeAsHTML()}</g:link></td>
				
			</tr>
		
		</tbody>
	</table>
</section>

</body>

</html>
