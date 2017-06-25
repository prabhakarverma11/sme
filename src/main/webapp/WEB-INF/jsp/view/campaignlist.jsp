<%@ page language="java" contentType="text/html; charset=ISO-8859-1" 
	pageEncoding="ISO-8859-1"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ page trimDirectiveWhitespaces="true" %>
<%@taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%
int totalRecords = (Integer) session.getAttribute("totalRecords");
int recordsPerPage = (Integer)session.getAttribute("recordsPerPage");
int pageNumber = (Integer)session.getAttribute("pageNumber");
String col = (String)session.getAttribute("col");
int o = (Integer)session.getAttribute("o");
int userId = (Integer)session.getAttribute("userId");
%>
<style>
ul.dropdown-menu{
	min-width: 110px;
}
th{
	text-align: left;
	height: 45px;
}
td{
	text-align: left;
}
.ui-autocomplete { max-height: 150px; overflow-y: scroll; overflow-x: hidden;}
</style>
<script>
	function getInfo() {
		var v;
		if(document.offersearchform)
			v = document.offersearchform.campaignName.value;
		var radioVal = $("input[name='searchOption']:checked").val();
		var url="";
		if(radioVal == "0"){
			url = "${pageContext.request.contextPath}/searchcampaignlist";
		}else{
			url = "${pageContext.request.contextPath}/searchuserlist";
		}
		
		$.ajax({
			url: url,
			data: "name="+v,
			dataType: 'text',
			success: function(res){
				var val = String(res).split(",");
				
				var campaignList = [];
				for(var i=0;i<val.length;i++){				
					var camp = val[i].split(":");
					if(camp.length>1){
						campaignList.push({value: camp[1], label: camp[1],id: camp[0]});
					}
				}
				$("#campaignName").autocomplete({
					minLength: 1,
					source: campaignList,
					autoFocus: true,
					select: function(event,ui){
						var val1 = ui.item.id;
						var val2 = ui.item.label;
						var radioVal = $("input[name='searchOption']:checked").val();
						if(radioVal == "0")
							$("#idFormSearch").attr("action","listadgroup?cid="+val1);
						else{
							userId=val1;						
							$("#idFormSearch").attr("action","listcampaign?userId="+val1+"&rows=${rows}");
						}
					}
				});
			},
			type: "GET",
			error: function(e){
				alert("An error occurred!!! excepetion: "+e);
			}
		});
	}
	$(document).on("click", "ul.ui-autocomplete li", function(e){
		$("#idFormSearch").submit();
	});
	$(function(){
		$("#campaignName").autocomplete({
			minLength: 0
		});
	});
</script>
<c:if test="${accountManager ne null }">
	<div class="row breadcrumb">
		<a class="breadcrumb-item" href="${pageContext.request.contextPath}/listcampaign">
					Advertiser List</a> >> <span class="active">
				<c:out value="${accountManager }"></c:out></span>
	</div>
</c:if>
<c:if test="${accountManager eq null }">
	<div class="row breadcrumb">
		<span class="active">Advertiser List</span>
	</div>
