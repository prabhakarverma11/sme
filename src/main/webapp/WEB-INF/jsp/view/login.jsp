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
        <h3>Login Access</h3>
      </div>
      <div style="background:#fff;">
      <div class="content">
      <form name='f' action="<c:url value='/j_spring_security_check' />" id="loginform" method='POST' style="margin-bottom: 10px !important;">
      
            <div class="form-group">
              <div class="col-sm-12">
                <div class="input-group"> <span class="input-group-addon"><i class="fa fa-user" style="width:15px;height:14px;"></i></span>
                 <input type="text" name='username' placeholder='UserName' value='${emailId}' id="username" required>
                </div>
              </div>
            </div>
            
              <div class="form-group">
              <div class="col-sm-12">
                <div class="input-group"> <span class="input-group-addon"><i class="fa fa-lock" style="width:15px;height:14px;"></i></span>
                 <input type='password' name='password' placeholder='Password' id="password" required >
                </div>
              </div>
            </div>
             
             
            
          <div class="form-group">
      <div class="col-sm-11">
      <c:choose>
      <c:when test="${error == 'Please verify your account to login!' }">
      <code>${error}</code><a href="${pageContext.request.contextPath}/accountverification?emailId=${emailId}"> Verify Account </a>
      </c:when>
      <c:otherwise>
      	<c:if test="${fn:length(error) gt 0 }">
	      	<code>${error}</code>
      	</c:if>
      </c:otherwise>
      </c:choose>
	
		</div>
		</div>
             
          
             
				<div class="form-group" >
					<div class="col-sm-12">
					<div class="input-group" style="margin:10px 0px;">
						<button type="submit" class="btn btn-success" >
							Login
						</button>
					</div>
					</div>
				</div>
             </form>
             <div class="col-sm-12"  id="idForgotPassword">
						 <a href="${pageContext.request.contextPath}/forgotpassword" >
						      Forgot Password?
						 </a>
             </div>
             
             <div class="col-sm-12" style="margin-bottom:15px;" id="idRegister">
						Don't have an account?  <a href="${pageContext.request.contextPath}/register" >
						      Register
						 </a>
             </div>
             
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



</script> 
<script src="https://code.jquery.com/jquery-2.1.1.min.js"></script>
<script src="${pageContext.request.contextPath}/js/jquery.validate.js"></script>

</body>
</html>
