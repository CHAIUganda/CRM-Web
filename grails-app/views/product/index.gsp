<%@ page import="com.omnitech.chai.model.Product" %>
<!DOCTYPE html>
<html>

<head>
    <meta name="layout" content="kickstart"/>
    <g:set var="entityName" value="${message(code: 'product.label', default: 'Product')}"/>
    <title><g:message code="default.index.label" args="[entityName]"/></title>
    <r:require module="jqueryTreeTable"/>
</head>

<body>

<section id="index-product" class="first">

    <table class="table table-bordered margin-top-medium treetable" id="tree-table">
        <thead>
        <tr>
            <g:sortableColumn property="name" title="${message(code: 'product.name.label', default: 'Name')}"/>

            <g:sortableColumn property="metric"
                              title="${message(code: 'product.unitOfMeasure.label', default: 'Measure')}"/>

            <g:sortableColumn property="metric"
                              title="${message(code: 'product.formulation.label', default: 'Formulation')}"/>

            <g:sortableColumn property="unitPrice"
                              title="${message(code: 'product.unitPrice.label', default: 'Unit Price')}"/>

            <td>
                Action
            </td>
        </tr>
        </thead>
        <tbody>
        <g:each in="${productInstanceList}" status="i" var="productInstance">

            <g:if test="${productInstance.isHead()}">
                <tr data-tt-id="${productInstance.id}" class="${(i % 2) == 0 ? 'even' : 'odd'}">
            </g:if>
            <g:else>
                <tr data-tt-id="${productInstance.id}" data-tt-parent-id="${productInstance.parentId}"  class="${(i % 2) == 0 ? 'even' : 'odd'}">
            </g:else>

            <g:if test="${productInstance.isGroup()}">
                <td>
                    <strong>${fieldValue(bean: productInstance, field: "name")}</strong>
                </td>
                <td></td>
                <td></td>
                <td></td>
                <td></td>
            </g:if>
            <g:else>

                <td>
                    <g:link action="show"
                            id="${productInstance.id}">${fieldValue(bean: productInstance, field: "name")}</g:link>
                </td>

                <td>${fieldValue(bean: productInstance, field: "unitOfMeasure")}</td>

                <td>${fieldValue(bean: productInstance, field: "formulation")}</td>

                <td>${fieldValue(bean: productInstance, field: "unitPrice")}</td>

                <td>
                    <g:link action="edit" id="${productInstance.id}"><i class="glyphicon glyphicon-pencil"></i></g:link>
                </td>
            </g:else>
            </tr>
        </g:each>
        </tbody>
    </table>

    <div>
        <bs:paginate total="${productInstanceCount}"/>
    </div>
</section>

<g:javascript>
    $("#tree-table").treetable({ expandable: true, initialState: "expanded" });
</g:javascript>
</body>

</html>
