package edu.wlu.graffiti.test;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.wlu.graffiti.data.setup.Utils;

public class PropertiesChecker {
	
	private static String MAPPING_JSON_DATA = "src/main/resources/geoJSON/agp_pompeii_properties.json";
	private static String MAPPING_CSV_DATA = "data/properties/pompeii_properties_all.csv";
	
	private static String regio  = "IX";
	private static String insula = "IX.1";
	
	private static ArrayList<String> ignoreList = new ArrayList<String>(Arrays.asList("VIII.2.7"));
	
	private static ArrayList<String> easyInsulae = new ArrayList<String>();
	private static ArrayList<String> hardInsulae = new ArrayList<String>();
	private static ArrayList<String> goodInsulae = new ArrayList<String>();
	
	public static void main(String[] args) {
		
		// Get the properties from the json and csv files
		ArrayList<String> jsonProperties = getPropertiesFromJSON();
		ArrayList<String> csvProperties = getPropertiesFromCSV();
		
		// Print out basic information on property lists
		System.out.println("Total properties in JSON: " + jsonProperties.size());
		System.out.println("Total properties in CSV:  " + csvProperties.size());
		
		// Find all of the properties in the json that are not in the csv
		ArrayList<String> jsonNOTcsv = new ArrayList<String>();
		for (String property : jsonProperties) {
			if (!csvProperties.contains(property) && !ignoreList.contains(property)) {
				jsonNOTcsv.add(property);
			}
		}
		
		// Find all of the properties in the csv that are not in the json
		ArrayList<String> csvNOTjson = new ArrayList<String>();
		for (String property : csvProperties) {
			if (!jsonProperties.contains(property) && !ignoreList.contains(property) && !property.equals("..")) {
				csvNOTjson.add(property);
			}
		}
		
		// Print out the number of discrepancies between lists
		System.out.println("Properties in JSON and not CSV: " + jsonNOTcsv.size());
		System.out.println("Properties in CSV and not JSON: " + csvNOTjson.size());
		
		System.out.println(jsonNOTcsv);
		System.out.println(csvNOTjson);
		
		//getDiscrepancies(insula, csvNOTjson, jsonNOTcsv, true);
		
		getDiscrepanciesByRegio(regio, csvNOTjson, jsonNOTcsv, csvProperties, jsonProperties, true);
		
		System.out.println();
		System.out.println("Good Insulae: " + goodInsulae);
		System.out.println("Easy Insulae: " + easyInsulae);
		System.out.println("Hard Insulae: " + hardInsulae);
		
//		try {
//			PrintWriter writer = new PrintWriter("data/json_not_csv.csv");
//			writer.print(serializeToCSV(jsonNOTcsv));
//			writer.close();
//			writer = new PrintWriter("data/csv_not_json.csv");
//			writer.print(serializeToCSV(csvNOTjson));
//			writer.close();
//		} catch (IOException e){
//			
//		}
		
	}
	
	private static void getDiscrepanciesByRegio(String regio, ArrayList<String> csvNOTjson, ArrayList<String> jsonNOTcsv,
			ArrayList<String> csvProperties, ArrayList<String> jsonProperties, boolean print) {
		// Find all of the insulae within a given regio
		ArrayList<String> insulae = getInsulaeFromRegio(regio, csvProperties, jsonProperties);
		for (String insula : insulae) {
			int count = getDiscrepancies(regio + "." + insula, csvNOTjson, jsonNOTcsv, print);
			if (count >= 6) {
				hardInsulae.add(regio + "." + insula);
			}
			else if (count == 0){
				goodInsulae.add(regio + "." + insula);
			}
			else {
				easyInsulae.add(regio + "." + insula);
			}
		}
	}
	
