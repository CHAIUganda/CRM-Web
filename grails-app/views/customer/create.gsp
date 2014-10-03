<%@ page import="com.omnitech.chai.model.Customer" %>
<!DOCTYPE html>
<html>

<head>
	<meta name="layout" content="kickstart" />
	<g:set var="entityName" value="${message(code: 'customer.label', default: 'Customer')}" />
	<title><g:message code="default.create.label" args="[entityName]" /></title>
    <g:javascript>
        customerContacts = ${raw(jsonContacts)};
    </g:javascript>
    <g:javascript src="controllers/CustomContactCtrl.js"/>
    <r:require modules="angular"/>
</head>

<body>

	<section id="create-customer" class="first">

		<g:hasErrors bean="${customerInstance}">
		<div class="alert alert-danger">
			<g:renderErrors bean="${customerInstance}" as="list" />
		</div>
		</g:hasErrors>

		<g:form action="save" class="form-horizontal" role="form" ng-controller="CustomContactCtrl" >
			<g:render template="form"/>

			<div class="form-actions margin-top-medium">
				<g:submitButton name="create" class="btn btn-primary" value="${message(code: 'default.button.create.label', default: 'Create')}" />
	            <button class="btn" type="reset"><g:message code="default.button.reset.label" default="Reset" /></button>
			</div>
		</g:form>

	</section>

</body>

</html>
