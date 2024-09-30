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
<title>Ancient Graffiti Project :: Figural Caption Occurrences
	Report</title>
<style type="text/css">
#table {
	width: 90%;
	margin: auto;
}
</style>
<body>

	<%@include file="/WEB-INF/jsp/header.jsp"%>

	<%
	List<List<String>> data = (List<List<String>>) request.getAttribute("figuralCaptionOccurrences");
	String paramCity = request.getParameter("city");
	if (paramCity == null) {
		paramCity = "All";
	}
	%>
	<main class="container">
		<h1>
			Report on
			<%=paramCity%>
			Figural Caption Occurrences
		</h1>

		<p>This report contains the count for each figural caption. NOTE:
			All figural inscriptions with no caption HAVE been included</p>

		<div class="row">
			<div class="col-8">
				<div class="table_box">
					<table id="table"
						class="table table-bordered tableFixHead table-hover">

						<thead class="tableFixHead">
							<tr>
								<th scope="col" class="tableFixHead" onclick="sortTable(0, 'string')">Caption</th>
								<th scope="col" class="tableFixHead" onclick="sortTable(1, 'number')">Occurrences</th>
							</tr>
						</thead>
						<tbody>
							<%
							for (int row = 0; row < data.size(); row++) {
							%>
							<tr>
								<%
								if (data.get(row).get(1).equals("1")) {
								%>

								<td><%=data.get(row).get(0)%> &nbsp; <a
									href="<%=request.getContextPath()%>/graffito/AGP-<%=data.get(row).get(2)%>">AGP-<%=data.get(row).get(2)%>
										<svg xmlns="http://www.w3.org/2000/svg" width="16" height="16"
											fill="currentColor" class="bi bi-box-arrow-up-right"
											viewBox="0 0 16 16">
  								<path fill-rule="evenodd"
												d="M8.636 3.5a.5.5 0 0 0-.5-.5H1.5A1.5 1.5 0 0 0 0 4.5v10A1.5 1.5 0 0 0 1.5 16h10a1.5 1.5 0 0 0 1.5-1.5V7.864a.5.5 0 0 0-1 0V14.5a.5.5 0 0 1-.5.5h-10a.5.5 0 0 1-.5-.5v-10a.5.5 0 0 1 .5-.5h6.636a.5.5 0 0 0 .5-.5z" />
  								<path fill-rule="evenodd"
												d="M16 .5a.5.5 0 0 0-.5-.5h-5a.5.5 0 0 0 0 1h3.793L6.146 9.146a.5.5 0 1 0 .708.708L15 1.707V5.5a.5.5 0 0 0 1 0v-5z" />
					</svg></a></td>
								<td><%=data.get(row).get(1)%></td>
								<%
								} else {
								for (int col = 0; col < data.get(row).size() - 1; col++) {
								%>
								<td><%=data.get(row).get(col)%></td>
								<%
								}
								}
								%>

							</tr>
							<%
							}
							%>
						</tbody>
					</table>
				</div>
			</div>
			<div class="col-3">
				<a class="btn btn-outline-secondary"
					style="color: rgb(51, 51, 51); margin: 1em"
					href="<%=request.getContextPath()%>/figuralCaptionOccurrences/<%=paramCity%>/csv">Download
					CSV <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16"
						fill="currentColor" class="bi bi-download" viewBox="0 0 16 16">
  						<path
							d="M.5 9.9a.5.5 0 0 1 .5.5v2.5a1 1 0 0 0 1 1h12a1 1 0 0 0 1-1v-2.5a.5.5 0 0 1 1 0v2.5a2 2 0 0 1-2 2H2a2 2 0 0 1-2-2v-2.5a.5.5 0 0 1 .5-.5z" />
  						<path
							d="M7.646 11.854a.5.5 0 0 0 .708 0l3-3a.5.5 0 0 0-.708-.708L8.5 10.293V1.5a.5.5 0 0 0-1 0v8.793L5.354 8.146a.5.5 0 1 0-.708.708l3 3z" />
						</svg>
				</a>
				<form
					action="<%=request.getContextPath()%>/admin/figuralCaptionOccurrencesReport">
					<fieldset>
					<c:set var="paramCity" value="${param.city}" />
						<legend>Select a City</legend>
						<div>
							<input type="radio" id="All" name="city" value="All" <c:if test="${paramCity.equals('All')}">checked</c:if>> <label
								for="All">All Cities</label>
						</div>
						<c:forEach var="k" begin="${1}"
							end="${fn:length(requestScope.cities)}">
							<c:set var="city" value="${requestScope.cities[k-1]}" />
							<div>
								<input type="radio" id="${city}" name="city" value="${city}" <c:if test="${paramCity.equals(city)}">checked</c:if> >
								<label for="${city}">${city} </label>
							</div>
						</c:forEach>
						<div>
							<button type="submit">Filter</button>
						</div>
					</fieldset>
				</form>
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