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
            <li class="${params.controller == 'home' ? 'active':''}"><g:link controller="home" action="index"><i class="glyphicon glyphicon-home"></i>Home</g:link></li>

            %{--    PRODUCTS    --}%
            <li>
                <a data-toggle="dropdown" class="dropdown-toggle" href="#"><i class="glyphicon glyphicon-briefcase"></i>Products <b class="caret"></b></a>
                <ul role="menu" class="dropdown-menu multi-level" role="menu" aria-labelledby="dropdownMenu">
                    <li><g:link controller="product" action="index">Products</g:link></li>
                    <li><g:link controller="productGroup" action="index">Product Groups</g:link></li>

                </ul>
            </li>

            %{--    CUSTOMERS   --}%
            <li><g:link controller="customer" action="index"><i class="glyphicon glyphicon-home"></i>Customers</g:link></li>


            %{-- Tasks --}%
            <li>
                <a data-toggle="dropdown" class="dropdown-toggle" href="#">
                    <i class="glyphicon glyphicon-tasks"></i>Tasks <b class="caret"></b></a>
                <ul role="menu" class="dropdown-menu multi-level" role="menu" aria-labelledby="dropdownMenu">
                    <li>
                        <g:link controller="task" action="index">All Tasks</g:link>
                    </li>
                    <li>
                        <g:link controller="detailerTask" action="index">Detailer</g:link>
                    </li>
                    <li>
                        <g:link controller="call" action="index">Calls</g:link>
                    </li>
                    <li>
                        <g:link controller="sale" action="index">Sales</g:link>
                    </li>
                </ul>

            </li>

            %{-- REPORTS --}%
            <li>
                <a data-toggle="dropdown" class="dropdown-toggle" href="#"><i
                        class="glyphicon glyphicon-dashboard"></i>Reports <b class="caret"></b></a>

                <ul role="menu" class="dropdown-menu multi-level" role="menu" aria-labelledby="dropdownMenu">
                    <li><g:link controller="report" action="index">Reports</g:link></li>
                    <li><g:link controller="reportGroup" action="index">Report Groups</g:link></li>
                </ul>

            </li>

            %{--    SETTINGS    --}%
            <li class="dropdown">
                <a data-toggle="dropdown" class="dropdown-toggle" href="#"><i class="glyphicon glyphicon-wrench"></i>Settings <b class="caret"></b></a>
                <ul role="menu" class="dropdown-menu multi-level" role="menu" aria-labelledby="dropdownMenu">
                    <li><g:link controller="region" action="index">Regions</g:link></li>
                    <li><g:link controller="district" action="index">Districts</g:link></li>
                    <li>
                        <g:link controller="subCounty" action="index">
                            Subcounties
                        </g:link>
                    </li>
                    <li>
                        <g:link controller="parish" action="index">
                            Parishes
                        </g:link>
                    </li>
                    <li>
                        <g:link controller="village" action="index">Village</g:link>
                    </li>
                    <li>
                        <g:link controller="territory" action="index">Territories</g:link>
                    </li>
                    <li>
                        <g:link controller="customerSegment" action="index">Customer Segments</g:link>
                    </li>
                    <li>
                        <g:link controller="wholeSaler" action="index">Whole Salers</g:link>
                    </li>
                    <sec:ifAllGranted roles="ROLE_SUPER_ADMIN">
                        <li>
                            <g:link controller="setting" action="index">Advanced Settings</g:link>
                        </li>
                    </sec:ifAllGranted>
                </ul>
            </li>
        </ul>

        %{-- User Dropdown--}%
        <ul class="nav navbar-nav navbar-right">
            <li class="dropdown">
                <a data-toggle="dropdown" class="dropdown-toggle glyphicon glyphicon-user" href="#">User <b
                        class="caret"></b></a>
                <ul role="menu" class="dropdown-menu multi-level" role="menu" aria-labelledby="dropdownMenu">
                    <li>
                        <g:link controller="user" action="index">
                            <i class="glyphicon glyphicon-user"></i>Users
                        </g:link>
                    </li>
                    <li>
                        <g:link controller="role" action="index">
                            <i class="glyphicon glyphicon-tags"></i>Roles
                        </g:link>
                    </li>
                    <li>
                        <g:link controller="device" action="index">
                            <i class="glyphicon glyphicon-phone"></i>Devices</g:link>
                    </li>
                    <sec:ifAllGranted roles="ROLE_SUPER_ADMIN">
                        <li>
                            <g:link controller="requestMap" action="index">
                                <i class="glyphicon glyphicon-tags"></i>Access Levels
                            </g:link>
                        </li>
                    </sec:ifAllGranted>
                    <li>
                        <g:link controller="logout" action="index">
                            <i class="glyphicon glyphicon-off"></i>Log out</g:link>
                    </li>
                </ul>
            </li>
        </ul>
    </div>
</nav>