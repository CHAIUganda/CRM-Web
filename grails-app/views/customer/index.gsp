
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

    <table class="table table-bordered margin-top-medium" id="tree-table">
        <thead>
        <tr>

            <g:sortableColumn property="outletName" title="${message(code: 'customer.outletName.label', default: 'Outlet')}" />

            <g:sortableColumn property="outletType" title="${message(code: 'customer.outletType.label', default: 'Outlet Type')}" />

            <g:sortableColumn property="openingHours" title="${message(code: 'customer.openingHours.label', default: 'Opening Hours')}" />

            <g:sortableColumn property="dateCreated" title="${message(code: 'customer.dateCreated.label', default: 'Date Created')}" />

            <th>
                Action
            </th>
        </tr>
        </thead>
        <tbody>
        <g:each in="${customerInstanceList}" status="i" var="customerInstance">
            <g:if test="${customerInstance.isHead()}">
                <tr data-tt-id="${customerInstance.id}" class="${(i % 2) == 0 ? 'even' : 'odd'}">
            </g:if>
            <g:else>
                <tr data-tt-id="${customerInstance.id}" data-tt-parent-id="${customerInstance.parentId}"  class="${(i % 2) == 0 ? 'even' : 'odd'}">
            </g:else>

            <g:if test="${customerInstance.isGroup()}">
                <td>
                    <strong>Segment[${fieldValue(bean: customerInstance, field: "name")}]</strong>
                </td>
                <td></td>
                <td></td>
                <td></td>
                <td></td>
            </g:if>
            <g:else>

                <td><g:link action="show" id="${customerInstance.id}">${fieldValue(bean: customerInstance, field: "outletName")}</g:link></td>

                <td>${fieldValue(bean: customerInstance, field: "outletType")}</td>

                <td>${fieldValue(bean: customerInstance, field: "openingHours")}</td>

                <td><g:formatDate date="${customerInstance.dateCreated}" format="dd-MMM-yyyy" /></td>

                <td>
                    <g:link action="edit" id="${customerInstance.id}"><i
                            class="glyphicon glyphicon-pencil"></i></g:link>
                </td>
            </g:else>
            </tr>
        </g:each>
        </tbody>
    </table>
    <div>
        <bs:paginate total="${customerInstanceCount}"
                     id="${params.action == 'search' ? (params.term ?: params.id) : null}"/>
    </div>
</section>
<r:require module="jqueryTreeTable"/>
<g:javascript>
    $("#tree-table").treetable({ expandable: true, initialState: "expanded" });
</g:javascript>
</body>

</html>
