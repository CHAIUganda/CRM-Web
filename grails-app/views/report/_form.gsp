<%@ page import="com.omnitech.chai.model.Report" %>


<div class="${hasErrors(bean: reportInstance, field: 'group', 'error')} ">
    <label for="group" class="control-label"><g:message code="report.group.label" default="Group"/></label>

    <div>
        <g:select class="form-control" id="group" name="group.id" from="${reportGroups}"
                  noSelection="${[null: 'No Group']}"
                  optionKey="id" required="" value="${reportInstance?.group?.id}"/>
        <span class="help-inline">${hasErrors(bean: reportInstance, field: 'group', 'error')}</span>
    </div>
</div>

<div class="${hasErrors(bean: reportInstance, field: 'name', 'error')} ">
    <label for="name" class="control-label"><g:message code="report.name.label" default="Name"/></label>

    <div>
        <g:textField class='form-control' name="name" value="${reportInstance?.name}"/>
        <span class="help-inline">${hasErrors(bean: reportInstance, field: 'name', 'error')}</span>
    </div>
</div>

<div class="${hasErrors(bean: reportInstance, field: 'type', 'error')} ">
    <label for="type" class="control-label"><g:message code="report.type.label" default="Type"/></label>

    <div>
        <g:textField class='form-control' name="type" value="${reportInstance?.type}"/>
        <span class="help-inline">${hasErrors(bean: reportInstance, field: 'type', 'error')}</span>
    </div>
</div>

<div class="${hasErrors(bean: reportInstance, field: 'fields', 'error')} ">
    <label for="fields" class="control-label"><g:message code="report.fields.label" default="Fields"/></label>

    <div>
        <g:textArea class='form-control' rows="5" name="fields" value="${reportInstance?.fields}"/>
        <span class="help-inline">${hasErrors(bean: reportInstance, field: 'fields', 'error')}</span>
    </div>
</div>

<div class="${hasErrors(bean: reportInstance, field: 'script', 'error')} ">
    <label for="script" class="control-label"><g:message code="report.script.label" default="Script"/></label>

    <div>
        <g:textArea class='form-control' rows="5" name="script" value="${reportInstance?.script}"/>
        <span class="help-inline">${hasErrors(bean: reportInstance, field: 'script', 'error')}</span>
    </div>
</div>



