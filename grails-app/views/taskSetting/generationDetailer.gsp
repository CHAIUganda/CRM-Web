<%--
  Created by IntelliJ IDEA.
  User: kay
  Date: 1/23/2015
  Time: 11:43 AM
--%>

<%@ page import="com.omnitech.chai.util.ChaiUtils" contentType="text/html;charset=UTF-8" %>
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

        <div class="form-group">
            <label for="avgTasksPerDay" class="col-md-2 control-label">Average No.Task Per Day</label>

            <div class="col-md-5"><g:field type="number" name="avgTasksPerDay" class="form-control"
                                           value="${params.avgTasksPerDay ?: 8}" required="true"/></div>
        </div>

        <div class="form-group" style="display: none;">
            <label for="startDate" class="col-md-2 control-label">Start Date</label>

            %{--<div class="col-md-5"><bs:datePicker class="form-control" name="startDate"--}%
                                                 %{--value="${params.startDate ? Date.parse('yyyy-MM-dd', params.startDate) : new Date()}"/></div>--}%
            <div class="col-md-5 "><bs:datePicker class="form-control" name="startDate"
                                                 value="${new Date()}"/></div>
        </div>

        <div class="form-group" style="display: none;">
            <label class="col-md-2 control-label">Segment Tasks Per Territory</label>

            <div class="col-md-10">
                <g:each in="${segments}" var="s" status="i">
                    <div class="col-md-3">

                        <div class="input-group">
                            <span class="input-group-addon" style="padding: 0 0;">
                                <label>
                                    %{--${s}:<g:checkBox type="checkbox" name="segments.${s.id}_all" checked="${params."segments.${s.id}_all" == 'on'}"/>  All--}%
                                    ${s}:<g:hiddenField type="checkbox" name="segments.${s.id}_all" checked="${true}"/>  All
                                </label>
                            </span>
                            %{--<g:field name="segments.${s.id}" type="number"--}%
                                     %{--value="${params."segments.$s.id" ?: 10}" class="form-control"/>--}%
                            <g:field name="segments.${s.id}" type="number"
                                     value="${2000}" class="form-control"/>

                        </div>
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
                    <g:checkBox name="workDays" value="${day.key}"
                                checked="${params.workDays == "$day.key" || params.workDays?.contains("$day.key")}"/>
                    <label for="workDays">${day.value}</label>
                </g:each>
            </div>

        </div>

        <div class="form-group">
            <label for="territories" class="col-md-2 control-label">Territories:</label>
        </div>

        <div class="form-group">

            <div class="col-md-12">
                <div class="row">

                    <g:each in="${territories}" var="t">
                        <div class="col-md-3">
                            <g:checkBox id="territories" type="checkbox" name="territories" value="${t.id}"
                                        checked="${params.territories == "$t.id" || params.territories?.contains("$t.id")}"/>
                            <label for="territories">${ChaiUtils.truncateString(t.toString(), 25)}</label>
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