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
}
td{
/* 	text-align: center; */
}
.pane {
    display: block;
    overflow-y: scroll;
    max-height:300px;
}
/* table{ */
/*     overflow: scroll; */
/*    text-align: center; */
/*    margin: auto; */
/*    max-height:800px; */
/* } */
hr{
margin: 5px 0px;
color: #666666;
}
</style>
<jsp:include page="addad.jsp"></jsp:include>
<jsp:include page="editad.jsp"></jsp:include>

<div class="row breadcrumb" >
	<c:if test="${showAllAdgroups eq true}">
		<a class="breadcrumb-item" href="${pageContext.request.contextPath}/listcampaign">
				Advertiser List</a> >> <a class="breadcrumb-item" href="${pageContext.request.contextPath}/listadgroup">
				Product List</a> >> <span class="active"> Product Details</span>
	</c:if>
	<c:if test="${showAllAdgroups eq false}">
		<a class="breadcrumb-item" href="${pageContext.request.contextPath}/listcampaign">
				Advertiser List</a> >> <a class="breadcrumb-item" href="${pageContext.request.contextPath}/listadgroup?cid=${campaignId}">
				<c:out value="${campaignName }"></c:out></a> >> <span class="active">
				<c:out value="${productName }"></c:out></span>
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

		<c:if test="${fn:length(adgroupDos) gt 0}">


			<div class="table-responsive ">
				<input type="hidden" id="idRowsValue" value="<c:out value='${rows}'/>">
				<table id="idAdgroupTable" class="table table-bordered table-hover table-condensed">
					<thead>
						<tr>
							<th id="idThSNO">S.No.</th>
							<c:if test="${showAllAdgroups eq true}">
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
							<th id="idThMa">Ad Messages</th>
							<th id="idThKe">Keywords</th>
						</tr>
					</thead>
					<c:set var="count" value="${(pageNumber-1)*recordsPerPage}" scope="page" />
					<c:forEach items="${adgroupDos}" var="adgroupDo">
						<c:set var="count" value="${count + 1}" scope="page" />

						<tr>
							<td style="width: 5%;"><c:out value="${count}" /></td>
							
							<c:if test="${showAllAdgroups eq true}">
								<td style="width: 10%;"><a href="adgroupdetails?adgid=${adgroupDo.id}"><c:out value="${adgroupDo.productName}" /></a></td>
								<td style="width: 10%;"><a href="listadgroup?cid=${adgroupDo.campaignDo.id}"><c:out value="${adgroupDo.campaignDo.name}" /></a></td>
								<td style="width: 19%;"><c:out value="${adgroupDo.categoryName}" /></td>
							</c:if>
							<c:if test="${showAllAdgroups eq false}">
								<td style="width: 35%;"><c:out value="${adgroupDo.categoryName}" /></td>
							</c:if>
							<td style="width: 28%;">
								
								<div class="pane" style="min-height: 35px;">
									<sec:authorize access="hasRole('ROLE_ADMIN')">
									<input id="${adgroupDo.id }" type="checkbox" class="pull-left selectall" value="" style="zoom: 1.3;margin: 5px 5px 0px 0px;">
									<select id="idSelectChangeStatus${adgroupDo.id }" class="col-sm-5" onchange="changeStatusInBulk(event,${adgroupDo.id});" style="height: 30px;" disabled="disabled">
										<option>Status</option>
										<option>Enabled</option>
										<option>Paused</option>
										<option>Disabled</option>
									</select>
									</sec:authorize>
									<a class="pull-right cursor-pointer" id="${adgroupDo.id }" data-toggle="modal" data-target="#addAdModal"><i class="glyphicon glyphicon-plus"></i>Add Ad</a>
								</div>
								
								<div class="pane">
									<c:set var="countAd" value="0" scope="page" />
									<c:forEach items="${adgroupDo.adDos}" var="adDo">
										<c:set var="countAd" value="${countAd + 1}"/>
										
										<c:if test="${adDo.status == 'Enabled'}">
											<c:set var="bgcolor" value="#d3f8d3"></c:set>
										</c:if>
										<c:if test="${adDo.status == 'Paused'}">
											<c:set var="bgcolor" value="#f2f2f2"></c:set>
										</c:if>
										<sec:authorize access="hasRole('ROLE_ADMIN')">
											<div class="pull-left" style="display: list-item;vertical-align: middle;">
												<input class="checkbox" type="checkbox" value="${adDo.id }" id="${adgroupDo.id }">
											</div>
										</sec:authorize>
										<div id="idAd" class="adClass${count }${countAd}" style="background-color: ${bgcolor};margin: 0px 5px 0px 20px;border-radius:5px;box-shadow: 1px 2px #888888;padding: 5px 10px;">
										
											<input type="hidden" id="adgroupId" value="${adgroupDo.id}"/>
											<input type="hidden" id="adId" class="classAdId" value="${adDo.id}"/>
											<input type="hidden" id="adBusinessName" class="classAdBusinessName" value="${adDo.businessName}" />
											<input type="hidden" id="adPhoneNumber" />
											<input type="hidden" id="adDescriptionLine1" />
											<input type="hidden" id="adDescriptionLine2" />
											<input type="hidden" id="adDisplayUrl" />
											<input type="hidden" id="adStatus" class="classAdStatus"  value="${adDo.status}"/>
											
											
											
											<span class="text-primary classAdPhoneNumber">Call: <c:out value="${adDo.phoneNumber}" /></span><span class="glyphicon glyphicon-earphone" style="color:blue;padding:0px 10px;"></span>
											
											<a class="cursor-pointer" data-toggle="modal" id="${count }${countAd}" data-target="#editAdModal" class="classEditAd" style="margin-right:5px;float:right;"><i class="glyphicon glyphicon-pencil"></i></a>
											<br>
											<span class="label label-success">Ad</span>
											<span class="text-success classAdDisplayUrl"><c:out value="${adDo.displayUrl}" /></span><hr>
											<span class="classAdDescription1"><c:out value="${adDo.descriptionLine1}" /></span><br>
											<span class="classAdDescription2"><c:out value="${adDo.descriptionLine2}" /></span>
										</div>
										<br>
								
									</c:forEach>
								</div>
							</td>
								
							<td style="width: 28%;">
								<div class="col-lg-10 pane">
									<c:set var="keywordNames">
											<c:out value="${adgroupDo.keywordNames}" />
									</c:set>
									<c:set var="keywordList" value="${fn:split(keywordNames,',')}" />
									<ul class="list-group" >
										<c:forEach items="${keywordList}" var="keyword">
											<li class="list-group-item" style="margin:2px 5px;padding: 3px;border: 1px solid grey;"><c:out value="${keyword}" /></li>
										</c:forEach>
									</ul>
								</div>	
								<sec:authorize access="hasRole('ROLE_ADMIN')">
									<div class="col-lg-1">
										<a href="editkeywords?adgid=<c:out value="${adgroupDo.id}" />&adgPage=<c:out value="${pageNumber}" />"><i class="glyphicon glyphicon-pencil"></i></a>
									</div>
								</sec:authorize>
							</td>
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
		<form action="adgroupdetails?col=${col }&o=${o }&page=1&rows=" role="form" id="noOfPagesForm" method="post">
			<div class="col-lg-5" id="idPaginationInfo"></div>
			<div class="col-lg-3" style="float:right;text-align:right;">
				<label for="idNoOfPages" class="control-label"><h5><code>Products Per Page: </code></h5></label>
				<select class=""  name="asdf" id="idNoOfPages">
						<option>1</option>
						<option>2</option>
						<option>5</option>
						<option>8</option>
						<option>10</option>
				</select>
			</div>
		</form>
		<div class="pagination-page col-lg-4" ></div>
		
		</div> 
	</div>
