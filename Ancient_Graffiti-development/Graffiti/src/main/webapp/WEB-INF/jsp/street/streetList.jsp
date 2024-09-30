<%@ page language="java" contentType="text/html; charset=US-ASCII"
	pageEncoding="US-ASCII"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<!DOCTYPE html>
<html lang="en">
<head>
<%@include file="/resources/common_head.txt"%>
<meta http-equiv="Content-Type" content="text/html; charset=US-ASCII">
<title>Ancient Graffiti Project :: Filter Results</title>
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

</style>
</head>
<body>
	<%@include file="/WEB-INF/jsp/header.jsp"%>
	<div class="container">
		<h1>Streets</h1>
		<a href="<%=request.getContextPath()%>/streets/sections/">See List of Street Sections</a>
		<div class="button_bar">
		<a
			href="<%=request.getContextPath()%>/streets/csv"
				id="strsCSV">
			<button class="btn btn-agp right-align btn-export">Export as CSV</button>
		</a> 
		<a
			href="<%=request.getContextPath()%>/streets/json"
			id="strsJSON">
			<button class="btn btn-agp right-align btn-export">Export as JSON</button>
		</a>
			</div>
		
		<div class="table-responsive">
		<table class="table table-bordered table-striped" id="herculaneumTable"
				style="margin-bottom: 30px;">
			<thead>
				<tr>
					<th>Street</th>
					<th>City</th>
					<th>URI</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach var="m" begin="${1}" end="${fn:length(requestScope.streets)}">
					<c:set var="str" value="${requestScope.streets[m-1]}" />
					<tr>
						<td>${str.streetName}</td>
						<td>${str.city.name}</td>
						<td>
							<a href="<%=request.getContextPath()%>/streets/${str.uri}">
							http://ancientgraffiti.org/Graffiti/streets/${str.uri}</a>
						</td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
		</div>
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