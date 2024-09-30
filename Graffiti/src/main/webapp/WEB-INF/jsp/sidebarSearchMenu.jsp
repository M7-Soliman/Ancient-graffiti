<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ page import="edu.wlu.graffiti.controller.GraffitiController"%>


<style type="text/css">
.card-header {
	border-color: #bbb;
	background-color: #eee;
	background-image: -webkit-linear-gradient(top, #ddd 0%, #eee 100%);
	background-image: linear-gradient(to bottom, #ddd 0%, #eee 100%);
	cursor: pointer;
	padding: 0px;
}

.card {
	margin-bottom: 8px;
	width: 100%;
}

.filter-group {
	width: 185px;
	float: left;
	display: block;	
}

.panel-title {
	font-size: 16px;
	padding: 10px 10px 0px 10px;
}

.card-body {
	padding: 10px 10px 10px 10px;
	font-size: smaller;
}

.btn-custom {
	width: 78px;
	margin-bottom: 0px;
	font-size: smaller;
}

.btn-keyboard {
	width: 160px;
	font-size: smaller;
}

#popupButton{
	margin-left: -6px;
}


#greekKeys input[type="button"] {
	padding-top: 2px;
	padding-right: 5px;
	padding-bottom: 2px;
	padding-left: 5px;
	border-width: 2px;
	font-size: smaller;
}

.badge-primary, .badge {
	border: 2px solid #428bca;
	margin-right: 5px;
	font-size: 12px;
}

input[type="checkbox"] {
	margin-right: 5px;
}

.checkbox {
	margin-top: 0;
	margin-bottom: 0;
	margin-left: 8px;
}

.checkbox-label {
	opacity: 1;
	border: none;
	width: 100%;
	margin-bottom: 0px;
}

.search-term-label {
	border: 2px solid #428bca;
	background-color: #337ab7;
	margin-bottom: 0px;
	line-height: 1;
}

input {
	cursor: pointer;
}

#refine, #report {
	width: 185px;
	height: 30px;
}

button:disabled {
	color: #aaa;
}

.pointer {
	cursor: pointer;
}

#po33 {
	margin-right: 0px;
}

</style>

<script>
var showGreekKeys = false;

function toggleKeyboard(){
	showGreekKeys = !showGreekKeys;
	if (showGreekKeys){
		$("#popupButton").show();
	}
	else{
		$("#popupButton").hide();	
	}	
}
</script>

