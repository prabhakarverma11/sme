<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<div class="row breadcrumb">
	<a class="breadcrumb-item" href="${pageContext.request.contextPath}/listcampaign">
			Advertiser List</a> >> <a class="breadcrumb-item" href="${pageContext.request.contextPath}/listadgroup?cid=${adgroupDo.campaignDo.id}">
			<c:out value="${adgroupDo.campaignDo.name }"></c:out> </a> >> <span class="active">Add Product</span>
</div>
<div class="auto-1000">
	<div class="main-container">
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

		<form:form class="form-horizontal" role="form" method="post"
			action="saveadgroup" modelAttribute="adgroupDo" id="adgroupform" onsubmit="return validateForm();">
			<form:hidden path="campaignDo.id" />

			<div class="container">

				<div class="row">
					<div class="form-group">
						<label for="product" class="col-lg-2 control-label">Campaign
						</label>
						<div class="col-lg-6">
							<form:input type="text" disabled="true" class="form-control"
								path="campaignDo.name" />

						</div>
					</div>
				</div>


				<div class="row">
					<div class="form-group">
						<label for="category" class="col-lg-2 control-label">Category<span
							style="color: red;">*</span>
						</label>
						<div class="col-lg-6">



							<div class="form-group">

								<div id="suggestboxCategory"
									class="col-lg-12 classSuggestBoxCategory">


									<div class="input-group" style="padding: 0px;">
										<span class="input-group-addon"><i
											class="glyphicon glyphicon-search"
											style="width: 15px; height: 14px;"></i></span>
										<form:input type="text"
											class="form-control classSearchBoxCategory"
											id="idSearchBoxCategory" path=""
											placeholder="Search Categories" />
										<span class="input-group-addon btn btn-small btn-default"
											id="idBtnClear">Clear</span>

									</div>

									<form:select path="categoryName" id="idCategoryName"
										class="col-lg-11 form-control" size="4"
										style="clear: both;overflow:auto;" required="required">
										<c:forEach items="${ProductCategoryDos}"
											var="ProductCategoryDo">
											<option value="${ProductCategoryDo.category}">${ProductCategoryDo.category}</option>
										</c:forEach>

									</form:select>

								</div>
							</div>
						</div>


					</div>


					<div class="row">
						<div class="form-group">
							<label for="product" class="col-lg-2 control-label">Product<span
								style="color: red;">*</span>
							</label>
							<div class="col-lg-6">
								<font color="red"></font>
								<form:input type="text" class="form-control" id="idProductName"
									placeholder="" path="productName" required="required"
									maxlength="150" />

							</div>
						</div>
					</div>




					<div class="row">
						<div class="form-group">
							<label for="business_name" class="col-lg-2 control-label">Business
								Name<span style="color: red;">*</span>
							</label>
							<div class="col-lg-6">
								<font color="red"></font> <input type="text"
									class="form-control" id="idBusinessName" placeholder=""
									name="adDos[0].businessName" required="required" maxlength="25" />
							</div>
						</div>
					</div>



					<div class="row">
						<div class="form-group">
							<label for="phone_number" class="col-lg-2 control-label">Phone
								Number<span style="color: red;">*</span>
							</label>
							<div class="col-lg-6">
								<font color="red"></font>
								<div class="input-group">
									<span class="input-group-addon"> India </span> 
									<input
										type="text" maxlength="10" pattern="([1-9])\d{9}"
										title="Enter valid 10 digits phone number (Don't use '0' in front)"
										class="form-control" id="idPhoneNumber" placeholder=""
										name="adDos[0].phoneNumber" required="required" />

								</div>
							</div>
						</div>
					</div>


					<div class="row">
						<div class="form-group">
							<label for="description_line_1" class="col-lg-2 control-label">Description
								Line 1<span style="color: red;">*</span>
							</label>
							<div class="col-lg-6">
								<font color="red"></font> <input type="text"
									class="form-control" id="idDescriptionLine1" placeholder=""
									name="adDos[0].descriptionLine1" required="required"
									maxlength="35" />
							</div>
						</div>
					</div>

					<div class="row">
						<div class="form-group">
							<label for="description_line_2" class="col-lg-2 control-label">Description
								Line 2<span style="color: red;">*</span>
							</label>
							<div class="col-lg-6">
								<font color="red"></font> <input type="text"
									class="form-control" id="idDescriptionLine2" placeholder=""
									name="adDos[0].descriptionLine2" required="required"
									maxlength="35" />
							</div>
						</div>
					</div>


					<div class="row">
						<div class="form-group">
							<label for="display_url" class="col-lg-2 control-label">Display
								URL<span style="color: red;">*</span>
							</label>
							<div class="col-lg-6">
								<font color="red"></font> <input type="url"
									pattern="http[s]?://(.+)\.(.+)"
									title="Valid URL of the form: http[s]//abc.com"
									class="form-control" id="idDisplayUrl" placeholder=""
									name="adDos[0].displayUrl" required="required" value="http://"/>

							</div>
						</div>
					</div>

					<div class="row">
						<div class="form-group">
							<div class="col-lg-offset-2 col-lg-10">
								<button id="idButtonAdgroup" type="submit" class="btn btn-success"
									data-loading-text="Saving Product...">
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
			function validateForm(){
				$("#idButtonAdgroup").button('loading');
				initLoader($);
			};
			$(document).ready(function(){


					$("#idCategoryName").val("${adgroupDo.categoryName}");
					$("#idBusinessName").val("${adgroupDo.adDos[0].businessName}");
					$("#idPhoneNumber").val("${adgroupDo.adDos[0].phoneNumber}");
					$("#idDescriptionLine1").val("${adgroupDo.adDos[0].descriptionLine1}");
					$("#idDescriptionLine2").val("${adgroupDo.adDos[0].descriptionLine2}");
					$("#idDisplayUrl").val("${adgroupDo.adDos[0].displayUrl}");
					if($("#idDisplayUrl").val()=="")
						{
						$("#idDisplayUrl").val("http://");
						}
			
//Category Searching Starts
		
				$("#idBtnClear").on('click',function(){
					$("#idSearchBoxCategory").val("");
					});

				$("#suggestboxCategory option").on('click',function(){
					$("#idSearchBoxCategory").val($(this).text());
					});
							
				
				$(".classSearchBoxCategory").each(
						function() {
							
							var textBox = $(this);
							var option = textBox.closest('.classSuggestBoxCategory').find('option');
							textBox.on('keyup keydown', function() {
								if (textBox.val() == '') {
									
									option.each(function() {
										
										var O = $(this);

										$(this).attr("style","display: block;");
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
								var hiddenOption = $('.classSuggestBoxCategory option[style="display: block;"]').toArray();
								if(hiddenOption.length==0)
									{
										$("#idCategoryName").hide();
									}
								else
									{
									$("#idCategoryName").show();
									}
							});
						});

					//Category Searching ends

				
				});

			</script>