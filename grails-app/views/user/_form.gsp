<%@ page import="com.omnitech.chai.model.User" %>
<div class="user-table">
    <div class="user-row">
        <div class="user-column">

			<div class="${hasErrors(bean: userInstance, field: 'username', 'error')} required">
				<label for="username" class="control-label"><g:message code="user.username.label" default="Username" /><span class="required-indicator">*</span></label>
				<div>
					<g:textField class="form-control" name="username" required="" value="${userInstance?.username}"/>
					<span class="help-inline">${hasErrors(bean: userInstance, field: 'username', 'error')}</span>
				</div>
			</div>

			<div class="${hasErrors(bean: userInstance, field: 'password', 'error')} required">
				<label for="password" class="control-label"><g:message code="user.password.label" default="Password" /><span class="required-indicator">*</span></label>
				<div>
                    <g:field class="form-control" style="width: 50%;" type="password" name="password" required="" value="${userInstance?.password}"/>
					<span class="help-inline">${hasErrors(bean: userInstance, field: 'password', 'error')}</span>
				</div>
			</div>

			<div class="${hasErrors(bean: userInstance, field: 'accountExpired', 'error')} ">
				<label for="accountExpired" class="control-label"><g:message code="user.accountExpired.label" default="Account Expired" /></label>
				<div>
					<bs:checkBox name="accountExpired" value="${userInstance?.accountExpired}" />
					<span class="help-inline">${hasErrors(bean: userInstance, field: 'accountExpired', 'error')}</span>
				</div>
			</div>

			<div class="${hasErrors(bean: userInstance, field: 'accountLocked', 'error')} ">
				<label for="accountLocked" class="control-label"><g:message code="user.accountLocked.label" default="Account Locked" /></label>
				<div>
					<bs:checkBox name="accountLocked" value="${userInstance?.accountLocked}" />
					<span class="help-inline">${hasErrors(bean: userInstance, field: 'accountLocked', 'error')}</span>
				</div>
			</div>

			<div class="${hasErrors(bean: userInstance, field: 'enabled', 'error')} ">
				<label for="enabled" class="control-label"><g:message code="user.enabled.label" default="Enabled" /></label>
				<div>
					<bs:checkBox name="enabled" value="${userInstance?.enabled}" />
					<span class="help-inline">${hasErrors(bean: userInstance, field: 'enabled', 'error')}</span>
				</div>
			</div>

			<div class="${hasErrors(bean: userInstance, field: 'passwordExpired', 'error')} ">
				<label for="passwordExpired" class="control-label"><g:message code="user.passwordExpired.label" default="Password Expired" /></label>
				<div>
					<bs:checkBox name="passwordExpired" value="${userInstance?.passwordExpired}" />
					<span class="help-inline">${hasErrors(bean: userInstance, field: 'passwordExpired', 'error')}</span>
				</div>
			</div>

        </div>

        <div class="user-column thumbnail">
            <div class="badge alert-info">ROLES</div>
            <div>
                <div class="user-table">
                        <g:each in="${rolez}" var="role" status="i">
                                <div class="user-column">
                                    <g:checkBox name="rolez" value="${role.id}"
                                                checked="${userInstance.hasRole(role)}"/>
                                    <label for="roles[${i}]">${role.authority}</label>
                                </div>

                        </g:each>
                </div>
            </div>
        </div>

        <div class="user-column thumbnail">
            <div class="badge alert-info">DEVICES</div>
            <div>
                <div class="user-table">
                    <g:each in="${rolez}" var="role" status="i">
                        <div class="user-column thumbnail">
                            <g:radio name="rolez" value="${role.id}"
                                        checked="${userInstance.hasRole(role)}"/>
                            <label for="roles[${i}]">${role.authority}</label>
                        </div>

                    </g:each>
                </div>
            </div>
        </div>
    </div>

</div>



