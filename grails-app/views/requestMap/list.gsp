
<%@ page import="com.omnitech.chai.model.RequestMap" %>
<!DOCTYPE html>
<html>

<head>
    <meta name="layout" content="kickstart"/>
    <g:set var="entityName" value="${message(code: 'requestMap.label', default: 'RequestMap')}"/>
    <title><g:message code="default.list.label" args="[entityName]"/></title>
</head>

<body>

<section id="list-requestMap" class="first">

    <table class="table table-bordered margin-top-medium">
        <thead>
        <tr>
            
            <g:sortableColumn property="url"
                              title="${message(code: 'requestMap.url.label', default: 'Url')}"/>
            
            <g:sortableColumn property="configAttribute"
                              title="${message(code: 'requestMap.configAttribute.label', default: 'Config Attribute')}"/>
            
            <g:sortableColumn property="httpMethod"
                              title="${message(code: 'requestMap.httpMethod.label', default: 'Http Method')}"/>
            
        </tr>
        </thead>
        <tbody>
        <g:each in="${requestMapInstanceList}" status="i" var="requestMapInstance">
            <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                
                <td><g:link action="show"
                            id="${requestMapInstance.id}">${fieldValue(bean: requestMapInstance, field: "url")}</g:link></td>
                
                <td>${fieldValue(bean: requestMapInstance, field: "configAttribute")}</td>
                
                <td>${fieldValue(bean: requestMapInstance, field: "httpMethod")}</td>
                
            </tr>
        </g:each>
        </tbody>
    </table>

</section>

</body>

</html>
