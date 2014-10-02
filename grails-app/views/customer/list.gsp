
<%@ page import="com.omnitech.chai.model.Customer" %>
<!DOCTYPE html>
<html>

<head>
    <meta name="layout" content="kickstart"/>
    <g:set var="entityName" value="${message(code: 'customer.label', default: 'Customer')}"/>
    <title><g:message code="default.list.label" args="[entityName]"/></title>
</head>

<body>

<section id="list-customer" class="first">

    <table class="table table-bordered margin-top-medium">
        <thead>
        <tr>
            
            <g:sortableColumn property="buildingStructure"
                              title="${message(code: 'customer.buildingStructure.label', default: 'Building Structure')}"/>
            
            <g:sortableColumn property="dateCreated"
                              title="${message(code: 'customer.dateCreated.label', default: 'Date Created')}"/>
            
            <g:sortableColumn property="descriptionOfOutletLocation"
                              title="${message(code: 'customer.descriptionOfOutletLocation.label', default: 'Description Of Outlet Location')}"/>
            
            <g:sortableColumn property="equipment"
                              title="${message(code: 'customer.equipment.label', default: 'Equipment')}"/>
            
            <g:sortableColumn property="keyWholeSalerContact"
                              title="${message(code: 'customer.keyWholeSalerContact.label', default: 'Key Whole Saler Contact')}"/>
            
            <g:sortableColumn property="keyWholeSalerName"
                              title="${message(code: 'customer.keyWholeSalerName.label', default: 'Key Whole Saler Name')}"/>
            
        </tr>
        </thead>
        <tbody>
        <g:each in="${customerInstanceList}" status="i" var="customerInstance">
            <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                
                <td><g:link action="show"
                            id="${customerInstance.id}">${fieldValue(bean: customerInstance, field: "buildingStructure")}</g:link></td>
                
                <td><g:formatDate date="${customerInstance.dateCreated}"/></td>
                
                <td>${fieldValue(bean: customerInstance, field: "descriptionOfOutletLocation")}</td>
                
                <td>${fieldValue(bean: customerInstance, field: "equipment")}</td>
                
                <td>${fieldValue(bean: customerInstance, field: "keyWholeSalerContact")}</td>
                
                <td>${fieldValue(bean: customerInstance, field: "keyWholeSalerName")}</td>
                
            </tr>
        </g:each>
        </tbody>
    </table>

    <div>
        <bs:paginate total="${customerInstanceCount}"/>
    </div>
</section>

</body>

</html>
