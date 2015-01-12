<%@ page import="com.omnitech.chai.model.Task" %>
<!DOCTYPE html>
<html>

<head>
    <meta name="layout" content="kickstart"/>
    <g:set var="entityName" value="${message(code: 'task.label', default: 'Task')}"/>
    <title><g:message code="default.create.label" args="[entityName]"/></title>
    <g:if test="${taskInstance?.customer}">
        <g:javascript>
            _orderCustomer = {id: ${taskInstance.customer?.id},outletName:'${taskInstance?.customer?.outletName}'};
        </g:javascript>
    </g:if>
</head>

<body>

<section id="create-task" class="first" ng-controller="CallCtrl">

    <g:hasErrors bean="${taskInstance}">
        <div class="alert alert-danger">
            <g:renderErrors bean="${taskInstance}" as="list"/>
        </div>
    </g:hasErrors>

    <g:form action="saveOrUpdateCall">

        <g:if test="${taskInstance?.id}">
            <g:hiddenField name="id" value="${taskInstance?.id}"/>
        </g:if>

        <g:render template="simpleCallForm"/>

        <div class="form-actions margin-top-medium">
            <g:submitButton name="create" class="btn btn-primary"
                            value="${message(code: 'default.button.create.label', default: 'Create')}"/>
            <button class="btn" type="reset"><g:message code="default.button.reset.label" default="Reset"/></button>
        </div>
    </g:form>

</section>
<r:require modules="angular,angular-resource,angular-ui"/>
<g:javascript src="services/Common.js"/>
<g:javascript src="controllers/CallCtrl.js"/>
</body>

</html>
