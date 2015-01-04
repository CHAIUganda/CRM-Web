<%@ page import="com.omnitech.chai.model.Report" %>
<!DOCTYPE html>
<html>

<head>
    <meta name="layout" content="kickstart"/>
    <g:set var="entityName" value="${message(code: 'report.label', default: 'Report')}"/>
    <title><g:message code="default.show.label" args="[entityName]"/></title>
    <r:require module="reportWiz"/>
</head>

<body>

<div ng-controller="ReportCtrl">

    <!--    THE COLUMNS     -->
    <div class="panel panel-success">

        <div class="panel-heading"><span class="badge">Columns To Display</span></div>

        <div class="panel-body">
            <div class="col-md-12 left-border">
                <div ng-repeat="col in columns" class="col-md-3">
                    <label class=""><input type="checkbox" ng-model="col.selected"> {{col.desc}}</label>
                </div>
            </div>
        </div>

    </div>

    <!--    AGGREGATIONS     -->
    <div class="panel panel-success">

        <div class="panel-heading"><span class="badge">Aggregation</span></div>

        <div class="panel-body">
            <div class="col-md-12 left-border">

                <div ng-repeat="col in statement.aggregations()" class="col-md-3 well anim-repeat-item">

                    <div class="form-inline">
                        <div class="form-group">
                            <select ng-model="col.agg" class="form-control" style="width: 100%">
                                <option>Average</option>
                                <option>Sum</option>
                                <option>Standard Deviation</option>
                                <option>Count</option>
                                <option>Count Unique</option>
                            </select>
                            <select ng-model="col.expr" class="form-control" style="width: 100%">
                                <option ng-repeat="col2 in columns" value="{{col2.name}}">{{col2.desc}}</option>
                            </select>
                        </div>

                    </div>

                    <div class="form-group">
                        <div class="col-sm-offset-3 col-sm-9">
                            <button class="glyphicon glyphicon-trash  form-control"
                                    ng-click="removeAggregation(col)">Delete</button>
                        </div>

                    </div>

                </div>


                <div class="col-md-12">
                    <button class="glyphicon glyphicon-plus-sign form-control" ng-click="addAggregation()">Add</button>
                </div>
            </div>
        </div>

    </div>


    <!--    SELECTED COLUMNS     -->
    <div class="panel panel-success ">

        <div class="panel-heading"><span class="badge">Selected Columns</span></div>

        <div class="panel-body">
            <div class="col-md-12 left-border">
                <div ng-repeat="col in statement.cols" class="col-md-3">
                    <label ng-class="{'glyphicon-asterisk': col.isAggregation() == true, 'glyphicon-tag': col.isAggregation() == false}"
                           class="glyphicon">{{col.emitString()}}</label>
                </div>
            </div>
        </div>

    </div>





    <!--    FILTERS     -->
    <div class="panel panel-success ">

        <div class="panel-heading"><span class="badge">Filters</span></div>

        <div class="panel-body">
            <div class="col-md-10 left-border">
                <% myTemplate = createLink(action: 'conditionGroup') %>
                <div ng-repeat="group in statement.where" ng-include="'${myTemplate}'"></div>
            </div>
        </div>

    </div>

    <!--    SUBMIT BUTTON     -->
    <div class="row ">

        <div class="text-center">
            <div class="btn btn-info" ng-click="showStatement()">Generate Report</div>
        </div>

    </div>

</div>

<r:require module="reportWiz"/>
<g:javascript>

    omnitech.js.reports.DataLoader.reportColumns = ${raw(reportInstance.fields)};
    omnitech.js.reports.DataLoader.generateReportCallback = function (cols, filter) {
        //alert('Columns:\n' + cols + '\n' + 'Filters:\n' + filter + '\n');
        var encodedCols = encodeURIComponent(cols);
        var encodedFilter = encodeURIComponent(filter);

        $.fileDownload(omnitechBase + '/report/download/${reportInstance.id}?cols='+encodedCols + '&filter='+encodedFilter)
            .done(function () { alert('File download a success!'); })
            .fail(function () { alert('Report Download Failed!'); });
    };

</g:javascript>
</body>

</html>
