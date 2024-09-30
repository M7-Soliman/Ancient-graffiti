
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
	<div class="container">
	
			<h1>${str.streetName}</h1>

		<div class="button_bar">
			<a
				href="<%=request.getContextPath() %>/streets/"
				id="allsegs"><button class="btn btn-agp right-align">View All
				Streets</button></a>
			
			<a
				href="<%=request.getContextPath() %>/streets/${seg.uri}/json"
				id="json"><button class="btn btn-agp right-align btn-export" style="margin-right: 5px;">Export
					JSON Data</button></a>
		</div>

		<table class="property-table table table-striped table-bordered" style="max-width: 450px;">
			<tr>
				<th class="propertyLabel">City:</th>
				<td><a
					href="http://pleiades.stoa.org/places/${str.city.pleiadesId}">${str.city.name}</a></td>
			</tr>
			<tr>
					<th class="propertyLabel">Inscriptions:</th>
					<td>
						<a href="<%=request.getContextPath()%>/results?street=${str.id}">
						Graffiti on this street</a>
					</td>
			</tr>
			<c:if test="${fn:length(requestScope.sections) > 0}">
				<tr>
					<th class="propertyLabel">Sections:</th>
					<td>
						<ul>
							<c:forEach var="k" begin="${1}" end="${fn:length(requestScope.sections)}">
							<c:set var="section" value="${requestScope.sections[k-1]}"/>
							<li>
								<a href="<%=request.getContextPath()%>/streets/${section.uri}">${section.segmentName}</a>
							</li>
							</c:forEach>
						</ul>
					</td>
				</tr>
			</c:if>
			
			<c:if test="${str.pompeiiinPicturesURL!=''}">
				<tr>
				<th class="propertyLabel">Archaeological Context:</th>
					<td>
						<a href="${str.pompeiiinPicturesURL}">Pompeii in Pictures</a>
					</td>
				</tr>
			</c:if>
		</table>
		
		<c:if test="${str.city.name == 'Herculaneum' or str.city.name == 'Pompeii'}">
		<div id="map">
			<div id="herculaneummap" class="propertyMetadataMap"></div>
			<div id="pompeiimap" class="propertyMetadataMap"></div>
		</div>
		</c:if>
		
		<c:if test="${str.city.name == 'Stabiae'}">
		<div id="map">
			<div id="stabiaemap" class="propertyMetadataMap">
				<img src = "https://my.wlu.edu/Images/communications/publications/graphic-identity/300-dpi-symbol.jpg" alt = "Stabiae placeholder image." >
			</div>	
		</div>
		</c:if>
		
		
	</div>

	<%@include file="/WEB-INF/jsp/footer.jsp"%>
	
	<script type="text/javascript">
	if("${str.city.name}"=="Herculaneum"){
		window.initHerculaneumMap("property",false,false,false,"${str.id}",[],true);
	}
	else if("${str.city.name}"=="Pompeii"){
		window.initPompeiiMap("insula",false,false,false,"${str.id}");
	}
	</script>
</body>
</html>