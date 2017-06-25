<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>

<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%
	int totalRecords = (Integer) session.getAttribute("totalRecords");
	int recordsPerPage = (Integer)session.getAttribute("recordsPerPage");
	int pageNumber = (Integer)session.getAttribute("pageNumber");
	String col = (String)session.getAttribute("col");
	int o = (Integer)session.getAttribute("o");
	
%>
<style>
	th{
	 	text-align: left; 
		height: 45px;
	}
	.pane {
	    display: block;
	    overflow-y: scroll;
	    max-height:300px;
	}
	hr{
		margin: 5px 0px;
		color: #666666;
	}
</style>

<div class="row breadcrumb">
	<c:if test="${showAllAdgroups eq true}">
		<a class="breadcrumb-item" href="${pageContext.request.contextPath}/listcampaign">
				Advertiser List</a> >> <span class="active">
				Product List</span>
	</c:if>
	<c:if test="${showAllAdgroups eq false}">
		<a class="breadcrumb-item" href="${pageContext.request.contextPath}/listcampaign">
				Advertiser List</a> >> <span class="active">
				<c:out value="${campaignName }"></c:out></span>
	</c:if>
</div>
<div class="container">
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
		<c:if test="${showAllAdgroups eq false}">
			<div class="col-sm-2 pull-right">
				<a
					href="${pageContext.request.contextPath}/addadgroup?cid=${campaignId}"
					style="float: right;"><i class="glyphicon glyphicon-plus"></i>
					Add Product</a>
			</div>
		</c:if>
	</div>
	<div class="row">
		<c:if test="${fn:length(adgroupDos) gt 0}">
			<div class="table-responsive ">
				<input type="hidden" id="idRowsValue" value="<c:out value='${rows}'/>">
				<table id="idAdgroupTable" class="table table-bordered table-hover table-condensed">
					<thead>
						<tr>
							<th id="idThSN">S.No.</th>
							<sec:authorize access="hasRole('ROLE_ADMIN')">
							<th id="idThSt">
								<input class="pull-left" id="select_all" type="checkbox" value="" style="margin: 5px 3px 3px 3px;">
								<div id="idDivStatus" style="margin: 0px 0px 0px 10px;width: 50px;align-items: inherit;display: inline-block;">Status</div>
								<div id="statusCol" class="pull-right cursor-pointer" style="margin: 0px 0px 0px 0px;width: 20px;">
									<c:if test="${col eq null or col eq ''}">
										<i class="fa fa-fw fa-sort-asc" style="color:blue;" aria-hidden="true"></i>
									</c:if>
									<c:if test="${col ne null and col ne 'st'}">
										<i class="fa fa-fw fa-sort" style="color:grey;" aria-hidden="true"></i>
									</c:if>
									<c:if test="${col eq 'st' and o eq 0 }">
										<i class="fa fa-fw fa-sort-asc" style="color:blue;" aria-hidden="true"></i>
									</c:if>
									<c:if test="${col eq 'st' && o eq 1 }">
										<i class="fa fa-fw fa-sort-desc" style="color:blue;" aria-hidden="true"></i>
									</c:if>
								</div>
							</th>
							</sec:authorize>
							<sec:authorize access="hasRole('ROLE_USER')">
								<th id="idThSt" style="text-align: center;">Status
									<div id="statusCol" class="pull-right cursor-pointer">
									<c:if test="${col eq null or col eq ''}">
										<i class="fa fa-fw fa-sort-asc" style="color:blue;" aria-hidden="true"></i>
									</c:if>
									<c:if test="${col ne null and col ne 'st'}">
										<i class="fa fa-fw fa-sort" style="color:grey;" aria-hidden="true"></i>
									</c:if>
									<c:if test="${col eq 'st' and o eq 0 }">
										<i class="fa fa-fw fa-sort-asc" style="color:blue;" aria-hidden="true"></i>
									</c:if>
									<c:if test="${col eq 'st' && o eq 1 }">
										<i class="fa fa-fw fa-sort-desc" style="color:blue;" aria-hidden="true"></i>
									</c:if>
									</div>
								</th>
							</sec:authorize>
							<th id="idThAction" style="border-right-width:0px;text-align:center;">Action</th>
							<th id="idThPr" class="cursor-pointer">Product
								<c:if test="${col eq null or col eq '' or col ne 'pr'}">
									<i class="fa fa-fw fa-sort pull-right" style="color:grey;" aria-hidden="true"></i>
								</c:if>
								<c:if test="${col eq 'pr' and o eq 0 }">
									<i class="fa fa-fw fa-sort-asc pull-right" style="color:blue;" aria-hidden="true"></i>
								</c:if>
								<c:if test="${col eq 'pr' && o eq 1 }">
									<i class="fa fa-fw fa-sort-desc pull-right" style="color:blue;" aria-hidden="true"></i>
								</c:if>
							</th>
							<c:if test="${showAllAdgroups eq true}">
								<th id="idThAd" class="cursor-pointer">Advertiser
									<c:if test="${col eq null or col eq '' or col ne 'ad'}">
										<i class="fa fa-fw fa-sort pull-right" style="color:grey;" aria-hidden="true"></i>
									</c:if>
									<c:if test="${col eq 'ad' and o eq 0 }">
										<i class="fa fa-fw fa-sort-asc pull-right" style="color:blue;" aria-hidden="true"></i>
									</c:if>
									<c:if test="${col eq 'ad' && o eq 1 }">
										<i class="fa fa-fw fa-sort-desc pull-right" style="color:blue;" aria-hidden="true"></i>
									</c:if>
								</th>
								<sec:authorize access="hasRole('ROLE_ADMIN')">
									<th id="idThAc" class="cursor-pointer">Account Manager
										<c:if test="${col eq null or col eq '' or col ne 'ac'}">
											<i class="fa fa-fw fa-sort pull-right" style="color:grey;" aria-hidden="true"></i>
										</c:if>
										<c:if test="${col eq 'ac' and o eq 0 }">
											<i class="fa fa-fw fa-sort-asc pull-right" style="color:blue;" aria-hidden="true"></i>
										</c:if>
										<c:if test="${col eq 'ac' && o eq 1 }">
											<i class="fa fa-fw fa-sort-desc pull-right" style="color:blue;" aria-hidden="true"></i>
										</c:if>
									</th>
								</sec:authorize>
							</c:if>
							<th id="idThCa" class="cursor-pointer">Category
								<c:if test="${col eq null or col eq '' or col ne 'ca'}">
									<i class="fa fa-fw fa-sort pull-right" style="color:grey;" aria-hidden="true"></i>
								</c:if>
								<c:if test="${col eq 'ca' and o eq 0 }">
									<i class="fa fa-fw fa-sort-asc pull-right" style="color:blue;" aria-hidden="true"></i>
								</c:if>
								<c:if test="${col eq 'ca' && o eq 1 }">
									<i class="fa fa-fw fa-sort-desc pull-right" style="color:blue;" aria-hidden="true"></i>
								</c:if>
							</th>
						</tr>
					</thead>
					<c:set var="count" value="${(pageNumber-1)*recordsPerPage}" scope="page" />
					<c:forEach items="${adgroupDos}" var="adgroupDo">
						<c:set var="count" value="${count + 1}" scope="page" />

						<tr>
							<td style="width: 5%;"><c:out value="${count}" /></td>
							
							<td style="width:11%;text-align: center;">
								<sec:authorize access="hasRole('ROLE_ADMIN')">
									<form id="idform" action="" role="form" id="" method="post">
									<input type="hidden" id="idHiddenInputStatus" value="<c:out value="${adgroupDo.status}"/>">
									<input class="pull-left checkbox" type="checkbox" value="<c:out value="${adgroupDo.id}"/>"
									 style="margin: 9px 4px 4px 3px;">
									<div class="btn-group" style="margin: 0px 10px 0px 10px;">
										
									    <button type="button" class="btn btn-sm btn-default dropdown-toggle" data-toggle="dropdown" onmouseover="$(this).find('#idSpanCaret').removeAttr('style');" onmouseout="$(this).find('#idSpanCaret').attr('style','display: none');">
										    <c:if test="${adgroupDo.status eq 'Enabled'}">
												<span class="text-success glyphicon glyphicon-play"></span>
										    </c:if> 
										    <c:if test="${adgroupDo.status eq 'Paused'}">
										        <span class="text-danger glyphicon glyphicon-pause"></span>
										        </c:if> 
										        <span id="idSpanCaret" style="display: none;" class="caret"></span>
									    </button>
									    <ul class="dropdown-menu" role="menu">
									        <li class="cursor-pointer" onmouseover="this.style.color = 'blue';" onmouseout="this.style.color = 'black';" style="margin: 1px 3px;" onclick="changeStatus(event,${adgroupDo.id});">
									        	<span style="padding: 0px 2px;float:left;" class="text-success glyphicon glyphicon-play"></span>
									        	<span style="padding: 0px 0px;">Enabled</span>
									        </li>
									        <li class="cursor-pointer" onmouseover="this.style.color = 'blue';" onmouseout="this.style.color = 'black';" style="margin: 1px 3px;" onclick="changeStatus(event,${adgroupDo.id});">
										        <span style="padding: 0px 2px;float:left;" class="text-warning glyphicon glyphicon-pause"></span>
										        <span style="padding: 0px 0px;">Paused</span>
									        </li>
									        <li class="cursor-pointer" onmouseover="this.style.color = 'blue';" onmouseout="this.style.color = 'black';" style="margin: 1px 3px;" onclick="changeStatus(event,${adgroupDo.id});">
										        <span style="padding: 0px 2px;float:left;" class="text-danger glyphicon glyphicon-remove"></span>
										        <span style="padding: 0px 4px;">Removed</span>
									       </li>
									    </ul>
									</div>
								</form>
								</sec:authorize>
								<sec:authorize access="hasRole('ROLE_USER')">
									 <c:if test="${adgroupDo.status eq 'Enabled'}">
								        <span class="text-success glyphicon glyphicon-play"></span>
								        </c:if> 
								    <c:if test="${adgroupDo.status eq 'Paused'}">
								        <span class="text-danger glyphicon glyphicon-pause"></span>
								        </c:if>
								</sec:authorize>
							</td>
							
							<td style="width: 9%;">
								<a href="editadgroup?adgid=${adgroupDo.id}&adgpage=${pageNumber}&rows=${rows}&cid=${campId}" 
									style="margin: 0px 5px;" class="btn btn-sm btn-default editbtn">
									<span class="glyphicon glyphicon-pencil"></span>
								</a>
								<sec:authorize access="hasRole('ROLE_ADMIN')">
									<a href= "syncadgroup?adgid=${adgroupDo.id}&adgpage=${pageNumber}&rows=${rows}&cid=${campId}"
										style="margin: 0px 0px 0px 5px;" class="btn btn-sm btn-default" 
										data-loading-text="<i class='fa fa-spinner fa-spin'></i>" onclick="initLoader($);$(this).button('loading');">
										<span class="glyphicon glyphicon-refresh"></span>
									</a>
								</sec:authorize>
							</td>
							<td style="width: 20%;">
								<a href="${pageContext.request.contextPath}/adgroupdetails?adgid=${adgroupDo.id}"><c:out value="${adgroupDo.productName}" /></a>
							</td>
							
							<c:if test="${showAllAdgroups eq true}">
								<td style="width: 15%;">
									<a href="listadgroup?cid=${adgroupDo.campaignDo.id}"><c:out value="${adgroupDo.campaignDo.name }"></c:out></a>
								</td>
								<sec:authorize access="hasRole('ROLE_ADMIN')">
									<td style="width: 15%;">
										<a href="listcampaign?userId=${adgroupDo.campaignDo.userDo.id}"><c:out value="${adgroupDo.campaignDo.userDo.name }"></c:out></a>
									</td>
								</sec:authorize>
								<td style="width: 25%;"><c:out value="${adgroupDo.categoryName}" /></td>
							</c:if>
							<c:if test="${showAllAdgroups eq false}">
								<td style="width: 55%;"><c:out value="${adgroupDo.categoryName}" /></td>
							</c:if>
						</tr>

					</c:forEach>
				</table>
				
				
			</div>
		</c:if>
	
			<c:if test="${fn:length(adgroupDos) eq 0}">
			<br><br><br>
	<div class="text-danger" style="margin:0 auto;width:50%;text-align:center">
	<strong>No product added yet !!</strong></div>
						</c:if>
						 
						<div class="row" id="idPaginationDiv"  style="margin-top:10px;">
		<form action="listadgroup?col=${col }&o=${o }&page=1&rows=" role="form" id="noOfPagesForm" method="post">
			<div class="col-lg-5" id="idPaginationInfo"></div>
			<div class="col-lg-3" style="float:right;text-align:right;">
				<label for="idNoOfPages" class="control-label"><h5><code>Products Per Page: </code></h5></label>
				<select class=""  name="asdf" id="idNoOfPages">
					<option>5</option>
					<option>10</option>
					<option>20</option>
					<option>50</option>
				</select>
			</div>
		</form>
		<div class="pagination-page col-lg-4" ></div>
		
		</div> 
	</div>
