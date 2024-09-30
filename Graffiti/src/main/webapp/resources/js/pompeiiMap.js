// make sure that, if there are globals, 
// they are the same for Pompeii and Herculaneum.  Consider refactoring.

// an orangey-yellow
var DEFAULT_COLOR = '#F4BF77';
// a maroon
var SELECTED_COLOR = '#800000';
var DEFAULT_BORDER_COLOR = "black";
var SELECTED_BORDER_COLOR = "#B00000";

var STREET_DEFAULT_COLOR = '#989898';
var STREET_BORDER_COLOR = "#A9A9A9"; // dark-grey

var ACTIVE_LAYER_Z = 4;

// This HAS to be declared here!!!  Otherwise the scope is not global!
var pompeiiMap;

function initPompeiiMap(zoomLevel = "large", showHover = true, colorDensity = false, interactive = true,
	idOfFeatureToHighlight = 0, propertyIdListToHighlight = [], zoomOnOneProperty = false,
	isFacadeView = false) {

	PROPERTY_VIEW_INSULA_LABEL_COLOR = "grey";
	FACADE_VIEW_INSULA_LABEL_COLOR = "white";
	var insula_label_color = PROPERTY_VIEW_INSULA_LABEL_COLOR;

	var PROPERTY_BORDER_COLOR = "white";
	var INSULA_BORDER_COLOR = "black";
	var STREET_BORDER_COLOR = "black"; //"DEFAULT_COLOR;

	var NOT_IA_SELECTED = '#845555'; // when feature is selected

	// but feature is not in interactive mode.
	var INSULA_BORDER_WEIGHT = 1;

	var HOVER_BORDER_WEIGHT = 2;
	var SELECTED_BORDER_WEIGHT = 1;

	var wallBorderColor = 'black';
	var wallFillColor = 'black';
	var streetBorderColor = '#888';
	var streetFillColor = '#888';
	var unexBorderColor = '#C9C9C9';
	var unexFillColor = '#CDCDCD';

	var REGIO_VIEW_ZOOM = 15;
	// The minimum zoom level to show insula view instead of property
	// view (smaller zoom level means more zoomed out)
	var LARGE_REGIO_ZOOM = 16;
	var INSULA_VIEW_ZOOM = 17;
	var INDIVIDUAL_PROPERTY_ZOOM = 18;
	var MAX_ZOOM = 21;

	// Sets the different zoom levels for the map
	var zoomLevels = {};
	zoomLevels["regio"] = REGIO_VIEW_ZOOM;
	zoomLevels["large"] = LARGE_REGIO_ZOOM;
	zoomLevels["insula"] = INSULA_VIEW_ZOOM;
	zoomLevels["property"] = INDIVIDUAL_PROPERTY_ZOOM;

	if (!interactive) {
		INSULA_BORDER_WEIGHT = 1;
		SELECTED_BORDER_COLOR = "white";
		SELECTED_BORDER_WEIGHT = 1;
	}

	// sets MapBox access token
	var mapboxAccessToken = 'pk.eyJ1IjoibWFydGluZXphMTgiLCJhIjoiY2lxczduaG5wMDJyc2doamY0NW53M3NnaCJ9.TeA0JhIaoNKHUUJr2HyLHQ';
	var borderColor;
	var southWest = L.latLng(40.746, 14.48),
		northEast = L.latLng(43.754, 15.494),
		bounds = L.latLngBounds(southWest, northEast);

	var propertyLayer, pompeiiWallsLayer, backgroundInsulaLayer, pompeiiStreetsLayer, pompeiiUnexcavatedLayer, pompeiiInsulaLayer;
	var pompeiiFacadesLayer, pompeiiInsulaNotIALayer, pompeiiSectionsLayer;

	var initialZoomLevel = zoomLevels[zoomLevel];

	var totalInsulaGraffitisDict = new Array();
	// The list of active insula markers.
	// Can be iterated through to remove all markers from the map(?)
	var insulaMarkersList = [];
	var regioMarkersList = [];
	var unexcavatedMarkersList = [];

	var clickedAreas = [];
	// A list filled with nested list of the full name, id, and short name of
	// each insula selected.
	var clickedInsula = [];

	// Holds the center latitudes and longitudes of all insula on the map.
	var insulaCentersDict = [];
	var insulaGroupIdsList = [];
	var insulaShortNamesDict = [];

	// Holds the center latitudes and longitudes of all unexcavated areas on the
	// map
	var unexcavatedCentersList = [];

	// Variables for all things regio:
	var regioCentersDict = {};
	var regioNamesList = [];
	var graffitiInEachRegioDict = {};

	// Shows or hides insula labels depending on zoom levels and if the map is
	// interactive
	function dealWithLabelsAndSelection() {
		if (interactive) {
			currentZoomLevel = pompeiiMap.getZoom();

			if (currentZoomLevel < INSULA_VIEW_ZOOM) {
				removeInsulaLabels();
				displayInsulaLabels();
			}
			else if (currentZoomLevel >= INSULA_VIEW_ZOOM) {
				// removeRegioLabels();
				removeInsulaLabels();
				displayInsulaLabels();
			}
			else {
				displayInsulaLabels();
			}
		}
	}

	// Centers the map around a single property
	function showCloseUpView() {
		if (idOfFeatureToHighlight) {
			var newCenterCoordinates = [];
			propertyLayer.eachLayer(function(layer) {
				if (layer.feature != undefined) {
					if (layer.feature.properties.Property_Id == idOfFeatureToHighlight) {
						newCenterCoordinates = layer.getBounds().getCenter();
						pompeiiMap.setView(newCenterCoordinates, INDIVIDUAL_PROPERTY_ZOOM);
						return; // because there is only one highlighted
					}
				}
			});
			pompeiiSectionsLayer.eachLayer(function(layer) {
				if (layer.feature.properties.section_id != undefined) {

					if (layer.feature.properties.section_id == idOfFeatureToHighlight) {
						newCenterCoordinates = layer.getBounds().getCenter();
						pompeiiMap.removeLayer(pompeiiFacadesLayer);
						pompeiiMap.removeLayer(pompeiiFacadesNotIALayer);
						pompeiiMap.addLayer(pompeiiSectionsLayer);
						pompeiiMap.setView(newCenterCoordinates, INDIVIDUAL_PROPERTY_ZOOM);
						layer.setStyle({
							fillColor: SELECTED_COLOR,
							color: SELECTED_COLOR
						});
						return; // because there is only one
					}
				}
			});
			pompeiiFacadesLayer.eachLayer(function(layer) {
				if (layer.feature.properties.street_id != undefined) {
					if (layer.feature.properties.street_id == idOfFeatureToHighlight) {
						newCenterCoordinates = layer.getBounds().getCenter();
						pompeiiMap.removeLayer(pompeiiFacadesNotIALayer);
						pompeiiFacadesLayer.bringToFront();
						pompeiiMap.setView(newCenterCoordinates, INSULA_VIEW_ZOOM);
						return; // because there is only one
					}
				}
			});
		} // below is for insula?  SS: I'm not sure this code is used. What is the use case?
		else if (propertyIdListToHighlight.length == 1) {
			var newCenterCoordinates = [];
			var idOfListHighlight = propertyIdListToHighlight[0];
			insulaLayer.eachLayer(function(layer) {
				if (layer.feature != undefined) {
					if (layer.feature.properties.insula_id == idOfListHighlight) {
						newCenterCoordinates = layer.getBounds().getCenter();
						pompeiiMap.setView(newCenterCoordinates, INDIVIDUAL_PROPERTY_ZOOM);
						return; // because there is only one
					}
				}
			});
		}
	}

	// Builds the global list of insula ids.
	function makeInsulaIdsListShortNamesList() {
		var currentInsulaId;
		pompeiiInsulaLayer.eachLayer(function(layer) {
			if (layer.feature != undefined) {
				currentInsulaId = layer.feature.properties.insula_id;
				if (insulaGroupIdsList.indexOf(currentInsulaId) == -1) {
					insulaGroupIdsList.push(currentInsulaId);
				}
				insulaShortNamesDict[currentInsulaId] = layer.feature.properties.insula_short_name;
			}
		});
	}

	function makeListOfRegioNames() {
		var someName;
		pompeiiInsulaLayer.eachLayer(function(layer) {
			if (layer.feature != undefined) {
				someName = layer.feature.properties.insula_short_name.split(".")[0];
				if (regioNamesList.indexOf(someName) == -1) {
					regioNamesList.push(someName);
				}
			}
		});
	}

	function makeTotalRegioGraffitiDict() {
		var currentNumberOfGraffiti;
		var currentRegioName;
		var regioNamesSoFar = [];
		pompeiiInsulaLayer.eachLayer(function(layer) {
			if (layer.feature != undefined) {
				currentRegioName = layer.feature.properties.insula_short_name.split(".")[0];
				currentNumberOfGraffiti = layer.feature.properties.number_of_graffiti;
				if (regioNamesSoFar.indexOf(currentRegioName) == -1) {
					regioNamesSoFar.push(currentRegioName);
					graffitiInEachRegioDict[currentRegioName] = currentNumberOfGraffiti;
				}
				else {
					graffitiInEachRegioDict[currentRegioName] += currentNumberOfGraffiti;
				}
			}
		});
	}

	// Builds the dictionary of the graffiti in each insula
	// This works well as graffiti numbers should not change over the session.
	// Modifies the closure wide variable once and only once at the beginning of
	// the program
	function makeTotalInsulaGraffitiDict() {
		totalInsulaGraffitisDict = new Array();
		pompeiiInsulaLayer.eachLayer(function(layer) {
			if (insulaViewZoomThresholdReached() && layer.feature != undefined) {
				graffitiInLayer = layer.feature.properties.number_of_graffiti;
				currentInsulaNumber = layer.feature.properties.insula_id;
				if (totalInsulaGraffitisDict[currentInsulaNumber] != undefined) {
					totalInsulaGraffitisDict[currentInsulaNumber] += graffitiInLayer;
				}
				else {
					totalInsulaGraffitisDict[currentInsulaNumber] = graffitiInLayer;
				}
			}
		});
	}

	// Meant to show the insula short name labels at the given x/y coordinates
	// (given as a normal list in Java array form)
	function showALabelOnMap(xYCoordinates, textToDisplay, textSize = "small", markerType) {
		currentZoomLevel = pompeiiMap.getZoom();
		if (markerType == "insula" && currentZoomLevel < INSULA_VIEW_ZOOM) {
			var splitLabel = textToDisplay.split(".");
			//var formattedText = "<u><sub>" + splitLabel[0] + "</sub></u><br><sup>" + splitLabel[1] + "</sup>";
			var formattedText = "<div class=\"fraction\"><div class=\"frac numerator\">" +
				splitLabel[0] + "</div><div class=\"frac\">" + splitLabel[1] + "</div></div>";
			var myIcon = L.divIcon({
				// iconSize: new L.Point(0, 0),
				// iconSize:0,
				className: "labelClassInsula",
				html: formattedText
			});
		}
		else if (markerType == "insula" && currentZoomLevel >= INSULA_VIEW_ZOOM) {
			var splitLabel = textToDisplay.split(".");
			//var formattedText = "<u><sub>" + splitLabel[0] + "</sub></u><br><sup>" + splitLabel[1] + "</sup>";
			var formattedText = "<div class=\"fraction\"><div class=\"frac numerator\">" +
				splitLabel[0] + "</div><div class=\"frac\">" + splitLabel[1] + "</div></div>";
			var myIcon = L.divIcon({
				className: "labelClassInsulaZoom",
				html: formattedText
			});
		}
		else {
			var myIcon = L.divIcon({
				className: "labelClass",
				html: textToDisplay
			});
		}

		var myMarker;
		myMarker = new L.marker([xYCoordinates[0], xYCoordinates[1]], { icon: myIcon }).addTo(pompeiiMap);

		if (markerType == "insula") {
			myMarker.on({
				mouseover: highlightInsulaFromMarker,
				mouseout: resetHighlightFromMarker,
				click: showInsulaDetailsFromMarker,
			});
			insulaMarkersList.push(myMarker);
		} else if (markerType == "regio") {
			regioMarkersList.push(myMarker)
		} else if (markerType == "unexcavated") {
			unexcavatedMarkersList.push(myMarker);
		}
	}

	// returns insula id of the insula that the marker represents
	function getInsulaFromMarker(insulaMarker) {
		var markerCoordinates = [insulaMarker._latlng["lat"], insulaMarker._latlng["lng"]];
		for (var i = 0; i < insulaGroupIdsList.length; i++) {
			insulaID = insulaGroupIdsList[i];
			insulaCenterCoordinates = insulaCentersDict[insulaID];
			if (insulaCenterCoordinates[0] == markerCoordinates[0] && insulaCenterCoordinates[1] == markerCoordinates[1]) {
				return insulaID;
			}
		}
		return -1;
	}

	// highlights insula if mouseover the label of insula
	function highlightInsulaFromMarker(e) {
		var insulaMarker = e.target;
		var insulaToHighlight = getInsulaFromMarker(insulaMarker);
		pompeiiInsulaLayer.eachLayer(function(layer) {
			if (layer.feature != undefined && interactive) {
				if (layer.feature.properties.insula_id == insulaToHighlight) {
					highlightInsula(layer);
				}
			}
		});
	}

	// resets highlight of insula if mouseout of the label of insula
	function resetHighlightFromMarker(e) {
		var insulaMarker = e.target;
		var insulaToHighlight = getInsulaFromMarker(insulaMarker);
		pompeiiInsulaLayer.eachLayer(function(layer) {
			if (layer.feature != undefined && interactive) {
				if (layer.feature.properties.insula_id == insulaToHighlight) {
					resetHighlight(layer);
				}
			}
		});
	}

	// selects and shows details of insula if click on the label of insula
	function showInsulaDetailsFromMarker(e) {
		var insulaMarker = e.target;
		var insulaToHighlight = getInsulaFromMarker(insulaMarker);
		pompeiiInsulaLayer.eachLayer(function(layer) {
			if (layer.feature != undefined && interactive) {
				if (layer.feature.properties.insula_id == insulaToHighlight) {
					showInsulaDetails(layer);
				}
			}
		});
	}

	// Removes each of the insula labels from the map.
	// Meant to be used for when the user zooms past the zoom threshold.
	// Stopped being used due to recommendations at the demo.
	function removeInsulaLabels() {
		var i = 0;
		for (i; i < insulaMarkersList.length; i++) {
			pompeiiMap.removeLayer(insulaMarkersList[i]);
		}
	}

	function colorInsulaLabels() {
		var x = document.getElementsByClassName("labelClassInsula");
		var i;
		for (i = 0; i < x.length; i++) {
			x[i].style.color = insula_label_color;
		}
		var x = document.getElementsByClassName("labelClassInsulaZoom");
		var i;
		for (i = 0; i < x.length; i++) {
			x[i].style.color = insula_label_color;
		}
	}

	function removeRegioLabels() {
		var i = 0;
		for (i; i < regioMarkersList.length; i++) {
			pompeiiMap.removeLayer(regioMarkersList[i]);
		}
	}

	// Shows the short names of each insula in black
	// at the center coordinates.
	function displayInsulaLabels() {
		var i;
		var insulaId;
		var insulaCenterCoordinates;
		var shortInsulaName;
		for (i = 0; i < insulaGroupIdsList.length; i++) {
			insulaId = insulaGroupIdsList[i];
			insulaCenterCoordinates = insulaCentersDict[insulaId];
			shortInsulaName = insulaShortNamesDict[insulaId];
			if (insulaCenterCoordinates != null) {
				showALabelOnMap(insulaCenterCoordinates, shortInsulaName, "small", "insula");
			}
		}
		colorInsulaLabels();
	}



	// labels 3 unexcavated areas with "unexcavated" at adjusted
	// center coordinates
	function displayUnexcavatedLabels() {
		var i;
		var currentCenter;
		var displayTheseUnexAreas = [];
		displayTheseUnexAreas.push(unexcavatedCentersList[0]);
		displayTheseUnexAreas.push(unexcavatedCentersList[1]);
		displayTheseUnexAreas.push(unexcavatedCentersList[5]);
		displayTheseUnexAreas = adjustUnexcavatedCenters(displayTheseUnexAreas);
		for (i = 0; i < displayTheseUnexAreas.length; i++) {
			currentCenter = displayTheseUnexAreas[i];
			if (currentCenter != null) {
				showALabelOnMap(currentCenter, "unexcavated", "small", "unexcavated");
			}
		}

	}

	// adjusts the unexcavated areas with
	function adjustUnexcavatedCenters(listOfCenters) {
		// adjust bottom right unex area
		listOfCenters[0][0] -= 0.0003;
		listOfCenters[0][1] -= 0.0007;
		// adjust top right unex area
		listOfCenters[1][0] += 0.0003;
		listOfCenters[1][1] -= 0.0005;
		// adjust top left unex area
		listOfCenters[2][0] += 0.0005;
		listOfCenters[2][1] -= 0.0003;

		return listOfCenters;
	}

	function displayRegioLabels() {
		var i;
		var regioCenterCoordinates;
		var regioName;
		for (i = 0; i < regioNamesList.length; i++) {
			regioName = regioNamesList[i];
			regioCenterCoordinates = regioCentersDict[regioName];
			if (regioCenterCoordinates != null) {
				showALabelOnMap(regioCenterCoordinates, regioName, "large", "regio");
			}
		}
	}

	function addMoreLatLng(oldList, newList) {
		oldList = [oldList[0] + newList[0], oldList[1] + newList[1]];
		return oldList;
	}

	// This way will take more time.
	// Trying to do it inside make center dict was too confusing/complex for me.
	function makeTotalPropsPerRegioDict(totalPropsSoFarDict) {
		var regioList = [];
		var currentRegio;
		var currentCount;
		pompeiiInsulaLayer.eachLayer(function(layer) {
			if (layer.feature != undefined) {
				currentRegio = layer.feature.properties.insula_short_name.split(".")[0];

				if (regioList.indexOf(currentRegio) == -1) {
					currentCount = 1;
					totalPropsSoFarDict[currentRegio] = currentCount;
					regioList.push(currentRegio);
				}
				else {
					currentCount = totalPropsSoFarDict[currentRegio];
					totalPropsSoFarDict[currentRegio] = currentCount + 1;
				}
			}
		});
		return totalPropsSoFarDict;
	}

	// Works like the maker for insula centers dict but for Regio instead.
	// Needed to account for the fact that Regio were not ordered one to the
	// other in database.
	function makeRegioCentersDict() {
		var currentRegioName;
		var totalPropsSoFarDict = {};
		var regioNamesSoFar = [];
		var currentRegioName;
		totalPropsSoFarDict = makeTotalPropsPerRegioDict(totalPropsSoFarDict);
		pompeiiInsulaLayer.eachLayer(function(layer) {
			if (layer.feature != undefined) {
				currentRegioName = layer.feature.properties.insula_short_name.split(".")[0];
				if (regioNamesSoFar.indexOf(currentRegioName) == -1) {
					regioNamesSoFar.push(currentRegioName);
					if (layer.feature.geometry.coordinates != undefined) {
						regioCentersDict[currentRegioName] = findCenter(layer.feature.geometry.coordinates[0]);
					}
					else {
						regioCentersDict[currentRegioName] = 0;
					}
				}
				else {
					if (layer.feature.geometry.coordinates != undefined) {
						regioCentersDict[currentRegioName] = addMoreLatLng(regioCentersDict[currentRegioName], [findCenter(layer.feature.geometry.coordinates[0])[0], findCenter(layer.feature.geometry.coordinates[0])[1]]);
					}
				}
			}
		});
		for (var key in regioCentersDict) {
			var div = [regioCentersDict[key][0] / totalPropsSoFarDict[key], regioCentersDict[key][1] / totalPropsSoFarDict[key]];
			regioCentersDict[key] = div;
		}
	}

	// This function gets and returns a "dictionary" of the latitude and
	// longitude of each insula given its id(as index).
	// Used to find where to place the labels of each insula on the map, upon
	// iteration through this list.
	function makeInsulaCentersDict() {
		var currentInsulaNumber;
		var latLngList;
		pompeiiInsulaLayer.eachLayer(function(layer) {
			if (layer.feature != undefined && interactive) {

				// Get the insula id from the feature
				currentInsulaNumber = layer.feature.properties.insula_id;

				// Get the longitude and latitude from the feature's geometry
				currentLong = layer.getBounds().getCenter().lng;
				currentLat = layer.getBounds().getCenter().lat;

				// Shift the middle coordinates in an attempt to make most of the insulae
				// labels fit on the map
				//currentLat  += 0.00015;
				//currentLong += 0.00009;
				latLngList = [currentLat, currentLong]

				// Fine-tune the points on an insula by insula basis
				latLngList = adjustInsulaCenters(layer.feature.properties.insula_short_name, latLngList);

				insulaCentersDict[currentInsulaNumber] = latLngList;
			}
		});
	}

	function adjustInsulaCenters(insulaID, center) {

		// Get the regio component of the insula name
		var regio = insulaID.split(".")[0];
		var insula = insulaID.split(".")[1];

		if (regio == "VI") {
			if (['1'].includes(insula)) {
				center[1] -= 0.00005;
			}
			if (['2', '7', '10', '12', '13'].includes(insula)) {
				center[1] -= 0.000075;
			}
			if (['3'].includes(insula)) {
				center[0] += 0.000075;
				center[1] -= 0.00015;
			}
			if (['4'].includes(insula)) {
				center[0] += 0.00001
				center[1] -= 0.000125;
			}
			if (['5', '6', '9', '11'].includes(insula)) {
				center[1] -= 0.000125;
			}
			if (['8', '15'].includes(insula)) {
				center[1] -= 0.0001;
			}
			if (['14'].includes(insula)) {
				center[1] -= 0.0001;
			}
			if (['16'].includes(insula)) {
				center[0] -= 0.00006
				center[1] -= 0.000075;
			}
			if (['17'].includes(insula)) {
				center[1] -= 0.00005;
			}
		}

		if (regio == "VII") {
			if (['1', '2', '5', '6', '8'].includes(insula)) {
				center[1] -= 0.000125;
			}
			if (['3', '12'].includes(insula)) {
				center[0] += 0.0001;
				center[1] -= 0.000125;
			}
			if (['7'].includes(insula)) {
				center[1] -= 0.000025;
			}
			if (['9'].includes(insula)) {
				center[0] += 0.0001;
			}
			if (['10', '11', '14'].includes(insula)) {
				center[0] += 0.000075;
				center[1] -= 0.0001;
			}
			if (['13'].includes(insula)) {
				center[0] += 0.000075;
				center[1] -= 0.00015;
			}
			if (['15'].includes(insula)) {
				center[0] += 0.00005;
				center[1] -= 0.0002;
			}
			if (['16'].includes(insula)) {
				center[0] -= 0.0003;
				center[1] -= 0.00005;
			}
		}
		// Clean up the labels for Regio VIII
		if (regio == "VIII") {
			if (['1', '7'].includes(insula)) {
				center[1] -= 0.0003;
			}
			if (['3', '4'].includes(insula)) {
				center[0] += 0.00005;
				center[1] -= 0.00015;
			}
			if (['2'].includes(insula)) {
				center[0] += 0.00006;
				center[1] -= 0.0009;
			}
			if (['5'].includes(insula)) {
				center[0] += 0.00005;
				center[1] += 0.00005;
			}
			if (['6'].includes(insula)) {
				center[0] += 0.00002;
				center[1] -= 0.00020;
			}
		}

		if (regio == "VI") {
			if (['2', '7', '8', '10', '12', '13', '15', '16'].includes(insula)) {
				center[1] -= 0.00005;
			}
			if (['17'].includes(insula)) {
				center[0] += 0.00018;
				center[1] -= 0.0001;
			}
			if (['1'].includes(insula)) {
				center[0] += 0.00015;
				center[1] -= 0.00016;
			}
		}
		//		if (insulaID == "I.8") {
		//			center[0] += 0.0001;
		//			center[1] += 0.0001;
		//		} else if (insulaID == "VII.12") {
		//			center[0] += 0.00021;
		//			center[1] += 0;
		//		} else if (insulaID == "V.1") {
		//			center[0] += 0.0002;
		//			center[1] += 0.00015;
		//		} else if (insulaID == "II.7") {
		//			center[0] += 0.00005;
		//			center[1] += 0.00005;
		//		}
		return center;
	}

	function makeUnexcavatedCentersList() {
		var xSoFar = 0;
		var ySoFar = 0;
		var latLngList;
		var currentCoordinatesList;
		var propertiesSoFar = 0;

		pompeiiUnexcavatedLayer.eachLayer(function(layer) {
			propertiesSoFar += 1;
			if (layer.feature != undefined && interactive) {
				currentCoordinatesList = layer.feature.geometry.coordinates;
				xAndYAddition = findCenter(currentCoordinatesList[0]);
				ySoFar += xAndYAddition[0];
				xSoFar += xAndYAddition[1];
				// Add to dictionary:
				// Both divisions are required
				latLngList = [xSoFar / propertiesSoFar, ySoFar / propertiesSoFar];
				// This treats the currentInsulaNumber as a key to the
				// dictionary

				unexcavatedCentersList.push(latLngList);

				// Reset old variables:
				xSoFar = 0;
				ySoFar = 0;
				propertiesSoFar = 0;
			}
		});
	}

	// Uses math to directly find and return the latitude and longitude of the
	// center of a list of coordinates.
	// Returns a list of the latitude, x and the longitude, y
	function findCenter(coordinatesList) {
		var i = 0;
		var x = 0;
		var y = 0
		var pointsSoFar = 0;
		for (i; i < coordinatesList.length; i++) {
			x += coordinatesList[i][0];
			y += coordinatesList[i][1];
			pointsSoFar += 1;
		}
		return [x / pointsSoFar, y / pointsSoFar];
	}

	// Responsible for showing the map view on the insula level.
	function dealWithInsulaLevelPropertyView() {
		if (insulaViewZoomThresholdReached() && interactive && !isFacadeView) {
			pompeiiInsulaLayer.addTo(pompeiiMap);
		}

		// deals with zoomed out pompeii results map (insula level)
		if (resultsViewThresholdReached() && !colorDensity && !interactive) {
			pompeiiInsulaLayer.eachLayer(function(layer) {
				if (layer.feature != undefined) {
					currentInsulaNumber = layer.feature.properties.insula_short_name;

					if (isInsulaSelected(layer.feature)) {
						setSelectedStyleOnLayer(layer);
					}
					else {
						numberOfGraffitiInGroup = layer.feature.properties.number_of_graffiti;
						newFillColor = getFillColor(numberOfGraffitiInGroup);
						layer.setStyle({
							fillColor: newFillColor,
							color: INSULA_BORDER_COLOR
						});
					}
					layer.setStyle({ fillOpacity: .7 });
					layer.bringToFront();
				}
			});
			pompeiiFacadesLayer.eachLayer(function(layer) {
				if (layer.feature.properties.street_id != undefined) {
					if (isStreetSelected(layer.feature)) {
						setSelectedStyleOnLayer(layer);
					}
					layer.setStyle({ fillOpacity: .95 });
					layer.bringToFront();
				}
			});
		}

		// deals with zoomed in pompeii results map (property level)
		if (!resultsViewThresholdReached() && !colorDensity && !interactive) {
			pompeiiInsulaLayer.eachLayer(function(layer) {
				if (layer.feature != undefined) {
					layer.setStyle({
						fillColor: DEFAULT_COLOR,
						color: DEFAULT_COLOR
					});
				}
			});
			propertyLayer.eachLayer(function(layer) {
				layer.bringToFront();
			});
		}

		// zoomed out (no colorDensity on the main search map, change that to
		// interactive as condition)
		// Resets properties when user zooms back in
		if (!insulaViewZoomThresholdReached() && interactive) {
			if (pompeiiInsulaLayer._map) {
				pompeiiInsulaLayer.remove(pompeiiMap);
			}

			propertyLayer.eachLayer(function(layer) {
				if (layer.feature != undefined) {
					if (!isPropertySelected(layer.feature)) {
						propertyLayer.resetStyle(layer);
					}
				}
			});
		}
	}

	function regioViewZoomThresholdReached() {
		currentZoomLevel = pompeiiMap.getZoom();
		return currentZoomLevel <= REGIO_VIEW_ZOOM;
	}

	function insulaViewZoomThresholdReached() {
		currentZoomLevel = pompeiiMap.getZoom();
		return currentZoomLevel <= INSULA_VIEW_ZOOM;
	}

	function resultsViewThresholdReached() {
		currentZoomLevel = pompeiiMap.getZoom();
		return (currentZoomLevel <= LARGE_REGIO_ZOOM);
	}

	function isFeatureSelected(feature) {
		return isPropertySelected(feature) || isInsulaSelected(feature) || isSectionSelected(feature) || isStreetSelected(feature);
	}

	function isPropertySelected(feature) {
		return feature.properties.clicked == true || feature.properties.Property_Id == idOfFeatureToHighlight || propertyIdListToHighlight.indexOf(feature.properties.Property_Id) >= 0;
	}

	function isInsulaSelected(feature) {
		return feature.properties.clicked == true || feature.properties.numPropertiesClicked > 0 || feature.properties.insula_id == idOfFeatureToHighlight || propertyIdListToHighlight.indexOf(feature.properties.insula_id) >= 0;
	}

	function isSectionSelected(feature) {
		return feature.properties.clicked == true || feature.properties.section_id == idOfFeatureToHighlight || propertyIdListToHighlight.indexOf(feature.properties.section_id) >= 0;
	}

	function isStreetSelected(feature) {
		return feature.properties.clicked == true || feature.properties.street_id == idOfFeatureToHighlight || propertyIdListToHighlight.indexOf(feature.properties.street_id) >= 0;
	}

	function getBorderColorForProperty(feature) {
		if (isPropertySelected(feature)) {
			return SELECTED_BORDER_COLOR;
		}
		return PROPERTY_BORDER_COLOR;
	}

	// Sets the style of the portions of the map. color is the outside borders.
	// There are different colors for clicked or normal fragments. When clicked,
	// items are stored in a collection. These collections will have the color
	// contained inside of else.
	function propertyStyle(feature) {
		borderColor = getBorderColorForProperty(feature);
		weight = 1;
		if (isPropertySelected(feature)) {
			fillColor = SELECTED_COLOR;
			weight = SELECTED_BORDER_WEIGHT;
		}
		else if (colorDensity) {
			fillColor = getFillColor(feature.properties.Number_Of_Graffiti);
		}
		else {
			fillColor = getFillColorForProperty(feature);
		}
		return {
			fillColor: fillColor,
			weight: weight,
			color: borderColor,
			fillOpacity: 1,
			zIndex: 1
		};

	}

	function insulaStyle(feature) {
		var fillOp = 0.7;

		if (colorDensity && regioViewZoomThresholdReached()) {
			regioName = feature.properties.insula_short_name.split(".")[0];
			numberOfGraffitiInGroup = graffitiInEachRegioDict[regioName];
			fillColor = getFillColor(numberOfGraffitiInGroup);
		}
		else if (colorDensity && insulaViewZoomThresholdReached()) {
			fillColor = getFillColor(feature.properties.number_of_graffiti);
		}
		else if (interactive && idOfFeatureToHighlight == 0) {
			// this highlights insula on click on big map
			fillColor = getFillColorForInsula(feature);
		}
		else if (!interactive) {
			// this fills insula color on small map on results screen
			fillColor = DEFAULT_COLOR;
			fillOp = 1;
		}
		else {
			fillColor = "none";
			// the propertyStyle is being set correctly,
			// but the insula style is being overwritten on top of what is
			// already the
			// correct property color.
			// therefore, if you change fill color for the insula level as
			// "none," it
			// fixes things.
		}

		if (isInsulaSelected(feature)) {
			borderColor = SELECTED_BORDER_COLOR;
		}
		else {
			borderColor = INSULA_BORDER_COLOR;
		}

		return {
			fillColor: fillColor,
			fillOpacity: fillOp,
			weight: INSULA_BORDER_WEIGHT,
			opacity: 1,
			color: borderColor
		};
	}

	function wallStyle(feature) {
		return {
			fillColor: wallFillColor,
			weight: 1,
			opacity: 1,
			color: wallBorderColor,
			fillOpacity: 1
		};
	}

	function forumStyle(feature) {
		return {
			fillColor: "none",
			weight: 1,
			opacity: 1,
			color: streetBorderColor,
			fillOpacity: 0.7,
		};
	}

	function streetStyle(feature) {
		return {
			fillColor: streetFillColor,
			weight: 1,
			opacity: 1,
			color: streetBorderColor,
			fillOpacity: 1,
		};
	}

	function backgroundInsulaStyle(feature) {
		return {
			fillColor: DEFAULT_COLOR,
			fillOpacity: 1,
			color: DEFAULT_COLOR,
			opacity: 1,
			weight: 1
		}
	}

	// for facades
	function facadeStyle(feature) {
		borderColor = DEFAULT_BORDER_COLOR;

		if (!interactive) {
			weight = 0;
		}
		else {
			weight = 1;
		}

		if (isFeatureSelected(feature)) {
			fillColor = SELECTED_COLOR;
		}
		else if (!interactive) {
			fillColor = STREET_DEFAULT_COLOR;
			//zIndex = 2;
		}
		else {
			fillColor = DEFAULT_COLOR;
			//zIndex = 2;
		}

		return {
			fillColor: fillColor,
			fillOpacity: .7,
			color: borderColor,
			weight: weight,
			//zIndex: zIndex,
		};
	}

	// styling to make the insula blocks appear inactive
	function insulaNotInteractiveStyle(feature) {
		return {
			fillColor: streetFillColor,
			fillOpacity: 0.7,
			color: streetBorderColor,
			weight: 1,
			opacity: 1,
		};
	}

	function facadeNotInteractiveStyle(feature) {
		return {
			fillColor: streetFillColor,
			fillOpacity: 0.5,
			color: streetBorderColor,
			weight: 1,
			opacity: 1,
		};
	}

	function unexcavatedStyle(feature) {
		return {
			fillColor: unexFillColor,
			fillOpacity: 1,
			color: unexBorderColor,
			weight: 1,
			opacity: 1,
		};
	}

	function updateNotIAInsulae() {
		pompeiiInsulaLayer.eachLayer(function(layer) {
			if (layer.feature != undefined) {
				if (isInsulaSelected(layer.feature)) {
					pompeiiInsulaNotIALayer.eachLayer(function(layerNotIA) {
						if (layerNotIA.feature != undefined) {
							// mark the insula selected in the non-interactive
							// mode
							if (layer.feature.properties.insula_id == layerNotIA.feature.properties.insula_id) {
								layerNotIA.setStyle({
									fillColor: NOT_IA_SELECTED,
									color: NOT_IA_SELECTED,
									fillOpacity: .9
								});
							}
						}
					});
				}
			}
		});
	}

	function getFillColorForInsula(feature) {
		if (isInsulaSelected(feature)) {
			return SELECTED_COLOR;
		}
		return DEFAULT_COLOR;
	}

	function getFillColorForProperty(feature) {
		if (isPropertySelected(feature)) {
			return SELECTED_COLOR;
		}
		return DEFAULT_COLOR;
	}

	function getFillColor(numberOfGraffiti) {
		if (colorDensity) {
			return numberOfGraffiti <= 2 ? '#FFEDC0' :
				numberOfGraffiti <= 5 ? '#FFEDA0' :
					numberOfGraffiti <= 10 ? '#fed39a' :
						numberOfGraffiti <= 20 ? '#fec880' :
							numberOfGraffiti <= 30 ? '#FEB24C' :
								numberOfGraffiti <= 40 ? '#fe9b1b' :
									numberOfGraffiti <= 60 ? '#fda668' :
										numberOfGraffiti <= 80 ? '#FD8D3C' :
											numberOfGraffiti <= 100 ? '#fd7a1c' :
												numberOfGraffiti <= 130 ? '#fc6c4f' :
													numberOfGraffiti <= 160 ? '#FC4E2A' :
														numberOfGraffiti <= 190 ? '#fb2d04' :
															numberOfGraffiti <= 210 ? '#ea484b' :
																numberOfGraffiti <= 240 ? '#E31A1C' :
																	numberOfGraffiti <= 270 ? '#b71518' :
																		numberOfGraffiti <= 300 ? '#cc0029' :
																			numberOfGraffiti <= 330 ? '#b30024' :
																				numberOfGraffiti <= 360 ? '#99001f' :
																					numberOfGraffiti <= 390 ? '#80001a' :
																						numberOfGraffiti <= 420 ? '#660014' :
																							numberOfGraffiti <= 460 ? '#4d000f' :
																								numberOfGraffiti <= 500 ? '#33000a' :
																									'#000000';
		}
		// an orangey-yellow
		return DEFAULT_COLOR;
	}

	// Sets color for properties which the cursor is moving over.
	function highlightFeature(e) {
		var layer = e.target;
		// Second condition prevents II.7.1 from overshadowing columns
		if (interactive && layer.feature.properties.PRIMARY_DOOR != "II.7.1") {
			layer.setStyle({
				fillOpacity: .7,
				weight: HOVER_BORDER_WEIGHT,
				fillColor: SELECTED_COLOR
			});

			if (!L.Browser.ie && !L.Browser.opera) {
				//Bring the highlighted property to the front
				layer.bringToFront();
				//Bring the alwayFrontLayer back to the front
				alwaysFrontLayer.eachLayer(function(frontLayer) {
					frontLayer.bringToFront();
				});
			}
			info.update(layer.feature.properties);
		}
	}

	// Sets color for insula which the cursor is moving over.
	function highlightInsula(e) {
		if (interactive) {
			var layer;
			// if true mouseover on actual insula; if false mouseover on insula
			// label
			if (e.feature == undefined) {
				layer = e.target;
			} else {
				layer = e;
			}
			layer.setStyle({
				fillColor: SELECTED_COLOR,
				color: SELECTED_BORDER_COLOR,
				weight: HOVER_BORDER_WEIGHT,
				fillOpacity: .7

			});

			if (!L.Browser.ie && !L.Browser.opera) {
				layer.bringToFront();
			}
			info.update(layer.feature.properties);
		}
	}

	// Sorts items based on whether they have been clicked
	// or not. If they have been and are clicked again, sets to false and vice
	// versa.
	function showPropertyDetails(e) {
		if (interactive && !isFacadeView) {
			var layer = e.target;
			// Prevent II.7.1 from overshadowing the columns
			if (layer.feature.properties.PRIMARY_DOOR != "II.7.1") {

				if (!insulaViewZoomThresholdReached()) {

					// If they have been clicked and are clicked again, sets to
					// false and vice versa.
					layer.feature.properties.clicked = !layer.feature.properties.clicked;

					insulaID = layer.feature.properties.insula_id;
					insulaLayer = insulaIdToInsulaLayer.get(parseInt(insulaID));

					if (layer.feature.properties.clicked) {
						setSelectedStyleOnLayer(layer);

						clickedAreas.push(layer);
						// Increment insula's number properties selected
						insulaLayer.feature.properties.numPropertiesClicked += 1;
						// individual property is selected so insula isn't.
						insulaLayer.feature.properties.clicked = false;
						setSelectedStyleOnLayer(insulaLayer);
					}
					else {
						resetPropertyHighlight(e);
						var index = clickedAreas.indexOf(layer);
						if (index > -1) {
							clickedAreas.splice(index, 1);
						}
						// TODO: decrement properties selected
						// check if 0 and reset insula
						insulaLayer.feature.properties.numPropertiesClicked -= 1;

						if (insulaLayer.feature.properties.numPropertiesClicked == 0) {
							pompeiiInsulaLayer.resetStyle(insulaLayer);
						}
					}
					if (!L.Browser.ie && !L.Browser.opera) {
						//Bring the selected layer to the front
						layer.bringToFront();
						//Bring the alwayFrontLayer back to the front
						alwaysFrontLayer.eachLayer(function(frontLayer) {
							frontLayer.bringToFront();
						});
					}
					info.update(layer.feature.properties);
					document.getElementById("clearbutton").disabled = false;
				}
				else if (!regioViewZoomThresholdReached()) {
					checkForInsulaClick(e.target);
				}
				updateSelectedListDisplay();
			}
		}
	}

	function showColumnDetails(e) {
		if (interactive && !isFacadeView) {
			var layer = e.target;

			layer.feature.properties.clicked = !layer.feature.properties.clicked;

			if (!L.Browser.ie && !L.Browser.opera) {
				layer.bringToFront();
			}

			clickedAreas.push(layer);
			info.update(layer.feature.properties);
			document.getElementById("clearbutton").disabled = false;
			updateSelectedListDisplay();
		}
	}

	function showFacadeDetails(e) {
		if (interactive && isFacadeView) {

			var layer = e.target;

			// Reset highlight on click
			layer.feature.properties.clicked = !layer.feature.properties.clicked;

			insulaID = layer.feature.properties.insula_id;
			insulaLayer = insulaIdToInsulaLayer.get(parseInt(insulaID));

			if (layer.feature.properties.clicked) {
				setSelectedStyleOnLayer(layer);
				clickedAreas.push(layer);
			}
			else {
				resetPropertyHighlight(e);
				var index = clickedAreas.indexOf(layer);
				if (index > -1) {
					clickedAreas.splice(index, 1);
				}
			}
			if (!L.Browser.ie && !L.Browser.opera) {
				layer.bringToFront();
			}
			info.update(layer.feature.properties);
			document.getElementById("clearbutton").disabled = false;
			updateSelectedListDisplay();
		}
	}

	// If item had been click and is clicked again, sets to false and vice
	// versa. This version is for insula click compatibility only.
	function showInsulaDetails(e) {
		if (interactive && !isFacadeView) {
			var layer;
			// if true clicked on actual insula; if false clicked on insula
			// label
			if (e.feature == undefined) {
				layer = e.target;
			} else {
				layer = e;
			}
			// If clicked
			if (layer.feature.properties.clicked) {
				layer.feature.properties.clicked = !layer.feature.properties.clicked;

				resetHighlight(e);
				var removedInsula = [];
				var length = clickedAreas.length;
				for (var i = 0; i < length; i++) {
					var singleInsula = clickedAreas[i]
					if (singleInsula.feature.properties.insula_id != layer.feature.properties.insula_id) {
						removedInsula.push(clickedAreas[i]);
					}
				}
				clickedAreas = removedInsula;
			}
			else if (layer.feature.properties.numPropertiesClicked > 0) {
				// some properties were clicked, so zoom in to the insula
				var coordinates = insulaCentersDict[layer.feature.properties.insula_id];
				pompeiiMap.setView(coordinates, INDIVIDUAL_PROPERTY_ZOOM);

				pompeiiColumnLayer.bringToFront();
				propertyLayer.bringToFront();
			} else {
				// wasn't selected, now it is!
				layer.feature.properties.clicked = !layer.feature.properties.clicked;
				setSelectedStyleOnLayer(layer);
				clickedAreas.push(layer);
				document.getElementById("propertyPopup").style.display = 'block';
			}
			updateSelectedListDisplay();

			if (!L.Browser.ie && !L.Browser.opera) {
				layer.bringToFront();
			}
			info.update(layer.feature.properties);
			document.getElementById("clearbutton").disabled = false;
		}
	}

	// Returns a new array with the contents of the previous index absent
	// We must search for a string in the array because, again, indexOf does not
	// work for nested lists.
	function removeStringedListFromArray(someArray, targetItem) {
		var newArray = [];
		var i;
		for (i = 0; i < someArray.length; i++) {
			if (someArray[i] != targetItem) {
				newArray.push(someArray[i]);
			}
		}
		return newArray;
	}

	// On click, sees if a new insula id # has been selected. If so, adds it to
	// the list of selected insula.
	function checkForInsulaClick(clickedProperty) {
		// Clicked property is a layer
		// layer.feature.properties.insula_id

		var clickedInsulaFullName = clickedProperty.feature.properties.insula_full_name;
		var clickedInsulaId = clickedProperty.feature.properties.insula_id;
		var clickedInsulaShortName = clickedProperty.feature.properties.insula_short_name;
		var targetInsulaItem = [clickedInsulaFullName, clickedInsulaId, clickedInsulaShortName];

		// Only adds the new id if it is already in the list
		if (!clickedAreas.includes(targetInsulaItem)) {
			clickedAreas.push(targetInsulaItem);
		}
		// Otherwise, removed the insula id from the list to deselect it
		else {
			clickedAreas = removeStringedListFromArray(clickedAreas, targetInsulaItem);
		}
	}

	// Used to reset the color, size, etc of items to their default state (i.e.,
	// after being clicked twice) for insula
	function resetHighlight(e) {
		var layer;
		// if true, mouseout on actual insula; if false, mouseout on insula
		// label
		if (e.feature == undefined) {
			layer = e.target;
		} else {
			layer = e;
		}
		if (interactive) {
			pompeiiInsulaLayer.resetStyle(layer);
			info.update();
		}
	}

	function resetPropertyHighlight(e) {
		if (interactive) {
			propertyLayer.resetStyle(e.target);
			info.update();
		}
	}

	// Calls the functions on their corresponding events for EVERY feature
	function onEachPropertyFeature(feature, layer) {
		layer.on({
			mouseover: highlightFeature,
			mouseout: resetPropertyHighlight,
			click: showPropertyDetails,
		});
	}

	function onEachInsulaFeature(feature, layer) {
		layer.on({
			mouseover: highlightInsula,
			mouseout: resetHighlight,
			click: showInsulaDetails,
		});
	}

	function onEachColumnFeature(feature, layer) {
		layer.on({
			mouseover: highlightFeature,
			mouseout: resetPropertyHighlight,
			click: showColumnDetails,
		});
	}

	// for facades
	function onEachFacadeFeature(feature, layer) {
		layer.on({
			mouseover: highlightFeature,
			mouseout: resetHighlight,
			click: showFacadeDetails,
		});
	}

	// for facades
	function onEachNotInteractiveFeature(feature, layer) {
		layer.on({
		});
	}

	function onEachUnexcavatedFeature(feature, layer) {
		layer.on({
		});
	}

	function setSelectedStyleOnLayer(layer) {
		if (layer.feature.properties.street_name != undefined) {
			weight = 0;
		}
		else {
			weight = SELECTED_BORDER_WEIGHT;
		}
		layer.setStyle({
			fillColor: SELECTED_COLOR,
			color: SELECTED_BORDER_COLOR,
			weight: weight
		});
	}

	// Marks all properties inside of selected insula as selected by
	// adding them to the clickedInsula list.
	function selectPropertiesInAllSelectedInsula(uniqueClicked) {
		if (interactive) {
			var i = 0;
			var currentInsulaId;
			var currentInsula;
			var listOfSelectedInsulaIds = [];
			for (i; i < clickedInsula.length; i++) {
				currentInsula = clickedInsula[i];
				currentInsulaId = currentInsula[1];
				listOfSelectedInsulaIds.push(currentInsulaId);
			}
			pompeiiInsulaLayer.eachLayer(function(layer) {
				if (layer.feature != undefined) {
					if (listOfSelectedInsulaIds.indexOf(layer.feature.properties.insula_id) != -1 && !uniqueClicked.includes(layer)) {
						uniqueClicked.push(layer);
						layer.feature.properties.clicked = true;
					}
				}
			});
		}
		return uniqueClicked;
	}

	// Acquire all of the items clicked for search when "Search" button clicked.
	// Iterate through the list of clicked items and add them to uniqueClicked,
	// then return uniqueClicked.
	function getUniqueClicked() {
		var uniqueClicked = [];
		var length = clickedAreas.length;
		for (var i = 0; i < length; i++) {
			var insula = clickedAreas[i];
			if (!uniqueClicked.includes(insula) && insula.feature.properties.clicked) {
				uniqueClicked.push(insula)
			}
		}
		return uniqueClicked;
	}

	// Collects the ids of the clicked item objects (the property id).
	function collectClicked() {
		var insulaIdsOfClicked = [];
		var propertyIdsOfClicked = [];
		var sectionIdsOfClicked = [];
		var streetIdsOfClicked = [];
		var colIdsOfClicked = [];
		var selectedFeatures = getUniqueClicked();
		var length = selectedFeatures.length;
		for (var i = 0; i < length; i++) {
			var property = selectedFeatures[i];
			if (property.feature.properties.Property_Id != undefined) {
				var propertyID = property.feature.properties.Property_Id;
				propertyIdsOfClicked.push(propertyID);
			}
			else if (property.feature.properties.section_id != undefined) {
				var sectionID = property.feature.properties.section_id;
				sectionIdsOfClicked.push(sectionID);
			}
			else if (property.feature.properties.street_id != undefined) {
				var streetID = property.feature.properties.street_id;
				streetIdsOfClicked.push(streetID);
			}
			else if (property.feature.properties.column_id != undefined) {
				var columnID = property.feature.properties.column_id;
				colIdsOfClicked.push(columnID);
			}
			else {
				var insulaID = property.feature.properties.insula_id;
				insulaIdsOfClicked.push(insulaID);
			}

		}
		return [insulaIdsOfClicked, propertyIdsOfClicked, streetIdsOfClicked, sectionIdsOfClicked, colIdsOfClicked];
	}

	// creates url to call for searching when the user clicks the search button.
	function searchForProperties() {
		var highlighted = collectClicked();
		var argString = "";
		if (highlighted[0].length > 0 || highlighted[1].length > 0 || highlighted[2].length > 0 ||
			highlighted[3].length > 0 || highlighted[4].length > 0) {
			for (var i = 0; i < highlighted[0].length; i++) {
				argString = argString + "insula=" + highlighted[0][i] + "&";
			}
			for (var j = 0; j < highlighted[1].length; j++) {
				argString = argString + "property=" + highlighted[1][j] + "&";
			}
			for (var k = 0; k < highlighted[2].length; k++) {
				argString = argString + "street=" + highlighted[2][k] + "&";
			}
			for (var m = 0; m < highlighted[3].length; m++) {
				argString += "segment=" + highlighted[3][m] + "&";
			}

			for (var n = 0; n < highlighted[4].length; n++) {
				argString = argString + "column=" + highlighted[4][n] + "&";
			}
			window.location = "results?" + argString;
			return true;
		}
	}
	
	//function called when user clicks clear selected button.
	function clearSelected() {
		window.location.reload();
	}

	function updateSelectedListDisplay() {
		// when you click on the map, it updates the selection info
		var clickedAreasTable = getUniqueClicked();
		var html = "<strong>Selected Areas:</strong><ul>";
		var length = clickedAreasTable.length;
		for (var i = 0; i < length; i++) {
			var property = clickedAreasTable[i];
			if (property.feature.properties.clicked) {
				if (property.feature.properties.PRIMARY_DOOR != undefined) {
					html += "<li>" + property.feature.properties.PRIMARY_DOOR;
					if (property.feature.properties.Property_Name != undefined && property.feature.properties.Property_Name != "") {
						html += ", " + property.feature.properties.Property_Name;
					}
					html += ",<p>" + property.feature.properties.Number_Of_Graffiti + " graffiti</p>" + "</li>";
				}
				else if (property.feature.properties.insula_full_name != undefined) {
					html += "<li>" + property.feature.properties.insula_full_name + ",<p>" + property.feature.properties.number_of_graffiti + " graffiti</p>" + "</li>";
				}
				else if (property.feature.properties.section_name != undefined) {
					html += "<li>" + property.feature.properties.street_name + " (" + property.feature.properties.section_name;
					html +=	"),<p>" + property.feature.properties.number_of_graffiti + " graffiti</p>" + "</li>";
				}
				else if (property.feature.properties.street_name != undefined) {
					html += "<li>" + property.feature.properties.street_name + ",<p>" + property.feature.properties.number_of_graffiti + " graffiti</p>" + "</li>";
				}
				else if (property.feature.properties.column_id != undefined) {
					html += "<li>Column " + property.feature.properties.numeral + ",<p>" + property.feature.properties.number_of_graffiti + " graffiti</p>" + "</li>";
				}
			}
		}
		html += "</ul>";
		// Checks to avoid error for element is null.
		var elem = document.getElementById("selectionDiv");
		if (typeof elem !== 'undefined' && elem !== null) {
			document.getElementById("selectionDiv").innerHTML = html;
		}
	}

	// A function that updates the color of insula labels
	function recolorInsulaLabels(color) {
		insula_label_color = color;
		removeInsulaLabels();
		displayInsulaLabels();
	}

	// Function that isFacadeViews between the Insula/Property View and the
	// facade view of the Pompeii map
	// isFacadeView is true when on facade view and false when on insula view
	function toggleInsulaVsFacade() {
		if (!isFacadeView) {

			// Redraws the map, displaying Streets and Segments
			pompeiiMap.removeLayer(pompeiiInsulaLayer);
			pompeiiMap.removeLayer(pompeiiFacadesNotIALayer);
			pompeiiMap.addLayer(pompeiiInsulaNotIALayer);
			updateNotIAInsulae();

			pompeiiFacadesLayer.bringToFront();
			pompeiiUnexcavatedLayer.bringToFront();
			$("#streetsection").show();
			document.getElementById("default_street").checked = true;
			recolorInsulaLabels(FACADE_VIEW_INSULA_LABEL_COLOR);
			isSegmentView = false;
		}
		else {

			// Reset to streets view if in section view
			// This makes the map look nicer and behave more predictably
			if (isSegmentView) {
				toggleStreetVsSection();
			}

			// Redraws the map, displaying Insulae and Properties
			// pompeiiMap.removeLayer(pompeiiFacadesLayer);
			pompeiiMap.removeLayer(pompeiiInsulaNotIALayer);
			pompeiiMap.addLayer(pompeiiFacadesNotIALayer);
			pompeiiMap.addLayer(pompeiiInsulaLayer);
			recolorInsulaLabels(PROPERTY_VIEW_INSULA_LABEL_COLOR);
			$("#streetsection").hide();

		}
		isFacadeView = !isFacadeView;
	}

	function toggleStreetVsSection() {
		if (!isSegmentView) {
			pompeiiMap.addLayer(pompeiiSectionsLayer);
			pompeiiMap.removeLayer(pompeiiFacadesLayer);
			pompeiiSectionsLayer.bringToFront();
		}
		else {
			pompeiiMap.removeLayer(pompeiiSectionsLayer);
			pompeiiMap.addLayer(pompeiiFacadesLayer);
			pompeiiFacadesLayer.bringToFront();

		}
		isSegmentView = !isSegmentView;
	}

	function goToPropertySelect() {
		if (clickedAreas.length > 0) {
			var lastClickedPos = clickedAreas.length - 1;
			var latestInsula = clickedAreas[lastClickedPos];
			var latestInsulaID = latestInsula.feature.properties.insula_id;
			var coordinates = insulaCentersDict[latestInsulaID];
			pompeiiMap.setView(coordinates, INDIVIDUAL_PROPERTY_ZOOM);

			clickedAreas.splice(lastClickedPos, 1);
			updateSelectedListDisplay();
			/*
			if (latestInsula.feature.properties.numPropertiesClicked == 0) {
				latestInsula.feature.properties.clicked = false;
				pompeiiInsulaLayer.resetStyle(latestInsula);
			}
			*/
			propertyLayer.bringToFront();
			pompeiiColumnLayer.bringToFront();
			alwaysFrontLayer.eachLayer(function(frontLayer) {
				frontLayer.bringToFront();
			});
		}
	}

	pompeiiMap = new L.map('pompeiimap', {
		center: [40.750950, 14.488600],
		zoom: initialZoomLevel,
		minZoom: REGIO_VIEW_ZOOM,
		maxZoom: MAX_ZOOM,
		maxBounds: bounds
	});

	backgroundInsulaLayer = L.geoJson(pompeiiInsulaData, { style: backgroundInsulaStyle });
	if (interactive) {
		backgroundInsulaLayer.addTo(pompeiiMap);
	}

	propertyLayer = L.geoJson(pompeiiPropertyData, { style: propertyStyle, onEachFeature: onEachPropertyFeature });
	propertyLayer.addTo(pompeiiMap);

	pompeiiColumnLayer = L.geoJson(columnData, { style: propertyStyle, onEachFeature: onEachColumnFeature });
	pompeiiColumnLayer.addTo(pompeiiMap);

	pompeiiStreetsLayer = L.geoJson(pompeiiStreetsData, { style: streetStyle });
	pompeiiStreetsLayer.addTo(pompeiiMap);


	pompeiiFacadesLayer = L.geoJson(pompeiiFacadeData, { style: facadeStyle, onEachFeature: onEachFacadeFeature });
	pompeiiFacadesLayer.addTo(pompeiiMap);

	pompeiiFacadesNotIALayer = L.geoJson(pompeiiFacadeData, { style: facadeNotInteractiveStyle, onEachFeature: onEachNotInteractiveFeature });
	pompeiiFacadesNotIALayer.addTo(pompeiiMap);

	pompeiiSectionsLayer = L.geoJson(pompeiiSectionData, { style: facadeStyle, onEachFeature: onEachFacadeFeature });
	//Don't add sections to the map initially -- only after entering street section view

	pompeiiInsulaNotIALayer = L.geoJson(pompeiiInsulaData, { style: insulaNotInteractiveStyle, onEachFeature: onEachNotInteractiveFeature });
	pompeiiInsulaNotIALayer.addTo(pompeiiMap);

	pompeiiInsulaLayer = L.geoJson(pompeiiInsulaData, { style: insulaStyle, onEachFeature: onEachInsulaFeature });
	pompeiiInsulaLayer.addTo(pompeiiMap);

	pompeiiForumOutline = L.geoJson(forumOutlineData, { style: forumStyle });
	pompeiiForumOutline.addTo(pompeiiMap);

	pompeiiUnexcavatedLayer = L.geoJson(pompeiiUnexcavatedData, { style: unexcavatedStyle, onEachFeature: onEachUnexcavatedFeature });
	pompeiiWallsLayer = L.geoJson(pompeiiWallsData, { style: wallStyle });
	pompeiiOutlineLayer = L.geoJson(pompeiiOutlineData, { style: wallStyle });

	var alwaysFrontLayer = L.layerGroup([pompeiiUnexcavatedLayer, pompeiiWallsLayer, pompeiiOutlineLayer])
	alwaysFrontLayer.addTo(pompeiiMap);
	alwaysFrontLayer.setZIndex(5);

	//	var insulaBaseLayer = L.layerGroup([pompeiiStreetsLayer, propertyLayer, pompeiiInsulaLayer])
	//	
	//	var baseMaps = {"Insula":insulaBaseLayer,"Streets":pompeiiFacadesLayer};
	//	var overlayMaps = {"Overlay":alwaysFrontLayer};



	//	L.control.layers(baseMaps, {}).addTo(pompeiiMap);

	/*
	 * This was the original ranges list. Simply add or remove the numbers to
	 * manipulate the ranges on the legend. -Hammad grades = [0, 5, 10, 20, 30,
	 * 40, 60, 80, 100, 130, 160, 190, 210, 240, 270, 300, 330, 360, 390, 420,
	 * 460, 500],
	 */
	/*
	 * var insulaLevelLegend = L.control({position: 'bottomright'});
	 * 
	 * insulaLevelLegend.onAdd = function (map) {
	 * 
	 * var div = L.DomUtil.create('div', 'info legend'), grades = [0, 40, 80,
	 * 120, 160], labels = [], from, to;
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

	var insulaIdToInsulaLayer = new Map();

	if (interactive) {
		makeInsulaCentersDict();
		makeUnexcavatedCentersList();
		makeInsulaIdsListShortNamesList();
		makeListOfRegioNames();
		makeRegioCentersDict();

		if (colorDensity) {
			makeTotalInsulaGraffitiDict();
			makeTotalRegioGraffitiDict();
			insulaLevelLegend.addTo(pompeiiMap);
		}

		pompeiiMap.addControl(new L.Control.Compass({ autoActive: true, position: "bottomleft" }));
		dealWithLabelsAndSelection();
		displayUnexcavatedLabels();
		// This sets the map to insula view at start
		// pompeiiMap.removeLayer(pompeiiFacadesLayer);
		pompeiiMap.removeLayer(pompeiiInsulaNotIALayer);
		pompeiiMap.addLayer(pompeiiInsulaLayer);

		pompeiiInsulaLayer.eachLayer(function(layer) {
			if (layer.feature != undefined) {
				layer.feature.properties.clicked = false;
				// keep track of how many properties clicked
				layer.feature.properties.numPropertiesClicked = 0;
				insulaIdToInsulaLayer.set(layer.feature.properties.insula_id, layer);
			}
		});

		propertyLayer.eachLayer(function(layer) {
			if (layer.feature != undefined) {
				layer.feature.properties.clicked = false;
			}
		});
	}
	else {
		propertyLayer.bringToFront();
	}

	// A listener for zoom events.
	pompeiiMap.on('zoomend', function(e) {
		dealWithInsulaLevelPropertyView();
		dealWithLabelsAndSelection();
	});

	var info = L.control();
	info.onAdd = function(map) {
		// create a div with a class "info"
		this._div = L.DomUtil.create('div', 'info');
		this.update();
		return this._div;
	};

	info.update = function(props) {
		currentZoomLevel = pompeiiMap.getZoom();
		if (showHover) {
			if (isFacadeView) {
				if (isSegmentView) {
					this._div.innerHTML = (props ? props.street_name + " (" + props.section_name + ")"
						: 'Hover over a street section to see name');
					if (this._div.innerHTML == "undefined (undefined)") {
						this._div.innerHTML = 'Hover over a street section to see name';
					}
				}
				else {
					this._div.innerHTML = (props ? "Street: " + props.street_name
						: 'Hover over a street to see name');
					if (this._div.innerHTML == "Street: undefined") {
						this._div.innerHTML = 'Hover over a street to see name';
					}
				}
			}
			else {
				if (!props) {
					if (currentZoomLevel < INDIVIDUAL_PROPERTY_ZOOM) {
						this._div.innerHTML = 'Hover over an insula to see name';
					} else {
						this._div.innerHTML = 'Hover over a property to see name';
					}
				}
				else {
					if (props.insula_full_name && currentZoomLevel < INDIVIDUAL_PROPERTY_ZOOM) {
						this._div.innerHTML = props.insula_full_name;
					}
					else if (props.PRIMARY_DOOR && currentZoomLevel >= INDIVIDUAL_PROPERTY_ZOOM) {
						this._div.innerHTML = 'Property ' + props.PRIMARY_DOOR;
						/* add the name if it exists */
						if (props.Property_Name != undefined && props.Property_Name != "") {
							this._div.innerHTML += ", " + props.Property_Name;
						}
					}
					else if (props.column_id && currentZoomLevel >= INDIVIDUAL_PROPERTY_ZOOM) {
						this._div.innerHTML = 'Column ' + props.numeral;
					}
					// else: rolling over insula marker
				}
			}
		}
	};

	info.addTo(pompeiiMap);

	dealWithInsulaLevelPropertyView();

	if (idOfFeatureToHighlight != 0 || propertyIdListToHighlight.length == 1) {
		showCloseUpView();
	}

	// Listens for the pressing of the search button
	var el = document.getElementById("search");
	if (el != null) {
		el.addEventListener("click", searchForProperties, false);
	}
	
	// Listens for pressing of clear selected button
	var clearButton = document.getElementById("clearbutton")
	if (clearButton!=null){
		clearButton.addEventListener("click", clearSelected, false);
	}

	// Listens to determine if the user wants to select by properties (rather
	// than by insula).
	// The selectPropsBtn displays along with the selectInsulaBtn only after a
	// user clicks on an insula
	var el4 = document.getElementById("selectPropsBtn");
	if (el4 != null) {
		el4.addEventListener("click", goToPropertySelect, false);
	}

	// Listens to the radio buttons and toggles the search
	$(document).ready(function() {
		$('input[name=search]').change(function() {
			toggleInsulaVsFacade();
		});
	});

	// Listens to the radio buttons and toggles the search
	$(document).ready(function() {
		$('input[name=refinedSearch]').change(function() {
			toggleStreetVsSection();
		});
	});

}
