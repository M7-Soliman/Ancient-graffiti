package edu.wlu.graffiti.test;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ForumFinder {

	private static final String OUTPUT_FILE = "src/main/resources/geoJSON/";
	private static final String POMPEII_PROPERTY_JSON_FILE = "src/main/resources/geoJSON/pompeii_properties.geojson";
	
	private static final String START_TEXT = "{\"type\":\"FeatureCollection\",\"crs\":{\"type\":\"name\",\"properties\":{\"name\":\"EPSG:4326\"}},\"features\":[";
	private static final String END_TEXT = "]}";
	
	// Lower Quadrant
	private static final int START_INDEX_LOWER = 1113;
	private static final int END_INDEX_LOWER = 1115;
	
	// Upper Quadrant
	private static final int START_INDEX_UPPER = END_INDEX_LOWER + 1;
	private static final int END_INDEX_UPPER = 1118;
	
	
	public static void main(String[] args) {
		findForum(START_INDEX_LOWER, END_INDEX_LOWER);
		findForum(START_INDEX_UPPER, END_INDEX_UPPER);
	}
	
	private static void findForum(int lowerBound, int upperBound) {
		
		try {
			PrintWriter pompeiiTextWriter = new PrintWriter(OUTPUT_FILE + lowerBound + "-" + upperBound + ".json", "UTF-8");
			
			pompeiiTextWriter.println(START_TEXT);

			// creates necessary objects to parse the original GeoJSON file
			ObjectMapper pompeiiMapper = new ObjectMapper();
			JsonFactory pompeiiJsonFactory = new JsonFactory();
			JsonParser pompeiiJsonParser;
			
			pompeiiJsonParser = pompeiiJsonFactory.createParser(new File(POMPEII_PROPERTY_JSON_FILE));
			JsonNode pompeiiRoot = pompeiiMapper.readTree(pompeiiJsonParser);
			// this accesses the 'features' level of the GeoJSON document
			JsonNode pompeiiFeaturesNode = pompeiiRoot.path("features");

			// iterates over the features node
			Iterator<JsonNode> featureIterator = pompeiiFeaturesNode.elements();
			
			int count = lowerBound;
			
			while (featureIterator.hasNext() && count < upperBound) {
				
				JsonNode featureNode = featureIterator.next();
				int id = featureNode.findValue("id").asInt() - 1;
				if (id==count) {
					if (count != upperBound -1) {
						pompeiiTextWriter.println(featureNode + ",");
					}
					else {
						pompeiiTextWriter.println(featureNode);
					}
					count++;
				}
			}
			
			pompeiiTextWriter.println(END_TEXT);
			pompeiiTextWriter.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
