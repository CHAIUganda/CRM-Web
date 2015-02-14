<%@ page import="com.omnitech.chai.util.ChaiUtils; com.omnitech.chai.model.Task" %>
<g:set var="entityName"
       value="${message(code: params.controller + '.label', default: params.controller.substring(0, 1).toUpperCase() + params.controller.substring(1))}"/>

<div class="container" style="max-width: 100%; padding: 0; margin-bottom: 3px;">
    <div class="container"
         style="background: #eeeeee; padding: 5px; border-radius: 0px; border: 1px solid #ddd; max-width: 100%;">
        <ul id="Menu" class="nav nav-pills margin-top-small">

            %{-- TASK LIST--}%
            <li class="${params.action == "index" ? 'active' : ''}">
                <g:link action="index" params="${[status: 'new']}"><i class="glyphicon glyphicon-th-list"></i> ${entityName}s</g:link>
            </li>

        %{-- CREATE MENU --}%
            <g:if test="${params.controller != 'sale' && params.controller != 'detailerTask'}">
                <li class="${params.action == "create" ? 'active' : ''}">
                    <g:link action="create"><i class="glyphicon glyphicon-plus"></i> <g:message code="default.new.label"
                                                                                                args="[entityName]"/></g:link>
                </li>
            </g:if>

        %{-- SELECT USERS MENU--}%
            <li>
                <a data-toggle="dropdown" href="#"><i
                        class="glyphicon glyphicon-user"></i>${params.user ? params.user : 'Select User'}<b
                        class="caret"></b></a>
                <ul role="menu" class="dropdown-menu multi-level" role="menu" aria-labelledby="dropdownMenu">
                    <g:each in="${users}" var="u">
                        <li><g:link action="${params.action == 'map' ? 'map' : 'index'}"
                                    params="${[user: u, status: params.status,max:2000]}">
                            <i class="glyphicon glyphicon-user"></i>${u} in ${u.territory}
                        </g:link></li>
                    </g:each>
                </ul>
            </li>

        %{-- COMPLETE OR NEW MENUS --}%
            <g:if test="${['detailerTask', 'call', 'task'].contains(params.controller)}">
                <li>
                    <a data-toggle="dropdown" href="#"><i
                            class="glyphicon glyphicon-filter"></i>
                        ${(params.status?.capitalize() == 'New' ? 'Active' : params.status?.capitalize()) ?: 'All'}  ${entityName}s
                        <b class="caret"></b>
                    </a>
                    <ul role="menu" class="dropdown-menu multi-level" role="menu" aria-labelledby="dropdownMenu">
                        <g:if test="${params.user != null}">
                            <li>
                                <g:link action="${params.action == 'map' ? 'map' : 'index'}"
                                        params="${[status: Task.STATUS_NEW, user: params.user]}">
                                    <i class="glyphicon glyphicon-list"></i>Active
                                </g:link>
                            </li>
                            <li>
                                <g:link action="${params.action == 'map' ? 'map' : 'index'}"
                                        params="${[status: Task.STATUS_COMPLETE, user: params.user]}">
                                    <i class="glyphicon glyphicon-list"></i>Complete
                                </g:link>
                            </li>
                        </g:if>
                        <g:else>
                            <li>
                                <g:link action="${params.action == 'map' ? 'map' : 'index'}"
                                        params="${[status: Task.STATUS_NEW]}">
                                    <i class="glyphicon glyphicon-list"></i>Active</g:link>
                            </li>
                            <li>
                                <g:link action="${params.action == 'map' ? 'map' : 'index'}"
                                        params="${[status: Task.STATUS_COMPLETE]}">
                                    <i class="glyphicon glyphicon-list"></i>Complete
                                </g:link>
                            </li>
                        </g:else>
                    </ul>
                </li>
            </g:if>

        %{--The Export Button--}%
            <g:if test="${params.user != null}">
                <li>
                    <g:link action="export" params="${[user: params.user]}">
                        <i class="glyphicon glyphicon-export"></i>Export ${params.user}'s   ${entityName}s
                    </g:link>
                </li>
            </g:if>
            <g:else>
                <li>
                    <g:link action="export" params="${[user: params.user]}">
                        <i class="glyphicon glyphicon-export"></i>Export All
                    </g:link>
                </li>
            </g:else>

        %{-- The Show Map Button--}%
            <li>
                <%
                    def newParams = [ui: 'map']
                    newParams.putAll(params)
                    def searchAction = params.action
                    switch (params.action) {
                        case 'map':
                            searchAction = 'index'
                            newParams.remove('ui')
                            break
                        case 'searchMap':
                            searchAction = 'search'
                            newParams.remove('ui')
                            break
                    }
                %>

                <g:link action="${searchAction}" params="${newParams}">
                    <i class="glyphicon glyphicon-map-marker"></i>
                    <g:if test="${params.action?.toLowerCase()?.contains('map')}">
                        Show List
                    </g:if>
                    <g:else>
                        Show Map
                    </g:else>
                </g:link>
            </li>
            %{--<li>--}%
            %{--<g:link action="cluster">--}%
            %{--<i class="glyphicon glyphicon-cloud" title="Cluster"></i>Clstr--}%
            %{--</g:link>--}%
            %{--</li>--}%

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