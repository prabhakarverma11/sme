<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<style>
	input[type=text]{
		height: 30px;
	}
	input[type=number]{
		height: 30px;
	}
</style>

<div class="row breadcrumb" >
	<a href="${pageContext.request.contextPath}/listcampaign">
			Advertiser List</a> >> <a href="${pageContext.request.contextPath}/listadgroup?cid=${campaignId}">
			<c:out value="${campaignName }"></c:out></a> >> <a href="${pageContext.request.contextPath}/adgroupdetails?adgid=${adgroupId}" >
			<c:out value="${productName }"></c:out></a> >> <span class="active">keywords</span>
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

		<div class="col-sm-2 pull-right" style="padding: 0px;">
			<a id="idNewKeyword" style="float: right; cursor: pointer;"><i
				class="glyphicon glyphicon-plus"></i> Add keyword</a>
		</div>

		<c:if test="${fn:length(keywordDos) gt 0}">
			<div class="table-responsive">
				<table id="idtable"
					class="table table-bordered table-hover table-condensed">
					<thead>
						<tr>
							<th>S. No</th>
							<th>
								<input class="pull-left" id="select_all" type="checkbox" value="">
								<div id="idDivStatus" style="padding-left: 26px;height: 20px;">Status
								</div>
							</th>
							<th>Action</th>
							<th>Keyword</th>
							<th>Bid Rate</th>
							<th>Match type</th>
						</tr>
					</thead>
					<c:set var="count" value="0" />
					<c:forEach items="${keywordDos}" var="keywordDo">
						<c:set var="count" value="${count + 1}" scope="page" />


						<tr>
							<td id="idTd1" style="width: 7%;"><c:out value="${count }"></c:out></td>
							<td style="width: 14%;">
								<input class="checkbox" type="checkbox" value="${keywordDo.id }" style="margin-top: 0px;width: 33px;margin-left: 5px;margin-bottom: 7px;">
								<c:if test="${keywordDo.status == 'Enabled' }">
									<span class="text-success glyphicon glyphicon-play" style="margin-top: 0px;zoom: 1.5;"></span>
								</c:if>
								<c:if test="${keywordDo.status == 'Paused' }">
									 <span class="text-danger glyphicon glyphicon-pause" style="margin-top: 0px;zoom: 1.5;"></span>
								</c:if>
							</td>
							<td id="idTd2" style="width: 7%;text-align: center;">
								<form action="" role="form" method="post">
									<input id="keywordId" type="hidden"
										value="<c:out value='${keywordDo.id}'></c:out>">
									<input
										id="idStatus" name="keywordStatus" type="hidden"
										value="<c:out value='${keywordDo.status}'></c:out>">
									<input id="idDataToSend" type="hidden" name="keywordDetails">
									
									<a id="editbtn" class="btn btn-sm btn-default editbtn"
										style="margin: 0px 10px 0px 5px;"
										data-loading-text="<i class='fa fa-spinner fa-spin'></i>">
										<span class="glyphicon glyphicon-pencil"></span>
									</a>
								</form>
							</td>
							<td id="idTd3" style="width: 28%;"><c:out
									value="${keywordDo.text}"></c:out></td>
							<td id="idTd4" style="width: 22%;"><c:out
									value="${keywordDo.bid}"></c:out></td>
							<td id="idTd5" style="width: 22%;"><c:out
									value="${keywordDo.matchType}"></c:out></td>
						</tr>
					</c:forEach>
				</table>


			</div>

		</c:if>
		<c:if test="${fn:length(keywordDos) eq 0}">

			<div class="text-danger"
				style="margin: 0 auto; width: 50%; text-align: center">
				<strong>No keyword added yet !!</strong>
			</div>

		</c:if>
		<!-- 		<div class="pagination-page" style="float: right;"></div> -->
	</div>

</div>

