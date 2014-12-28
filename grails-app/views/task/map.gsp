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
        <g:render template="/task/taskMenuBar"/>
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
                <bs:paginate total="${taskInstanceCount}" params="${params}"
                             id="${params.action == 'search' ? (params.term ?: params.id) : null}"/>
            </div>
            %{--<div id="map" class="col-lg-12"--}%
            %{--style=" margin: 5px 10px 10px 10px;  height: 300px; width: 300px; border: 1px solid #ccc;">dsddsd</div>--}%
        </div>

        <div class="col-lg-4" ng-controller="TaskMapCtrl">
            <div ng-show="task" class="ng-hide">
                <h4>{{task.customer}}</h4>

                <p><i>{{task.customerDescription}}</i></p>

                <div class="form-horizontal">
                    <div class="form-group">
                        <label class="col-md-3 control-label">Task</label>

                        <div class="col-md-9"><p class="form-control-static">{{task.description}}</p></div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label">Priority</label>

                        <div class="col-md-9"><p class="form-control-static">{{task.segment}}</p></div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label">GPS</label>

                        <div class="col-md-9"><p class="form-control-static">{{task.lat }},{{ task.lng}}</p></div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label">Assigned User(s)</label>

                        <div class="col-md-9"><p class="form-control-static">{{task.assignedUser}}</p></div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label">Moment From Now</label>

                        <div class="col-md-9"><p class="form-control-static">{{momentFromNow()}}</p></div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label">Set Due Date</label>

                        <div class="col-md-9">

                            <div class="input-group">
                                <input id="dueDateText" name="dueDate" class="datepicker form-control" size="16" type="text" value="2014-12-22" data-date-format="yyyy-mm-dd" ng-model="text.dueDateText">
                                %{--<bs:datePicker name="dueDate" class="form-control" ng-model="text.dueDateText"/>--}%
                                %{--<input type="text" class="form-control">--}%
                                <span class="input-group-btn">
                                    <button class="btn btn-default" type="button" ng-click="persistDueDate()">Save!</button>
                                </span>
                            </div>

                        </div>
                    </div>

                    <div class="form-group">
                        <div class="col-md-offset-3 col-md-9"><g:render template="/_common/ngError"/></div>
                    </div>
                </div>
            </div>

        </div>

    </div>

</section>
<script type="text/javascript" src="http://maps.google.com/maps/api/js?sensor=false"></script>
<script type="text/javascript"
        src="http://google-maps-utility-library-v3.googlecode.com/svn/tags/markerwithlabel/1.1.5/src/markerwithlabel_packed.js"></script>
<r:require modules="angular,angular-resource"/>
<g:javascript src="lib/moment.min.js"/>
<g:javascript src="lib/gmaps.js"/>
<g:javascript src="services/Common.js"/>
<g:javascript src="controllers/TaskMapCtrl.js"/>
<g:javascript src="maps/ListMap.js"/>

</body>

</html>
