<%@ page import="com.omnitech.chai.model.Village" %>



			<div class="${hasErrors(bean: villageInstance, field: 'name', 'error')} ">
				<label for="name" class="control-label"><g:message code="village.name.label" default="Name" /></label>
				<div>
					<g:textField class='form-control' style="width: 50%;" name="name" value="${villageInstance?.name}" required="" />
					<span class="help-inline">${hasErrors(bean: villageInstance, field: 'name', 'error')}</span>
				</div>
			</div>

			<div class="${hasErrors(bean: villageInstance, field: 'parish', 'error')} ">
				<label for="parish" class="control-label"><g:message code="village.parish.label" default="Parish" /></label>
				<div>
					<g:select class="form-control" style="width: 50%;" id="parish" name="parish.id" from="${parishs}" optionKey="id" required="" value="${villageInstance?.parish?.id}" />
					<span class="help-inline">${hasErrors(bean: villageInstance, field: 'parish', 'error')}</span>
				</div>
			</div>

