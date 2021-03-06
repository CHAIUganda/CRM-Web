<%@ page import="com.omnitech.chai.util.ChaiUtils; com.omnitech.chai.model.Task" %>
<!DOCTYPE html>
<html>

<head>
    <meta name="layout" content="kickstart"/>
    <g:set var="entityName" value="${message(code: 'task.label', default: 'Task')}"/>
    <g:set var="layout_nosecondarymenu" value="${true}" scope="request"/>
    <title><g:message code="default.index.label" args="[entityName]"/></title>
    <script type="application/javascript">
        chaiMapData =
        ${raw(mapData)}
    </script>
</head>

<body>

%{-- THE SUBMENU BAR --}%
<g:if test="${!no_mapsubmenu}">
    <g:render template="/task/taskMenuBar"/>
</g:if>
%{-- END SUBMENU BAR --}%

<section id="index-task" class="first">

    <div class="row">
        <div class="col-lg-8">
            <div id="map" style="height: 550px;
            position: relative;
            overflow: hidden;
            transform: translateZ(0px);
            background-color: rgb(229, 227, 223);"></div>

            <div>
                <g:if test="${!no_pagination}">
                    <bs:paginate total="${taskInstanceCount}" params="${params}"
                                 id="${params.action == 'search' ? (params.term ?: params.id) : null}"/>
                </g:if>
            </div>

        </div>

        <div class="col-lg-4" ng-controller="TaskMapCtrl">

            <div class="ng-hide">
                <div id="showCustomers" class="form-inline" style="border-radius: 4px;">

                    <div class="form-group">
                        <input type="text" placeholder="Search..." class="form-control"
                               ng-model="task"
                               typeahead="t as t.description for t in allTasks | filter:{description:$viewValue}"
                               typeahead-editable="false"
                               typeahead-on-select="onTaskSelected($item)"
                        >
                    </div>

                    <div class="form-group ${params.user ?: 'ng-hide'}">
                        <label style="font-weight: bold">
                            <input type="checkbox" ng-click="onShowCustomers()" ng-model="showCustomers"> Show Customers
                        </label>
                    </div>
                </div>
            </div>


            <div ng-show="task.type == 'task'" class="ng-hide">
                <g:render template="/task/mapTaskView"/>
            </div>

            <div ng-show="task.type == 'customer'" class="ng-hide">
                <g:render template="/task/mapCustomerView"/>
            </div>

            <div class="ng-hide">
                <g:render template="/task/mapLegend"/>
            </div>
        </div>

    </div>




</div>

</section>
<script type="text/javascript" src="http://maps.google.com/maps/api/js?sensor=false"></script>
<script type="text/javascript"
        src="http://google-maps-utility-library-v3.googlecode.com/svn/tags/markerwithlabel/1.1.5/src/markerwithlabel_packed.js"></script>
<r:require modules="angular,angular-resource,angular-ui"/>
<g:javascript src="lib/moment.min.js"/>
<g:javascript src="lib/gmaps.js"/>
<g:javascript src="services/Common.js"/>
<g:javascript src="controllers/TaskMapCtrl.js"/>
<g:javascript src="maps/ListMap.js"/>

</body>

</html>
