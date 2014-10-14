
<%@ page import="com.omnitech.chai.model.Territory" %>
<!DOCTYPE html>
<html>

<head>
    <meta name="layout" content="kickstart" />
    <g:set var="entityName" value="${message(code: 'territory.label', default: 'Territory')}" />
    <title><g:message code="default.index.label" args="[entityName]" /></title>
</head>

<body>

<section id="index-territory" class="first" ng-controller="TerritoryMapCtrl">

    <table class="table table-bordered margin-top-medium">
        <thead>
        <tr>
            <g:sortableColumn property="name" title="${message(code: 'territory.name.label', default: 'Name')}" />

            <g:sortableColumn property="dateCreated" title="${message(code: 'territory.dateCreated.label', default: 'Date Created')}" />

            <g:sortableColumn property="lastUpdated" title="${message(code: 'territory.lastUpdated.label', default: 'Last Updated')}" />

            <td>
                Action
            </td>
        </tr>
        </thead>
        <tbody>
        <g:each in="${territoryInstanceList}" status="i" var="territoryInstance">
            <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">

                <td><g:link action="show" id="${territoryInstance.id}">${fieldValue(bean: territoryInstance, field: "name")}</g:link></td>

                <td><g:formatDate date="${territoryInstance.lastUpdated}" format="dd-MMM-yyyy" /></td>

                <td>${fieldValue(bean: territoryInstance, field: "dateCreated")}</td>

                <td>
                    <g:link action="edit" id="${territoryInstance.id}"><i
                            class="glyphicon glyphicon-pencil"></i></g:link>

                    <a href="#" id="${territoryInstance.id}" title="Map To SubCounty"
                       data-target="#map-territory"
                       data-toggle="modal" ng-click="onRemap(${territoryInstance.id})">

                        <i class="glyphicon glyphicon-transfer"></i>
                    </a>
                </td>
            </tr>
        </g:each>
        </tbody>
    </table>
    <div>
        <bs:paginate total="${territoryInstanceCount}"
                     id="${params.action == 'search' ? (params.term ?: params.id) : null}"/>
    </div>
    <g:render template="mapTerritory"/>

    <r:require modules="angular,angular-resource"/>
    <g:javascript src="services/Common.js"/>
    <g:javascript src="controllers/TerritoryMapCtrl.js"/>
</section>

</body>

</html>
