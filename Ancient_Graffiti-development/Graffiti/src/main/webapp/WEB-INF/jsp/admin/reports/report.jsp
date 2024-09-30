<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<%@include file="/resources/common_head.txt"%>
<title>Ancient Graffiti Project :: Report</title>
<link href="<%=request.getContextPath()%>/resources/css/reports.css" rel="stylesheet" type="text/css">
<!--
<style type="text/css">
	table, th, td {
	border: 1px solid black;
	border-collapse: collapse;
	padding: 10px;
	table-layout:fixed;
	 overflow: hidden; 
    text-overflow: ellipsis; 
    word-wrap: break-word;
}

th {
	text-align: center;
}

</style>
-->
</head>
<body>
	<%@include file="/WEB-INF/jsp/header.jsp"%>
	<main class="container">
	<div>
		<c:set var="num" value="${fn:length(requestScope.resultsLyst)}" />
		<span style="width: 475px;"> <c:out
				value="${num} results ${searchQueryDesc}" />
		</span>
		<c:if test="${num == 0}">
			<br />
			<c:out value="Try broadening your search" />
		</c:if>

		<div>
			<h1>Report on Graffiti</h1>
			
			<a class="btn btn-outline-secondary" style="color: rgb(51, 51, 51);" download="AGP-report.xls" href="#"
				onclick="return ExcellentExport.excel(this, 'reportTable', 'Worksheet');">Export
					to Excel</a>
		</div>

		<br>

		<div class = "table_box" >
			<table class="table table-bordered tableFixHead table-hover" id="reportTable">
			<thead class="table-dark tableFixHead">
				<tr>
					<th scope="col" class="prop tableFixHead">AGP ID</th>
					<th scope="col" class="prop tableFixHead">CIL #</th>
					<th scope="col" class="prop tableFixHead">Langner</th>
					<th scope="col" class="prop tableFixHead">City</th>
					<th scope="col" class="prop tableFixHead">Location</th>
					<th scope="col" class="prop tableFixHead">Description</th>
					<th scope="col" class="prop tableFixHead">Category</th>
					<th scope="col" class="prop tableFixHead">Language</th>
					<th scope="col" class="prop tableFixHead">Writing Style</th>
					<th scope="col" class="prop tableFixHead">Graffito Text</th>
					<th scope="col" class="prop tableFixHead">Bibliography</th>
					
				</tr>
			</thead>
				<c:forEach var="k" begin="${1}"
					end="${fn:length(requestScope.resultsLyst)}">
					<c:set var="i" value="${requestScope.resultsLyst[k-1]}" />
					<tr >
						<!-- AGP ID -->
						
						<td ><a
							href="<%=request.getContextPath() %>/graffito/AGP-${i.graffitiId}">AGP-${i.graffitiId}</a> </br> 
							
							<form action="<%=request.getContextPath()%>/admin/updateGraffito">
								
								<input class = "btn  btn-outline-secondary" type=submit value="Edit" />
								<input type="hidden" name="graffitiID" value="${i.graffitiId}" />
									
							</form>	
								
							</td>
						<!-- CIL # -->
						<td>${i.cil}</td>
						<!-- Langner # -->
						<td>${i.langner}</td>
						<!-- City -->
						<td><a
							href="http://pleiades.stoa.org/places/${i.property.insula.city.pleiadesId}">${i.ancientCity}</a></td>
						<!-- Location -->
						<td>${i.property.propertyName}&nbsp;(<a
							href="<%=request.getContextPath() %>/results?city=${i.property.insula.city.name}&property=${i.property.id}">${i.property.insula.shortName}.${i.property.propertyNumber}</a>)
						</td>
						<!-- Summary -->
						<td>${i.caption }</td>
						<!-- Category -->
						<c:choose>
							<c:when test="${i.figuralInfo.getDrawingTags().size() > 0}">
								<td><c:forEach var="dt"
										items="${i.figuralInfo.getDrawingTags()}"
										varStatus="loopStatus">
										<a
											href="<%=request.getContextPath() %>/results?drawing=${dt.id}">${dt.name}</a>
										<c:if test="${!loopStatus.last}">, </c:if>
									</c:forEach></td>
							</c:when>
							<c:otherwise>
								<td></td>
							</c:otherwise>
						</c:choose>
						<!-- Language -->
						<td>${i.language}</td>
						<!-- Writing Style -->
						<td>${i.writingStyle}</td>
						<!-- Graffito Text -->
						<td style="text-align: center;">${i.contentWithLineBreaks}</td>

						<!-- Bibliography -->
						<td>${i.bibliography}</td>

						
					</tr>
				</c:forEach>
			</table>
		</div>
	</div>
	</main>
	<%@include file="/WEB-INF/jsp/scrollTop.jsp"%>
	<%@include file="/WEB-INF/jsp/footer.jsp"%>
</body>
</html>