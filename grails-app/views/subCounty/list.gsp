
<%@ page import="com.omnitech.chai.model.SubCounty" %>
<!DOCTYPE html>
<html>

<head>
    <meta name="layout" content="kickstart"/>
    <g:set var="entityName" value="${message(code: 'subCounty.label', default: 'SubCounty')}"/>
    <title><g:message code="default.list.label" args="[entityName]"/></title>
</head>

<body>

<section id="list-subCounty" class="first">

    <table class="table table-bordered margin-top-medium">
        <thead>
        <tr>
            
            <g:sortableColumn property="dateCreated"
                              title="${message(code: 'subCounty.dateCreated.label', default: 'Date Created')}"/>
            
            <g:sortableColumn property="district"
                              title="${message(code: 'subCounty.district.label', default: 'District')}"/>
            
            <g:sortableColumn property="lastUpdated"
                              title="${message(code: 'subCounty.lastUpdated.label', default: 'Last Updated')}"/>
            
            <g:sortableColumn property="name"
                              title="${message(code: 'subCounty.name.label', default: 'Name')}"/>
            
            <g:sortableColumn property="uuid"
                              title="${message(code: 'subCounty.uuid.label', default: 'Uuid')}"/>
            
        </tr>
        </thead>
        <tbody>
        <g:each in="${subCountyInstanceList}" status="i" var="subCountyInstance">
            <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                
                <td><g:link action="show"
                            id="${subCountyInstance.id}">${fieldValue(bean: subCountyInstance, field: "dateCreated")}</g:link></td>
                
                <td>${fieldValue(bean: subCountyInstance, field: "district")}</td>
                
                <td><g:formatDate date="${subCountyInstance.lastUpdated}"/></td>
                
                <td>${fieldValue(bean: subCountyInstance, field: "name")}</td>
                
                <td>${fieldValue(bean: subCountyInstance, field: "uuid")}</td>
                
            </tr>
        </g:each>
        </tbody>
    </table>

    <div>
        <bs:paginate total="${subCountyInstanceCount}"/>
    </div>
</section>

</body>

</html>
