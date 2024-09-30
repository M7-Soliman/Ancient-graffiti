<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<c:set var="num" value="${requestScope.totalResults}" />
<%@ page import="java.util.List"%>
<%
List<Integer> updatedLocationKeys = (List<Integer>) request.getAttribute("findLocationKeys");
List<Integer> updatedInsulaKeys = (List<Integer>) request.getAttribute("insulaLocationKeys");
updatedLocationKeys.addAll(updatedInsulaKeys);

boolean hasHerc = ((boolean) request.getAttribute("hasHerc"));
boolean hasPomp = ((boolean) request.getAttribute("hasPomp"));
boolean hasStab = ((boolean) request.getAttribute("hasStab"));

boolean adminSession = false;
if (session.getAttribute("authenticated") != null) {
	adminSession = (Boolean) session.getAttribute("authenticated");
}
%>

<%--If there are results display page results --%>
<c:choose>
	<c:when test="${num > 0}">

		<div id="mapkeys" style="display: none;"><%=updatedLocationKeys%></div>
		<div id="hasHerc" style="display: none;"><%=hasHerc%></div>
		<div id="hasPomp" style="display: none;"><%=hasPomp%></div>
		<div id="hasStab" style="display: none;"><%=hasStab%></div>

		<c:set var="pageCount" value="${requestScope.pageCount}" />
		<c:set var="current" value="${requestScope.currentPage}" />

		<div id="results_header" style="width: 100%;" class="container">
			<div class="row" style="margin-bottom: 10px;">
				<div class="col-md-2"
					style="background-color: lightblue; border-radius: 5%; vertical-align: middle;">
					<c:out value="${num} results found" />
				</div>
				<div class="col-md-10" id="export_buttons_panel">
					<a href="<%=request.getContextPath()%>/filtered-results/xml"
						id="bulkEpidocs" class="btn btn-agp btn-sm btn-export">Export
						as EpiDoc </a> <a
						href="<%=request.getContextPath()%>/filtered-results/json"
						id="bulkJson" class="btn btn-agp btn-sm btn-export">Export as
						JSON </a> <a href="<%=request.getContextPath()%>/filtered-results/csv"
						id="bulkEpidocs" class="btn btn-agp btn-sm btn-export">Export
						as CSV </a>
					<button id="print" class="btn btn-agp btn-sm btn-export"
						onclick="printResults();">Print</button>
				</div>
			</div>
			<%
			if (adminSession) {
			%>
			<div
				style="display: flex; align-items: center; justify-content: center;">
				<button class="btn btn-agp btn-sm" onclick="generateReport();"
					id="report">Generate Report</button>
			</div>
			<%
			}
			%>
			<div class="row">
				<div class="col-md-2">
					<c:out value="Page ${current} of ${pageCount}" />
				</div>
				<div id="refine_results_panel" class="col-md-10">
					<div style="margin-right: 8px;">
						Sort by <select id="sortParam" onchange="refineResults('sort');">
							<%
							String param = request.getParameter("sort_by");
							boolean isNull = false;
							if (param == null)
								isNull = true;
							%>
							<option value="relevance"
								<%=!isNull && param.equals("relevance") ? "selected" : ""%>>
								Relevance</option>
							<option value="caption"
								<%=!isNull && param.equals("caption") ? "selected" : ""%>>
								Caption</option>
							<option value="cil"
								<%=!isNull && param.equals("cil") ? "selected" : ""%>>CIL
								#</option>
							<option value="property.property_id"
								<%=!isNull && param.equals("property.property_id") ? "selected" : ""%>>
								Findspot</option>
						</select>
					</div>

					<div>
						View <select id="viewParam" onchange="refineResults('view');">
							<%
							String viewParam = request.getParameter("view_count");
							boolean viewIsNull = viewParam == null;
							%>
							<option value="200"
								<%=!viewIsNull && viewParam.equals("200") ? "selected" : ""%>>
								200</option>
							<option value="100"
								<%=!viewIsNull && viewParam.equals("100") ? "selected" : ""%>>
								100</option>
							<option value="50"
								<%=!viewIsNull && viewParam.equals("50") ? "selected" : ""%>>
								50</option>
							<option value="25"
								<%=!viewIsNull && viewParam.equals("25") ? "selected" : ""%>>
								25</option>
						</select>
					</div>

				</div>

				<c:set var="endTab" value="${requestScope.endTab}" />
				<c:set var="startTab" value="${requestScope.firstTab}" />

				<div class="Pagination">
					<ul class="pagination">
						<c:if test="${pageCount > 1 }">
							<c:if test="${current > 1}">
								<li class="page-item">
									<button class="page-link" onclick="pageResults(1);"
										value="${i}"}>&larrb;</button>
								</li>
								<li class="page-item">
									<button class="page-link" onclick="previousPage();"
										value="${i}">&lt;</button>
								</li>
							</c:if>

							<c:forEach var="i" begin="${startTab}" end="${endTab}">
								<c:choose>
									<c:when test="${i == current}">
										<li class="page-item active" name="${i}">
									</c:when>
									<c:otherwise>
										<li class="page-item" name="${i}">
									</c:otherwise>
								</c:choose>
								<button class="page-link" onclick="pageResults(${i});"
									value="${i}">
									<c:out value="${i}" />
								</button>
							</c:forEach>
							<c:if test="${current < pageCount}">
								<li class="page-item">
									<button class="page-link" onclick="nextPage();" value="${i}">&gt;
									</button>
								</li>
								<li class="page-item">
									<button class="page-link" onclick="pageResults(${pageCount});"
										value="${i}">&rarrb;</button>
								</li>
							</c:if>
						</c:if>
					</ul>
				</div>
			</div>
		</div>
		<div class="container-fluid">
			<div class="row">
				<div class="col-md-1"></div>
				<div class="col-md-11">
					<%@ include file="resultsList.jsp"%>
				</div>
			</div>
		</div>
	</c:when>
	<%--Else if there are no results do not display the results page--%>
	<c:otherwise>
		<div id="results_header">
			<p class="alert alert-info">No results found</p>
		</div>
		<p>Try broadening your search</p>
	</c:otherwise>
</c:choose>


