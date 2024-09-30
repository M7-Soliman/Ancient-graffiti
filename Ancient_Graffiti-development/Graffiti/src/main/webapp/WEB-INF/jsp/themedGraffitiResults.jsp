<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="edu.wlu.graffiti.bean.Theme"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<!DOCTYPE html>
<html lang="en">
<head>
	<%Theme theme = (Theme) request.getAttribute("theme");%>

<%@include file="/resources/common_head.txt"%>
<meta charset="UTF-8">
<title>Ancient Graffiti Project :: Featured Graffiti - <%= theme.getName() %> </title>
<link rel="stylesheet" type="text/css"
	href="<%=request.getContextPath()%>/resources/css/themedGraffiti.css" />
<link rel="stylesheet" type="text/css"
	href="<%=request.getContextPath()%>/resources/css/details.css" />
</head>
<body>
	<%@include file="header.jsp"%>

	<main>
		<div class="container">
		
			<c:if test="${requestScope.hasWarmUp}">
				<div class="button_bar">
					<a
						href="http://ancientgraffiti.org/about/wp-content/uploads/DiscussionQuestions/
						<%=theme.getName()%>
						-Featured-Graffiti-Discussion-Questions.pdf"
						target="_blank">
						<button class="btn btn-agp right-align">Teaching
							Resources: Warm-Up Activities</button>
					</a>
				</div>
			</c:if>

			<c:set var="theme" value="${theme}" />

			<h2 id="theme_name">
				<c:choose>
					<c:when test="${theme.name == 'Fungraffiti'}">
						<a href="<%=request.getContextPath()%>/featured-graffiti"
							style="color: maroon;">Featured Graffiti</a>: Fun Graffiti
				</c:when>
					<c:otherwise>
						<a href="<%=request.getContextPath()%>/featured-graffiti"
							style="color: maroon;">Featured Graffiti</a>:
					<%=theme.getName()%>
					</c:otherwise>
				</c:choose>
			</h2>

			<c:if test="${requestScope.hasWarmUp}">
				<p><%=theme.getDescription()%></p>
			</c:if>
			
			<c:set var="inscriptions" value="${inscriptions}" />

			<div class="row row-cols-2 row-cols-md-2 g-4" style="width: 100%">
				<c:forEach var="i" items="${inscriptions}">
					<div class="col">
						<div class="card mb-4">
							<div class="card-header">
								<h5>
									<c:choose>
										<c:when test="${i.inDatabase}">
											<a
												href="<%=request.getContextPath() %>/graffito/AGP-${i.graffitiId}">
										</c:when>
										<c:otherwise>
											<a
												href="<%=request.getContextPath() %>/graffito/featured/AGP-${i.graffitiId}">
										</c:otherwise>
									</c:choose>
									AGP-${i.graffitiId}</a>
								</h5>
							</div>
							<div class="card-body">
								<h6 class="card-title">${i.contentWithLineBreaks}</h6>
								<hr>
								<p class="card-text" style="">${i.contentTranslationWithLineBreaks}</p>
								<div class="learnMoreDiv">
									<c:choose>
										<c:when test="${i.inDatabase}">
											<a class="learnMore"
												href="<%=request.getContextPath() %>/graffito/AGP-${i.graffitiId}"
												id="${i.graffitiId}">
										</c:when>
										<c:otherwise>
											<a class="learnMore"
												href="<%=request.getContextPath() %>/graffito/featured/AGP-${i.graffitiId}"
												id="${i.graffitiId}">
										</c:otherwise>
									</c:choose>
									Learn More &#10140;</a>
								</div>
							</div>
						</div>
					</div>
				</c:forEach>
			</div>
		</div>
	</main>
	<%@include file="footer.jsp"%>
</body>
</html>