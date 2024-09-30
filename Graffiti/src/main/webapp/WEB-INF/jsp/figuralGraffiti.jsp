<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<!DOCTYPE html>
<html lang="en">
<head>
<%@include file="/resources/common_head.txt"%>
<meta charset="UTF-8">
<title>Ancient Graffiti Project :: Figural Graffiti</title>
<link rel="stylesheet" type="text/css"
	href="<%=request.getContextPath()%>/resources/css/featured_graffiti.css" />
<link rel="stylesheet" type="text/css"
	href="../resources/css/themedGraffiti.css" />
</head>
<body>
	<%@include file="header.jsp"%>
	<main>
		<div class="container">

			<h2>
				<a href="<%=request.getContextPath()%>/featured-graffiti"
					style="color: maroon;">Featured Graffiti</a>: Figural Graffiti
			</h2>

			<c:set var="figuralHits" value="${figuralHits}" />

			<%@include file="featured_figural_hits.jsp"%>
		</div>
	</main>
	<%@include file="footer.jsp"%>
</body>
</html>