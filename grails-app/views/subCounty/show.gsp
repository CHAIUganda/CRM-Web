
<%@ page import="com.omnitech.chai.model.SubCounty" %>
<!DOCTYPE html>
<html>

<head>
	<meta name="layout" content="kickstart" />
	<g:set var="entityName" value="${message(code: 'subCounty.label', default: 'SubCounty')}" />
	<title><g:message code="default.show.label" args="[entityName]" /></title>
</head>

<body>

<section id="show-subCounty" class="first">

	<table class="table">
		<tbody>

            <tr class="prop">
                <td valign="top" class="name"><g:message code="subCounty.district.label" default="District" /></td>

                <td valign="top" class="value">${fieldValue(bean: subCountyInstance, field: "district")}</td>

            </tr>

        <tr class="prop">
                <td valign="top" class="name"><g:message code="subCounty.territory.label" default="Territory" /></td>

                <td valign="top" class="value">${fieldValue(bean: subCountyInstance, field: "territory")}</td>

            </tr>

        <tr class="prop">
            <td valign="top" class="name"><g:message code="subCounty.name.label" default="SubCounty" /></td>

            <td valign="top" class="value">${fieldValue(bean: subCountyInstance, field: "name")}</td>

        </tr>

            <tr class="prop">
				<td valign="top" class="name"><g:message code="subCounty.dateCreated.label" default="Date Created" /></td>

				<td valign="top" class="value"><g:formatDate date="${subCountyInstance?.dateCreated}" /></td>

			</tr>

			<tr class="prop">
				<td valign="top" class="name"><g:message code="subCounty.lastUpdated.label" default="Last Updated" /></td>

				<td valign="top" class="value"><g:formatDate date="${subCountyInstance?.lastUpdated}" /></td>

			</tr>

			<tr class="prop">
				<td valign="top" class="name"><g:message code="subCounty.uuid.label" default="Uuid" /></td>

				<td valign="top" class="value">${fieldValue(bean: subCountyInstance, field: "uuid")}</td>

			</tr>

		</tbody>
	</table>
</section>

</body>

</html>
