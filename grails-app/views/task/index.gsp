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
<div class="container" style="max-width: 100%; padding: 0; margin-bottom: 3px;">
    <div class="container"
         style="background: #eeeeee; padding: 5px; border-radius: 0px; border: 1px solid #ddd; max-width: 100%;">
        <ul id="Menu" class="nav nav-pills margin-top-small">

            %{-- TASK LIST--}%
            <li class="${params.action == "index" ? 'active' : ''}">
                <g:link action="index"><i class="glyphicon glyphicon-th-list"></i> All <g:message code="default.list.label"
                                                                                              args="[entityName]"/></g:link>
            </li>

            %{-- CREATE MENU --}%
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
                        <li><g:link controller="task" action="index" params="${[user: u, status: params.status]}">
                            <i class="glyphicon glyphicon-user"></i>${u}
                        </g:link></li>
                    </g:each>
                </ul>
            </li>

            %{-- COMPLETE OR NEW MENUS --}%
            <li>
                <a data-toggle="dropdown" href="#"><i
                        class="glyphicon glyphicon-filter"></i>${params.status?.toUpperCase() ?: 'New'} Tasks<b
                        class="caret"></b></a>
                <ul role="menu" class="dropdown-menu multi-level" role="menu" aria-labelledby="dropdownMenu">
                    <g:if test="${params.user != null}">
                        <li>

                            <g:link controller="task" action="index"
                                    params="${[status: Task.STATUS_NEW, user: params.user]}">
                                <i class="glyphicon glyphicon-list"></i>Active
                            </g:link></li>
                        <li>
                            <g:link controller="task" action="index"
                                    params="${[status: Task.STATUS_COMPLETE, user: params.user]}">
                                <i class="glyphicon glyphicon-list"></i>Complete
                            </g:link>
                        </li>

                    </g:if>
                    <g:else>
                        <li>

                            <g:link controller="task" action="index"
                                    params="${[status: Task.STATUS_NEW]}">
                                <i class="glyphicon glyphicon-list"></i>Active</g:link></li>
                        <li>
                            <g:link controller="task" action="index"
                                    params="${[status: Task.STATUS_COMPLETE]}">
                                <i class="glyphicon glyphicon-list"></i>Complete
                            </g:link>
                        </li>
                    </g:else>
                </ul>
            </li>


        %{--The Export Button--}%
            <g:if test="${params.user != null}">
                <li>
                    <g:link controller="task" action="export" params="${[user: params.user]}">
                        <i class="glyphicon glyphicon-export"></i>Export ${params.user}'s  Tasks
                    </g:link>
                </li>
            </g:if>
            <g:else>
                <li>
                    <g:link controller="task" action="export" params="${[user: params.user]}">
                        <i class="glyphicon glyphicon-export"></i>Export
                    </g:link>
                </li>
            </g:else>

        %{--The Search Box--}%
                <li class="navbar-right">
                    <div class="col-lg-12">
                        <form action="search">
                            <input class="form-control" name="term" value="${(params.term ?: params.id)}"
                                   placeholder="Please type search item and press enter" style="width: 300px;"/>
                        </form>
                    </div>
                </li>

        </ul>
    </div>
</div>
%{-- END SUBMENU BAR --}%

<section id="index-task" class="first">

    <table class="table table-bordered margin-top-medium">
        <thead>
        <tr>
            <g:sortableColumn property="description" params="${params}"
                              title="${message(code: 'task.description.label', default: 'Description')}"/>

            <g:sortableColumn property="status" params="${params}"
                              title="${message(code: 'task.status.label', default: 'Status')}"/>

            <g:sortableColumn property="dueDate" params="${params}"
                              title="${message(code: 'task.dueDate.label', default: 'Due Date')}"/>

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
                    <g:formatDate date="${taskInstance.dueDate}" format="dd-MMM-yyyy"/> <span
                            class="${new Date().after(taskInstance.dueDate) ? 'alert-danger' : ''}">(${ChaiUtils.fromNow(taskInstance.dueDate)})</span>
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
        <bs:paginate total="${taskInstanceCount}" params="${params}"
                     id="${params.action == 'search' ? (params.term ?: params.id) : null}"/>
    </div>
</section>

</body>

</html>
