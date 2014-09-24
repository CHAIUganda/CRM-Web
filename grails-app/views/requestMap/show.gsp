
<%@ page import="com.omnitech.chai.model.RequestMap" %>
<!DOCTYPE html>
<html>

<head>
	<meta name="layout" content="kickstart" />
	<g:set var="entityName" value="${message(code: 'requestMap.label', default: 'RequestMap')}" />
	<title><g:message code="default.show.label" args="[entityName]" /></title>
</head>

<body>

<section id="show-requestMap" class="first">

	<table class="table">
		<tbody>
		
			<tr class="prop">
				<td valign="top" class="name"><g:message code="requestMap.url.label" default="Url" /></td>
				
				<td valign="top" class="value">${fieldValue(bean: requestMapInstance, field: "url")}</td>
				
			</tr>
		
			<tr class="prop">
				<td valign="top" class="name"><g:message code="requestMap.configAttribute.label" default="Config Attribute" /></td>
				
				<td valign="top" class="value">${fieldValue(bean: requestMapInstance, field: "configAttribute")}</td>
				
			</tr>
		
			<tr class="prop">
				<td valign="top" class="name"><g:message code="requestMap.httpMethod.label" default="Http Method" /></td>
				
				<td valign="top" class="value">${requestMapInstance?.httpMethod?.encodeAsHTML()}</td>
				
			</tr>
		
		</tbody>
	</table>
</section>

</body>

</html>
