<%@ page import="com.omnitech.mis.Parish" %>



			<div class="${hasErrors(bean: parishInstance, field: 'name', 'error')} ">
				<label for="name" class="control-label"><g:message code="parish.name.label" default="Name" /></label>
				<div>
					<g:textField class="form-control" name="name" value="${parishInstance?.name}"/>
					<span class="help-inline">${hasErrors(bean: parishInstance, field: 'name', 'error')}</span>
				</div>
			</div>

			<div class="${hasErrors(bean: parishInstance, field: 'subcounty', 'error')} required">
				<label for="subcounty" class="control-label"><g:message code="parish.subcounty.label" default="Subcounty" /><span class="required-indicator">*</span></label>
				<div>
					<g:select class="form-control" id="subcounty" name="subcounty.id" from="${com.omnitech.mis.SubCounty.list()}" optionKey="id" required="" value="${parishInstance?.subcounty?.id}" class="many-to-one"/>
					<span class="help-inline">${hasErrors(bean: parishInstance, field: 'subcounty', 'error')}</span>
				</div>
			</div>

