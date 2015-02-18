<%@ page import="com.omnitech.chai.model.Territory" %>



<div class="${hasErrors(bean: territoryInstance, field: 'name', 'error')} ">
    <label for="name" class="control-label"><g:message code="territory.name.label" default="Name"/></label>

    <div>
        <g:textField class='form-control' style="width: 50%;" name="name" value="${territoryInstance?.name}"/>
        <span class="help-inline">${hasErrors(bean: territoryInstance, field: 'name', 'error')}</span>
    </div>
</div>

<div class="${hasErrors(bean: territoryInstance, field: 'name', 'error')} ">
    <label for="name" class="control-label"><g:message code="territory.type.label" default="Type"/></label>

    <div>
        <g:select name="type" from="${[Territory.TYPE_DETAILING, Territory.TYPE_SALES]}" value="${territoryInstance?.type}" class="form-control" style="width: 50%;"/>
        <span class="help-inline">${hasErrors(bean: territoryInstance, field: 'type', 'error')}</span>
    </div>
</div>

