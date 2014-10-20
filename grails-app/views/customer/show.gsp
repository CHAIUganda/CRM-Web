
<%@ page import="com.omnitech.chai.model.Customer" %>
<!DOCTYPE html>
<html>

<head>
	<meta name="layout" content="kickstart" />
	<g:set var="entityName" value="${message(code: 'customer.label', default: 'Customer')}" />
	<title><g:message code="default.show.label" args="[entityName]" /></title>
</head>

<body>

<section id="show-customer" class="first">

	<table class="table">
		<tbody>
		
			<tr class="prop">
				<td valign="top" class="name"><g:message code="customer.buildingStructure.label" default="Building Structure" /></td>
				
				<td valign="top" class="value">${fieldValue(bean: customerInstance, field: "buildingStructure")}</td>
				
			</tr>
		
			<tr class="prop">
				<td valign="top" class="name"><g:message code="customer.dateCreated.label" default="Date Created" /></td>
				
				<td valign="top" class="value"><g:formatDate date="${customerInstance?.dateCreated}" /></td>
				
			</tr>
		
			<tr class="prop">
				<td valign="top" class="name"><g:message code="customer.descriptionOfOutletLocation.label" default="Description Of Outlet Location" /></td>
				
				<td valign="top" class="value">${fieldValue(bean: customerInstance, field: "descriptionOfOutletLocation")}</td>
				
			</tr>
		
			<tr class="prop">
				<td valign="top" class="name"><g:message code="customer.equipment.label" default="Equipment" /></td>
				
				<td valign="top" class="value">${fieldValue(bean: customerInstance, field: "equipment")}</td>
				
			</tr>
		
			<tr class="prop">
				<td valign="top" class="name"><g:message code="customer.keyWholeSalerContact.label" default="Key Whole Saler Contact" /></td>
				
				<td valign="top" class="value">${fieldValue(bean: customerInstance, field: "keyWholeSalerContact")}</td>
				
			</tr>
		
			<tr class="prop">
				<td valign="top" class="name"><g:message code="customer.keyWholeSalerName.label" default="Key Whole Saler Name" /></td>
				
				<td valign="top" class="value">${fieldValue(bean: customerInstance, field: "keyWholeSalerName")}</td>
				
			</tr>
		
			<tr class="prop">
				<td valign="top" class="name"><g:message code="customer.lastUpdated.label" default="Last Updated" /></td>
				
				<td valign="top" class="value"><g:formatDate date="${customerInstance?.lastUpdated}" /></td>
				
			</tr>
		
			<tr class="prop">
				<td valign="top" class="name"><g:message code="customer.lat.label" default="Lat" /></td>
				
				<td valign="top" class="value">${fieldValue(bean: customerInstance, field: "lat")}</td>
				
			</tr>
		
			<tr class="prop">
				<td valign="top" class="name"><g:message code="customer.latLng.label" default="Lat Lng" /></td>
				
				<td valign="top" class="value">${fieldValue(bean: customerInstance, field: "wkt")}</td>
				
			</tr>
		
			<tr class="prop">
				<td valign="top" class="name"><g:message code="customer.lng.label" default="Lng" /></td>
				
				<td valign="top" class="value">${fieldValue(bean: customerInstance, field: "lng")}</td>
				
			</tr>
		
			<tr class="prop">
				<td valign="top" class="name"><g:message code="customer.majoritySourceOfSupply.label" default="Majority Source Of Supply" /></td>
				
				<td valign="top" class="value">${fieldValue(bean: customerInstance, field: "majoritySourceOfSupply")}</td>
				
			</tr>
		
			<tr class="prop">
				<td valign="top" class="name"><g:message code="customer.numberOfBranches.label" default="Number Of Branches" /></td>
				
				<td valign="top" class="value">${fieldValue(bean: customerInstance, field: "numberOfBranches")}</td>
				
			</tr>
		
			<tr class="prop">
				<td valign="top" class="name"><g:message code="customer.numberOfCustomersPerDay.label" default="Number Of Customers Per Day" /></td>
				
				<td valign="top" class="value">${fieldValue(bean: customerInstance, field: "numberOfCustomersPerDay")}</td>
				
			</tr>
		
			<tr class="prop">
				<td valign="top" class="name"><g:message code="customer.numberOfEmployees.label" default="Number Of Employees" /></td>
				
				<td valign="top" class="value">${fieldValue(bean: customerInstance, field: "numberOfEmployees")}</td>
				
			</tr>
		
			<tr class="prop">
				<td valign="top" class="name"><g:message code="customer.numberOfProducts.label" default="Number Of Products" /></td>
				
				<td valign="top" class="value">${fieldValue(bean: customerInstance, field: "numberOfProducts")}</td>
				
			</tr>
		
			<tr class="prop">
				<td valign="top" class="name"><g:message code="customer.openingHours.label" default="Opening Hours" /></td>
				
				<td valign="top" class="value">${fieldValue(bean: customerInstance, field: "openingHours")}</td>
				
			</tr>
		
			<tr class="prop">
				<td valign="top" class="name"><g:message code="customer.outletSize.label" default="Out Let Size" /></td>
				
				<td valign="top" class="value">${fieldValue(bean: customerInstance, field: "outletSize")}</td>
				
			</tr>
		
			<tr class="prop">
				<td valign="top" class="name"><g:message code="customer.outletType.label" default="Out Let Type" /></td>
				
				<td valign="top" class="value">${fieldValue(bean: customerInstance, field: "outletType")}</td>
				
			</tr>
		
			<tr class="prop">
				<td valign="top" class="name"><g:message code="customer.outletName.label" default="Outlet Name" /></td>
				
				<td valign="top" class="value">${fieldValue(bean: customerInstance, field: "outletName")}</td>
				
			</tr>
		
			<tr class="prop">
				<td valign="top" class="name"><g:message code="customer.split.label" default="Split" /></td>
				
				<td valign="top" class="value">${fieldValue(bean: customerInstance, field: "split")}</td>
				
			</tr>
		
			<tr class="prop">
				<td valign="top" class="name"><g:message code="customer.tenureEndDate.label" default="Tenure Length" /></td>
				
				<td valign="top" class="value"><g:formatDate date="${customerInstance?.tenureLength}" /></td>
				
			</tr>
		
			<tr class="prop">
				<td valign="top" class="name"><g:message code="customer.turnOver.label" default="Turn Over" /></td>
				
				<td valign="top" class="value">${fieldValue(bean: customerInstance, field: "turnOver")}</td>
				
			</tr>
		
			<tr class="prop">
				<td valign="top" class="name"><g:message code="customer.uuid.label" default="Uuid" /></td>
				
				<td valign="top" class="value">${fieldValue(bean: customerInstance, field: "uuid")}</td>
				
			</tr>
		
		</tbody>
	</table>
</section>

<section>
<g:render template="contactDetail"/>
</section>

</body>

</html>
