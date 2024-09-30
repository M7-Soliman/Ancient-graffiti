<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Collections"%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<%@include file="/resources/common_head.txt"%>
<link href="<%=request.getContextPath()%>/resources/css/reports.css"
	rel="stylesheet" type="text/css">
<title>Ancient Graffiti Project :: Figural Terms Occurrences
	Report</title>
<style type="text/css">
#table {
	width: 90%;
	margin: auto;
}
</style>
<body>

	<%@include file="/WEB-INF/jsp/header.jsp"%>
	<%@	page import="edu.wlu.graffiti.bean.IndexTerm"%>
	<%@ page import="java.util.Arrays"%>
	
	<%
	//List<List<String>> data = (List<List<String>>) request.getAttribute("figuralTermsOccurrences");
	java.util.List<IndexTerm> terms = (java.util.List<IndexTerm>)request.getAttribute("terms");
	%>
	<main class="container">
		<h1>
			Report on All Figural Term Occurrences
		</h1>

		<p>This report contains the count for each figural term.</p>

		<div class="row">
			<div class="col-8">
				<div class="table_box">
					<table id="table"
						class="table table-bordered tableFixHead table-hover">

						<thead class="tableFixHead">
							<tr>
								<th scope="col" class="tableFixHead" onclick="sortTable(0, 'string')">Term</th>
								<th scope="col" class="tableFixHead" onclick="sortTable(1, 'number')">Occurrences</th>
							</tr>
						</thead>
						<tbody>
							<c:forEach var="m" begin="${1}"
								end="${fn:length(requestScope.figuralTerms)}">
								<c:set var="prop" value="${requestScope.figuralTerms[m-1]}" />		
								<tr>
									<td><a href = "${pageContext.request.contextPath}/indices/figural-terms/term?id=${prop.termID}">${prop.term}</a></td>
									<td>${fn:length(prop.entries)}</td> 									
								</tr>
							</c:forEach>
						</tbody>
					</table>
				</div>
			</div>

			<script>
				function sortTable(n, type) {
					var table, rows, switching, i, x, y, shouldSwitch, dir, switchcount = 0;
					table = document.getElementById("table");
					switching = true;
					dir = "asc";
					while (switching) {
						switching = false;
						rows = table.rows;
						for (i = 1; i < (rows.length - 1); i++) {
							shouldSwitch = false;
							x = rows[i].getElementsByTagName("TD")[n];
							y = rows[i + 1].getElementsByTagName("TD")[n];
							if( type == 'number') {
								compareX = Number(x.innerHTML);
								compareY = Number(y.innerHTML);
							} else {
								compareX = x.innerHTML.toLowerCase();
								compareY = y.innerHTML.toLowerCase();
							}
							if (dir == "asc") {
								if (compareX > compareY) {
									shouldSwitch = true;
									break;
								}
							} else if (dir == "desc") {
								if (compareX < compareY) {
									shouldSwitch = true;
									break;
								}
							}
						}
						if (shouldSwitch) {
							rows[i].parentNode.insertBefore(rows[i + 1],
									rows[i]);
							switching = true;
							switchcount++;
						} else {
							if (switchcount == 0 && dir == "asc") {
								dir = "desc";
								switching = true;
							}
						}
					}
				}
			</script>
		</div>
	</main>
	<%@include file="/WEB-INF/jsp/scrollTop.jsp"%>
	<%@include file="/WEB-INF/jsp/footer.jsp"%>
</body>
</html>