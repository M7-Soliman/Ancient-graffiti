package edu.wlu.graffiti.data.setup.maps;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * This class parses through PBMP's JSON data and extracts all of the objects /
 * properties that belong to a given insula. All of these objects are then saved
 * in a new file with the insula's name that can be found in the
 * extracted_insula_data folder. Insula should be extracted so that edits can be
 * made to them. The edited insulae can then be merged with the PBMP data using
 * CreateAGPJSON.java
 * 
 * @author Trevor Stalnaker
 */
public class ExtractInsulaFromPBMP {

	private static String INSULA = "II.7";

	private static String PBMP_JSON_DATA = "src/main/resources/geoJSON/pompeii_properties.geojson";
	private static String INSULA_JSON_PATH = "src/main/resources/geoJSON/extracted_insula_data/";

	private static final String START_TEXT = "{\"type\":\"FeatureCollection\",\"crs\":{\"type\":\"name\",\"properties\":{\"name\":\"EPSG:4326\"}},\"features\":[";
	private static final String END_TEXT = "]}";

	public static void main(String[] args) {
		extractInsula(INSULA);
	}

	private static void extractInsula(String insula) {
		try {
			String file = INSULA_JSON_PATH + insula.replace(".", "_") + ".json";
			PrintWriter pompeiiTextWriter = new PrintWriter(file, "UTF-8");

			pompeiiTextWriter.println(START_TEXT);

			// creates necessary objects to parse the original GeoJSON file
			ObjectMapper pompeiiMapper = new ObjectMapper();
			JsonFactory pompeiiJsonFactory = new JsonFactory();
			JsonParser pompeiiJsonParser;

			pompeiiJsonParser = pompeiiJsonFactory.createParser(new File(PBMP_JSON_DATA));
			JsonNode pompeiiRoot = pompeiiMapper.readTree(pompeiiJsonParser);

			// this accesses the 'features' level of the GeoJSON document
			JsonNode pompeiiFeaturesNode = pompeiiRoot.path("features");

			// iterates over the features node
			Iterator<JsonNode> featureIterator = pompeiiFeaturesNode.elements();

			// use this as a means to get the commas correct in the final JSON
			JsonNode nodeToPrint = null;

			while (featureIterator.hasNext()) {

				JsonNode featureNode = featureIterator.next();
				JsonNode primaryDOORNode = featureNode.findValue("PRIMARY_DO");
				if (primaryDOORNode == null) {
					primaryDOORNode = featureNode.findValue("PRIMARY_DOOR");
				}
				String primaryDoor = primaryDOORNode.textValue();
				if (primaryDoor != null && primaryDoor.contains(".")) {
					String[] parts = primaryDoor.split("\\.");
					String parsed_insula = parts[0] + "." + parts[1];
					// Remove all references to the insula in the JSON file
					if (insula.equals(parsed_insula)) {
						if (nodeToPrint != null) {
							pompeiiTextWriter.println(nodeToPrint + ",");
						}
						nodeToPrint = featureNode; // We're always going to be one node behind what we want to print
					}
				}
			}

			pompeiiTextWriter.println(nodeToPrint);
			pompeiiTextWriter.println(END_TEXT);
			pompeiiTextWriter.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
