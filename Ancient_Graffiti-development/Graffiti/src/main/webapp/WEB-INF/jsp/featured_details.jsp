<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Ancient Graffiti Project :: Graffito Information</title>
<%@ include file="/resources/common_head.txt"%>
<%@ include file="/resources/leaflet_common.txt"%>
<link rel="stylesheet" type="text/css"
	href="<%=request.getContextPath()%>/resources/css/details.css" />
<link rel="stylesheet"
	href="<%=request.getContextPath()%>/resources/css/main.css" />

<style type="text/css">
#epigraphic_conventions {
	text-align: center;
}

.convention-header {
	float: right;
	max-width: 450px;
	font-size: 12pt;
	display: none;
}

#ep_con_link:link, #ep_con_link:visited, #ep_con_link:active {
	color: maroon;
	font-size: 18px;
	text-decoration: none;
}

#ep_con_link:hover {
	color: #D34444;
	text-decoration: underline;
}

#second_ep_con_link:link, #second_ep_con_link:visited,
	#second_ep_con_link:active {
	color: grey;
	font-size: 12px;
	text-decoration: none;
}

#second_ep_con_link:hover {
	color: maroon;
	text-decoration: underline;
}

#convention_table {
	display: inline;
	margin-top: 20px;
	max-width: 70px;
	font-size: 10pt;
	border: 1px;
}

#convention_table th {
	text-align: center;
}

#sym {
	text-align: center;
	color: maroon;
	width: 30%;
	padding: 5px;
}

#def {
	text-align: center;
	color: black;
	width: 70%;
	padding: 2px;
}

h4 {
	float: right;
	position: relative;
	margin-bottom: 10px;
	margin-top: 10px;
	margin-right: 78%;
}

.btn-agp {
	margin-bottom: 10px;
}

.arrow {
  border: solid black;
  border-width: 0 3px 3px 0;
  display: inline-block;
  padding: 3px;
  cursor: pointer;
}

img {
	image-orientation: from-image;
}
</style>
<script type="text/javascript">

function displayConventions() { 
	document.getElementById("epigraphic_conventions").style.display = 'inline';
 	document.getElementById("hideConvBtn").style.display = 'inline';
 	document.getElementById("showConvBtn").style.display = 'none';
}

function hideConventions() {
	document.getElementById("epigraphic_conventions").style.display = 'none';
 	document.getElementById("hideConvBtn").style.display = 'none';
 	document.getElementById("showConvBtn").style.display = 'inline';
}

function backToResults(){
	xmlHttp = new XMLHttpRequest();
	xmlHttp.open("GET", "<%=request.getContextPath()%>/backToResults?edr=" + "${inscription.graffitiId}", false);
	xmlHttp.send(null);
	var url = "${sessionScope.returnURL}";
	if(url.includes("filter")) {
		url = url.replace("filter", "results"); // generate the results page--makes sure the page is formatted
	} else if(url.includes("print")) {
		url = url.replace("print", "results");
	}
	window.location.href = url;
}

</script>
</head>
<body>
	<%@include file="header.jsp"%>

	<c:set var="i" value="${requestScope.inscription}" />
	<c:set var="notations" value="${requestScope.notations}" />

	<div class="button_bar">
		<button class="btn btn-agp" onclick="backToResults();">Back
			to Results</button>
	</div>
	
	<div class="container">
		<div class="top-div">
			<h3>Graffito</h3>
		</div>

		<div class="leftcol">

			<div class="detail-body">
			
				<div class="graffiti_content">
					<p class="lead">${i.contentWithLineBreaks}</p>


					<button class="btn btn-agp" id="hideConvBtn"
						onclick="hideConventions()" style="display:none;">Hide Epigraphic Convention
						Key</button>
						
					<c:choose>
					<c:when test="${notations.size() == 0}">
						<button class="btn btn-agp" id="showConvBtn"
							onclick="displayConventions()"
							style="display:none;">Show Epigraphic Convention
							Key</button>
					</c:when>
					<c:otherwise>
						<button class="btn btn-agp" id="showConvBtn"
							onclick="displayConventions()">Show Epigraphic Convention
							Key</button>
						<br />
					</c:otherwise>
					</c:choose>
					
					<%@include file="convention_key.jsp"%>

				</div>

				<table class="table table-striped table-bordered">
					<tr>
						<th class="propertyLabel">Translation:</th>
						<td>${i.contentTranslation}</td>
					</tr>
					<tr>
						<th class="propertyLabel">CIL:</th>
						<td>${i.cil}</td>
					</tr>
					<tr>
						<th class="propertyLabel">Link to EDR:</th>
						<td><a href="${i.pagePath}">#${i.graffitiId}</a></td>
					</tr>
				</table>
			</div>
		</div>
	</div>
	<%@include file="footer.jsp"%>
</body>
</html>