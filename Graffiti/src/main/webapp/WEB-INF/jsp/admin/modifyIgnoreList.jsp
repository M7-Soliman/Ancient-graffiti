<!-- Authors: Emily Cohen and Joe Wen -->
<!-- Date: 05/17/19 -->
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="java.util.List"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<%@include file="/resources/common_head.txt"%>
<title>Ignore List</title>

<style>
option {
 	font-size: 16px;
}

select {
	border: none;
}

 .termSearch {  
 	width: 100%;
 	height: 20px;
 	margin-bottom: 2px;
}
</style>
</head>
<body>
	<%@include file="/WEB-INF/jsp/header.jsp"%>
	<%
		java.util.List<String> termsDisplayedList = (java.util.List<String>) request.getAttribute("displayingTerms");
		java.util.List<String> termsBufferList = (java.util.List<String>) request.getAttribute("bufferTerms");
		java.util.List<String> termsIgnoredList = (java.util.List<String>) request.getAttribute("ignoredTerms");
	%>
	<div class="container">
		
			<h2>Edit/Remove Ignore List Terms</h2>
			
			<form action="modifyIgnoreList" id="termForm" name="termForm" method="post">
			
			<!-- Terms on Display Currently -->
			<div id="all_indicies"
				style="width: 25%; display: inline-block; margin-right: 2%; margin-right: 2%;">	
				<h3>Terms Displaying</h3>
				<!-- Search Bar -->
					<input type="text" class="termSearch" id="searchone"
						onkeyup="searchDisplay()" placeholder="Search for terms..."/>
					<select multiple class="form-control" id="displayList"
						name="displayList"
						style="padding: 10px; height: 300px; overflow: scroll;">
						<%
							for (String term : termsDisplayedList) {
						%>
						<option><%=term%></option>
						<%
							}
						%>
					</select>
			</div>
			
			<!-- Left Buttons -->
			<div >
				<div>
					<input type="button" onclick="keepTerms()" class="btn btn-agp" id="addTerm"
						style="margin-right: 2%; margin-left: 2%; margin-top: -350%;" value="&lt;
						Keep"/>
				</div>

				<div>
					<input type="button" onclick="reviewDisplayedTerms()" class="btn btn-agp"
						id="removeTerm"
						style="margin-top: -250%; margin-right: 2%; margin-left: 2%;" value="
						Review &gt;"/>
				</div>

			</div>

			<!-- Terms that are under review by the classicists -->
			<div id="buffer_indicies" style="width: 25%; display: inline-block; margin-right: 2%; margin-left: 2%;">
				
				<h3>Terms Under Review</h3>
				<!-- Search Bar -->
				<input type="text" class="termSearch" id="searchtwo"
					onkeyup="searchBuffer()" placeholder="Search for terms..."/>

				<select multiple class="form-control" id="bufferList"
					name="bufferList" style="padding: 10px; height: 300px;">
					<%
						for (String term : termsBufferList) {
					%>
					<option><%=term%></option>
					<%
						}
					%>
				</select>

			</div>
			
			<!-- Right Buttons -->
			<div>
				<div>
					<input type="button" onclick="reviewIgnoredTerms()" class="btn btn-agp"
						id="removeTerm"
						style="margin-top: -350%; margin-right: 2%; margin-left: 2%;" value="
						&lt; Review"/>
				</div>
				<div>
					<input type="button" onclick="ignoreTerms()" class="btn btn-agp" id="addTerm"
						style="margin-right: 2%; margin-left: 2%; margin-top: -250%;" value="Ignore
						&gt;"/>
				</div>

			</div>

			<!-- Terms Currently Being Ignored -->
			<div id="ignorelist_terms" style="width: 25%; display: inline-block; margin-left: 2%;">
			
				<h3>Terms Ignored</h3>
				<!-- Search Bar -->
				<input type="text" class="termSearch" id="searchthree"
					onkeyup="searchIgnore()" placeholder="Search for terms..."/>
					
				<select multiple class="form-control" name="hiddenList" id="hiddenList"
					    style="padding: 10px; height: 300px;">
					    
					<%
						for (String term : termsIgnoredList) {
					%>
					<option><%=term%></option>
					<%
						}
					%>
				</select>
			</div>
			<input type="hidden"  name="${_csrf.parameterName}"   value="${_csrf.token}"/>
			<p>
				<i>*Hold the control/command button to select multiple options</i>
			</p>
			<p>
				<input type="button" onclick="clearSelections()" class="btn btn-agp"
					id="clearSelection" value="Clear Selection(s)"/>
			</p>
			<p>
 				<input type="button" onclick="saveListsFunction()" class="btn btn-agp"
					id="saveLists" value="Save"/> 
			</p>
			</form>
			
	</div>
	
	
		<script>
		java.util.List<String> lyst;
		function ignoreTerms() {
			var terms = document.getElementById("bufferList").selectedOptions;
			var termsArray = [];
			for (var i = 0; i < terms.length; i++) {
				termsArray.push(terms[i].value);
			}
			for (var i = 0; i < termsArray.length; i++) {
				var list = document.getElementById("hiddenList");
				var newTerm = document.createElement("option");
				newTerm.innerHTML = termsArray[i];
				list.appendChild(newTerm);
				terms[0].parentNode.removeChild(terms[0]);
			}
		};

		function reviewIgnoredTerms() {
			var terms = document.getElementById("hiddenList").selectedOptions;
			var termsArray = [];
			for (var i = 0; i < terms.length; i++) {
				termsArray.push(terms[i].value);
			}
			
			for (var i = 0; i < termsArray.length; i++) {
				var list = document.getElementById("bufferList");
				var newTerm = document.createElement("option");
				newTerm.innerHTML = termsArray[i];
				list.appendChild(newTerm);
				terms[0].parentNode.removeChild(terms[0]);
			}
		};

		function reviewDisplayedTerms() {
			var terms = document.getElementById("displayList").selectedOptions;
			var termsArray = [];
			for (var i = 0; i < terms.length; i++) {
				termsArray.push(terms[i].value);
			}
			
			for (var i = 0; i < termsArray.length; i++) {
				var list = document.getElementById("bufferList");
				var newTerm = document.createElement("option");
				newTerm.innerHTML = termsArray[i];
				list.appendChild(newTerm);
				terms[0].parentNode.removeChild(terms[0]);
			}
		};

		function keepTerms() {
			var terms = document.getElementById("bufferList").selectedOptions;
			var termsArray = [];
			for (var i = 0; i < terms.length; i++) {
				termsArray.push(terms[i].value);
			}
			
			for (var i = 0; i < termsArray.length; i++) {
				var list = document.getElementById("displayList");
				var newTerm = document.createElement("option");
				newTerm.innerHTML = termsArray[i];
				list.appendChild(newTerm);
				terms[0].parentNode.removeChild(terms[0]);
			}
		};

		function clearSelections() {
			var displayTerms = document.getElementById("displayList").options;
			for (var i = 0; i < displayTerms.length; i++) {
				displayTerms[i].selected = false;
			}
			
			var hiddenTerms = document.getElementById("hiddenList").options;
			for (var i = 0; i < hiddenTerms.length; i++) {
				hiddenTerms[i].selected = false;
			}
			
			var bufferTerms = document.getElementById("bufferList").options;
			for (var i = 0; i < bufferTerms.length; i++) {
				bufferTerms[i].selected = false;
			}
			
		}

		function saveListsFunction() {
			
   			var currentTerms = document.getElementById("displayList").options;
			for (var i = 0; i < currentTerms.length; i++) {
				currentTerms[i].selected = true;
			};
			
			var bufferTerms = document.getElementById("bufferList").options;
			for (var i = 0; i < bufferTerms.length; i++) {
				bufferTerms[i].selected = true;
			};

			var hiddenTerms = document.getElementById("hiddenList").options;
			for (var i = 0; i < hiddenTerms.length; i++) {
				hiddenTerms[i].selected = true;
			};  
			
			document.forms["termForm"].submit(); 
			clearSelections();
			alert("Your edits have been saved");
		}
		
		function searchDisplay(){
			var input, select;
			input = document.getElementById("searchone");
			select = document.getElementById("displayList");
			searchTerms(input, select);
		}
		
		function searchBuffer(){
			var input, select;
			input = document.getElementById("searchtwo");
			select = document.getElementById("bufferList");
			searchTerms(input, select);
		}
		
		function searchIgnore(){
			var input, select;
			input = document.getElementById("searchthree");
			select = document.getElementById("hiddenList");
			searchTerms(input, select);
		}
		
		function searchTerms(input, select) {
			var filter, option, txtValue;
			filter = input.value.toUpperCase();
			option = select.getElementsByTagName('option');
			
			for (i = 0; i < option.length; i++) {
				txtValue = option[i].textContent || option[i].innerText;
				if (txtValue.toUpperCase().indexOf(filter) > -1) {
					option[i].style.display = "";
				} else {
					option[i].style.display = "none";
				}
			}
		}
	</script>
	<%@include file="/WEB-INF/jsp/footer.jsp"%>
</body>
</html>