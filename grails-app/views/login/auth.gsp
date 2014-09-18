<html>
<head>
	<title><g:message code="springSecurity.login.title"/></title>
	%{--<meta name="layout" content="kickstart" />

	<g:set var="layout_nomainmenu"		value="${true}" scope="request"/>
	<g:set var="layout_nosecondarymenu"	value="${true}" scope="request"/>--}%
    <r:require modules="jquery"/> <%-- jQuery is required for Bootstrap! --%>
    <r:require module="jquery-ui"/>
    <r:require modules="bootstrap"/>
    <r:require modules="bootstrap_utils"/>

    <r:layoutResources />
    <link rel="stylesheet" href="${resource(dir: 'css', file: 'omnitech.css')}"
</head>

<body>
<div class=" col-lg-12 header-wrapper">
    <div class="col-lg-12 col-lg-offset-5" style="top: 30px;">
       <h3 style="color: #ffffff;font-weight: bold;">International Alert</h3>
    </div>
</div>

<div class="col-lg-12 col-lg-offset-4" style="padding: 0px;top:40px;">

    <div class="col-lg-4"
         style="background: none repeat scroll 0 0 #F7F7F4;border: 1px solid #EAEAEA;padding: 40px;">
        <div class="col-lg-12" style="height: 10%;"></div>

        <div class="col-lg-12" style="padding: 0px;">

            <div class="col-lg-12" style="padding: 10px;">

                <form role="form" id="loginForm" action="${postUrl}" method="POST" autocomplete="off"
                      class="form-horizontal">

                    <div class="input-group input-group-lg" style="padding: 10px;">
                        <span class="input-group-addon"><span class="glyphicon glyphicon-user"></span></span>

                        <input type='text' name='j_username' id='username' class='form-control'
                               placeholder='Username' required autofocus />
                    </div>

                    <div class="input-group input-group-lg" style="padding: 10px;">
                        <span class="input-group-addon"><span class="glyphicon glyphicon-lock"></span></span>
                        <input type='password' name='j_password' id='password' class='form-control'
                               placeholder='Password' required />
                    </div>

                    <div class="form-group">
                        <div class="col-xs-10" style="margin-left: 11px;">
                            <div class="checkbox">
                                <input type='checkbox' class='chk' name='${rememberMeParameter}' id='remember_me'
                                       <g:if test='${hasCookie}'>checked='checked'</g:if>/>
                                <label for='remember_me'><g:message
                                        code="springSecurity.login.remember.me.label"/></label>
                            </div>
                        </div>
                    </div>

                    <div class="col-lg-12">

                        <input type='submit' id="submit" value='${message(code: "springSecurity.login.button")}'
                               class="btn btn-lg btn-primary btn-block"/>
                    </div>

                </form>

            </div>
            <g:if test='${flash.message}'>
                <div class='login_message' style="color: #ff0000;">${flash.message}</div>
            </g:if>
        </div>
    </div>

</div>
<div class="col-lg-12" style="padding: 0px;margin: 0px;">
    <g:render template="/layouts/footer"/>
</div>

<script type='text/javascript'>
	<!--
	(function() {
		document.forms['loginForm'].elements['j_username'].focus();
	})();
	// -->
</script>

<r:layoutResources/>
</body>
</html>
