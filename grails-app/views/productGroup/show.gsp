
<%@ page import="com.omnitech.chai.model.ProductGroup" %>
<!DOCTYPE html>
<html>

<head>
	<meta name="layout" content="kickstart" />
	<g:set var="entityName" value="${message(code: 'productGroup.label', default: 'ProductGroup')}" />
	<title><g:message code="default.show.label" args="[entityName]" /></title>
</head>

<body>

<section id="show-productGroup" class="first">

	<table class="table">
		<tbody>
		
			<tr class="prop">
				<td valign="top" class="name"><g:message code="productGroup.dateCreated.label" default="Date Created" /></td>
				
				<td valign="top" class="value"><g:formatDate date="${productGroupInstance?.dateCreated}" /></td>
				
			</tr>
		
			<tr class="prop">
				<td valign="top" class="name"><g:message code="productGroup.lastUpdated.label" default="Last Updated" /></td>
				
				<td valign="top" class="value"><g:formatDate date="${productGroupInstance?.lastUpdated}" /></td>
				
			</tr>
		
			<tr class="prop">
				<td valign="top" class="name"><g:message code="productGroup.name.label" default="Name" /></td>
				
				<td valign="top" class="value">${fieldValue(bean: productGroupInstance, field: "name")}</td>
				
			</tr>
		
			<tr class="prop">
				<td valign="top" class="name"><g:message code="productGroup.parent.label" default="Parent" /></td>
				
				<td valign="top" class="value"><g:link controller="productGroup" action="show" id="${productGroupInstance?.parent?.id}">${productGroupInstance?.parent?.encodeAsHTML()}</g:link></td>
				
			</tr>
		
			<tr class="prop">
				<td valign="top" class="name"><g:message code="productGroup.uuid.label" default="Uuid" /></td>
				
				<td valign="top" class="value">${fieldValue(bean: productGroupInstance, field: "uuid")}</td>
				
			</tr>
		
		</tbody>
	</table>
</section>

</body>

</html>