</c:if>
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
		<sec:authorize access="hasRole('ROLE_ADMIN')">
			<div class="col-sm-7 pull-right">
				<form:form class="form-horizontal" role="form" method="post"
 					name="offersearchform" action="listcampaign?cid=" 
					modelAttribute="OfferStatusDo" id="idFormSearch" autocomplete="off"> 
					<div class="form-group">
 						<div class="col-lg-7" id="idDivRadioButtons"> 
 							<label class="radio-inline">Search By: </label> 
 							<label 
 								class="radio-inline"> <input type="radio" 
 								name="searchOption" value="0" checked="checked">Advertiser 
 							</label> 
 							<label class="radio-inline"> <input type="radio" 
 								name="searchOption" value="1">Account Manager 
 							</label> 
 						</div> 
 						<div class="col-lg-5"> 
 							<input type="text" id="campaignName" placeholder="Search" 
 								class="form-control" name="campaignName" onkeyup="getInfo()" onkeydown="getInfo()"> 
 						</div> 
 					</div> 
 				</form:form> 
 			</div> 
		</sec:authorize>
 	</div> 
	<div class="row">
		<div class="col-sm-2 pull-right">
			<a href="${pageContext.request.contextPath}/addcampaign"
				style="float: right;"><i class="glyphicon glyphicon-plus"></i>
				Add Advertiser</a>
		</div>
	</div>
	
		<c:if test="${fn:length(campaignDos) gt 0}">
			<span id="amit"> </span>
			<div class="table-responsive">
				<input type="hidden" id="idRowsValue" value="<c:out value='${rows}'/>">
				<table id="campaignTable" class="table table-bordered table-hover table-condensed">
					<thead>
						<tr>
							<th id="idThSNo">S. No</th>
							<sec:authorize access="hasRole('ROLE_ADMIN')">
								<th id="idThStatus">
									<input id="select_all" type="checkbox" value="" class="pull-left" style="margin: 3px 0px 0px 5px;">
									<div id="idDivStatus" style="margin: 0px 0px 0px 15px;width: 50px;align-items: inherit;display: inline-block;">Status
									</div>
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
							<sec:authorize access="hasRole('ROLE_USER')">
								<th id="idThStatus" style="text-align: center;">Status
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
							<th style="text-align:center;">Actions</th>
							<th id="idThAdvName" class="cursor-pointer">Advertiser Name
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
								<c:if test="${showAllCampaigns eq true}">
									<th id="idThAccManager" class="cursor-pointer">Account Manager
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
								</c:if>
							</sec:authorize>
							<th id="idThDailyBudget" class="cursor-pointer">Daily Budget (in <i class="fa fa-inr"
								style="width: 15px; height: 14px;"></i>)
								<c:if test="${col eq null or col eq '' or col ne 'bu'}">
									<i class="fa fa-fw fa-sort pull-right" style="color:grey;" aria-hidden="true"></i>
								</c:if>
								<c:if test="${col eq 'bu' and o eq 0 }">
									<i class="fa fa-fw fa-sort-asc pull-right" style="color:blue;" aria-hidden="true"></i>
								</c:if>
								<c:if test="${col eq 'bu' && o eq 1 }">
									<i class="fa fa-fw fa-sort-desc pull-right" style="color:blue;" aria-hidden="true"></i>
								</c:if>
							</th>
							<th id="idThStartDate" class="cursor-pointer">Start Date
								<c:if test="${col eq null or col eq '' or col ne 'sd'}">
									<i class="fa fa-fw fa-sort pull-right" style="color:grey;" aria-hidden="true"></i>
								</c:if>
								<c:if test="${col eq 'sd' and o eq 0 }">
									<i class="fa fa-fw fa-sort-asc pull-right" style="color:blue;" aria-hidden="true"></i>
								</c:if>
								<c:if test="${col eq 'sd' && o eq 1 }">
									<i class="fa fa-fw fa-sort-desc pull-right" style="color:blue;" aria-hidden="true"></i>
								</c:if>
							</th>
							<th id="idThEndDate" class="cursor-pointer">End Date
								<c:if test="${col eq null or col eq '' or col ne 'en'}">
									<i class="fa fa-fw fa-sort pull-right" style="color:grey;" aria-hidden="true"></i>
								</c:if>
								<c:if test="${col eq 'en' and o eq 0 }">
									<i class="fa fa-fw fa-sort-asc pull-right" style="color:blue;" aria-hidden="true"></i>
								</c:if>
								<c:if test="${col eq 'en' && o eq 1 }">
									<i class="fa fa-fw fa-sort-desc pull-right" style="color:blue;" aria-hidden="true"></i>
								</c:if>
							</th>
						</tr>
					</thead>
					<c:set var="count" value="${(pageNumber-1)*recordsPerPage}"
						scope="page" />
					<c:forEach items="${campaignDos}" var="campaignDo">
						<c:set var="count" value="${count + 1}" scope="page" />

						<tr>
							<td style="width:5%"><c:out value="${count}"/></td>
							<td style="width:11%; text-align: center;">
							<sec:authorize access="hasRole('ROLE_ADMIN')">
								
								<form id="idform" action="" role="form" id="" method="post">
									<input type="hidden" id="idHiddenInputStatus" value="<c:out value="${campaignDo.status}"/>">
									<input class="pull-left checkbox" type="checkbox" value="${campaignDo.id }" style="margin: 10px 0px 0px 5px;">
									<div class="btn-group">
										
									    <button type="button" class="btn btn-sm btn-default dropdown-toggle" data-toggle="dropdown" onmouseover="$(this).find('#idSpanCaret').removeAttr('style');" onmouseout="$(this).find('#idSpanCaret').attr('style','display: none;');">
										    <c:if test="${campaignDo.status eq 'Enabled'}">
										        <span class="text-success glyphicon glyphicon-play"></span>
										        </c:if> 
										    <c:if test="${campaignDo.status eq 'Paused'}">
										        <span class="text-danger glyphicon glyphicon-pause"></span>
										        </c:if> 
										        <span id="idSpanCaret" style="display: none;" class="caret"></span>
									    </button>
									    <ul class="dropdown-menu" role="menu">
									        <li class="cursor-pointer" onmouseover="this.style.color = 'blue';" onmouseout="this.style.color = 'black';" 
									        	style="margin: 1px 7px;"
									        	onclick="changeStatus(event,${campaignDo.id });">
									        	<span style="padding: 0px 2px;float:left;" class="text-success glyphicon glyphicon-play"></span>
									        	<span style="padding: 0px 0px;">Enabled</span>
									        </li>
									        <li class="cursor-pointer" onmouseover="this.style.color = 'blue';" onmouseout="this.style.color = 'black';" style="margin: 1px 7px;" onclick="changeStatus(event,${campaignDo.id});">
										        <span style="padding: 0px 2px;float:left;" class="text-warning glyphicon glyphicon-pause"></span>
										        <span style="padding: 0px 0px;">Paused</span>
									        </li>
									        <li class="cursor-pointer" onmouseover="this.style.color = 'blue'" onmouseout="this.style.color = 'black';" style="margin: 1px 7px;" onclick="changeStatus(event,${campaignDo.id});">
										        <span style="padding: 0px 2px;float:left;" class="text-danger glyphicon glyphicon-remove"></span>
										        <span style="padding: 0px 4px;">Removed</span>
									       </li>
									    </ul>
									</div>
								</form>
							</sec:authorize>
							<sec:authorize access="hasRole('ROLE_USER')">
								<c:if test="${campaignDo.status eq 'Enabled'}">
									<span class="text-success glyphicon glyphicon-play"></span>
						        </c:if> 
							    <c:if test="${campaignDo.status eq 'Paused'}">
							        <span class="text-danger glyphicon glyphicon-pause"></span>
						        </c:if> 
							</sec:authorize>
							</td>
							<td style="width:9%">
								<a id="editbtn1" class="btn btn-sm btn-default" href="editcampaign?cid=<c:out value="${campaignDo.id}" />&cpage=<c:out value="${pageNumber}" />&rows=${rows}"
										style="margin: 0px 3px;"
										data-loading-text="<i class='fa fa-spinner fa-spin'></i>">
										<span class="glyphicon glyphicon-pencil"></span>
								</a>
								<sec:authorize access="hasRole('ROLE_ADMIN')">
									<a id="syncbtn1" class="btn btn-sm btn-default" href="synccampaign?cid=<c:out value="${campaignDo.id}" />&cpage=${pageNumber}&rows=${rows}"
										style="margin: 0px 3px;"
										data-loading-text="<i class='fa fa-spinner fa-spin'></i>" onclick="$(this).button('loading');initLoader($);">
										<span class="glyphicon glyphicon-refresh"></span>
									</a>
								</sec:authorize>
							</td>

							<c:if test="${showAllCampaigns eq true}">
								<td style="width:16%"><a
									href="listadgroup?cid=${campaignDo.id}">${campaignDo.name}</a></td>
								<sec:authorize access="hasRole('ROLE_ADMIN')">
									<td style="width:16%"><a
										href="listcampaign?userId=<c:out value="${campaignDo.userDo.id}" />"><c:out value="${campaignDo.createdBy}" /></a></td>
								</sec:authorize>
							</c:if>
							<c:if test="${showAllCampaigns eq false}">
								<td style="width:32%"><a
									href="listadgroup?cid=${campaignDo.id}">${campaignDo.name}</a></td>
							</c:if>
							

							<td style="width:15%"><c:out value="${campaignDo.budgetAmount}" /></td>
							<td style="width:12%"><c:out value="${campaignDo.startDateString}" /></td>
							<td style="width:12%"><c:out value="${campaignDo.endDateString}" /></td>
						</tr>

					</c:forEach>
				</table>

			</div>	
