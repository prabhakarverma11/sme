<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<div class="modal fade" id="editAdModal"
	role="dialog">
	<div class="modal-dialog">
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal">&times;</button>
				<h4 class="modal-title" align="center">Edit Ad</h4>
			</div>
			<div class="modal-body" style="overflow: hidden;">

				<form:form class="form-horizontal" modelAttribute="adDo"
					id="idFormModal" action="updatead?adgid="
					role="form" method="post">

					<form:hidden path="id" id="idEditId" />
					<input type="hidden" id="idEditStatus" name="editAdStatus">
					<div class="row ModalRow">
						<div class="form-group">
							<label for="business_name" class="col-lg-4 control-label">Business
								Name<span style="color: red;">*</span>
							</label>
							<div class="col-lg-7">
								<font color="red"></font>
								<form:input type="text" class="form-control"
									id="idEditBusinessName" placeholder="" path="businessName"
									required="required" maxlength="25" />
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
										class="form-control" id="idEditPhoneNumber" placeholder=""
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
									id="idEditDescriptionLine1" placeholder=""
									path="descriptionLine1" required="required" maxlength="35" />
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
									id="idEditDescriptionLine2" placeholder=""
									path="descriptionLine2" required="required" maxlength="35" />
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
									class="form-control" id="idEditDisplayUrl" placeholder=""
									path="displayUrl" required="required" />

							</div>
						</div>
					</div>

					<div class="row">
						<div class="form-group">
							<label for="adStatus" class="col-lg-4 control-label">Status<span
								style="color: red;">*</span>
							</label>
							<div class="col-lg-7">
								<label for="enabled" class="radio-inline"> <input
									id="enabled" type="radio" name="campaignStatus" value="Enabled">
									Enabled
								</label> <label for="paused" class="radio-inline"> <input
									id="paused" type="radio" name="campaignStatus" value="Paused">
									Paused
								</label> <label for="removed" class="radio-inline"> <input
									id="removed" type="radio" name="campaignStatus"
									value="Disabled"> Removed
								</label>
							</div>
						</div>
					</div>


					<div class="row ModalRow">
						<div class="form-group">
							<div class="col-lg-offset-4 col-lg-7">
								<button type="submit" id="idButtonEdit"
									class="btn btn-success btnUpdateAd"
									data-loading-text="Updating Ad...">Update Ad</button>
							</div>
						</div>
					</div>

				</form:form>
			</div>
		</div>

	</div>
</div>