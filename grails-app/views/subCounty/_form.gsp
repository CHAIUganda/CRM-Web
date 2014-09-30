<%@ page import="com.omnitech.chai.model.SubCounty" %>


<div class="${hasErrors(bean: subCountyInstance, field: 'district', 'error')} ">
    <label for="district" class="control-label"><g:message code="subCounty.district.label" default="District"/></label>

    <div>
        <g:select class='form-control' style="width: 50%;" name="district.id" from="${districts}" optionKey="id"
                  value="${subCountyInstance?.district?.id}"/>
        <span class="help-inline">${hasErrors(bean: subCountyInstance, field: 'district', 'error')}</span>
    </div>
</div>


<div class="${hasErrors(bean: subCountyInstance, field: 'name', 'error')} ">
    <label for="name" class="control-label"><g:message code="subCounty.name.label" default="Name"/></label>

    <div>
        <g:textField class='form-control' style="width: 50%;" name="name" value="${subCountyInstance?.name}"/>
        <span class="help-inline">${hasErrors(bean: subCountyInstance, field: 'name', 'error')}</span>
    </div>
</div>


