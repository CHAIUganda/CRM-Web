<%@ page import="com.omnitech.chai.model.Task" %>


%{-- Customer Details Sections --}%
%{--<div class="row">--}%
    %{--<div class="col-md-2">--}%
        %{--<strong><g:message code="task.assignedTo.label" default="Assigned User"/>:</strong>--}%
    %{--</div>--}%

    %{--<div class="col-md-8">--}%
        %{--<g:each in="${taskInstance?.territoryUser()}" var="user">--}%
            %{--<g:link controller="user" action="show" id="${user.id}">--}%
                %{--<i class="glyphicon glyphicon-user"></i>  ${user}--}%
            %{--</g:link>--}%
        %{--</g:each>--}%
    %{--</div>--}%
%{--</div>--}%

%{--<div class="${hasErrors(bean: taskInstance, field: 'description', 'error')} ">--}%
    %{--<label for="description" class="control-label"><g:message code="task.description.label"--}%
                                                              %{--default="Description"/></label>--}%

    %{--<div>--}%
        %{--<g:textField class='form-control' style="width: 50%;" name="description" value="${taskInstance?.description}"/>--}%
        %{--<span class="help-inline">${hasErrors(bean: taskInstance, field: 'description', 'error')}</span>--}%
    %{--</div>--}%
%{--</div>--}%

<div>
    <label for="status" class="control-label"><g:message code="task.cutomer.label" default="Select Customer"/></label>

    <div>
        <input type="text" placeholder="Select Customer..."
            value="${taskInstance?.customer?.outletName}"
               class="form-control"
               ng-model="cutomerObj"
               style="width: 50%;"
               typeahead="customer.outletName for customer in searchCustomerByName($viewValue) | filter:$viewValue"
               typeahead-editable="false" typeahead-on-select="onSelectCustomer($item)">

        <span>Target Customer: {{customer.outletName}}</span>
        <input type="hidden" name="customerId" value="{{customer.id}}">
    </div>
</div>

<div>
    <label for="status" class="control-label"><g:message code="task.dueDate.label" default="Due Date"/></label>

    <div>
        <g:datePicker class="form-control" relativeYears="${-5..5}" precision="day" style="width: 50%;" name="dueDate"
                      optionKey="id" value="${taskInstance?.dueDate}"/>
    </div>
</div>
