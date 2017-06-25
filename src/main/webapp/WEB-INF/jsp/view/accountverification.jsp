<!DOCTYPE html>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<html lang="en">
<head>

<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Verfication Mail Sent</title>
<!-- Bootstrap -->
<link
	href="${pageContext.request.contextPath}/login/bootstrap/css/bootstrap.min.css"
	rel="stylesheet">
<link href='http://fonts.googleapis.com/css?family=Open+Sans:400,300'
	rel='stylesheet' type='text/css'>
<link
	href="${pageContext.request.contextPath}/login/css/font-awesome.min.css"
	rel="stylesheet">
<link href="${pageContext.request.contextPath}/login/css/style.css"
	rel="stylesheet">
<link
	href="${pageContext.request.contextPath}/login/css/style-responsive.css"
	rel="stylesheet">
<!-- HTML5 Shim and Respond.js IE8 support of HTML5 elements and media queries -->
<!-- WARNING: Respond.js doesn't work if you view the page via file:// -->

</head>
<body>
	<div class="login-container">

		<div class="col-md-offset-3 col-md-6">
		
		<a href="${pageContext.request.contextPath}">
			<div class="login-logo" style="margin-top: 30px;">
				<img src="images/login-page-logo.gif" alt="logo"
					style="margin-left: auto; margin-right: auto; display: block;" />
			</div>
			</a>
			<div class="block-web">
				<div class="head">
					<h3>Account Verification</h3>
				</div>

				<div class="row" style="margin: 0px 15px 0px 15px;">
					<c:if test="${(msg!=null) and (errorMsg eq 0)}">
						<br>
						<div class="alert alert-danger fade in text-center">
							<a href="#" class="close" data-dismiss="alert" aria-label="close">&times;</a>
							<c:out value="${msg}" />
						</div>
					</c:if>
					<c:if test="${(msg!=null) and (errorMsg eq 1)}">
						<br>
					<div class="alert alert-success fade in text-center">
						<a href="#" class="close" data-dismiss="alert" aria-label="close">&times;</a>
						<c:out value="${msg}" />
					</div>
					</c:if>
					<div align="center">
						<a href="${pageContext.request.contextPath}/Welcome"
							class="btn btn-success" role="button"> Login </a>
					</div>
					<br>


				</div>

			</div>

		</div>
	</div>

	<!-- jQuery (necessary for Bootstrap's JavaScript plugins) -->
	<script src="login/js/jquery-2.0.2.min.js"></script>
	<!-- Include all compiled plugins (below), or include individual files as needed -->
	<script src="login/bootstrap/js/bootstrap.min.js"></script>
	<script type="text/javascript">
		/* $("#loginform").validate(); */
	</script>
	<script src="https://code.jquery.com/jquery-2.1.1.min.js"></script>
	<script src="${pageContext.request.contextPath}/js/jquery.validate.js"></script>
</body>
</html>