<%--script for checking hierarchical checkboxes for search--%>
<script type="text/javascript">
	// Sets the child checkboxes to the same status as the parent's
	function check_sub_category(parentCheckbox, childrenName) {
		var checkboxes = document.getElementsByName(childrenName);
		for (var i = 0; i < checkboxes.length; i++) {
			checkboxes[i].checked = parentCheckbox.checked;
		}
	}

	// This function takes the checkboxes and makes the blue boxes at the top of the page
	// It does so for a checkbox and all its children (but not grandchildren)
	// Though they're just strings, the names of everything must be specified correctly
	function select_parent_and_children(parentCheckbox, childrenName,
			filterParentFunction, parentLabel) {
		filterParentFunction;
		var checkboxes = document.getElementsByName(childrenName);
		// Restore escaped apostrophes
		parentLabel = parentLabel.replace("_", "'");
		for (var i = 0; i < checkboxes.length; i++) {
			// I feel like this might not be perfectly written, but I'm not going to rock the boat
			// You'll need to add TWO else if statements for each section that could have a parent-child relationship
			// One for if the parent is selected but the child isn't, and the other for the opposite
			var necessaryAspects = checkboxes[i].value.split(", ");
			if (termExists("Property Type: " + parentLabel)
					&& !termExists("Property Type: " + necessaryAspects[0])) {
				filterBy('Property Type', necessaryAspects[0],
						necessaryAspects[1], necessaryAspects[2]);
			} else if (!termExists("Property Type: " + parentLabel)
					&& termExists("Property Type: " + necessaryAspects[0])) {
				filterBy('Property Type', necessaryAspects[0],
						necessaryAspects[1], necessaryAspects[2]);
			} else if (termExists("Street: " + parentLabel)
					&& !termExists("Segment: " + necessaryAspects[0])) {
				filterBy('Segment', necessaryAspects[0], necessaryAspects[1],
						necessaryAspects[2]);
			} else if (!termExists("Street: " + parentLabel)
					&& termExists("Segment: " + necessaryAspects[0])) {
				filterBy('Segment', necessaryAspects[0], necessaryAspects[1],
						necessaryAspects[2]);
			} else if (termExists("Poetry: " + parentLabel)
					&& !termExists("Poetry: " + necessaryAspects[0])) {
				filterBy('Poetry', necessaryAspects[0], necessaryAspects[1],
						necessaryAspects[2]);
			} else if (!termExists("Poetry: " + parentLabel)
					&& termExists("Poetry: " + necessaryAspects[0])) {
				filterBy('Poetry', necessaryAspects[0], necessaryAspects[1],
						necessaryAspects[2]);
			}
		}
	}
	
	// This function will select all the children and grandchildren of the All Poetry filter
	// It will check the boxes, apply the filter, and create the blue boxes at the top
	// Though poorly written, as of the time of writing there is only one use case--for All Poetry
	// In the future, we can make this more dynamic by generalizing the code written here
	function select_poetry() {
		// For some reason the checkbox starts off undefined
		// We set it to false to remove all other poetry filters
		this.checked = false;
		// This checks off the children and grandchildren given All Poetry's checked status
		
		//check_sub_category(this, 'Poems');
		//check_sub_category(this, 'Quotations');
		//check_sub_category(this, 'Popular');
		//check_sub_category(this, 'Meters');
		
		// We now apply the filterBy logic to the children and grandchildren as we would if there were only parents and children
		// In the future, this can be done with a for-loop but since it's just one filter now we'll keep it like this
		if (!termExists("Poetry: Literary Quotation"))  {
			select_parent_and_children(this, 'Quotations', filterBy('Poetry', 'Literary Quotation', '1', 'po1'), 'Literary Quotation');
		}
		select_parent_and_children(this, 'Quotations', filterBy('Poetry', 'Literary Quotation', '1', 'po1'), 'Literary Quotation');
		
		if (!termExists("Poetry: Popular Poetry")) {
			select_parent_and_children(this, 'Popular', filterBy('Poetry', 'Popular Poetry', '2', 'po2'), 'Popular Poetry');
		}
		select_parent_and_children(this, 'Popular', filterBy('Poetry', 'Popular Poetry', '2', 'po2'), 'Popular Poetry');
		
		if (!termExists("Poetry: Meter")) {
			select_parent_and_children(this, 'Meters', filterBy('Poetry', 'Meter', '3', 'po3'), 'Meter');
		}
		select_parent_and_children(this, 'Meters', filterBy('Poetry', 'Meter', '3', 'po3'), 'Meter');
	  
		var poemfilters = ["Poems", "Quotations", "Popular", "Meters"];
		for (var i = 0; i < poemfilters.length; i++ ) {
			var checkboxes = document.getElementsByName(poemfilters[i]);
			for (var c = 0; c < checkboxes.length; c++) {
				checkboxes[c].checked = false;
			}
		}
		
		
		// Now we enable ONLY the Poetry: All filter
		filterBy('Poetry', 'All', 'All', 'po99');
	}
</script>

