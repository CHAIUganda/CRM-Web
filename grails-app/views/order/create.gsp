<%@ page import="com.omnitech.chai.model.Task" %>
<!DOCTYPE html>
<html>

<head>
    <meta name="layout" content="kickstart"/>
    <g:set var="entityName" value="${message(code: 'task.label', default: 'Task')}"/>
    <title><g:message code="default.create.label" args="[entityName]"/></title>
    <script type="application/javascript">
        _products =
        ${raw(jsProducts)}
    </script>
</head>

<body>

<section id="create-task" class="first" ng-controller="OrderCtrl">

    <g:hasErrors bean="${taskInstance}">
        <div class="alert alert-danger">
            <g:renderErrors bean="${taskInstance}" as="list"/>
        </div>
    </g:hasErrors>


    <g:render template="form"/>


</section>
<r:require modules="angular,angular-resource,angular-ui"/>
<g:javascript src="models/Domain.js"/>
<g:javascript src="services/Common.js"/>
<g:javascript src="controllers/OrderCtrl.js"/>
</body>

</html>
