<%@ page language="java" contentType="text/html; charset=US-ASCII"
	pageEncoding="US-ASCII"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<!DOCTYPE html>
<html lang="en">
<head>
<%@include file="/resources/common_head.txt"%>
<meta http-equiv="Content-Type" content="text/html; charset=US-ASCII">
<title>Ancient Graffiti Project ::  List of Properties</title>
<link rel="stylesheet" type="text/css"
	href="<%=request.getContextPath()%>/resources/css/details.css" />
<script type="text/javascript"
	src="<%=request.getContextPath()%>/resources/js/filterSearch.js"/>"></script>


<style>
th {
	font-weight: bold;
	color: maroon;
}

.fixed {
	position: fixed;
	top: 100px;
	width: auto;
	display: none;
	border: none;
	background-color: white;
}

a {
    text-decoration: none;
}

</style>
</head>
<body>
	<%@include file="/WEB-INF/jsp/header.jsp"%>
	<div class="container">
		<%
		if (request.getAttribute("herculaneumProperties") != null) {
			// only add the Jump to Pompeii button if both cities are being displayed
			if(request.getAttribute("pompeiiProperties") != null) {
			%>
				<a href="#pompeii">
					<button class="btn btn-agp scroll_bottom"> Pompeii </button>
				</a>
			<% } 
			%>
			<%
			if (request.getAttribute("stabiaeProperties") != null) {
			%>
			<a href="#stabiae">
					<button class="btn btn-agp scroll_bottom">Stabiae</button>
				</a>
			<% } 
			%>
			<%
				if (request.getAttribute("filterByInsula") != null) {
			%>
			<h1>Herculaneum Insula <%=request.getAttribute("filterByInsula")%> Properties</h1>
			<div class="button_bar">
			<a
				href="<%=request.getContextPath()%>/properties/Herculaneum/<%=request.getAttribute("filterByInsula")%>/csv"
				id="herculaneumCSV">
				<button class="btn btn-agp right-align btn-export">Export as CSV</button>
			</a> <a
				href="<%=request.getContextPath()%>/properties/Herculaneum/<%=request.getAttribute("filterByInsula")%>/json"
				id="herculaneumJSON">
				<button class="btn btn-agp right-align btn-export">Export as JSON</button>
			</a>
			</div>
			<%
				} else {
			%>
			<h1>Herculaneum Properties</h1>
			<div class="button_bar">
			<a href="<%=request.getContextPath()%>/properties/Herculaneum/csv"
				id="herculaneumCSV">
				<button class="btn btn-agp right-align btn-export">Export as CSV</button>
			</a> <a href="<%=request.getContextPath()%>/properties/Herculaneum/json"
				id="herculaneumJSON">
				<button class="btn btn-agp right-align btn-export">Export as JSON</button>
			</a>
			</div>
			<%
				}
			%>
		<div class="table-responsive">
		<table class="table table-bordered table-striped" id="herculaneumTable"
				style="margin-bottom: 30px;">
			<thead>
				<tr>
					<%
					if (request.getAttribute("filterByInsula") == null) {
					%>
					<th>Insula</th>
					<% } %>
					<th>Number</th>
					<th>Name</th>
					<th>Type</th>
					<th>URI</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach var="m" begin="${1}"
					end="${fn:length(requestScope.herculaneumProperties)}">
					<c:set var="prop" value="${requestScope.herculaneumProperties[m-1]}" />
					<tr>
						<%
							if (request.getAttribute("filterByInsula") == null) {
						%>
						<td>${prop.insula.shortName}</td>
						<% } %>
						<td>${prop.propertyNumber}</td>
						<td>${prop.propertyName}</td>
						<td>${prop.propertyTypesAsString}</td>
						<td><a href="<%=request.getContextPath()%>/properties/${prop.uri}">
							http://ancientgraffiti.org/Graffiti/properties/${prop.uri}</a></td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
		</div>
		<%
		}	
		%>

		<%
			if (request.getAttribute("pompeiiProperties") != null) {
		%>
			<%
				if (request.getAttribute("filterByInsula") != null) {
			%>
			<h2 id="pompeii">Pompeii Insula <%=request.getAttribute("filterByInsula")%> Properties - in development</h2>
			<div class="button_bar">
			
			<a
				href="<%=request.getContextPath()%>/properties/Pompeii/<%=request.getAttribute("filterByInsula")%>/csv"
				id="pompeiiCSV">
				<button class="btn btn-agp right-align btn-export">Export as CSV</button>
			</a> <a
				href="<%=request.getContextPath()%>/properties/Pompeii/<%=request.getAttribute("filterByInsula")%>/json"
				id="pompeiiJSON">
				<button class="btn btn-agp right-align btn-export">Export as JSON</button>
			</a>
			</div>
			<%
				} else {
			%>
			<h2 id="pompeii">Pompeii Properties - in development</h2>
			<div class="button_bar">
			<a href="<%=request.getContextPath()%>/properties/Pompeii/csv"
				id="pompeiiCSV">
				<button class="btn btn-agp right-align btn-export">Export as CSV</button>
			</a> <a href="<%=request.getContextPath()%>/properties/Pompeii/json"
				id="pompeiiJSON">
				<button class="btn btn-agp right-align btn-export">Export as JSON</button>
			</a>
			</div>
			<%
				}
			%>
		<div class="table-responsive">
		<table class="table table-bordered table-striped" id="pompeiiTable"
			style="margin-bottom: 30px;">
			<thead>
			<tr>
				<%
					if (request.getAttribute("filterByInsula") == null) {
				%>
				<th>Insula</th>
				<% } %>
				<th>Number</th>
				<th>Name</th>
				<th>Type</th>
				<th>URI</th>
			</tr>
			</thead>
			<tbody>
			<c:forEach var="k" begin="${1}"
				end="${fn:length(requestScope.pompeiiProperties)}">
				<c:set var="prop" value="${requestScope.pompeiiProperties[k-1]}" />
				<tr>
					<%
					if (request.getAttribute("filterByInsula") == null) {
					%>
					<td>${prop.insula.shortName}</td>
					<% } %>
					<td>${prop.propertyNumber}</td>
					<td>${prop.propertyName}</td>
					<td>${prop.propertyTypesAsString}</td>
					<td><a href="<%=request.getContextPath()%>/properties/${prop.uri}">
							http://ancientgraffiti.org/Graffiti/properties/${prop.uri}</a></a></td>
				</tr>
			</c:forEach>
			</tbody>
		</table>
		</div>
		<%
			}
		%>
		<!-- Stabiae  -->
		<%
			if (request.getAttribute("stabiaeProperties") != null) {
		%>
			<%
				if (request.getAttribute("filterByInsula") != null) {
			%>
			<h2 id="stabiae">Stabiae Insula <%=request.getAttribute("filterByInsula")%> Properties - in development</h2>
			<div class="button_bar">
			
			<a
				href="<%=request.getContextPath()%>/properties/Stabiae/<%=request.getAttribute("filterByInsula")%>/csv"
				id="stabiaeCSV">
				<button class="btn btn-agp right-align btn-export">Export as CSV</button>
			</a> <a
				href="<%=request.getContextPath()%>/properties/Stabiae/<%=request.getAttribute("filterByInsula")%>/json"
				id="stabiaeJSON">
				<button class="btn btn-agp right-align btn-export">Export as JSON</button>
			</a>
			</div>
			<%
				} else {
			%>
			<h2 id="stabiae">Stabiae Properties - in development</h2>
			<div class="button_bar">
			<a href="<%=request.getContextPath()%>/properties/Stabiae/csv"
				id="stabiaeCSV">
				<button class="btn btn-agp right-align btn-export">Export as CSV</button>
			</a> <a href="<%=request.getContextPath()%>/properties/Stabiae/json"
				id="stabiaeJSON">
				<button class="btn btn-agp right-align btn-export">Export as JSON</button>
			</a>
			</div>
			<%
				}
			%>
		<div class="table-responsive">
		<table class="table table-bordered table-striped" id="stabiaeTable"
			style="margin-bottom: 30px;">
			<thead>
			<tr>
				<%
					if (request.getAttribute("filterByInsula") == null) {
				%>
				<th>Insula</th>
				<% } %>
				<th>Number</th>
				<th>Name</th>
				<th>Type</th>
				<th>URI</th>
			</tr>
			</thead>
			<tbody>
			<c:forEach var="k" begin="${1}"
				end="${fn:length(requestScope.stabiaeProperties)}">
				<c:set var="prop" value="${requestScope.stabiaeProperties[k-1]}" />
				<tr>
					<%
					if (request.getAttribute("filterByInsula") == null) {
					%>
					<td>${prop.insula.shortName}</td>
					<% } %>
					<td>${prop.propertyNumber}</td>
					<td>${prop.propertyName}</td>
					<td>${prop.propertyTypesAsString}</td>
					<td><a href="<%=request.getContextPath()%>/properties/${prop.uri}">
							http://ancientgraffiti.org/Graffiti/properties/${prop.uri}</a></a></td>
				</tr>
			</c:forEach>
			</tbody>
		</table>
		</div>
		<%
			}
		%>
		

	</div>
	<%@include file="/WEB-INF/jsp/footer.jsp"%>
	
	<script type="text/javascript">