<div class="filter-group" style="width: 200px;">
<nav class="navbar navbar-expand-lg navbar-light ">
	<button class="navbar-toggler" type="button" data-bs-toggle="collapse"
		data-bs-target="#filterSideBar" aria-controls="filterSideBar"
		aria-expanded="false" aria-label="Toggle navigation"
		style="width: 100%; border:none; text-align:left" id="navbar-collapser">
		<span class="navbar-toggler-icon"></span>
		<span class="visible-xs-inline">Filter</span>
		
	</button>
	
  <div class="collapse navbar-collapse flex-column" id="filterSideBar">
	<%-- City --%>
	<div class="card">
		<div class="card-header" data-bs-toggle="collapse"
			data-bs-target="#collapse2" onclick="switchArrow(2);"
			aria-expanded="true" aria-controls="collapse2">
			<h4 class="panel-title">
			<fmt:message key="browse.city"/><span id="menu2" style="float: right;">&#9660;</span>
			</h4>
		</div>
		<div id="collapse2" class="card-body collapse">
			<div class="checkbox" id="City">
				<c:forEach var="k" begin="${1}"
					end="${fn:length(requestScope.cities)}">
					<c:set var="city" value="${requestScope.cities[k-1]}" />
					<label class="checkbox-label"><input id="c${k-1}"
						type="checkbox" value=""
						onclick="filterBy('Ancient Site', '${city}', '${city}', 'c${k-1}');" />${city}</label>
				</c:forEach>
			</div>
		</div>
	</div>

	<%-- Insula --%>
	<div class="card">
		<div class="card-header" data-bs-toggle="collapse"
			data-bs-target="#collapse3" onclick="switchArrow(3);"
			aria-expanded="true" aria-controls="collapse3">
			<h4 class="panel-title">
				<fmt:message key="browse.insula"/><span id="menu3" style="float: right;">&#9660;</span>
			</h4>
		</div>
		<div id="collapse3" class="card-body collapse">
			<c:forEach var="k" begin="${1}"
				end="${fn:length(requestScope.cities)}">
				<c:set var="city" value="${requestScope.cities[k-1]}" />
				<span class="pointer" style="display: block;" data-bs-toggle="collapse"
					data-bs-target="#i_${city}" onclick="switchSign('i_${city}');">
					${city}<span class="pointer" id="expandi_${city}"
					style="float: right;">&#43;</span>
				</span>
				<div class="checkbox collapse" id="i_${city}">
					<c:forEach var="l" begin="${1}"
						end="${fn:length(requestScope.insulaList)}">
						<c:set var="insula" value="${requestScope.insulaList[l-1]}" />
						<c:if test="${insula.modernCity == city}">
							<label class="checkbox-label"><input id="i${insula.id}"
								type="checkbox" value=""
								onclick="filterBy('Insula', '${insula.fullName}', '${insula.id}', 'i${insula.id}');" />
								${insula.fullName}</label>
						</c:if>
					</c:forEach>
				</div>
			</c:forEach>
			</div>
		</div>
	

	<%-- Property --%>
	<div class="card">
		<div class="card-header" data-bs-toggle="collapse"
			data-bs-target="#collapse4" onclick="switchArrow(4);"
			aria-expanded="false" aria-controls="collapse4"
			id="property_card_header">
			<h4 class="panel-title">
				<fmt:message key="browse.property"/><span id="menu4" style="float: right;">&#9660;</span>
			</h4>
		</div>
		<div id="collapse4" class="card-body collapse" id="Property"
			aria-labelledby="property_card_header">
			<c:forEach var="k" begin="${1}"
				end="${fn:length(requestScope.cities)}">
				<c:set var="city" value="${requestScope.cities[k-1]}" />
				<div class="collapse show" id="${city}">
					<span style="display: block;" class="pointer"
						data-bs-toggle="collapse" data-bs-target="#p_${city}"
						onclick="switchSign('p_${city}');" aria-expanded="false">
						${city}<span class="pointer" id="expandp_${city}"
						style="float: right;">&#43;</span>
					</span>
					<div id="p_${city}" class="collapse" style="margin-left: 10px;">
						<c:forEach var="l" begin="${1}"
							end="${fn:length(requestScope.insulaList)}">
							<c:set var="insula" value="${requestScope.insulaList[l-1]}" />
							<c:if test="${insula.modernCity == city}">
								<span style="display: block;" class="pointer"
									data-bs-toggle="collapse" data-bs-target=".p_${insula.id}"
									onclick="switchSign('p_${insula.id}');">
									${insula.fullName}<span class="pointer"
									id="expandp_${insula.id}" style="float: right;">&#43;</span>
								</span>
								<div class="checkbox collapse p_${insula.id}">
									<c:forEach var="m" begin="${1}"
										end="${fn:length(requestScope.propertiesList)}">
										<c:set var="prop" value="${requestScope.propertiesList[m-1]}" />
										<c:if test="${prop.insula.shortName == insula.shortName}">
											<label class="checkbox-label"><input id="p${prop.id}"
												type="checkbox"
												onclick="filterBy('Property', '${insula.modernCity} ${insula.shortName}.${prop.propertyNumber} ${prop.propertyName}', '${prop.id}', 'p${prop.id}');" />${prop.propertyNumber}
												${prop.propertyName}</label>
										</c:if>
									</c:forEach>
								</div>
							</c:if>
						</c:forEach>
					</div>
				</div>
			</c:forEach>
		</div>
	</div>

	<%-- Property Type --%>
	<div class="card">
		<div class="card-header" data-bs-toggle="collapse"
			data-bs-target="#collapse5" onclick="switchArrow(5);">
			<h4 class="panel-title">
				<fmt:message key="browse.propertyType"/><span id="menu5" style="float: right;">&#9660;</span>
			</h4>
		</div>
		<div id="collapse5" class="card-body collapse">
			<div class="checkbox" id="Property_Type">
				<c:forEach var="k" begin="${1}"
					end="${fn:length(requestScope.propertyTypes)}">
					<c:set var="pt" value="${requestScope.propertyTypes[k-1]}" />
					<c:set var="parentID" value="${pt.id}" />
					<c:if test="${pt.isParent}">
						<ul style="list-style: none; padding-left: 0px;">
							<li><c:choose>
									<c:when test="${fn:length(pt.children)!=0}">
										<span style="display: inline; max-height: 20px"><span
											style="padding-left: 0px"> <label
												class="checkbox-label" style="display: inline;"><input
													id="pt${pt.id}" type="checkbox" value=""
													onchange="check_sub_category(this, '${pt.name}s'); 
							select_parent_and_children(this, '${pt.name}s', filterBy('Property Type', '${pt.name}', '${pt.id}', 'pt${pt.id }'), '${pt.name}')" />${pt.name}
											</label><span id="expandpt_${pt.id}" class="pointer"
												style="float: right;" data-bs-toggle="collapse"
												data-bs-target="#pt_${pt.id}"
												onclick="switchSign('pt_${pt.id}');">&#43;</span></span></span>

										<div class="collapse" id="pt_${pt.id}">
											<ul style="list-style: none; padding-left: 20px;">
												<c:forEach var="j" begin="${1}"
													end="${fn:length(pt.children)}">
													<c:set var="subpt" value="${pt.children[j-1]}" />
													<li><label class="checkbox-label"><input
															type="checkbox" id="pt${subpt.id}"
															value="${subpt.name}, ${subpt.id}, pt${subpt.id }"
															name="${pt.name}s"
															onchange="filterBy('Property Type', '${subpt.name}', '${subpt.id}', 'pt${subpt.id }');" />
															${subpt.name}</label></li>
												</c:forEach>
											</ul>
										</div>
									</c:when>
									<c:otherwise>
										<label class="checkbox-label"><input id="pt${pt.id}"
											type="checkbox" value=""
											onchange="check_sub_category(this, '${pt.name}s'); 
							select_parent_and_children(this, '${pt.name}s', filterBy('Property Type', '${pt.name}', '${pt.id}', 'pt${pt.id }'), '${pt.name}')" />
											${pt.name}</label>
									</c:otherwise>
								</c:choose></li>
						</ul>
					</c:if>
				</c:forEach>
			</div>
		</div>
	</div>

	<!-- Streets and Segments -->
	<div class="card">
		<div class="card-header" data-bs-toggle="collapse"
			data-bs-target="#collapse11" onclick="switchArrow(11);">
			<h4 class="panel-title">
				<fmt:message key="browse.street"/><span id="menu11" style="float: right;">&#9660;</span>
			</h4>
		</div>
		<!-- Cities Drop Down -->
		<div id="collapse11" class="card-body collapse" id="City_Street">
			<c:forEach var="x" begin="${1}"
				end="${fn:length(requestScope.citiesWithStreets)}">
				<c:set var="city"
					value="${requestScope.citiesWithStreets[x-1].name}" />
				<span style="display: block;" class="pointer" data-bs-toggle="collapse"
					data-bs-target="#str_${city}" onclick="switchSign('str_${city}');">
					${city} <span class="pointer" id="expandstr_${city}"
					style="float: right;">&#43;</span>
				</span>
				<!-- Street sub-menus -->
				<div id="str_${city}" class="collapse">
					<ul style="list-style: none; padding-left: 0px;">
						<c:forEach var="k" begin="${1}"
							end="${fn:length(requestScope.streets)}">
							<c:set var="str" value="${requestScope.streets[k-1]}" />
							<ul style="list-style: none; padding-left: 0px;">
								<c:if test="${str.city.name == city}">
									<li><span
										style="display: inline; max-height: 20px; font-size: 9pt;">
											<span style="padding-left: 0px"> <label
												class="checkbox-label"
												style="display: inline; font-weight: normal;"> <input
													id="str${str.id}" type="checkbox" value=""
													onchange="check_sub_category(this, '${str.escapedStreetName}'); 
						select_parent_and_children(this, '${str.escapedStreetName}', filterBy('Street', '${str.escapedStreetName}', '${str.id}', 'str${str.id}'), 
						'${str.escapedStreetName}');" />
													&nbsp;${str.streetName}
											</label> <span id="expandstr_${str.id}" class="pointer"
												style="float: right;" data-bs-toggle="collapse"
												data-bs-target="#str_${str.id}"
												onclick="switchSign('str_${str.id}');">&#43;</span>
										</span>
									</span> <!-- Segment submenu -->
										<div class="collapse" id="str_${str.id}">
											<ul style="list-style: none; padding-left: 20px;">
												<c:forEach var="j" begin="${1}"
													end="${fn:length(requestScope.segments)}">
													<c:set var="seg" value="${requestScope.segments[j-1]}" />
													<c:if test="${seg.street.id==str.id}">
														<li><label class="checkbox-label"
															style="font-weight: normal; font-size: 10pt; margin-bottom: 0px;">
																<input type="checkbox" id="seg${seg.id}"
																value="${seg.segmentName}, ${seg.id}, seg${seg.id}"
																name="${str.escapedStreetName}"
																onchange="filterBy('Segment', '${seg.segmentName}', '${seg.id}', 'seg${seg.id}');" />
																${seg.segmentName}
														</label></li>
													</c:if>
												</c:forEach>
											</ul>
										</div></li>
								</c:if>
							</ul>
						</c:forEach>
					</ul>
				</div>
			</c:forEach>
		</div>
	</div>

	<%-- Drawing Category --%>
	<div class="card">
		<div class="card-header" data-bs-toggle="collapse"
			data-bs-target="#collapse6" onclick="switchArrow(6);">
			<h4 class="panel-title">
				<fmt:message key="browse.drawing"/><span id="menu6" style="float: right;">&#9660;</span>
			</h4>
		</div>
		<div id="collapse6" class="card-body collapse">
			<div class="checkbox" id="Drawing_Category">
				<label class="checkbox-label"><input id="dc0"
					type="checkbox" value=""
					onclick="filterBy('Drawing Category', 'All', 'All', 'dc0');" />All</label>
				<c:forEach var="k" begin="${1}"
					end="${fn:length(requestScope.drawingCategories)}">
					<c:set var="dc" value="${requestScope.drawingCategories[k-1]}" />
					<label class="checkbox-label"><input id="dc${dc.id}"
						type="checkbox" value=""
						onclick="filterBy('Drawing Category', '${dc.name}', '${dc.id}', 'dc${dc.id}');" />${dc.name}</label>
				</c:forEach>
			</div>
		</div>
	</div>

	<%-- Writing Style --%>
	<div class="card">
		<div class="card-header" data-bs-toggle="collapse"
			data-bs-target="#collapse7" onclick="switchArrow(7);">
			<h4 class="panel-title">
				<fmt:message key="browse.writing"/><span id="menu7" style="float: right;">&#9660;</span>
			</h4>
		</div>
		<div id="collapse7" class="card-body collapse">
			<div class="checkbox"
				id="<%=GraffitiController.WRITING_STYLE_PARAM_NAME%>">
				<label class="checkbox-label"><input id="ws1"
					type="checkbox" value=""
					onclick="filterBy('<%=GraffitiController.WRITING_STYLE_SEARCH_DESC%>', 'Inscribed/Scratched', '<%=GraffitiController.WRITING_STYLE_GRAFFITI_INSCRIBED%>', 'ws1');" /><fmt:message key="browse.writinginscribed"/></label>
				<label class="checkbox-label"><input id="ws2"
					type="checkbox" value=""
					onclick="filterBy('<%=GraffitiController.WRITING_STYLE_SEARCH_DESC%>', 'charcoal', 'charcoal', 'ws2');" /><fmt:message key="browse.writingcharcoal"/></label>
				<label class="checkbox-label"><input id="ws3"
					type="checkbox" value=""
					onclick="filterBy('<%=GraffitiController.WRITING_STYLE_SEARCH_DESC%>', 'other', 'other', 'ws3');" /><fmt:message key="browse.writingother"/></label>
			</div>
		</div>
	</div>
	

	<%-- Language --%>
	<div class="card">
		<div class="card-header" data-bs-toggle="collapse"
			data-bs-target="#collapse8" onclick="switchArrow(8);">
			<h4 class="panel-title">
				<fmt:message key="browse.language"/><span id="menu8" style="float: right;">&#9660;</span>
			</h4>
		</div>
		<div id="collapse8" class="card-body collapse">
			<div class="checkbox" id="Language">
				<label class="checkbox-label"><input id="l1" type="checkbox"
					value="" onclick="filterBy('Language', 'Latin', 'Latin', 'l1');" /><fmt:message key="browse.languagelatin"/></label>
				<label class="checkbox-label"><input id="l2" type="checkbox"
					value="" onclick="filterBy('Language', 'Greek', 'Greek', 'l2');" /><fmt:message key="browse.languagegreek"/></label>
				<label class="checkbox-label"><input id="l3" type="checkbox"
					value=""
					onclick="filterBy('Language', 'Latin-Greek', 'Latin/Greek', 'l3');" /><fmt:message key="browse.languageboth"/></label>
				<label class="checkbox-label"><input id="l4" type="checkbox"
					value="" onclick="filterBy('Language', 'Other', 'other', 'l4');" /><fmt:message key="browse.languageother"/></label>
			</div>
		</div>
	</div>
	
		
	<%-- Poetry --%>
	<div class="card">
		<div class="card-header" data-bs-toggle="collapse"
			data-bs-target="#collapseP" onclick="switchArrow(10);">
			<h4 class="panel-title">
				Poetry<span id="menu10" style="float: right;">&#9660;</span>
			</h4>
		</div>
		<div id="collapseP" class="card-body collapse">
			<div class="checkbox" id="Poetry">
				<ul style="list-style: none; padding-left: 0px;">
				<li>
					<span style="display: inline; max-height: 20px">
						<span style="padding-left: 0px"> 
							<label
								class="checkbox-label" style="display: inline;">
								<input
									id="po99" type="checkbox" value=""
									onchange="select_poetry();" />All
							</label>
							<span id="expandpo_99" class="pointer"
								style="float: right;" data-bs-toggle="collapse"
								data-bs-target="#po_99"
								onclick="switchSign('po_99');">&#43;
							</span>
						</span>
					</span>
					
				<div class="collapse" id="po_99">
					
					<%-- Literary Quotations --%>
					<ul style="list-style: none; padding-left: 20px;">
						<li>
							<span style="display: inline; max-height: 20px">
								<span style="padding-left: 0px"> 
									<label 
										class="checkbox-label" style="display: inline;">
										<input name="Poems"
											id="po1" type="checkbox" value="Literary Quotation, 1, po1"
											onchange="check_sub_category(this, 'Quotations'); 
					select_parent_and_children(this, 'Quotations', filterBy('Poetry', 'Literary Quotation', '1', 'po1'), 'Literary Quotation')" />Literary Quotation
									</label>
									<span id="expandpo_1" class="pointer"
										style="float: right;" data-bs-toggle="collapse"
										data-bs-target="#po_1"
										onclick="switchSign('po_1');">&#43;
									</span>
								</span>
							</span>
							<div class="collapse" id="po_1">
								<ul style="list-style: none; padding-left: 20px;">
									<li><label class="checkbox-label"><input
											type="checkbox" id="po11"
											value="Ennius, 11, po11"
											name="Quotations"
											onchange="filterBy('Poetry', 'Ennius', '11', 'po11');" />
											Ennius</label></li>
									<li><label class="checkbox-label"><input
											type="checkbox" id="po12"
											value="Lucretius, 12, po12"
											name="Quotations"
											onchange="filterBy('Poetry', 'Lucretius', '12', 'po12');" />
											Lucretius</label></li>
									<li><label class="checkbox-label"><input
											type="checkbox" id="po13"
											value="Ovid, 13, po13"
											name="Quotations"
											onchange="filterBy('Poetry', 'Ovid', '13', 'po13');" />
											Ovid</label></li>	
									<li><label class="checkbox-label"><input
											type="checkbox" id="po14"
											value="Propertius, 14, po14"
											name="Quotations"
											onchange="filterBy('Poetry', 'Propertius', '14', 'po14');" />
											Propertius</label></li>	
									<li><label class="checkbox-label"><input
											type="checkbox" id="po15"
											value="Seneca, 15, po15"
											name="Quotations"
											onchange="filterBy('Poetry', 'Seneca', '15', 'po15');" />
											Seneca</label></li>		
									<li><label class="checkbox-label"><input
											type="checkbox" id="po16"
											value="Tibullus, 16, po16"
											name="Quotations"
											onchange="filterBy('Poetry', 'Tibullus', '16', 'po16');" />
											Tibullus</label></li>
									<li><label class="checkbox-label"><input
											type="checkbox" id="po17"
											value="Vergil, 17, po17"
											name="Quotations"
											onchange="filterBy('Poetry', 'Vergil', '17', 'po17');" />
											Vergil</label></li>							
								</ul>
							</div>
						</li>
					</ul>
					
					<%-- Popular Poetry --%>
					<ul style="list-style: none; padding-left: 20px;">
						<li>
							<span style="display: inline; max-height: 20px">
								<span style="padding-left: 0px"> 
									<label 
										class="checkbox-label" style="display: inline;">
										<input name="Poems"
											id="po2" type="checkbox" value="Popular Poetry, 2, po2"
											onchange="check_sub_category(this, 'Popular'); 
					select_parent_and_children(this, 'Popular', filterBy('Poetry', 'Popular Poetry', '2', 'po2'), 'Popular Poetry')" />Popular Poetry
									</label>
									<span id="expandpo_2" class="pointer"
										style="float: right;" data-bs-toggle="collapse"
										data-bs-target="#po_2"
										onclick="switchSign('po_2');">&#43;
									</span>
								</span>
							</span>
							<div class="collapse" id="po_2">
								<ul style="list-style: none; padding-left: 20px;">
									<li><label class="checkbox-label"><input
											type="checkbox" id="po21"
											value="candida me docuit, 21, po21"
											name="Popular"
											onchange="filterBy('Poetry', 'candida me docuit', '21', 'po21');" />
											candida me docuit...</label></li>
									<li><label class="checkbox-label"><input
											type="checkbox" id="po22"
											value="minimum malum, 22, po22"
											name="Popular"
											onchange="filterBy('Poetry', 'minimum malum', '22', 'po22');" />
											minimum malum...</label></li>	
									<li><label class="checkbox-label"><input
											type="checkbox" id="po23"
											value="quisquis amat, 23, po23"
											name="Popular"
											onchange="filterBy('Poetry', 'quisquis amat', '23', 'po23');" />
											quisquis amat...</label></li>
									<li><label class="checkbox-label"><input
											type="checkbox" id="po24"
											value="venimus huc cupidi, 24, po24"
											name="Popular"
											onchange="filterBy('Poetry', 'venimus huc cupidi', '24', 'po24');" />
											venimus huc cupidi...</label></li>			
									<li><label class="checkbox-label"><input
											type="checkbox" id="po25"
											value="Others, 25, po25"
											name="Popular"
											onchange="filterBy('Poetry', 'Others', '25', 'po25');" />
											Other</label></li>									
								</ul>
							</div>
						</li>
					</ul>
					
					<%-- Meter Type --%>
					<ul style="list-style: none; padding-left: 20px;">
						<li>
							<span style="display: inline; max-height: 20px">
								<span style="padding-left: 0px"> 
									<label 
										class="checkbox-label" style="display: inline;">
										<input name="Poems"
											id="po3" type="checkbox" value="Meter, 3, po3"
											onchange="check_sub_category(this, 'Meters'); 
					select_parent_and_children(this, 'Meters', filterBy('Poetry', 'Meter', '3', 'po3'), 'Meter')" />Meter
					<!-- select_parent_and_children(this, 'Popular', filterBy('Poetry', 'Popular Poetry', '2', 'po2'), 'Popular Poetry')" />Popular Poetry -->
									</label>
									<span id="expandpo_3" class="pointer"
										style="float: right;" data-bs-toggle="collapse"
										data-bs-target="#po_3"
										onclick="switchSign('po_3');">&#43;
									</span>
								</span>
							</span>
							<div class="collapse" id="po_3">
								<ul style="list-style: none; padding-left: 20px;">
									<li><label class="checkbox-label"><input
											type="checkbox" id="po31"
											value="Dactylic haxameter, 31, po31"
											name="Meters"
											onchange="filterBy('Poetry', 'Dactylic hexameter', '31', 'po31');" />
											Dactylic hexameter</label></li>
									<li><label class="checkbox-label"><input
											type="checkbox" id="po32"
											value="Iambic, 32, po32"
											name="Meters"
											onchange="filterBy('Poetry', 'Iambic', '32', 'po32');" />
											Iambic</label></li>	
									<li><label class="checkbox-label"><input
											type="checkbox" id="po33"
											value="Hendecasyllabic, 33, po33"
											name="Meters"
											onchange="filterBy('Poetry', 'Hendecasyllabic', '33', 'po33');" />
											Hendecasyllabic</label></li>
									<li><label class="checkbox-label"><input
											type="checkbox" id="po34"
											value="Elegiac couplet, 34, po34"
											name="Meters"
											onchange="filterBy('Poetry', 'Elegiac couplet', '34', 'po34');" />
											Elegiac couplet</label></li>	
									<li><label class="checkbox-label"><input
											type="checkbox" id="po35"
											value="Senarius, 35, po35"
											name="Meters"
											onchange="filterBy('Poetry', 'Senarius', '35', 'po35');" />
											Senarius</label></li>	
									<li><label class="checkbox-label"><input
											type="checkbox" id="po36"
											value="Pentameter, 36, po36"
											name="Meters"
											onchange="filterBy('Poetry', 'Pentameter', '36', 'po36');" />
											Pentameter</label></li>
									<li><label class="checkbox-label"><input
											type="checkbox" id="po37"
											value="Trochaic, 37, po37"
											name="Meters"
											onchange="filterBy('Poetry', 'Trochaic', '37', 'po37');" />
											Trochaic</label></li>										
								</ul>
							</div>
						</li>
					</ul>
				</div>
				</li>
				</ul>
			</div>
		</div>
	</div>	
	
	
	
	
	
	
	
	
	
	
	<%-- Images --%>
	<div class="card">
		<div class="card-header" data-bs-toggle="collapse"
			data-bs-target="#collapse9" onclick="switchArrow(9);">
			<h4 class="panel-title">
				<fmt:message key="browse.images"/><span id="menu9" style="float: right;">&#9660;</span>
			</h4>
		</div>
		<div id="collapse9" class="card-body collapse">
			<div class="checkbox" id="Photos">
				<label class="checkbox-label"><input id="ph1" type="checkbox"
					value="" onclick="filterBy('Photos', 'True', 'true', 'ph1');" /><fmt:message key="browse.imageshasphotos"/></label>
			</div>
		</div>
	</div>

	<%-- Keyword --%>
	<div class="card">
		<div class="card-header" data-bs-toggle="collapse"
			data-bs-target="#collapse1" onclick="switchArrow(1);">
			<h4 class="panel-title">
				<fmt:message key="browse.search"/><span id="menu1" style="float: right">&#9660;</span>
			</h4>
		</div>
		<div id="collapse1" class="card-body collapse">
			<input id="keyword" type="text" class="form-control"
				style="border-radius: 4px; margin-bottom: 3px;" />
			<div class="form-row"
				style="display: block; margin-left: auto; margin-right: auto">
				<button class="btn btn-agp btn-custom" onclick="contentSearch();"
					data-bs-toggle="tooltip" data-placement="bottom"
					title="Perform a search based only on the text of the graffiti">Text</button>
				<button class="btn btn-agp btn-custom" onclick="cilSearch();"
					data-bs-toggle="tooltip" data-placement="bottom"
					title="Perform a search based only on the CIL number">CIL #</button>
			</div>
			<button class="btn btn-agp btn-keyboard" onclick="globalSearch();"
				style="margin-bottom: 3px; margin-top: 3px" data-bs-toggle="tooltip"
				data-placement="bottom"
				title="Perform a search based on all data fields">Global</button>
