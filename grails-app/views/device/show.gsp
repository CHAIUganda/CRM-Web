
<%@ page import="com.omnitech.chai.model.Device" %>
<!DOCTYPE html>
<html>

<head>
	<meta name="layout" content="kickstart" />
	<g:set var="entityName" value="${message(code: 'device.label', default: 'Device')}" />
	<title><g:message code="default.show.label" args="[entityName]" /></title>
</head>

<body>

<section id="show-device" class="first">

	<table class="table">
		<tbody>
		
			<tr class="prop">
				<td valign="top" class="name"><g:message code="device.imei.label" default="Imei" /></td>
				
				<td valign="top" class="value">${fieldValue(bean: deviceInstance, field: "imei")}</td>
				
			</tr>
		
			<tr class="prop">
				<td valign="top" class="name"><g:message code="device.model.label" default="Model" /></td>
				
				<td valign="top" class="value">${fieldValue(bean: deviceInstance, field: "model")}</td>
				
			</tr>

        <tr class="prop">
            <td valign="top" class="name"><g:message code="device.user.label" default="Assigned User"/></td>

            <td valign="top" class="value">
                <g:if test="${deviceInstance.user}">
                    <g:link controller="user" id="">
                        ${fieldValue(bean: deviceInstance, field: "user")}
                    </g:link>
                </g:if>
            </td>

        </tr>

        </tbody>
	</table>
</section>

</body>

</html>
