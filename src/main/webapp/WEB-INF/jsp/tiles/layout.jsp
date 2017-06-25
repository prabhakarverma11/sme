<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="sec"
	uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<!DOCTYPE html>
<html lang="en">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">

<link rel="stylesheet" href="css/jquery-ui.css">

<%
	Object sUser =  session.getAttribute("sUser");
	Object userRoleDo =  session.getAttribute("userRoleDo");
%>

<!-- Bootstrap -->
<link href='bootstrap/css/bootstrap.min.css' rel="stylesheet"
	media="screen">
<link href='bootstrap/css/bootstrap-toggle.min.css' rel="stylesheet">
	

<link href='css/stylenew.css' rel="stylesheet" media="screen">
<link href='css/font-awesome.min.css' rel="stylesheet" media="screen">
<link type="text/css" rel="stylesheet" href="css/simplePagination.css"/>
<link type="text/css" rel="stylesheet" href="https://cdn.datatables.net/1.10.13/css/jquery.dataTables.min.css"/>
<link rel="stylesheet" href="//code.jquery.com/ui/1.12.1/themes/base/jquery-ui.css">
<style>
.datepicker>span:hover {
	cursor: pointer;
}

.datepicker .next {
	cursor: pointer;
}

.datepicker .prev {
	cursor: pointer;
}

.datepicker .day {
	cursor: pointer;
}

.breadcrumb{
	padding: 3px;
	font-size: 13px;
}

.cursor-pointer{
	cursor: pointer;
}
</style>

<script src="https://code.jquery.com/jquery-1.12.4.js"
	type="text/javascript"></script>
<script src="<%=request.getContextPath()%>/js/jquery-ui.js"></script>
<script src="<%=request.getContextPath()%>/js/jquery.validate.js"></script>
<script src="<%=request.getContextPath()%>/js/additional-methods.min.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery.simplePagination.js"></script>
<script type="text/javascript" src="https://cdn.datatables.net/1.10.13/js/jquery.dataTables.min.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/dataTables.bootstrap.min.js"></script>


<script
	src="<%=request.getContextPath()%>/bootstrap/js/bootstrap.min.js"></script>
<script
	src="<%=request.getContextPath()%>/bootstrap/js/bootstrap-toggle.min.js"></script>
	
	
<!-- fusioncharts -->
<script type="text/javascript" src="<%=request.getContextPath()%>/js/fusioncharts.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/fusioncharts.charts.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/fusioncharts.theme.fint.js"></script>


<script src="<%=request.getContextPath()%>/js/loadingoverlay.min.js" type="text/javascript"></script>
<script src="<%=request.getContextPath()%>/js/loadingoverlay_progress.min.js" type="text/javascript"></script>
<script>
	$(document).on("click",".page-link",function(){
		$.LoadingOverlay("show",{fade:false,imagePosition: "center center"});
	});
	$(document).on("click",".ui-menu-item",function(){
		$.LoadingOverlay("show",{fade:false,imagePosition: "center center"});
	});
	$(document).on("click",".container a",function(){
		if(!$(this).attr("data-toggle") && ["idDownloadReport","editbtn","idNewKeyword","idAddMail"].indexOf($(this).attr("id")) ==-1 && ["close"].indexOf($(this).attr("class")) == -1)
			$.LoadingOverlay("show",{fade:false,imagePosition: "center center"});
	});
	function initLoader($){
		$.LoadingOverlay("show",{fade:true,imagePosition: "center center",zIndex:9999,color: "rgba(255, 255, 255, 0.5)"});
	};

</script>
<title><tiles:insertAttribute name="title" ignore="true" /></title>

</head>
<body>

	<div class="container">
	 
		<div class="row">
			<tiles:insertAttribute name="header" />
		</div>
		<div class="row" >
		
		<div class="col-md-12 text-right">
				<i class="glyphicon glyphicon-user"></i> 
				<sec:authorize access="hasRole('ROLE_ADMIN')">
				<span class="text-warning"><b>Hi <c:out value="${sUser.name}" /></b></span><kbd>(Admin)</kbd>
				</sec:authorize>
				<sec:authorize access="hasRole('ROLE_USER')">
				<span class="text-warning"><b>Hi <c:out value="${sUser.name}" /></b></span>
				</sec:authorize>
			</div>
			
			
			</div>
		
		
		
		<div class="row">
			<tiles:insertAttribute name="menu" />
		</div>

		

		<div>
			<tiles:insertAttribute name="body" />
		</div>

<br>
		<div class="row">

			<tiles:insertAttribute name="footer" />
		</div>
	</div>
	<!--  for date picker -->



</body>
</html>