<%@ page import="com.omnitech.chai.model.User" %>
<div>
    <div class="row ">
        <div class="col-md-6 thumbnail">

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
                    <g:field class="form-control" type="password" name="password" required="" value="${userInstance?.password}"/>
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
       %{--ROLEZ--}%
        <div class="col-md-6 thumbnail">
            <div class="row">
                <div class="col-md-12 text-center">
                    <legend>Roles</legend>
                </div>
            </div>

            <div class="row">
                        <g:each in="${rolez}" var="role" status="i">
                                <div class="col-md-5">
                                    <g:checkBox name="rolez" value="${role.id}"
                                                checked="${userInstance.hasRole(role)}"/>
                                    <label for="roles[${i}]">${role.authority}</label>
                                </div>

                        </g:each>
            </div>
        </div>
    </div>
    %{--SELECT DEVICE--}%
    <div class="row">
        <div class="row">
            <legend class="text-center">Select A Device For This User</legend>
        </div>

        <div class="row ">
            <div class="col-md-12">
                <g:each in="${devices}" var="device" status="i">
                    <div class="col-md-4 thumbnail  ${userInstance?.device?.id == device.id ? 'alert-info' : ''}">
                        <g:radio name="dvc" value="${device.id}"
                                 checked="${userInstance?.device?.id == device.id}"/>
                        <label for="dvc">${device}</label>
                    </div>

                </g:each>

                <div class="col-md-4 thumbnail">
                    <g:radio name="dvc" value=""/>
                    <label for="dvc">None</label>
                </div>
            </div>
        </div>
    </div>

</div>