<script>
	$("#select_all").change(function(){  //"select all" change 
	    var status = this.checked; // "select all" checked status
	    $('.checkbox').each(function(){ //iterate all listed checkbox items
	        this.checked = status; //change ".checkbox" checked status
	    });
	    $html = '<div class="form-group" style="margin:0px;">'
    		+	'<select id="idSelectChangeStatus" class="form-control" onchange="changeStatus(event);" style="height: 30px;width: 85px;">'
    		+		'<option>Status</option>'
    		+		'<option>Active</option>'
    		+		'<option>Paused</option>'
			+	'</select>'
			+'</div>';
// 	    $html = '<div class="form-group" style="width: 90px;margin:0px;">'
// 	    	+ '<input id="idChangeStatus" type="checkbox" checked="" data-toggle="toggle" data-size="small">'
// 			+'</div>';
	
		if(this.checked){
			$("#idDivStatus").html($html);
		}
		else{
			$("#idDivStatus").html("Status");
		}
// 		$('#idChangeStatus').bootstrapToggle();
	});

	$('.checkbox').change(function(){ //".checkbox" change 
	    //uncheck "select all", if one of the listed checkbox item is unchecked
	    if(this.checked == false){ //if this item is unchecked
	        $("#select_all")[0].checked = false; //change "select all" checked status to false
	    }
		
	    if($('.checkbox:checked').length == 0){
			$("#idDivStatus").html("Status");
	    }else{
	    	$html = '<div class="form-group" style="margin:0px;">'
	    		+	'<select id="idSelectChangeStatus" class="form-control" onchange="changeStatus(event);" style="height: 30px;width: 85px;">'
	    		+		'<option>Status</option>'
	    		+		'<option>Active</option>'
	    		+		'<option>Paused</option>'
				+	'</select>'
				+'</div>';
// 	    	$html = '<div class="form-group" style="width: 90px;margin:0px;">'
// 		    	+ '<input id="idChangeStatus" type="checkbox" checked="" data-toggle="toggle" data-size="small">'
// 				+'</div>';
			$("#idDivStatus").html($html);
	    }
	    
	    //check "select all" if all checkbox items are checked
	    if ($('.checkbox:checked').length == $('.checkbox').length ){ 
	        $("#select_all")[0].checked = true; //change "select all" checked status to true
	    }
// 	    $('#idChangeStatus').bootstrapToggle();
	});
	function changeStatus(event){
		var checkboxes = $("#idtable .checkbox:checked");
		var selectVal = event.target.value;
		var keyidList = "";
		checkboxes.each(function(){
			keyidList+= $(this).val()+"|";
		});
		initLoader($);
		if(selectVal=="Active"){
			window.location="${pageContext.request.contextPath}/changekeywordstatus?kid="+keyidList+"&st=1&adgid=${adgroupId}&adgPage=${adgPage}";
		}
		else if(selectVal=="Paused"){
			window.location="${pageContext.request.contextPath}/changekeywordstatus?kid="+keyidList+"&st=2&adgid=${adgroupId}&adgPage=${adgPage}";
		}
	}
	
	function validateForm(obj){
		$(obj).closest('form').attr('action','updatekeyword?adgid=<c:out value="${adgroupId}"/>&adgPage=<c:out value="${adgPage}"/>');
		$(obj).button('loading');
		initLoader($);
		$(obj).closest('form').submit();
	};
	$(document).on('keyup', '.has-error input', function () {
		$(this).closest("div").removeClass("has-error").addClass("has-success");
	});
	
	$(document)
			.ready(function() {
						$("#idtable")
								.on('click',
										'#editbtn',
		function() {
			
			var $this = $(this);
			var tr = $this.closest('tr');
			var spanTag = $this.find('span');
			if (spanTag[0].className
					.indexOf('pencil') > -1) {
				//to edit
				spanTag
						.removeClass('glyphicon-pencil');
				spanTag
						.addClass('glyphicon-ok');

				var inputTagBidRate = '<div class="form-group has-feedback has-success">'
						+ ' <input type="number" min="0.01" class="form-control" value="'
						+ parseFloat(tr
								.find(
										"td[id='idTd4']")
								.text())
						+ '">'
						+ '</div>';
				
						
				tr.find("td[id='idTd4']").html(
						inputTagBidRate);

				text = [ 'PHRASE', 'BROAD',
						'EXACT' ];
				alreadySelectedValue = tr.find("td[id='idTd5']").text();
				select = function() {
					var returnstring = '';
					for ( var i in text) {
						if(text[i] == alreadySelectedValue){
							returnstring += "<option value='" + text[i] + "' selected='true'>"
									+ text[i]
									+ "</option>";
						}else{
							returnstring += "<option value='" + text[i] + "'>"
									+ text[i]
									+ "</option>";
						}
					}
					return "<div class='form-group has-feedback has-success'><select class='form-control' style='height: 30px;'>"
							+ returnstring
							+ "</select></div>";
				}();
				tr.find("td[id='idTd5']").html(
						select);
			} else {
				///to save edited data
				
				var keywordTag = tr.find("td[id='idTd3'] input");
				if(keywordTag.length && !keywordTag.val()){
					var inputTag = '<div class="form-group has-feedback has-error">'
						+ ' <input type="text" class="form-control" placeholder="Enter keyword name" value="'
						+ keywordTag.val()
						+ '">'
						+ '</div>';
						keywordTag.closest("td[id='idTd3']").html(inputTag);
					return;
				}
				
				var bidRateTag = tr.find("td[id='idTd4'] input");
				if(!bidRateTag.val() || bidRateTag.val() <0.01){
					var inputTag = '<div class="form-group has-feedback has-error">'
						+ ' <input type="number" min="0.01" class="form-control" value="'
						+ parseFloat(bidRateTag.val())
						+ '">'
						+ '</div>';
					bidRateTag.closest("td[id='idTd4']").html(inputTag);
					return;
				}
				
				//case: when cell is input
				spanTag.removeClass(
								'glyphicon-ok')
						.addClass(
								'glyphicon-pencil');
				el0 = tr.find("td[id='idTd2'] input").val();
				if(keywordTag.length)
					el1 = tr.find("td[id='idTd3'] input").val();
				else
					el1 = tr.find("td[id='idTd3']").text();
				el2 = tr.find("td[id='idTd4'] input").val();
				el3 = tr.find("td[id='idTd5'] select").val();
				
				tr.find("td[id='idTd3']").text(el1);
				tr.find("td[id='idTd4']").text(el2);
				tr.find("td[id='idTd5']").text(el3);
				
				
				res = el0+ "|" + el1 + "|" + el2 + "|" + el3;
				tr.find("td[id='idTd2'] input[id='idDataToSend']").val(res);
				return validateForm(this);
			}
		});
						
		$("#idNewKeyword")
				.on(
						'click',
						function() {
							if ($("#editbtn span:first")[0].className
									.indexOf('ok') > -1) {
								alert("First close the row you are editing");
								return;
							} else {
								text = [ 'PHRASE', 'BROAD',
										'EXACT' ];
								select = function() {
									var returnstring = '';
									for ( var i in text) {
										returnstring += "<option value='" + text[i] + "'>"
												+ text[i]
												+ "</option>";
									}
									return "<div class='form-group has-feedback has-success'><select class='form-control' style='height: 30px;'>"
											+ returnstring
											+ "</select></div>";
								}();
								var inputTagKeyword = '<div class="form-group has-feedback has-success">'
										+ ' <input type="text" class="form-control" placeholder="Enter keyword name">'
										+ '</div>';
								var inputTagBidRate = '<div class="form-group has-feedback has-success">'
										+ ' <input type="number" value="0.01" min="0.01" class="form-control">'
										+ '</div>';
								$html = "<tr>"
											+ "<td id='idTd1' style='width:7%;'>x</td>"
											+ "<td style='width: 12%;'>"
											+	 "<input class='checkbox' type='checkbox' value='' style='margin-top: 0px;width: 33px;margin-left: 5px;margin-bottom: 7px;'>"
											+ 	 "<span class='text-success glyphicon glyphicon-play' style='margin-left: 2px;margin-top: 0px;zoom: 1.5;'></span>"
											+ "</td>"
											+ "<td id='idTd2' style='width:7%;'>"
												+ "<form action='' role='form' method='post'>"
												+ "<input id='keywordId' type='hidden' value=''>"
												+ "<input id='idStatus' name='keywordStatus' type='hidden' value='Enabled'>"
												+ "<input id='idDataToSend' type='hidden' name='keywordDetails' >"
												+ "<a id='editbtn' class='btn btn-sm btn-default editbtn' style='margin: 0px 10px 0px 11px;' data-loading-text='<i class=\"fa fa-spinner fa-spin\"></i>'>"
												+ "<span class='glyphicon glyphicon-ok'></span>"
												+ "</a>"
												+ "</form></td>"
											+ "<td id='idTd3' style='width:30%;'>"
												+ inputTagKeyword
											+ "</td>"
											+ "<td id='idTd4' style='width%;22'>"
												+ inputTagBidRate
											+ "</td>"
											+ "<td id='idTd5' style='width:22%;'>"
												+ select
											+ "</td>"
										+ "</tr>";
								if ($("#idtable tr").length == 1) {
									$($html).prependTo('table');
								} else {
									$($html)
											.insertBefore(
													'table > tbody > tr:first');
								}
							}
						});

		var t ="";
		//DataTable used for sorting starts
		
		 t = $('#idtable').DataTable({
			 paging: false,
			 info: false,
			 filter: false,
			 searching: false,
			 ordering: true,
			 "columnDefs": [
			                { "visible": true,"orderable": false, "targets": [0,1,2] }
			              ],
		 	"order": [[ 3, 'asc' ]]
				 });
		 t.on( 'order.dt search.dt', function () {
			 j=0;
		        t.column(0, {search:'applied', order:'applied'}).nodes().each( function (cell) {
		            cell.innerHTML = j+1;j++;
		        } );
		    } ).draw();

		//DataTable used for sorting starts
	});
</script>