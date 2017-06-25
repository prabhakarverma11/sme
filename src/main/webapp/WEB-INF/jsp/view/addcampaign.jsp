<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<div class="row breadcrumb">
	<a class="breadcrumb-item" href="${pageContext.request.contextPath}/listcampaign">
				Advertiser List</a> >> <span class="active">Add Advertiser</span>
</div>
<div class="auto-1000">
	<div class="main-container">
		<div class="row">
			<div class="col-md-12 text-center">
				<div class="text-center">
					<c:if test="${not empty msg}">
						<div class="alert alert-danger fade in text-center">
							<a href="#" class="close" data-dismiss="alert">&times;</a>
							<c:out value="${msg}" />
						</div>
					</c:if>
				</div>
			</div>
		</div>

		<form:form class="form-horizontal" onsubmit="return validateForm();"
			role="form" method="post" action="savecampaign"
			modelAttribute="campaignDo" id="campaignform">

			<form:input type="hidden" id="idLocationInclude"
				path="locationInclude" />

			<form:input type="hidden" id="idLocationExclude"
				path="locationExclude" />



			<div class="container">

				<sec:authorize access="hasRole('ROLE_ADMIN')">

					<div class="row">
						<div class="form-group">
							<label for="client" class="col-lg-2 control-label">Account Manager<span
								style="color: red;">*</span>
							</label>
							<div class="col-lg-6">

								<form:select path="" class="form-control" name="clientId"
									id="creativeId" required="required">
									<option value="" selected="selected">Select</option>
									<form:options items="${userDos}" itemValue="id"
										itemLabel="name" />
								</form:select>
							</div>
						</div>
					</div>
				</sec:authorize>

				<div class="row">
					<div class="form-group">
						<label for="advertiser" class="col-lg-2 control-label">Advertiser<span
							style="color: red;">*</span>
						</label>
						<div class="col-lg-6">
							<form:input type="text" class="form-control" id="idName"
								placeholder="" path="name" required="required" maxlength="125" />

						</div>
					</div>
				</div>

				<div class="row">
					<div class="form-group">
						<label for="location" class="col-lg-2 control-label">Include
							Locations<span style="color: red;">*</span>
						</label>

						<div class="col-lg-6" id="rdButtonInclude">
							<label class="col-sm-2 radio-inline">
								<form:radiobutton 
									id="idIncludeAllLocations" path="locationIncludeCriteria"
									 value="1" checked='checked' /> All</label>
							<label class="col-sm-2 radio-inline">
								<form:radiobutton
									id="idIncludeIndia" path="locationIncludeCriteria"
									 value="2" /> India</label>
							<label class="col-sm-3 radio-inline">
								<form:radiobutton
									id="idLetMeChooseInclude" path="locationIncludeCriteria"
									 value="3" /> Let me choose</label>
							<label class="col-sm-4 radio-inline">
								<form:radiobutton
									id="idIncludeRadiusTargeting" path="locationIncludeCriteria"
									 value="4" /> Radius Targeting</label>
						</div>
					</div>
				</div>
				<!-- 				clear: both;border-style:none solid none solid; border-width:1px -->
				<div class="row" id="divLetMeChooseInclude">
					<div class="form-group" style="float: center;">

						<div id="suggestboxInclude"
							class="col-lg-6 col-lg-offset-2 adsInclude"
							style="display: none;">

							<div class="input-group" style="padding: 0px;">
								<span class="input-group-addon"><i
									class="glyphicon glyphicon-search"
									style="width: 15px; height: 14px;"></i></span>
								<form:input type="text" class="form-control searchBoxInclude"
									id="searchBoxInclude" path="" placeholder="Search Locations" />
								<span id="clearallInclude" style="cursor: pointer; color: blue;"
									class="input-group-addon">Clear all</span>
							</div>



							<div id="divsearchBoxInclude" class="col-lg-12"
								style="padding-top: 0px; border-style: solid solid none solid; border-width: 1px; border-color: grey; max-height: 105px; overflow: auto;">
							</div>

							<form:select path="includeLocationsList"
								id="idIncludeLocationList" class="col-lg-11 form-control"
								size="8" multiple="multiple" style="clear: both;">
								<c:forEach items="${indiaLocationList}" var="indiaLocationList">
									<option value="${indiaLocationList.key}">${indiaLocationList.value}</option>
								</c:forEach>

							</form:select>

						</div>

					</div>
				</div>


				<div class="row" id="divIncludeRadius">
					<div class="form-group" style="float: center">

						<div id="suggestboxIncludeRadius"
							class="col-lg-6 col-lg-offset-2 adsIncludeRadius"
							style="display: none; width: 50%;">


							<div class="input-group" style="padding: 0px;">
								<span class="input-group-addon"><i
									class="glyphicon glyphicon-search"
									style="width: 15px; height: 14px;"></i></span>
								<form:input type="text"
									class="form-control searchBoxIncludeRadius"
									id="searchBoxIncludeRadius" path=""
									placeholder="Search Locations" />

							</div>

							<form:select path="" id="idIncludeRadiusLocationList"
								class="col-lg-11 form-control" size="4" style="clear: both;">
								<c:forEach items="${indiaLocationList}" var="indiaLocationList">
									<option value="${indiaLocationList.key}">${indiaLocationList.value}</option>
								</c:forEach>

							</form:select>

						</div>
					</div>
				</div>


				<div class="row" id="idShowCustomLocation" style="display: none;">
					<div class="form-group">
						<div class="col-lg-11 col-lg-offset-2">
							No more locations. <span class="classClickHere"
								style="color: blue; cursor: pointer; text-decoration: underline;">Click
								here</span> to add custom location.
						</div>
					</div>
				</div>


				<div id="divIncludeCustomLocation">
					<div class="row">
						<div class="form-group">

							<div class="col-lg-2 col-lg-offset-2">
								<form:select path="" id="idCountries"
									class="col-lg-11 form-control">
									<c:forEach items="${countryList}" var="countryList">
										<option value="${countryList.key}">${countryList.value}</option>
									</c:forEach>

								</form:select>
							</div>

							<div class="col-lg-4">
								<input type="text" class="form-control" id="idStreet" path=""
									value="" placeholder="Street Address" />
							</div>

						</div>
					</div>



					<div class="row">
						<div class="form-group">

							<div class="col-lg-2 col-lg-offset-2">
								<form:input type="text" class="form-control" id="idCity" path=""
									value="" placeholder="City" />
							</div>

							<div class="col-lg-2">
								<form:input type="text" class="form-control" id="idState"
									path="" value="" placeholder="State" />
							</div>

							<div class="col-lg-2">
								<form:input type="text" maxlength="6" pattern="\d{6}" title="Please enter a valid 6 digits postal code" class="form-control" id="idPostal"
									path="" value="" placeholder="Postal Code" />
							</div>

						</div>
					</div>
				</div>

				<div class="row" id="divRadiusAndKm">
					<div class="form-group">
						<div class="col-lg-2 col-lg-offset-2">
							<form:input type="number" class="form-control" path="" min="0"
								id="idRadius" placeholder="Radius" />
						</div>
						<div class="col-lg-1">
							<form:select path="" class="form-control" id="idRadiusUnit">
								<option value="km">Km</option>
								<option value="mi">Miles</option>
							</form:select>
						</div>

					</div>

				</div>

				<div class="row" id="idSelectFromExistingLocations"
					style="display: none;">
					<div class="form-group">
						<div class="col-lg-11 col-lg-offset-2">
							<span class="classExistingLocations"
								style="color: blue; cursor: pointer; text-decoration: underline;">
								Click Here</span> to select from existing locations
						</div>
					</div>
				</div>

				<div class="row">
					<div class="form-group">
						<label for="location" class="col-lg-2 control-label">Exclude
							Locations<span style="color: red;">*</span>
						</label>

						<div class="col-lg-6" id="rdButtonExclude">
							<label class="col-sm-2 radio-inline">
								<form:radiobutton
									id="idExcludeAllLocations" path="locationExcludeCriteria"
									value="1" checked='checked' /> None</label>
							<label class="col-sm-2 radio-inline">
								<form:radiobutton
									id="idExcludeIndia" path="locationExcludeCriteria"
									value="2" /> India</label>
							<label class="col-sm-3 radio-inline">
								<form:radiobutton
									id="idLetMeChooseExclude" path="locationExcludeCriteria"
									value="3" /> Let me choose</label>
						</div>
					</div>
				</div>


				<div class="row">
					<div class="form-group" style="float: center;">

						<div id="suggestboxExclude" class="col-lg-6 col-lg-offset-2 ads"
							style="display: none;">

							<div class="input-group" style="padding: 0px;">
								<span class="input-group-addon"><i
									class="glyphicon glyphicon-search"
									style="width: 15px; height: 14px;"></i></span>
								<form:input type="text" class="form-control searchBoxExclude"
									id="searchBoxExclude" path="" placeholder="Search Locations" />
								<span id="clearallExclude" style="cursor: pointer; color: blue;"
									class="input-group-addon">Clear all</span>
							</div>



							<div id="divsearchBoxExclude" class="col-lg-12"
								style="padding-top: 0px; border-style: solid solid none solid; border-width: 1px; border-color: grey; max-height: 105px; overflow: auto;">
							</div>

							<form:select path="excludeLocationsList"
								id="idExcludeLocationList" class="col-lg-11 form-control"
								size="8" multiple="multiple" style="clear: both;">
								<c:forEach items="${indiaLocationList}" var="indiaLocationList">
									<option value="${indiaLocationList.key}">${indiaLocationList.value}</option>
								</c:forEach>

							</form:select>

						</div>

					</div>
				</div>


				<div class="row">
					<div class="form-group">
						<label for="daily_budget" class="col-lg-2 control-label">Daily
							Budget<span style="color: red;">*</span>
						</label>
						<div class="col-lg-6">
							<font color="red"></font>
							<div class="input-group">
								<span class="input-group-addon"><i class="fa fa-inr"
									style="width: 15px; height: 14px;"></i></span>
								<form:input type="number" class="form-control"
									id="idBudgetAmount" path="budgetAmount" placeholder=""
									maxlength="30" min="1" value="3" required="required" step="any" />
							</div>
						</div>
					</div>
				</div>



				<div class="row">
					<div class="form-group">
						<label for="start_Date" class="col-lg-2 control-label">Start
							Date<span style="color: red;">*</span>
						</label>
						<div class="col-lg-6">
							<font color="red"></font>
							<div class="input-group">
								 <label class="input-group-addon btn" for="idStartDate">
										<span class="fa fa-calendar"
									style="width: 15px; height: 14px;"></span>
								</label>
								<form:input type="text" class="form-control" id="idStartDate"
									placeholder="mm/dd/yyyy" path="startDateString" maxlength="30"
									required="required" />
							</div>
						</div>
					</div>
				</div>



				<div class="row">
					<div class="form-group">
						<label for="end_Date" class="col-lg-2 control-label">End
							Date<span style="color: red;">*</span>
						</label>
						<div class="col-lg-6">
							<font color="red"></font>
							<div class="input-group">
								<label class="input-group-addon btn" for="idEndDate">
									<span class="fa fa-calendar"
									style="width: 15px; height: 14px;"></span>
								</label>
								<form:input type="text" class="form-control" id="idEndDate"
									placeholder="mm/dd/yyyy" path="endDateString" maxlength="30"
									required="required" />
							</div>
						</div>
					</div>
				</div>

				<div class="row">
					<div class="form-group">
						<div class="col-lg-offset-2 col-lg-10">
							<button type="submit" id="idButton"
								class="btn btn-success btnSubmitCampaign"
								data-loading-text="Saving Campaign...">
								Save and Continue <span
									class="glyphicon glyphicon-chevron-right"></span>
							</button>
						</div>
					</div>
				</div>
			</div>
		</form:form>
	</div>