<!-- 			<a href="#popupButton" role="button" class="btn btn-agp btn-keyboard" -->
<!-- 				data-bs-toggle="popover" data-placement="bottom">Greek Alphabet</a> -->
			<button class="btn btn-agp btn-keyboard" onclick="toggleKeyboard();" 
			 data-bs-toggle="popover" data-placement="bottom">Greek Alphabet</button>

<!-- 			<button type="button" class="btn btn-agp btn-keyboard" -->
<!-- 				data-bs-toggle="popover" data-placement="bottom" -->
<!-- 				data-content="And here's some amazing content. It's very engaging. Right?">Click -->
<!-- 				to toggle popover</button> -->

			<!-- The Greek Keyboard -->
			<div id="popupButton">
				<div>
					<table class="center">
						<tr>
							<td nowrap align="center"><div id="greekKeys"></div>
						</tr>
					</table>
				</div>
			</div>
			</div>
		</div>
	</div>
</nav>
</div>

<script type="text/javascript">

	function createButton(i) {
		var textBox = document.getElementById("keyword");
		var v = document.createElement("input");
		v.type = "button";
		v.value = String.fromCharCode(i);
		v.addEventListener("click", function(event) {
			textBox.value += this.value;
		});
		document.getElementById("greekKeys").appendChild(v);
	}

	// Create the keyboard buttons
	window.onload = function() {
		checkboxesAfterBack();
		
		// Initially hide the Greek Keyboard
		$("#popupButton").hide();

		// Uncomment this and the matching code in graffitiController if
		// you want the window to jump to the previous result on browsing
		<c:if test="${not empty sessionScope.returnFromDetails}">
		var edrElement = document
				.getElementById("${sessionScope.returnFromDetails}");
		if (edrElement) {
			edrElement.scrollIntoView();
		}
		<c:set var="returnFromDetails" value="" scope="session" />
		</c:if>

		var brCount = 1;

		for (var i = 945; i < 962; i++) {
			createButton(i);
			if (brCount == 8) {
				var brTag = document.createElement("br");
				document.getElementById("greekKeys").appendChild(brTag);
				brCount = 0;
			}
			brCount++;
		}
		for (var i = 963; i < 970; i++) {
			createButton(i);
			if (brCount == 8) { // make a new line every 8 characters
				var brTag = document.createElement("br");
				document.getElementById("greekKeys").appendChild(brTag);
				brCount = 0;
			}
			brCount++;
		}
	}
</script>