// Based on code from https://codepen.io/jgx/pen/wiIGc
;(function($) {
	   $.fn.fixMe = function() {
	      return this.each(function() {
	         var $this = $(this),
	            $t_fixed;
	         function init() {
	            $this.wrap('<div class="propContainer" />');
	            $t_fixed = $this.clone();
	            $t_fixed.find("tbody").remove().end().addClass("fixed").insertBefore($this);
	            resizeFixed();
	         }
	         function resizeFixed() {
	            $t_fixed.find("th").each(function(index) {
	               $(this).css("width",$this.find("th").eq(index).outerWidth()+"px");
	            });
	         }
	         function scrollFixed() {
	            var offset = $(this).scrollTop() + $("#nav").height(),
	            tableOffsetTop = $this.offset().top,
	            tableOffsetBottom = tableOffsetTop + $this.height() - $this.find("thead").height();
	            console.log("offset: " + offset + "\ntableoffsettop: " + tableOffsetTop + "\ntableOffsetBottom: " +  tableOffsetBottom);
	            if(offset < tableOffsetTop || offset > tableOffsetBottom)
	               $t_fixed.hide();
	            else if(offset >= tableOffsetTop && offset <= tableOffsetBottom && $t_fixed.is(":hidden")) {
	           	   $t_fixed.css({"position":"fixed","top":$("#nav").height() + "px", "width":"auto", "display":"none","border":"none"});
	               $t_fixed.show();
	            }
	         }
	         $(window).resize(resizeFixed);
	         $(window).scroll(scrollFixed);
	         init();
	      });
	   };
	})(jQuery);

	$(document).ready(function(){
	   $("table").fixMe();
	});
</script>
</body>
</html>