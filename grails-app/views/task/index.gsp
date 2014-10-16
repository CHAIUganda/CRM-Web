
<%@ page import="com.omnitech.chai.model.Task" %>
<!DOCTYPE html>
<html>

<head>
    <meta name="layout" content="kickstart" />
    <g:set var="entityName" value="${message(code: 'task.label', default: 'Task')}" />
    <title><g:message code="default.index.label" args="[entityName]" /></title>
</head>

<body>

<section id="index-task" class="first">

    <table class="table table-bordered margin-top-medium">
        <thead>
        <tr>
            
            <g:sortableColumn property="dateCreated" title="${message(code: 'task.dateCreated.label', default: 'Date Created')}" />
            
            <g:sortableColumn property="description" title="${message(code: 'task.description.label', default: 'Description')}" />
            
            <g:sortableColumn property="lastUpdated" title="${message(code: 'task.lastUpdated.label', default: 'Last Updated')}" />
            
            <g:sortableColumn property="status" title="${message(code: 'task.status.label', default: 'Status')}" />
            
            <g:sortableColumn property="uuid" title="${message(code: 'task.uuid.label', default: 'Uuid')}" />
            
            <td>
                Action
            </td>
        </tr>
        </thead>
        <tbody>
        <g:each in="${taskInstanceList}" status="i" var="taskInstance">
            <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                
                <td><g:link action="show" id="${taskInstance.id}">${fieldValue(bean: taskInstance, field: "dateCreated")}</g:link></td>
                
                <td>${fieldValue(bean: taskInstance, field: "description")}</td>
                
                <td><g:formatDate date="${taskInstance.lastUpdated}" format="dd-MMM-yyyy" /></td>
                
                <td>${fieldValue(bean: taskInstance, field: "status")}</td>
                
                <td>${fieldValue(bean: taskInstance, field: "uuid")}</td>
                
                <td>
                    <g:link action="edit" id="${taskInstance.id}"><i
                            class="glyphicon glyphicon-pencil"></i></g:link>
                    <g:link action="delete" id="${taskInstance.id}"><i
                            class="glyphicon glyphicon-remove"></i></g:link>
                </td>
            </tr>
        </g:each>
        </tbody>
    </table>
    <div>
        <bs:paginate total="${taskInstanceCount}"
                     id="${params.action == 'search' ? (params.term ?: params.id) : null}"/>
    </div>
</section>

</body>

</html>
