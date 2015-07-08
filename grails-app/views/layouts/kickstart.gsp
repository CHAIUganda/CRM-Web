<!DOCTYPE html>
<%-- <html lang="${org.springframework.web.servlet.support.RequestContextUtils.getLocale(request).toString().replace('_', '-')}"> --%>
<html lang="${session.'org.springframework.web.servlet.i18n.SessionLocaleResolver.LOCALE'}">

<head>
    <title><g:layoutTitle default="${meta(name:'app.name')}" /></title>

    <meta charset="utf-8">
    <meta name="viewport"		content="width=device-width, initial-scale=1.0">
    <meta name="description"	content="">
    <meta name="author"			content="">

    <link rel="shortcut icon"		href="${resource(dir:'images',file:'favicon.ico')}" type="image/x-icon" />

    <link rel="apple-touch-icon"	href="assets/ico/apple-touch-icon.png">
    <link rel="apple-touch-icon"	href="assets/ico/apple-touch-icon-72x72.png"	sizes="72x72">
    <link rel="apple-touch-icon"	href="assets/ico/apple-touch-icon-114x114.png"	sizes="114x114">
    <link rel="stylesheet" href="${resource(dir: 'css', file: 'omnitech.css')}" type="text/css">

    <%-- Manual switch for the skin can be found in /view/_menu/_config.gsp --%>
    <r:require modules="jquery"/> <%-- jQuery is required for Bootstrap! --%>
    <r:require modules="bootstrap"/>
    <r:require modules="bootstrap_utils"/>

    <r:layoutResources />
    <g:layoutHead />

    <!-- Le HTML5 shim, for IE6-8 support of HTML elements -->
    <!--[if lt IE 9]>
		<script src="https://html5shim.googlecode.com/svn/trunk/html5.js"></script>
	<![endif]-->

    <%-- For Javascript see end of body --%>
</head>
    <%
        def server = grailsApplication.config.deploy.server
        def bannerBackground = ''
        def bannerText = ''
        switch (server){
            case 'dev':
                bannerBackground = 'red'
                bannerText = 'DEVELOPMENT SERVER'
                break
            case 'test':
                bannerBackground = 'green'
                bannerText = 'TEST SERVER'
                break

        }
    %>
<body>
<div class="container" style=" max-width: 1170px; padding: 0; background: fff; box-shadow: none;">
    <script>
        omnitechBase = '${request.contextPath}';
    </script>
    <div class="container" id="header" style="margin-top: 10px; max-width: 1170px; padding: 0;">
    	<div class="server-status" style="background: ${bannerBackground}">
           ${bannerText}
        </div>
        <div class="clear"></div>
        <div class="logo">
            <a href="${createLink(uri: '/', absolute: true)}">
            	<g:img uri="/images/Clinton-Health.png" absolute="true" />
            </a>
        </div>
	    <!-- Main menu in one row (e.g., controller entry points -->
	    <g:if test="${!layout_nomainmenu}">
	        <g:render template="/_menu/menubar"/>
	    </g:if>
	</div>

    <div ng-app="omnitechApp">
        <g:render template="/layouts/content"/>
    </div>

    <!-- Enable to overwrite Footer by individual page -->
    <g:if test="${ pageProperty(name:'page.footer') }">
        <g:pageProperty name="page.footer" />
    </g:if>
    <g:else>
        <g:render template="/layouts/footer"/>
    </g:else>

    <!-- Enable to insert additional components (e.g., modals, javascript, etc.) by any individual page -->
    <g:if test="${ pageProperty(name:'page.include.bottom') }">
        <g:pageProperty name="page.include.bottom" />
    </g:if>
    <g:else>
        <!-- Insert a modal dialog for registering (for an open site registering is possible on any page) -->
        <g:render template="/_common/modals/registerDialog" model="[item: item]"/>
    </g:else>

    <!-- Included Javascript files and other resources -->
    <g:javascript src="jquery.sticky.js"/>
    <g:javascript src="utils.js"/>
    <r:layoutResources />
</div>
</body>

</html>