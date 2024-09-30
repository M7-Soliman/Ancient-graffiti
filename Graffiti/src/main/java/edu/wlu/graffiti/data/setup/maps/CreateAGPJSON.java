package edu.wlu.graffiti.data.setup.maps;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Iterator;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

//TODO: Should this file and other related GeoJSON files be deleted from the repo?

/**
 * This class reads in our stored property JSON files and merges them with
 * PBMP's JSON data. If we have made changes to an insula, our changes are given
 * precedence. If the insula / properties are not in our changed files, then we
 * use PBMP's by default.
 * 
 * @author Trevor Stalnaker
 * 
 */
public class CreateAGPJSON {

	private static String PBMP_JSON_DATA_INPUT = "src/main/resources/geoJSON/pompeii_properties.geojson";
	private static String AGP_JSON_DATA_OUTPUT = "src/main/resources/geoJSON/agp_pompeii_properties.json";

	private static String INPUT_FILE_PATH = "src/main/resources/geoJSON/agp_property_files_cleaned/";

	private static String[] STORED_INSULA = { "I.8", "II.7", "V.1", "VII.12", "VIII.7" };
	private static String[] FILES = new String[STORED_INSULA.length];

	private static final String START_TEXT = "{\"type\":\"FeatureCollection\",\"crs\":{\"type\":\"name\",\"properties\":{\"name\":\"EPSG:4326\"}},\"features\":[";
	private static final String END_TEXT = "]}";

	public static void main(String[] args) {
		for (int i = 0; i < STORED_INSULA.length; i++) {
			FILES[i] = STORED_INSULA[i].replace(".", "_");
		}
		mergeAGPInsulaeWithPBMP();
	}

	private static void mergeAGPInsulaeWithPBMP() {
		try {
			PrintWriter pompeiiTextWriter = new PrintWriter(AGP_JSON_DATA_OUTPUT, "UTF-8");

			pompeiiTextWriter.println(START_TEXT);

			// creates necessary objects to parse the original GeoJSON file
			ObjectMapper pompeiiMapper = new ObjectMapper();
			JsonFactory pompeiiJsonFactory = new JsonFactory();
			JsonParser pompeiiJsonParser;

			pompeiiJsonParser = pompeiiJsonFactory.createParser(new File(PBMP_JSON_DATA_INPUT));
			JsonNode pompeiiRoot = pompeiiMapper.readTree(pompeiiJsonParser);

			// this accesses the 'features' level of the GeoJSON document
			JsonNode pompeiiFeaturesNode = pompeiiRoot.path("features");

			// iterates over the features node
			Iterator<JsonNode> featureIterator = pompeiiFeaturesNode.elements();

			while (featureIterator.hasNext()) {

				JsonNode featureNode = featureIterator.next();
				JsonNode primaryDOORNode = featureNode.findValue("PRIMARY_DO");
				if (primaryDOORNode == null) {
					primaryDOORNode = featureNode.findValue("PRIMARY_DOOR");
				}
				String primaryDoor = primaryDOORNode.textValue();
				if (primaryDoor != null && primaryDoor.contains(".")) {
					String[] parts = primaryDoor.split("\\.");
					String insula = parts[0] + "." + parts[1];
					// Remove all references to the insula in the JSON file
					if (!Arrays.asList(STORED_INSULA).contains(insula)) {
						pompeiiTextWriter.println(featureNode + ",");
					}
				} else {
					pompeiiTextWriter.println(featureNode + ",");

				}
			}

			for (int x = 0; x < FILES.length; x++) {

				// creates necessary objects to parse the original GeoJSON file
				ObjectMapper mapper = new ObjectMapper();
				JsonFactory jsonFactory = new JsonFactory();
				JsonParser jsonParser;

				jsonParser = jsonFactory.createParser(new File(INPUT_FILE_PATH + FILES[x] + ".json"));
				JsonNode root = mapper.readTree(jsonParser);
				// this accesses the 'features' level of the GeoJSON document
				JsonNode featuresNode = root.path("features");

				// iterates over the features node
				Iterator<JsonNode> addFeaturesIterator = featuresNode.elements();

				while (addFeaturesIterator.hasNext()) {

					JsonNode featureNode = addFeaturesIterator.next();
					if (FILES.length - 1 != x || addFeaturesIterator.hasNext()) {
						pompeiiTextWriter.println(featureNode + ",");
					} else {
						pompeiiTextWriter.println(featureNode);
					}

				}
			}

			pompeiiTextWriter.println(END_TEXT);
			pompeiiTextWriter.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
