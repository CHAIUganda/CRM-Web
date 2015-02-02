<%@ page import="com.omnitech.chai.model.Role; com.omnitech.chai.model.User" %>
<!DOCTYPE html>
<html>

<head>
    <meta name="layout" content="kickstart"/>
    <g:set var="entityName" value="${message(code: 'user.label', default: 'User')}"/>
    <title><g:message code="default.show.label" args="[entityName]"/></title>
</head>

<body>

<section id="show-user" class="first">

    <table class="table">
        <tbody>

        <c:renderProperty label="Name" value="${userInstance?.name}"/>

        <tr class="prop">
            <td valign="top" class="name"><g:message code="user.username.label" default="Username"/></td>
            <td valign="top" class="value">${fieldValue(bean: userInstance, field: "username")}</td>
        </tr>

        <tr class="prop">
            <td valign="top" class="name"><g:message code="user.territory.label" default="Territory"/></td>

            <td valign="top" class="value">${fieldValue(bean: userInstance, field: "territory")}</td>

        </tr>

        <tr class="prop">
            <td valign="top" class="name"><g:message code="Device.label" default="Device"/></td>

            <td valign="top" class="value">${fieldValue(bean: userInstance, field: "device")}</td>
        </tr>

        <tr class="prop">
            <td valign="top" class="name"><g:message code="user.accountExpired.label" default="Account Expired"/></td>

            <td valign="top" class="value"><g:formatBoolean boolean="${userInstance?.accountExpired}"/></td>

        </tr>

        <tr class="prop">
            <td valign="top" class="name"><g:message code="user.accountLocked.label" default="Account Locked"/></td>

            <td valign="top" class="value"><g:formatBoolean boolean="${userInstance?.accountLocked}"/></td>

        </tr>

        <tr class="prop">
            <td valign="top" class="name"><g:message code="user.passwordExpired.label" default="Password Expired"/></td>

            <td valign="top" class="value"><g:formatBoolean boolean="${userInstance?.passwordExpired}"/></td>

        </tr>

        <tr class="prop">
            <td valign="top" class="name"><g:message code="user.enabled.label" default="Enabled"/></td>

            <td valign="top" class="value"><g:formatBoolean boolean="${userInstance?.enabled}"/></td>

        </tr>

        <tr class="prop">
            <td valign="top" class="name"><g:message code="user.dateCreated.label" default="Date Created"/></td>

            <td valign="top" class="value"><g:formatDate date="${userInstance?.dateCreated}"/></td>

        </tr>

        <tr class="prop">
            <td valign="top" class="name"><g:message code="user.lastUpdated.label" default="Last Updated"/></td>

            <td valign="top" class="value"><g:formatDate date="${userInstance?.lastUpdated}"/></td>

        </tr>

        %{--<g:if test="${userInstance?.hasRole(com.omnitech.chai.model.Role.SUPERVISOR_ROLE_NAME)}">--}%
        <tr class="prop">
            <td valign="top" class="name"><g:message code="user.superVisedTerritories.label"
                                                     default="Territories Supervised"/></td>

            <td valign="top" class="value">
                <g:each in="${userInstance?.supervisedTerritories}" var="t">
                    <div class="col-md-3">
                        <g:link controller="territory" action="show" id="${t.id}">
                            <i class="glyphicon glyphicon-arrow-right"></i> ${t}
                        </g:link>

                    </div>
                </g:each>
            </td>

        </tr>
        %{--</g:if>--}%

        </tbody>
    </table>
</section>

</body>

</html>