</div>

<script>

$("#select_all").change(function(){  //"select all" change 
    var status = this.checked; // "select all" checked status
    $('.checkbox').each(function(){ //iterate all listed checkbox items
        this.checked = status; //change ".checkbox" checked status
    });
    $html = '<select id="idSelectChangeStatus" class="form-control" onchange="changeStatusInBulk(event);" style="width: 85px; height: 30px;">'
    		+		'<option>Status</option>'
    		+		'<option>Enabled</option>'
    		+		'<option>Paused</option>'
    		+		'<option>Removed</option>'
			+	'</select>';
    
	if(this.checked){
		$("#statusCol").attr("style","display: none;");    		
		$("#idDivStatus").html($html);
	}
	else{
		$("#statusCol").removeAttr("style");    		
		$("#idDivStatus").html("Status");
	}
});

$('.checkbox').change(function(){ //".checkbox" change 
    //uncheck "select all", if one of the listed checkbox item is unchecked
    if(this.checked == false){ //if this item is unchecked
        $("#select_all")[0].checked = false; //change "select all" checked status to false
    }
    if($('.checkbox:checked').length == 0){
    	$("#statusCol").removeAttr("style");    		
		$("#idDivStatus").html("Status");
    }else{
    	$html = '<select id="idSelectChangeStatus" class="form-control" onchange="changeStatusInBulk(event);" style="width: 85px; height: 30px;">'
    		+		'<option>Status</option>'
    		+		'<option>Enabled</option>'
    		+		'<option>Paused</option>'
    		+		'<option>Removed</option>'
			+	'</select>';
		$("#statusCol").attr("style","display: none;");    		
		$("#idDivStatus").html($html);
    }
    
    //check "select all" if all checkbox items are checked
    if ($('.checkbox:checked').length == $('.checkbox').length ){ 
        $("#select_all")[0].checked = true; //change "select all" checked status to true
    }
});

