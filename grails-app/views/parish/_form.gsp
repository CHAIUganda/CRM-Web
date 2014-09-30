<%@ page import="com.omnitech.chai.model.Parish" %>



			<div class="${hasErrors(bean: parishInstance, field: 'name', 'error')} ">
				<label for="name" class="control-label"><g:message code="parish.name.label" default="Name" /></label>
				<div>
					<g:textField class='form-control' style="width: 50%;" name="name" value="${parishInstance?.name}" />
					<span class="help-inline">${hasErrors(bean: parishInstance, field: 'name', 'error')}</span>
				</div>
			</div>

			<div class="${hasErrors(bean: parishInstance, field: 'subCounty', 'error')} ">
				<label for="subCounty" class="control-label"><g:message code="parish.subCounty.label" default="Sub County" /></label>
				<div>
					<g:select class="form-control" style="width: 50%;" id="subCounty" name="subCounty.id" from="${subCountys}" optionKey="id" required="" value="${parishInstance?.subCounty?.id}" optionValue="description"/>
					<span class="help-inline">${hasErrors(bean: parishInstance, field: 'subCounty', 'error')}</span>
				</div>
			</div>

