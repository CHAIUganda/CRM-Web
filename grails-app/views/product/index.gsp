
<%@ page import="com.omnitech.chai.model.Product" %>
<!DOCTYPE html>
<html>

<head>
    <meta name="layout" content="kickstart" />
    <g:set var="entityName" value="${message(code: 'product.label', default: 'Product')}" />
    <title><g:message code="default.index.label" args="[entityName]" /></title>
</head>

<body>

<section id="index-product" class="first">

    <table class="table table-bordered margin-top-medium">
        <thead>
        <tr>
            <g:sortableColumn property="name" title="${message(code: 'product.name.label', default: 'Name')}" />

            <g:sortableColumn property="metric" title="${message(code: 'product.unitOfMeasure.label', default: 'Measure')}" />

            <g:sortableColumn property="metric" title="${message(code: 'product.formulation.label', default: 'Formulation')}" />

            <g:sortableColumn property="unitPrice" title="${message(code: 'product.unitPrice.label', default: 'Unit Price')}" />
            
            <td>
                Action
            </td>
        </tr>
        </thead>
        <tbody>
        <g:each in="${productInstanceList}" status="i" var="productInstance">
            <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                
                <td><g:link action="show" id="${productInstance.id}">${fieldValue(bean: productInstance, field: "name")}</g:link></td>
                
                <td>${fieldValue(bean: productInstance, field: "unitOfMeasure")}</td>

                <td>${fieldValue(bean: productInstance, field: "formulation")}</td>

                <td>${fieldValue(bean: productInstance, field: "unitPrice")}</td>
                
                <td>
                    <g:link action="edit" id="${productInstance.id}"><i
                            class="glyphicon glyphicon-pencil"></i></g:link>
                </td>
            </tr>
        </g:each>
        </tbody>
    </table>
    <div>
        <bs:paginate total="${productInstanceCount}" />
    </div>
</section>

</body>

</html>
