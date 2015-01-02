<%@ page import="com.omnitech.chai.model.ReportGroup" %>



			<div class="${hasErrors(bean: reportGroupInstance, field: '_dateCreated', 'error')} ">
				<label for="_dateCreated" class="control-label"><g:message code="reportGroup._dateCreated.label" default="Date Created" /></label>
				<div>
					<g:textField class='form-control' style="width: 50%;" name="_dateCreated" value="${reportGroupInstance?._dateCreated}" />
					<span class="help-inline">${hasErrors(bean: reportGroupInstance, field: '_dateCreated', 'error')}</span>
				</div>
			</div>

			<div class="${hasErrors(bean: reportGroupInstance, field: '_dateLastUpdated', 'error')} ">
				<label for="_dateLastUpdated" class="control-label"><g:message code="reportGroup._dateLastUpdated.label" default="Date Last Updated" /></label>
				<div>
					<g:textField class='form-control' style="width: 50%;" name="_dateLastUpdated" value="${reportGroupInstance?._dateLastUpdated}" />
					<span class="help-inline">${hasErrors(bean: reportGroupInstance, field: '_dateLastUpdated', 'error')}</span>
				</div>
			</div>

			<div class="${hasErrors(bean: reportGroupInstance, field: 'name', 'error')} ">
				<label for="name" class="control-label"><g:message code="reportGroup.name.label" default="Name" /></label>
				<div>
					<g:textField class='form-control' style="width: 50%;" name="name" value="${reportGroupInstance?.name}" />
					<span class="help-inline">${hasErrors(bean: reportGroupInstance, field: 'name', 'error')}</span>
				</div>
			</div>

			<div class="${hasErrors(bean: reportGroupInstance, field: 'parent', 'error')} ">
				<label for="parent" class="control-label"><g:message code="reportGroup.parent.label" default="Parent" /></label>
				<div>
					<g:select class="form-control" style="width: 50%;" id="parent" name="parent.id" from="${reportGroups}" noSelection="${[ null:'No Group']}" optionKey="id"  value="${reportGroupInstance?.parent?.id}" class="many-to-one"/>
					<span class="help-inline">${hasErrors(bean: reportGroupInstance, field: 'parent', 'error')}</span>
				</div>
			</div>

