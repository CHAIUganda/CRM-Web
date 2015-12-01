<%@ page import="com.omnitech.chai.model.StockInfo; com.omnitech.chai.model.DetailerTask; com.omnitech.chai.util.ChaiUtils; com.omnitech.chai.model.Task" %>
<!DOCTYPE html>
<html>

<head>
    <meta name="layout" content="kickstart"/>
    <g:set var="entityName" value="${message(code: 'task.label', default: 'Sale')}"/>
    %{--<g:set var="noedit_menu" value="${true}" scope="request"/>--}%
    <g:set var="nocreate_menu" value="${true}" scope="request"/>
    <title><g:message code="default.show.label" args="[entityName]"/></title>
</head>

<body>

<section id="show-task" class="first">

    <table class="table">
        <tbody>

        <tr class="prop">
            <td valign="top" class="name"><g:message code="task.description.label" default="Description"/></td>

            <td valign="top" class="value">${fieldValue(bean: taskInstance, field: "description")}</td>

        </tr>

        <tr class="prop">
            <td valign="top" class="name">Assigned User</td>

            <td valign="top" class="value">
                <g:each in="${taskInstance?.territoryUser()}" var="user">
                    <g:link controller="user" action="show" id="${user?.id}">
                        <i class="glyphicon glyphicon-user"></i>  ${user}
                    </g:link>
                </g:each>
            </td>

        </tr>

        <g:if test="${taskInstance?.hasProperty('takenBy') && taskInstance?.takenBy}">
            <tr class="prop">
                <td valign="top" class="name">Order Taken By</td>

                <td valign="top" class="value">
                    <g:link controller="user" action="show" id="${taskInstance.takenBy.id}">
                        <i class="glyphicon glyphicon-user"></i>  ${taskInstance.takenBy}
                    </g:link>
                </td>

            </tr>
        </g:if>

        <g:if test="${taskInstance?.cancelledBy}" >
            <tr class="prop">
                <td valign="top" class="name">Cancelled By</td>

                <td valign="top" class="value">
                    <g:link controller="user" action="show" id="${taskInstance.cancelledBy.id}">
                        <i class="glyphicon glyphicon-user"></i>  ${taskInstance.cancelledBy}
                    </g:link>
                </td>

            </tr>
        </g:if>

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
            <td valign="top" class="name"><g:message code="task.dueDate.label" default="Due Date"/></td>

            <td valign="top" class="value">
                <g:if test="${taskInstance.dueDate}">
                    <g:formatDate date="${taskInstance.dueDate}" format="dd-MMM-yyyy"/> <span
                        class="${new Date().after(taskInstance.dueDate) ? 'alert-danger' : ''}">(${ChaiUtils.fromNow(taskInstance.dueDate)})</span>
                </g:if>
            </td>

        </tr>

        <tr class="prop">
            <td valign="top" class="name"><g:message code="task.completionDate.label" default="Completion Date"/></td>

            <td valign="top" class="value">
                <g:if test="${taskInstance.completionDate}">
                    <g:formatDate date="${taskInstance.completionDate}"
                                  format="dd-MMM-yyyy HH:mm"/> (Completed ${ChaiUtils.fromNow(taskInstance.completionDate)})
                </g:if>
            </td>

        </tr>


        <tr class="prop">
            <td valign="top" class="name"><g:message code="task.status.label" default="Status"/></td>

            <td valign="top" class="value">${fieldValue(bean: taskInstance, field: "status")}</td>

        </tr>


        <g:if test="${taskInstance instanceof com.omnitech.chai.model.Sale}">
            <tr class="prop">
                <td valign="top" class="name"><g:message code="task.howManyZincInStock.label"
                                                         default="How Many Zinc In Stock"/></td>

                <td valign="top" class="value">${fieldValue(bean: taskInstance, field: "howManyZincInStock")}</td>

            </tr>

            <tr class="prop">
                <td valign="top" class="name"><g:message code="task.howManyOrsInStock.label"
                                                         default="How Many ORS In Stock"/></td>

                <td valign="top" class="value">${fieldValue(bean: taskInstance, field: "howManyOrsInStock")}</td>

            </tr>

            <tr class="prop">
                <td valign="top" class="name"><g:message code="task.pointOfSaleMaterial.label"
                                                         default="Point Of Sale Materials"/></td>

                <td valign="top" class="value">${fieldValue(bean: taskInstance, field: "pointOfSaleMaterial")}</td>

            </tr>

            <tr class="prop">
                <td valign="top" class="name"><g:message code="task.recommendationNextStep.label"
                                                         default="Recommendation Next Step"/></td>

                <td valign="top" class="value">${fieldValue(bean: taskInstance, field: "recommendationNextStep")}</td>

            </tr>

            <tr class="prop">
                <td valign="top" class="name"><g:message code="task.recommendationLevel.label"
                                                         default="Recommendation Level"/></td>

                <td valign="top" class="value">${fieldValue(bean: taskInstance, field: "recommendationLevel")}</td>

            </tr>
            <tr class="prop">
                <td valign="top" class="name"><g:message code="task.governmentApproval.label"
                                                         default="Government Approval"/></td>

                <td valign="top" class="value">${fieldValue(bean: taskInstance, field: "governmentApproval")}</td>

            </tr>

            <tr class="prop">
                <td valign="top" class="name"><g:message code="task.stocksACTs.label"
                                                         default="Do you stock ACTs?"/></td>

                <td valign="top" class="value">${fieldValue(bean: taskInstance, field: "stocksACTs")}</td>

            </tr>
            <tr class="prop">
                <td valign="top" class="name"><g:message code="task.minACTPrice.label"
                                                         default="What is the lowest price you sell 1 ACT tablet?"/></td>

                <td valign="top" class="value">${fieldValue(bean: taskInstance, field: "minACTPrice")}</td>

            </tr>
            <tr class="prop">
                <td valign="top" class="name"><g:message code="task.stocksZinc.label"
                                                         default="Do you stock Zinc"/></td>

                <td valign="top" class="value">${fieldValue(bean: taskInstance, field: "stocksZinc")}</td>

            </tr>

            <tr class="prop">
                <td valign="top" class="name"><g:message code="task.minZincPrice.label"
                                                         default="What is the lowest price you sell 1 tablet of zinc?"/></td>

                <td valign="top" class="value">${fieldValue(bean: taskInstance, field: "minZincPrice")}</td>

            </tr>

            <tr class="prop">
                <td valign="top" class="name"><g:message code="task.stocksORS.label"
                                                         default="Do you stock ORS"/></td>

                <td valign="top" class="value">${fieldValue(bean: taskInstance, field: "stocksORS")}</td>

            </tr>

            <tr class="prop">
                <td valign="top" class="name"><g:message code="task.minORSPrice.label"
                                                         default="What is the lowest price you sell 1 sachet of ORS?"/></td>

                <td valign="top" class="value">${fieldValue(bean: taskInstance, field: "minORSPrice")}</td>

            </tr>

            <tr class="prop">
                <td valign="top" class="name"><g:message code="task.stocksRDT.label"
                                                         default="Do you stock Malaria Rapid Diagnostic Tests (mRDTs)?"/></td>

                <td valign="top" class="value">${fieldValue(bean: taskInstance, field: "stocksRDT")}</td>

            </tr>
            <tr class="prop">
                <td valign="top" class="name"><g:message code="task.minRDTPrice.label"
                                                         default="What is the lowest price you sell 1 mRDT?"/></td>

                <td valign="top" class="value">${fieldValue(bean: taskInstance, field: "minRDTPrice")}</td>

            </tr>
            <tr class="prop">
                <td valign="top" class="name"><g:message code="task.stocksAmox.label"
                                                         default="Do you stock Amoxicillin 250mg (dispersible tablets)?"/></td>

                <td valign="top" class="value">${fieldValue(bean: taskInstance, field: "stocksAmox")}</td>

            </tr>
            <tr class="prop">
                <td valign="top" class="name"><g:message code="task.minAmoxPrice.label"
                                                         default="What is the lowest price you sell 1 tablet of Amoxicillin 250mg?"/></td>

                <td valign="top" class="value">${fieldValue(bean: taskInstance, field: "minAmoxPrice")}</td>

            </tr>
            <tr class="prop">
                <td valign="top" class="name"><g:message code="task.dateOfSale.label" default="Date Of Sale"/></td>

                <td valign="top" class="value">${fieldValue(bean: taskInstance, field: "dateOfSale")}</td>

            </tr>
        </g:if>
        </tbody>
    </table>

    %{-- Line Items--}%
    <g:render template="/call/lineItemList"/>

    <g:if test="${taskInstance instanceof StockInfo}">
        <g:render template="/call/stockInfoList"/>
    </g:if>
</section>

</body>

</html>