function changeStatusInBulk(event){
	var checkboxes = $("#idAdgroupTable .checkbox:checked");
	var selectVal = event.target.value;
	var adgidList = "";
	checkboxes.each(function(){
		adgidList+= $(this).val()+"|";
	});
	initLoader($);
	if(selectVal=="Enabled"){
		window.location="${pageContext.request.contextPath}/changeadgroupstatus?adgid="+adgidList+"&st=1&rows=${rows}&cid=${campaignId}&adgpage=${pageNumber}";
	}
	else if(selectVal=="Paused"){
		window.location="${pageContext.request.contextPath}/changeadgroupstatus?adgid="+adgidList+"&st=2&rows=${rows}&cid=${campaignId}&adgpage=${pageNumber}";
	}
	else if(selectVal=="Removed"){
		window.location="${pageContext.request.contextPath}/changeadgroupstatus?adgid="+adgidList+"&st=3&rows=${rows}&cid=${campaignId}&adgpage=${pageNumber}";
	}
}
function changeStatus(event,adgid){
	var selectVal = event.target.textContent;
	var spanTag = $(event.target.parentElement.parentElement.previousElementSibling.children)[0];
	if(selectVal=="Enabled"){
		initLoader($);
		spanTag.className = "text-success glyphicon glyphicon-play";
		window.location="${pageContext.request.contextPath}/changeadgroupstatus?adgid="+adgid+"&st=1&rows=${rows}&cid=${campaignId}&adgpage=${pageNumber}";
		return;
	}
	if(selectVal=="Paused"){
		initLoader($);
		spanTag.className = "text-danger glyphicon glyphicon-pause";
		window.location="${pageContext.request.contextPath}/changeadgroupstatus?adgid="+adgid+"&st=2&rows=${rows}&cid=${campaignId}&adgpage=${pageNumber}";
		return;
	}
	if(selectVal=="Removed"){
		initLoader($);
		spanTag.className = "text-danger glyphicon glyphicon-remove";
		window.location="${pageContext.request.contextPath}/changeadgroupstatus?adgid="+adgid+"&st=3&rows=${rows}&cid=${campaignId}&adgpage=${pageNumber}";
		return;
	}
}

