<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>


<!-- Modal -->
<div class="modal fade" id="addAdModal"
	role="dialog">
	<div class="modal-dialog">
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal">&times;</button>
				<h4 class="modal-title" align="center">Add Ad</h4>
			</div>
			<div class="modal-body" style="overflow: hidden;">

				<form:form class="form-horizontal" modelAttribute="adDo"
					id="idFormModal" action="savead?adgid="
					role="form" method="post">
					<div class="row ModalRow">
						<div class="form-group">
							<label for="business_name" class="col-lg-4 control-label">Business
								Name<span style="color: red;">*</span>
							</label>
							<div class="col-lg-7">
								<font color="red"></font>
								<form:input type="text" class="form-control" id="idBusinessName"
									placeholder="" path="businessName" required="required"
									maxlength="25" />
							</div>
						</div>
					</div>



					<div class="row ModalRow">
						<div class="form-group">
							<label for="phone_number" class="col-lg-4 control-label">Phone
								Number<span style="color: red;">*</span>
							</label>
							<div class="col-lg-7">
								<font color="red"></font>
								<div class="input-group">
									<span class="input-group-addon"> India </span>


									<form:input type="text" maxlength="10" pattern="([1-9])\d{9}"
										title="Enter valid 10 digits phone number (Don't use '0' in front)"
										class="form-control" id="idPhoneNumber" placeholder=""
										path="phoneNumber" required="required" />

								</div>
							</div>
						</div>
					</div>


					<div class="row ModalRow">
						<div class="form-group ">
							<label for="description_line_1" class="col-lg-4 control-label">Description
								Line 1<span style="color: red;">*</span>
							</label>
							<div class="col-lg-7">
								<font color="red"></font>
								<form:input type="text" class="form-control"
									id="idDescriptionLine1" placeholder="" path="descriptionLine1"
									required="required" maxlength="35" />
							</div>
						</div>
					</div>

					<div class="row ModalRow">
						<div class="form-group">
							<label for="description_line_2" class="col-lg-4 control-label">Description
								Line 2<span style="color: red;">*</span>
							</label>
							<div class="col-lg-7">
								<font color="red"></font>
								<form:input type="text" class="form-control"
									id="idDescriptionLine2" placeholder="" path="descriptionLine2"
									required="required" maxlength="35" />
							</div>
						</div>
					</div>


					<div class="row ModalRow">
						<div class="form-group">
							<label for="display_url" class="col-lg-4 control-label">Display
								URL<span style="color: red;">*</span>
							</label>
							<div class="col-lg-7">
								<font color="red"></font>
								<form:input type="url" pattern="http[s]?://(.+)\.(.+)"
									title="Valid URL of the form: http[s]//abc.com"
									class="form-control" id="idDisplayUrl" placeholder=""
									path="displayUrl" required="required" />

							</div>
						</div>
					</div>
					<div class="row ModalRow">
						<div class="form-group">
							<div class="col-lg-offset-4 col-lg-7">
								<button id="idButtonAddAd" type="submit" class="btn btn-success"
									data-loading-text="Saving Ad...">Save Ad</button>
							</div>
						</div>
					</div>

				</form:form>
			</div>
		</div>

	</div>
</div>
