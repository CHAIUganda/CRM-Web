<%@ page import="com.omnitech.chai.model.Region" %>



			<div class="${hasErrors(bean: regionInstance, field: 'name', 'error')} ">
				<label for="name" class="control-label"><g:message code="region.name.label" default="Name" /></label>
				<div>
					<g:textField class='form-control' style="width: 50%;" name="name" value="${regionInstance?.name}" />
					<span class="help-inline">${hasErrors(bean: regionInstance, field: 'name', 'error')}</span>
				</div>
			</div>

			<div class="${hasErrors(bean: regionInstance, field: 'uuid', 'error')} ">
				<label for="uuid" class="control-label"><g:message code="region.uuid.label" default="Uuid" /></label>
				<div>
					<g:textField class='form-control' style="width: 50%;" name="uuid" value="${regionInstance?.uuid}" />
					<span class="help-inline">${hasErrors(bean: regionInstance, field: 'uuid', 'error')}</span>
				</div>
			</div>

