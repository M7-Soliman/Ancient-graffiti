<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<!DOCTYPE html>
<html lang="en">
<head>
<%@include file="/resources/common_head.txt"%>
<meta charset="UTF-8">
<title>Ancient Graffiti Project :: Translation Practice</title>
<link rel="stylesheet" type="text/css"
	href="resources/css/featured_graffiti.css">
<style>
@media only screen and (max-width: 1023px) {
	[class*="col"] {
		width: 100%;
	}
}

th {
	text-align: center;
}
</style>
</head>
<body>
	<%@include file="header.jsp"%>
	<section class="container">
		<div id="selectors">
			<a href="/about/teaching-resources/">
				<button class="btn btn-agp right-align">Teaching Resources</button>
			</a>
		</div>
		<h2>Translation Practice</h2>
		<c:set var="translationHits" value="${translationHits}" />
		<%@include file="translation_featured_graffiti.jsp"%>
	</section>
	<%@include file="footer.jsp"%>
	<script type="text/javascript">
		//Toggle Translations
		$(".showTrans").click(function() {
			var button = $(this);
			if (button.val() == "Show Translation") {
				button.val("Hide Translation");
			} else {
				button.val("Show Translation");
			}

			button.prev().toggle();
		});
	</script>
</body>
</html>