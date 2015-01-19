<%@ page import="com.omnitech.chai.model.Role" %>
<html>

<head>
    <title><g:message code="default.welcome.title" args="[meta(name: 'app.name')]"/></title>
    <meta name="layout" content="kickstart"/>
    <r:require module="dataTable"/>
</head>

<body>

<section id="intro" class="first">
%{--<h1>Welcome !!!</h1>--}%
    <sec:ifAnyGranted
            roles="${[Role.ADMIN_ROLE_NAME, Role.DETAILER_ROLE_NAME, Role.SUPER_ADMIN_ROLE_NAME, Role.DETAILING_SUPERVISOR_ROLE_NAME].join(',')}">
        <div class="panel panel-success">
            <div class="panel-heading">Detailing Call Plans</div>

            <div class="panel-body">

                %{--<div class="row">--}%
                %{--<div class="col-md-12">--}%
                %{--<div class="form-inline">--}%

                %{--<div class="form-group">--}%
                %{--<label>Type</label>--}%
                %{--<g:select name="type" from="${['Detailing', 'Sales']}" class="form-control"/>--}%
                %{--</div>--}%

                %{--<div class="form-group">--}%
                %{--<label>Start Date</label>--}%
                %{--<bs:datePicker name="startDate" class="form-control"/>--}%
                %{--</div>--}%

                %{--<div class="form-group">--}%
                %{--<label>End Date</label>--}%
                %{--<input id="dueDateText" name="dueDate" class=" form-control" type="text"--}%
                %{--data-date-format="yyyy-mm-dd" ng-model="text.dueDateText">--}%
                %{--</div>--}%
                %{--</div>--}%
                %{--</div>--}%
                %{--</div>--}%

                %{-- For Detailers --}%

                <div class="row">
                    <div class="col-md-12">
                        <table class="pageableTable" cellspacing="0" width="100%">
                            <thead>
                            <tr>
                                <th>Rep</th>
                                <th>Territory</th>
                                <th>No. Tasks</th>
                                <th>% Complete</th>
                                <th>% Productivity</th>
                                %{--<td>Orders</td>--}%
                                %{--<td>New Customers</td>--}%
                            </tr>
                            </thead>
                            <tbody>
                            <g:each in="${detailingInfo}" var="item">
                                <tr>
                                    <td>${item.username}</td>
                                    <td>${item.territory}</td>
                                    <td>${item.numAll}</td>
                                    <td>
                                        <div class="progress">
                                            <div class="progress-bar progress-bar-info"
                                                 role="progressbar"
                                                 aria-valuenow="${item.covered}" aria-valuemin="0" aria-valuemax="100"
                                                 style="width: ${item.covered}%; color: #000000;">
                                                ${item.covered}%
                                            </div>
                                        </div>
                                    </td>
                                    <td>
                                        <div class="progress">
                                            <div class="progress-bar progress-bar-info"
                                                 role="progressbar"
                                                 aria-valuenow="${item.productivity}" aria-valuemin="0"
                                                 aria-valuemax="100"
                                                 style="width: ${item.productivity}%; color: #000000;">
                                                ${item.productivity}%
                                            </div>
                                        </div>
                                    </td>
                                    %{--<td>5</td>--}%
                                    %{--<td>5</td>--}%
                                </tr>
                            </g:each>
                            </tbody>
                        </table>
                    </div>
                </div>

            </div>

        </div>
    </sec:ifAnyGranted>

    <div class="panel panel-success">
        <div class="panel-heading">Sales Call Plans</div>

        <div class="panel-body">

        %{--<div class="row">--}%
        %{--<div class="col-md-12">--}%
        %{--<div class="form-inline">--}%

        %{--<div class="form-group">--}%
        %{--<label>Type</label>--}%
        %{--<g:select name="type" from="${['Detailing', 'Sales']}" class="form-control"/>--}%
        %{--</div>--}%

        %{--<div class="form-group">--}%
        %{--<label>Start Date</label>--}%
        %{--<bs:datePicker name="startDate" class="form-control"/>--}%
        %{--</div>--}%

        %{--<div class="form-group">--}%
        %{--<label>End Date</label>--}%
        %{--<input id="dueDateText" name="dueDate" class=" form-control" type="text"--}%
        %{--data-date-format="yyyy-mm-dd" ng-model="text.dueDateText">--}%
        %{--</div>--}%
        %{--</div>--}%
        %{--</div>--}%
        %{--</div>--}%

        %{-- For Detailers --}%
            <sec:ifAnyGranted
                    roles="${[Role.SALES_SUPERVISOR_ROLE_NAME, Role.SALES_ROLE_NAME, Role.SUPER_ADMIN_ROLE_NAME, Role.ADMIN_ROLE_NAME].join(',')}">
                <div class="row">
                    <div class="col-md-12">
                        <table class="pageableTable">
                            <thead>
                            <tr>
                                <th>Rep</th>
                                <th>Territory</th>
                                <th>No. Tasks</th>
                                <th>% Complete</th>
                                <th>% Productivity</th>
                                %{--<td>Orders</td>--}%
                                %{--<td>New Customers</td>--}%
                            </tr>
                            </thead>
                            <tbody>
                            <g:each in="${salesInfo}" var="item">
                                <tr>
                                    <td>${item.username}</td>
                                    <td>${item.territory}</td>
                                    <td>${item.numAll}</td>
                                    <td>
                                        <div class="progress">
                                            <div class="progress-bar progress-bar-info"
                                                 role="progressbar"
                                                 aria-valuenow="${item.covered}" aria-valuemin="0" aria-valuemax="100"
                                                 style="width: ${item.covered}%; color: #000000;">
                                                ${item.covered}%
                                            </div>
                                        </div>
                                    </td>
                                    <td>
                                        <div class="progress">
                                            <div class="progress-bar progress-bar-info"
                                                 role="progressbar"
                                                 aria-valuenow="${item.productivity}" aria-valuemin="0"
                                                 aria-valuemax="100"
                                                 style="width: ${item.productivity}%; color: #000000;">
                                                ${item.productivity}%
                                            </div>
                                        </div>
                                    </td>
                                    %{--<td>5</td>--}%
                                    %{--<td>5</td>--}%
                                </tr>
                            </g:each>
                            </tbody>
                        </table>
                    </div>
                </div>

            </sec:ifAnyGranted>

        </div>

    </div>

</section>


<g:javascript>
    $('.pageableTable').DataTable({
        "pagingType": "full_numbers"
    });
</g:javascript>

</body>

</html>
