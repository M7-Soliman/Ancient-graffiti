<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<title>Ancient Graffiti Project :: Search Results</title>
<%@include file="../../resources/common_head.txt"%>
<script type="text/javascript">
	function selectImg(ind, k, page, thumbnail){
		document.getElementById("imgLink"+k).href = page;
		document.getElementById("imgSrc"+k).src = thumbnail;
	}
</script>
</head>
<body>
	<%@include file="header.jsp"%>
	<div class="container">
		<p>
			<c:out value="${fn:length(requestScope.resultsLyst)} graffiti found" />
		</p>
		<%@ include file="resultsList.jsp"%>
	</div>
	<%@include file="footer.jsp" %>
</body>
</html>
