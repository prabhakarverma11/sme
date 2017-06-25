<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
         pageEncoding="ISO-8859-1" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="sec"
          uri="http://www.springframework.org/security/tags" %>
<%
    int totalRecords = (Integer) session.getAttribute("totalRecords");
    int recordsPerPage = (Integer) session.getAttribute("recordsPerPage");
    int pageNumber = (Integer) session.getAttribute("pageNumber");
    String userEmail = (String) session.getAttribute("userEmail");
    String col = (String) session.getAttribute("col");
    int o = (Integer) session.getAttribute("o");
%>
<style>
    th {
        text-align: right;
    }

    tr {
        text-align: right;
    }
</style>
<div class="container">
    <!-- Modal -->
    <div class="modal fade" id="myModal" role="dialog">
        <div class="modal-dialog">
            <!-- Modal content-->
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal">&times;</button>
                    <h4 class="modal-title">Mail Recipient(s)</h4>
                </div>
                <div class="modal-body" style="overflow: hidden;">
                    <form id="idFormModal" class="form-horizontal" action="mailReport"
                          role="form" method="post">
                        <input type="hidden" name="toAddresses" id="toAddresses">
                        <div id="idDivEmail" class="row">
                            <div class="form-group">
                                <label for="email" class="col-lg-2 control-label">To: </label>
                                <div class="col-lg-8">
                                    <input class="form-control" type="email" required="required">
                                </div>
                                <div class="col-lg-1 default" id="idDivAddEmail">
                                    <a id="idAddMail" class="btn btn-small"><i
                                            class="glyphicon glyphicon-plus"></i> Add</a>
                                </div>
                            </div>
                        </div>
                        <div class="row">
                            <div class="form-group">
                                <div class="col-lg-offset-2 col-lg-8">
                                    <button type="submit" class="btn btn-success">Send</button>
                                </div>
                            </div>
                        </div>

                    </form>
                </div>
            </div>

        </div>
    </div>
    <!-- 	<div class="row alert alert-warning text-center">Campaign -->
    <!-- 		Performance Report</div> -->
    <div class="row">

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
        <div class="container">
            <form:form action="cpreport" role="form"
                       commandName="reportCriteriaDo" id="idForm">
                <form:input type="hidden" id="idCampaignInclude"
                            path="campaignsToFilter"/>
                <div id="formDiv" class="panel-group" role="tablist"
                     aria-multiselectable="true">
                    <div class="panel panel-default">

                        <div class="panel-heading" role="tab" id="headingOne">
                            <h4 class="panel-title" id="idCollapsePanel">
                                <a data-toggle="collapse" data-parent="#accordion"
                                   href="#collapseOne" aria-expanded="true" id="idCollapseOne"
                                   aria-controls="collapseOne"> <span
                                        id="idReportCriteriaTitle" aria-hidden="false"></span><strong>REPORT
                                    CRITERIA </strong>
                                </a>
                            </h4>
                        </div>
                        <div id="collapseOne" class="panel-collapse collapse in"
                             role="tabpanel" aria-labelledby="headingOne">
                            <div class="panel-body">
                                <div class="row">
                                    <div class="form-group">
                                        <label class="col-sm-2"><strong>Date Criteria</strong></label>
                                        <div class="col-sm-2">

                                            <label> <form:radiobutton path="criteria"
                                                                      name="criteria" id="criteriaRadio"
                                                                      value="daywise"/>
                                                Daywise
                                            </label> <label> <form:radiobutton path="criteria"
                                                                               name="criteria" value="cumulative"
                                                                               id="criteriaRadio"/>
                                            Cumulative
                                        </label>
                                        </div>
                                        <div class="col-sm-2">
                                            <form:select path="dateRangeType" class="form-control"
                                                         id="idDateRangeType">
                                                <form:option value="TODAY">TODAY</form:option>
                                                <form:option value="YESTERDAY">YESTERDAY</form:option>
                                                <form:option value="CUSTOM_DATE">CUSTOM DATE</form:option>
                                                <form:option value="LAST_7_DAYS">LAST 7 DAYS</form:option>
                                                <form:option value="LAST_14_DAYS">LAST 14 DAYS</form:option>
                                                <form:option value="LAST_30_DAYS">LAST 30 DAYS</form:option>
                                                <%-- <form:option value="LAST_WEEK">LAST WEEK</form:option>
                                <form:option value="LAST_BUSINESS_WEEK">LAST BUSINESS WEEK</form:option>
                                <form:option value="THIS_MONTH">THIS MONTH</form:option>
                                <form:option value="LAST_MONTH">LAST MONTH</form:option>
                                <form:option value="ALL_TIME">ALL TIME</form:option>
                                <form:option value="THIS_WEEK_SUN_TODAY">THIS WEEK SUN TODAY</form:option>
                                <form:option value="THIS_WEEK_MON_TODAY">THIS WEEK MON TODAY</form:option>
                                <form:option value="LAST_WEEK_SUN_SAT">LAST WEEK SUN SAT</form:option> --%>
                                            </form:select>
                                        </div>
                                        <div class="col-sm-2" id="idStartDateCol">

                                            <form:input class="form-control" type="text" path="startDate"
                                                        id="idStartDate" required="required"/>


                                        </div>
                                        <div class="col-sm-2" id="idEndDateCol">

                                            <form:input class="form-control" type="text" path="endDate"
                                                        id="idEndDate" required="required"/>

                                        </div>

                                    </div>
                                </div>
                                <div class="row" style="padding: 15px 0px;">
                                    <div class="form-group">
                                        <label class="col-sm-2"><strong>Status</strong></label>
                                        <div class="col-sm-3 ">
                                            <label class="checkbox-inline"><form:checkbox
                                                    path="status" value="Enabled"/>Active</label> <label
                                                class="checkbox-inline"><form:checkbox path="status"
                                                                                       value="Paused"/>Paused</label>
                                            <label class="checkbox-inline"><form:checkbox
                                                    path="status" value="Removed"/>Removed</label>
                                        </div>
                                    </div>
                                </div>

                                <div class="row">
                                    <div class="form-group" style="float: center;">
                                        <label class="col-lg-2"><strong>Show
                                            Campaigns </strong></label>
                                        <div class="col-lg-2 ">

                                            <label> <form:radiobutton path="filterCampaign"
                                                                      name="filterCampaign" id="rButtonAllCampaign"
                                                                      value="0"
                                                                      checked="checked"/> All
                                            </label> <label> <form:radiobutton path="filterCampaign"
                                                                               name="filterCampaign" value="1"
                                                                               id="rButtonFilteredCampaign"/> Let me
                                            choose
                                        </label>
                                        </div>
                                        <div class="col-lg-6" id="idDivFilter" style="display: none;">

                                            <div id="idDivSearchBox" class="input-group"
                                                 style="padding: 0px;">
												<span class="input-group-addon"><i
                                                        class="glyphicon glyphicon-search"
                                                        style="width: 15px; height: 14px;"></i></span>
                                                <form:input type="text" class="form-control"
                                                            id="searchCampaigns" path=""
                                                            placeholder="Search Campaigns"/>
                                                <span id="clearAll" style="cursor: pointer; color: blue;"
                                                      class="input-group-addon">Clear all</span>
                                            </div>


                                            <div id="divsearchBox" class="col-lg-12"
                                                 style="display: block; padding-top: 0px; border-style: solid solid solid solid; border-width: 1px; border-color: grey; max-height: 105px; overflow: auto;">
                                            </div>
                                            <form:select path="" id="idSelectOptions"
                                                         class="col-lg-11 form-control" size="4" multiple="multiple"
                                                         style="display: none;clear: both;">
                                                <c:forEach items="${campaignDosList}" var="campaignDo">
                                                    <option value="${campaignDo.id}">${campaignDo.name}</option>
                                                </c:forEach>
                                            </form:select>
                                        </div>

                                    </div>
                                </div>

                                <div class="row" style="margin-top: 5px">
                                    <div class="form-group">
                                        <div class="col-sm-2 col-sm-offset-2">
                                            <button id="idButtonFetchReport"
                                                    data-loading-text="Fetching Report..." type="submit"
                                                    class="btn btn-success">Fetch Report
                                            </button>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

            </form:form>

            <!-- 			<br> <br> -->
            <!-- 			<div id="idReportOverview"> -->
            <!-- 			OverView -->
            <!-- 			</div> -->
            <!-- 			<br><br> -->
            <br>
            <div id="dataDiv">
                <c:if test="${fn:length(campaignPerformanceReportDos) ne 0}">
                    <div class="row">
                        <div class="col-sm-8"></div>
                        <div id="idDivMail" class="col-sm-2" style="text-align: right;">
                            <i class="glyphicon glyphicon-envelope"></i> <a
                                data-toggle="modal" data-target="#myModal"
                                style="cursor: pointer;"> Mail Report</a>
                        </div>

                        <div class="col-sm-2" style="text-align: right;">
                            <i class="glyphicon glyphicon-download-alt"></i> <a
                                id="idDownloadReport"
                                href="${pageContext.request.contextPath}/downloadcsv">
                            Download Report</a>
                        </div>
                    </div>
                    <div class="table-responsive">
                        <input type="hidden" id="idRowsValue"
                               value="<c:out value='${rows}'/>">
                        <table id="idReportTable"
                               class="table table-bordered table-hover table-condensed">
                            <thead>


                            <tr>
                                <th id="idThSNo">S. No</th>

                                <c:if test="${reportCriteriaDo.criteria=='daywise'}">
                                    <th id="idThDa" class="cursor-pointer">Date <c:if
                                            test="${col eq null or col eq '' or col ne 'da'}">
                                        <i class="fa fa-fw fa-sort pull-right" style="color: grey;"
                                           aria-hidden="true"></i>
                                    </c:if> <c:if test="${col eq 'da' and o eq 0 }">
                                        <i class="fa fa-fw fa-sort-asc pull-right"
                                           style="color: blue;" aria-hidden="true"></i>
                                    </c:if> <c:if test="${col eq 'da' && o eq 1 }">
                                        <i class="fa fa-fw fa-sort-desc pull-right"
                                           style="color: blue;" aria-hidden="true"></i>
                                    </c:if>
                                    </th>
                                </c:if>

                                <th id="idThAd" class="cursor-pointer">Advertiser <c:if
                                        test="${col eq null or col eq '' or col ne 'ad'}">
                                    <i class="fa fa-fw fa-sort pull-right" style="color: grey;"
                                       aria-hidden="true"></i>
                                </c:if> <c:if test="${col eq 'ad' and o eq 0 }">
                                    <i class="fa fa-fw fa-sort-asc pull-right"
                                       style="color: blue;" aria-hidden="true"></i>
                                </c:if> <c:if test="${col eq 'ad' && o eq 1 }">
                                    <i class="fa fa-fw fa-sort-desc pull-right"
                                       style="color: blue;" aria-hidden="true"></i>
                                </c:if>
                                </th>
                                <th id="idThSt" class="cursor-pointer">Status <c:if
                                        test="${col eq null or col eq '' or col ne 'st'}">
                                    <i class="fa fa-fw fa-sort pull-right" style="color: grey;"
                                       aria-hidden="true"></i>
                                </c:if> <c:if test="${col eq 'st' and o eq 0 }">
                                    <i class="fa fa-fw fa-sort-asc pull-right"
                                       style="color: blue;" aria-hidden="true"></i>
                                </c:if> <c:if test="${col eq 'st' && o eq 1 }">
                                    <i class="fa fa-fw fa-sort-desc pull-right"
                                       style="color: blue;" aria-hidden="true"></i>
                                </c:if>
                                </th>
                                <th id="idThIm" class="cursor-pointer">Impressions <c:if
                                        test="${col eq null or col eq '' or col ne 'im'}">
                                    <i class="fa fa-fw fa-sort pull-right" style="color: grey;"
                                       aria-hidden="true"></i>
                                </c:if> <c:if test="${col eq 'im' and o eq 0 }">
                                    <i class="fa fa-fw fa-sort-asc pull-right"
                                       style="color: blue;" aria-hidden="true"></i>
                                </c:if> <c:if test="${col eq 'im' && o eq 1 }">
                                    <i class="fa fa-fw fa-sort-desc pull-right"
                                       style="color: blue;" aria-hidden="true"></i>
                                </c:if>
                                </th>
                                <th id="idThCl" class="cursor-pointer">Clicks <c:if
                                        test="${col eq null or col eq '' or col ne 'cl'}">
                                    <i class="fa fa-fw fa-sort pull-right" style="color: grey;"
                                       aria-hidden="true"></i>
                                </c:if> <c:if test="${col eq 'cl' and o eq 0 }">
                                    <i class="fa fa-fw fa-sort-asc pull-right"
                                       style="color: blue;" aria-hidden="true"></i>
                                </c:if> <c:if test="${col eq 'cl' && o eq 1 }">
                                    <i class="fa fa-fw fa-sort-desc pull-right"
                                       style="color: blue;" aria-hidden="true"></i>
                                </c:if>
                                </th>
                                <th id="idThCp">CPC</th>
                                <th id="idThCt">CTR (in %)</th>
                                <th id="idThCo" class="cursor-pointer">Cost ( in <i
                                        class="fa fa-inr" style="width: 15px; height: 14px;"></i>) <c:if
                                        test="${col eq null or col eq '' or col ne 'co'}">
                                    <i class="fa fa-fw fa-sort pull-right" style="color: grey;"
                                       aria-hidden="true"></i>
                                </c:if> <c:if test="${col eq 'co' and o eq 0 }">
                                    <i class="fa fa-fw fa-sort-asc pull-right"
                                       style="color: blue;" aria-hidden="true"></i>
                                </c:if> <c:if test="${col eq 'co' && o eq 1 }">
                                    <i class="fa fa-fw fa-sort-desc pull-right"
                                       style="color: blue;" aria-hidden="true"></i>
                                </c:if>
                                </th>
                            </tr>
                            <tr>
                                <th><b>Total</b></th>
                                <c:if test="${reportCriteriaDo.criteria=='daywise'}">
                                    <th></th>
                                </c:if>
                                <th></th>
                                <th></th>
                                <th><b><c:out value="${totalImpressions }"></c:out></b></th>
                                <th><b><c:out value="${totalClicks }"></c:out></b></th>

                                <c:if test="${totalClicks gt 0}">
                                    <th><b><fmt:formatNumber type="number"
                                                             minFractionDigits="2" maxFractionDigits="2"
                                                             value="${totalCost/totalClicks}"/> </b></th>
                                </c:if>
                                <c:if test="${totalClicks eq 0}">
                                    <th><b><c:out value="${'-'}"/></b></th>
                                </c:if>
                                <c:if test="${totalImpressions gt 0 }">
                                    <th><b><fmt:formatNumber type="number"
                                                             minFractionDigits="2" maxFractionDigits="2"
                                                             value="${100*totalClicks/totalImpressions}"/></b></th>
                                </c:if>
                                <c:if test="${totalImpressions eq 0 }">
                                    <th><b><c:out value="${'-'}"/></b></th>
                                </c:if>
                                <th><b><fmt:formatNumber type="number"
                                                         maxFractionDigits="0" value="${totalCost }"/></b></th>
                            </tr>
                            </thead>
                            <c:if test="${fn:length(campaignPerformanceReportDos) gt 0}">

                                <c:set var="count" value="${(pageNumber-1)*recordsPerPage}"
                                       scope="page"/>
                                <c:forEach items="${campaignPerformanceReportDos}"
                                           var="campaignPerformanceReportDo">
                                    <c:set var="count" value="${count + 1}" scope="page"/>

                                    <tr>
                                        <td style="width: 5%"><c:out value="${count}"/></td>
                                        <fmt:formatDate pattern="yyyy-MM-dd"
                                                        value="${campaignPerformanceReportDo.date}" var="myDate"/>
                                        <c:if test="${reportCriteriaDo.criteria=='daywise'}">
                                            <td style="width: 10%"><c:out value="${myDate}"/></td>
                                        </c:if>

                                        <td style="width: 20%"><c:out
                                                value="${campaignPerformanceReportDo.campaignDo.name}"/></td>
                                        <td style="width: 10%"><c:out
                                                value="${campaignPerformanceReportDo.campaignDo.status}"/></td>
                                        <td style="width: 10%"><c:out
                                                value="${campaignPerformanceReportDo.impressions}"/></td>
                                        <td style="width: 10%"><c:out
                                                value="${campaignPerformanceReportDo.clicks}"/></td>

                                        <c:if test="${campaignPerformanceReportDo.clicks gt 0}">
                                            <td style="width: 10%"><fmt:formatNumber type="number"
                                                                                     minFractionDigits="2"
                                                                                     maxFractionDigits="2"
                                                                                     value="${campaignPerformanceReportDo.cost/campaignPerformanceReportDo.clicks}"/>
                                            </td>
                                        </c:if>
                                        <c:if test="${campaignPerformanceReportDo.clicks eq 0}">
                                            <td style="width: 10%"><c:out value="${'-'}"/></td>
                                        </c:if>

                                        <c:if test="${campaignPerformanceReportDo.impressions gt 0 }">
                                            <td style="width: 10%"><fmt:formatNumber type="number"
                                                                                     minFractionDigits="2"
                                                                                     maxFractionDigits="2"
                                                                                     value="${100*campaignPerformanceReportDo.clicks/campaignPerformanceReportDo.impressions}"/></td>
                                        </c:if>
                                        <c:if test="${campaignPerformanceReportDo.impressions eq 0 }">
                                            <td style="width: 10%"><c:out value="${'-'}"/></td>
                                        </c:if>

                                        <td style="width: 10%"><fmt:formatNumber type="number"
                                                                                 maxFractionDigits="0"
                                                                                 value="${campaignPerformanceReportDo.cost}"/></td>
                                    </tr>

                                </c:forEach>
                            </c:if>
                        </table>

                    </div>
                </c:if>
                <c:if test="${fn:length(campaignPerformanceReportDos) eq 0}">
                    <div class="row">
                        <div class="col-sm-3"></div>
                        <div class="col-sm-6 center">
                            <div class="alert alert-default  fade in text-center text-danger">
                                <a href="#" class="close" data-dismiss="alert"
                                   aria-label="close"></a><strong>No data found for
                                selected criteria</strong>
                            </div>
                        </div>
                    </div>
                    <div class="col-sm-3"></div>
                </c:if>
                <div class="row" id="idPaginationDiv" style="margin-top: 10px;">
                    <form action="cpreport?col=${col }&o=${o }&page=1&rows="
                          role="form" id="noOfPagesForm" method="post">


                        <div class="col-lg-5" id="idPaginationInfo">
                            <code></code>
                        </div>
                        <div class="col-lg-3" style="float: right; text-align: right;">
                            <label for="noOfPages" class="control-label"><h5>
                                <code>Items Per Page: </code>
                            </h5></label> <select class="" name="asdf" id="idNoOfPages">
                            <option>5</option>
                            <option>10</option>
                            <option>20</option>
                            <option>50</option>
                            <option>100</option>
                            <%-- <option value="${totalRecords}">All</option> --%>
                        </select>
                        </div>
                    </form>
                    <div class="pagination-page col-lg-4"></div>

                </div>
            </div>
        </div>
    </div>