<!-- 			<div class="form-group" style="margin-left: 55px; width: 135px;"> -->
<!-- 					<select id="idSelectChangeStatus" class="form-control" onchange="changeStatusInBulk(event);"> -->
<!-- 						<option>select</option> -->
<!-- 						<option>Enabled</option> -->
<!-- 						<option>Paused</option> -->
<!-- 						<option>Removed</option> -->
<!-- 					</select> -->
<!-- 			</div> -->
		</c:if>
		<c:if test="${fn:length(campaignDos) eq 0}">
			<br>
			<br>
			<br>
			<div class="text-danger"
				style="margin: 0 auto; width: 50%; text-align: center">
				<strong>No advertiser added yet !!</strong>
			</div>

		</c:if>
		 <div class="row" id="idPaginationDiv" >
		<form action="listcampaign?col=${col }&o=${o }&page=1&userId=${userId }&rows=" role="form" id="noOfPagesForm" method="post">
			
			<div class="col-lg-5" id="idPaginationInfo"></div>
			<div class="col-lg-3" style="float:right;text-align:right;">
			<label for="noOfPages" class="control-label"><code>Advertisers Per Page: </code></label>
			<select class=""  name="asdf" id="idNoOfPages">
			<option>5</option>
			<option>10</option>
			<option>20</option>
			<option>50</option>
			<%-- <option value="${totalRecords}">All</option> --%>
			</select>
			</div>
		</form>
		<div class="pagination-page col-lg-4" ></div>
		
		</div>
		<input type="text" id="idUserRole" value="${userRoleDo.role}" style="visibility: hidden;"> 
