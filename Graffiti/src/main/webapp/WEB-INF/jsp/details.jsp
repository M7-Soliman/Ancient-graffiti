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

function selectImg(ind) {
	var hrefs = "${requestScope.imagePages}".slice(1,-1).split(','); //turns string of urls into array
	var srcs = "${requestScope.images}".slice(1,-1).split(',');
	document.getElementById("imgLink").href = hrefs[ind-1];
	document.getElementById("imgSrc").src = srcs[ind-1];
	document.getElementById(ind).checked = true;
	current = ind;
}

var current = 1;
var count = "${requestScope.images}".split(',').length;

function prevImg(){
	if (current == 1){
		current = count;
	}
	else{
		current -= 1;
	}
	selectImg(current);
}

function nextImg(){
	if (current == count){
		current = 1;
	}
	else{
		current += 1;
	}
	selectImg(current);
}
</script>
</head>
<body>
	<%@include file="header.jsp"%>

	<c:set var="i" value="${requestScope.inscription}" />
	<c:set var="notations" value="${requestScope.notations}" />
	<nav class="button_bar">
		<button class="btn btn-agp" onclick="backToResults();">Back
			to Results</button>
		<a href="<%=request.getContextPath() %>/graffito/${i.agpId}/csv"
			id="csv">
			<button class="btn btn-agp right-align btn-export">Export as CSV</button>
		</a><a href="<%=request.getContextPath() %>/graffito/${i.agpId}/json"
			id="json">
			<button class="btn btn-agp right-align btn-export">Export as JSON</button>
		</a> <a href="<%=request.getContextPath() %>/graffito/${i.agpId}/xml"
			id="xml">
			<button class="btn btn-agp right-align btn-export">Export as EpiDoc</button>
		</a>
	</nav>
	<main class="container">
		<div class="row">
			<div class="col-lg">
			<div id="graffito_summary">
			<!-- sets the title of graffito -->
			<c:choose>
				<c:when test="${not empty i.caption}">
					<c:set var="summary" value="${i.caption}" />
				</c:when>
				<c:otherwise>
					<c:set var="summary" value="Graffito" />
				</c:otherwise>
			</c:choose>
			<h3>${summary}</h3>
		</div>
				<c:choose>
					<c:when test="${not empty i.ancientCity}">
						<c:set var="city" value="${i.ancientCity}" />
					</c:when>
					<c:otherwise>
						<c:set var="city" value="City Unavailable" />
					</c:otherwise>
				</c:choose>
				<c:choose>
					<c:when test="${not empty i.property.propertyName}">
						<c:set var="findspot" value="${i.property.propertyName}" />
					</c:when>
					<c:otherwise>
						<c:set var="findspot" value="" />
					</c:otherwise>
				</c:choose>
				<div class="graffiti_content">
					<c:choose>
						<c:when test="${not empty i.contentWithLineBreaks}">
							<p class="lead">${i.contentWithLineBreaks}</p>
						</c:when>
						<c:otherwise>
							<p class="lead">&lt;:textus non legitur&gt;</p>
						</c:otherwise>
					</c:choose>

					<button class="btn btn-agp" id="hideConvBtn"
						onclick="hideConventions()" style="display: none;">Hide
						Epigraphic Convention Key</button>

					<c:choose>
						<c:when test="${notations.size() == 0}">
							<button class="btn btn-agp" id="showConvBtn"
								onclick="displayConventions()" style="display: none;">Show
								Epigraphic Convention Key</button>
						</c:when>
						<c:otherwise>
							<button class="btn btn-agp" id="showConvBtn"
								onclick="displayConventions()">Show Epigraphic
								Convention Key</button>
							<br />
						</c:otherwise>
					</c:choose>
					<%@include file="convention_key.jsp"%>
				</div>
				<div class="table-responsive">
				<table class="property-table table table-striped table-bordered">
					<c:if test="${not empty i.contentTranslation}">
						<tr>
							<th class="propertyLabel">Translation:</th>
							<td>${i.contentTranslation}</td>
						</tr>
					</c:if>
					<c:if test="${not empty i.figuralInfo.descriptionInEnglish}">
						<tr>
							<th class="propertyLabel">Description of Drawing (English):</th>
							<td>${i.figuralInfo.descriptionInEnglish}</td>
						</tr>
					</c:if>
					<tr>
						<th class="propertyLabel">Findspot:</th>
						<td>
						<c:choose>
						<c:when test="${i.findSpotPropertyID == 0 and i.property.propertyName == 'Not found' and i.preciseLocation==null}"> 
							<c:if test="${ i.ancientCity == 'Herculaneum' }">
								<a href="http://pleiades.stoa.org/places/432873">${city}</a>,
								locus incertus
							</c:if>
							<c:if test="${i.ancientCity == 'Stabiae' and i.sourceFindSpot.contains('Ager Stabianus')}">
								<a href="http://pleiades.stoa.org/places/433128">${city}</a>,
								Ager Stabianus (locus incertus)
							</c:if>
							<c:if test="${i.ancientCity == 'Stabiae' and i.sourceFindSpot.contains('Arianna?')}">
								<a href="http://pleiades.stoa.org/places/433128">${city}</a>,
								locus incertus (Villa Arianna?)
							</c:if>
							
						</c:when>
						<c:otherwise>
						<c:choose>
								<c:when test="${i.onFacade}">
									<a
										href="http://pleiades.stoa.org/places/${i.pleiadesId}">${city}</a>,
								<a
										href="<%=request.getContextPath() %>/results?street=${i.segment.street.id}">
										${i.segment.street.streetName}</a>
									<a
										href="<%=request.getContextPath() %>/results?segment=${i.segment.id}">
										(${i.segment.segmentName})</a>
								</c:when>
								<c:otherwise>
									<a
										href="http://pleiades.stoa.org/places/${i.pleiadesId}">${city}</a>, ${findspot}
								<a
										href="<%=request.getContextPath() %>/results?property=${i.property.id}">
										(${i.property.insula.shortName}.${i.property.propertyNumber})</a>

								</c:otherwise>
								</c:choose>
								</c:otherwise>
							</c:choose> <br /> <c:choose>
								<c:when test="${i.onFacade}">
									<div class="locationMetadataDiv">
									<a  class="learnMore"
										href="<%=request.getContextPath() %>/streets/${i.segment.uri}">Location
										metadata &#10140;</a>
									</div>
								</c:when>
								<c:otherwise>
									<c:if
										test="${not i.property.propertyNumber.isEmpty() and ! i.property.propertyNumber.equals('0')}">
										<div class="locationMetadataDiv">
										<a  class="learnMore"
											href="<%=request.getContextPath() %>/properties/${i.property.insula.city.name}/${i.property.insula.shortName}/${i.property.propertyNumber}">Location
											metadata &#10140;</a>
										</div>
									</c:if>
								</c:otherwise>
							</c:choose></td>
					</tr>
					<!-- Add additional findspot information if it's available -->
					<c:if
						test="${i.preciseLocation != null && i.preciseLocation != ''}">
						<tr>
							<th class="propertyLabel">Precise Location:</th>
							<td>${i.preciseLocation}</td>
						</tr>
					</c:if>
					<!-- defining key words -->
					<c:if test="${i.figuralInfo.getDrawingTags().size() > 0}">
						<tr>
							<c:choose>
								<c:when test="${i.figuralInfo.getDrawingTags().size() == 1}">
									<th class="propertyLabel">Drawing Category:</th>
								</c:when>
								<c:otherwise>
									<th class="propertyLabel">Drawing Categories:</th>
								</c:otherwise>
							</c:choose>
							<td><c:forEach var="dt"
									items="${i.figuralInfo.getDrawingTags()}"
									varStatus="loopStatus">
									<a
										href="<%=request.getContextPath()%>/results?drawing_category=${dt.id}">${dt.name}</a>
									<c:if test="${!loopStatus.last}">, </c:if>
								</c:forEach></td>
						</tr>
					</c:if>

					<!--  add keywords here, when controlled vocabulary is working -->

					<c:if test="${not empty i.languageInEnglish}">
						<tr>
							<th class="propertyLabel">Language:</th>
							<td>${i.languageInEnglish}</td>
						</tr>
					</c:if>
					<c:if test="${not empty i.writingStyleInEnglish}">
						<tr>
							<th class="propertyLabel">Writing Style:</th>
							<td>${i.writingStyleInEnglish}</td>
						</tr>
					</c:if>
							<!-- Print results for poetry graffito -->
					<c:if test="${not empty i.getMeter()}">
						<tr>
							<th class="propertyLabel">Meter Type:</th>
							<td>${i.getMeter()}</td>

						<tr>
							<th class="propertyLabel">Meter Type:</th>
							<td>Placeholder Meter</td>
						</tr>
					</c:if> 
					
					<c:if
						test="${not empty i.graffitoHeight || not empty i.graffitoLength || 
					not empty i.heightFromGround || not empty i.minLetterHeight || not empty i.maxLetterHeight 
					|| not empty i.minLetterWithFlourishesHeight || not empty i.maxLetterWithFlourishesHeight}">
						<tr>
							<th class="propertyLabel"><input type="button"
								id="showMeasure" class="btn btn-agp" value="Show Measurements"></th>
							<td>
								<div id="measurements">
									<ul>
										<c:if test="${not empty i.graffitoHeight }">
											<li>Graffito Height: ${ i.graffitoHeight }</li>
										</c:if>
										<c:if test="${not empty i.graffitoLength }">
											<li>Graffito Length: ${i.graffitoLength }</li>
										</c:if>
										<c:if test="${not empty i.heightFromGround }">
											<li>Height from Ground: ${i.heightFromGround }</li>
										</c:if>
										<c:if test="${not empty i.minLetterHeight }">
											<li>Min Letter Height: ${i.minLetterHeight}</li>
										</c:if>
										<c:if test="${not empty i.maxLetterHeight }">
											<li>Max Letter Height: ${i.maxLetterHeight }</li>
										</c:if>
										<c:if test="${not empty i.minLetterWithFlourishesHeight }">
											<li>Min Letter Height with Flourishes:
												${i.minLetterWithFlourishesHeight }</li>
										</c:if>
										<c:if test="${not empty i.maxLetterWithFlourishesHeight }">
											<li>Max Letter Height with Flourishes:
												${i.maxLetterWithFlourishesHeight }</li>
										</c:if>
									</ul>
								</div>
							</td>
						</tr>
					</c:if>
					<c:if test="${not empty i.apparatusDisplay}">
						<tr>
							<th class="propertyLabel">Apparatus Criticus:</th>
							<td id="apparatus">${i.apparatusDisplay}</td>
						</tr>
					</c:if>
					<c:if test="${not empty i.bibliography}">
						<tr>
							<th class="propertyLabel">Bibliography:</th>
							<td>${i.bibliography}</td>
						</tr>
					</c:if>
					<c:if test="${i.ancientCity != 'Smyrna'}">
						<tr>
							<th class="propertyLabel">Link to EDR:</th>
							<td><a href="${i.pagePath}">#${i.graffitiId}</a></td>
						</tr>
					</c:if>
					<c:if test="${not empty i.commentary}">
						<tr>
							<th class="propertyLabel">Commentary:</th>
							<td>${i.commentary}</td>
						</tr>
					</c:if>
					<tr>
						<th class="propertyLabel">Suggested Citation:</th>
						<td>${i.citation}</td>
					</tr>
					<c:if test="${not empty i.editor}">
						<tr>
							<th class="propertyLabel">Contributions:</th>
							<td>
								<p>
									<strong>Editor:</strong> ${i.editor}
								</p> <c:choose>
									<c:when test="${fn:contains(i.principleContributors, ',')}">
										<p>
											<strong>Principal Contributors:</strong>
											${i.principleContributors}
										</p>
									</c:when>
									<c:otherwise>
										<p>
											<strong>Principal Contributor:</strong>
											${i.principleContributors}
										</p>
									</c:otherwise>
								</c:choose> <c:if test="${not empty i.contributors}">
									<c:choose>
										<c:when test="${fn:contains(i.contributors, ',')}">
											<p>
												<strong>Contributors:</strong> ${i.contributors}
											</p>
										</c:when>
										<c:otherwise>
											<p>
												<strong>Contributor:</strong> ${i.contributors}
											</p>
										</c:otherwise>
									</c:choose>
								</c:if>
								<p>
									<strong>Last Revision:</strong> ${i.lastRevision}
								</p>
							</td>
						</tr>
					</c:if>
				</table>
				</div>
				<%
				if (session.getAttribute("authenticated") != null) {
				%>
				<form action="<%=request.getContextPath()%>/admin/updateGraffito">
					<input class="btn btn-agp" type=submit value="Edit Graffito"><input
						type="hidden" name="graffitiID" value="${i.graffitiId}" />
				</form>
				<%
				}
				%>
			</div>
			<div class="col-lg">
				<c:set var="len" value="${fn:length(i.photos)}" />
				<c:choose>
					<c:when test="${len == 1}">
						<div class="image_display">
							<a target="_blank" href="${i.pages[0]}" id="imgLink"> <img
								src="${i.images[0]}" id="imgSrc" />
							</a>
						</div>
					</c:when>
					<c:when test="${len gt 1}">
						<div id="trifold" class="flex-container"
							style="display: flex; column-count: 3; text-align: justify; align-items: center;">
							<div class="image_display">
								<a target="_blank" href="${i.pages[0]}" id="imgLink"> <img
									src="${i.images[0]}" id="imgSrc" />
								</a>
							</div>
						</div>

						<table class="buttons">
							<tr>
								<!-- First Image -->
								<td class="thumb-holder"><input type="radio" name="image"
									onclick="selectImg(1);" id="1" checked="checked" /> <label
									for="1" class="imageLabel"> <img
										src="${i.thumbnails[0]}" class="thumb" />
								</label></td>
								<%
								int counter = 1;
								%>
								<!--  Every Other Image -->
								<c:forEach var="k" begin="${2}" end="${len}">
									<%if (counter % 4 == 0) {%>
								
							</tr>
							<tr>
								<%}%>
								<td class="thumb-holder"><input type="radio" name="image"
									onclick="selectImg(${k});" id="${k}" /> <label for="${k}"
									class="imageLabel"> <img src="${i.thumbnails[k-1]}"
										class="thumb" />
								</label></td>
								<%counter = counter + 1;%>
								</c:forEach>
							</tr>
						</table>
					</c:when>
				</c:choose>
				<c:if test="${i.ancientCity == 'Herculaneum' or i.ancientCity == 'Pompeii'}">
					<div id="maps">
						<h4>Findspot:</h4>
						<div id="pompeiimap" class="findspotMap"></div>
						<div id="herculaneummap" class="findspotMap"></div>
						
					</div>
				</c:if>
				
				<c:if test="${i.ancientCity == 'Stabiae'}">
					<div id="maps">
						<h4>Findspot:</h4>
						<div id="stabiaemap" class="findspotMap"> 
							<img src = "https://my.wlu.edu/Images/communications/publications/graphic-identity/300-dpi-symbol.jpg" alt = "Logo">
						</div>
						
					</div>
				</c:if>
				
				
				
				
			</div>
		</div>
	</main>
	<%@include file="footer.jsp"%>
	<c:choose>
		<c:when test="${i.onFacade}">
			<script type="text/javascript">
					if("${i.segment.street.city.name}"=="Herculaneum"){
						window.initHerculaneumMap("property",false,false,false,"${i.segment.id}",[],false,false,false,true);
					}
					else if("${i.segment.street.city.name}"=="Pompeii"){
						window.initPompeiiMap("large",false,false,false,"${i.segment.id}");
					}
					
				</script>
		</c:when>
		<c:otherwise>
			<script type="text/javascript">
					if ("${i.ancientCity}" == "Pompeii"){
						window.initPompeiiMap("property",false,false,false,<c:out value = "${i.property.id}"/>,[],true);
					}
					else{
						window.initHerculaneumMap("property",false,false,false,<c:out value = "${i.property.id}"/>,[],true);
					}		
				</script>
		</c:otherwise>
	</c:choose>
	<script>
	$(document).keydown(function(e) {;
	   if (e.which == 37){
		   prevImg();
	   }
	   if (e.which == 39) {
		   nextImg();
	   }
	});

	//Function to hide measurements on load:
	//Note: putting these in the head can cause problems, including endless loading
	$(document).ready(function() {
		$("#measurements").hide();
		});
	
	//Do NOT delete the commented out code in this function. It is for the Show/Hide
	//measurements button and will be re-implemented when we have the data that we need.  
	
	//Toggles Measurements to hide and show the text
	$(document).ready(function() {
		$("#showMeasure").click(function(){
			var button = $(this);
			if (button.val() == "Show Measurements"){
				button.val("Hide Measurements");
				$("#measurements").show();
			}else{
				button.val("Show Measurements");
				$("#measurements").hide();
			}
			});
		});
	</script>
</body>
</html>