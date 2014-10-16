<%@ page import="com.omnitech.chai.model.Task" %>



			<div class="${hasErrors(bean: taskInstance, field: 'description', 'error')} ">
				<label for="description" class="control-label"><g:message code="task.description.label" default="Description" /></label>
				<div>
					<g:textField class='form-control' style="width: 50%;" name="description" value="${taskInstance?.description}" />
					<span class="help-inline">${hasErrors(bean: taskInstance, field: 'description', 'error')}</span>
				</div>
			</div>

			<div class="${hasErrors(bean: taskInstance, field: 'status', 'error')} ">
				<label for="status" class="control-label"><g:message code="task.status.label" default="Status" /></label>
				<div>
					<g:textField class='form-control' style="width: 50%;" name="status" value="${taskInstance?.status}" />
					<span class="help-inline">${hasErrors(bean: taskInstance, field: 'status', 'error')}</span>
				</div>
			</div>

