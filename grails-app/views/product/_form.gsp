<%@ page import="com.omnitech.chai.model.Product" %>



			<div class="${hasErrors(bean: productInstance, field: 'metric', 'error')} ">
				<label for="metric" class="control-label"><g:message code="product.metric.label" default="Metric" /></label>
				<div>
					<g:textField class='form-control' style="width: 50%;" name="metric" value="${productInstance?.metric}" />
					<span class="help-inline">${hasErrors(bean: productInstance, field: 'metric', 'error')}</span>
				</div>
			</div>

			<div class="${hasErrors(bean: productInstance, field: 'name', 'error')} ">
				<label for="name" class="control-label"><g:message code="product.name.label" default="Name" /></label>
				<div>
					<g:textField class='form-control' style="width: 50%;" name="name" value="${productInstance?.name}" />
					<span class="help-inline">${hasErrors(bean: productInstance, field: 'name', 'error')}</span>
				</div>
			</div>

			<div class="${hasErrors(bean: productInstance, field: 'unitPrice', 'error')} ">
				<label for="unitPrice" class="control-label"><g:message code="product.unitPrice.label" default="Unit Price" /></label>
				<div>
					<g:field class='form-control' style="width: 50%;" type="number" name="unitPrice" value="${productInstance.unitPrice}" />
					<span class="help-inline">${hasErrors(bean: productInstance, field: 'unitPrice', 'error')}</span>
				</div>
			</div>

