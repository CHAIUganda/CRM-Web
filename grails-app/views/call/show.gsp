
<%@ page import="com.omnitech.chai.model.DetailerTask; com.omnitech.chai.util.ChaiUtils; com.omnitech.chai.model.Task" %>
<!DOCTYPE html>
<html>

<head>
	<meta name="layout" content="kickstart" />
	<g:set var="entityName" value="${message(code: 'task.label', default: 'Task')}" />
	<title><g:message code="default.show.label" args="[entityName]" /></title>
</head>

<body>

<section id="show-task" class="first">

	<table class="table">
		<tbody>
		
			<tr class="prop">
				<td valign="top" class="name"><g:message code="task.description.label" default="Description" /></td>
				
				<td valign="top" class="value">${fieldValue(bean: taskInstance, field: "description")}</td>

            </tr>

        <tr class="prop">
            <td valign="top" class="name">Assigned User</td>

            <td valign="top" class="value">
                <g:each in="${taskInstance.territoryUser()}" var="user">
                    <g:link controller="user" action="show" id="${user.id}">
                        <i class="glyphicon glyphicon-user"></i>  ${user}
                    </g:link>
                </g:each>
            </td>

        </tr>

        <tr class="prop">
            <td valign="top" class="name">Customer</td>

            <td valign="top" class="value">
                    <g:link controller="customer" action="show" id="${taskInstance.customer?.id}">
                        <i class="glyphicon glyphicon-home"></i>  ${taskInstance.customer}
                    </g:link>
            </td>

        </tr>

        <g:if test="${taskInstance.completedBy}">
            <tr class="prop">
                <td valign="top" class="name">Completed By</td>

                <td valign="top" class="value">
                    <g:link controller="user" action="show" id="${taskInstance.completedBy.id}">
                        <i class="glyphicon glyphicon-user"></i>
                        ${fieldValue(bean: taskInstance, field: "completedBy")}
                    </g:link>
                </td>

            </tr>
        </g:if>

        <tr class="prop">
            <td valign="top" class="name"><g:message code="task.dateCreated.label" default="Date Created"/></td>

            <td valign="top" class="value"><g:formatDate date="${taskInstance?.dateCreated}"/></td>

        </tr>

        <g:if test="${taskInstance.assignedTo}">
            <tr class="prop">
                <td valign="top" class="name">Assigned To</td>

                <td valign="top" class="value">
                    <g:link controller="user" action="show" id="${taskInstance.assignedTo.id}">
                        <i class="glyphicon glyphicon-user"></i>  ${fieldValue(bean: taskInstance, field: "assignedTo")}
                    </g:link>
                </td>

            </tr>
        </g:if>

        <tr class="prop">
            <td valign="top" class="name"><g:message code="task.dueDate.label" default="Due Date" /></td>

            <td valign="top" class="value">
                <g:if test="${taskInstance.dueDate}">
                    <g:formatDate date="${taskInstance.dueDate}" format="dd-MMM-yyyy" /> <span class="${new Date().after(taskInstance.dueDate) ? 'alert-danger':''}">(${ChaiUtils.fromNow(taskInstance.dueDate)})</span>
                </g:if>
            </td>

        </tr>

        <tr class="prop">
            <td valign="top" class="name"><g:message code="task.completionDate.label" default="Completion Date" /></td>

            <td valign="top" class="value">
                <g:if test="${taskInstance.completionDate}">
                    <g:formatDate date="${taskInstance.completionDate}" format="dd-MMM-yyyy" /> (Completed ${ChaiUtils.fromNow(taskInstance.dueDate)})
                </g:if>
            </td>

        </tr>


        <tr class="prop">
            <td valign="top" class="name"><g:message code="task.status.label" default="Status"/></td>

            <td valign="top" class="value">${fieldValue(bean: taskInstance, field: "status")}</td>
				
	    </tr>


         <g:if test="${taskInstance instanceof com.omnitech.chai.model.Sale}">
             <tr class="prop">
                 <td valign="top" class="name"><g:message code="task.howManyZincInStock.label" default="How Many Zinc In Stock" /></td>

                 <td valign="top" class="value">${fieldValue(bean: taskInstance, field: "howManyZincInStock")}</td>

             </tr>

             <tr class="prop">
                 <td valign="top" class="name"><g:message code="task.howManyOrsInStock.label" default="How Many ORS In Stock" /></td>

                 <td valign="top" class="value">${fieldValue(bean: taskInstance, field: "howManyOrsInStock")}</td>

             </tr>

             <tr class="prop">
                 <td valign="top" class="name"><g:message code="task.pointOfSaleMaterial.label" default="Point Of Sale Materials" /></td>

                 <td valign="top" class="value">${fieldValue(bean: taskInstance, field: "pointOfSaleMaterial")}</td>

             </tr>

             <tr class="prop">
                 <td valign="top" class="name"><g:message code="task.recommendationNextStep.label" default="Recommendation Next Step" /></td>

                 <td valign="top" class="value">${fieldValue(bean: taskInstance, field: "recommendationNextStep")}</td>

             </tr>

             <tr class="prop">
                 <td valign="top" class="name"><g:message code="task.recommendationLevel.label" default="Recommendation Level" /></td>

                 <td valign="top" class="value">${fieldValue(bean: taskInstance, field: "recommendationLevel")}</td>

             </tr>
             <tr class="prop">
                 <td valign="top" class="name"><g:message code="task.governmentApproval.label" default="Government Approval" /></td>

                 <td valign="top" class="value">${fieldValue(bean: taskInstance, field: "governmentApproval")}</td>

             </tr>

             <tr class="prop">
                 <td valign="top" class="name"><g:message code="task.dateOfSale.label" default="Date Of Sale" /></td>

                 <td valign="top" class="value">${fieldValue(bean: taskInstance, field: "dateOfSale")}</td>

             </tr>
         </g:if>
		</tbody>
	</table>

    <g:render template="/call/lineItemList"/>
</section>

</body>

</html>