</div>
<script>
	$("#select_all").change(function(){  //"select all" change 
	    var status = this.checked; // "select all" checked status
	    $('.checkbox').each(function(){ //iterate all listed checkbox items
	        this.checked = status; //change ".checkbox" checked status
	    });
	    $html = '<select id="idSelectChangeStatus" class="form-control" onchange="changeStatusInBulk(event);" style="width: 85px;height: 30px;">'
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
	    	$html = '<select id="idSelectChangeStatus" class="form-control" onchange="changeStatusInBulk(event);" style="width: 85px;height:30px;">'
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
		var checkboxes = $("#campaignTable .checkbox:checked");
		var selectVal = event.target.value;
		var cidList = "";
		checkboxes.each(function(){
			cidList+= $(this).val()+"|";
		});
		initLoader($);
		if(selectVal=="Enabled"){
			window.location="${pageContext.request.contextPath}/changecampaignstatus?cid="+cidList+"&st=1&rows=${rows}&userId=${userId}";
		}
		else if(selectVal=="Paused"){
			window.location="${pageContext.request.contextPath}/changecampaignstatus?cid="+cidList+"&st=2&rows=${rows}&userId=${userId}";
		}
		else if(selectVal=="Removed"){
			window.location="${pageContext.request.contextPath}/changecampaignstatus?cid="+cidList+"&st=3&rows=${rows}&userId=${userId}";
		}
	}
	function changeStatus(event,cid){
		var selectVal = event.target.textContent;
		var spanTag = $(event.target.parentElement.parentElement.previousElementSibling.children)[0];
		if(selectVal=="Enabled"){
			initLoader($);
			spanTag.className = "text-success glyphicon glyphicon-play";
			window.location="${pageContext.request.contextPath}/changecampaignstatus?cid="+cid+"&st=1&rows=${rows}&userId=${userId}";
			return;
		}
		if(selectVal=="Paused"){
			initLoader($);
			spanTag.className = "text-danger glyphicon glyphicon-pause";
			window.location="${pageContext.request.contextPath}/changecampaignstatus?cid="+cid+"&st=2&rows=${rows}&userId=${userId}";
			return;
		}
		if(selectVal=="Removed"){
			initLoader($);
			spanTag.className = "text-danger glyphicon glyphicon-remove";
			window.location="${pageContext.request.contextPath}/changecampaignstatus?cid="+cid+"&st=3&rows=${rows}&userId=${userId}";
			return;
		}
	}


$(document).ready(function(){
	getInfo();
	var a = $(".selectpicker");
	for(var i = 0; i<a.length;i++){
		var optionValue = $(a[i]).closest("form").find("#idHiddenInputStatus").val();
		$(a[i]).find("option[value='"+optionValue+"']").attr("selected","selected");
	}
	$("#idDropdownAdvertiser").addClass("active");
	$("#idDropdownMenuManageAdvertiser").addClass("active");


	var currentURL = document.URL;
	
	if("${accountManager ne null and accountManager ne ''}" == "true")
	{
		$("input[name=searchOption][value='1']").prop("checked",true);
	}
	else
	{
		$("input[name=searchOption][value='0']").prop("checked",true);
	}
	
	$("#campaignTable").on("click","#statusCol",function(){// Status
		var rowsVal = $("#idRowsValue").val();
		initLoader($);
		if($("#idThStatus").find("i").length == 0){
			window.location=document.URL.substring(0,lastSlash)+  "/listcampaign?col=st&o=0&rows="+rowsVal+"&userId=${userId}";
		}else if($("#idThStatus").find("i").attr("class").indexOf("fa-sort-asc") > -1){
			$("#idThStatus").find("i").removeClass("fa-sort-asc").addClass("fa-sort-desc");
			window.location=document.URL.substring(0,lastSlash)+  "/listcampaign?col=st&o=1&rows="+rowsVal+"&userId=${userId}";
		}
		else {
			$("#idThStatus").find("i").removeClass("fa-sort-desc").addClass("fa-sort-asc");
			window.location=document.URL.substring(0,lastSlash)+  "/listcampaign?col=st&o=0&rows="+rowsVal+"&userId=${userId}";
		}
	});
	
	$("#campaignTable").on("click","th",function(){
		var rowsVal = $("#idRowsValue").val();
		
		var tagId = $(this).attr("id");
		if(tagId == "idThAdvName"){//advertiser name
			initLoader($);
			if($(this).find("i").length == 0){
				window.location=document.URL.substring(0,lastSlash)+  "/listcampaign?col=ad&o=0&rows="+rowsVal+"&userId=${userId}";
			}else if($(this).find("i").attr("class").indexOf("fa-sort-asc") > -1){
				$(this).find("i").removeClass("fa-sort-asc").addClass("fa-sort-desc");
				window.location=document.URL.substring(0,lastSlash)+  "/listcampaign?col=ad&o=1&rows="+rowsVal+"&userId=${userId}";
			}
			else {
				$(this).find("i").removeClass("fa-sort-desc").addClass("fa-sort-asc");
				window.location=document.URL.substring(0,lastSlash)+  "/listcampaign?col=ad&o=0&rows="+rowsVal+"&userId=${userId}";
			}
				
		}else if(tagId == "idThAccManager"){// account manager
			initLoader($);
			if($(this).find("i").length == 0){
				window.location=document.URL.substring(0,lastSlash)+  "/listcampaign?col=ac&o=0&rows="+rowsVal+"&userId=${userId}";
			}else if($(this).find("i").attr("class").indexOf("fa-sort-asc") > -1){
				$(this).find("i").removeClass("fa-sort-asc").addClass("fa-sort-desc");
				window.location=document.URL.substring(0,lastSlash)+  "/listcampaign?col=ac&o=1&rows="+rowsVal+"&userId=${userId}";
			}
			else {
				$(this).find("i").removeClass("fa-sort-desc").addClass("fa-sort-asc");
				window.location=document.URL.substring(0,lastSlash)+  "/listcampaign?col=ac&o=0&rows="+rowsVal+"&userId=${userId}";
			}
		}else if(tagId == "idThDailyBudget"){//Daily Budget
			initLoader($);
			if($(this).find("i").length == 1){
				window.location=document.URL.substring(0,lastSlash)+  "/listcampaign?col=bu&o=0&rows="+rowsVal+"&userId=${userId}";
			}else if($(this).find("i:last").attr("class").indexOf("fa-sort-asc") > -1){
				$(this).find("i:last").removeClass("fa-sort-asc").addClass("fa-sort-desc");
				window.location=document.URL.substring(0,lastSlash)+  "/listcampaign?col=bu&o=1&rows="+rowsVal+"&userId=${userId}";
			}
			else{
				$(this).find("i:last").removeClass("fa-sort-desc").addClass("fa-sort-asc");
				window.location=document.URL.substring(0,lastSlash)+  "/listcampaign?col=bu&o=0&rows="+rowsVal+"&userId=${userId}";
			}
			
		}else if(tagId == "idThStartDate"){//Start Date
			initLoader($);
			if($(this).find("i").length == 0){
				window.location=document.URL.substring(0,lastSlash)+  "/listcampaign?col=sd&o=0&rows="+rowsVal+"&userId=${userId}";
			}else if($(this).find("i").attr("class").indexOf("fa-sort-asc") > -1){
				$(this).find("i").removeClass("fa-sort-asc").addClass("fa-sort-desc");
				window.location=document.URL.substring(0,lastSlash)+  "/listcampaign?col=sd&o=1&rows="+rowsVal+"&userId=${userId}";
			}
			else {
				$(this).find("i").removeClass("fa-sort-desc").addClass("fa-sort-asc");
				window.location=document.URL.substring(0,lastSlash)+  "/listcampaign?col=sd&o=0&rows="+rowsVal+"&userId=${userId}";
			}
		}else if(tagId == "idThEndDate"){//End Date
			initLoader($);
			if($(this).find("i").length == 0){
				window.location=document.URL.substring(0,lastSlash)+  "/listcampaign?col=en&o=0&rows="+rowsVal+"&userId=${userId}";
			}else if($(this).find("i").attr("class").indexOf("fa-sort-asc") > -1){
				$(this).find("i").removeClass("fa-sort-asc").addClass("fa-sort-desc");
				window.location=document.URL.substring(0,lastSlash)+  "/listcampaign?col=en&o=1&rows="+rowsVal+"&userId=${userId}";
			}
			else {
				$(this).find("i").removeClass("fa-sort-desc").addClass("fa-sort-asc");
				window.location=document.URL.substring(0,lastSlash)+  "/listcampaign?col=en&o=0&rows="+rowsVal+"&userId=${userId}";
			}
		}
	});
	var lastSlash = document.URL.lastIndexOf('/');
    var items = $("table tbody tr");
    var pageNumber ="${pageNumber}";
	var totalRecords = "${totalRecords}";
	var recordsPerPage =  "${recordsPerPage}";
	var colmn =  "${col}";
	var orderby =  "${o}";
	var userId =  "${userId}";
	var showFrom = recordsPerPage * (pageNumber - 1);
	var showTo = showFrom + recordsPerPage*1;

   
	//Code for pagination starts
	 if (parseInt(totalRecords) <= 1) {
		$("#idPaginationDiv").attr("style", "display: none;");
	}
	if (parseInt(totalRecords) <= parseInt(recordsPerPage)) {
		$(".pagination-page").attr("style", "display: none;");
	}

	
   $(".pagination-page").pagination({
   	
       items: totalRecords,
       itemsOnPage: recordsPerPage,
       ellipsePageSet: false,
       cssStyle: "light-theme",
       currentPage: pageNumber,
       selectOnClick: false,
       hrefTextPrefix : document.URL.substring(0,lastSlash)+'/listcampaign?col='+colmn+'&o='+orderby+'&userId='+userId+'&rows='+recordsPerPage+'&page=',
       displayedPages: 3,
       edges: 2,
       onPageClick: function(pageNumber) { 
           items.hide() 
                .slice(showFrom, showTo).show();
       }
   });  
	
	//Code for pagination ends
	var t ="";
	//DataTable used for sorting starts
	/* if($("#idUserRole").val()=="ROLE_ADMIN")
		{
	  t= $('#campaignTable').DataTable({
		 paging: false,
		 info: false,
		 filter: false,
		 searching: false,
		 ordering: true,
		 "columnDefs": [
		                { "visible": true,"orderable": false, "targets": [0,7,8,9] }
		              ],
	 	"order": [[ 1, 'asc' ]]
			 });
		}
	else{
		 t = $('#campaignTable').DataTable({
			 paging: false,
			 info: false,
			 filter: true,
			 searching: false,
			 ordering: true,
			 "columnDefs": [
			                { "visible": true,"orderable": false, "targets": [0,6,7] }
			              ],
		 	"order": [[ 1, 'asc' ]]
				 });
		}
	 t.on( 'order.dt search.dt', function () {
		 j=showFrom;
	        t.column(0, {search:'applied', order:'applied'}).nodes().each( function (cell) {
	            cell.innerHTML = j+1;j++;
	        } );
	    } ).draw(); */

	//DataTable used for sorting starts
	 
	 $("#idNoOfPages").val("${recordsPerPage}");


	if(showTo <= totalRecords)
		{		
			 $("#idPaginationInfo").text("Showing "+(showFrom+1)+" - "+showTo+" of "+totalRecords+" campaigns");
		}
	else
		{
			showTo = totalRecords;
			$("#idPaginationInfo").text("Showing "+(showFrom+1)+" - "+showTo+" of "+totalRecords+" campaigns");
		}
	 
	$("#idNoOfPages").on('change',function(){
		initLoader($);
		var oldActionUrl="";
		var noOfPages="";
		oldActionUrl = $("#noOfPagesForm").attr('action');
		noOfPages = $(this).val();
		$("#noOfPagesForm").attr('action',oldActionUrl+noOfPages);
		$("#noOfPagesForm").submit();
	}); 


	
	
    
});
</script>