<!-- 
This menu is used to show function that can be triggered on the content (an object or list of objects).
-->

<%-- Only show the "Pills" navigation menu if a controller exists (but not for home) --%>
<g:if test="${params.controller != null && params.controller != '' && params.controller != 'home'}">
    <div class="container" style="background: #eeeeee; padding: 5px; border-radius: 0px; border: 1px solid #ddd;">
        <ul id="Menu" class="nav nav-pills margin-top-small">

            <g:set var="entityName"
                   value="${message(code: params.controller + '.label', default: params.controller.substring(0, 1).toUpperCase() + params.controller.substring(1))}"/>

            <li class="${params.action == "index" ? 'active' : ''}">
                <g:link action="index"><i class="glyphicon glyphicon-th-list"></i> <g:message code="default.list.label"
                                                                                              args="[entityName]"/></g:link>
            </li>
            <li class="${params.action == "create" ? 'active' : ''}">
                <g:link action="create"><i class="glyphicon glyphicon-plus"></i> <g:message code="default.new.label"
                                                                                            args="[entityName]"/></g:link>
            </li>

        %{-- Rendered on Customer View --}%
            <g:if test="${params.controller == 'customer' && ['index', '', null].contains(params.action)}">
                <li>
                    <a href="#SegmentModal" role="button" class="btn btn-success" data-toggle="modal"
                       title="Run Segmentation">
                        <i class="glyphicon glyphicon-play"></i>Auto Segment
                    </a>
                    <g:render template="/customer/segmentDialog"/>
                </li>
            </g:if>

            <g:if test="${params.action == 'show' || params.action == 'edit'}">
                <!-- the item is an object (not a list) -->
                <li class="${params.action == "edit" ? 'active' : ''}">
                    <g:link action="edit" id="${params.id}"><i class="glyphicon glyphicon-pencil"></i> <g:message
                            code="default.edit.label" args="[entityName]"/></g:link>
                </li>
                <li class="">
                    <g:render template="/_common/modals/deleteTextLink"/>
                </li>
            </g:if>

        %{-- The Search Box--}%
            <g:if test="${!layout_nosearchtext && (params.action == 'index' || params.action == 'search')}">
                <li class="navbar-right">
                    <div class="col-lg-12">
                        %{--<input type="hidden" name="currentPage" value="${currentPage}"/>--}%
                        %{--<input type="hidden" name="domain" value="${clazz}"/>--}%
                        <form action="search">
                            <input class="form-control" name="term" value="${(params.term ?: params.id)}"
                                   placeholder="Please type search item and press enter" style="width: 300px;"/>
                        </form>
                    </div>
                </li>
            </g:if>

        </ul>
    </div>
</g:if>