$(document).ready(function(){
	$("#idDropdownAdvertiser").addClass("active");
	$("#idDropdownMenuManageProduct").addClass("active");
	
	$("#idAdgroupTable").on("click","#statusCol",function(){// Status
		var rowsVal = $("#idRowsValue").val();
		initLoader($);
		if($("#idThSt").find("i").length == 0){
			if("${showAllAdgroups eq true}" == "true"){
				window.location=document.URL.substring(0,lastSlash)+  "/listadgroup?col=st&o=0&rows="+rowsVal;
			}else{
				window.location=document.URL.substring(0,lastSlash)+  "/listadgroup?col=st&o=0&rows="+rowsVal+"&cid=${campaignId}";
			}
		}else if($("#idThSt").find("i").attr("class").indexOf("fa-sort-asc") > -1){
			$("#idThSt").find("i").removeClass("fa-sort-asc").addClass("fa-sort-desc");
			if("${showAllAdgroups eq true}" == "true"){
				window.location=document.URL.substring(0,lastSlash)+  "/listadgroup?col=st&o=1&rows="+rowsVal;
			}else{
				window.location=document.URL.substring(0,lastSlash)+  "/listadgroup?col=st&o=1&rows="+rowsVal+"&cid=${campaignId}";
			}
		}
		else {
			$("#idThSt").find("i").removeClass("fa-sort-desc").addClass("fa-sort-asc");
			if("${showAllAdgroups eq true}" == "true"){
				window.location=document.URL.substring(0,lastSlash)+  "/listadgroup?col=st&o=0&rows="+rowsVal;
			}else{
				window.location=document.URL.substring(0,lastSlash)+  "/listadgroup?col=st&o=0&rows="+rowsVal+"&cid=${campaignId}";
			}
		}
	});
	
	$("#idAdgroupTable").on("click","th",function(){
		var rowsVal = $("#idRowsValue").val();
		var tagId = $(this).attr("id");
		if(tagId == "idThAd"){//advertiser name
			initLoader($);
			if($(this).find("i").length == 0){
				if("${showAllAdgroups eq true}" == "true"){
					window.location=document.URL.substring(0,lastSlash)+  "/listadgroup?col=ad&o=0&rows="+rowsVal;
				}else{
					window.location=document.URL.substring(0,lastSlash)+  "/listadgroup?col=ad&o=0&rows="+rowsVal+"&cid=${campaignId}";
				}
			}else if($(this).find("i").attr("class").indexOf("fa-sort-asc") > -1){
				$(this).find("i").removeClass("fa-sort-asc").addClass("fa-sort-desc");
				if("${showAllAdgroups eq true}" == "true"){
					window.location=document.URL.substring(0,lastSlash)+  "/listadgroup?col=ad&o=1&rows="+rowsVal;
				}else{
					window.location=document.URL.substring(0,lastSlash)+  "/listadgroup?col=ad&o=1&rows="+rowsVal+"&cid=${campaignId}";
				}
			}
			else {
				$(this).find("i").removeClass("fa-sort-desc").addClass("fa-sort-asc");
				if("${showAllAdgroups eq true}" == "true"){
					window.location=document.URL.substring(0,lastSlash)+  "/listadgroup?col=ad&o=0&rows="+rowsVal;
				}else{
					window.location=document.URL.substring(0,lastSlash)+  "/listadgroup?col=ad&o=0&rows="+rowsVal+"&cid=${campaignId}";
				}
			}
				
		}else if(tagId == "idThAc"){// account manager
			initLoader($);
			if($(this).find("i").length == 0){
				if("${showAllAdgroups eq true}" == "true"){
					window.location=document.URL.substring(0,lastSlash)+  "/listadgroup?col=ac&o=0&rows="+rowsVal;
				}else{
					window.location=document.URL.substring(0,lastSlash)+  "/listadgroup?col=ac&o=0&rows="+rowsVal+"&cid=${campaignId}";
				}
			}else if($(this).find("i").attr("class").indexOf("fa-sort-asc") > -1){
				$(this).find("i").removeClass("fa-sort-asc").addClass("fa-sort-desc");
				if("${showAllAdgroups eq true}" == "true"){
					window.location=document.URL.substring(0,lastSlash)+  "/listadgroup?col=ac&o=1&rows="+rowsVal;
				}else{
					window.location=document.URL.substring(0,lastSlash)+  "/listadgroup?col=ac&o=1&rows="+rowsVal+"&cid=${campaignId}";
				}
			}
			else {
				$(this).find("i").removeClass("fa-sort-desc").addClass("fa-sort-asc");
				if("${showAllAdgroups eq true}" == "true"){
					window.location=document.URL.substring(0,lastSlash)+  "/listadgroup?col=ac&o=0&rows="+rowsVal;
				}else{
					window.location=document.URL.substring(0,lastSlash)+  "/listadgroup?col=ac&o=0&rows="+rowsVal+"&cid=${campaignId}";
				}
			}
		}else if(tagId == "idThPr"){// Product name
			initLoader($);
			if($(this).find("i").length == 0){
				if("${showAllAdgroups eq true}" == "true"){
					window.location=document.URL.substring(0,lastSlash)+  "/listadgroup?col=pr&o=0&rows="+rowsVal;
				}else{
					window.location=document.URL.substring(0,lastSlash)+  "/listadgroup?col=pr&o=0&rows="+rowsVal+"&cid=${campaignId}";
				}
			}else if($(this).find("i").attr("class").indexOf("fa-sort-asc") > -1){
				$(this).find("i").removeClass("fa-sort-asc").addClass("fa-sort-desc");
				if("${showAllAdgroups eq true}" == "true"){
					window.location=document.URL.substring(0,lastSlash)+  "/listadgroup?col=pr&o=1&rows="+rowsVal;
				}else{
					window.location=document.URL.substring(0,lastSlash)+  "/listadgroup?col=pr&o=1&rows="+rowsVal+"&cid=${campaignId}";
				}
			}
			else {
				$(this).find("i").removeClass("fa-sort-desc").addClass("fa-sort-asc");
				if("${showAllAdgroups eq true}" == "true"){
					window.location=document.URL.substring(0,lastSlash)+  "/listadgroup?col=pr&o=0&rows="+rowsVal;
				}else{
					window.location=document.URL.substring(0,lastSlash)+  "/listadgroup?col=pr&o=0&rows="+rowsVal+"&cid=${campaignId}";
				}
			}
		}else if(tagId == "idThCa"){//Catergory
			initLoader($);
			if($(this).find("i").length == 0){
				if("${showAllAdgroups eq true}" == "true"){
					window.location=document.URL.substring(0,lastSlash)+  "/listadgroup?col=ca&o=0&rows="+rowsVal;
				}else{
					window.location=document.URL.substring(0,lastSlash)+  "/listadgroup?col=ca&o=0&rows="+rowsVal+"&cid=${campaignId}";
				}
			}else if($(this).find("i").attr("class").indexOf("fa-sort-asc") > -1){
				$(this).find("i").removeClass("fa-sort-asc").addClass("fa-sort-desc");
				if("${showAllAdgroups eq true}" == "true"){
					window.location=document.URL.substring(0,lastSlash)+  "/listadgroup?col=ca&o=1&rows="+rowsVal;
				}else{
					window.location=document.URL.substring(0,lastSlash)+  "/listadgroup?col=ca&o=1&rows="+rowsVal+"&cid=${campaignId}";
				}
			}
			else{
				$(this).find("i").removeClass("fa-sort-desc").addClass("fa-sort-asc");
				if("${showAllAdgroups eq true}" == "true"){
					window.location=document.URL.substring(0,lastSlash)+  "/listadgroup?col=ca&o=0&rows="+rowsVal;
				}else{
					window.location=document.URL.substring(0,lastSlash)+  "/listadgroup?col=ca&o=0&rows="+rowsVal+"&cid=${campaignId}";
				}
			}
		}
	});
	// edit model
	
		status=0;
		function validateFormEditAd(obj){
			$(obj).find("#idButtonEditAd").button('loading');
			initLoader($);
		}
		function validateFormAddAd(obj){
			$(obj).find("#idButtonAddAd").button('loading');
			initLoader($);
		}
		$('#addAdModal').on("submit","#idFormModal",function(e){
			return validateFormAddAd(this);
		});
		$('#editAdModal').on("submit","#idFormModal",function(e){
			
			newStatus = $(this).find("input[type='radio']:checked").val()
			$(this).find("input[id='idEditStatus']").val(newStatus);
			return validateFormEditAd(this);
		});
		$('#editAdModal').on('show.bs.modal', function (e) {
			
	    	var divClass = "adClass"+e.relatedTarget.id;
	    	var adgroupId = $("."+divClass).find('#adgroupId')[0].value;
	    	
			$(this).find("#idFormModal").attr("action","updatead?adgid="+adgroupId);
	    	
	    	var businessName = $("."+divClass).find('.classAdBusinessName')[0].value;
	    	$(this).find("#idEditBusinessName").val(businessName);
	    	
	    	var phone = $("."+divClass).find('.classAdPhoneNumber')[0].innerText.split("Call: ")[1];
	    	$(this).find("#idEditPhoneNumber").val(phone);
	    	
	    	var displayUrl = $("."+divClass).find('.classAdDisplayUrl')[0].innerText;
	    	$(this).find("#idEditDisplayUrl").val(displayUrl);
	    	
	    	var desc1 = $("."+divClass).find('.classAdDescription1')[0].innerText;
	    	$(this).find("#idEditDescriptionLine1").val(desc1);
	    	
	    	var desc2 = $("."+divClass).find('.classAdDescription2')[0].innerText;
	    	$(this).find("#idEditDescriptionLine2").val(desc2);
	    	
	    	var id = $("."+divClass).find('.classAdId')[0].value;
	    	$(this).find("#idEditId").val(id);
	
	    	status = ""+$("."+divClass).find('.classAdStatus')[0].value;
	    	$(this).find("input[name='campaignStatus'][value='"+status+"']").prop('checked', true);	
	      });
	
	
	//add model
		$("#addAdModal").on("show.bs.modal",function(e){
			$(this).find("#idFormModal").attr("action",$(this).find("#idFormModal").attr("action")+e.relatedTarget.id);
		});
	 	var items = $("table tbody tr");
	    var pageNumber ="${pageNumber}";
		var totalRecords = "${totalRecords}";
		var recordsPerPage =  "${recordsPerPage}";
		var colmn =  "${col}";
		var orderby =  "${o}";
		var campaignId = "${campaignId}";
		var lastSlash = document.URL.lastIndexOf('/');
		var showFrom = recordsPerPage * (pageNumber - 1);
		var showTo = showFrom + recordsPerPage*1;
		
		//Code for Pagination starts
		var url = "";
		if("${showAllAdgroups eq true}" == "true"){
			url = document.URL.substring(0,lastSlash)+'/listadgroup?col='+colmn+'&o='+orderby+'&rows='+recordsPerPage+'&page=';
		}else{
			url = document.URL.substring(0,lastSlash)+'/listadgroup?col='+colmn+'&o='+orderby+'&cid='+campaignId+'&rows='+recordsPerPage+'&page=';
		}
		if (parseInt(totalRecords) <= 1) {
			$("#idPaginationDiv").attr("style", "display: none;");
		}
		if (parseInt(totalRecords) <= parseInt(recordsPerPage)) {
			$(".pagination-page").attr("style", "display: none;");
		}
	  
	    $(".pagination-page").pagination({
	    	
	        items: totalRecords,
	        itemsOnPage: recordsPerPage,
	        cssStyle: "light-theme",
	        currentPage: pageNumber,
	        selectOnClick: false,
	        displayedPages: 3,
	        edges: 2,
	        hrefTextPrefix : url,
	        onPageClick: function(pageNumber) { 
	            
	            items.hide() 
	                 .slice(showFrom, showTo).show();
	        }
	    });

	    //Code for Pagination ends
	    

	if(showTo <= totalRecords)
		{		
			 $("#idPaginationInfo").text("Showing "+(showFrom+1)+" - "+showTo+" of "+totalRecords+" products");
		}
	else
		{
			showTo = totalRecords;
			$("#idPaginationInfo").text("Showing "+(showFrom+1)+" - "+showTo+" of "+totalRecords+" products");
		}
	
	$("#idNoOfPages").val("${recordsPerPage}");
	$("#idNoOfPages").on('change',function(){
		initLoader($);
		var oldActionUrl="";
		var noOfPages="";
		oldActionUrl = $("#noOfPagesForm").attr('action');
		noOfPages = $(this).val();
		if("${showAllAdgroups eq true}" == "true"){
			$("#noOfPagesForm").attr('action',oldActionUrl+noOfPages);
		}else{
			$("#noOfPagesForm").attr('action',oldActionUrl+noOfPages+"&cid=${campaignId}");
		}
		$("#noOfPagesForm").submit();
	});
	
});


</script>