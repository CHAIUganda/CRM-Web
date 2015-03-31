
<%@ page import="com.omnitech.chai.model.Customer" %>
<!DOCTYPE html>
<html>

<head>
    <meta name="layout" content="kickstart" />
    <g:set var="entityName" value="${message(code: 'customer.label', default: 'Customer')}" />
    <title><g:message code="default.index.label" args="[entityName]" /></title>
    <g:set var="layout_nosecondarymenu" value="${true}" scope="request"/>
</head>

<body>

<g:render template="customerMenubar"/>

<section id="index-customer" class="first">

    <table class="table table-bordered margin-top-medium" id="tree-table">
        <thead>
        <tr>

            <g:sortableColumn property="outletName" title="${message(code: 'customer.outletName.label', default: 'Outlet')}" />

            <g:sortableColumn property="outletType" title="${message(code: 'customer.outletType.label', default: 'Outlet Type')}" />

            <g:sortableColumn property="outletSize" title="${message(code: 'customer.outletSize.label', default: 'Outlet Size')}" />

            <g:sortableColumn property="segment" title="${message(code: 'customer.segment.label', default: (params.segment ? "Segment-($params.segment)": 'Segment'))}" />

            <g:sortableColumn property="district" title="${message(code: 'customer.district.label', default: 'District')}" />

            <g:sortableColumn property="dateCreated" title="${message(code: 'customer.dateCreated.label', default: 'Date Created')}" />

            <g:sortableColumn property="lastVisit" title="${message(code: 'customer.lastVisit.label', default: 'Last Visit')}" />

            <th>
                Action
            </th>
        </tr>
        </thead>
        <tbody>
        <g:each in="${customerInstanceList}" status="i" var="customerInstance">

            <tr data-tt-id="${customerInstance.id}" class="${(i % 2) == 0 ? 'even' : 'odd'}">

                <td><g:link action="show" id="${customerInstance.id}">
                    ${(params.offset ? (params.offset as Long) + 1 : 1) + i}.
                    ${fieldValue(bean: customerInstance, field: "outletName")}
                </g:link></td>

                <td>${fieldValue(bean: customerInstance, field: "outletType")}</td>

                <td>${fieldValue(bean: customerInstance, field: "outletSize")}</td>

                <td>${customerInstance?.segment}</td>

                <td>${customerInstance?.district}</td>

                <td><g:formatDate date="${customerInstance.dateCreated}" format="dd-MMM-yyyy" /></td>

                <td><g:formatDate date="${customerInstance.lastVisit}" format="dd-MMM-yyyy" /></td>

                <td>
                    <g:link action="edit" id="${customerInstance.id}"><i
                            class="glyphicon glyphicon-pencil"></i></g:link>
                </td>

            </tr>
        </g:each>
        </tbody>
    </table>
    <div>
        <bs:paginate total="${customerInstanceCount}"
                     id="${params.action == 'search' ? (params.term ?: params.id) : null}" params="${params}"/>
    </div>
</section>

<div class="modal fade" id="importCustomers" tabindex="-1" role="dialog"
     aria-labelledby="myModalLabel" aria-hidden="true">

    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                <h4 class="modal-title">Import Customers</h4>
            </div>

            <div class="modal-body">
                <g:uploadForm action="importCustomers">
                    <div class="row">
                        <div class="col-md-7"><input type="file"  name="myFile"/></div>
                        <div class="col-md-3">
                            <button type="submit" class="btn btn-primary">
                                <i class="glyphicon glyphicon-upload"></i> Upload
                            </button>
                        </div>
                    </div>
                </g:uploadForm>
            %{--<g:link action="downloadSample" class="btn btn-primary">Download Sample</g:link>--}%
            </div>
        </div>
    </div>
</div>

<r:require module="jqueryTreeTable"/>
</body>

</html>
