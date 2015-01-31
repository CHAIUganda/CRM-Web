<%--
  Created by IntelliJ IDEA.
  User: kay
  Date: 1/23/2015
  Time: 11:43 AM
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="kickstart"/>
    <g:set var="entityName" value="${message(code: 'task.label', default: 'Task')}"/>
    <title>Generate Tasks</title>
    <g:set var="layout_nosecondarymenu" value="${true}" scope="request"/>
</head>

<body>

<h1>
    Generate ${taskType}
</h1>

<g:form action="${actionName == 'generationDetailer' ? 'generateDetailerTasks' : 'generateOrderTasks'}">
    <div class="form-horizontal">

        %{--<div class="form-group">--}%
        %{--<label for="numberOfTasks" class="col-md-2 control-label">Average Number Of Task Per Day</label>--}%
        %{----}%
        %{--<div class="col-md-5"><g:field type="number" name="numberOfTasks" class="form-control"/></div>--}%
        %{--</div>--}%

        %{--<div class="form-group">--}%
        %{--<label for="numberOfTasks" class="col-md-2 control-label">Tasks Per Territory</label>--}%

        %{--<div class="col-md-5"><g:field type="number" name="numberOfTasks" class="form-control"/></div>--}%
        %{--</div>--}%

        <div class="form-group">
            <label for="startDate" class="col-md-2 control-label">Start Date</label>

            <div class="col-md-5"><bs:datePicker class="form-control" name="startDate"/></div>
        </div>

        <div class="form-group">
            <label class="col-md-2 control-label">Segment Tasks Per Territory</label>

            <div class="col-md-10">
                <g:each in="${segments}" var="s" status="i">
                    <div class="col-md-3">
                        <label for="segments.${s.id}">${s.name}</label>
                        <g:field name="segments.${s.id}" type="number"/>
                    </div>

                </g:each>
            </div>

        </div>

        <div class="form-group">
            <label for="workDays" class="col-md-2 control-label">Select Work Days</label>

            <div class="col-md-10">
                <g:each in="${["$Calendar.MONDAY"   : 'Mon',
                               "$Calendar.TUESDAY"  : 'Tue',
                               "$Calendar.WEDNESDAY": 'Wed',
                               "$Calendar.THURSDAY" : 'Thu',
                               "$Calendar.FRIDAY"   : 'Fri',
                               "$Calendar.SATURDAY" : 'Sat',
                               "$Calendar.SUNDAY"   : 'Sun']}" var="day" status="i">
                    <input id="workDays" type="checkbox" name="workDays" value="${day.key}">
                    <label for="workDays">${day.value}</label>
                </g:each>
            </div>

        </div>

        <div class="form-group">
            <label for="territories" class="col-md-2 control-label">Territories</label>

            <div class="col-md-10">
                <div class="row">

                    <g:each in="${territories}" var="t">
                        <div class="col-md-3">
                            <input id="territories" type="checkbox" name="territories" value="${t.id}">
                            <label for="territories">${t}</label>
                        </div>
                    </g:each>
                </div>

            </div>
        </div>

        <div class="form-group">
            <div class="col-md-offset-2 col-sm-10">
                <g:submitButton class="btn btn-primary" name="Generate Tasks"/>
            </div>
        </div>
    </div>
</g:form>

</body>
</html>