	private static int getDiscrepancies(String insula, ArrayList<String> csvNOTjson, ArrayList<String> jsonNOTcsv, boolean print) {
		ArrayList<String> csvNOTjsonFROMinsula = new ArrayList<String>();
		for (String property : csvNOTjson) {
			if (property.startsWith(insula + ".")) {
				csvNOTjsonFROMinsula.add(property);
			}
		}
		
		ArrayList<String> jsonNOTcsvFROMinsula = new ArrayList<String>();
		for (String property : jsonNOTcsv) {
			if (property.startsWith(insula + ".")) {
				jsonNOTcsvFROMinsula.add(property);
			}
		}

		if (print) {
			System.out.println("\nDiscrepancies for " + insula + ":");
			System.out.println("In CSV not in JSON: " + csvNOTjsonFROMinsula);
			System.out.println("In JSON not in CSV: " + jsonNOTcsvFROMinsula);
		}
		
		return csvNOTjsonFROMinsula.size() + jsonNOTcsvFROMinsula.size();
	}
	
	private static ArrayList<String> getInsulaeFromRegio(String regio, ArrayList<String> csv, ArrayList<String> json){
		ArrayList<String> props = new ArrayList<String>();
		for (String prop : csv) {
			String insula = prop.replace(regio + ".", "");
			insula = insula.substring(0, insula.indexOf("."));
			if (prop.startsWith(regio + ".") && !props.contains(insula)) {
				props.add(insula);
			}
		}
		return props;
	}
	
	private static ArrayList<String> getPropertiesFromJSON() {
		try {

			// creates necessary objects to parse the original GeoJSON file
			ObjectMapper pompeiiMapper = new ObjectMapper();
			JsonFactory pompeiiJsonFactory = new JsonFactory();
			JsonParser pompeiiJsonParser;
			
			pompeiiJsonParser = pompeiiJsonFactory.createParser(new File(MAPPING_JSON_DATA));
			JsonNode pompeiiRoot = pompeiiMapper.readTree(pompeiiJsonParser);
			
			// this accesses the 'features' level of the GeoJSON document
			JsonNode pompeiiFeaturesNode = pompeiiRoot.path("features");

			// iterates over the features node
			Iterator<JsonNode> featureIterator = pompeiiFeaturesNode.elements();
			
			ArrayList<String> primaryDoors = new ArrayList<String>();
	
			while (featureIterator.hasNext()) {
				
				JsonNode featureNode = featureIterator.next();
				JsonNode primaryDOORNode = featureNode.findValue("PRIMARY_DO");
				if (primaryDOORNode == null) {
					primaryDOORNode = featureNode.findValue("PRIMARY_DOOR");
				}
				String primaryDoor = primaryDOORNode.textValue();
				primaryDoors.add(primaryDoor);
			}
			
			return primaryDoors;

		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	private static ArrayList<String> getPropertiesFromCSV(){
		try {
			Reader in = new FileReader(MAPPING_CSV_DATA);
			Iterable<CSVRecord> records = CSVFormat.EXCEL.parse(in);
			ArrayList<String> primaryDoors = new ArrayList<String>();
			int count = 0;
			for (CSVRecord record : records) {
				
				//Clean data in CSV File and save to Strings
				String insula = Utils.cleanData(record.get(1));
				String primaryDoor = Utils.cleanData(record.get(2));
				String primary_do = insula + "." + primaryDoor;
				if (count > 0 && !primary_do.equals("")){
					primaryDoors.add(primary_do);
				}
				count++;


			}
			return primaryDoors;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static String serializeToCSV(List<String> properties) {
		
		StringBuilder stringBuilder = new StringBuilder();
		CSVPrinter csvFilePrinter = null;
		
		CSVFormat csvFileFormat = CSVFormat.DEFAULT.withRecordSeparator("\n");
		
		try {
			csvFilePrinter = new CSVPrinter(stringBuilder, csvFileFormat);
			for(String prop : properties) {
				csvFilePrinter.printRecord(prop);
			}
			csvFilePrinter.close();
			
			return stringBuilder.toString();
			
		} catch (IOException e) {
			e.printStackTrace();
		} 
		return "";
	}
	

}
