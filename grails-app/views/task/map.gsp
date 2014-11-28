<%@ page import="com.omnitech.chai.util.ChaiUtils; com.omnitech.chai.model.Task" %>
<!DOCTYPE html>
<html>

<head>
    <meta name="layout" content="kickstart"/>
    <g:set var="entityName" value="${message(code: 'task.label', default: 'Task')}"/>
    <g:set var="layout_nosecondarymenu" value="${true}" scope="request"/>
    <title><g:message code="default.index.label" args="[entityName]"/></title>
    <script type="application/javascript">
        chaiMapData = ${raw(mapData)}
    </script>
</head>
<body>


%{-- THE SUBMENU BAR --}%
<div class="container" style="max-width: 100%; padding: 0; margin-bottom: 3px;">
    <div class="container"
         style="background: #eeeeee; padding: 5px; border-radius: 0px; border: 1px solid #ddd; max-width: 100%;">
        <ul id="Menu" class="nav nav-pills margin-top-small">

            %{-- TASK LIST--}%
            <li class="${params.action == "index" ? 'active' : ''}">
                <g:link action="index"><i class="glyphicon glyphicon-th-list"></i> All <g:message
                        code="default.list.label"
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
                            </g:link>
                        </li>
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
                        <i class="glyphicon glyphicon-export"></i>Export All
                    </g:link>
                </li>
            </g:else>

            <li>
                <a href="#"
                   data-target="#map-view"
                   data-toggle="modal">
                    <i class="glyphicon glyphicon-map-marker"></i>Show Map
                </a>
            </li>

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

    <div class="row">
        <div class="col-lg-6">
            <div id="map" style="height: 550px;
            position: relative;
            overflow: hidden;
            transform: translateZ(0px);
            background-color: rgb(229, 227, 223);"></div>
            <div>
                <bs:paginate total="${taskInstanceCount}" params="${params}"
                             id="${params.action == 'search' ? (params.term ?: params.id) : null}"/>
            </div>
            %{--<div id="map" class="col-lg-12"--}%
            %{--style=" margin: 5px 10px 10px 10px;  height: 300px; width: 300px; border: 1px solid #ccc;">dsddsd</div>--}%
        </div>
        <div class="col-lg-6">

            <table class="table table-bordered margin-top-medium">
                <thead>
                <tr>
                    <g:sortableColumn property="description" params="${params}"
                                      title="${message(code: 'task.description.label', default: 'Description')}"/>


                    <g:sortableColumn property="status" params="${params}"
                                      title="${message(code: 'task.status.label', default: 'Status')}"/>

                    <th>Customer</th>

                </tr>
                </thead>
                <tbody>
                <g:each in="${taskInstanceList}" status="i" var="taskInstance">
                    <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">

                        <td><span class="${taskInstance.isOverDue() ? 'alert-danger' : ''}">
                            <g:link action="show"
                                    id="${taskInstance.id}">${fieldValue(bean: taskInstance, field: "description")}
                            </span></g:link>
                        </td>


                        <td>${taskInstance.getStatusMessage()}</td>

                        <td>${taskInstance.customer}</td>

                    </tr>
                </g:each>
                </tbody>
            </table>
            <div>
                <bs:paginate total="${taskInstanceCount}" params="${params}"
                             id="${params.action == 'search' ? (params.term ?: params.id) : null}"/>
            </div>
        </div>

    </div>



</section>
<script type="text/javascript" src="http://maps.google.com/maps/api/js?sensor=false"></script>
<g:javascript src="lib/gmaps.js"/>
<g:javascript src="maps/ListMap.js"/>

</body>

</html>