</div>
<script>
	$(document)
			.ready(
					function() {
							$("#idDropdownAdvertiser").addClass("active");
							$("#idDropdownMenuAddCampaign").addClass("active");
							
							$("#rdButtonInclude").on("change",function(){
								if($("#idIncludeIndia").is(':checked')){
									$("#idExcludeIndia").attr("disabled","disabled");
								}else{
									$("#idExcludeIndia").removeAttr("disabled");
								}
							});
							$("#rdButtonExclude").on("change",function(){
								if($("#idExcludeIndia").is(':checked')){
									$("#idIncludeIndia").attr("disabled","disabled");
								}else{
									$("#idIncludeIndia").removeAttr("disabled");
								}
							});
							
							
							if ($("#divsearchBoxInclude").find(
									"#suggestiontagInclude").length == 0) {
								$("#clearallInclude").hide();
							}
						
						    var dateFormat = "mm/dd/yy",
						      from = $( "#idStartDate" )
						        .datepicker({
						          defaultDate: "+0d",
						          changeMonth: true,
						          changeYear: true,
						          showOtherMonths: true,
						          numberOfMonths: 1,
						          minDate: new Date(),
						          selectOtherMonths: true
						        })
						        .on( "change", function() {
						          to.datepicker( "option", "minDate", getDate( this ) );
						        }),
						      to = $( "#idEndDate" ).datepicker({
						        defaultDate: "+1d",
						        changeMonth: true,
						        changeYear: true,
						        showOtherMonths: true,
						        numberOfMonths: 1,
						        minDate: new Date(),
						        selectOtherMonths: true
						      })
						      .on( "change", function() {
						        from.datepicker( "option", "maxDate", getDate( this ) );
						      });
						 
						    function getDate( element ) {
						      var date;
						      try {
						        date = $.datepicker.parseDate( dateFormat, element.value );
						      } catch( error ) {
						        date = null;
						      }
						 
						      return date;
						    }
						


						$("#divLetMeChooseInclude").attr("style","display: none;");
						$("#divIncludeRadius").attr("style","display: none;");
						$("#divIncludeCustomLocation").attr("style","display: none;");
						$("#idSelectFromExistingLocations").attr("style","display: none;");
						$("#divRadiusAndKm").attr("style","display: none;");
						$("#idCountries").val("IN");
						//Include Location Starts
						$("#idIncludeAllLocations").on('click', function() {
							$("#idLocationInclude").val("");
							$("#suggestboxInclude").hide();
							$("#divLetMeChooseInclude").attr("style","display: none;");
							$("#idShowCustomLocation").hide();
							$("#divIncludeRadius").attr("style","display: none;");
							$("#divIncludeCustomLocation").attr("style","display: none;");
							$("#idSelectFromExistingLocations").attr("style","display: none;");
							$("#divRadiusAndKm").attr("style","display: none;");
						});
						$("#idIncludeIndia").on('click', function() {
							$("#idLocationInclude").val("");
							$("#suggestboxInclude").hide();
							$("#divLetMeChooseInclude").attr("style","display: none;");
							$("#idShowCustomLocation").hide();
							$("#divIncludeRadius").attr("style","display: none;");
							$("#divIncludeCustomLocation").attr("style","display: none;");
							$("#idSelectFromExistingLocations").attr("style","display: none;");
							$("#divRadiusAndKm").attr("style","display: none;");
						});
						$("#idLetMeChooseInclude").on('click', function() {
							$("#clearallInclude").click();
							$("#idLocationInclude").val("");
							$("#divLetMeChooseInclude").show();
							$("#suggestboxInclude").show();
							$("#idShowCustomLocation").hide();
							$("#divIncludeRadius").attr("style","display: none;");
							$("#divIncludeCustomLocation").attr("style","display: none;");
							$("#idSelectFromExistingLocations").attr("style","display: none;");
							$("#divRadiusAndKm").attr("style","display: none;");
						});
						$("#idIncludeRadiusTargeting").on('click', function() {
							$("#idLocationInclude").val("");
							$("#suggestboxInclude").hide();
							$("#divLetMeChooseInclude").attr("style","display: none;");
							$("#divIncludeRadius").show();
							$("#suggestboxIncludeRadius").show();
							$("#divIncludeCustomLocation").attr("style","display: none;");
							$("#idSelectFromExistingLocations").attr("style","display: none;");
							$("#divRadiusAndKm").attr("style","display: block;");
						});

						
						if($("#idLetMeChooseInclude").is(':checked'))
						{
							var includeLocationArray = $("#idLocationInclude").val().slice(0, -1).split(",");
							
							$.each(includeLocationArray,function(index,value)
									{
								$("#suggestboxInclude").show();
								$("#divLetMeChooseInclude").show();
								var text = $("#idIncludeLocationList option[value=" + value + "]").text();
								
								
		
								var suggestion = '<span id="suggestiontagInclude" class="suggestiontagClass">'
									+ text
									+ '<i id="crossbExclude" onmouseover=\'this.style.color="red"\' onmouseout=\'this.style.color="black"\' style="cursor: pointer; padding:0px 5px;" data-id='
									+ value + '>x</i>' + '</span>';
								$("#divsearchBoxInclude").prepend(suggestion);
								if($("#divsearchBoxInclude").find("#suggestiontagInclude").length == 1){
									$("#clearallInclude").show();
								}
								$val = value;
								$("#idIncludeLocationList option[value=" + $val + "]")
										.attr("style", "display: none;");
								$("#idIncludeLocationList option[value=" + $val + "]")
								.attr("selected", "selected");	
								
								});
						}
					else if($("#idIncludeRadiusTargeting").is(':checked'))
						{

						var includeRadiusLocationArray = $("#idLocationInclude").val().slice(0, -1).split(":");

						if(includeRadiusLocationArray.length<4)
							{
							
							$("#divIncludeRadius").attr("style","display: block;");
							$("#suggestboxIncludeRadius").attr("style","display: block;");
							$("#divIncludeCustomLocation").attr("style","display: none;");
							$("#idSelectFromExistingLocations").attr("style","display: none;");
							$("#divRadiusAndKm").attr("style","display: block;");


							
							$("#idIncludeRadiusLocationList option[value=" + includeRadiusLocationArray[0] + "]").prop('selected', true);
							$("#idRadius").val(includeRadiusLocationArray[1]);
							$("#idRadiusUnit").val(includeRadiusLocationArray[2]);
							}
						else
							{
							$("#divIncludeRadius").attr("style","display: none;");
							$("#divIncludeCustomLocation").attr("style","display: block;");
							$("#idSelectFromExistingLocations").attr("style","display: block;");
							$("#divRadiusAndKm").attr("style","display: block;");

							
							$("#idCountries option[value=" + includeRadiusLocationArray[0] + "]").prop('selected', true);
							$("#idStreet").val(includeRadiusLocationArray[1]);
							$("#idCity").val(includeRadiusLocationArray[2]);
							$("#idState").val(includeRadiusLocationArray[3]);
							$("#idPostal").val(includeRadiusLocationArray[4]);
							$("#idRadius").val(includeRadiusLocationArray[5]);
							$("#idRadiusUnit").val(includeRadiusLocationArray[6]);
							}

						}	
						var excludeLocationArray = $("#idLocationExclude").val().slice(0, -1).split(",");
						if(excludeLocationArray[0]!="" && $("#idLetMeChooseExclude").is(':checked'))
						{
							$("#suggestboxExclude").show();
							$.each(excludeLocationArray,function(index,value)
								{
							
							var text = $("#idExcludeLocationList option[value=" + value + "]").text();
							
							

							var suggestion = '<span id="suggestiontagExclude" class="suggestiontagClass">'
								+ text
								+ '<i id="crossbExclude" onmouseover=\'this.style.color="red"\' onmouseout=\'this.style.color="black"\' style="cursor: pointer; padding:0px 5px;" data-id='
								+ value + '>x</i>' + '</span>';
							$("#divsearchBoxExclude").prepend(suggestion);
							if($("#divsearchBoxExclude").find("#suggestiontagExclude").length == 1){
								$("#clearallExclude").show();
							}
							$val = value;
							$("#idExcludeLocationList option[value=" + $val + "]")
									.attr("style", "display: none;");
							$("#idExcludeLocationList option[value=" + $val + "]")
							.attr("selected", "selected");	
							
							});
						}

						$(document)
								.on(
										'click',
										'#crossbInclude',
										function() {
											$id = $(this).data('id');
											$("#idExcludeLocationList option[value="
													+ $id+ "]")
											.removeAttr("style");
											
											$(
													"#idIncludeLocationList option[value="
															+ $id + "]")
													.removeAttr("style");
											$(
													"#idIncludeLocationList option[value="
															+ $id + "]")
													.removeAttr("selected");
											suggestionTag = $(this).closest(
													'#suggestiontagInclude');
											suggestionTag.remove();
											if ($("#divsearchBoxInclude").find(
													"#suggestiontagInclude").length == 0) {
												$("#clearallInclude").hide();
											}
											$("#idLocationInclude").val(
													$("#idLocationInclude")
															.val().replace(
																	$id + "\,",
																	""));
										});

						$(".classClickHere").on('click',function(){

							$("#divIncludeRadius").attr("style","display: none;");
							$("#idShowCustomLocation").attr("style","display: none;");
							$("#divIncludeCustomLocation").attr("style","display: block;");
							$("#idSelectFromExistingLocations").attr("style","display: block;");
							});


						$(".classExistingLocations").on('click',function(){

							$("#divIncludeRadius").attr("style","display: block;");
							$("#suggestboxIncludeRadius").attr("style","display: block;");
							$("#searchBoxIncludeRadius").val("");
							$("#idIncludeRadiusLocationList").show();
							$('#idIncludeRadiusLocationList option').each(function(){$(this).attr("style","display: block;");});
							$("#idShowCustomLocation").attr("style","display: none;");
							$("#divIncludeCustomLocation").attr("style","display: none;");
							$("#idSelectFromExistingLocations").attr("style","display: none;");
							});
						
						$("#clearallInclude")
								.on(
										'click',
										function() {
											suggestionBoxes = $(this).parents(
													"#suggestboxInclude").find(
													"#divsearchBoxInclude")
													.find("span");
											suggestionBoxes
													.each(function() {
														$id = $(this)
																.find(
																		"#crossbInclude")
																.data('id');
														$("#idExcludeLocationList option[value="
																+ $id+ "]")
														.removeAttr("style");
														
														$(
																"#idIncludeLocationList option[value="
																		+ $id
																		+ "]")
																.removeAttr(
																		"style");
														$(
																"#idIncludeLocationList option[value="
																		+ $id
																		+ "]")
																.removeAttr(
																		"selected");
														$(this).remove();
														if ($(
																"#divsearchBoxInclude")
																.find(
																		"#suggestiontagInclude").length == 0) {
															$(
																	"#clearallInclude")
																	.hide();
															return;
														}
														$("#idLocationInclude")
																.val("");
													});
											//						$("#idIncludeLocationList").scrollTop(0);
										});
						$("#idIncludeLocationList")
								.on(
										"change",
										function() {

											var values = $(
													"#idIncludeLocationList option:selected")
													.toArray();
											for ( var a in values) {
												$text = values[a].text;
												$val = values[a].value;
												
												$("#idExcludeLocationList option[value="
																+ $val+ "]")
														.attr("style","display:none;");
												
												
												
												
												
												var suggestion = '<span id="suggestiontagInclude" class="suggestiontagClass">'
														+ $text
														+ '<i id="crossbInclude" onmouseover=\'this.style.color="red"\' onmouseout=\'this.style.color="black"\' style="cursor: pointer; padding:0px 5px;" data-id='
													+ $val + '>x</i>'
														+ '</span>';
												$("#divsearchBoxInclude")
														.prepend(suggestion);
												if ($("#divsearchBoxInclude")
														.find(
																"#suggestiontagInclude").length == 1) {
													$("#clearallInclude")
															.show();
												}
												$("#idLocationInclude").val(
														$("#idLocationInclude")
																.val()
																+ $val + ",");
												$val = values[a].value;
												$(
														"#idIncludeLocationList option[value="
																+ $val + "]")
														.attr("style",
																"display: none;");
												$(
														"#idIncludeLocationList option[value="
																+ $val + "]")
														.attr("selected",
																"selected");
											}
										});
						$("#searchBoxInclude")
								.each(
										function() {
											var textBox = $(this);
											var option = $('#suggestboxInclude')
													.find('option');
											textBox
													.on(
															'keyup change',
															function() {
																if (textBox
																		.val() == '') {
																	option
																			.each(function() {
																				if ($(this)
																						.attr("selected") == "selected") {
																				} else {
																					if($("#divsearchBoxExclude").find("span i[data-id='"+$(this).val()+"']").length == 0){
																						var O = $(this);
																						$(this).removeAttr("style");
																					}
																				}
																			});
																	return;
																}
																// case when search box is not empty
																option
																		.each(function() {
																			if ($(this)
																					.attr("selected") == "selected") {
																			} else {
																				var O = $(this);
																				var i = $(
																						this)
																						.text()
																						.toLowerCase()
																						.indexOf(
																								textBox.val()
																										.toLowerCase());

																				// There is a match
																				if (i >= 0) {
																					if($("#divsearchBoxExclude").find("span i[data-id='"+$(this).val()+"']").length == 0){
																						$(this).removeAttr("style");
																					}
																				}
																				// No match
																				else {
																					$(this).attr("style","display: none;");
																				}
																			}
																		});
															});
										});

						//Include location ends

						//Exclude Location Starts
						$("#idExcludeAllLocations").on('click', function() {
							$("#suggestboxExclude").hide();
						});
						$("#idExcludeIndia").on('click', function() {
							$("#suggestboxExclude").hide();
						});
						$("#idLetMeChooseExclude").on('click', function() {
							$("#suggestboxExclude").show();
						});

						$(document)
								.on(
										'click',
										'#crossbExclude',
										function() {
											$id = $(this).data('id');
											$("#idIncludeLocationList option[value="
													+ $id+ "]")
											.removeAttr("style");
											$(
													"#idExcludeLocationList option[value="
															+ $id + "]")
													.removeAttr("style");
											$(
													"#idExcludeLocationList option[value="
															+ $id + "]")
													.removeAttr("selected");
											suggestionTag = $(this).closest(
													'#suggestiontagExclude');
											suggestionTag.remove();
											if ($("#divsearchBoxExclude").find(
													"#suggestiontagExclude").length == 0) {
												$("#clearallExclude").hide();
											}
											$("#idLocationExclude").val(
													$("#idLocationExclude")
															.val().replace(
																	$id + "\,",
																	""));
										});
						$("#clearallExclude")
								.on(
										'click',
										function() {
											suggestionBoxes = $(this).parents(
													"#suggestboxExclude").find(
													"#divsearchBoxExclude")
													.find("span");
											suggestionBoxes
													.each(function() {
														$id = $(this)
																.find(
																		"#crossbExclude")
																.data('id');
														$("#idIncludeLocationList option[value="
																+ $id+ "]")
														.removeAttr("style");
														$(
																"#idExcludeLocationList option[value="
																		+ $id
																		+ "]")
																.removeAttr(
																		"style");
														$(
																"#idExcludeLocationList option[value="
																		+ $id
																		+ "]")
																.removeAttr(
																		"selected");
														$(this).remove();
														if ($(
																"#divsearchBoxExclude")
																.find(
																		"#suggestiontagExclude").length == 0) {
															$(
																	"#clearallExclude")
																	.hide();
															return;
														}
														$("#idLocationExclude")
																.val("");
													});
											//						$("#idExcludeLocationList").scrollTop(0);
										});
						$("#idExcludeLocationList")
								.on(
										"change",
										function() {

											var values = $(
													"#idExcludeLocationList option:selected")
													.toArray();
											for ( var a in values) {
												$text = values[a].text;
												$val = values[a].value;
												$("#idIncludeLocationList option[value="
														+ $val+ "]")
												.attr("style","display:none;");
												
												
												var suggestion = '<span id="suggestiontagExclude" class="suggestiontagClass">'
														+ $text
														+ '<i id="crossbExclude" onmouseover=\'this.style.color="red"\' onmouseout=\'this.style.color="black"\' style="cursor: pointer; padding:0px 5px;" data-id='
													+ $val + '>x</i>'
														+ '</span>';
												$("#divsearchBoxExclude")
														.prepend(suggestion);
												if ($("#divsearchBoxExclude")
														.find(
																"#suggestiontagExclude").length == 1) {
													$("#clearallExclude")
															.show();
												}
												$("#idLocationExclude").val(
														$("#idLocationExclude")
																.val()
																+ $val + ",");
												$val = values[a].value;
												$(
														"#idExcludeLocationList option[value="
																+ $val + "]")
														.attr("style",
																"display: none;");
												$(
														"#idExcludeLocationList option[value="
																+ $val + "]")
														.attr("selected",
																"selected");
											}
										});
						$("#searchBoxExclude")
								.each(
										function() {
											var textBox = $(this);
											var option = $('#suggestboxExclude')
													.find('option');
											textBox
													.on('keyup change',
															function() {
																if (textBox
																		.val() == '') {
																	option.each(function() {
																				if ($(this).attr("selected") == "selected") {
																				} else {
																					if($("#divsearchBoxInclude").find("span i[data-id='"+$(this).val()+"']").length == 0){
																						var O = $(this);
																						$(this).removeAttr("style");
																					}
																				}
																			});
																	return;
																}
																// case when search box is not empty
																option.each(function() {
																			if ($(this)
																					.attr("selected") == "selected") {
																			} else {
																				var O = $(this);
																				var i = $(this)
																						.text()
																						.toLowerCase()
																						.indexOf(textBox
																								.val()
																								.toLowerCase());

																				// There is a match
																				if (i >= 0) {
																					if($("#divsearchBoxInclude").find("span i[data-id='"+$(this).val()+"']").length == 0){
																						$(this).removeAttr("style");
																					}
																				}
																				// No match
																				else {
																					$(this).attr("style","display: none;");
																				}
																			}
																		});
															});
										});

						//Exclude location ends

						//IncludeRadius Targeting starts

								
				
				$(".searchBoxIncludeRadius").each(
						function() {
							
							var textBox = $(this);
							var option = textBox.closest('.adsIncludeRadius').find('option');
							textBox.on('keyup keydown', function() {
								if (textBox.val() == '') {
									
									option.each(function() {
										
										var O = $(this);
										$(this).removeAttr("style");
										
									});
									return;
								}
								//case when search box is not empty
								
								option.each(function() {
									
									var O = $(this);
									var i = $(this).text().toLowerCase().indexOf(
											textBox.val().toLowerCase());

									//There is a match
									if (i >= 0) {
										$(this).attr("style","display: block;");
									}
									//No match
									else {
										$(this).attr("style","display: none;");
									}
								});
								var hiddenOption = $('.adsIncludeRadius option[style="display: block;"]').toArray();
								if(hiddenOption.length==0)
									{
										$("#idIncludeRadiusLocationList").hide();
										$("#idShowCustomLocation").attr("style","display: block;");
									}
								else
									{
									$("#idIncludeRadiusLocationList").show();
									$("#idShowCustomLocation").attr("style","display: none;");
									
									}
							});
						});

						

					$("#idButton").on('click',function()
							{
						
						if($("#idIncludeRadiusTargeting").is(':checked'))
							{
							if($("#divIncludeCustomLocation:visible").length==1)
								{
									$("#idLocationInclude").val($("#idCountries").val()+":"
											+$("#idStreet").val()+":"
											+$("#idCity").val()+":"
											+$("#idState").val()+":"
											+$("#idPostal").val()+":"
											+$("#idRadius").val()+":"
											+$("#idRadiusUnit").val()+":"
											);
								}
							else{
								
									$("#idLocationInclude").val($("#idIncludeRadiusLocationList").val()+":"
											+$("#idRadius").val()+":"
											+$("#idRadiusUnit").val()+":"
											);
								}

						}
							}
						);

					//IncludeRadius Targeting ends
						
						

					});

