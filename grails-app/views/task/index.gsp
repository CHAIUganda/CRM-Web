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
<g:render template="/task/taskMenuBar"/>
%{-- END SUBMENU BAR --}%

<section id="index-task" class="first">

    <table class="table table-bordered margin-top-medium">
        <thead>
        <tr>
            <g:sortableColumn property="description" params="${params}"
                              title="${message(code: 'task.description.label', default: 'Description')}"/>

            <g:if test="${params.status == Task.STATUS_COMPLETE}">
                <g:sortableColumn property="completionDate" params="${params}"
                                  title="${message(code: 'task.completion.label', default: 'Completion Date')}"/>

            </g:if>
            <g:else>
                <g:sortableColumn property="dueDate" params="${params}"
                                  title="${message(code: 'task.dueDate.label', default: 'Due Date')}"/>
            </g:else>

            <g:sortableColumn property="status" params="${params}"
                              title="${message(code: 'task.status.label', default: 'Status')}"/>

            <g:sortableColumn property="di.name" params="${params}"
                              title="${message(code: 'district.label', default: 'District')}"/>
            <th>Customer</th>
            <th>Assigned User</th>

        </tr>
        </thead>
        <tbody>
        <g:each in="${taskInstanceList}" status="i" var="taskInstance">
            <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">

                <td><g:link action="show"
                            id="${taskInstance.id}">
                    ${(params.offset ? (params.offset as Long) + 1 : 1) + i}.
                    ${ChaiUtils.truncateString(taskInstance.description, 50)}
                </g:link></td>

                <td>
                    <g:if test="${params.status == Task.STATUS_COMPLETE}">
                            <g:formatDate date="${taskInstance.completionDate}" format="EE,dd-MMM-yyyy"/>
                    </g:if>
                    <g:else>
                        <g:if test="${taskInstance.dueDate}">
                            <span class="${taskInstance.isOverDue() ? 'alert-danger' : ''}">
                                <g:formatDate date="${taskInstance.dueDate}" format="EE,dd-MMM-yyyy"/>
                            </span>
                        </g:if>
                    </g:else>
                </td>

                <td>${taskInstance.getStatusMessage()}</td>

                <td>${taskInstance.customer?.subCounty?.district}</td>

                <td> ${ChaiUtils.truncateString(taskInstance.customer, 10)}</td>

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
