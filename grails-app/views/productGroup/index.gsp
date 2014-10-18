
<%@ page import="com.omnitech.chai.model.ProductGroup" %>
<!DOCTYPE html>
<html>

<head>
    <meta name="layout" content="kickstart" />
    <g:set var="entityName" value="${message(code: 'productGroup.label', default: 'ProductGroup')}" />
    <title><g:message code="default.index.label" args="[entityName]" /></title>
</head>

<body>

<section id="index-productGroup" class="first">

    <table class="table table-bordered margin-top-medium">
        <thead>
        <tr>
            
            <g:sortableColumn property="dateCreated" title="${message(code: 'productGroup.dateCreated.label', default: 'Date Created')}" />
            
            <g:sortableColumn property="lastUpdated" title="${message(code: 'productGroup.lastUpdated.label', default: 'Last Updated')}" />
            
            <g:sortableColumn property="name" title="${message(code: 'productGroup.name.label', default: 'Name')}" />
            
            <th><g:message code="productGroup.parent.label" default="Parent" /></th>
            
            <g:sortableColumn property="uuid" title="${message(code: 'productGroup.uuid.label', default: 'Uuid')}" />
            
            <td>
                Action
            </td>
        </tr>
        </thead>
        <tbody>
        <g:each in="${productGroupInstanceList}" status="i" var="productGroupInstance">
            <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                
                <td><g:link action="show" id="${productGroupInstance.id}">${fieldValue(bean: productGroupInstance, field: "dateCreated")}</g:link></td>
                
                <td><g:formatDate date="${productGroupInstance.lastUpdated}" format="dd-MMM-yyyy" /></td>
                
                <td>${fieldValue(bean: productGroupInstance, field: "name")}</td>
                
                <td>${fieldValue(bean: productGroupInstance, field: "parent")}</td>
                
                <td>${fieldValue(bean: productGroupInstance, field: "uuid")}</td>
                
                <td>
                    <g:link action="edit" id="${productGroupInstance.id}"><i
                            class="glyphicon glyphicon-pencil"></i></g:link>
                    <g:link action="delete" id="${productGroupInstance.id}"><i
                            class="glyphicon glyphicon-remove"></i></g:link>
                </td>
            </tr>
        </g:each>
        </tbody>
    </table>
    <div>
        <bs:paginate total="${productGroupInstanceCount}"
                     id="${params.action == 'search' ? (params.term ?: params.id) : null}"/>
    </div>
</section>

</body>

</html>
