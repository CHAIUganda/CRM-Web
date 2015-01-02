<%@ page import="com.omnitech.chai.model.Report" %>
<!DOCTYPE html>
<html>

<head>
    <meta name="layout" content="kickstart"/>
    <g:set var="entityName" value="${message(code: 'report.label', default: 'Report')}"/>
    <title><g:message code="default.show.label" args="[entityName]"/></title>
    <r:require module="reportWiz"/>
    <style type="text/css">
    .glyphicon:empty {
            width: auto;
    }
    .form-control {
        width: auto;
        padding: 6px 10px;
    }
</style>
</head>

<body>

<div class="container" ng-controller="ReportCtrl">

    <!--    THE COLUMNS     -->
    <div class="row ">

        <div class="col-md-2 text-right">
            <span class="badge">Columns To Display</span>
        </div>

        <div class="col-md-10 left-border">
            <div ng-repeat="col in columns" class="col-md-3">
                <label class=""><input type="checkbox" ng-model="col.selected"> {{col.name}}</label>
            </div>
        </div>

    </div>


    <!--    SELECTED COLUMNS     -->
    <div class="row ">

        <div class="col-md-2 text-right">
            <div class="badge">Selected Columns</div>
        </div>

        <div class="col-md-10 left-border">
            <div ng-repeat="col in statement.cols" class="col-md-3">
                <label ng-class="{'glyphicon-asterisk': col.isAggregation() == true, 'glyphicon-tag': col.isAggregation() == false}"
                       class="glyphicon">{{col.emitString()}}</label>
            </div>
        </div>

    </div>


    <!--    AGGREGATIONS     -->
    <div class="row ">

        <div class="col-md-2 text-right"><span class="badge">Aggregation</span></div>

        <div class="col-md-10 left-border">
            <div class="col-md-12">
                <div ng-repeat="col in statement.aggregations()" class="col-md-4 thumbnail">
                    <div class="col-md-12">
                        <label class="col-md-4 text-right">Function</label>
                        <select ng-model="col.agg" class="col-md-8">
                            <option>Average</option>
                            <option>Sum</option>
                            <option>Standard Deviation</option>
                            <option>Count</option>
                            <option>Count Unique</option>
                        </select>
                    </div>

                    <div class="col-md-12">
                        <label class="col-md-4 text-right">
                            Column
                        </label>
                        <select class="col-md-8" ng-model="col.expr">
                            <option ng-repeat="col2 in columns">{{col2.emitString()}}</option>
                        </select>

                    </div>

                    <div class="col-md-12 text-right">
                        <button class="glyphicon glyphicon-trash alert-danger btn"
                                ng-click="removeAggregation()"></button>
                    </div>

                </div>
            </div>

            <div class="col-md-12">
                <button class="glyphicon glyphicon-plus-sign btn-info btn" ng-click="addAggregation()">Add</button>
            </div>
        </div>

    </div>


    <!--    FILTERS     -->
    <div class="row ">

        <div class="col-md-2 text-right"><span class="badge">Filters</span></div>

        <div class="col-md-10 left-border">
            <% myTemplate = createLink(action: 'conditionGroup') %>
            <div ng-repeat="group in statement.where" ng-include="'${myTemplate}'"></div>
        </div>

    </div>

    <!--    SUBMIT BUTTON     -->
    <div class="row ">

        <div class="text-center">
            <div class="btn btn-info" ng-click="showStatement()">Generate Report</div>
        </div>

    </div>

</div>
</body>

</html>
