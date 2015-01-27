<%@ page import="com.omnitech.chai.model.Role; com.omnitech.chai.model.Task" %>
<nav role="navigation" class="navbar navbar-inverse" style="border-radius: 0px;">
    <!-- Brand and toggle get grouped for better mobile display -->
    <div class="navbar-header">
        <button type="button" data-target="#navbarCollapse" data-toggle="collapse" class="navbar-toggle">
            <span class="sr-only">Toggle navigation</span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
        </button>
    </div>
    <!-- Collection of nav links, forms, and other content for toggling -->
    <div id="navbarCollapse" class="collapse navbar-collapse">
        <ul class="nav navbar-nav">
            <li class="${params.controller == 'home' ? 'active' : ''}"><sec2:link controller="home" action="index"><i
                    class="glyphicon glyphicon-home"></i>Home</sec2:link></li>

        %{--    PRODUCTS    --}%
            <sec:ifAnyGranted roles="${"$Role.ADMIN_ROLE_NAME,$Role.SUPER_ADMIN_ROLE_NAME"}">
                <li>
                    <a data-toggle="dropdown" class="dropdown-toggle" href="#"><i
                            class="glyphicon glyphicon-briefcase"></i>Products <b class="caret"></b></a>
                    <ul role="menu" class="dropdown-menu multi-level" role="menu" aria-labelledby="dropdownMenu">
                        <li><sec2:link controller="product" action="index">Products</sec2:link></li>
                        <li><sec2:link controller="productGroup" action="index">Product Groups</sec2:link></li>

                    </ul>
                </li>
            </sec:ifAnyGranted>




        %{--    CUSTOMERS   --}%
            <li><sec2:link controller="customer" action="index"><i
                    class="glyphicon glyphicon-home"></i>Customers</sec2:link>
            </li>


            %{-- Tasks --}%
            <li>
                <a data-toggle="dropdown" class="dropdown-toggle" href="#">
                    <i class="glyphicon glyphicon-tasks"></i>Tasks <b class="caret"></b></a>
                <ul role="menu" class="dropdown-menu multi-level" role="menu" aria-labelledby="dropdownMenu">
                    <li>
                        <sec2:link controller="detailerTask" action="index"
                                   params="${[status: Task.STATUS_NEW]}">Detailer</sec2:link>
                    </li>
                    <li>
                        <sec2:link controller="call" action="index"
                                   params="${[status: Task.STATUS_NEW]}">Calls</sec2:link>
                    </li>
                    <li>
                        <sec2:link controller="sale" action="index">Sales</sec2:link>
                    </li>
                    <li>
                        <sec2:link controller="taskSetting"
                                   action="generationDetailer">Generate Detailing Tasks</sec2:link>
                    </li>
                    <li>
                        <sec2:link controller="taskSetting" action="generationOrder">Generate Calls</sec2:link>
                    </li>
                </ul>

            </li>

        %{-- REPORTS --}%
            <sec:ifAnyGranted
                    roles="${"$Role.ADMIN_ROLE_NAME,$Role.SUPER_ADMIN_ROLE_NAME,$Role.DETAILING_SUPERVISOR_ROLE_NAME,$Role.SALES_SUPERVISOR_ROLE_NAME"}">
                <li>
                    <a data-toggle="dropdown" class="dropdown-toggle" href="#"><i
                            class="glyphicon glyphicon-dashboard"></i>Reports <b class="caret"></b></a>

                    <ul role="menu" class="dropdown-menu multi-level" role="menu" aria-labelledby="dropdownMenu">
                        <li><sec2:link controller="report" action="index">Reports</sec2:link></li>
                        <li><sec2:link controller="reportGroup" action="index">Report Groups</sec2:link></li>
                    </ul>

                </li>
            </sec:ifAnyGranted>

        %{--    SETTINGS    --}%
            <sec:ifAnyGranted roles="${"$Role.ADMIN_ROLE_NAME,$Role.SUPER_ADMIN_ROLE_NAME"}">
                <li class="dropdown">
                    <a data-toggle="dropdown" class="dropdown-toggle" href="#"><i
                            class="glyphicon glyphicon-wrench"></i>Settings <b class="caret"></b></a>
                    <ul role="menu" class="dropdown-menu multi-level" role="menu" aria-labelledby="dropdownMenu">
                        <li><sec2:link controller="region" action="index">Regions</sec2:link></li>
                        <li><sec2:link controller="district" action="index">Districts</sec2:link></li>
                        <li>
                            <sec2:link controller="subCounty" action="index">
                                Subcounties
                            </sec2:link>
                        </li>
                        <li>
                            <sec2:link controller="parish" action="index">
                                Parishes
                            </sec2:link>
                        </li>
                        <li>
                            <sec2:link controller="village" action="index">Village</sec2:link>
                        </li>
                        <li>
                            <sec2:link controller="customerSegment" action="index">Customer Segments</sec2:link>
                        </li>
                        <li>
                            <sec2:link controller="wholeSaler" action="index">Whole Salers</sec2:link>
                        </li>
                        <sec:ifAllGranted roles="ROLE_SUPER_ADMIN">
                            <li>
                                <sec2:link controller="setting" action="index">Advanced Settings</sec2:link>
                            </li>
                        </sec:ifAllGranted>
                    </ul>
                </li>
            </sec:ifAnyGranted>
        </ul>

        %{-- User Dropdown--}%
        <ul class="nav navbar-nav navbar-right">
            <li class="dropdown">
                <a data-toggle="dropdown" class="dropdown-toggle glyphicon glyphicon-user"
                   href="#">Users(<sec:username/>) <b
                        class="caret"></b></a>
                <ul role="menu" class="dropdown-menu multi-level" role="menu" aria-labelledby="dropdownMenu">
                    <li>
                        <sec2:link controller="user" action="index">
                            <i class="glyphicon glyphicon-user"></i>Users
                        </sec2:link>
                    </li>
                    <li>
                        <sec2:link controller="territory" action="index">
                            <i class="glyphicon glyphicon-globe"></i>
                            Territories
                        </sec2:link>
                    </li>
                    <li>
                        <sec2:link controller="role" action="index">
                            <i class="glyphicon glyphicon-tags"></i>Roles
                        </sec2:link>
                    </li>
                    <li>
                        <sec2:link controller="device" action="index">
                            <i class="glyphicon glyphicon-phone"></i>Devices</sec2:link>
                    </li>

                    <li>
                        <sec2:link controller="requestMap" action="index">
                            <i class="glyphicon glyphicon-tags"></i>Access Levels
                        </sec2:link>
                    </li>

                    <li>
                        <sec2:link controller="logout" action="index">
                            <i class="glyphicon glyphicon-off"></i>Log out</sec2:link>
                    </li>
                </ul>
            </li>
        </ul>
    </div>
</nav>