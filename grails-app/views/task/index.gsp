<%@ page import="com.omnitech.chai.util.ChaiUtils; com.omnitech.chai.model.Task" %>
<!DOCTYPE html>
<html>

<head>
    <meta name="layout" content="kickstart"/>
    <g:set var="entityName" value="${message(code: 'task.label', default: 'Task')}"/>
    <g:set var="layout_nosecondarymenu" value="${true}" scope="request"/>
    <title><g:message code="default.index.label" args="[entityName]"/></title>
</head>

<body>

%{-- THE SUBMENU BAR --}%

<div class="container" style="background: #eeeeee; padding: 5px; border-radius: 0px; border: 1px solid #ddd;">
    <ul id="Menu" class="nav nav-pills margin-top-small">

        <li class="${params.action == "index" ? 'active' : ''}">
            <g:link action="index"><i class="glyphicon glyphicon-th-list"></i> <g:message code="default.list.label"
                                                                                          args="[entityName]"/></g:link>
        </li>
        <li class="${params.action == "create" ? 'active' : ''}">
            <g:link action="create"><i class="glyphicon glyphicon-plus"></i> <g:message code="default.new.label"
                                                                                        args="[entityName]"/></g:link>
        </li>

        %{-- SELECT USERS MENU--}%
        <li>
            <a data-toggle="dropdown" href="#"><i
                    class="glyphicon glyphicon-user"></i>${params.user ? params.user : 'Select User'}<b
                    class="caret"></b></a>
            <ul role="menu" class="dropdown-menu multi-level" role="menu" aria-labelledby="dropdownMenu">
                <g:each in="${users}" var="u">
                    <li><g:link controller="task" action="index" params="${[user: u]}">
                        <i class="glyphicon glyphicon-user"></i>${u}
                    </g:link></li>
                </g:each>
            </ul>
        </li>

        %{-- ACTIVE OR INACTIVE STATUS --}%
        <li>
            <a data-toggle="dropdown" href="#"><i class="glyphicon glyphicon-filter"></i>All Tasks<b
                    class="caret"></b></a>
            <ul role="menu" class="dropdown-menu multi-level" role="menu" aria-labelledby="dropdownMenu">
                <li>
                    <g:link controller="task" action="index" params="${[status: 'active']}">
                        <i class="glyphicon glyphicon-list"></i>Active
                    </g:link></li>
                <li>
                    <g:link controller="task" action="index" params="${[status: 'active']}">
                        <i class="glyphicon glyphicon-list"></i>Complete
                    </g:link>
                </li>
            </ul>
        </li>

    </ul>
</div>

%{-- END SUBMENU BAR --}%

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

                %{--<td>${taskInstance.assignedTo ?: taskInstance.completedBy}</td>--}%
                <td>${taskInstance.territoryUser()}</td>

                <td>
                    <g:link action="edit" id="${taskInstance.id}" title="Edit/Schedule"><i
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
