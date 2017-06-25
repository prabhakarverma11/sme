<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<html>
<head>
<title>403 - Access Denied</title>
</head>

<body>
	<h1>HTTP Status 403 - Access is denied</h1>

	 <h2><c:if test="${username ne null}">Username : ${username} <br/></c:if>
	     You do not have permission to access this page!
	 </h2>
	 <c:if test="${username ne null}"><br></c:if>
	 
	 <a href="/sme/userHome">Return to Home Page</a>
</body>
</html>