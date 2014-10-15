
<%@ page import="com.omnitech.chai.model.Device" %>
<!DOCTYPE html>
<html>

<head>
    <meta name="layout" content="kickstart" />
    <g:set var="entityName" value="${message(code: 'device.label', default: 'Device')}" />
    <title><g:message code="default.index.label" args="[entityName]" /></title>
</head>

<body>

<section id="index-device" class="first">

    <table class="table table-bordered margin-top-medium">
        <thead>
        <tr>
            
            <g:sortableColumn property="model" title="${message(code: 'device.model.label', default: 'Model')}" />

            <g:sortableColumn property="imei" title="${message(code: 'device.imei.label', default: 'Imei')}" />
            <th>${message(code: 'device.user.label', default: 'Assigned User')}</th>

            <td>
                Action
            </td>
        </tr>
        </thead>
        <tbody>
        <g:each in="${deviceInstanceList}" status="i" var="deviceInstance">
            <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">

                <td><g:link action="show" id="${deviceInstance.id}">${fieldValue(bean: deviceInstance, field: "model")}</g:link></td>

                <td>${fieldValue(bean: deviceInstance, field: "imei")}</td>

                <td>${fieldValue(bean: deviceInstance, field: "user")}</td>

                <td>
                    <g:link action="edit" id="${deviceInstance.id}"><i
                            class="glyphicon glyphicon-pencil"></i></g:link>
                    <g:link action="delete" id="${deviceInstance.id}"><i
                            class="glyphicon glyphicon-remove"></i></g:link>
                </td>
            </tr>
        </g:each>
        </tbody>
    </table>
    <div>
        <bs:paginate total="${deviceInstanceCount}" />
    </div>
</section>

</body>

</html>
