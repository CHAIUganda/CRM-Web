
<%@ page import="com.omnitech.chai.model.User" %>
<!DOCTYPE html>
<html>

<head>
	<meta name="layout" content="kickstart" />
	<g:set var="entityName" value="${message(code: 'user.label', default: 'User')}" />
	<title><g:message code="default.index.label" args="[entityName]" /></title>
</head>

<body>

<section id="index-user" class="first" ng-controller="UserTerritoryCtrl">

	<table class="table table-bordered margin-top-medium">
		<thead>
			<tr>
			
				<g:sortableColumn property="username" title="${message(code: 'user.username.label', default: 'Username')}" />

                <th> ${message(code: 'Device.label', default: 'Device')}</th>

                <g:sortableColumn property="dateCreated" title="${message(code: 'user.territory.label', default: 'Territory')}" />

				<g:sortableColumn property="accountExpired" title="${message(code: 'user.accountExpired.label', default: 'Account Expired')}" />
			
				<g:sortableColumn property="accountLocked" title="${message(code: 'user.accountLocked.label', default: 'Account Locked')}" />
			
				<g:sortableColumn property="enabled" title="${message(code: 'user.enabled.label', default: 'Enabled')}" />

				<th></th>
			
			</tr>
		</thead>
		<tbody>
		<g:each in="${userInstanceList}" status="i" var="userInstance">
			<tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
			
				<td><g:link action="show" id="${userInstance.id}">${fieldValue(bean: userInstance, field: "username")}</g:link></td>

                <td>${fieldValue(bean: userInstance, field: "device")}</td>

                <td>${fieldValue(bean: userInstance, field: "territory")}</td>

				<td><g:formatBoolean boolean="${userInstance.accountExpired}" /></td>
			
				<td><g:formatBoolean boolean="${userInstance.accountLocked}" /></td>
			
				<td><g:formatBoolean boolean="${userInstance.enabled}" /></td>

				<td>

					<a href="#" id="${userInstance.id}" title="Map To SubCounty"
					   data-target="#map-supervisor"
					   data-toggle="modal" ng-click="onRemap(${userInstance.id})">

						<i class="glyphicon glyphicon-transfer"></i>
					</a>
				</td>

			</tr>
		</g:each>
		</tbody>
	</table>
	<div>
		<bs:paginate total="${userInstanceCount}"
                     id="${params.action == 'search' ? (params.term ?: params.id) : null}"/>
	</div>
</section>

<g:render template="mapSupervisor"/>

<r:require modules="angular,angular-resource"/>
<g:javascript src="services/Common.js"/>
<g:javascript src="controllers/UserTerritoryCtrl.js"/>
</body>

</html>