</div>


<script>
    $(document)
        .ready(
            function () {
                $("#idDropdownReport").addClass("active");
                $("#idDropdownMenuCPReport").addClass("active");

                $("#idReportTable").on("click", "th", function () {
                    var rowsVal = $("#idRowsValue").val();
                    var tagId = $(this).attr("id");
                    if (tagId == "idThDa") {//date
                        initLoader($);
                        if ($(this).find("i").length == 0) {
                            $("#idForm").attr("action", $("#idForm").attr("action") + "?col=da&o=0&rows=" + rowsVal);
                            $("#idForm").submit();
                        } else if ($(this).find("i").attr("class").indexOf("fa-sort-asc") > -1) {
                            $(this).find("i").removeClass("fa-sort-asc").addClass("fa-sort-desc");
                            $("#idForm").attr("action", $("#idForm").attr("action") + "?col=da&o=1&rows=" + rowsVal);
                            $("#idForm").submit();
                        }
                        else {
                            $(this).find("i").removeClass("fa-sort-desc").addClass("fa-sort-asc");
                            $("#idForm").attr("action", $("#idForm").attr("action") + "?col=da&o=0&rows=" + rowsVal);
                            $("#idForm").submit();
                        }
                    } else if (tagId == "idThAd") {// asvertiser
                        initLoader($);
                        if ($(this).find("i").length == 0) {
                            $("#idForm").attr("action", $("#idForm").attr("action") + "?col=ad&o=0&rows=" + rowsVal);
                            $("#idForm").submit();
                        } else if ($(this).find("i").attr("class").indexOf("fa-sort-asc") > -1) {
                            $(this).find("i").removeClass("fa-sort-asc").addClass("fa-sort-desc");
                            $("#idForm").attr("action", $("#idForm").attr("action") + "?col=ad&o=1&rows=" + rowsVal);
                            $("#idForm").submit();
                        }
                        else {
                            $(this).find("i").removeClass("fa-sort-desc").addClass("fa-sort-asc");
                            $("#idForm").attr("action", $("#idForm").attr("action") + "?col=ad&o=0&rows=" + rowsVal);
                            $("#idForm").submit();
                        }
                    } else if (tagId == "idThSt") {// status
                        initLoader($);
                        if ($(this).find("i").length == 0) {
                            $("#idForm").attr("action", $("#idForm").attr("action") + "?col=st&o=0&rows=" + rowsVal);
                            $("#idForm").submit();
                        } else if ($(this).find("i").attr("class").indexOf("fa-sort-asc") > -1) {
                            $(this).find("i").removeClass("fa-sort-asc").addClass("fa-sort-desc");
                            $("#idForm").attr("action", $("#idForm").attr("action") + "?col=st&o=1&rows=" + rowsVal);
                            $("#idForm").submit();
                        }
                        else {
                            $(this).find("i").removeClass("fa-sort-desc").addClass("fa-sort-asc");
                            $("#idForm").attr("action", $("#idForm").attr("action") + "?col=st&o=0&rows=" + rowsVal);
                            $("#idForm").submit();
                        }
                    } else if (tagId == "idThIm") {//Impressions
                        initLoader($);
                        if ($(this).find("i").length == 0) {
                            $("#idForm").attr("action", $("#idForm").attr("action") + "?col=im&o=0&rows=" + rowsVal);
                            $("#idForm").submit();
                        } else if ($(this).find("i").attr("class").indexOf("fa-sort-asc") > -1) {
                            $(this).find("i").removeClass("fa-sort-asc").addClass("fa-sort-desc");
                            $("#idForm").attr("action", $("#idForm").attr("action") + "?col=im&o=1&rows=" + rowsVal);
                            $("#idForm").submit();
                        }
                        else {
                            $(this).find("i").removeClass("fa-sort-desc").addClass("fa-sort-asc");
                            $("#idForm").attr("action", $("#idForm").attr("action") + "?col=im&o=0&rows=" + rowsVal);
                            $("#idForm").submit();
                        }
                    } else if (tagId == "idThCl") {//clicks
                        initLoader($);
                        if ($(this).find("i").length == 0) {
                            $("#idForm").attr("action", $("#idForm").attr("action") + "?col=cl&o=0&rows=" + rowsVal);
                            $("#idForm").submit();
                        } else if ($(this).find("i").attr("class").indexOf("fa-sort-asc") > -1) {
                            $(this).find("i").removeClass("fa-sort-asc").addClass("fa-sort-desc");
                            $("#idForm").attr("action", $("#idForm").attr("action") + "?col=cl&o=1&rows=" + rowsVal);
                            $("#idForm").submit();
                        }
                        else {
                            $(this).find("i").removeClass("fa-sort-desc").addClass("fa-sort-asc");
                            $("#idForm").attr("action", $("#idForm").attr("action") + "?col=cl&o=0&rows=" + rowsVal);
                            $("#idForm").submit();
                        }
                    } else if (tagId == "idThCo") {//Cost
                        initLoader($);
                        if ($(this).find("i:last").length == 0) {
                            $("#idForm").attr("action", $("#idForm").attr("action") + "?col=co&o=0&rows=" + rowsVal);
                            $("#idForm").submit();
                        } else if ($(this).find("i:last").attr("class").indexOf("fa-sort-asc") > -1) {
                            $(this).find("i:last").removeClass("fa-sort-asc").addClass("fa-sort-desc");
                            $("#idForm").attr("action", $("#idForm").attr("action") + "?col=co&o=1&rows=" + rowsVal);
                            $("#idForm").submit();
                        }
                        else {
                            $(this).find("i:last").removeClass("fa-sort-desc").addClass("fa-sort-asc");
                            $("#idForm").attr("action", $("#idForm").attr("action") + "?col=co&o=0&rows=" + rowsVal);
                            $("#idForm").submit();
                        }
                    }
                });

                $(document).on("click", "#idDivDeleteEmail", function () {
                    $(this).parent().remove();
                });
                $(document).on("click", "#idDivAddEmail", function () {
                    $(this).attr('id', 'idDivDeleteEmail');
                    if ($(this).attr('class').indexOf('default') == -1) {
                        $(this).html('<a class="btn btn-small"><i class="glyphicon glyphicon-remove"></i> Delete</a>');
                    } else {
                        $(this).html('');
                    }
                    $html = '<div class="form-group">'
                        + '<label for="email" class="col-lg-2 control-label">'
                        + '</label>'
                        + '<div class="col-lg-8">'
                        + '	<input class="form-control" type="email" required="required">'
                        + '</div>'
                        + '<div class="col-lg-1" id="idDivAddEmail">'
                        + '	<a id="idAddMail" class="btn btn-small"><i class="glyphicon glyphicon-plus"></i> Add</a>'
                        + '</div>'
                        + '</div>';
                    $("#idDivEmail").append($html);
                });
                $("#idFormModal").on("submit", function () {
                    inputTags = $(this).find("input[type='email']");
                    $("#toAddresses").val("");
                    inputTags.each(function () {
                        $("#toAddresses").val($("#toAddresses").val() + $(this).val() + ",");
                    });

                });
                $("#idReportCriteriaTitle").addClass("glyphicon glyphicon-minus");


                $('#idCollapseOne').on('click', function () {
                    if ($('#collapseOne').hasClass('in') == false) {

                        $("#idReportCriteriaTitle").removeClass("glyphicon-plus");
                        $("#idReportCriteriaTitle").addClass("glyphicon-minus");
                    }
                    else {
                        $("#idReportCriteriaTitle").removeClass("glyphicon-minus");
                        $("#idReportCriteriaTitle").addClass("glyphicon-plus");
                    }
                });

                if ("${fn:length(campaignPerformanceReportDos)}" == 0) {
                    $("#collapseOne").attr("class",
                        "panel-collapse collapse in");
                    $("#idReportCriteriaTitle").removeClass("glyphicon-plus");
                    $("#idReportCriteriaTitle").addClass("glyphicon-minus");

                } else {
                    $("#collapseOne").attr("class",
                        "panel-collapse collapse");
                    $("#idReportCriteriaTitle").removeClass("glyphicon-minus");
                    $("#idReportCriteriaTitle").addClass("glyphicon-plus");
                }

                $("#formDiv").children().each(function () {
                    $(this).on('keyup', function () {

                        if ($(this).data('c_val') != $(this).val()) {
                            alert("value changed!");
                        }
                        $(this).data('c_val', $(this).val());
                    }).data('c_val', $(this).val());

                });

                var options = $("#idSelectOptions").find("option");
                var allCampaigns = "";
                options.each(function () {
                    allCampaigns += $(this).val() + ",";
                });
                if ($("#rButtonFilteredCampaign").is(":checked")) {
                    $("#idDivFilter").show();
                } else {
                    $("#idDivFilter").hide();
                }
                $("#rButtonFilteredCampaign").on("click", function () {
                    if ($("#suggestiontagInclude").length == 0) {
                        $("#idCampaignInclude").val("");
                    }
                    $("#idDivFilter").show();
                });
                $("#rButtonAllCampaign").on("click", function () {
                    $("#idDivFilter").hide();
                });

                if ($("#idCampaignInclude").val()
                    && $("#rButtonFilteredCampaign").is(':checked')) {
                    var includeCampaignArray = $("#idCampaignInclude")
                        .val().slice(0, -1).split(",");

                    $
                        .each(
                            includeCampaignArray,
                            function (index, value) {
                                $("#divsearchBox").show();
                                $("#clearAll").show();
                                var text = $(
                                    "#idSelectOptions option[value="
                                    + value + "]")
                                    .text();
                                var suggestion = '<span id="suggestiontagInclude" class="suggestiontagClass">'
                                    + text
                                    + '<i id="crossbInclude" onmouseover=\'this.style.color="red"\' onmouseout=\'this.style.color="black"\' style="cursor: pointer; padding:0px 5px;" data-id='
                                    + value + '>x</i>'
                                    + '</span>';
                                $("#divsearchBox").prepend(
                                    suggestion);
                                if ($("#divsearchBox")
                                        .find(
                                            "#suggestiontagInclude").length == 1) {
                                    $("#clearAll").show();
                                }
                                $val = value;
                                $(
                                    "#idSelectOptions option[value="
                                    + $val + "]")
                                    .attr("style",
                                        "display: none;");
                                $(
                                    "#idSelectOptions option[value="
                                    + $val + "]")
                                    .attr("selected",
                                        "selected");

                            });

                }
                if ($("#divsearchBox").find("#suggestiontagInclude").length == 0) {
                    $("#clearAll").hide();
                }

                $("#idDivSearchBox").on(
                    "click",
                    function () {
                        $("#idSelectOptions").attr("style",
                            "display: block");
                    });
                $("#searchCampaigns")
                    .on(
                        "keyup keydown",
                        function () {
                            T = $(this);
                            var options = $("#idSelectOptions")
                                .find("option");
                            options
                                .each(function () {
                                    i = $(this)
                                        .text()
                                        .toLowerCase()
                                        .indexOf(
                                            T
                                                .val()
                                                .toLowerCase());
                                    if (i >= 0) {
                                        if ($(this).attr(
                                                "selected") != "selected") {
                                            $(this)
                                                .attr(
                                                    "style",
                                                    "display: block;");
                                        }
                                    } else {
                                        $(this)
                                            .attr(
                                                "style",
                                                "display: none;");
                                    }
                                });
                        });
                $("#idSelectOptions")
                    .on(
                        "change",
                        function () {
                            var values = $(
                                "#idSelectOptions option:selected")
                                .toArray();
                            for (var a in values) {
                                $text = values[a].text;
                                $val = values[a].value;
                                var suggestion = '<span id="suggestiontagInclude" class="suggestiontagClass">'
                                    + $text
                                    + '<i id="crossbInclude" onmouseover=\'this.style.color="red"\' onmouseout=\'this.style.color="black"\' style="cursor: pointer; padding:0px 5px;" data-id='
                                    + $val + '>x</i>'
                                    + '</span>';
                                $("#divsearchBox").prepend(
                                    suggestion);
                                if ($("#divsearchBox")
                                        .find(
                                            "#suggestiontagInclude").length == 1) {
                                    $("#clearAll").show();
                                }
                                $("#idCampaignInclude").val(
                                    $("#idCampaignInclude")
                                        .val()
                                    + $val + ",");

                                $(
                                    "#idSelectOptions option[value='"
                                    + $val + "']")
                                    .attr("style",
                                        "display: none;");
                                $(
                                    "#idSelectOptions option[value='"
                                    + $val + "']")
                                    .attr("selected",
                                        "selected");
                            }
                        });
                $(document)
                    .on(
                        'click',
                        '#crossbInclude',
                        function () {
                            $id = $(this).data('id');
                            $(
                                "#idSelectOptions option[value='"
                                + $id + "']")
                                .removeAttr("style");
                            $(
                                "#idSelectOptions option[value='"
                                + $id + "']")
                                .removeAttr("selected");
                            suggestionTag = $(this).closest(
                                '#suggestiontagInclude');
                            suggestionTag.remove();
                            if ($("#divsearchBox").find(
                                    "#suggestiontagInclude").length == 0) {
                                $("#clearAll").hide();
                            }
                            $("#idCampaignInclude").val(
                                $("#idCampaignInclude")
                                    .val().replace(
                                    $id + ",",
                                    ""));
                        });
                $(document).on('submit', function () {

                    if ($("#rButtonAllCampaign").is(':checked')) {
                        $("#idCampaignInclude").val(allCampaigns);
                    } else if ($("#suggestiontagInclude").length == 0) {
                        alert("Select a campaign to show");
                        //$("#collapseOne").attr("class","panel-collapse collapse in");
                        return false;
                    }
                    $("#idButtonFetchReport").button('loading');
                    initLoader($);
                });
                $("#clearAll")
                    .on(
                        'click',
                        function () {
                            suggestionBoxes = $("#divsearchBox")
                                .find("span");
                            suggestionBoxes
                                .each(function () {
                                    $id = $(this)
                                        .find(
                                            "#crossbInclude")
                                        .data('id');
                                    $(
                                        "#idSelectOptions option[value='"
                                        + $id
                                        + "']")
                                        .removeAttr(
                                            "style");
                                    $(
                                        "#idSelectOptions option[value='"
                                        + $id
                                        + "']")
                                        .removeAttr(
                                            "selected");
                                    $(this).remove();
                                    if ($("#divsearchBox")
                                            .find(
                                                "#suggestiontagInclude").length == 0) {
                                        $("#clearAll")
                                            .hide();
                                        return;
                                    }
                                    $("#idCampaignInclude")
                                        .val("");
                                });
                        });

                if ($("#idDateRangeType").val() != "CUSTOM_DATE") {
                    $("#idStartDate").prop('readonly', true);
                    $("#idEndDate").prop('readonly', true);
                }
                value = $("#idDateRangeType").val();

                if (value == "CUSTOM_DATE") {

                    custom_date();
                    $("#idStartDate").datepicker("enable");
                    $("#idEndDate").datepicker("enable");
                }
                else {
                    $("#idStartDate").datepicker("disable");
                    $("#idEndDate").datepicker("disable");
                }
                $("#idDateRangeType")
                    .change(
                        function () {
                            value = $("#idDateRangeType").val();

                            if (value == "CUSTOM_DATE") {

                                custom_date();
                                $("#idStartDate").datepicker("enable");
                                $("#idEndDate").datepicker("enable");

                            } else {

                                $("#idStartDate").datepicker("disable");
                                $("#idEndDate").datepicker("disable");

                                $("#idStartDate").prop(
                                    'readonly', true);
                                $("#idEndDate").prop(
                                    'readonly', true);

                                var StartDate = new Date();
                                var EndDate = new Date();
                                if (value == "TODAY") {
                                }

                                else if (value == "YESTERDAY") {
                                    StartDate.setDate(StartDate
                                            .getDate() - 1);
                                    EndDate.setDate(EndDate
                                            .getDate() - 1);
                                } else if (value == "LAST_7_DAYS") {
                                    StartDate.setDate(StartDate
                                            .getDate() - 7);
                                } else if (value == "LAST_14_DAYS") {
                                    StartDate.setDate(StartDate
                                            .getDate() - 14);
                                } else if (value == "LAST_30_DAYS") {
                                    StartDate.setDate(StartDate
                                            .getDate() - 30);
                                }

                                $("#idStartDate").val(
                                    formatDate(StartDate));
                                $("#idEndDate").val(
                                    formatDate(EndDate));

                            }
                        });

            });

    function custom_date() {
        $("#idStartDate").prop(
            'readonly', false);
        $("#idEndDate").prop(
            'readonly', false);

        var dateFormat = "mm/dd/yy", from = $(
            "#idStartDate")
            .datepicker(
                {
                    changeMonth: true,
                    changeYear: true,
                    showOtherMonths: true,
                    numberOfMonths: 1,
                    maxDate: new Date(),
                    selectOtherMonths: true
                })
            .on(
                "change",
                function () {
                    to
                        .datepicker(
                            "option",
                            "minDate",
                            getDate(this));
                }), to = $(
            "#idEndDate")
            .datepicker(
                {
                    changeMonth: true,
                    changeYear: true,
                    showOtherMonths: true,
                    numberOfMonths: 1,
                    maxDate: new Date(),
                    selectOtherMonths: true
                })
            .on(
                "change",
                function () {
                    from
                        .datepicker(
                            "option",
                            "maxDate",
                            getDate(this));
                });

        function getDate(element) {
            var date;
            try {
                date = $.datepicker
                    .parseDate(
                        dateFormat,
                        element.value);
            } catch (error) {
                date = null;
            }

            return date;
        }

    }

    function formatDate(date) {
        var d = new Date(date), month = '' + (d.getMonth() + 1), day = ''
            + d.getDate(), year = d.getFullYear();

        if (month.length < 2)
            month = '0' + month;
        if (day.length < 2)
            day = '0' + day;

        return [month, day, year].join('/');
    }

    $(document).ready(function () {

        var url = document.URL.split('?');
        var items = $("table tbody tr");
        var pageNumber = "${pageNumber}";
        var totalRecords = "${totalRecords}";
        var recordsPerPage = "${recordsPerPage}";
        var colmn = "${col}";
        var orderby = "${o}";
        var showFrom = recordsPerPage * (pageNumber - 1);
        var showTo = showFrom + recordsPerPage * 1;

        //Pagintation starts

        if (parseInt(totalRecords) <= 1) {
            $("#idPaginationDiv").attr("style", "display: none;");
        }
        if (parseInt(totalRecords) <= parseInt(recordsPerPage)) {
            $(".pagination-page").attr("style", "display: none;");
        }

        $(".pagination-page").pagination({

            items: totalRecords,
            ellipsePageSet: false,
            displayedPages: 3,
            edges: 2,
            itemsOnPage: recordsPerPage,
            cssStyle: "light-theme",
            currentPage: pageNumber,
            selectOnClick: false,
            hrefTextPrefix: url[0] + '?col=' + colmn + '&o=' + orderby + '&rows=' + recordsPerPage + '&page=',
            onPageClick: function (pageNumber) {
                items.hide().slice(showFrom, showTo).show();
            }
        });

        //Pagination ends


        //DataTable used for sorting starts

        /* 	 var t = $('#idReportTable').DataTable({
         paging: false,
         info: false,
         filter: true,
         searching: false,
         orderCellsTop: true,
         "columnDefs": [
         { "visible": true,"orderable": false,order:"applied", "targets": 0 },
         ],
         "order": [[ 2, 'asc' ]]
         });


         t.on( 'order.dt search.dt', function () {
         j=showFrom;
         t.column(0, {search:'applied', order:'applied'}).nodes().each( function (cell) {
         cell.innerHTML = j+1;j++;
         } );
         } ).draw();
         */

        //DataTable used for sorting starts

        if (showTo <= totalRecords) {
            $("#idPaginationInfo").text("Showing " + (showFrom + 1) + " - " + showTo + " of " + totalRecords + " campaigns");
        }
        else {
            showTo = totalRecords;
            $("#idPaginationInfo").text("Showing " + (showFrom + 1) + " - " + showTo + " of " + totalRecords + " campaigns");
        }
        $("#idNoOfPages").val("${recordsPerPage}")
        $("#idNoOfPages").on('change', function () {
            var oldActionUrl = "";
            var noOfPages = "";
            oldActionUrl = $("#noOfPagesForm").attr('action');
            noOfPages = $(this).val();
            $("#noOfPagesForm").attr('action', oldActionUrl + noOfPages);
            $("#noOfPagesForm").submit();
        });

    });

</script>