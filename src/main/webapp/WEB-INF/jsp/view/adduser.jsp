<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<div class="row breadcrumb">
	<a class="breadcrumb-item" href="${pageContext.request.contextPath}/listuser">
				User List</a> >> <span class="active">Add User</span>
</div>
<div class="auto-1000">
	<div class="main-container">
		<div class="row">
			<div>
				<div class="col-md-12 text-center">
					<div class="text-center">
						<c:if test="${(msg!=null) and (errorMsg eq 0)}">
							<div class="alert alert-danger text-center">
								<a href="#" class="close" data-dismiss="alert"
									aria-label="close">&times;</a>
								<c:out value="${msg}" />
							</div>
						</c:if>
						<c:if test="${(msg!=null) and (errorMsg eq 1)}">
							<div class="alert alert-success text-center">
								<a href="#" class="close" data-dismiss="alert"
									aria-label="close">&times;</a>
								<c:out value="${msg}" />
							</div>
						</c:if>
					</div>
				</div>
			</div>
		</div>

		<form:form class="form-horizontal" onsubmit="return matchPassword();"
			role="form" method="post" action="saveuser" modelAttribute="userDo"
			id="userform">

			<div class="container">
				<div class="row">
					<div class="form-group">
						<label for="name" class="col-lg-2 control-label">Name<span
							style="color: red;">*</span>
						</label>
						<div class="col-lg-6">
							<font color="red"></font>
							<form:input type="text" class="form-control" id="idName"
								placeholder="" path="name" maxlength="100" required="required" />

						</div>
					</div>
				</div>




				<div class="row">
					<div class="form-group">
						<label for="phone_no" class="col-lg-2 control-label">Phone
							No<span style="color: red;">*</span>
						</label>
						<div class="col-lg-6">
							<font color="red"></font>
							<div class="input-group">
								<span class="input-group-addon"><i
									class="glyphicon glyphicon-earphone"
									style="width: 15px; height: 14px;"></i></span>
								<form:input type="text" maxlength="10" pattern="([1-9])\d{9}"
									title="Enter Valid phone number (Don't use '-')"
									class="form-control" id="idPhoneno" path="phoneno"
									placeholder="" required="required" />
							</div>
						</div>
					</div>
				</div>


				<div class="row">
					<div class="form-group">
						<label for="email" class="col-lg-2 control-label">Email<span
							style="color: red;">*</span>
						</label>
						<div class="col-lg-6">
							<font color="red"></font>
							<div class="input-group">
								<span class="input-group-addon"><i
									class="glyphicon glyphicon-envelope"
									style="width: 15px; height: 14px;"></i></span>

								<form:input type="email" title="Valid Email Id"
									class="form-control" id="idEmail" placeholder="" path="email"
									required="required" maxlength="100" />
							</div>
						</div>
					</div>
				</div>


				<div class="row">
					<div class="form-group">
						<label for="password" class="col-lg-2 control-label">Password<span
							style="color: red;">*</span>
						</label>
						<div class="col-lg-6">
							<font color="red"></font>
							<form:input type="password" class="form-control" id="idPassword"
								placeholder="" path="password" maxlength="100"
								required="required" />

						</div>
					</div>
				</div>



				<div class="row">
					<div class="form-group">
						<label for="retype_password" class="col-lg-2 control-label">Retype
							Password<span style="color: red;">*</span>
						</label>
						<div class="col-lg-6">
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
							<button type="submit" class="btn btn-success"
								id="idButtonSaveUser" data-loading-text="Saving User...">
								Save</button>
						</div>
					</div>
				</div>
			</div>
		</form:form>
	</div>
</div>
<script type="text/javascript">
	$(document).ready(function(){
		$("#idDropdownUser").addClass("active");
		$("#idDropdownAddUser").addClass("active");
	});
	function matchPassword() {
		if ($("#idPassword").val() != $("#idRetypePassword").val()) {
			alert("Passwords do not match");
			return false;
		}
		$("#idButtonSaveUser").button('loading');
		initLoader($);
		return true;
	}
</script>