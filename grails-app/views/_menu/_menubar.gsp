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
            <li class="active"><a href="#">Home</a></li>
            <li><a href="#">Profile</a></li>

            %{--    PRODUCTS    --}%
            <li><g:link controller="product" action="index">Products</g:link></li>

            %{--    CUSTOMERS   --}%
            <li><g:link controller="customer" action="index">Customers</g:link></li>


            %{--    SETTINGS    --}%
            <li class="dropdown">
                <a data-toggle="dropdown" class="dropdown-toggle" href="#">Settings <b class="caret"></b></a>
                <ul role="menu" class="dropdown-menu multi-level" role="menu" aria-labelledby="dropdownMenu">
                    <li>
                        <a class="trigger right-caret" href="#">Location</a>
                        <ul class="dropdown-menu sub-menu">
                            <li>
                                <g:link controller="district" action="index">
                                    Districts
                                </g:link>
                            </li>
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
                        </ul>
                    </li>
                    <li><a href="#">Access Logs</a></li>
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