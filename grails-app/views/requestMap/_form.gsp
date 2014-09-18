<%@ page import="com.omnitech.mis.RequestMap" %>



			<div class="${hasErrors(bean: requestMapInstance, field: 'url', 'error')} required">
				<label for="url" class="control-label"><g:message code="requestMap.url.label" default="Url" /><span class="required-indicator">*</span></label>
				<div>
					<g:textField class="form-control" name="url" required="" value="${requestMapInstance?.url}"/>
					<span class="help-inline">${hasErrors(bean: requestMapInstance, field: 'url', 'error')}</span>
				</div>
			</div>

			<div class="${hasErrors(bean: requestMapInstance, field: 'configAttribute', 'error')} required">
				<label for="configAttribute" class="control-label"><g:message code="requestMap.configAttribute.label" default="Config Attribute" /><span class="required-indicator">*</span></label>
				<div>
					<g:textField class="form-control" name="configAttribute" required="" value="${requestMapInstance?.configAttribute}"/>
					<span class="help-inline">${hasErrors(bean: requestMapInstance, field: 'configAttribute', 'error')}</span>
				</div>
			</div>

			<div class="${hasErrors(bean: requestMapInstance, field: 'httpMethod', 'error')} ">
				<label for="httpMethod" class="control-label"><g:message code="requestMap.httpMethod.label" default="Http Method" /></label>
				<div>
					<g:select class="form-control" name="httpMethod" from="${org.springframework.http.HttpMethod?.values()}" keys="${org.springframework.http.HttpMethod.values()*.name()}" value="${requestMapInstance?.httpMethod?.name()}" noSelection="['': '']"/>
					<span class="help-inline">${hasErrors(bean: requestMapInstance, field: 'httpMethod', 'error')}</span>
				</div>
			</div>

