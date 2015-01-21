<html>
	<head>
		<title>403 - Forbidden!</title>
		<meta name="layout" content="kickstart" />
		<g:set var="layout_nomainmenu"		value="${true}" scope="request"/>
		<g:set var="layout_nosecondarymenu"	value="${true}" scope="request"/>
	</head>

<body>
	<content tag="header">
		<!-- Empty Header -->
	</content>
	
  	<section id="Error" class="">
		<div class="big-message">
			<div class="container">
				<h1>
					<g:message code="error.403.callout"/>
				</h1>
				<h2>
					<g:message code="error.403.title"/>
				</h2>
				<p>
					<g:message code="error.403.message"/>
				</p>
				
				<div class="actions">
					<a href="${createLink(uri: '/')}" class="btn btn-large btn-primary">
						<i class="glyphicon glyphicon-chevron-left icon-white"></i>
						<g:message code="error.button.backToHome"/>
					</a>
					%{--<a  href="${createLink(uri: '/login/auth')}" class="btn btn-large btn-success">--}%
						%{--<i class="glyphicon glyphicon-log-in"></i> Back to Login--}%
					%{--</a>--}%


						<g:link controller="logout" action="index"  class="btn btn-large btn-danger">
							<i class="glyphicon glyphicon-off"></i>Log out</g:link>


				</div>
			</div>
		</div>
	</section>
  
  
  </body>
</html>