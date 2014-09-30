
<%@ page import="com.omnitech.chai.model.District" %>
<!DOCTYPE html>
<html>

<head>
	<meta name="layout" content="kickstart" />
	<g:set var="entityName" value="${message(code: 'district.label', default: 'District')}" />
	<title><g:message code="default.show.label" args="[entityName]" /></title>
</head>

<body>

<section id="show-district" class="first">

	<table class="table">
		<tbody>


            <tr class="prop">
                <td valign="top" class="name"><g:message code="Region.label" default="Region" /></td>

                <td valign="top" class="value">${fieldValue(bean: districtInstance, field: "region")}</td>

            </tr>


			<tr class="prop">
				<td valign="top" class="name"><g:message code="district.name.label" default="Name" /></td>
				
				<td valign="top" class="value">${fieldValue(bean: districtInstance, field: "name")}</td>
				
			</tr>
		
			<tr class="prop">
				<td valign="top" class="name"><g:message code="district.uuid.label" default="Uuid" /></td>
				
				<td valign="top" class="value">${fieldValue(bean: districtInstance, field: "uuid")}</td>
				
			</tr>

        <tr class="prop">
            <td valign="top" class="name"><g:message code="district.dateCreated.label" default="Date Created" /></td>

            <td valign="top" class="value"><g:formatDate date="${districtInstance?.dateCreated}" /></td>

        </tr>

        <tr class="prop">
            <td valign="top" class="name"><g:message code="district.lastUpdated.label" default="Last Updated" /></td>

            <td valign="top" class="value"><g:formatDate date="${districtInstance?.lastUpdated}" /></td>

        </tr>

		</tbody>
	</table>
</section>

</body>

</html>
