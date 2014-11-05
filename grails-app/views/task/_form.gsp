<%@ page import="com.omnitech.chai.model.Task" %>


<div class="row">
    <div class="col-md-2">
        <strong><g:message code="task.assignedTo.label" default="Assigned User"/>:</strong>
    </div>

    <div class="col-md-8">

        <g:each in="${taskInstance.territoryUser()}" var="user">
            <g:link controller="user" action="show" id="${user.id}">
                <i class="glyphicon glyphicon-user"></i>  ${user}
            </g:link>
        </g:each>
    </div>
</div>

<div class="${hasErrors(bean: taskInstance, field: 'description', 'error')} ">
    <label for="description" class="control-label"><g:message code="task.description.label"
                                                              default="Description"/></label>

    <div>
        <g:textField class='form-control' style="width: 50%;" name="description" value="${taskInstance?.description}"/>
        <span class="help-inline">${hasErrors(bean: taskInstance, field: 'description', 'error')}</span>
    </div>
</div>

<div>
    <label for="status" class="control-label"><g:message code="task.cutomer.label" default="Target Customer"/></label>

    <div>
        <g:select from="${customers}" class="form-control chzn-select" style="width: 50%;" name="customer.id"
                  optionKey="id" value="${taskInstance?.customer?.id}"/>
    </div>
</div>

<div>
    <label for="status" class="control-label"><g:message code="task.dueDate.label" default="Due Date"/></label>

    <div>
        <g:datePicker class="form-control" relativeYears="${-5..5}" precision="day" style="width: 50%;" name="dueDate"
                      optionKey="id" value="${taskInstance?.dueDate}"/>
    </div>
</div>

