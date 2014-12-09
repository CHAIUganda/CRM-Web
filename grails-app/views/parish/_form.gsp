<%@ page import="com.omnitech.chai.model.Parish" %>



			<div class="${hasErrors(bean: parishInstance, field: 'name', 'error')} ">
				<label for="name" class="control-label"><g:message code="parish.name.label" default="Name" /></label>
				<div>
					<g:textField class='form-control' style="width: 50%;" name="name" value="${parishInstance?.name}" />
					<span class="help-inline">${hasErrors(bean: parishInstance, field: 'name', 'error')}</span>
				</div>
			</div>

