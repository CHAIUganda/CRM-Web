<%@ page import="com.omnitech.chai.util.ChaiUtils; com.omnitech.chai.model.Task" %>
<!DOCTYPE html>
<html>

<head>
    <meta name="layout" content="kickstart"/>
    <g:set var="entityName" value="${message(code: 'order.label', default: 'Order')}"/>
    <g:set var="layout_nosecondarymenu" value="${true}" scope="request"/>
    <title><g:message code="default.index.label" args="[entityName]"/></title>
</head>

<body>

%{-- THE SUBMENU BAR --}%
<g:render template="/task/taskMenuBar"/>
%{-- END SUBMENU BAR --}%

<section id="index-task" class="first">

    <table class="table table-bordered margin-top-medium">
        <thead>
        <tr>
            <g:sortableColumn property="description" params="${params}"
                              title="${message(code: 'task.description.label', default: 'Description')}"/>

            <g:if test="${params.controller == 'call'}">
                <g:sortableColumn property="dateCreated" params="${params}"
                                  title="${message(code: 'task.dateCreated.label', default: 'Date Created')}"/>
            </g:if>

            <g:if test="${params.status == Task.STATUS_COMPLETE || params.controller == 'sale'}">
                <g:sortableColumn property="dueDate" params="${params}"
                                  title="${message(code: 'task.completion.label', default: 'Completion Date')}"/>

            </g:if>
            <g:else>
                <g:sortableColumn property="dueDate" params="${params}"
                                  title="${message(code: 'task.dueDate.label', default: 'Due Date')}"/>
            </g:else>
            <g:sortableColumn property="status" params="${params}"
                              title="${message(code: 'task.status.label', default: 'Status')}"/>

            <th>Customer</th>
            <th>Assigned User</th>

            %{--<th>Action</th>--}%
        </tr>
        </thead>
        <tbody>
        <g:each in="${taskInstanceList}" status="i" var="taskInstance">
            <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">

                <td>



                    <g:link action="show" id="${taskInstance.id}">
                        ${(params.offset ? (params.offset as Long) + 1 : 1) + i}.
                        <g:if test="${taskInstance?.respondsTo('takenBy') && taskInstance?.takenBy}">
                            <i class="glyphicon glyphicon-star"></i>
                        </g:if>
                        ${fieldValue(bean: taskInstance, field: "description")}
                    </g:link>
                </td>

                <g:if test="${params.controller == 'call'}">
                    <td><g:formatDate date="${taskInstance.dateCreated}" format="dd-MMM-yyyy"/></td>
                </g:if>

                <td>
                    <g:if test="${params.status == Task.STATUS_COMPLETE || params.controller == 'sale'}">
                        <g:formatDate date="${taskInstance.completionDate}" format="dd-MMM-yyyy"/>
                    </g:if>
                    <g:else>
                        <g:if test="${taskInstance.dueDate}">
                            <span class="${taskInstance.isOverDue() ? 'alert-danger' : ''}">
                                <g:formatDate date="${taskInstance.dueDate}" format="dd-MMM-yyyy"/>
                            </span>
                        </g:if>
                    </g:else>
                </td>

                <td>${taskInstance.getStatusMessage()}</td>

                <td>${taskInstance.customer}</td>

                <td>${taskInstance.territoryUser()}</td>

                %{--<td>--}%
                %{--<g:link action="edit" id="${taskInstance.id}" title="Edit/Schedule"><i--}%
                %{--class="glyphicon glyphicon-calendar"></i></g:link>--}%
                %{--</td>--}%
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
