// make sure that if there are globals, they are the same for Pompeii and Herculaneum.  Consider refactoring.

// an orangey-yellow
var DEFAULT_COLOR = '#F4BF77';
// a maroon
var SELECTED_COLOR = '#800000';
var DEFAULT_BORDER_COLOR = "black";
var SELECTED_BORDER_COLOR = "#B00000";

var STREET_DEFAULT_COLOR = '#989898';
var STREET_BORDER_COLOR = "#A9A9A9"; // dark-grey

//This HAS to be declared here!!!  Otherwise the scope is not global!
var hercMap;

function initHerculaneumMap(zoomLevel="regio", showHover=true, colorDensity=false,
							interactive=true, idOfFeatureToHighlight=0, propertyIdListToHighlight=[],
							zoomOnOneProperty=false, isFacadeView=false, strvsec_toggle=false, showStreets=true) {
	

	var PROPERTY_BORDER_COLOR = "white";

	var INSULA_BORDER_WEIGHT = 2;
	var HOVER_BORDER_WEIGHT = 2;
	var SELECTED_BORDER_WEIGHT = 2;

	var REGIO_VIEW_ZOOM = 17;
	var INSULA_VIEW_ZOOM = 18;
	var PROPERTY_VIEW_ZOOM = 19;
	var MAX_ZOOM = 20;

	var zoomLevels = {};
	zoomLevels["regio"] = REGIO_VIEW_ZOOM;
	zoomLevels["insula"] = INSULA_VIEW_ZOOM;
	zoomLevels["property"] = PROPERTY_VIEW_ZOOM;
	
	// sets the MapBox access token
	var mapboxAccessToken = 'pk.eyJ1IjoibWFydGluZXphMTgiLCJhIjoiY2lxczduaG5wMDJyc2doamY0NW53M3NnaCJ9.TeA0JhIaoNKHUUJr2HyLHQ';
	var borderColor;
	var fillColor;
	var southWest = L.latLng(40.8040619, 14.343131),
	northEast = L.latLng(40.8082619, 14.351131),
	bounds = L.latLngBounds(southWest, northEast);
	
	if( !interactive ) {
		INSULA_BORDER_WEIGHT=1;
		SELECTED_BORDER_COLOR = "white";
		SELECTED_BORDER_WEIGHT=1;
	}
		
	var currentZoomLevel = zoomLevels[zoomLevel];	
	
	var showInsulaMarkers;
	var totalInsulaGraffitisDict=new Array();
	var graffitiInLayer;
	var numberOfGraffitiInGroup;
	
	var insulaMarkersList=[];
	var clickedAreas = [];
	
	var insulaGroupIdsList=[];
	var insulaShortNamesDict=[];
	
	// Fires when the map is initialized
	hercMap = new L.map('herculaneummap', {
		center: [40.8059119, 14.3473933], 
		zoom: currentZoomLevel,
		minZoom: REGIO_VIEW_ZOOM,
		maxZoom: MAX_ZOOM,
		maxBounds: bounds,
	});
	
	var herculaneumProperties = L.geoJson(herculaneumPropertyData, {
		style: propertyStyle,
	    onEachFeature: onEachProperty
	}).addTo(hercMap);
	
	var herculaneumSections = L.geoJson(herculaneumSectionData, {
		style: facadeStyle,
		onEachFeature: onEachProperty
	}).addTo(hercMap);
	
	var herculaneumStreets = L.geoJson(herculaneumStreetData, {
		style: facadeStyle,
		onEachFeature: onEachProperty
	}).addTo(hercMap);
	
	var herculaneumPropertiesNotIA = L.geoJson(herculaneumPropertyData, {
		style: propertyNotIAStyle,
	}).addTo(hercMap);
	
	var herculaneumSectionsNotIA = L.geoJson(herculaneumStreetData, {
		style: sectionNotIAStyle,
	}).addTo(hercMap);
	
	var herculaneumInsulae = L.geoJson(herculaneumInsulaData, {
		style: insulaStyle
	}).addTo(hercMap);
	
	// Sinks with mapbox(?), why do we need access tokens security?
	var mapboxUrl = 'https://api.mapbox.com/styles/v1/martineza18/ciqsdxkit0000cpmd73lxz8o5/tiles/256/{z}/{x}/{y}?access_token=' + mapboxAccessToken;
	
	/*
	 * var propertyLevelLegend = L.control({position: 'bottomright'});
	 * 
	 * propertyLevelLegend.onAdd = function (map) { var div =
	 * L.DomUtil.create('div', 'info legend'), grades = [0, 5, 10], labels = [],
	 * from, to; labels.push( '<i style="background:' + getFillColor(0) + '"></i> ' +
	 * 0);
	 * 
	 * for (var i = 0; i < grades.length; i++) { from = grades[i]; to = grades[i +
	 * 1];
	 * 
	 * labels.push( '<i style="background:' + getFillColor(from + 1) + '"></i> ' +
	 * (from+1) + (to ? '&ndash;' + to : '+')); }
	 * 
	 * div.innerHTML = labels.join('<br>'); return div; };
	 * 
	 * var insulaLevelLegend = L.control({position: 'bottomright'});
	 * 
	 * insulaLevelLegend.onAdd = function (map) { var div =
	 * L.DomUtil.create('div', 'info legend'), //grades = [0, 5, 10, 20, 30, 40,
	 * 60, 80, 100, 130, 160, 190, 210, 240, 270, 300, 330, 360, 390, 420, 460,
	 * 500], grades = [0, 5, 10, 20, 30, 40, 60, 80], labels = [], from, to;
	 * 
	 * labels.push( '<i style="background:' + getFillColor(0) + '"></i> ' +
	 * 0);
	 * 
	 * for (var i = 0; i < grades.length; i++) { from = grades[i]; to = grades[i +
	 * 1];
	 * 
	 * labels.push( '<i style="background:' + getFillColor(from + 1) + '"></i> ' +
	 * (from+1) + (to ? '&ndash;' + to : '+')); }
	 * 
	 * div.innerHTML = labels.join('<br>'); return div; };
	 */
	
	if(!showStreets){
		hercMap.removeLayer(herculaneumStreets);
		hercMap.removeLayer(herculaneumSections);
	}
	else if (showStreets && zoomOnOneProperty){
		herculaneumStreets.bringToFront();
	}
	else{
		herculaneumStreets.bringToBack();
	}
	
	if(interactive){
		makeTotalInsulaGraffitiDict();
		makeInsulaIdsListShortNamesList();
		displayInsulaLabels();
		// insulaLevelLegend.addTo(hercMap);
		// legend.remove(hercMap);
		
		// This sets the map to insula view at start
		// hercMap.removeLayer(herculaneumSections);
		hercMap.addLayer(herculaneumInsulae);
		hercMap.addLayer(herculaneumProperties);
		hercMap.removeLayer(herculaneumPropertiesNotIA);
		hercMap.addControl(new L.Control.Compass({autoActive: true, position: "bottomleft"}));
	}
	else {
		hercMap.removeLayer(herculaneumPropertiesNotIA);
		hercMap.removeLayer(herculaneumSectionsNotIA);
	}
	
	// A listener for zoom events.
	hercMap.on('zoomend', function(e) {
		dealWithInsulaLevelView();
		dealWithInsulaLabelsAndSelectionOnZoom();
	});
	
	if( idOfFeatureToHighlight!=0 || propertyIdListToHighlight.length == 1 ) {
		showCloseUpView();
	}
	
	// Centers the map around a single property
	function showCloseUpView(){
		if(idOfFeatureToHighlight==null && propertyIdListToHighlight.length == 1){
			idOfFeatureToHighlight = propertyIdListToHighlight[0];
		}
		var newCenterCoordinates=[];
		hercMap.eachLayer(function(layer){
			if(layer.feature!=undefined){
				if(layer.feature.properties.Property_Id==idOfFeatureToHighlight ||
					layer.feature.properties.section_id==idOfFeatureToHighlight ||
					layer.feature.properties.street_id==idOfFeatureToHighlight){
					newCenterCoordinates=layer.getBounds().getCenter();
					hercMap.setView(newCenterCoordinates,PROPERTY_VIEW_ZOOM);
				}
			}
		});
	}
	
	// Responsible for showing the map view on the insula level.
	function dealWithInsulaLevelView(){
		// TODO: handle legend changes depending on zoom level
		// if(propertyLevelLegend._map) {
		// propertyLevelLegend.remove(hercMap);
		// }
		// insulaLevelLegend.addTo(hercMap);
	}
	
	// Returns a new array with the contents of the previous index absent
	// We must search for a string in the array because, again, indexOf does not
	// work for nested lists.
	function removeStringedListFromArray(someArray,stringPortion){
		var newArray=[];
		var i;
		for(i=0;i<someArray.length;i++){
			if(""+someArray[i]!=stringPortion){
				newArray.push(someArray[i]);
			}
		}
		return newArray;
	}
	
	// Shows or hides insula labels depending on zoom levels and if the map is
	// interactive
	function dealWithInsulaLabelsAndSelectionOnZoom(){
		// console.log("5");
		if(interactive){
			if(!reachedInsulaViewZoomLevel()){
				if(showInsulaMarkers){
					// removeInsulaLabels();
					showInsulaMarkers=false;
					// This shows selected properties from the insula when the
					// map zooms in.
					// updateBorderColors();
				}
			}
			else if(!showInsulaMarkers){
				// displayInsulaLabels();
				showInsulaMarkers=true;
			}
		}
	}
	
	// Builds the global list of insula ids.
	function makeInsulaIdsListShortNamesList(){
		var currentInsulaId=183;
		hercMap.eachLayer(function(layer){
			if(layer.feature!=undefined){
				if(layer.feature.properties.insula_id!=currentInsulaId){
					if(insulaGroupIdsList.indexOf(currentInsulaId)==-1){
						insulaGroupIdsList.push(currentInsulaId);
					}
				}
				currentInsulaId=layer.feature.properties.insula_id;
				insulaShortNamesDict[currentInsulaId]=layer.feature.properties.short_insula_name;	
			}
		});
	}
	
	// Builds the dictionary of the graffiti in each insula
	// This works well as graffiti numbers should not change over the session.
	// Modifies the closure-wide variable once and only once at the beginning of
	// the program
	function makeTotalInsulaGraffitiDict(){
		totalInsulaGraffitisDict=new Array();
		hercMap.eachLayer(function(layer){
			if(colorDensity && layer.feature!=undefined){
				graffitiInLayer=layer.feature.properties.Number_Of_Graffiti;
				currentInsulaNumber=layer.feature.properties.insula_id;
				if(totalInsulaGraffitisDict[currentInsulaNumber]!=undefined){
					totalInsulaGraffitisDict[currentInsulaNumber]+=graffitiInLayer;
				}
				else {
					totalInsulaGraffitisDict[currentInsulaNumber]=graffitiInLayer;
				}
			}
		});
	}
	
	var createLabelIcon = function(labelClass,labelText){
		return L.divIcon({ 
			className: labelClass,
			html: labelText
		});
	}
	
	// Shows the names of each insula at the center of the feature.
	function displayInsulaLabels(){
		herculaneumInsulae.eachLayer(function(layer){
			// layer.bindTooltip(layer.feature.properties.name, {permanent:true,
			// direction:'centered'}).addTo(hercMap);
			if(layer.feature!=undefined){
				var myIcon = L.divIcon({ 
					className: "labelClassHerculaneum",
					html: formatInsulaLabels(layer.feature.properties.name),
					// shifting the iconAnchor to get the labels for the insula
					// a little closer to center of the insula
					iconAnchor: shiftIconAnchor(layer.feature.properties.name)
				});
				var myMarker=new L.marker(layer.getBounds().getCenter(), {icon: myIcon}).addTo(hercMap);
				insulaMarkersList.push(myMarker);
			}
		});
	}
	
	// shift labels of the insulae to make them better centered
	function shiftIconAnchor(insulaName){
		if (insulaName.valueOf() == new String("Insula Orientalis I").valueOf()){
			return [27,30];
		}
		else if (insulaName.valueOf() == new String("Insula Orientalis II").valueOf()){
			return [55,5];	
		}
		else if (insulaName.valueOf() == new String("Insula II").valueOf()){
			return[26,27];
		}
		else if (insulaName.valueOf() == new String("Insula VII").valueOf()){
			return [40, 30];
		}
		else if (insulaName.valueOf() == new String("Insula III").valueOf()){
			return [25, 10];
		}
		else{
			return [25,15];
		}
	}
	
	// make Insula Orientalis I be on two lines
	function formatInsulaLabels(insulaName){
		if (insulaName.valueOf() == new String("Insula Orientalis I").valueOf()){
			return "Insula<br/>Orientalis I";
		}
		else{
			return insulaName;
		}
	}
	
	function reachedInsulaViewZoomLevel(){
		currentZoomLevel=hercMap.getZoom();
		return currentZoomLevel<=INSULA_VIEW_ZOOM;
	}
	
	function reachedIndividualPropertyZoomLevel(){
		currentZoomLevel=hercMap.getZoom();
		return currentZoomLevel>=PROPERTY_VIEW_ZOOM;
	}
	
	function getBorderColorForCloseZoom(feature){
		if (isFeatureSelected(feature) || isFacadeView) {
			return SELECTED_BORDER_COLOR;
		}
		return PROPERTY_BORDER_COLOR;
	}
	
	// Bring highlighted features to the front of the map
	function bringHighlightedToFront(layer){
		if (isFeatureSelected(layer.feature)){
			layer.bringToFront();
		}
	}
	
	function isFeatureSelected(feature) {
		return isPropertySelected(feature) || isSegmentSelected(feature) || isStreetSelected(feature);
	}
	
	function isPropertySelected(feature) {
		return feature.properties.clicked == true || feature.properties.Property_Id==idOfFeatureToHighlight || propertyIdListToHighlight.indexOf(feature.properties.Property_Id)>=0;
	}
	
	function isSegmentSelected(feature) {
		return feature.properties.clicked == true || feature.properties.section_id==idOfFeatureToHighlight || propertyIdListToHighlight.indexOf(feature.properties.section_id)>=0;
	}
	
	function isStreetSelected(feature) {
		return feature.properties.clicked == true || feature.properties.street_id==idOfFeatureToHighlight || propertyIdListToHighlight.indexOf(feature.properties.street_id)>=0;
	}
	
	// Sets the style of the portions of the map. Color is the outside borders.
	// There are different colors for
	// clicked or normal fragments. When clicked, items are stored in a
	// collection. These collections will have the color
	// contained inside of else.
	function propertyStyle(feature) {
		// Displays the insula level view at the start of the run if necessary
		borderColor = getBorderColorForCloseZoom(feature);
		weight = 1;
		if( isFeatureSelected(feature) ) {
			fillColor = SELECTED_COLOR;
			weight = SELECTED_BORDER_WEIGHT;
		}
		else if( colorDensity ) {
			fillColor = getFillColor( feature.properties.Number_Of_Graffiti);
		} else {
			fillColor = getFillColorByFeature(feature);
		}
		return { 
			fillColor:fillColor,
			weight: weight,
			color: borderColor,
			fillOpacity: 1,
			zIndex: 1,
		};
	}
	
	function propertyNotIAStyle(feature) {
		return { 
			fillColor:'#888', 
			fillOpacity: 0.7,
			color: "#383838", // dark-grey
			weight: 1,
			opacity: 1,
			zIndex: 2,
		};
	}
	
	function sectionNotIAStyle(feature) {
		return { 
			fillColor:STREET_DEFAULT_COLOR, 
			fillOpacity: 0.4,
			color: STREET_BORDER_COLOR,
			weight: 1,
			opacity: .8,
			zIndex: 2,
		};
	}
	
	function insulaStyle(feature) {
		borderColor=DEFAULT_BORDER_COLOR;
		fillColor = DEFAULT_COLOR;
		
		// TODO: Check if colorDensity and color insula based on total number of
		// graffiti in insula
		
		return { 
			fillColor:fillColor,
			fillOpacity: 0.7,
			color: borderColor,
			weight: INSULA_BORDER_WEIGHT,
			opacity: 1,
			zIndex: 2,
		};
	}
	
	function facadeStyle(feature) {
		borderColor=DEFAULT_BORDER_COLOR;
		fillOpacity = 1;// 0.7;
		
		if(!interactive){
			weight=0;
		}
		else{
			weight=1;
		}
		
		if( isFeatureSelected(feature)) {
			fillColor = SELECTED_COLOR;
		}
		else if( !interactive ) {
			fillColor = STREET_DEFAULT_COLOR;
		}
		else{
			fillColor = DEFAULT_COLOR;
		}
		
		return { 
			fillColor:fillColor,
			fillOpacity: fillOpacity,
			color: borderColor,
			weight: weight
		};
	}
	
	function getFillColor(numberOfGraffiti){
		if(colorDensity){
			if( reachedInsulaViewZoomLevel() ) { // for insula level
				return numberOfGraffiti == 0   ? '#FFEDC0' :
					   numberOfGraffiti <= 5   ? '#FFEDA0' :
					   numberOfGraffiti <= 10  ? '#fed39a' :
					   numberOfGraffiti <= 20  ? '#fec880' :
					   numberOfGraffiti <= 90 ? '#FEB24C':
												 '#000000';
			} else { // for property level
				return numberOfGraffiti == 0   ? '#FFEDC0' :
					   numberOfGraffiti <= 5   ? '#FFEDA0' :
					   numberOfGraffiti <= 10  ? '#fed39a' :
					   numberOfGraffiti <= 90  ? '#fec880' :
												 '#000000';
			}
		}
		
		return DEFAULT_COLOR;
	}
	
	function getFillColorByFeature(feature){
		if( isFeatureSelected(feature)) {
			return SELECTED_COLOR;
		}
		return DEFAULT_COLOR;
	}
	
	// Sets color for properties which the cursor is moving over.
	function highlightFeature(e) {
		var layer = e.target;
		
		layer.setStyle({
			fillOpacity: .8,
			weight: HOVER_BORDER_WEIGHT
		});
	
		if (!L.Browser.ie && !L.Browser.opera) {
			layer.bringToFront();
			// keep outline of insulae at the front when hovering over
			// properties
			herculaneumInsulae.bringToFront();
		}
		info.update(layer.feature.properties);
	}
	
	/* What to do when property clicked */
	function showDetails(e) {
		var layer = e.target;
		if( layer.feature.properties.clicked == null ) {
			// create the clicked attribute; will flip value shortly
			layer.feature.properties.clicked = false;
		}
		// If they have been clicked and are clicked again, sets to
		// false and vice versa.
		layer.feature.properties.clicked = !layer.feature.properties.clicked;

		if(layer.feature.properties.clicked ) {
			layer.setStyle({
				fillColor:SELECTED_COLOR,
				color:SELECTED_BORDER_COLOR,
				weight:SELECTED_BORDER_WEIGHT
				});
			clickedAreas.push(layer);
		} else {
			resetHighlight(e);
			var index = clickedAreas.indexOf(layer);
			if(index > -1) {
				clickedAreas.splice(index, 1);
			}
		}
		if (!L.Browser.ie && !L.Browser.opera) {
	        layer.bringToFront();
	    }
		displayHighlightedRegions();
		info.update(layer.feature.properties);
		document.getElementById("clearbutton").disabled = false;
	}
	
	
	// Used to reset the color, size, etc of items to their default state(ie.
	// after being clicked twice)
	function resetHighlight(e) {
		if(interactive){
			herculaneumProperties.resetStyle(e.target);
			info.update();
		}
	}

	// Calls the functions on their corresponding events for the feature
	function onEachProperty(feature, layer) {
		if( interactive ) {
			layer.on({
		        click: showDetails,
		        mouseover: highlightFeature,
		        mouseout: resetHighlight,
		    });
		}
	}
	
	var info = L.control();
	info.onAdd = function(map) {
		// create a div with a class "info"
		this._div = L.DomUtil.create('div', 'info'); 
	    this.update();
	    return this._div;
	};
	
	// method to update the control based on feature properties passed
	function updateHoverText(){
		info.update = function (props) {
			if(showHover){
				if (!isFacadeView){
					this._div.innerHTML = (props ? props.Property_Address + " " + props.Property_Name
							: 'Hover over property to see name');
				}
				else{
					if (strvsec_toggle){
						this._div.innerHTML = (props ? props.street_name + " (" + props.section_name + ")"
								: 'Hover over segment to see name');
					}
					else{
						this._div.innerHTML = (props ? props.street_name
								: 'Hover over street to see name');
					}
				}
			}
		};
	
		info.addTo(hercMap);
	}
	
	updateHoverText();
	
	// Used to acquire all of the items clicked for search(red button "Click
	// here to search).
	// Does this by iterating through the list of clicked items and adding them
	// to uniqueClicked,
	// then returning uniqueClicked.
	function getUniqueClicked() {
		var uniqueClicked = [];
		var length = clickedAreas.length;
		for (var i = 0; i < length; i++) {
			var property = clickedAreas[i];
			if (!uniqueClicked.includes(property)) {
				uniqueClicked.push(property)
			}
		}
		return uniqueClicked;
	}

	// Collects the ids of the clicked item objects(the id property).
	function collectClicked() {
		var propIdsOfClicked = [];
		var segIdsOfClicked = [];
		var strIdsOfClicked = [];
		var selectedProps = getUniqueClicked();
		var length = selectedProps.length;
		for (var i=0; i<length; i++) {
			var property = selectedProps[i];
			if (property.feature.properties.Property_Id != undefined){
				var propertyID = property.feature.properties.Property_Id;
				propIdsOfClicked.push(propertyID);
			}
			else if (property.feature.properties.section_id != undefined){
				var segID = property.feature.properties.section_id;
				segIdsOfClicked.push(segID);
			}
			else if (property.feature.properties.street_id != undefined){
				var strID = property.feature.properties.street_id;
				strIdsOfClicked.push(strID);
			}
		}
		return [propIdsOfClicked, segIdsOfClicked, strIdsOfClicked];
	}
	
	// function called when user clicks the search button.
	function searchProperties() {
		var highlighted = collectClicked();
		var argString = "";
		if (highlighted[0].length > 0 || highlighted[1].length > 0 || highlighted[2].length > 0){
			for (var i = 0; i < highlighted[0].length; i++) {
				argString += "property=" + highlighted[0][i] + "&";
			}
			for (var i = 0; i < highlighted[1].length; i++) {
				argString += "segment=" + highlighted[1][i] + "&";
			}
			for (var i = 0; i < highlighted[2].length; i++) {
				argString += "street=" + highlighted[2][i] + "&";
			}
			window.location = "results?" + argString;
			return true;
		}
	}
	
	//function called when user clicks clear selected button.
	function clearSelected() {
		window.location.reload();
	}

	
	// Displays the selected areas and their corresponding information in
	// an HTML table formatted.
	// Achieved by mixing html and javascript, accessing text properties of the
	// regions(items).
	function displayHighlightedRegions() {
		var clickedAreasTable = getUniqueClicked();
		
		var html = "<strong>Selected Areas:</strong><ul>";
		var length = clickedAreasTable.length;
		for (var i=0; i<length; i++) {
			var property = clickedAreasTable[i];
			if (property.feature.properties.clicked) {
				if (property.feature.properties.Property_Address != null){
					html += "<li>" +property.feature.properties.Property_Address + ", " +property.feature.properties.Property_Name +
							"<p>"+property.feature.properties.Number_Of_Graffiti+" graffiti</p>"+ "</li>";
				}
				else if (property.feature.properties.section_name != null) {
					html += "<li>" + property.feature.properties.street_name + " (" + property.feature.properties.section_name +
							")<p>"+property.feature.properties.number_of_graffiti+" graffiti</p>"+ "</li>";
				}
				else if (property.feature.properties.street_name != null) {
					html += "<li>" + property.feature.properties.street_name +
							"<p>"+property.feature.properties.number_of_graffiti+" graffiti</p>"+ "</li>";
				}
			}
		}
		html += "</ul>";
		// Checks to avoid error for element is null.
		var elem = document.getElementById("toSearch");
		if(typeof elem !== 'undefined' && elem !== null) {
			document.getElementById("toSearch").innerHTML = html;
		}
	}
	
	function togglePropertyVsFacade(){
		if(!isFacadeView){
			// Redraws the map, displaying Streets and Segments
			hercMap.addLayer(herculaneumPropertiesNotIA);
			hercMap.removeLayer(herculaneumSectionsNotIA);
			$("#streetsection").show();
			document.getElementById("default_street").checked = true;
			strvsec_toggle = false;
			herculaneumStreets.bringToFront();
		}
		else{	
			// Redraws the map, displaying Insulae and Properties
			// hercMap.removeLayer(herculaneumSections);
			hercMap.removeLayer(herculaneumPropertiesNotIA);
			hercMap.addLayer(herculaneumSectionsNotIA);
			hercMap.addLayer(herculaneumStreets);
			// hercMap.addLayer(herculaneumProperties);
			$("#streetsection").hide();
		}
		herculaneumInsulae.bringToFront();
		isFacadeView=!isFacadeView;
	}
	
	function toggleStreetVsSection(){
		if(!strvsec_toggle){
			hercMap.removeLayer(herculaneumStreets);
		}
		else{
			hercMap.addLayer(herculaneumStreets);
			herculaneumStreets.bringToFront();

		}
		strvsec_toggle = !strvsec_toggle;
	}

	// handles additional events.
	var el = document.getElementById("search");
	if(el!=null){
		el.addEventListener("click", searchProperties, false);
	}
	
	//adds listener to clear selected button
	var clearButton = document.getElementById("clearbutton")
	if (clearButton!=null){
		clearButton.addEventListener("click", clearSelected, false);
	}
	
	// Listens to the radio buttons and toggles the search
	$(document).ready(function(){
	    $('input[name=search]').change(function(){
	        togglePropertyVsFacade();
	    });
	});	
	
	// Listens to the radio buttons and toggles the search
	$(document).ready(function(){
	    $('input[name=refinedSearch]').change(function(){
	        toggleStreetVsSection();
	    });
	});	
	
	if (!interactive){
		hercMap.eachLayer(function(layer){
			if(layer.feature!=undefined){
				bringHighlightedToFront(layer);
			}
		});
	}
	
}
