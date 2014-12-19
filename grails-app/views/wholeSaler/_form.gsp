<%@ page import="com.omnitech.chai.model.WholeSaler" %>



			<div class="${hasErrors(bean: wholeSalerInstance, field: 'contact', 'error')} ">
				<label for="contact" class="control-label"><g:message code="wholeSaler.contact.label" default="Contact" /></label>
				<div>
					<g:textField class='form-control' style="width: 50%;" name="contact" value="${wholeSalerInstance?.contact}" />
					<span class="help-inline">${hasErrors(bean: wholeSalerInstance, field: 'contact', 'error')}</span>
				</div>
			</div>

			<div class="${hasErrors(bean: wholeSalerInstance, field: 'name', 'error')} ">
				<label for="name" class="control-label"><g:message code="wholeSaler.name.label" default="Name" /></label>
				<div>
					<g:textField class='form-control' style="width: 50%;" name="name" value="${wholeSalerInstance?.name}" />
					<span class="help-inline">${hasErrors(bean: wholeSalerInstance, field: 'name', 'error')}</span>
				</div>
			</div>

