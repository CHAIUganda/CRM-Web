<%@ page import="com.omnitech.chai.model.District" %>

            <div class="${hasErrors(bean: districtInstance, field: 'name', 'error')} ">
                <label for="name" class="control-label"><g:message code="district.name.label" default="Region" /></label>
                <div>
                    <g:select name="region.id" from="${regions}" optionKey="id"  required="" class="form-control" style="width: 50%" value="${districtInstance?.region?.id}"/>
                    <span class="help-inline">${hasErrors(bean: districtInstance, field: 'name', 'error')}</span>
                </div>
            </div>

			<div class="${hasErrors(bean: districtInstance, field: 'name', 'error')} ">
				<label for="name" class="control-label"><g:message code="district.name.label" default="Name" /></label>
				<div>
					<g:textField class='form-control' style="width: 50%;" name="name" value="${districtInstance?.name}" />
					<span class="help-inline">${hasErrors(bean: districtInstance, field: 'name', 'error')}</span>
				</div>
			</div>

			<div class="${hasErrors(bean: districtInstance, field: 'uuid', 'error')} ">
				<label for="uuid" class="control-label"><g:message code="district.uuid.label" default="Uuid" /></label>
				<div>
					<g:textField class='form-control' style="width: 50%;" name="uuid" value="${districtInstance?.uuid}" />
					<span class="help-inline">${hasErrors(bean: districtInstance, field: 'uuid', 'error')}</span>
				</div>
			</div>

