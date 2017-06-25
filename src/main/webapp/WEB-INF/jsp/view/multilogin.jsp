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
  <div class="login-logo">
  	<img src="images/login-page-logo.gif" alt="logo" />
  </div>
    <div class="block-web">
      <div class="head">
        <h3>Multiple Login Detected</h3>
      </div>
      <div style="background:#fff;">
      <div class="content">
      <form:form class="form-horizontal" role="form" modelAttribute="userSessionDos"  
      action="destroySession" id="idDestroySessionForm" method='POST' >
      
      <div class="row">
      <h4 class="text-primary" style="padding-left:10px;">Please select a session to destroy</h4>
      </div>
      
            <div class="form-group">
            <c:forEach items="${userSessionDos}" var="userSessionDo">
							<label class="col-sm-12 " style="padding:5px 15px;">
								<input type="radio" name="sessionRadio" value="${userSessionDo.sessionId}"/>
								 ${userSessionDo.deviceDetails} Logged in at <i class="small"> ${userSessionDo.loggedInTime}</i></label>
								 
								</c:forEach>
								<label class="col-sm-12" style="top:-10px;">
								<span class="small text-warning" style="padding-left:15px;">(Current session)</span></label>
						</div>
						
						
			
             
          
             
				<div class="form-group" >
					<div class="col-sm-12">
					<div class="input-group" style="margin:10px 0px;">
						<button type="submit" class="btn btn-success" >
							Submit
						</button>
					</div>
					</div>
				</div>
				
				
				
             </form:form>
          
             
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
<script>
$(document).ready(function(){
	$("input:radio[name=sessionRadio]:first").attr('checked', true);
});
</script>
</body>
</html>
