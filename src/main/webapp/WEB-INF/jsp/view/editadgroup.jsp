<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<div class="row breadcrumb">
	<a class="breadcrumb-item" href="${pageContext.request.contextPath}/listcampaign">
			Advertiser List</a> >> <a class="breadcrumb-item" href="${pageContext.request.contextPath}/listadgroup?cid=${adgroupDo.campaignDo.id}">
			<c:out value="${adgroupDo.campaignDo.name }"></c:out> </a> >> <a class="breadcrumb-item" href="${pageContext.request.contextPath}/adgroupdetails?adgid=${adgroupDo.id}">
			<c:out value="${adgroupDo.productName }"></c:out> </a> >> <span class="active">Edit Product</span>
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

		<form:form class="form-horizontal" role="form" method="post" onsubmit="return validateForm();"
			action="updateadgroup?adgpage=${adgPage }&rows=${rows}&cid=${cid}" modelAttribute="adgroupDo"
			id="adgroupform">
			<form:hidden path="campaignDo.id" />
			<form:hidden path="id" />
			<form:hidden path="apiId" />
			<form:hidden path="keywordNames" />
			<form:hidden path="createdBy" />
			<form:hidden path="createdOnString" />
			<form:hidden path="thresholdKeywordAvgCpc" />

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
											placeholder="Search Categories"
											value="${adgroupDo.categoryName}" />
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
							<label for="campaignStatus" class="col-lg-2 control-label">Status<span
								style="color: red;">*</span>
							</label>
							<div class="col-lg-6">
								<label for="enabled" class="radio-inline"><form:radiobutton
										path="status" value="Enabled" /> Enabled</label> <label for="paused"
									class="radio-inline"><form:radiobutton path="status"
										name="campaignStatus" value="Paused" /> Paused</label> <label
									for="removed" class="radio-inline"><form:radiobutton
										path="status" name="campaignStatus" value="Removed" />
									Removed</label>
							</div>
						</div>
					</div>




					<div class="row">
						<div class="form-group">
							<div class="col-lg-offset-2 col-lg-10">
								<button id="idButtonEditAdgroup" type="submit"
									class="btn btn-success" data-loading-text="Updating Product...">
									Save and Update <span class="glyphicon glyphicon-chevron-right"></span>
								</button>
							</div>
						</div>
					</div>
				</div>
			</div>
		</form:form>
	</div>
</div>
<script>
			function validateForm(){
				$("#idButtonEditAdgroup").button('loading');
				initLoader($);
			};
			$(document).ready(function(){
				$("#idCategoryName").val("${adgroupDo.categoryName}");
				
				$("#suggestboxCategory option").on('click',function(){
					$("#idSearchBoxCategory").val($(this).text());
					});


				$("#idBtnClear").on('click',function(){
					$("#idSearchBoxCategory").val("");
					});
								

//Category Searching Starts

				
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