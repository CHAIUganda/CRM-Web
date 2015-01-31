<%@ page import="com.omnitech.chai.model.Customer" %>
<!DOCTYPE html>
<html>

<head>
    <meta name="layout" content="kickstart"/>
    <g:set var="entityName" value="${message(code: 'customer.label', default: 'Customer')}"/>
    <title><g:message code="default.show.label" args="[entityName]"/></title>
</head>

<body>

<section id="show-customer" class="first">

    <table class="table">
        <tbody>
        %{--Other Info--}%
        <c:renderProperty label="Is Active"
                          value="${customerInstance?.isActive == null ? true : customerInstance?.isActive}"/>

        <c:renderProperty label="1. Outlet Name" value="${customerInstance?.outletName}"/>
        <c:renderProperty label="2. Outlet Type" value="${customerInstance?.outletType}"/>

        <c:renderProperty label="3. Outlet Size" value="${customerInstance?.outletSize}"/>
        <c:renderProperty label="4. Type of Licence" value="${customerInstance?.typeOfLicence}"/>
        <c:renderProperty label="5. Rural Or Urban" value="${customerInstance?.split}"/>

        %{-- Region Info--}%
        <c:renderProperty label="6. District" value="${customerInstance?.subCounty?.district}"/>
        <c:renderProperty label="7. SubCounty" value="${customerInstance?.subCounty}"/>
        <c:renderProperty label="8. Trading Center" value="${customerInstance?.tradingCenter}"/>
        <c:renderProperty label="Parish" value="${customerInstance?.parish}"/>
        <c:renderProperty label="Village" value="${customerInstance?.village}"/>

        <c:renderProperty label="9. Description Of Outlet Location"
                          value="${customerInstance?.descriptionOfOutletLocation}"/>
        <c:renderProperty label="10. GPS" value="${customerInstance?.wkt}"/>

        <c:renderProperty label="12. Date Outlet Opened" value="${customerInstance?.dateOutletOpened}"/>
        <c:renderProperty label="13. Number Of Employees" value="${customerInstance?.numberOfEmployees}"/>
        <c:renderProperty label="15. Is Outlet Linked to any Sister Branches"
                          value="${customerInstance?.hasSisterBranch}"/>
        %{--todo not on mobile model--}%
        %{--<c:renderProperty label="16. How Many Diarrhea Patients Under 5 Years" value="${customerInstance?.5}"/>--}%
        <c:renderProperty label="Children Patients Under 5 Years" value="${customerInstance?.childrenUnder5yrsPerDay}"/>
        <c:renderProperty label="Number Of Customers Per Day" value="${customerInstance?.numberOfCustomersPerDay}"/>
        <c:renderProperty label="17. Where Does Outlet Source Majority Source Of Supply"
                          value="${customerInstance?.majoritySourceOfSupply}"/>
        <c:renderProperty label="18. Key Wholesaler Name" value="${customerInstance?.keyWholeSalerName}"/>
        <c:renderProperty label="19. Key Wholesaler Contact" value="${customerInstance?.keyWholeSalerContact}"/>
        <c:renderProperty label="20. Restock Frequency" value="${customerInstance?.restockFrequency}"/>
        <c:renderProperty label="21. Building Structure" value="${customerInstance?.buildingStructure}"/>

        %{--Meta Info--}%
        <c:renderProperty label="Date Created" value="${customerInstance?.dateCreated}"/>
        <c:renderProperty label="Date Updated" value="${customerInstance?.lastUpdated}"/>
        <c:renderProperty label="System Id" value="${customerInstance?.uuid}"/>

        %{--<tr class="prop">--}%
            %{--<td valign="top" class="name"><g:message code="customer.numberOfProducts.label"--}%
            %{--<td valign="top" class="value">${fieldValue(bean: customerInstance, field: "numberOfProducts")}</td>--}%
        %{--</tr>--}%

        %{--<tr class="prop">--}%
            %{--<td valign="top" class="name"><g:message code="customer.visibleEquipment.label"--}%
                                                     %{--default="Visible Equipment"/></td>--}%
            %{--<td valign="top" class="value">${fieldValue(bean: customerInstance, field: "visibleEquipment")}</td>--}%
        %{--</tr>--}%

        %{--<tr class="prop">--}%
            %{--<td valign="top" class="name"><g:message code="customer.openingHours.label" default="Opening Hours"/></td>--}%
            %{--<td valign="top" class="value">${fieldValue(bean: customerInstance, field: "openingHours")}</td>--}%
        %{--</tr>--}%

        %{--<tr class="prop">--}%
            %{--<td valign="top" class="name"><g:message code="customer.hasSisterBranch.label"--}%
                                                     %{--default="Has Sister Branches"/></td>--}%
            %{--<td valign="top" class="value">${fieldValue(bean: customerInstance, field: "hasSisterBranch")}</td>--}%
        %{--</tr>--}%

        %{--<tr class="prop">--}%
            %{--<td valign="top" class="name"><g:message code="customer.dateOutletOpened.label"--}%
                                                     %{--default="Date Outlet Opened"/></td>--}%
            %{--<td valign="top" class="value">${fieldValue(bean: customerInstance, field: "dateOutletOpened")}</td>--}%
        %{--</tr>--}%

        %{--<tr class="prop">--}%
            %{--<td valign="top" class="name"><g:message code="customer.turnOver.label" default="Turn Over"/></td>--}%
            %{--<td valign="top" class="value">${fieldValue(bean: customerInstance, field: "turnOver")}</td>--}%
        %{--</tr>--}%


        </tbody>
    </table>
</section>

<section>
    <g:render template="contactDetail"/>
</section>

</body>

</html>
