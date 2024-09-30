
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
	
			<h1>${seg.displayName} Section of ${seg.street.streetName}</h1>	

		<div class="button_bar">
			<a
				href="<%=request.getContextPath() %>/streets/sections/"
				id="allsegs"><button class="btn btn-agp right-align">View All
				Street Sections</button></a>
			
			<a
				href="<%=request.getContextPath() %>/streets/${seg.uri}/json"
				id="json"><button class="btn btn-agp right-align btn-export" style="margin-right: 5px;">Export
					JSON Data</button></a>
		</div>

		<table class="property-table table table-striped table-bordered" style="max-width: 42%;">
			<tr>
				<th class="propertyLabel">City:</th>
				<td><a
					href="http://pleiades.stoa.org/places/${seg.street.city.pleiadesId}">${seg.street.city.name}</a></td>
			</tr>
			<tr>
				<th class="propertyLabel">Street:</th>
				<td>
					<a href="<%=request.getContextPath()%>/streets/${seg.street.uri}">${seg.street.streetName}</a>
				</td>
			</tr>
			<tr>
				<th class="propertyLabel">Section:</th>
				<td>${seg.segmentName}</td>
			</tr>
			
			<tr>
					<th class="propertyLabel">Inscriptions:</th>
					<td>
						<a href="<%=request.getContextPath()%>/results?segment=${seg.id}">
						Graffiti on this section of ${seg.street.streetName}</a>
					</td>
			</tr>
<!-- 			<tr> -->
<!-- 				<th class="propertyLabel">Archaeological Context:</th> -->
<!-- 				<td> -->
<!-- 				</td> -->
<!-- 			</tr> -->
		</table>
		<div id="map">
			<div id="herculaneummap" class="propertyMetadataMap"></div>
			<div id="pompeiimap" class="propertyMetadataMap"></div>
			<div id="stabiaemap" class="propertyMetadataMap">
				<img src = "https://my.wlu.edu/Images/communications/publications/graphic-identity/300-dpi-symbol.jpg" alt = "Stabiae placeholder image." >
			</div>	
		</div>
		
	</div>
	
	<%@include file="/WEB-INF/jsp/footer.jsp"%>

	<script type="text/javascript">
 	if("${seg.street.city.name}"=="Herculaneum"){
		window.initHerculaneumMap("property",false,false,false,"${seg.id}",[],false,false,false,true);
 	}
 	else if("${seg.street.city.name}"=="Pompeii"){
 		window.initPompeiiMap("insula",false,false,false,"${seg.id}");
 	}
	</script>
	
</body>
</html>