
<%@ page import="com.omnitech.mis.UserRole" %>
<!DOCTYPE html>
<html>

<head>
    <meta name="layout" content="kickstart"/>
    <g:set var="entityName" value="${message(code: 'userRole.label', default: 'UserRole')}"/>
    <title><g:message code="default.list.label" args="[entityName]"/></title>
</head>

<body>

<section id="list-userRole" class="first">

    <table class="table table-bordered margin-top-medium">
        <thead>
        <tr>
            
            <th><g:message code="userRole.user.label" default="User"/></th>
            
            <th><g:message code="userRole.role.label" default="Role"/></th>
            
        </tr>
        </thead>
        <tbody>
        <g:each in="${userRoleInstanceList}" status="i" var="userRoleInstance">
            <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                
                <td><g:link action="show"
                            id="${userRoleInstance.id}">${fieldValue(bean: userRoleInstance, field: "user")}</g:link></td>
                
                <td>${fieldValue(bean: userRoleInstance, field: "role")}</td>
                
            </tr>
        </g:each>
        </tbody>
    </table>

    <div>
        <bs:paginate total="${userRoleInstanceCount}"/>
    </div>
</section>

</body>

</html>
