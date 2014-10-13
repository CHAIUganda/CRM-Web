
<%@ page import="com.omnitech.chai.model.Territory" %>
<!DOCTYPE html>
<html>

<head>
    <meta name="layout" content="kickstart" />
    <g:set var="entityName" value="${message(code: 'territory.label', default: 'Territory')}" />
    <title><g:message code="default.index.label" args="[entityName]" /></title>
</head>

<body>

<section id="index-territory" class="first">

    <table class="table table-bordered margin-top-medium">
        <thead>
        <tr>
            
            <g:sortableColumn property="dateCreated" title="${message(code: 'territory.dateCreated.label', default: 'Date Created')}" />
            
            <g:sortableColumn property="lastUpdated" title="${message(code: 'territory.lastUpdated.label', default: 'Last Updated')}" />
            
            <g:sortableColumn property="name" title="${message(code: 'territory.name.label', default: 'Name')}" />
            
            <g:sortableColumn property="uuid" title="${message(code: 'territory.uuid.label', default: 'Uuid')}" />
            
            <td>
                Action
            </td>
        </tr>
        </thead>
        <tbody>
        <g:each in="${territoryInstanceList}" status="i" var="territoryInstance">
            <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                
                <td><g:link action="show" id="${territoryInstance.id}">${fieldValue(bean: territoryInstance, field: "dateCreated")}</g:link></td>
                
                <td><g:formatDate date="${territoryInstance.lastUpdated}" format="dd-MMM-yyyy" /></td>
                
                <td>${fieldValue(bean: territoryInstance, field: "name")}</td>
                
                <td>${fieldValue(bean: territoryInstance, field: "uuid")}</td>
                
                <td>
                    <g:link action="edit" id="${territoryInstance.id}"><i
                            class="glyphicon glyphicon-pencil"></i></g:link>
                    <g:link action="delete" id="${territoryInstance.id}"><i
                            class="glyphicon glyphicon-remove"></i></g:link>
                </td>
            </tr>
        </g:each>
        </tbody>
    </table>
    <div>
        <bs:paginate total="${territoryInstanceCount}"
                     id="${params.action == 'search' ? (params.term ?: params.id) : null}"/>
    </div>
</section>

</body>

</html>
