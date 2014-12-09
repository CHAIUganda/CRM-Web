<%@ page import="com.omnitech.chai.model.Village" %>



			<div class="${hasErrors(bean: villageInstance, field: 'name', 'error')} ">
				<label for="name" class="control-label"><g:message code="village.name.label" default="Name" /></label>
				<div>
					<g:textField class='form-control' style="width: 50%;" name="name" value="${villageInstance?.name}" required="" />
					<span class="help-inline">${hasErrors(bean: villageInstance, field: 'name', 'error')}</span>
				</div>
			</div>


