<%@ page import="com.omnitech.chai.util.ChaiUtils; com.omnitech.chai.model.Task" %>
<!DOCTYPE html>
<html>

<head>
    <meta name="layout" content="kickstart"/>
    <g:set var="entityName" value="${message(code: 'task.label', default: 'Task')}"/>
    <title><g:message code="default.index.label" args="[entityName]"/></title>
</head>

<body>

<section id="index-task" class="first">

    <table class="table table-bordered margin-top-medium">
        <thead>
        <tr>
            <g:sortableColumn property="description"
                              title="${message(code: 'task.description.label', default: 'Description')}"/>

            <g:sortableColumn property="status" title="${message(code: 'task.status.label', default: 'Status')}"/>

            <g:sortableColumn property="dueDate" title="${message(code: 'task.dueDate.label', default: 'Due Date')}"/>

            <th>Customer</th>
            <th>Assigned User</th>

            <th>Action</th>
        </tr>
        </thead>
        <tbody>
        <g:each in="${taskInstanceList}" status="i" var="taskInstance">
            <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">

                <td><g:link action="show"
                            id="${taskInstance.id}">${fieldValue(bean: taskInstance, field: "description")}</g:link></td>

                <td>${fieldValue(bean: taskInstance, field: "status")}</td>

                <td><g:if test="${taskInstance.dueDate}">
                    <g:formatDate date="${taskInstance.dueDate}" format="dd-MMM-yyyy" /> <span class="${new Date().after(taskInstance.dueDate) ? 'alert-danger':''}">(${ChaiUtils.fromNow(taskInstance.dueDate)})</span>
                </g:if></td>

                <td>${taskInstance.customer}</td>

                <td>${taskInstance.assignedTo ?: taskInstance.completedBy}</td>

                <td>
                    <g:link action="edit" id="${taskInstance.id}"><i
                            class="glyphicon glyphicon-calendar"></i></g:link>
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
