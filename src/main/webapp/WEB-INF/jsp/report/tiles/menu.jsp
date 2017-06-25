<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib prefix="sec"
	uri="http://www.springframework.org/security/tags"%>

<%
	Object sUser =  session.getAttribute("sUser");
%>

<nav class="navbar navbar-inverse">
	<!-- Collect the nav links, forms, and other content for toggling -->
	<div class="collapse navbar-collapse navbar-ex1-collapse">

		<ul class="nav navbar-nav">
			<li id="idDropdownReport" class="dropdown"><a href="#"
				class="dropdown-toggle" data-toggle="dropdown"><i
					class="glyphicon glyphicon-stats white"></i> Reports <b
					class="caret"></b></a>
				<ul class="dropdown-menu">
					<li id="idDropdownMenuCPReport"><a
						href="${pageContext.request.contextPath}/cpreport"
						title="Campaign Performance Report"><i
							class="glyphicon glyphicon-list-alt white"></i> Advertiser
							Performance Report </a></li>
				</ul></li>
		</ul>

		<ul class="nav navbar-nav">
			<li id="idDropdownAccount" class="dropdown"><a href="#"
				class="dropdown-toggle" data-toggle="dropdown"><i
					class="glyphicon glyphicon-stats white"></i> Account <b
					class="caret"></b></a>
				<ul class="dropdown-menu">
					<li id="idDropdownMenuManageAccount"><a
						href="${pageContext.request.contextPath}/edituser?uid=${sUser.id}"
						title="Manage Account"><i
							class="glyphicon glyphicon-list-alt white"></i> Update Account </a></li>

				</ul></li>
		</ul>


		<ul class="nav navbar-nav navbar-right">
			<li><a href="<c:url value="/logout" />" title="logout"><i
					class="glyphicon glyphicon-log-out white"></i> Logout</a></li>
		</ul>
	</div>
	<!-- /.navbar-collapse -->
</nav>