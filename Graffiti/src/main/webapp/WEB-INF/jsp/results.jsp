<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<fmt:setBundle basename="messages"/>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Ancient Graffiti Project :: Search Results</title>
<%@ include file="/resources/common_head.txt"%>
<%@ include file="/resources/leaflet_common.txt"%>
<script type="text/javascript"
	src="<%=request.getContextPath()%>/resources/js/filterSearch.js"></script>
<link rel="stylesheet"
	href="<%=request.getContextPath()%>/resources/css/main.css" />
<%@ page import="java.util.List,java.util.ArrayList"%>

<!-- @author connor lehman -->

<!-- number of results  -->
<c:set var="num" value="${requestScope.totalResults}" />

<script type="text/javascript">
var locationKeys; 

function setLocationKeys(){
	<%List<Integer> locationKeys = (List<Integer>) request.getAttribute("findLocationKeys");
if (locationKeys == null) {
	locationKeys = new ArrayList();
}
List<Integer> insulaLocations = (List<Integer>) request.getAttribute("insulaLocationKeys");
if (insulaLocations == null) {
	insulaLocations = new ArrayList();
}
locationKeys.addAll(insulaLocations);%>
	locationKeys = <%=locationKeys%>;
}

function selectImg(ind, k, page, thumbnail, image){
	document.getElementById("imgLink"+k).href = page;
	document.getElementById("imgSrc"+k).src = image;
}

function printResults() {
	var labels = document.getElementsByClassName("search-term-label");
	newUrl = createURL("print");
	window.open(newUrl, '_blank');
}

function checkAlreadyClicked(ids){
	idList = ids.split(";");
	for (var i = 0; i < idList.length-1; i++){
		$("#"+idList[i]).click();
	}
}

function checkboxesAfterBack() {
	contentsUrl = window.location.href;
	if(contentsUrl.split("?")[1]) {
		var params = contentsUrl.split("?")[1].split("&");
		
		var categoryToAbbreviation = {
				"drawing_category" : "dc",
				"property" : "p",
				"property_type" : "pt",
				"insula" : "i",
				"city" : "c",
				"writing_style" : "ws",
				"language" : "l",
				"poetry" : "po", 
				"street": "str",
				"segment":"seg",
		};
		
		var cities = {
				"Herculaneum" : 0,
				"Pompeii" : 1,
				"Smyrna" : 2
		};
		
		var writingStyle = {
			"Graffito/incised" : 1,
			"charcoal" : 2,
			"other" : 3
		};

		var languages = {
			"Latin" : 1,
			"Greek" : 2,
			"Latin/Greek" : 3,
			"other" : 4
		};
		
		for (var i in params){
			if (params[i] != "query_all=false"){
				var param = params[i];
				var term = param.split("=");
				var category = term[0];
				var value = term[1];
				
				//console.log(param);
				
				if (category == "drawing_category" && value == "All") {
					value = 0;
				}
				if (category in categoryToAbbreviation) {
					var categoryToken = categoryToAbbreviation[category];
					// convert the human-readable description into IDs for checkboxes
					if (categoryToken == "ws") {
						value = writingStyle[value];
					} else if (categoryToken == "c") {
						value = cities[value];
					} else if (categoryToken == "l") {
						value = languages[value];
					} else if (categoryToken == "dc" && value == 0) {
						var id = categoryToken + value;
						$("#" + id).click();
					} 
					else {
						// click the button representing this element
						// need to do this way because we don't have the other
						// data/information to create the buttons.						
						var id = categoryToken + value;
						$("#" + id).click();
					}
				}
				else if (category == "content") {
					addSearchTerm("Content", value, value);
				} else if (category == "cil"){
					addSearchTerm("CIL", value, value);
				}
				else if (category == "global") {
					addSearchTerm("Global", value, value);
				}
					
			}
		}
		refineResults("filter");
	}
}
	
	// Controls the display of the Return to Top Button
	window.onscroll = function(){scrollFunction()};
	function scrollFunction() {
		if (( document.body.scrollTop > screen.height/2 ) || (document.documentElement.scrollTop > screen.height/2)) {
		    document.getElementById("scroll_top").style.display = "inline";
		} else {
			document.getElementById("scroll_top").style.display = "none";
		}
	}
</script>
<style>
th {
	vertical-align: top;
	width: 170px;
}

.main-table {
	width: 100%;
}

hr.main-table {
	margin-left: 0px;
}

#scroll_top {
	display: none;
	float: right;
	position: fixed;
	color: white;
	cursor: pointer;
	bottom: 20px;
	right: 30px;
	z-index: 1;
}

#herculaneummap, #pompeiimap, #stabiaemap {
	z-index: 2;
}

.btn-agp {
	margin-bottom: 0px;
}

ul#searchTerms li {
	display: inline-block;
}

img {
	image-orientation: from-image;
}

#sortParam {
	max-width: 110px;
}

#dropdownMap {
	margin-top: 10px;
	}
</style>
</head>
<body>
	<%@include file="header.jsp"%>

	<div class="container-fluid">
      <div class="row">
			<%--Check for error message informing user of invalid city name or inscription id --%>
			<c:if test="${not empty requestScope.error}">
				<p style="color:indianred; margin-bottom: 30px;margin-left: 250px;margin-top: 20px;">${requestScope.error}</p>
			</c:if>
			<div class="col-md-2">
				<%@include file="sidebarSearchMenu.jsp"%>
        	</div>
			<!--  Right Column -->
		<!-- Only render the maps if there are results  -->
			<c:if test="${num != 0}">
			<div id="dropdownMap" class="col-md-7" style="height: 100%;" >
				<nav class="navbar navbar-expand-lg navbar-light ">
					<button class="navbar-toggler" type="button" data-bs-toggle="collapse"
						data-bs-target="#dropdownMap" aria-controls="dropdownMap"
						aria-expanded="false" aria-label="Toggle navigation"
						style="width: 100%; border: none; text-align: left" id="navbar-collapser">
						<span class="navbar-toggler-icon"></span>	
                		<span class="visible-xs-inline">Map</span>
					</button>

					
	 				<div class="collapse navbar-collapse" id="dropdownMap">
						<div id="herculaneummap" class="searchResultsHerculaneum" style="margin-left: 0px;"></div>
						<div id="pompeiimap" class="searchResultsPompeii" style="margin-right: 150px;"></div>
				<!-- 	<div id="stabiaemap" class="searchResultsStabiae"> 
							<img src = "https://my.wlu.edu/Images/communications/publications/graphic-identity/300-dpi-symbol.jpg" alt = "Logo"></div> -->
					</div>
				</nav>
			</div>
			</c:if>
	</div>		
	<div class="row">
			<a href="#top" id="scroll_top" class="btn btn-agp">Return To Top</a>
	
			<div class="col-md-12">
				<div style="width: 80%;">
					<ul id="searchTerms" style="width: 100%; margin-left: -40px;"></ul>
				</div>
				<div id="search-results">
					<%@include file="filter.jsp"%>
				</div>
			</div>
		</div>
	</div>

	<%@include file="footer.jsp"%>`
	
	<script>
		setLocationKeys();
		//These need to be used in same order as they are in div.
		window.initHerculaneumMap("regio", false, false, false, 0, locationKeys);
		window.initPompeiiMap("regio", false, false, false, 0, locationKeys);
		
		$(document).ready(function() {
			$('[data-toggle="tooltip"]').tooltip();
		});
		
		$(function () {
			$('[data-toggle="popover"]').popover()
		});
	</script>
</body>
</html>