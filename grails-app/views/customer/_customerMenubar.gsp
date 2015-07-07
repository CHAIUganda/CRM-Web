<div class="container"
     style="background: #eeeeee; padding: 5px; border-radius: 0px; border: 1px solid #ddd; max-width: 100%;">
    <ul id="Menu" class="nav nav-pills margin-top-small">

        <g:set var="entityName"
               value="${message(code: params.controller + '.label', default: params.controller.substring(0, 1).toUpperCase() + params.controller.substring(1))}"/>

        <li class="${params.action == "index" ? 'active' : ''}">
            <g:link action="index"><i class="glyphicon glyphicon-th-list"></i> <g:message code="default.list.label"
                                                                                          args="[entityName]"/></g:link>
        </li>

        <li class="${params.action == "create" ? 'active' : ''}">
            <g:link action="create"><i class="glyphicon glyphicon-plus"></i> <g:message code="default.new.label"
                                                                                        args="."/></g:link>
        </li>


        %{--Segment Filter--}%
        <li>
            <a data-toggle="dropdown" href="#"><i
                    class="glyphicon glyphicon-stats"></i>${params.segment ? params.segment : 'Segment'}<b
                    class="caret"></b></a>
            <ul role="menu" class="dropdown-menu multi-level" role="menu" aria-labelledby="dropdownMenu">
                <li>
                    <c:link action="index" params="${params}" reset="segment">
                        <i class="glyphicon glyphicon-th-list"></i> *All
                    </c:link>
                </li>
                <g:each in="${segments}" var="s">
                    <li><c:link action="index"
                                params="${[segment: s]}" extraParams="${params}">
                        <i class="glyphicon glyphicon-th-list"></i> ${s}
                    </c:link></li>
                </g:each>
            </ul>
        </li>


        %{--ACTIVE Filter--}%
        <li>
            <a data-toggle="dropdown" href="#"><i
                    class="glyphicon glyphicon-check"></i>${params.active ? (Boolean.parseBoolean(params.active) ? 'Active' : 'Inactive') : 'Activity'}<b
                    class="caret"></b></a>
            <ul role="menu" class="dropdown-menu multi-level" role="menu" aria-labelledby="dropdownMenu">
                <li>
                    <c:link action="index" params="${params}" reset="active">
                        <i class="glyphicon glyphicon-th-list"></i> *All
                    </c:link>
                </li>
                <li>
                    <c:link action="index"
                            params="${[active: true]}" extraParams="${params}">
                        <i class="glyphicon glyphicon-thumbs-up"></i> Active
                    </c:link>
                </li>
                <li>
                    <c:link action="index"
                            params="${[active: false]}" extraParams="${params}">
                        <i class="glyphicon glyphicon-thumbs-down"></i> Inactive
                    </c:link>
                </li>
            </ul>
        </li>

    %{-- Detailing Territory Filters--}%
        <g:if test="${detailingTerritories}">
            <li>
                <a data-toggle="dropdown" href="#"><i
                        class="glyphicon glyphicon-filter"></i>${params.detTerritory ? detailingTerritories.find {
                    it.id == params.detTerritory as Long
                } : 'Detailing Territories'}<b
                        class="caret"></b></a>
                <ul role="menu" class="dropdown-menu multi-level" role="menu" aria-labelledby="dropdownMenu">
                    <li>
                        <c:link action="index" params="${params}" reset="detTerritory">
                            <i class="glyphicon glyphicon-th-list"></i> *All
                        </c:link>
                    </li>
                    <g:each in="${detailingTerritories}" var="t">
                        <li><c:link action="index"
                                    params="${[detTerritory: t.id]}" extraParams="${params}" reset="salTerritory">
                            <i class="glyphicon glyphicon-th-list"></i> ${t}
                        </c:link></li>
                    </g:each>
                </ul>
            </li>
        </g:if>


    %{-- Sales Territory Filters--}%
        <g:if test="${saleTerritories}">
            <li>
                <a data-toggle="dropdown" href="#"><i
                        class="glyphicon glyphicon-filter"></i>${params.salTerritory ? saleTerritories.find {
                    it.id == params.salTerritory as Long
                } : 'Sales Territories'}<b
                        class="caret"></b></a>
                <ul role="menu" class="dropdown-menu multi-level" role="menu" aria-labelledby="dropdownMenu">
                    <li>
                        <c:link action="index" params="${params}" reset="salTerritory">
                            <i class="glyphicon glyphicon-th-list"></i> *All
                        </c:link>
                    </li>
                    <g:each in="${saleTerritories}" var="t">
                        <li><c:link action="index"
                                    params="${[salTerritory: t.id]}" extraParams="${params}" reset="detTerritory">
                            <i class="glyphicon glyphicon-th-list"></i> ${t}
                        </c:link></li>
                    </g:each>
                </ul>
            </li>
        </g:if>



    %{--Customer--}%
        <sec2:ifAllGranted roles="ROLE_SUPER_ROOT">
            <li class="active">
                <a class="btn btn-primary" data-toggle="modal" data-target="#importCustomers">
                    <i class="glyphicon glyphicon-upload"></i> Import Customers
                </a>
            </li>
        </sec2:ifAllGranted>

        <li>
            <sec2:link controller="customer" action="export">
                <i class="glyphicon glyphicon-upload"></i> Export
            </sec2:link>
        </li>



        %{-- The Search Box--}%
        <li class="navbar-right">
            <div class="col-lg-12">
                %{--<input type="hidden" name="currentPage" value="${currentPage}"/>--}%
                %{--<input type="hidden" name="domain" value="${clazz}"/>--}%
                <form>
                    <input class="form-control" name="search" value="${params.search}"
                           placeholder="Search by Outlet name"/>
                </form>
            </div>
        </li>

    </ul>
</div>

