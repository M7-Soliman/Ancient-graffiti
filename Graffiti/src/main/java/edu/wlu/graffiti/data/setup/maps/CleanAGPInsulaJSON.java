package edu.wlu.graffiti.data.setup.maps;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;


/**
 * @author Trevor Stalnaker
 * 
 * A class that cleans unwanted fields out of JSON data.  This is used when we are trying to recover JSON map data from JavaScript,
 * which contains AGP added fields.  We want those fields removed before merging with PBMP's JSON data
 * 
 */
public class CleanAGPInsulaJSON {
	
	private static final String OUTPUT_FILE_PATH = "src/main/resources/geoJSON/agp_property_files_cleaned/";
	private static final String INPUT_FILE_PATH = "src/main/resources/geoJSON/agp_property_files/";
	
	private static final String START_TEXT = "{\"type\":\"FeatureCollection\",\"crs\":{\"type\":\"name\",\"properties\":{\"name\":\"EPSG:4326\"}},\"features\":[";
	private static final String END_TEXT = "]}";
	
	private static final String[] FIELDS_TO_REMOVE = {"Property_Id","Number_Of_Graffiti","Property_Name","Additional_Properties",
			 "Italian_Property_Name","Insula_Description","Insula_Pleiades_Id","Property_Pleiades_Id","Property_Type",
			 "insula_id","full_insula_name","short_insula_name","stroke","stroke-width","stroke-opacity","fill","fill-opacity",
			 "OBJECTID_1","OBJECTID","GIS_PROPER","PinP_Addre","PinP_URL",
			 "CTP_Page_N","EntrancePr","EntranceSe","Name_1","Name_2","Name_3",
			 "Name_4","Name_5","Name_6","Name_7","Name_8","Name_9","Name_10","Name_11",
			 "Name_12","Name_13","Name_14","Ref_Name_1","Ref_Name_2","Ref_Name_3","Ref_Name_4",
			 "Ref_Name_5","Ref_Name_6","Ref_Name_7","Ref_Name_8","Ref_Name_9","Ref_Name_10",
			 "Ref_Name_11","Ref_Name_12","Ref_Name_13","Notes","Refs","LOCATION_I","AUX_ORIG_H",
			 "FCLASS_NAM","Names_","Shape__Are","Shape__Len","FCLASS_COD","NAME","FCLASS_ALI",
			 "AUX_ORIG_O","GIS_ID","SECONDARY_","FUNCTION_E","IMG_Link1","IMG_Link2","IMG_Link3","IMG_Link4",
			 "IMG_Link5","IMG_Link6","IMG_Link7","IMG_Link8","IMG_Link9",
			 "IMG_Link10","Shape_Length","Shape_Area","Ref_Nam_10","Ref_Nam_11",
			 "Ref_Nam_12","Ref_Nam_13"};
	
	public static String[] files = {"I_8","II_7","V_1","VII_12","VIII_7"};

	public static void main(String[] args) {
		cleanJSON();
	}
	
	private static void cleanJSON() {
		try {
			for (String file : files) {
				PrintWriter pompeiiTextWriter = new PrintWriter(OUTPUT_FILE_PATH + file + ".json", "UTF-8");
				
				pompeiiTextWriter.println(START_TEXT);

				// creates necessary objects to parse the original GeoJSON file
				ObjectMapper pompeiiMapper = new ObjectMapper();
				JsonFactory pompeiiJsonFactory = new JsonFactory();
				JsonParser pompeiiJsonParser;
				
				pompeiiJsonParser = pompeiiJsonFactory.createParser(new File(INPUT_FILE_PATH + file + ".json"));
				JsonNode pompeiiRoot = pompeiiMapper.readTree(pompeiiJsonParser);
				// this accesses the 'features' level of the GeoJSON document
				JsonNode pompeiiFeaturesNode = pompeiiRoot.path("features");

				// iterates over the features node
				Iterator<JsonNode> featureIterator = pompeiiFeaturesNode.elements();
				
				while (featureIterator.hasNext()) {
					
					JsonNode featureNode = featureIterator.next();
					
					ObjectNode propertyNode = (ObjectNode) featureNode;
					ObjectNode properties = (ObjectNode) propertyNode.path("properties");
					
					for (String field : FIELDS_TO_REMOVE) {
						properties.remove(field);
					}

					if (featureIterator.hasNext()) {
						pompeiiTextWriter.println(propertyNode + ",");
					}
					else {
						pompeiiTextWriter.println(propertyNode);
					}
				}
				
				pompeiiTextWriter.println(END_TEXT);
				pompeiiTextWriter.close();
			}
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
