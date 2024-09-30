<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%
	/* Used by the filter and stable URIs to display results */
	session.setAttribute("filteredList", request.getAttribute("allResultsLyst"));
	
	// defining how many images in block on card results
	int imagesPerRow = 4;
%>


<style>
.themes-grid {
	list-style: none;
	padding: 0;
	margin: 0 auto;
	text-align: left;
	width: 100%;
}

.themes-grid li {
	display: inline-block;
	margin-top: 25px;
	margin-bottom: 25px;
 	margin-right: 100px;
	vertical-align: left;
	width: 450px;
}
></style>


<div id="themes">
	<ul class="themes-grid">

<c:forEach var="i" items="${resultsLyst}" varStatus="graffitoIndex">
	<li>
	<div class="card border-dark" style="border: 1px solid; padding: 20px;box-shadow: 5px 10px 8px #888888;">
	<h4 id="${i.graffitiId }">
		<c:choose>
			<c:when test="${not empty i.caption }">
				<c:out value="${i.caption }" />
			</c:when>
			<c:otherwise>
		Graffito
		</c:otherwise>
		</c:choose>
	</h4>
	<table class="main-table" style="margin-bottom: 15px;">
		<c:choose>
			<c:when test="${not empty i.contentWithLineBreaks}">
				<tr>
					<th class="propertyLabel">Graffito:</th>
					<td><p>${i.contentWithLineBreaks}</p></td>
				</tr>
				<c:if test="${not empty i.contentTranslation}">
					<tr>
						<th><span class="propertyLabel">Translation:</span></th>
						<td>${i.contentTranslation}</td>
					</tr>
				</c:if>
			</c:when>
			<c:otherwise>
			<!-- SS: I don't think this is ever called with how we get the data
			from EDR.  Consider removing or refactoring -->
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
		<c:if test="${not empty i.figuralInfo.descriptionInEnglish}">
			<tr>
				<th class="propertyLabel">Description of Drawing (English):</th>
				<td>${i.figuralInfo.descriptionInEnglish}</td>
			</tr>
		</c:if>
		<c:if test="${fn:length(i.photos) gt 0}">
			<c:set var="len" value="${fn:length(i.photos)}" />
			<c:set var="images" value="${i.images}" />
			<c:set var="thumbnails" value="${i.thumbnails}" />
			<c:set var="pages" value="${i.pages}" />
			

			<c:choose>
				<c:when test="${len == 1}">
					<tr>
						<td colspan="2">
							<div class="thumb_holder">
								<a target="_blank" href="${pages[0]}">
									<img style="display: block; margin-left: auto; margin-right: auto;" 
										 class="thumbnail" src="${images[0]}" />
								</a>
							</div>
						</td>
					</tr>
				</c:when>
				<c:otherwise>
					<c:set var="k" value="${i.graffitiId}" />
					<tr>
						<td colspan="2">
							<div class="thumb_holder">
								<a target="_blank" href="${pages[0]}" id="imgLink${k}">
									<img class="thumbnail" src="${images[0]}" id="imgSrc${k}" />
								</a>
							</div>
							<table class="buttons">
									<c:forEach var="j" begin="${0}" end="${len-1}">
										<%if (((int)pageContext.getAttribute("j")%imagesPerRow) == 0) {%>
											<tr class="image_select">
										<%}%>
											<td class="image_select">
												<input type="radio" name="image${k}"
												onclick="selectImg(${j},'${k}','${pages[j] }','${thumbnails[j]}','${images[j]}');"
												id="${k}${j}" 
												<%if (((int)pageContext.getAttribute("j")==0)){%>
													checked="checked"<%}%>
												/>
												<label for="${k}${j}" class="img_select"> 
													<img src="${thumbnails[j] }" class="selector_thumbnail"/>
												</label>
											</td>
										<%if (((int)pageContext.getAttribute("j")%imagesPerRow) == (imagesPerRow-1)) {%>
											</tr>
										<%}%>
									</c:forEach>
							</table>
						</td>
					</tr>
				</c:otherwise>
			</c:choose>
		</c:if>
		<tr>
			<th class="propertyLabel">City:</th>
			<td>
				<c:choose>
				<c:when test="${i.ancientCity != 'Smyrna'}">
					<a href="http://pleiades.stoa.org/places/${i.property.insula.city.pleiadesId}">${i.ancientCity}</a>
				</c:when>
				<c:otherwise>
					<a href="http://pleiades.stoa.org/places/550771">${i.ancientCity}</a>
				</c:otherwise>
				</c:choose>
			
			</td>
		</tr>
			<tr>
				<th class="propertyLabel">Findspot:</th>
				<td>
				<c:choose>
						<c:when test="${i.findSpotPropertyID == 0 and i.property.propertyName == 'Not found' and i.preciseLocation==null}"> 		
							<c:if test="${ i.ancientCity == 'Herculaneum' }">
								Locus incertus
							</c:if>
							<c:if test="${i.ancientCity == 'Stabiae' and i.sourceFindSpot.contains('Ager Stabianus')}">
								Ager Stabianus (locus incertus)
							</c:if>
							<c:if test="${i.ancientCity == 'Stabiae' and i.sourceFindSpot.contains('Arianna?')}">
								Locus incertus (Villa Arianna?)
							</c:if>
						</c:when>
						<c:otherwise>
				<c:choose>
					<c:when test="${not i.onFacade}">
						<a
						href="<%=request.getContextPath() %>/results?property=${i.property.id}">${i.property.propertyName}
						(${i.property.insula.shortName}.${i.property.propertyNumber})</a>
					</c:when>
					<c:otherwise>
						<c:choose>
							<c:when test="${i.segment.hidden == false}">
								<a href="<%=request.getContextPath() %>/results?segment=${i.segment.id}">${i.segment.street.streetName}
								(${i.segment.segmentName})</a>
							</c:when>
							<c:otherwise>
								<a href="<%=request.getContextPath() %>/results?street=${i.segment.street.id}">
								${i.segment.street.streetName}</a>
							</c:otherwise>
						</c:choose>
					</c:otherwise>
				</c:choose>
				</c:otherwise>
				</c:choose>
				</td>
			</tr>
		<c:if test="${not empty i.cil}">
			<tr>
				<th><span class="propertyLabel">CIL:</span></th>
				<td>${i.cil}</td>
			</tr>
		</c:if>
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
				<td><c:forEach var="dt"
						items="${i.figuralInfo.getDrawingTags()}"
						varStatus="loopStatus">
						<a href="<%=request.getContextPath() %>/results?drawing_category=${dt.id}">${dt.name}</a>
						<c:if test="${!loopStatus.last}">, </c:if>
					</c:forEach></td>
			</tr>
		</c:if>


		<%
			if (session.getAttribute("authenticated") != null) {
		%>
		<tr>
			<td colspan="2">
				<form action="<%=request.getContextPath()%>/admin/updateGraffito">
					<input class="btn btn-agp" type=submit value="Edit Graffito"><input
						type="hidden" name="graffitiID" value="${i.graffitiId}" />
				</form>
			</td>
		</tr>
		<%
			}
		%>
	</table>
				<div class="learnMoreDiv">
					<a class="learnMore"
						href="<%=request.getContextPath() %>/graffito/AGP-${i.graffitiId}"
						id="${i.graffitiId}"> Learn More &#10140;</a>
				</div>
	</div>
<!-- 	<hr class="main-table" />
 -->	</li>
</c:forEach>
	</ul>
	</div>