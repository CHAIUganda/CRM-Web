<%@ page import="com.omnitech.mis.District" %>



			<div class="${hasErrors(bean: districtInstance, field: 'name', 'error')} required">
				<label for="name" class="control-label"><g:message code="district.name.label" default="Name" /><span class="required-indicator">*</span></label>
				<div>
					<g:textField class="form-control" name="name" required="" value="${districtInstance?.name}"/>
					<span class="help-inline">${hasErrors(bean: districtInstance, field: 'name', 'error')}</span>
				</div>
			</div>

			<div class="${hasErrors(bean: districtInstance, field: 'subcounties', 'error')} ">
				<label for="subcounties" class="control-label"><g:message code="district.subcounties.label" default="Subcounties" /></label>
				<div>
					
<ul class="one-to-many">
<g:each in="${districtInstance?.subcounties?}" var="s">
    <li><g:link controller="subCounty" action="show" id="${s.id}">${s?.encodeAsHTML()}</g:link></li>
</g:each>
<li class="add">
<g:link controller="subCounty" action="create" params="['district.id': districtInstance?.id]">${message(code: 'default.add.label', args: [message(code: 'subCounty.label', default: 'SubCounty')])}</g:link>
</li>
</ul>

					<span class="help-inline">${hasErrors(bean: districtInstance, field: 'subcounties', 'error')}</span>
				</div>
			</div>

