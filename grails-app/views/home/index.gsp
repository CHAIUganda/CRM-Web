<html>

<head>
    <title><g:message code="default.welcome.title" args="[meta(name: 'app.name')]"/></title>
    <meta name="layout" content="kickstart"/>
</head>

<body>

<section id="intro" class="first">
    %{--<h1>Welcome !!!</h1>--}%

    <div class="panel panel-success">
        <div class="panel-heading">Call Plans</div>

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

            <div class="row">
                <div class="col-md-12">
                    <table class="table table-condensed sortable">
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
                        <g:each in="${reportInfo}" var="item">
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
                                             aria-valuenow="${item.productivity}" aria-valuemin="0" aria-valuemax="100"
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

</section>

<g:javascript src="lib/sortable.js"/>

</body>

</html>
