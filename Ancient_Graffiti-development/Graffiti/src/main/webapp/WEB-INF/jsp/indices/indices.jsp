<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>indices</title>
<%@ include file="/resources/common_head.txt"%>
<link rel="stylesheet" href="<%=request.getContextPath()%>/resources/css/main.css" />
<style>
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
</style>

<script>
// Controls the display of the Return to Top Button
window.onscroll = function(){scrollFunction()};
function scrollFunction() {
	  if ((document.body.scrollTop > (screen.height/2)) || (document.documentElement.scrollTop > (screen.height/2))) {
	    document.getElementById("scroll_top").style.display = "inline";
	  } else {
	    document.getElementById("scroll_top").style.display = "none";
	  }
	}
</script>
</head>
<body>
	<%@include file="/WEB-INF/jsp/header.jsp"%>
	<%@include file="indexSideBar.jsp" %>
	<%@include file="termIndexList.jsp"%>
	<a href="#top" id="scroll_top" class="btn btn-agp">Return To Top</a>
	<%@include file="/WEB-INF/jsp/footer.jsp"%>
	
	<%-- All javascripts needed to be below the header to work properly
	 Therefore, scripts that were once spread across files can be found below--%>
	
	<%--script for checking hierarchical checkboxes for search--%>
	<script type="text/javascript">
	
	/**
	 * flips the arrow when a filter type (City, Property Type, Drawing Type, etc.)
	 * is clicked
	 * 
	 * @param x
	 *            the value to append to "expand" to find the appropriate arrow to
	 *            flip
	 */
	function switchArrow(x) {
		var menu = document.getElementById(x + "_menu");
		var code = "&#" + menu.innerHTML.charCodeAt(0) + ";";
		if (code == "&#9650;") {
			menu.innerHTML = "&#9660;"; // change to down arrow
		} else {
			menu.innerHTML = "&#9650;"; // change to up arrow
		}
	}
	
	function createURL(baseURL) {
		var myUrl = baseURL + "?";
		
		// Determine the ordering for the index
		var selectBox = document.getElementById("sortParam");
		var selection = selectBox.options[selectBox.selectedIndex].value;
		myUrl += ("sort_by=" + selection);
		
		myUrl += getChecked('lang');
		myUrl += getChecked('pos');
		myUrl += getChecked('name');
		myUrl += getChecked('person');
		myUrl += getChecked('gender');
		myUrl += getChecked('city');
		return myUrl;
	}
	
	function getChecked(param){
		var retString = '';
		var labels = document.getElementsByClassName(param + '-check');
		if (labels.length > 0){
			currentParams = new Array();
			for (var i = 0; i < labels.length; i++) {
				if (labels[i].checked){
					currentParams.push(labels[i].value);
				}
			}
			if (currentParams.length > 0){
				retString +=  "&" + param + "=" + currentParams.join("+");
			}
		}
		return retString;
	}
	
	function updateOrdering(){
		url = '<%=request.getContextPath()%>/indices/<%=request.getAttribute("index")%>';
		window.location.href = createURL(url);
	}
	
	function setSortingSelection(){
		var type = '<%=request.getParameter("sort_by")%>';
		var e = document.getElementById("sortParam");
		if (type == "null"){
			e.value="alpha";
		}
		else{
			e.value=type;
		}
		
		var poses = '<%=request.getParameter("pos")%>'.split(" ");
		if (poses != 'null'){
			for (var i = 0; i < poses.length; i++) {
				e = document.getElementById(poses[i]);
				e.checked = true;
			}
			$('#pos_collapse').collapse("show");
			switchArrow('pos');
		}
		
		var langs = '<%=request.getParameter("lang")%>'.split(" ");
		if (langs != 'null'){
			for (var i = 0; i < langs.length; i++) {
				e = document.getElementById(langs[i]);
				e.checked = true;
			}
			$('#lang_collapse').collapse("show");
			switchArrow('lang');
		}
	
		var ntypes = '<%=request.getParameter("name")%>'.split(" ");
		if (ntypes != 'null'){
			for (var i = 0; i < ntypes.length; i++) {
				e = document.getElementById(ntypes[i]);
				e.checked = true;
			}
			$('#name_collapse').collapse("show");
			switchArrow('name');
		}
		
		var ptypes = '<%=request.getParameter("person")%>'.split(" ");
		if (ptypes != 'null'){
			for (var i = 0; i < ptypes.length; i++) {
				e = document.getElementById(ptypes[i]);
				e.checked = true;
			}
			$('#person_collapse').collapse("show");
			switchArrow('person');
		}
		
		var genders = '<%=request.getParameter("gender")%>'.split(" ");
		if (genders != 'null'){
			for (var i = 0; i < genders.length; i++) {
				e = document.getElementById(genders[i]);
				e.checked = true;
			}
			$('#gender_collapse').collapse("show");
			switchArrow('gender');
		}
		
		var cities = '<%=request.getParameter("city")%>'.split(" ");
		if (cities != 'null'){
			for (var i = 0; i < cities.length; i++) {
				e = document.getElementById(cities[i]);
				e.checked = true;
			}
			$('#city_collapse').collapse("show");
			switchArrow('city');
		}
	}
	
	</script>
	
	<script type="text/javascript">
		window.onload = function() {
			<c:if test="${not empty sessionScope.returnFromTerms}">
			document.getElementById("${sessionScope.returnFromTerms}")
					.scrollIntoView();
			// Do this to put the term within view
			window.scrollBy(0, -100);
			<c:set var="returnFromTerms" value="" scope="session" />
			</c:if>
		}
	</script>
	
	<script type="text/javascript">
	
	//Based on code from https://codepen.io/jgx/pen/wiIGc
	;(function($) {
		   $.fn.fixMe = function() {
		      return this.each(function() {
		         var $this = $(this),
		            $t_fixed;
		         function init() {
		            $this.wrap('<div class="headContainer" />');
		            $t_fixed = $this.clone();
		            $t_fixed.find("tbody").remove().end().addClass("fixed").insertBefore($this);
		            resizeFixed();
		            $t_fixed.hide();
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
		   checkBox();
		   setSortingSelection();
		   $("table").fixMe();
		});
		
	// Set the current index check box
	function checkBox(){
		var type = '<%=request.getAttribute("index")%>';
		var e = document.getElementById(type);
		e.checked = true;	  
	}
	</script>
</body>
</html>