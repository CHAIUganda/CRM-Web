<%@ page import="com.omnitech.chai.model.Device" %>



			<div class="${hasErrors(bean: deviceInstance, field: 'imei', 'error')} ">
				<label for="imei" class="control-label"><g:message code="device.imei.label" default="Imei" /></label>
				<div>
					<g:textField class='form-control' style="width: 50%;" name="imei" value="${deviceInstance?.imei}" />
					<span class="help-inline">${hasErrors(bean: deviceInstance, field: 'imei', 'error')}</span>
				</div>
			</div>

			<div class="${hasErrors(bean: deviceInstance, field: 'model', 'error')} ">
				<label for="model" class="control-label"><g:message code="device.model.label" default="Model" /></label>
				<div>
					<g:textField class='form-control' style="width: 50%;" name="model" value="${deviceInstance?.model}" />
					<span class="help-inline">${hasErrors(bean: deviceInstance, field: 'model', 'error')}</span>
				</div>
			</div>

