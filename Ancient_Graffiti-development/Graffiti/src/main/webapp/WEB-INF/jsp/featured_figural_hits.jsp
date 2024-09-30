<!-- Author: Trevor Stalnaker -->
<!-- The page that displays the figural graffiti gallery -->
<%@ page import="edu.wlu.graffiti.bean.Theme"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ page import="java.util.List"%>
<%@ page import="edu.wlu.graffiti.bean.Inscription"%>
<main>
	<%Theme theme = (Theme) request.getAttribute("theme");%>
	<c:set var="theme" value="${theme}" />
	<p><%=theme.getDescription()%></p>

	<div class="row row-cols-2 g-4">
		<c:forEach var="k" begin="1" end="${fn:length(figuralHits) }">
			<c:set var="inscription" value="${figuralHits[k-1]}" />
			<div class="col">
				<div class="card mb-4 text-center">
					<div class="card-header">
						<h5>
							<a
								href="<%=request.getContextPath() %>/graffito/AGP-${inscription.graffitiId}">AGP-${inscription.graffitiId}</a>
						</h5>
					</div>
					<a
						href="<%=request.getContextPath() %>/graffito/AGP-${inscription.graffitiId}">
						<img class="card-img-bottom mx-auto"
						src="${inscription.preferredImage.imagePath}"
						alt="No Image Available" onclick="imgClicked(${k})" />
					</a>
					<div class="card-footer">
						<p>${inscription.commentary }</p>
						<div style="text-align: right; float: right;">
							<a class="learnMore"
								href="<%=request.getContextPath() %>/graffito/AGP-${inscription.graffitiId}"
								id="${inscription.graffitiId}"> Learn More &#10140;</a>
						</div>
					</div>
				</div>
			</div>
		</c:forEach>
	</div>
</main>