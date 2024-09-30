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
<title>Ancient Graffiti Project :: All Poetry Report</title>
<link href="<%=request.getContextPath()%>/resources/css/reports.css"
	rel="stylesheet" type="text/css">
</head>
<body>

	<%@include file="/WEB-INF/jsp/header.jsp"%>

	<%
	List<List<String>> info = (List<List<String>>) request.getAttribute("confirmedPoetry");

	int count = 0;
	for (int i = 0; i < info.size(); i++) {
		if (!info.get(i).isEmpty()) {
			count += 1;
			info.get(i).add(0, String.valueOf(count));
		}
	}
	%>

	<main class="container">

		<h1>Report on All Confirmed Poetry Graffiti</h1>

		<p>This report returns all of the inscriptions in the database
			that are marked as being poetry and have been confirmed.</p>


		<span style="width: 475px"> <%=count%> results filtering
		</span> <a class="btn btn-outline-secondary"
			style="color: rgb(51, 51, 51); margin: 1em"
			href="<%=request.getContextPath()%>/confirmedPoetryTable/csv">Download
			CSV <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16"
				fill="currentColor" class="bi bi-download" viewBox="0 0 16 16">
  <path
					d="M.5 9.9a.5.5 0 0 1 .5.5v2.5a1 1 0 0 0 1 1h12a1 1 0 0 0 1-1v-2.5a.5.5 0 0 1 1 0v2.5a2 2 0 0 1-2 2H2a2 2 0 0 1-2-2v-2.5a.5.5 0 0 1 .5-.5z" />
  <path
					d="M7.646 11.854a.5.5 0 0 0 .708 0l3-3a.5.5 0 0 0-.708-.708L8.5 10.293V1.5a.5.5 0 0 0-1 0v8.793L5.354 8.146a.5.5 0 1 0-.708.708l3 3z" />
</svg>
		</a>
		<div class="table_box">
			<table class="table table-bordered tableFixHead thirteen table-hover">
				<thead class="table-dark tableFixHead">
					<tr>
						<th scope="col" class="tableFixHead">#</th>
						<th scope="col" class="tableFixHead">EDR</th>
						<th scope="col" class="tableFixHead">Text</th>
						<th scope="col" class="tableFixHead">Apparatus</th>
						<th scope="col" class="tableFixHead">Meter</th>
						<th scope="col" class="tableFixHead">Author</th>
					</tr>
				</thead>
				<tbody>
									<%
					for (int row = 0; row < info.size(); row++) {
						Collections.replaceAll(info.get(row), null, "");
					%>
					<tr>
						<%
						for (int col = 0; col < info.get(row).size(); col++) {
						%>

						<td>
							<%
							if (col == 1) {
							%> <a
							href="<%=request.getContextPath()%>/graffito/AGP-<%=info.get(row).get(col)%>">AGP-<%=info.get(row).get(col)%></a>
 <%
 } else {
 %> <%=info.get(row).get(col)%> <%
 }
 %>

						</td>
						<%
						}
						%>
					</tr>
					<%
					}
					%>
				
				
				</tbody>
			</table>
		</div>
	</main>
	<%@include file="/WEB-INF/jsp/scrollTop.jsp"%>

	<%@include file="/WEB-INF/jsp/footer.jsp"%>
</body>
</html>