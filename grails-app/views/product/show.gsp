
<%@ page import="com.omnitech.chai.model.Product" %>
<!DOCTYPE html>
<html>

<head>
	<meta name="layout" content="kickstart" />
	<g:set var="entityName" value="${message(code: 'product.label', default: 'Product')}" />
	<title><g:message code="default.show.label" args="[entityName]" /></title>
</head>

<body>

<section id="show-product" class="first">

	<table class="table">
		<tbody>
		
			<tr class="prop">
				<td valign="top" class="name"><g:message code="product.metric.label" default="Metric" /></td>
				
				<td valign="top" class="value">${fieldValue(bean: productInstance, field: "metric")}</td>
				
			</tr>
		
			<tr class="prop">
				<td valign="top" class="name"><g:message code="product.name.label" default="Name" /></td>
				
				<td valign="top" class="value">${fieldValue(bean: productInstance, field: "name")}</td>
				
			</tr>
		
			<tr class="prop">
				<td valign="top" class="name"><g:message code="product.unitPrice.label" default="Unit Price" /></td>
				
				<td valign="top" class="value">${fieldValue(bean: productInstance, field: "unitPrice")}</td>
				
			</tr>
		
		</tbody>
	</table>
</section>

</body>

</html>