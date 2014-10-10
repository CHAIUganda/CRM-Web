
<%@ page import="com.omnitech.chai.model.Customer" %>
<!DOCTYPE html>
<html>

<head>
    <meta name="layout" content="kickstart" />
    <g:set var="entityName" value="${message(code: 'customer.label', default: 'Customer')}" />
    <title><g:message code="default.index.label" args="[entityName]" /></title>
</head>

<body>

<section id="index-customer" class="first">

    <table class="table table-bordered margin-top-medium">
        <thead>
        <tr>
            
            <g:sortableColumn property="outletName" title="${message(code: 'customer.outletName.label', default: 'Outlet Name')}" />
            
            <g:sortableColumn property="outletType" title="${message(code: 'customer.outletType.label', default: 'Outlet Type')}" />

            <g:sortableColumn property="openingHours" title="${message(code: 'customer.openingHours.label', default: 'Opening Hours')}" />

            <g:sortableColumn property="dateCreated" title="${message(code: 'customer.dateCreated.label', default: 'Date Created')}" />
            
            <td>
                Action
            </td>
        </tr>
        </thead>
        <tbody>
        <g:each in="${customerInstanceList}" status="i" var="customerInstance">
            <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                
                <td><g:link action="show" id="${customerInstance.id}">${fieldValue(bean: customerInstance, field: "outletName")}</g:link></td>

                <td>${fieldValue(bean: customerInstance, field: "outletType")}</td>
                
                <td>${fieldValue(bean: customerInstance, field: "openingHours")}</td>

                <td><g:formatDate date="${customerInstance.dateCreated}" format="dd-MMM-yyyy" /></td>
                
                <td>
                    <g:link action="edit" id="${customerInstance.id}"><i
                            class="glyphicon glyphicon-pencil"></i></g:link>
                </td>
            </tr>
        </g:each>
        </tbody>
    </table>
    <div>
        <bs:paginate total="${customerInstanceCount}"
                     id="${params.action == 'search' ? (params.term ?: params.id) : null}"/>
    </div>
</section>

</body>

</html>
