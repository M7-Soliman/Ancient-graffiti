<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="en">
<head>
<%@ include file="/resources/common_head.txt"%>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Term Index</title>
<link rel="stylesheet" href="<%=request.getContextPath()%>/resources/css/main.css" />
<link rel="stylesheet"
	href="<%=request.getContextPath()%>/resources/css/main.css" />
<link rel="stylesheet" type="text/css"
	href="<%=request.getContextPath()%>/resources/css/details.css" />
<script>
// Right now this only returns the user to the general index page
function backToResults(){
	var url = "${sessionScope.returnFromTermsURL}";
	window.location.href = url;
}
</script>		
</head>
<body>
	<%@include file="/WEB-INF/jsp/header.jsp"%>
	<div class="button_bar">
		<button class="btn btn-agp" onclick="backToResults();">Back to Results</button>
	</div>
	<%@include file="termList.jsp" %>
	<%@include file="/WEB-INF/jsp/footer.jsp" %>
</body>
</html>