</script>
<script>
function validateForm()

	{
		if ($("#idIncludeRadiusTargeting").is(':checked')) {
			if ($("#idRadius").val() == "") {
				alert("Please enter radius if you need radius targeting");
				return false;
			}
			if (($("#divIncludeCustomLocation:visible").length == 0)
					&& ($("#idIncludeRadiusLocationList").val() == null)) {
				alert("Select at least one location for radius targeting");
				return false;
			}
			if (($("#divIncludeCustomLocation:visible").length == 1)
					&& ($("#idCity").val() == "")) {
				alert("City is mandatory for choosing custom location");
				return false;
			}
			if (($("#divIncludeCustomLocation:visible").length == 1)
					&& ($("#idPostal").val() == "")
					&& ($("#idState").val() == "")) {
				alert("At least one from POSTAL CODE or STATE must be mentioned");
				return false;
			}

		}
		if (($("#idIncludeRadiusTargeting").is(':checked') || $(
				"#idLetMeChooseInclude").is(':checked'))
				&& ($("#idLocationInclude").val() == "")) {
			alert("No location selected");
			return false;
		}

		if (($("#idLetMeChooseExclude").is(':checked'))
				&& ($("#idLocationExclude").val() == "")) {
			alert("No location selected");
			return false;
		}
		if($("#idIncludeIndia").is(':checked') && $("#idExcludeLocationList").find("option:not(:selected)").length==0){
			alert("Can't exclude all locations while including India");
			return false;
		}
		if($("#idExcludeIndia").is(':checked') && $("#idIncludeLocationList").find("option:not(:selected)").length==0){
			alert("Can't include all locations while excluding India");
			return false;
		}
		$("#idButton").button('loading');
		initLoader($);
		return true;
	}
</script>
