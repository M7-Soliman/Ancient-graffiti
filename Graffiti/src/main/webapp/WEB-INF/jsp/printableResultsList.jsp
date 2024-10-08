<%@page import="org.aspectj.weaver.reflect.Java14GenericSignatureInformationProvider"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%
	/* Used by the filter and stable URIs to display results */
	session.setAttribute("filteredList", request.getAttribute("resultsLyst"));
%>

<c:forEach var="i" items="${resultsLyst}" varStatus="graffitoIndex">
	<h4 id="${i.graffitiId }" style="font-size: 24px;">
		<c:choose>
			<c:when test="${not empty i.caption }">
				<c:out value="${i.caption }" />
			</c:when>
			<c:otherwise>
		Graffito
		</c:otherwise>
		</c:choose>
	</h4>

	<table class="main-table" style="margin-bottom: 30px;">
		<tr>
			<th class="propertyLabel">AGP ID:</th>
			<td>AGP-${i.graffitiId}</td>
		</tr>
		<c:choose>
			<c:when test="${not empty i.contentWithLineBreaks}">
				<tr>
					<th class="propertyLabel">Graffito:</th>
					<td>${i.contentWithLineBreaks}</td>
				</tr>
				<c:if test="${not empty i.contentTranslation}">
					<tr>
						<th><span class="propertyLabel">Translation:</span></th>
						<td>${i.contentTranslation}</td>
					</tr>
				</c:if>
			</c:when>
			<c:otherwise>
				<tr>
					<th class="propertyLabel">Drawing Description:</th>
					<td>${i.figuralInfo.descriptionInEnglish}</td>
				</tr>
				<c:if test="${not empty i.figuralInfo.descriptionInLatin}">
					<tr>
						<th class="propertyLabel">Description In Latin:</th>
						<td>${i.figuralInfo.descriptionInLatin}</td>
					</tr>
				</c:if>
			</c:otherwise>
		</c:choose>
		<c:if test="${not empty i.bibliography}">
			<tr>
				<th><span class="propertyLabel">Bibliography:</span></th>
				<td>${i.bibliography}</td>
			</tr>
		</c:if>
		<c:if test="${not empty i.writingStyle}">
			<tr>
				<th><span class="propertyLabel">Writing Style:</span></th>
				<td>${i.writingStyle}</td>
			</tr>
		</c:if>
		<c:if test="${not empty i.language}">
			<tr>
				<th><span class="propertyLabel">Language:</span></th>
				<td>${i.language}</td>
			</tr>
		</c:if>
		<tr>
			<th class="propertyLabel">City:</th>
			<td><a
				href="http://pleiades.stoa.org/places/${i.property.insula.city.pleiadesId}">${i.ancientCity}</a></td>
		</tr>
		<tr>
			<th class="propertyLabel">Findspot:</th>
			<td><a
				href="<%=request.getContextPath() %>/results?property=${i.property.id}">${i.property.propertyName}
					(${i.property.insula.shortName}.${i.property.propertyNumber})</a>
			</td>
		</tr>
		<c:if test="${i.figuralInfo.getDrawingTags().size() > 0}">
			<tr>
				<c:choose>
					<c:when test="${i.figuralInfo.getDrawingTags().size() == 1}">
						<th class="propertyLabel">Drawing Category:</th>
					</c:when>
					<c:otherwise>
						<th class="propertyLabel">Drawing Categories:</th>
					</c:otherwise>
				</c:choose>
				<td>
				<c:forEach var="dt"
						items="${i.figuralInfo.getDrawingTags()}"
						varStatus="loopStatus">
						<a href="<%=request.getContextPath() %>/results?drawing_category=${dt.id}">${dt.name}</a>
						<c:if test="${!loopStatus.last}">, </c:if>
					</c:forEach>
					</td>
			</tr>
		</c:if>
		<tr>
			<th><span class="propertyLabel">Suggested Citation:</span></th>
			<!-- Cannot use {i.citation} here because we need the URI to be a link -->
			<td>AGP-${i.graffitiId}, <i>The Ancient Graffiti Project</i>,
			<a href="http://ancientgraffiti.org/Graffiti/graffito/AGP-${i.graffitiId}">&lt;http://ancientgraffiti.org/Graffiti/graffito/AGP-${i.graffitiId}&gt;</a> 
			[accessed: <%= new java.text.SimpleDateFormat("dd MMM yyyy").format(new java.util.Date()) %>]
			</td>
		</tr>
	</table>
	<hr class="main-table" />

</c:forEach>