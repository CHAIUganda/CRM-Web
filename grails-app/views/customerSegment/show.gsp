
<%@ page import="com.omnitech.chai.model.CustomerSegment" %>
<!DOCTYPE html>
<html>

<head>
	<meta name="layout" content="kickstart" />
	<g:set var="entityName" value="${message(code: 'customerSegment.label', default: 'CustomerSegment')}" />
	<title><g:message code="default.show.label" args="[entityName]" /></title>
</head>

<body>

<section id="show-customerSegment" class="first">

	<table class="table">
		<tbody>
		
			<tr class="prop">
				<td valign="top" class="name"><g:message code="customerSegment.callFrequency.label" default="Call Frequency" /></td>
				
				<td valign="top" class="value">${fieldValue(bean: customerSegmentInstance, field: "callFrequency")}</td>
				
			</tr>
		
			<tr class="prop">
				<td valign="top" class="name"><g:message code="customerSegment.dateCreated.label" default="Date Created" /></td>
				
				<td valign="top" class="value"><g:formatDate date="${customerSegmentInstance?.dateCreated}" /></td>
				
			</tr>
		
			<tr class="prop">
				<td valign="top" class="name"><g:message code="customerSegment.lastUpdated.label" default="Last Updated" /></td>
				
				<td valign="top" class="value"><g:formatDate date="${customerSegmentInstance?.lastUpdated}" /></td>
				
			</tr>
		
			<tr class="prop">
				<td valign="top" class="name"><g:message code="customerSegment.name.label" default="Name" /></td>
				
				<td valign="top" class="value">${fieldValue(bean: customerSegmentInstance, field: "name")}</td>
				
			</tr>
		
			<tr class="prop">
				<td valign="top" class="name"><g:message code="customerSegment.segmentationScript.label" default="Segmentation Script" /></td>
				
				<td valign="top" class="value">${fieldValue(bean: customerSegmentInstance, field: "segmentationScript")}</td>
				
			</tr>
		
			<tr class="prop">
				<td valign="top" class="name"><g:message code="customerSegment.taskGeneratorScript.label" default="Tesk Generator Script" /></td>
				
				<td valign="top" class="value">${fieldValue(bean: customerSegmentInstance, field: "taskGeneratorScript")}</td>
				
			</tr>
		
			<tr class="prop">
				<td valign="top" class="name"><g:message code="customerSegment.uuid.label" default="Uuid" /></td>
				
				<td valign="top" class="value">${fieldValue(bean: customerSegmentInstance, field: "uuid")}</td>
				
			</tr>
		
		</tbody>
	</table>
</section>

</body>

</html>
