<%@ page import="com.omnitech.chai.model.Customer" %>


<div class="panel panel-success">
<div class="panel-heading">Outlet Information</div>

<div class="panel-body">

<div class="col-md-6">

    <div class="${hasErrors(bean: customerInstance, field: 'subCounty', 'alert-danger')} ">
        <label for="subCounty.id" class="control-label"><g:message code="SubCounty.label"
                                                                   default="Select Subcounty In District"/></label>

        <div>
            <g:select class='form-control chzn-select' name="subCounty.id"
                      optionKey="id"
                      optionValue="description"
                      from="${subCounties}"
                      value="${customerInstance?.subCounty?.id}"/>
            <span class="help-inline alert-danger">${hasErrors(bean: customerInstance, field: 'subCounty', 'error')}</span>
        </div>
    </div>

    <div class="${hasErrors(bean: customerInstance, field: 'outletName', 'error')} ">
        <label for="outletName" class="control-label"><g:message code="customer.outletName.label"
                                                                 default="Outlet Name"/></label>

        <div>
            <g:textField class='form-control' name="outletName"
                         value="${customerInstance?.outletName}"/>
            <span class="help-inline alert-danger">${hasErrors(bean: customerInstance, field: 'outletName', 'error')}</span>
        </div>
    </div>

    <div class="${hasErrors(bean: customerInstance, field: 'outletType', 'error')} ">
        <label for="outletType" class="control-label"><g:message code="customer.outletType.label"
                                                                 default="Out Let Type"/></label>

        <div>
            <g:select class='form-control' name="outletType"
                      from="${customerInstance?.constraints?.outletType?.inList}"
                      value="${customerInstance?.outletType}"/>
            <span class="help-inline alert-danger">${hasErrors(bean: customerInstance, field: 'outletType', 'error')}</span>
        </div>
    </div>

    <div class="${hasErrors(bean: customerInstance, field: 'outletSize', 'error')} ">
        <label for="outletSize" class="control-label"><g:message code="customer.outletSize.label"
                                                                 default="Out Let Size"/></label>

        <div>
            <g:select class='form-control' name="outletSize"
                      from="${customerInstance?.constraints?.outletSize?.inList}"
                      value="${customerInstance?.outletSize}"/>
            <span class="help-inline alert-danger">${hasErrors(bean: customerInstance, field: 'outletSize', 'error')}</span>
        </div>
    </div>

    %{--    Type of License     --}%
    <div class="${hasErrors(bean: customerInstance, field: 'typeOfLicence', 'error')} ">
        <label for="typeOfLicence" class="control-label"><g:message code="customer.typeOfLicence.label"
                                                                    default="Type of Licence"/></label>

        <div>
            <g:select type="text" class='form-control'
                      from="${customerInstance?.constraints?.typeOfLicence?.inList}"
                      name="typeOfLicence" value="${customerInstance?.typeOfLicence}"/>
            <span class="help-inline alert-danger">${hasErrors(bean: customerInstance, field: 'typeOfLicence', 'error')}</span>
        </div>
    </div>

    <div class="${hasErrors(bean: customerInstance, field: 'split', 'error')} ">
        <label for="split" class="control-label"><g:message code="customer.split.label" default="Split"/></label>

        <div>
            <g:select class='form-control' name="split"
                      from="${customerInstance?.constraints?.split?.inList}"
                      value="${customerInstance?.split}"/>
            <span class="help-inline alert-danger">${hasErrors(bean: customerInstance, field: 'split', 'error')}</span>
        </div>
    </div>

    <div class="${hasErrors(bean: customerInstance, field: 'descriptionOfOutletLocation', 'error')} ">
        <label for="descriptionOfOutletLocation" class="control-label"><g:message
                code="customer.descriptionOfOutletLocation.label" default="Description Of Outlet Location"/></label>

        <div>
            <g:textField class='form-control' name="descriptionOfOutletLocation"
                         value="${customerInstance?.descriptionOfOutletLocation}"/>
            <span class="help-inline alert-danger">${hasErrors(bean: customerInstance, field: 'descriptionOfOutletLocation', 'error')}</span>
        </div>
    </div>

    <div class="${hasErrors(bean: customerInstance, field: 'lat', 'error')} ">
        <label for="lat" class="control-label"><g:message code="customer.lat.label" default="Lat"/></label>

        <div>
            <g:field class='form-control' type="decimal" step="any" name="lat" value="${customerInstance?.lat}"/>
            <span class="help-inline alert-danger">${hasErrors(bean: customerInstance, field: 'lat', 'error')}</span>
        </div>
    </div>

    <div class="${hasErrors(bean: customerInstance, field: 'lng', 'error')} ">
        <label for="lng" class="control-label"><g:message code="customer.lng.label" default="Lng"/></label>

        <div>
            <g:field class='form-control' type="number" name="lng"  step="any"  value="${customerInstance?.lng}"/>
            <span class="help-inline alert-danger">${hasErrors(bean: customerInstance, field: 'lng', 'error')}</span>
        </div>
    </div>

    <div class="${hasErrors(bean: customerInstance, field: 'tenureLength', 'error')} ">
        <label for="tenureLength" class="control-label">
            <g:message code="customer.tenureLength.label" default="Tenure Length"/>
        </label>

        <div>
            <g:field class='form-control'  type="number" name="tenureLength"  value="${customerInstance?.tenureLength}"/>
            <span class="help-inline alert-danger">${hasErrors(bean: customerInstance, field: 'tenureLength', 'error')}</span>
        </div>
    </div>


    <div class="${hasErrors(bean: customerInstance, field: 'numberOfEmployees', 'error')} ">
        <label for="numberOfEmployees" class="control-label"><g:message code="customer.numberOfEmployees.label"
                                                                        default="Number Of Employees"/></label>

        <div>
            <g:field class='form-control' type="number" name="numberOfEmployees" value="${customerInstance.numberOfEmployees}"/>
            <span class="help-inline alert-danger">${hasErrors(bean: customerInstance, field: 'numberOfEmployees', 'error')}</span>
        </div>
    </div>
