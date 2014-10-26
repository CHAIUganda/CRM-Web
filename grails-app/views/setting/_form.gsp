<%@ page import="com.omnitech.chai.model.Setting" %>



			<div class="${hasErrors(bean: settingInstance, field: 'name', 'error')} ">
				<label for="name" class="control-label"><g:message code="setting.name.label" default="Name" /></label>
				<div>
					<g:textField class='form-control' name="name" value="${settingInstance?.name}" />
					<span class="help-inline">${hasErrors(bean: settingInstance, field: 'name', 'error')}</span>
				</div>
			</div>

			<div class="${hasErrors(bean: settingInstance, field: 'value', 'error')} ">
				<label for="value" class="control-label"><g:message code="setting.value.label" default="Value" /></label>
				<div>
					<g:textArea rows="15" class='form-control'  name="value" value="${settingInstance?.value}" />
					<span class="help-inline">${hasErrors(bean: settingInstance, field: 'value', 'error')}</span>
				</div>
			</div>

