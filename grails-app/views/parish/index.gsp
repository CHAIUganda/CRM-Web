
<%@ page import="com.omnitech.chai.model.Parish" %>
<!DOCTYPE html>
<html>

<head>
    <meta name="layout" content="kickstart" />
    <g:set var="entityName" value="${message(code: 'parish.label', default: 'Parish')}" />
    <title><g:message code="default.index.label" args="[entityName]" /></title>
</head>

<body>

<section id="index-parish" class="first">

    <table class="table table-bordered margin-top-medium">
        <thead>
        <tr>
            
            <g:sortableColumn property="name" title="${message(code: 'parish.name.label', default: 'Name')}" />
            
            <th><g:message code="parish.subCounty.label" default="Sub County" /></th>
            
            <td>
                Action
            </td>
        </tr>
        </thead>
        <tbody>
        <g:each in="${parishInstanceList}" status="i" var="parishInstance">
            <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                
                <td><g:link action="show" id="${parishInstance.id}">${fieldValue(bean: parishInstance, field: "name")}</g:link></td>
                
                <td>${fieldValue(bean: parishInstance, field: "subCounty")}</td>
                
                <td>
                    <g:link action="edit" id="${parishInstance.id}"><i
                            class="glyphicon glyphicon-pencil"></i></g:link>
                    <g:link action="delete" id="${parishInstance.id}"><i
                            class="glyphicon glyphicon-remove"></i></g:link>
                </td>
            </tr>
        </g:each>
        </tbody>
    </table>
    <div>
        <bs:paginate total="${parishInstanceCount}" />
    </div>
</section>

</body>

</html>