</div>

<div class="col-md-6">

    <div class="${hasErrors(bean: customerInstance, field: 'turnOver', 'error')} ">
        <label for="turnOver" class="control-label"><g:message code="customer.turnOver.label"
                                                               default="Turn Over"/></label>

        <div>
            <g:select class='form-control' type="number" name="turnOver"
                from="${customerInstance.constraints.turnOver.inList}"
                     value="${customerInstance.turnOver}"/>
            <span class="help-inline alert-danger">${hasErrors(bean: customerInstance, field: 'turnOver', 'error')}</span>
        </div>
    </div>

    <div class="${hasErrors(bean: customerInstance, field: 'numberOfCustomersPerDay', 'error')} ">
        <label for="numberOfCustomersPerDay" class="control-label"><g:message
                code="customer.numberOfCustomersPerDay.label"
                default="Number Of Customers Per Day"/></label>

        <div>
            <g:field class='form-control' type="number" name="numberOfCustomersPerDay"
                     value="${customerInstance.numberOfCustomersPerDay}"/>
            <span class="help-inline alert-danger">${hasErrors(bean: customerInstance, field: 'numberOfCustomersPerDay', 'error')}</span>
        </div>
    </div>

    <div class="${hasErrors(bean: customerInstance, field: 'keyWholeSalerName', 'error')} ">
        <label for="keyWholeSalerName" class="control-label"><g:message code="customer.keyWholeSalerName.label"
                                                                        default="Key Whole Saler Name"/></label>

        <div>
            <g:textField class='form-control' name="keyWholeSalerName"
                         value="${customerInstance?.keyWholeSalerName}"/>
            <span class="help-inline alert-danger">${hasErrors(bean: customerInstance, field: 'keyWholeSalerName', 'error')}</span>
        </div>
    </div>

    <div class="${hasErrors(bean: customerInstance, field: 'keyWholeSalerContact', 'error')} ">
        <label for="keyWholeSalerContact" class="control-label"><g:message
                code="customer.keyWholeSalerContact.label"
                default="Key Whole Saler Contact"/></label>

        <div>
            <g:textField class='form-control' name="keyWholeSalerContact"
                         value="${customerInstance?.keyWholeSalerContact}"/>
            <span class="help-inline alert-danger">${hasErrors(bean: customerInstance, field: 'keyWholeSalerContact', 'error')}</span>
        </div>
    </div>

    <div class="${hasErrors(bean: customerInstance, field: 'restockFrequency', 'error')} ">
        <label for="numberOfEmployees" class="control-label">
            <g:message code="customer.numberOfEmployees.label" default="Re-Stock Frequency"/></label>

        <div>
            <g:field class='form-control' type="number" name="restockFrequency"
                     value="${customerInstance.restockFrequency}"/>
            <span class="help-inline alert-danger">${hasErrors(bean: customerInstance, field: 'restockFrequency', 'error')}</span>
        </div>
    </div>


    <div class="${hasErrors(bean: customerInstance, field: 'numberOfProducts', 'error')} ">
        <label for="numberOfProducts" class="control-label"><g:message code="customer.numberOfProducts.label"
                                                                       default="Number Of Products"/></label>

        <div>
            <g:select class='form-control' type="number" name="numberOfProducts"
                from="${customerInstance.constraints.numberOfProducts.inList}"
                     value="${customerInstance.numberOfProducts}"/>
            <span class="help-inline alert-danger">${hasErrors(bean: customerInstance, field: 'numberOfProducts', 'error')}</span>
        </div>
    </div>

    <div class="${hasErrors(bean: customerInstance, field: 'buildingStructure', 'error')} ">
        <label for="buildingStructure" class="control-label"><g:message code="customer.buildingStructure.label"
                                                                        default="Building Structure"/></label>

        <div>
            <g:select class='form-control' name="buildingStructure"
                      from="${customerInstance?.constraints?.buildingStructure?.inList}"
                      value="${customerInstance?.buildingStructure}"/>
            <span class="help-inline alert-danger">${hasErrors(bean: customerInstance, field: 'buildingStructure', 'error')}</span>
        </div>
    </div>


    <div class="${hasErrors(bean: customerInstance, field: 'equipment', 'error')} ">
        <label for="equipment" class="control-label"><g:message code="customer.equipment.label"
                                                                default="Equipment"/></label>

        <div>
            <g:textField class='form-control' name="equipment"
                         value="${customerInstance?.equipment}"/>
            <span class="help-inline alert-danger">${hasErrors(bean: customerInstance, field: 'equipment', 'error')}</span>
        </div>
    </div>


    <div class="${hasErrors(bean: customerInstance, field: 'majoritySourceOfSupply', 'error')} ">
        <label for="majoritySourceOfSupply" class="control-label"><g:message
                code="customer.majoritySourceOfSupply.label"
                default="Majority Source Of Supply"/></label>

        <div>
            <g:textField class='form-control' name="majoritySourceOfSupply"
                      value="${customerInstance?.majoritySourceOfSupply}"/>
            <span class="help-inline alert-danger">${hasErrors(bean: customerInstance, field: 'majoritySourceOfSupply', 'error')}</span>
        </div>
    </div>

    <div class="${hasErrors(bean: customerInstance, field: 'numberOfBranches', 'error')} ">
        <label for="numberOfBranches" class="control-label"><g:message code="customer.numberOfBranches.label"
                                                                       default="Number Of Branches"/></label>

        <div>
            <g:field class='form-control' type="number" name="numberOfBranches"
                     value="${customerInstance.numberOfBranches}"/>
            <span class="help-inline alert-danger">${hasErrors(bean: customerInstance, field: 'numberOfBranches', 'error')}</span>
        </div>
    </div>


    <div class="${hasErrors(bean: customerInstance, field: 'openingHours', 'error')} ">
        <label for="openingHours" class="control-label"><g:message code="customer.openingHours.label"
                                                                   default="Opening Hours"/></label>

        <div>
            <g:select class='form-control' name="openingHours"
                from="${customerInstance?.constraints?.openingHours?.inList}"
                         value="${customerInstance?.openingHours}"/>
            <span class="help-inline alert-danger">${hasErrors(bean: customerInstance, field: 'openingHours', 'error')}</span>
        </div>
    </div>
</div>
</div>
</div>


<g:render template="customerContact"/>

