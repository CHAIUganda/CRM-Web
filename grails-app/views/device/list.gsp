
<%@ page import="com.omnitech.chai.model.Device" %>
<!DOCTYPE html>
<html>

<head>
    <meta name="layout" content="kickstart"/>
    <g:set var="entityName" value="${message(code: 'device.label', default: 'Device')}"/>
    <title><g:message code="default.list.label" args="[entityName]"/></title>
</head>

<body>

<section id="list-device" class="first">

    <table class="table table-bordered margin-top-medium">
        <thead>
        <tr>
            
            <g:sortableColumn property="imei"
                              title="${message(code: 'device.imei.label', default: 'Imei')}"/>
            
            <g:sortableColumn property="model"
                              title="${message(code: 'device.model.label', default: 'Model')}"/>
            
        </tr>
        </thead>
        <tbody>
        <g:each in="${deviceInstanceList}" status="i" var="deviceInstance">
            <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                
                <td><g:link action="show"
                            id="${deviceInstance.id}">${fieldValue(bean: deviceInstance, field: "imei")}</g:link></td>
                
                <td>${fieldValue(bean: deviceInstance, field: "model")}</td>
                
            </tr>
        </g:each>
        </tbody>
    </table>

    <div>
        <bs:paginate total="${0}" maxsteps="${-1}"/>
    </div>
</section>

</body>

</html>
