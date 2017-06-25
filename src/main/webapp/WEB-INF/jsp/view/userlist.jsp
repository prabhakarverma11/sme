<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<%
int totalRecords = (Integer) session.getAttribute("totalRecords");
int recordsPerPage = (Integer)session.getAttribute("recordsPerPage");
int pageNumber = (Integer)session.getAttribute("pageNumber");
%>
<style>
</style>
<div class="row breadcrumb">
	<span class="active">User List</span>
</div>
<div class="container">
<!-- <div class="row alert alert-warning text-center">Users</div> -->

<div class="row">
	<c:if test="${(msg!=null) and (errorMsg gt 0)}">
		<div class="alert alert-success fade in text-center">
			<a href="#" class="close" data-dismiss="alert" aria-label="close">&times;</a>${msg}</div>
	</c:if>

	<c:if test="${(msg!=null) and (errorMsg eq 0)}">
		<div class="alert alert-danger  fade in text-center">
			<a href="#" class="close" data-dismiss="alert" aria-label="close">&times;</a>${msg}</div>
	</c:if>
</div>

<div class="row">
<div>
	<div align="right">
<a href="${pageContext.request.contextPath}/adduser"><i class="glyphicon glyphicon-plus"></i> Add User</a>
</div>
</div>



<c:if test="${fn:length(userDos) gt 0}">

	<div class="table-responsive">
		<table id="idUserTable" class="table table-bordered table-hover table-condensed">
			<thead>
				<tr>
					<th id="idThSNo">S. No</th>
					<th id="idThNa" class="cursor-pointer">Name
						<c:if test="${col eq null or col eq '' or col ne 'na'}">
							<i class="fa fa-fw fa-sort pull-right" style="color:blue;" aria-hidden="true"></i>
						</c:if>
						<c:if test="${col eq 'na' and o eq 0 }">
							<i class="fa fa-fw fa-sort-asc pull-right" style="color:blue;" aria-hidden="true"></i>
						</c:if>
						<c:if test="${col eq 'na' && o eq 1 }">
							<i class="fa fa-fw fa-sort-desc pull-right" style="color:blue;" aria-hidden="true"></i>
						</c:if>	
					</th>
					<th id="idThEm" class="cursor-pointer">Email
						<c:if test="${col eq null or col eq '' or col ne 'em'}">
							<i class="fa fa-fw fa-sort pull-right" style="color:blue;" aria-hidden="true"></i>
						</c:if>
						<c:if test="${col eq 'em' and o eq 0 }">
							<i class="fa fa-fw fa-sort-asc pull-right" style="color:blue;" aria-hidden="true"></i>
						</c:if>
						<c:if test="${col eq 'em' && o eq 1 }">
							<i class="fa fa-fw fa-sort-desc pull-right" style="color:blue;" aria-hidden="true"></i>
						</c:if>
					</th>
					<th id="idThPh">Phone Number
					</th>
					<th id="idThAc">Action
					</th>
				</tr>
			</thead>
        	<c:set var="count" value="${(pageNumber-1)*recordsPerPage}" scope="page" />
			<c:forEach items="${userDos}" var="userDo">
				<c:set var="count" value="${count + 1}" scope="page"/>

				<tr>
					<td><c:out value="${count}" /></td>
					<td><c:out value="${userDo.name}" /></td>
					<td><c:out value="${userDo.email}" /></td>
					<td><c:out value="${userDo.phoneno}" /></td>
					<td><a href="edituser?uid=<c:out value="${userDo.id}" />">Edit</a></td>
				</tr>
				
			</c:forEach>
		</table>
	</div>
</c:if>
<c:if test="${fn:length(userDos) eq 0}">
							
	<div class="text-danger" style="margin:0 auto;width:50%;text-align:center">
	<strong>No user added yet !!</strong></div>
								
						</c:if> 
		<div class="pagination-page" style="float:right; "></div>
</div>
</div>
<script>
	
	$(document).ready(function(){
		$("#idUserTable").on("click","th",function(){
			var tagId = $(this).attr("id");
			if(tagId == "idThNa"){//advertiser name
				initLoader($);
				if($(this).find("i").length == 0){
					window.location=document.URL.substring(0,lastSlash)+  "/listuser?col=na&o=0";
				}else if($(this).find("i").attr("class").indexOf("fa-sort-asc") > -1){
					$(this).find("i").removeClass("fa-sort-asc").addClass("fa-sort-desc");
					window.location=document.URL.substring(0,lastSlash)+  "/listuser?col=na&o=1";
				}
				else {
					$(this).find("i").removeClass("fa-sort-desc").addClass("fa-sort-asc");
					window.location=document.URL.substring(0,lastSlash)+  "/listuser?col=na&o=0";
				}
					
			}else if(tagId == "idThEm"){// account manager
				initLoader($);
				if($(this).find("i").length == 0){
					window.location=document.URL.substring(0,lastSlash)+  "/listuser?col=em&o=0";
				}else if($(this).find("i").attr("class").indexOf("fa-sort-asc") > -1){
					$(this).find("i").removeClass("fa-sort-asc").addClass("fa-sort-desc");
					window.location=document.URL.substring(0,lastSlash)+  "/listuser?col=em&o=1";
				}
				else {
					$(this).find("i").removeClass("fa-sort-desc").addClass("fa-sort-asc");
					window.location=document.URL.substring(0,lastSlash)+  "/listuser?col=em&o=0";
				}
			}
		});
		$("#idDropdownUser").addClass("active");
		$("#idDropdownManageUser").addClass("active");
		var lastSlash = document.URL.lastIndexOf('/');
	    var items = $("table tbody tr");
	    var pageNumber ="${pageNumber}";
		var totalRecords = "${totalRecords}";
		var recordsPerPage =  "${recordsPerPage}";
		if(parseInt(totalRecords) < parseInt(recordsPerPage))
	    {
		$(".pagination-page").attr("style","display: none;");
	    }
	  
	    $(".pagination-page").pagination({
	    	
	        items: totalRecords,
	        itemsOnPage: recordsPerPage,
	        cssStyle: "light-theme",
	        currentPage: pageNumber,
	        selectOnClick: false,
	        hrefTextPrefix : document.URL.substring(0,lastSlash)+'/listuser?page=',
	        onPageClick: function(pageNumber) { 
	            
	            var showFrom = perPage * (pageNumber - 1);
	            var showTo = showFrom + perPage;
	            items.hide() 
	                 .slice(showFrom, showTo).show();
	        }
	    });
		
	});


</script>