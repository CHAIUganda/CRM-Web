
<%@ page import="com.omnitech.mis.Parish" %>
<!DOCTYPE html>
<html>

<head>
    <meta name="layout" content="kickstart"/>
    <g:set var="entityName" value="${message(code: 'parish.label', default: 'Parish')}"/>
    <title><g:message code="default.list.label" args="[entityName]"/></title>
</head>

<body>

<section id="list-parish" class="first">

    <table class="table table-bordered margin-top-medium">
        <thead>
        <tr>
            
            <g:sortableColumn property="dateCreated"
                              title="${message(code: 'parish.dateCreated.label', default: 'Date Created')}"/>
            
            <g:sortableColumn property="lastUpdated"
                              title="${message(code: 'parish.lastUpdated.label', default: 'Last Updated')}"/>
            
            <g:sortableColumn property="name"
                              title="${message(code: 'parish.name.label', default: 'Name')}"/>
            
            <th><g:message code="parish.subcounty.label" default="Subcounty"/></th>
            
        </tr>
        </thead>
        <tbody>
        <g:each in="${parishInstanceList}" status="i" var="parishInstance">
            <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                
                <td><g:link action="show"
                            id="${parishInstance.id}">${fieldValue(bean: parishInstance, field: "dateCreated")}</g:link></td>
                
                <td><g:formatDate date="${parishInstance.lastUpdated}"/></td>
                
                <td>${fieldValue(bean: parishInstance, field: "name")}</td>
                
                <td>${fieldValue(bean: parishInstance, field: "subcounty")}</td>
                
            </tr>
        </g:each>
        </tbody>
    </table>

    <div>
        <bs:paginate total="${parishInstanceCount}"/>
    </div>
</section>

</body>

</html>
