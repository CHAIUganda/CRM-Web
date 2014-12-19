<%@ page import="com.omnitech.chai.model.WholeSaler" %>
<!DOCTYPE html>
<html>

<head>
    <meta name="layout" content="kickstart"/>
    <g:set var="entityName" value="${message(code: 'wholeSaler.label', default: 'WholeSaler')}"/>
    <title><g:message code="default.index.label" args="[entityName]"/></title>
</head>

<body>

<section id="index-wholeSaler" class="first" ng-controller="WholeSaleCtrl">

    <table class="table table-bordered margin-top-medium">
        <thead>
        <tr>

            <g:sortableColumn property="name" title="${message(code: 'wholeSaler.name.label', default: 'Name')}"/>
            <g:sortableColumn property="contact"
                              title="${message(code: 'wholeSaler.contact.label', default: 'Contact')}"/>

            <td>
                Action
            </td>
        </tr>
        </thead>
        <tbody>
        <g:each in="${wholeSalerInstanceList}" status="i" var="wholeSalerInstance">
            <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">

                <td><g:link action="show"
                            id="${wholeSalerInstance.id}">${fieldValue(bean: wholeSalerInstance, field: "name")}</g:link></td>

                <td>${fieldValue(bean: wholeSalerInstance, field: "contact")}</td>

                <td>
                    <g:link action="edit" id="${wholeSalerInstance.id}"><i
                            class="glyphicon glyphicon-pencil"></i></g:link>
                    <a href="#" id="${wholeSalerInstance.id}" title="Map To SubCounty"
                       data-target="#map-territory"
                       data-toggle="modal" ng-click="onRemap(${wholeSalerInstance.id})">

                        <i class="glyphicon glyphicon-transfer"></i>
                    </a>
                </td>
            </tr>
        </g:each>
        </tbody>
    </table>

    <div>
        <bs:paginate total="${wholeSalerInstanceCount}"
                     id="${params.action == 'search' ? (params.term ?: params.id) : null}"/>
    </div>
</section>

<g:render template="/territory/mapTerritory"/>

<r:require modules="angular,angular-resource"/>
<g:javascript src="services/Common.js"/>
<g:javascript src="controllers/WholeSaleCtrl.js"/>

</body>

</html>
