<%@ page import="com.omnitech.chai.model.ProductGroup" %>



			<div class="${hasErrors(bean: productGroupInstance, field: 'name', 'error')} ">
				<label for="name" class="control-label"><g:message code="productGroup.name.label" default="Name" /></label>
				<div>
					<g:textField class='form-control' style="width: 50%;" name="name" value="${productGroupInstance?.name}" />
					<span class="help-inline">${hasErrors(bean: productGroupInstance, field: 'name', 'error')}</span>
				</div>
			</div>

			<div class="${hasErrors(bean: productGroupInstance, field: 'parent', 'error')} ">
				<label for="parent" class="control-label"><g:message code="productGroup.parent.label" default="Parent" /></label>
				<div>
					<g:select class="form-control" style="width: 50%;" id="parent" name="parent.id" from="${otherGroups}" optionKey="id" value="${productGroupInstance?.parent?.id}" noSelection="['null': '']"/>
					<span class="help-inline">${hasErrors(bean: productGroupInstance, field: 'parent', 'error')}</span>
				</div>
			</div>

