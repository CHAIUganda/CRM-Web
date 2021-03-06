
<%@ page import="com.omnitech.chai.model.Region" %>
<!DOCTYPE html>
<html>

<head>
    <meta name="layout" content="kickstart" />
    <g:set var="entityName" value="${message(code: 'region.label', default: 'Region')}" />
    <title><g:message code="default.index.label" args="[entityName]" /></title>
    <r:require module="dataTable"/>
</head>

<body>

<section id="index-region" class="first">

    <table class="table table-bordered margin-top-medium">
        <thead>
        <tr>
            
            <g:sortableColumn property="dateCreated" title="${message(code: 'region.dateCreated.label', default: 'Date Created')}" />
            
            <g:sortableColumn property="lastUpdated" title="${message(code: 'region.lastUpdated.label', default: 'Last Updated')}" />
            
            <g:sortableColumn property="name" title="${message(code: 'region.name.label', default: 'Name')}" />
            
            <g:sortableColumn property="uuid" title="${message(code: 'region.uuid.label', default: 'Uuid')}" />
            
            <td>
                Action
            </td>
        </tr>
        </thead>
        <tbody>
        <g:each in="${regionInstanceList}" status="i" var="regionInstance">
            <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                
                <td><g:link action="show" id="${regionInstance.id}">${fieldValue(bean: regionInstance, field: "dateCreated")}</g:link></td>
                
                <td><g:formatDate date="${regionInstance.lastUpdated}" format="dd-MMM-yyyy" /></td>
                
                <td>${fieldValue(bean: regionInstance, field: "name")}</td>
                
                <td>${fieldValue(bean: regionInstance, field: "uuid")}</td>
                
                <td>
                    <g:link action="edit" id="${regionInstance.id}"><i
                            class="glyphicon glyphicon-pencil"></i></g:link>
                    <g:link action="delete" id="${regionInstance.id}"><i
                            class="glyphicon glyphicon-remove"></i></g:link>
                </td>
            </tr>
        </g:each>
        </tbody>
    </table>
    <div>
        <bs:paginate total="${regionInstanceCount}" />
    </div>
</section>

</body>

</html>
