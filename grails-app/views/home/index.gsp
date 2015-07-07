<%@ page import="com.omnitech.chai.model.Role" %>
<html>

<head>
    <title><g:message code="default.welcome.title" args="[meta(name: 'app.name')]"/></title>
    <meta name="layout" content="kickstart"/>
    <r:require module="dataTable"/>
</head>

<body>

<section id="intro" class="first">
    <g:form action="index" class="form-inline home-det-search" method="get">
        <div class="form-group"><label class="">Start Date</label></div>

        <div class="form-group"><bs:datePicker name="startDate" class="form-control" value="${startDate}"/></div>

        <div class="form-group"><label class="">End Date</label></div>

        <div class="form-group"><bs:datePicker name="endDate" class="form-control" value="${endDate}"/></div>

        <div class="form-group"><g:submitButton name="submit" value="Submit" class="btn btn-mini"/></div>
    </g:form>
    <sec:ifAnyGranted
            roles="${[Role.ADMIN_ROLE_NAME, Role.DETAILER_ROLE_NAME, Role.SUPER_ADMIN_ROLE_NAME, Role.DETAILING_SUPERVISOR_ROLE_NAME].join(',')}">
            <div class="panel-heading">Detailing Call Plans</div>
                %{-- For Detailers --}%

                <div class="row">
                    <div class="col-md-12">
                        <table class="pageableTable table table-bordered margin-top-medium" cellspacing="0" width="100%">
                            <thead>
                            <tr>
                                <th>Rep</th>
                                <th>Territory</th>
                                <th>No. Tasks</th>
                                <th>% Complete</th>
                                <th>Orders</th>
                                <th>New Customers</th>
                            </tr>
                            </thead>
                            <tbody>
                            <g:each in="${detailingInfo}" status="i" var="item">
                                <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                                    <td>${item.username}</td>
                                    <td>${item.territory}</td>
                                    <td>${item.numAll}</td>
                                    <td>
                                        <div class="progress">
                                            <div class="progress-bar progress-bar-info"
                                                 role="progressbar"
                                                 aria-valuenow="${item.covered}" aria-valuemin="0" aria-valuemax="100"
                                                 style="width: ${item.covered}%; color: #000000;">
                                                ${item.covered}%
                                            </div>
                                        </div>
                                    </td>
                                    <td>${item.orders}</td>
                                    <td>${item.numCustomers}</td>
                                </tr>
                            </g:each>
                            </tbody>
                        </table>
                    </div>
                </div>
    </sec:ifAnyGranted>

    <sec:ifAnyGranted
            roles="${[Role.SALES_SUPERVISOR_ROLE_NAME, Role.SALES_ROLE_NAME, Role.SUPER_ADMIN_ROLE_NAME, Role.ADMIN_ROLE_NAME].join(',')}">
            <div class="panel-heading">Sales Call Plans</div>
                %{-- For Detailers --}%

                <div class="row">
                    <div class="col-md-12">
                        <table class="table table-bordered margin-top-medium">
                            <thead>
                            <tr>
                                <th>Rep</th>
                                <th>Territory</th>
                                <th>No. Tasks</th>
                                <th>% Complete</th>
                                <th>% Productivity</th>
                                <th>Orders</th>
                                <th>New Customers</th>
                            </tr>
                            </thead>
                            <tbody>
                            <g:each in="${salesInfo}" var="item">
                                <tr>
                                    <td>${item.username}</td>
                                    <td>${item.territory}</td>
                                    <td>${item.numAll}</td>
                                    <td>
                                        <div class="progress">
                                            <div class="progress-bar progress-bar-info"
                                                 role="progressbar"
                                                 aria-valuenow="${item.covered}" aria-valuemin="0" aria-valuemax="100"
                                                 style="width: ${item.covered}%; color: #000000;">
                                                ${item.covered}%
                                            </div>
                                        </div>
                                    </td>
                                    <td>
                                        <div class="progress">
                                            <div class="progress-bar progress-bar-info"
                                                 role="progressbar"
                                                 aria-valuenow="${item.productivity}" aria-valuemin="0"
                                                 aria-valuemax="100"
                                                 style="width: ${item.productivity}%; color: #000000;">
                                                ${item.productivity}%
                                            </div>
                                        </div>
                                    </td>
                                    <td>${item.orders}</td>
                                    <td>${item.numCustomers}</td>
                                </tr>
                            </g:each>
                            </tbody>
                        </table>
                    </div>
        </div>
    </sec:ifAnyGranted>

</section>


<g:javascript>
    $('.pageableTable').DataTable({
        "pagingType": "full_numbers"
    });
</g:javascript>

</body>

</html>
