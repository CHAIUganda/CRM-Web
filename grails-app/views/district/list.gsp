
<%@ page import="com.omnitech.mis.District" %>
<!DOCTYPE html>
<html>

<head>
    <meta name="layout" content="kickstart"/>
    <g:set var="entityName" value="${message(code: 'district.label', default: 'District')}"/>
    <title><g:message code="default.list.label" args="[entityName]"/></title>
</head>

<body>

<section id="list-district" class="first">

    <table class="table table-bordered margin-top-medium">
        <thead>
        <tr>
            
            <g:sortableColumn property="name"
                              title="${message(code: 'district.name.label', default: 'Name')}"/>
            
            <g:sortableColumn property="dateCreated"
                              title="${message(code: 'district.dateCreated.label', default: 'Date Created')}"/>
            
            <g:sortableColumn property="lastUpdated"
                              title="${message(code: 'district.lastUpdated.label', default: 'Last Updated')}"/>
            
        </tr>
        </thead>
        <tbody>
        <g:each in="${districtInstanceList}" status="i" var="districtInstance">
            <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                
                <td><g:link action="show"
                            id="${districtInstance.id}">${fieldValue(bean: districtInstance, field: "name")}</g:link></td>
                
                <td><g:formatDate date="${districtInstance.dateCreated}"/></td>
                
                <td><g:formatDate date="${districtInstance.lastUpdated}"/></td>
                
            </tr>
        </g:each>
        </tbody>
    </table>

    <div>
        <bs:paginate total="${districtInstanceCount}"/>
    </div>
</section>

</body>

</html>
