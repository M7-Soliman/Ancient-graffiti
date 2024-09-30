<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<main>
	<table class="table table-striped table-bordered"
		id="featured_graffiti_table">
		<thead>
			<tr>
				<th id="idCol">ID</th>
				<th id="textCol">Text (Latin or Greek)</th>
				<th id="transCol">Translation</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach var="i" items="${translationHits}">
				<tr>
					<th><a
						href="<%=request.getContextPath() %>/graffito/${i.agpId}">${i.agpId}</a>
						<c:if test="${not empty i.cil }">
							<br />CIL: ${i.cil}
						</c:if> <c:if test="${not empty i.langner }">
							<br />Lagner: ${i.langner}
						</c:if></th>
					<td><em>${i.contentWithLineBreaks }</em></td>
					<td><c:if test="${not empty i.contentTranslation }">
							<p class="trans" style="display: none;">
								<em>${i.contentTranslation}</em>
							</p>
							<input type="button" class="btn btn-agp showTrans"
								value="Show Translation">
						</c:if></td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
</main>