<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ page import="edu.wlu.graffiti.controller.GraffitiController"%>

<style type="text/css">
.panel-default>.panel-heading {
	border-color: #bbb;
	background-image: -webkit-linear-gradient(top, #d0d0d0 0%, #dfdfdf 100%);
	background-image: linear-gradient(to bottom, #d0d0d0 0%, #dfdfdf 100%);
	margin-top: 5px;
	cursor: pointer;
}

.top-panel-heading {
	margin-top: 0px;
}

.panel-default {
	border-color: #fff;
}

table.center {
	margin-left: auto;
	margin-right: auto;
}

.label-primary, .label {
	border: 2px solid #428bca;
	margin-right: 1px;
	font-size: 12px;
}

.large-font {
	font-size: 16px;
}

.checkbox-label {
	opacity: 1;
	border: none;
	width: 100%;
}

.checkbox {
	margin-top: 0;
	margin-bottom: 0;
}

.panel-body {
	padding-top: 20px;
}

button:disabled {
	color: #aaa;
}

.pointer {
	cursor: pointer;
}

select {
	min-width: 140px;
	width: auto;
}
</style>



<div class="panel-group" style="width: 20%; margin-left: 1.75%; margin-top: 15pt; float:left;">
	<div class="panel panel-default">

		
		<%-- Filters for the people index --%>
		<% if (request.getAttribute("index").equals("people")){ %>
		
			<%-- Name Types --%>
			<div class="panel-heading" data-bs-toggle="collapse"
				data-bs-target="#name_collapse" onclick="switchArrow('name');">
				<h3 class="panel-title">
					Name Type
					<span id="name_menu" style="float: right;">&#9660;</span>
				</h3>
			</div>
			<div id="name_collapse" class="panel-collapse collapse">
				<div class="panel-body">
					<div class="checkbox" id="name_type">
						<label class="checkbox-label">
							<input id="praenomen" class="name-check" type="checkbox" value="praenomen" onclick="updateOrdering()" />
							Praenomen
						</label>
						<label class="checkbox-label">
							<input id="gentilicium" class="name-check" type="checkbox" value="gentilicium" onclick="updateOrdering()" />
							Gentilicium
						</label>
						<label class="checkbox-label">
							<input id="cognomen" class="name-check" type="checkbox" value="cognomen" onclick="updateOrdering()" />
							Cognomen
						</label>
					</div>
				</div>
			</div>
			
			<%-- Person Type --%>
			<div class="panel-heading" data-bs-toggle="collapse"
				data-bs-target="#person_collapse" onclick="switchArrow('person');">
				<h3 class="panel-title">
					Person Type
					<span id="person_menu" style="float: right;">&#9660;</span>
				</h3>
			</div>
			<div id="person_collapse" class="panel-collapse collapse">
				<div class="panel-body">
					<div class="checkbox" id="person_type">
						<label class="checkbox-label">
							<input id="attested" class="person-check" type="checkbox" value="attested" onclick="updateOrdering()" />
							Attested
						</label>
						<label class="checkbox-label">
							<input id="emperor" class="person-check" type="checkbox" value="emperor" onclick="updateOrdering()" />
							Emperor
						</label>
						<label class="checkbox-label">
							<input id="divine" class="person-check" type="checkbox" value="divine" onclick="updateOrdering()" />
							Divine
						</label>
						<label class="checkbox-label">
							<input id="consular" class="person-check" type="checkbox" value="consular" onclick="updateOrdering()" />
							Consular
						</label>
						<label class="checkbox-label">
							<input id="other" class="person-check" type="checkbox" value="other" onclick="updateOrdering()" />
							Other
						</label>
					</div>
				</div>
			</div>
			
			<%-- Gender --%>
			<div class="panel-heading" data-bs-toggle="collapse"
				data-bs-target="#gender_collapse" onclick="switchArrow('gender');">
				<h3 class="panel-title">
					Gender
					<span id="gender_menu" style="float: right;">&#9660;</span>
				</h3>
			</div>
			<div id="gender_collapse" class="panel-collapse collapse">
				<div class="panel-body">
					<div class="checkbox" id="gender">
						<label class="checkbox-label">
							<input id="masculine" class="gender-check" type="checkbox" value="masculine" onclick="updateOrdering()" />
							Masculine
						</label>
						<label class="checkbox-label">
							<input id="feminine" class="gender-check" type="checkbox" value="feminine" onclick="updateOrdering()" />
							Feminine
						</label>
						<label class="checkbox-label">
							<input id="neuter" class="gender-check" type="checkbox" value="neuter" onclick="updateOrdering()" />
							Neuter
						</label>
					</div>
				</div>
			</div>
			
		<%} %>
		
		
		
		
		<%-- Languages --%>
		<% if (!request.getAttribute("index").equals("figural-terms") && !request.getAttribute("index").equals("figural")){ %>
			<div class="panel-heading" data-bs-toggle="collapse"
				data-bs-target="#lang_collapse" onclick="switchArrow('lang');">
				<h3 class="panel-title">
					Language
					<span id="lang_menu" style="float: right;">&#9660;</span>
				</h3>
			</div>
			<div id="lang_collapse" class="panel-collapse collapse">
				<div class="panel-body">
					<div class="checkbox" id="lang">
						<label class="checkbox-label">
							<input id="latin" class="lang-check" type="checkbox" value="latin" onclick="updateOrdering()" />
							Latin
						</label>
						<label class="checkbox-label lang-check">
							<input id="greek" class="lang-check"type="checkbox" value="greek" onclick="updateOrdering()" />
							Greek
						</label>
						<label class="checkbox-label">
							<input id="greek-latin" class="lang-check" type="checkbox" value="greek-latin" onclick="updateOrdering()" />
							Greek-Latin
						</label>
					</div>
				</div>
			</div>
		<%} %>
		
		<!-- Ancient City Filter -->
		<div class="panel-heading" data-bs-toggle="collapse"
				data-bs-target="#city_collapse" onclick="switchArrow('city');">
				<h3 class="panel-title">
					Ancient City
					<span id="city_menu" style="float: right;">&#9660;</span>
				</h3>
			</div>
			<div id="city_collapse" class="panel-collapse collapse">
				<div class="panel-body">
					<div class="checkbox" id="city">
						<label class="checkbox-label">
							<input id="Herculaneum" class="city-check" type="checkbox" value="Herculaneum" onclick="updateOrdering()" />
							Herculaneum
						</label>
						<label class="checkbox-label lang-check">
							<input id="Pompeii" class="city-check"type="checkbox" value="Pompeii" onclick="updateOrdering()" />
							Pompeii
						</label>
						<label class="checkbox-label">
							<input id="Stabiae" class="city-check" type="checkbox" value="Stabiae" onclick="updateOrdering()" />
							Stabiae
						</label>
						<label class="checkbox-label">
							<input id="Smyrna" class="city-check" type="checkbox" value="Smyrna" onclick="updateOrdering()" />
							Smyrna
						</label>
					</div>
				</div>
			</div>
	</div>
</div>

<div class="panel-group" style="width: 20%; margin-right: 1%; margin-top: 20pt; float:right;">
		<%-- Order By --%>
		<h5 style="display:inline;">Order:</h5>
		<select id="sortParam" onchange="updateOrdering()">
			<option value="alpha">Alphabetically</option>
			<option value="appear">By Appearance</option>
		</select>
</div>