</div>

<script>
	$(".selectall").change(function(){  //"select all" change 
		var chkboxId = this.id;
	    var status = this.checked; // "select all" checked status
	    $('.checkbox[id='+chkboxId+']').each(function(){ //iterate all listed checkbox items
	        this.checked = status; //change ".checkbox" checked status
	    });
		if(this.checked){
			$("#idSelectChangeStatus"+chkboxId).removeAttr("disabled");
		}
		else{
			$("#idSelectChangeStatus"+chkboxId).attr("disabled","disabled");    		
		}
	});
	
	$('.checkbox').change(function(){//".checkbox" change 
	    //uncheck "select all", if one of the listed checkbox item is unchecked
	   	var chkboxId = this.id;
	    if(this.checked == false){ //if this item is unchecked
	        $(".selectall[id="+chkboxId+"]")[0].checked = false; //change "select all" checked status to false
	    }
	    if($('.checkbox[id='+chkboxId+']:checked').length == 0){
	    	$("#idSelectChangeStatus"+chkboxId).attr("disabled","disabled");
	    }else{
	    	$("#idSelectChangeStatus"+chkboxId).removeAttr("disabled");
	    }
	    
	    //check "select all" if all checkbox items are checked
	    if ($('.checkbox[id='+chkboxId+']:checked').length == $('.checkbox[id='+chkboxId+']').length ){ 
	        $(".selectall[id="+chkboxId+"]")[0].checked = true; //change "select all" checked status to true
	    }
	});
	function changeStatusInBulk(event,adgid){
		var checkboxes = $("#idAdgroupTable .checkbox[id="+adgid+"]:checked");
		var selectVal = event.target.value;
		var adidList = "";
		checkboxes.each(function(){
			adidList+= $(this).val()+"|";
		});
		initLoader($);
		if(selectVal=="Enabled"){
			if("${showAllAdgroups eq true}" == "false")
				window.location="${pageContext.request.contextPath}/changeadstatus?adid="+adidList+"&st=1&rows=${rows}&adgdpage=${pageNumber}&adgid="+adgid;
			else
				window.location="${pageContext.request.contextPath}/changeadstatus?adid="+adidList+"&st=1&rows=${rows}&adgdpage=${pageNumber}&showall=1";
		}
		else if(selectVal=="Paused"){
			if("${showAllAdgroups eq true}" == "false")
				window.location="${pageContext.request.contextPath}/changeadstatus?adid="+adidList+"&st=2&rows=${rows}&adgdpage=${pageNumber}&adgid="+adgid;
			else
				window.location="${pageContext.request.contextPath}/changeadstatus?adid="+adidList+"&st=2&rows=${rows}&adgdpage=${pageNumber}&showall=1";
		}
		else if(selectVal=="Disabled"){
			if("${showAllAdgroups eq true}" == "false")
				window.location="${pageContext.request.contextPath}/changeadstatus?adid="+adidList+"&st=3&rows=${rows}&adgdpage=${pageNumber}&adgid="+adgid;
			else
				window.location="${pageContext.request.contextPath}/changeadstatus?adid="+adidList+"&st=3&rows=${rows}&adgdpage=${pageNumber}&showall=1";
		}
	}

