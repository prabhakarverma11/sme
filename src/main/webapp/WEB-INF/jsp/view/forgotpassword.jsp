<!DOCTYPE html>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<html lang="en">
<head>
<style>
.underline-on-hover:hover {
    text-decoration: underline;
    cursor: pointer; cursor: hand;
}
</style>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Login</title>
<!-- Bootstrap -->
<link href="${pageContext.request.contextPath}/login/bootstrap/css/bootstrap.min.css" rel="stylesheet">
<link href='http://fonts.googleapis.com/css?family=Open+Sans:400,300' rel='stylesheet' type='text/css'>
<link href="${pageContext.request.contextPath}/login/css/font-awesome.min.css" rel="stylesheet">
<link href="${pageContext.request.contextPath}/login/css/style.css" rel="stylesheet">
<link href="${pageContext.request.contextPath}/login/css/style-responsive.css" rel="stylesheet">
<!-- HTML5 Shim and Respond.js IE8 support of HTML5 elements and media queries -->
<!-- WARNING: Respond.js doesn't work if you view the page via file:// -->

</head>
<body >
<div class="login-container">

  <div class="middle-login">
  <a href="${pageContext.request.contextPath}">
  <div class="login-logo">
  	<img src="images/login-page-logo.gif" alt="logo" />
  </div>
  </a>
    <div class="block-web">
      <div class="head">
        <h3>Forgot Password</h3>
      </div>
      <div style="background:#fff;">
      <div class="content">
      <form name='f' action="forgotpassword" id="loginform" method='POST' style="margin-bottom: 10px !important;">

            <div class="form-group">
              <div class="col-sm-12">
                 <input  type="email" title="Valid Email Id"  name='userEmail' placeholder='Enter E-mail Address' size='45'  value='' id="idUserEmail" required maxlength="100">
                </div>
            </div>
            
            
				<div class="form-group" >
					<div class="col-sm-12">
					<div class="input-group" style="margin:10px 0px;">
						<button type="submit" class="btn btn-success" >
							Send New Password
						</button>
					</div>
					</div>
				</div>
				
				   <div class="form-group">
      <div class="col-sm-12" style="margin-bottom: 20px;">
       <c:if test="${(msg!=null) and (errorMsg eq 1)}">
			<span class="text-primary">${msg}</span>
		</c:if>
		<c:if test="${(msg!=null) and (errorMsg eq 0)}">
			<span class="text-danger">${msg}</span>
		</c:if>
		</div>
		</div>
		
		 <c:if test="${(msg!=null) and (errorMsg eq 1)}">
			<div class="col-sm-12" style="margin-bottom:15px;"  id="idLogin">
						<code><a href="${pageContext.request.contextPath}/Welcome" class="text-warning"
							 > Login</a></code> 
					</div>
		</c:if>
		
		<c:if test="${(msg!=null) and (errorMsg eq 0)}">
		 <div class="col-sm-12" style="margin-bottom:15px;" id="idRegister">
						Don't have an account?  <a href="${pageContext.request.contextPath}/register" >
						      Register
						 </a>
             </div>
		</c:if>
		
             </form>
             
          </div>
      
        
        
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
