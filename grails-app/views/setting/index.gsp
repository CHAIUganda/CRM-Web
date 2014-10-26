
<%@ page import="com.omnitech.chai.model.Setting" %>
<!DOCTYPE html>
<html>

<head>
    <meta name="layout" content="kickstart" />
    <g:set var="entityName" value="${message(code: 'setting.label', default: 'Setting')}" />
    <title><g:message code="default.index.label" args="[entityName]" /></title>
</head>

<body>

<section id="index-setting" class="first">

    <table class="table table-bordered margin-top-medium">
        <thead>
        <tr>
            
            <g:sortableColumn property="dateCreated" title="${message(code: 'setting.dateCreated.label', default: 'Date Created')}" />
            
            <g:sortableColumn property="lastUpdated" title="${message(code: 'setting.lastUpdated.label', default: 'Last Updated')}" />
            
            <g:sortableColumn property="name" title="${message(code: 'setting.name.label', default: 'Name')}" />
            
            <g:sortableColumn property="uuid" title="${message(code: 'setting.uuid.label', default: 'Uuid')}" />
            
            <g:sortableColumn property="value" title="${message(code: 'setting.value.label', default: 'Value')}" />
            
            <td>
                Action
            </td>
        </tr>
        </thead>
        <tbody>
        <g:each in="${settingInstanceList}" status="i" var="settingInstance">
            <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                
                <td><g:link action="show" id="${settingInstance.id}">${fieldValue(bean: settingInstance, field: "dateCreated")}</g:link></td>
                
                <td><g:formatDate date="${settingInstance.lastUpdated}" format="dd-MMM-yyyy" /></td>
                
                <td>${fieldValue(bean: settingInstance, field: "name")}</td>
                
                <td>${fieldValue(bean: settingInstance, field: "uuid")}</td>
                
                <td>${fieldValue(bean: settingInstance, field: "value")}</td>
                
                <td>
                    <g:link action="edit" id="${settingInstance.id}"><i
                            class="glyphicon glyphicon-pencil"></i></g:link>
                    <g:link action="delete" id="${settingInstance.id}"><i
                            class="glyphicon glyphicon-remove"></i></g:link>
                </td>
            </tr>
        </g:each>
        </tbody>
    </table>
    <div>
        <bs:paginate total="${settingInstanceCount}"
                     id="${params.action == 'search' ? (params.term ?: params.id) : null}"/>
    </div>
</section>

</body>

</html>
