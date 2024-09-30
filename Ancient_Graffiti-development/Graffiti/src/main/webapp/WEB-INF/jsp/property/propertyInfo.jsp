
<%@ page language="java" contentType="text/html; charset=US-ASCII"
	pageEncoding="US-ASCII"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=US-ASCII">
<title>Ancient Graffiti Project :: Property Info</title>
<%@include file="/resources/common_head.txt"%>
<%@include file="/resources/leaflet_common.txt"%>
<!-- this is the stuff for leaflet map -->
<link rel="stylesheet"
	href="<%=request.getContextPath()%>/resources/css/main.css" />
<link rel="stylesheet" type="text/css"
	href="<%=request.getContextPath()%>/resources/css/details.css" />
</head>
<body>
	<!-- this script is also for leaflet -->
	<%@include file="/WEB-INF/jsp/header.jsp"%>
	<c:set var="i" value="${requestScope.inscription}" />
	<div class="container">
		<c:if test="${prop.insula.city.name.equals('Pompeii')}">
			<h1>${prop.italianPropertyName}</h1>
		</c:if>
		<c:if test="${prop.insula.city.name.equals('Herculaneum')}">
			<h1>${prop.englishPropertyName}</h1>
		</c:if>

		<div class="button_bar">
			<a href="<%=request.getContextPath()%>/properties/" id="allprops"><button
					class="btn btn-agp right-align">View All Properties</button></a> <a
				href="<%=request.getContextPath() %>/properties/${prop.insula.city.name}/${prop.insula.shortName}/${prop.propertyNumber }/json"
				id="json"><button class="btn btn-agp right-align btn-export"
					style="margin-right: 5px;">Export JSON Data</button></a>
		</div>

		<table class="property-table table table-striped table-bordered"
			style="max-width: 450px;">
			<tr>
				<th class="propertyLabel">City:</th>
				<td><a
					href="http://pleiades.stoa.org/places/${prop.insula.city.pleiadesId}">${prop.insula.modernCity}</a></td>
			</tr>
			<tr>
				<th class="propertyLabel">Insula:</th>
				<td>${prop.insula.fullName}</td>
			</tr>
			<tr>
				<th class="propertyLabel">Property Number:</th>
				<td>${prop.propertyNumber}</td>
			</tr>
			<c:if test="${not empty prop.additionalEntrances}">
				<tr>
					<th class="propertyLabel">Additional Entrances:</th>
					<td>${prop.additionalEntrances}</td>
				</tr>
			</c:if>
			<c:if test="${not empty prop.englishPropertyName}">
				<tr>
					<th class="propertyLabel">Property Name<br />(in English):
					</th>
					<td>${prop.englishPropertyName}</td>
				</tr>
			</c:if>
			<c:if test="${not empty prop.italianPropertyName}">
				<tr>
					<th class="propertyLabel">Property Name<br />(in Italian):
					</th>
					<td>${prop.italianPropertyName}</td>
				</tr>
			</c:if>

			<c:if test="${not empty prop.pleiadesId}">
				<tr>
					<th class="propertyLabel">Pleiades ID:</th>
					<td>${prop.pleiadesId }</td>
				</tr>
			</c:if>
			<tr>
				<th class="propertyLabel">Property Type:</th>
				<td>${prop.propertyTypesAsString}</td>
			</tr>
			<c:if test="{not empty prop.commentary}">
				<tr>
					<th class="propertyLabel">Commentary:</th>
					<td>${prop.commentary }</td>
				</tr>
			</c:if>

			<tr>
				<th class="propertyLabel">Inscriptions:</th>
				<c:choose>
					<c:when test="${prop.numberOfGraffiti > 0}">
						<td><a
							href="<%=request.getContextPath()%>/results?property=${ prop.id}">
								Graffiti in this property</a></td>
					</c:when>
					<c:when
						test="${prop.numberOfGraffiti==0 && prop.insula.city.name.equals('Pompeii')}">
						<td>
							<p>No graffiti currently available</p>
						</td>
					</c:when>
					<%--0 graffiti in Herculaneum property --%>
					<c:otherwise>
						<td>
							<p>No graffiti in property</p>
						</td>
					</c:otherwise>
				</c:choose>
			</tr>
			<c:set var="hasLinks" value="${not empty prop.propertyLinks}" />
			<c:if test="${hasLinks }">
				<tr>
					<th class="propertyLabel">Archaeological Context:</th>
					<td><c:forEach var="l" items="${prop.propertyLinks}">
							<a href="${l.link }">${l.linkName}</a>
							<br />
						</c:forEach></td>
				</tr>
			</c:if>

		</table>

		<c:if test="${prop.insula.modernCity == 'Pompeii'}">
			<div id="map">
				<div id="pompeiimap" class="propertyMetadataMap"></div>
			</div>
		</c:if>
		<c:if test="${prop.insula.modernCity == 'Herculaneum'}">
			<div id="map">
				<div id="herculaneummap" class="propertyMetadataMap"></div>
			</div>
		</c:if>

		
		<c:if test="${prop.insula.modernCity == 'Stabiae'}">
			<div id="map">
				<div id="stabiaemap" class="propertyMetadataMap">
					<img src = "https://my.wlu.edu/Images/communications/publications/graphic-identity/300-dpi-symbol.jpg" alt = "Stabiae placeholder image." >
				</div>
			</div>
		</c:if>

	</div>

	<%@include file="/WEB-INF/jsp/footer.jsp"%>

	<script type="text/javascript">
		if ("${prop.insula.city.name}" == "Herculaneum") {
			window.initHerculaneumMap("property", false, false, false,
					"${prop.id}", [], false, false, false, true);
		} else if ("${prop.insula.city.name}" == "Pompeii") {
			window.initPompeiiMap("property", false, false, false,
							"${prop.id}");
		}
	</script>
</body>
</html>