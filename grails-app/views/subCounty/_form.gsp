<%@ page import="com.omnitech.mis.SubCounty" %>



			<div class="${hasErrors(bean: subCountyInstance, field: 'district', 'error')} required">
				<label for="district" class="control-label"><g:message code="subCounty.district.label" default="District" /><span class="required-indicator">*</span></label>
				<div>
					<g:select class="form-control" id="district" name="district.id" from="${com.omnitech.mis.District.list()}" optionKey="id" required="" value="${subCountyInstance?.district?.id}" class="many-to-one"/>
					<span class="help-inline">${hasErrors(bean: subCountyInstance, field: 'district', 'error')}</span>
				</div>
			</div>

			<div class="${hasErrors(bean: subCountyInstance, field: 'name', 'error')} ">
				<label for="name" class="control-label"><g:message code="subCounty.name.label" default="Name" /></label>
				<div>
					<g:textField class="form-control" name="name" value="${subCountyInstance?.name}"/>
					<span class="help-inline">${hasErrors(bean: subCountyInstance, field: 'name', 'error')}</span>
				</div>
			</div>

			<div class="${hasErrors(bean: subCountyInstance, field: 'parishes', 'error')} ">
				<label for="parishes" class="control-label"><g:message code="subCounty.parishes.label" default="Parishes" /></label>
				<div>
					
<ul class="one-to-many">
<g:each in="${subCountyInstance?.parishes?}" var="p">
    <li><g:link controller="parish" action="show" id="${p.id}">${p?.encodeAsHTML()}</g:link></li>
</g:each>
<li class="add">
<g:link controller="parish" action="create" params="['subCounty.id': subCountyInstance?.id]">${message(code: 'default.add.label', args: [message(code: 'parish.label', default: 'Parish')])}</g:link>
</li>
</ul>

					<span class="help-inline">${hasErrors(bean: subCountyInstance, field: 'parishes', 'error')}</span>
				</div>
			</div>

