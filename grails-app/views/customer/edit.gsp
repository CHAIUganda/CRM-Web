<%@ page import="com.omnitech.chai.model.Customer" %>
<!DOCTYPE html>
<html>

<head>
	<meta name="layout" content="kickstart" />
	<g:set var="entityName" value="${message(code: 'customer.label', default: 'Customer')}" />
	<title><g:message code="default.edit.label" args="[entityName]" /></title>
    <g:javascript>
        customerContacts = ${raw(jsonContacts)};
    </g:javascript>
</head>

<body>

	<section id="edit-customer" class="first">

		<g:hasErrors bean="${customerInstance}">
		<div class="alert alert-danger">
			<g:renderErrors bean="${customerInstance}" as="list" />
		</div>
		</g:hasErrors>

		<g:form method="post" class="form-horizontal" role="form" ng-controller="CustomContactCtrl">
			<g:hiddenField name="id" value="${customerInstance?.id}" />
			<g:hiddenField name="_method" value="PUT" />
			
			<g:render template="form"/>
			
			<div class="form-actions margin-top-medium">
				<g:actionSubmit class="btn btn-primary" action="update" value="${message(code: 'default.button.update.label', default: 'Update')}" />
	            <button class="btn" type="reset"><g:message code="default.button.reset.label" default="Reset" /></button>
			</div>
		</g:form>

	</section>
<g:javascript src="controllers/CustomContactCtrl.js"/>
<r:require modules="angular"/>
</body>

</html>
