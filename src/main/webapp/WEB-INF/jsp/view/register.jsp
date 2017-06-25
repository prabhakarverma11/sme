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
<title>Login</title>
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
					<c:if test="${errorMsg ne 1}">
						<h3>Register User</h3>
					</c:if>
				</div>

				<div class="row" style="margin: 0px 15px 0px 15px;">
					<c:if test="${(msg!=null) and (errorMsg eq 0)}">
						<br>
						<div class="alert alert-danger fade in text-center">
							<a href="#" class="close" data-dismiss="alert" aria-label="close">&times;</a>
							<c:out value="${msg}" />
						</div>
					</c:if>
				</div>
				<c:if test="${(msg  eq null) or (errorMsg eq 0) }">
					<div style="background: #fff; margin: 15px 0px;">
						<form:form class="form-horizontal"
							onsubmit="return matchPassword();" role="form" method="post"
							action="registernewuser" modelAttribute="userDo" id="userform">

							<div class="container">
								<div class="row">
									<div class="form-group">
										<label for="name" class="col-lg-2 control-label">Name<span
											style="color: red;">*</span>
										</label>
										<div class="col-lg-3">
											<font color="red"></font>
											<form:input type="text" class="form-control" id="idName"
												placeholder="" path="name" maxlength="100"
												required="required" />

										</div>
									</div>
								</div>




								<div class="row">
									<div class="form-group">
										<label for="phone_no" class="col-lg-2 control-label">Phone
											No<span style="color: red;">*</span>
										</label>
										<div class="col-lg-3">
											<font color="red"></font>
											<div class="input-group">
												<span class="input-group-addon"><i
													class="glyphicon glyphicon-earphone"
													style="width: 15px; height: 14px;"></i></span>
												<form:input type="text" pattern="[0-9]{10,11}"
													title="Enter Valid phone number (Don't use '-')"
													class="form-control" id="idPhoneno" path="phoneno"
													placeholder="" maxlength="11" required="required" />
											</div>
										</div>
									</div>
								</div>


								<div class="row">
									<div class="form-group">
										<label for="email" class="col-lg-2 control-label">Email<span
											style="color: red;">*</span>
										</label>
										<div class="col-lg-3">
											<font color="red"></font>
											<div class="input-group">
												<span class="input-group-addon"><i
													class="glyphicon glyphicon-envelope"
													style="width: 15px; height: 14px;"></i></span>

												<form:input type="email" title="Valid Email Id"
													class="form-control" id="idEmail" placeholder=""
													path="email" maxlength="100" required="required" />
											</div>
										</div>
									</div>
								</div>


								<div class="row">
									<div class="form-group">
										<label for="password" class="col-lg-2 control-label">Password<span
											style="color: red;">*</span>
										</label>
										<div class="col-lg-3">
											<font color="red"></font>
											<form:input type="password" class="form-control"
												id="idPassword" placeholder="" path="password"
												maxlength="100" required="required" />

										</div>
									</div>
								</div>



								<div class="row">
									<div class="form-group">
										<label for="retype_password" class="col-lg-2 control-label">Retype
											Password<span style="color: red;">*</span>
										</label>
										<div class="col-lg-3">
											<font color="red"></font>
											<form:input type="password" class="form-control"
												id="idRetypePassword" placeholder="" path="" maxlength="100"
												required="required" />

										</div>
									</div>
								</div>


								<div class="row">
									<div class="form-group">
										<div class=" col-lg-offset-2 col-lg-2">
											<button type="submit" class="btn btn-success">Save</button>
										</div>
									</div>
								</div>

								<div class="row">
									<div class="form-group">
										<div class="col-sm-12 col-sm-offset-2"
											style="margin-bottom: 15px;">
											Already have an account? <a
												href="${pageContext.request.contextPath}/Welcome"> Login
											</a>
										</div>
									</div>
								</div>

							</div>
						</form:form>
					</div>
				</c:if>
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
	<script type="text/javascript">
		function matchPassword() {
			if ($("#idPassword").val() != $("#idRetypePassword").val()) {
				alert("Passwords do not match");
				return false;
			}
			return true;
		}
	</script>
</body>
</html>
