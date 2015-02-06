<%@ page import="com.omnitech.chai.util.ChaiUtils; com.omnitech.chai.model.Product" %>


            <div class="${hasErrors(bean: productInstance, field: 'group', 'error')} ">
                <label for="name" class="control-label"><g:message code="product.group.label" default="Product Group" /></label>
                <div>
                    <g:select class='form-control' from="${productGroups}" optionKey="id" required="" style="width: 50%;" name="group.id" value="${productInstance?.group?.id}" />
                    <span class="help-inline">${hasErrors(bean: productInstance, field: 'name', 'error')}</span>
                </div>
            </div>

            <div class="${hasErrors(bean: productInstance, field: 'name', 'error')} ">
                <label for="name" class="control-label"><g:message code="product.name.label" default="Name" /></label>
                <div>
                    <g:textField class='form-control' required="" style="width: 50%;" name="name" value="${productInstance?.name}" />
                    <span class="help-inline">${hasErrors(bean: productInstance, field: 'name', 'error')}</span>
                </div>
            </div>

			<div class="${hasErrors(bean: productInstance, field: 'unitOfMeasure', 'error')} ">
				<label for="unitOfMeasure" class="control-label"><g:message code="product.unitOfMeasure.label" default="Unit of Measure" /></label>
				<div>
					<g:textField class='form-control' required="" style="width: 50%;" name="unitOfMeasure" value="${productInstance?.unitOfMeasure}" />
					<span class="help-inline">${hasErrors(bean: productInstance, field: 'unitOfMeasure', 'error')}</span>
				</div>
			</div>

        <div class="${hasErrors(bean: productInstance, field: 'formulation', 'error')} ">
            <label for="unitOfMeasure" class="control-label"><g:message code="product.unitOfMeasure.label" default="Formulation" /></label>
            <div>
                <g:textField class='form-control' required="" style="width: 50%;" name="formulation" value="${productInstance?.formulation}" />
                <span class="help-inline">${hasErrors(bean: productInstance, field: 'formulation', 'error')}</span>
            </div>
        </div>



			<div class="${hasErrors(bean: productInstance, field: 'unitPrice', 'error')} ">
				<label for="unitPrice" class="control-label"><g:message code="product.unitPrice.label" default="Unit Price" /></label>
				<div>
					<g:field class='form-control' required="" style="width: 50%;" type="number" name="unitPrice" value="${productInstance.unitPrice}" />
					<span class="help-inline">${hasErrors(bean: productInstance, field: 'unitPrice', 'error')}</span>
				</div>
			</div>

%{--SELECT TERRITORIES--}%
<div class="row">
    <div class="row">
        <legend class="text-center">In Which Territories Should This Product Be Sold?</legend>
    </div>

    <div class="row">

        <g:each in="${territories}" var="territory" status="i">
            <div class="col-md-4  ${productInstance?.isSoldInTerritory(territory)? 'alert-info' : ''}">
                <g:checkBox name="territoriz" value="${territory.id}"
                            checked="${productInstance?.isSoldInTerritory(territory)}"/>
                <label for="territoriz">${ChaiUtils.truncateString(territory.toString(),35)}</label>
            </div>

        </g:each>


    </div>
</div>

