<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ page trimDirectiveWhitespaces="true" %>
<%@taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<style>
.homebtn{
	font-size: 18px;
	width: 160px;
	height: 50px;
}
</style>
<script>
$("#idDropdownHome").attr("style","color: #fff");
FusionCharts.ready(function () {
    var vstrChart = new FusionCharts({
        type: 'msline',
        renderAt: 'chart-container',
        width: '550',
        height: '400',
        dataFormat: 'json',
        dataSource: {
            "chart": {
                "caption": "Website Visitors",
                "subCaption": "",
                "xAxisName": "Day",
                "yAxisName": "No. of Visitors",
                "theme": "fint",
                "showValues": "0",
                //Setting automatic calculation of div lines to off
                "adjustDiv": "0",
                //Manually defining y-axis lower and upper limit
                "yAxisMaxvalue": "35000",
                "yAxisMinValue": "3000",
                //Setting number of divisional lines to 9
                "numDivLines": "9"
            },            
            "categories": [
                {
                    "category": [
                        {
                            "label": "Mon"
                        }, 
                        {
                            "label": "Tue"
                        }, 
                        {
                            "label": "Wed"
                        }, 
                        {
                            "label": "Thu"
                        }, 
                        {
                            "label": "Fri"
                        }, 
                        {
                            "label": "Sat"
                        }, 
                        {
                            "label": "Sun"
                        }
                    ]
                }
            ],            
            "dataset": [
                {
                    "seriesname": "Last Week",
                    "data": [
                        {
                            "value": "13000"
                        }, 
                        {
                            "value": "14500"
                        }, 
                        {
                            "value": "13500"
                        }, 
                        {
                            "value": "15000"
                        }, 
                        {
                            "value": "15500"
                        }, 
                        {
                            "value": "17650"
                        }, 
                        {
                            "value": "19500"
                        }
                    ]
                }, 
                {
                    "seriesname": "This Week",
                    "data": [
                        {
                            "value": "15400"
                        }, 
                        {
                            "value": "16800"
                        }, 
                        {
                            "value": "18800"
                        }, 
                        {
                            "value": "22400"
                        }, 
                        {
                            "value": "23800"
                        }, 
                        {
                            "value": "25800"
                        }, 
                        {
                            "value": "30800"
                        }
                    ]
                }
            ]
        }
    }).render();
});
	function getInfo() {
		var v = document.offersearchform.campaignName.value;
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
							$("#idFormSearch").attr("action","listcampaign?userId="+val1);
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

<div class="container" style="min-height: 500px;">
	<div class="row" style="text-align: center;">
		<div class="col-sm-3">
			<input type="button" class="btn btn-primary homebtn"
				value="Advertiser List" onclick="location.href='listcampaign'">
		</div>
		<div class="col-sm-3">
			<input type="button" class="btn btn-success homebtn"
				value="Product List" onclick="location.href='listadgroup'">
		</div>
		<div class="col-sm-3">
			<input type="button" class="btn btn-success homebtn"
				value="Product Details" onclick="location.href='adgroupdetails'">
		</div>
		<div class="col-sm-3">
			<input type="button" class="btn btn-primary homebtn"
				value="User List" onclick="location.href='listuser'">
		</div>
	</div>
<%-- 	<c:if test="${fn:length(newCampaignDos) gt 0 }"> --%>
	<div class="row" style="padding-top: 30px;">
		<div class="col-lg-3">
			<div class="panel panel-default">
				<div class="panel-heading" role="tab" id="headingOne" style="text-align:center;">
					<strong >New Advertisers</strong>
				</div>
				<div id="collapseOne" class="panel-collapse collapse in" style="overflow :scroll; max-height:300px;min-height: 300px;"
								role="tabpanel" aria-labelledby="headingOne">
					<ul class="list-group">
						<c:forEach items="${newCampaignDos }" var="campaignDo">
							<li class="list-group-item" style="margin:2px 2px 2px 2px;">
								<strong><a href="${pageContext.request.contextPath}/listcampaign?userId=${campaignDo.userDo.id}"><c:out value="${campaignDo.userDo.name }"></c:out></a></strong> has created <strong><a href="${pageContext.request.contextPath}/listadgroup?cid=${campaignDo.id}"><c:out value="${campaignDo.name}"></c:out></a></strong> today.
							</li>
						</c:forEach>
					</ul>
				</div>
			</div>
		</div>
<%-- 	</c:if> --%>
<%-- 		<c:if test="${fn:length(campaignDosExpireToday) gt 0 }"> --%>
		<div class="col-lg-3">
			<div class="panel panel-default">
				<div class="panel-heading" role="tab" id="headingOne" style="text-align:center;">
					<strong>Advertisers going to expire</strong>
				</div>
				<div id="collapseOne" class="panel-collapse collapse in"
								role="tabpanel" aria-labelledby="headingOne" style="overflow :scroll; max-height:300px;min-height: 300px;">
					<ul class="list-group" >
						<c:forEach items="${campaignDosExpireToday }" var="campaignDo">
							<li class="list-group-item" style="margin:2px 2px 2px 2px;">
								<strong><a href="${pageContext.request.contextPath}/listadgroup?cid=${campaignDo.id}"><c:out value="${campaignDo.name}"></c:out></a></strong> is going to expire today.
							</li>
						</c:forEach>
					</ul>
				</div>
			</div>
		</div>
<%-- 		</c:if> --%>
		<div class="col-lg-6">
			<div class="panel panel-default">
				<div class="panel-heading" role="tab" id="headingOne" style="text-align:center;">
					<strong>Weekly Report</strong>
				</div>
				<div id="collapseOne" class="panel-collapse collapse in"
								role="tabpanel" aria-labelledby="headingOne" style="overflow :scroll; max-height:300px;min-height: 300px;">
<!-- 					<div id="chart-container"></div> -->
				</div>
			</div>
		</div>
	</div>

</div>