$(document).ready(function(){
	$("#idDropdownAdvertiser").addClass("active");
	$("#idDropdownMenuProductDetails").addClass("active");
	
	$("#idAdgroupTable").on("click","th",function(){
		var rowsVal = $("#idRowsValue").val();
		var tagId = $(this).attr("id");
		if(tagId == "idThAd"){//advertiser name
			initLoader($);
			if($(this).find("i").length == 0){
				if("${showAllAdgroups eq true}" == "true"){
					window.location=document.URL.substring(0,lastSlash)+  "/adgroupdetails?col=ad&o=0&rows="+rowsVal;
				}else{
					window.location=document.URL.substring(0,lastSlash)+  "/adgroupdetails?col=ad&o=0&rows="+rowsVal+"&adgid=${adgroupId}";
				}
			}else if($(this).find("i").attr("class").indexOf("fa-sort-asc") > -1){
				$(this).find("i").removeClass("fa-sort-asc").addClass("fa-sort-desc");
				if("${showAllAdgroups eq true}" == "true"){
					window.location=document.URL.substring(0,lastSlash)+  "/adgroupdetails?col=ad&o=1&rows="+rowsVal;
				}else{
					window.location=document.URL.substring(0,lastSlash)+  "/adgroupdetails?col=ad&o=1&rows="+rowsVal+"&adgid=${adgroupId}";
				}
			}
			else {
				$(this).find("i").removeClass("fa-sort-desc").addClass("fa-sort-asc");
				if("${showAllAdgroups eq true}" == "true"){
					window.location=document.URL.substring(0,lastSlash)+  "/adgroupdetails?col=ad&o=0&rows="+rowsVal;
				}else{
					window.location=document.URL.substring(0,lastSlash)+  "/adgroupdetails?col=ad&o=0&rows="+rowsVal+"&adgid=${adgroupId}";
				}
			}
				
		}else if(tagId == "idThAc"){// account manager
			initLoader($);
			if($(this).find("i").length == 0){
				if("${showAllAdgroups eq true}" == "true"){
					window.location=document.URL.substring(0,lastSlash)+  "/adgroupdetails?col=ac&o=0&rows="+rowsVal;
				}else{
					window.location=document.URL.substring(0,lastSlash)+  "/adgroupdetails?col=ac&o=0&rows="+rowsVal+"&adgid=${adgroupId}";
				}
			}else if($(this).find("i").attr("class").indexOf("fa-sort-asc") > -1){
				$(this).find("i").removeClass("fa-sort-asc").addClass("fa-sort-desc");
				if("${showAllAdgroups eq true}" == "true"){
					window.location=document.URL.substring(0,lastSlash)+  "/adgroupdetails?col=ac&o=1&rows="+rowsVal;
				}else{
					window.location=document.URL.substring(0,lastSlash)+  "/adgroupdetails?col=ac&o=1&rows="+rowsVal+"&adgid=${adgroupId}";
				}
			}
			else {
				$(this).find("i").removeClass("fa-sort-desc").addClass("fa-sort-asc");
				if("${showAllAdgroups eq true}" == "true"){
					window.location=document.URL.substring(0,lastSlash)+  "/adgroupdetails?col=ac&o=0&rows="+rowsVal;
				}else{
					window.location=document.URL.substring(0,lastSlash)+  "/adgroupdetails?col=ac&o=0&rows="+rowsVal+"&adgid=${adgroupId}";
				}
			}
		}else if(tagId == "idThPr"){// Product name
			initLoader($);
			if($(this).find("i").length == 0){
				if("${showAllAdgroups eq true}" == "true"){
					window.location=document.URL.substring(0,lastSlash)+  "/adgroupdetails?col=pr&o=0&rows="+rowsVal;
				}else{
					window.location=document.URL.substring(0,lastSlash)+  "/adgroupdetails?col=pr&o=0&rows="+rowsVal+"&adgid=${adgroupId}";
				}
			}else if($(this).find("i").attr("class").indexOf("fa-sort-asc") > -1){
				$(this).find("i").removeClass("fa-sort-asc").addClass("fa-sort-desc");
				if("${showAllAdgroups eq true}" == "true"){
					window.location=document.URL.substring(0,lastSlash)+  "/adgroupdetails?col=pr&o=1&rows="+rowsVal;
				}else{
					window.location=document.URL.substring(0,lastSlash)+  "/adgroupdetails?col=pr&o=1&rows="+rowsVal+"&adgid=${adgroupId}";
				}
			}
			else {
				$(this).find("i").removeClass("fa-sort-desc").addClass("fa-sort-asc");
				if("${showAllAdgroups eq true}" == "true"){
					window.location=document.URL.substring(0,lastSlash)+  "/adgroupdetails?col=pr&o=0&rows="+rowsVal;
				}else{
					window.location=document.URL.substring(0,lastSlash)+  "/adgroupdetails?col=pr&o=0&rows="+rowsVal+"&adgid=${adgroupId}";
				}
			}
		}else if(tagId == "idThCa"){//Catergory
			initLoader($);
			if($(this).find("i").length == 0){
				if("${showAllAdgroups eq true}" == "true"){
					window.location=document.URL.substring(0,lastSlash)+  "/adgroupdetails?col=ca&o=0&rows="+rowsVal;
				}else{
					window.location=document.URL.substring(0,lastSlash)+  "/adgroupdetails?col=ca&o=0&rows="+rowsVal+"&adgid=${adgroupId}";
				}
			}else if($(this).find("i").attr("class").indexOf("fa-sort-asc") > -1){
				$(this).find("i").removeClass("fa-sort-asc").addClass("fa-sort-desc");
				if("${showAllAdgroups eq true}" == "true"){
					window.location=document.URL.substring(0,lastSlash)+  "/adgroupdetails?col=ca&o=1&rows="+rowsVal;
				}else{
					window.location=document.URL.substring(0,lastSlash)+  "/adgroupdetails?col=ca&o=1&rows="+rowsVal+"&adgid=${adgroupId}";
				}
			}
			else{
				$(this).find("i").removeClass("fa-sort-desc").addClass("fa-sort-asc");
				if("${showAllAdgroups eq true}" == "true"){
					window.location=document.URL.substring(0,lastSlash)+  "/adgroupdetails?col=ca&o=0&rows="+rowsVal;
				}else{
					window.location=document.URL.substring(0,lastSlash)+  "/adgroupdetails?col=ca&o=0&rows="+rowsVal+"&adgid=${adgroupId}";
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
	    	if("${showAllAdgroups eq true}" == "true"){
				$(this).find("#idFormModal").attr("action","updatead?adgid="+adgroupId+"&rows=${rows}&adgdpage=${pageNumber}&showall=1");
	    	}else{
	    		$(this).find("#idFormModal").attr("action","updatead?adgid="+adgroupId+"&rows=${rows}&adgdpage=${pageNumber}");
	    	}
	    	
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
			if("${showAllAdgroups eq true}" == "true"){
				$(this).find("#idFormModal").attr("action",$(this).find("#idFormModal").attr("action")+e.relatedTarget.id+"&rows=${rows}&adgdpage=${pageNumber}&showall=1");
			}else{
				$(this).find("#idFormModal").attr("action",$(this).find("#idFormModal").attr("action")+e.relatedTarget.id+"&rows=${rows}&adgdpage=${pageNumber}");
			}
		});
	 	var items = $("table tbody tr");
	    var pageNumber ="${pageNumber}";
		var totalRecords = "${totalRecords}";
		var recordsPerPage =  "${recordsPerPage}";
		var colmn =  "${col}";
		var orderby =  "${o}";
		var adgroupId = "${adgroupId}";
		var lastSlash = document.URL.lastIndexOf('/');
		var showFrom = recordsPerPage * (pageNumber - 1);
		var showTo = showFrom + recordsPerPage*1;
		
		//Code for Pagination starts
		var url = "";
		if("${showAllAdgroups eq true}" == "true"){
			url = document.URL.substring(0,lastSlash)+'/adgroupdetails?col='+colmn+'&o='+orderby+'&rows='+recordsPerPage+'&page=';
		}else{
			url = document.URL.substring(0,lastSlash)+'/adgroupdetails?col='+colmn+'&o='+orderby+'&adgid='+adgroupId+'&rows='+recordsPerPage+'&page=';
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
			$("#noOfPagesForm").attr('action',oldActionUrl+noOfPages+"&adgid=${adgroupId}");
		}
		$("#noOfPagesForm").submit();
	});
	
});


</script>