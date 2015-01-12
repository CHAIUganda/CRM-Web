<%@ page import="com.omnitech.chai.model.Task" %>
<!DOCTYPE html>
<html>

<head>
    <g:set var="entityName" value="${message(code: 'order.label', default: 'Call')}" scope="request"/>
	<meta name="layout" content="kickstart" />
	<title><g:message code="default.edit.label" args="[entityName]" /></title>
</head>

<body>

	<section id="edit-task" class="first">

		<g:hasErrors bean="${taskInstance}">
		<div class="alert alert-danger">
			<g:renderErrors bean="${taskInstance}" as="list" />
		</div>
		</g:hasErrors>

		<g:form method="post" class="form-horizontal" role="form" >
			<g:hiddenField name="id" value="${taskInstance?.id}" />
			<g:hiddenField name="_method" value="PUT" />

			<g:render template="simpleCallForm"/>

			<div class="form-actions margin-top-medium">
				<g:submitButton class="btn btn-primary" action="update" value="${message(code: 'default.button.update.label', default: 'Update')}" />
	            <button class="btn" type="reset"><g:message code="default.button.reset.label" default="Reset" /></button>
			</div>
		</g:form>

	</section>

<r:require modules="chosen"/>
<g:javascript>
    $(".chzn-select").chosen({enable_split_word_search:true});
</g:javascript>
</body>
</body>

</html>
