<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<!DOCTYPE html>
<html lang="en">
<head>
<%@include file="/resources/common_head.txt"%>
<meta charset="UTF-8">
<title>Ancient Graffiti Project :: Featured Graffiti</title>
<link rel="stylesheet" type="text/css"
	href="resources/css/featured_graffiti.css">
</head>
<body>
	<%@include file="header.jsp"%>

	<div class="container" style="max-width: 1140px;">
		<div id="selectors">
			<a href="/about/teaching-resources/lesson-plans/warm-up-activities/"
				target="_blank">
				<button class="btn btn-agp right-align">Teaching Resources:
					Warm-Up Activities</button>
			</a> <a href="<%=request.getContextPath()%>/TranslationPractice">
				<button class="btn btn-agp right-align">Translation
					Practice</button>
			</a>
		</div>

		<h2>Featured Graffiti</h2>

		<p>Explore some of the most fascinating graffiti from Pompeii and
			Herculaneum.</p>

		<div id="themes">
			<ul class="themes-grid">
				<c:forEach var="theme" items="${themes}">
					<li><a
						href="<%=request.getContextPath()%>/themes/${theme.name}"
						class="darken"> <img
							src="<%=request.getContextPath()%>/resources/images/featured_graffiti/${theme.name}.png"
							alt="${theme.name}" /> <span class="message">${theme.description}</span>
					</a></li>
				</c:forEach>
			</ul>
		</div>
	</div>
	<%@include file="footer.jsp"%>
	<script type="text/javascript">
		$('.darken').hover(function() {
			$(this).find('.message').fadeIn(100);
		}, function() {
			$(this).find('.message').fadeOut(100);
		});
	</script>
</body>
</html>