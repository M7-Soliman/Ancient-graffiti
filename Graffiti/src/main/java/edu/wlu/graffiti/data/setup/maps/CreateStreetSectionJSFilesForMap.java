package edu.wlu.graffiti.data.setup.maps;

import java.util.Iterator;
import java.util.Properties;

import javax.annotation.Resource;

import org.geojson.Polygon;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import edu.wlu.graffiti.dao.GraffitiDao;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 
 * Creates the JavaScript for Street Sections
 *
 * @author Trevor Stalnaker
 */
public class CreateStreetSectionJSFilesForMap {

	private static final String HERC_JS_FILE_HEADER = "src/main/resources/map_starter_text/hercSectionDataFirst.txt";
	private static final String HERC_SECTION_JSON_FILE = "src/main/resources/geoJSON/herculaneum_sections.json";
	private static final String HERC_SECTION_DATA_TXT_FILE = "src/main/resources/map_starter_text/hercSectionData.txt";
	private static final String HERC_SECTION_JAVASCRIPT_FILE = "src/main/webapp/resources/js/herculaneumSectionData.js";

	private static final String POMP_JS_FILE_HEADER = "src/main/resources/map_starter_text/pompSectionDataFirst.txt";
	private static final String POMP_SECTION_JSON_FILE = "src/main/resources/geoJSON/pompeii_sections.json";
	private static final String POMP_SECTION_DATA_TXT_FILE = "src/main/resources/map_starter_text/pompSectionData.txt";
	private static final String POMP_SECTION_JAVASCRIPT_FILE = "src/main/webapp/resources/js/pompeiiSectionData.js";

	final static String GET_COUNT_ON_SECTION = "SELECT COUNT(*) as count FROM inscriptions "
			+ "LEFT JOIN segments ON inscriptions.segment_id=segments.id "
			+ "WHERE inscriptions.on_facade=true AND segments.id=?";

	final static String GET_SECTION_ID = "SELECT id FROM segments WHERE UPPER(segment_name) = ?";

	final static String[] FIELDS_TO_REMOVE = { "amenity", "operator", "historic", "name", "tourism", "building",
			"man_made", "addr_house", "name_en", "heritage_o", "wikipedia", "osm_id", "ref_whc", "name_fr",
			"whc_inscri", "name_it", "heritage", "whc_criter", "name_es", "wikidata", "tourist_bu", "parking",
			"addr_city", "addr_stree", "Segment", "Street", "FID" };

	static Connection dbCon;

	private static PreparedStatement getCount;
	private static PreparedStatement getId;
	
	private static Boolean ADD_ALL_SECTIONS = false;

	@Resource
	private static GraffitiDao graffitiDao;

	public static void main(String args[]) {
		storeStreetSections();
	}

	public static void storeStreetSections() {
		init();
		storeHerc();
		storePompeii();
		copyToJavascriptFiles();
		destroy();
	}

	private static void storeHerc() {
		try {
			PrintWriter hercTextWriter = new PrintWriter(HERC_SECTION_DATA_TXT_FILE, "UTF-8");

			// creates necessary objects to parse the original GeoJSON file
			ObjectMapper mapper = new ObjectMapper();
			JsonFactory jsonFactory = new JsonFactory();
			JsonParser jsonParser = jsonFactory.createParser(new File(HERC_SECTION_JSON_FILE));

			// this accesses the 'features' level of the GeoJSON document
			JsonNode root = mapper.readTree(jsonParser);
			JsonNode featuresNode = root.path("features");

			// iterates over the features node
			Iterator<JsonNode> featureIterator = featuresNode.elements();

			while (featureIterator.hasNext()) {
				JsonNode featureNode = featureIterator.next();

				// Get the section's name
				JsonNode segNode = featureNode.findValue("Segment");
				if (segNode == null) {
					continue;
				}
				
				String section = segNode.textValue();
				if (section == null){
					continue;
				}
				
				//section = section.replaceAll(" ", "");
				System.out.println("Section Name: " + section);

				// Get the street's name
				JsonNode strNode = featureNode.findValue("Street");
				if (strNode == null) {
					continue;
				}
				String street = strNode.textValue();
				if (street.equals("Decumanus Inferiore")) {
					street = "Decumanus Inferior";
				}
				System.out.println("Street Name: " + street);

				// Parse the geometry and get rid of the z coordinates
				Polygon p = Utils.parseGeometryAndRemoveCoordinates(featureNode);

				try {

					int section_id = -1;
					int numberOfGraffiti = 0;

					getId.setString(1, section.toUpperCase());
					ResultSet idResultSet = getId.executeQuery();
					if (idResultSet.next()) {
						section_id = idResultSet.getInt("id");
						System.out.println("Section ID: " + section_id);
					}
					else {
						section = section.replaceAll(" ", "");
						getId.setString(1,  section.toUpperCase());
						ResultSet idResultSet2 = getId.executeQuery();
						if (idResultSet2.next()) {
							section_id = idResultSet2.getInt("id");
							System.out.println("Section ID, try 2: " + section_id);
						}
					}

					getCount.setInt(1, section_id);
					ResultSet rs = getCount.executeQuery();
					if (rs.next()) {
						if (section_id == -1) {
							continue;
						}

						numberOfGraffiti = rs.getInt("count");

						System.out.println("Count: " + numberOfGraffiti);
						System.out.println();

						ObjectNode facadeNode = (ObjectNode) featureNode;
						ObjectNode facades = (ObjectNode) facadeNode.path("properties");

						facades.put("section_name", section);
						facades.put("section_id", section_id);
						facades.put("street_name", street);
						facades.put("number_of_graffiti", numberOfGraffiti);

						for (String field : FIELDS_TO_REMOVE) {
							facades.remove(field);
						}

						String jsonPoly = new ObjectMapper().writeValueAsString(p);
						JsonNode updatedGeometry = new ObjectMapper().readTree(jsonPoly);

						facadeNode.replace("geometry", updatedGeometry);

						hercTextWriter.println(facadeNode + ",");
					}

				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

			hercTextWriter.close();

		} catch (JsonParseException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	private static void storePompeii() {
		try {
			PrintWriter pompTextWriter = new PrintWriter(POMP_SECTION_DATA_TXT_FILE, "UTF-8");

			// creates necessary objects to parse the original GeoJSON file
			ObjectMapper mapper = new ObjectMapper();
			JsonFactory jsonFactory = new JsonFactory();
			JsonParser jsonParser = jsonFactory.createParser(new File(POMP_SECTION_JSON_FILE));

			// this accesses the 'features' level of the GeoJSON document
			JsonNode root = mapper.readTree(jsonParser);
			JsonNode featuresNode = root.path("features");

			// iterates over the features node
			Iterator<JsonNode> featureIterator = featuresNode.elements();

			while (featureIterator.hasNext()) {
				JsonNode featureNode = featureIterator.next();

				// Get the section's name
				JsonNode segNode = featureNode.findValue("Segment");
				if (segNode == null) {
					continue;
				}
				
				String section = segNode.textValue();
				if (section == null){
					continue;
				}
				
				section = section.replaceAll(" ", "");
				System.out.println("Section Name: " + section);

				// Get the street's name
				JsonNode strNode = featureNode.findValue("Street");
				if (strNode == null) {
					continue;
				}
				String street = strNode.textValue();
				
				System.out.println("Street Name: " + street);

				// Parse the geometry and get rid of the z coordinates
				Polygon p = Utils.parseGeometryAndRemoveCoordinates(featureNode);

				try {

					int section_id = -1;
					int numberOfGraffiti = 0;

					getId.setString(1, section.toUpperCase());
					ResultSet idResultSet = getId.executeQuery();
					if (idResultSet.next()) {
						section_id = idResultSet.getInt("id");
						System.out.println("Section ID: " + section_id);
					}

					getCount.setInt(1, section_id);
					ResultSet rs = getCount.executeQuery();
					if (rs.next()) {
						if (section_id == -1) {
							continue;
						}

						numberOfGraffiti = rs.getInt("count");

						System.out.println("Count: " + numberOfGraffiti);
						System.out.println();
						
						// Only add the section if it has graffiti on it
						if (numberOfGraffiti > 0 || ADD_ALL_SECTIONS) {
							ObjectNode facadeNode = (ObjectNode) featureNode;
							ObjectNode facades = (ObjectNode) facadeNode.path("properties");

							facades.put("section_name", section);
							facades.put("section_id", section_id);
							facades.put("street_name", street);
							facades.put("number_of_graffiti", numberOfGraffiti);

							for (String field : FIELDS_TO_REMOVE) {
								facades.remove(field);
							}

							String jsonPoly = new ObjectMapper().writeValueAsString(p);
							JsonNode updatedGeometry = new ObjectMapper().readTree(jsonPoly);

							facadeNode.replace("geometry", updatedGeometry);

							pompTextWriter.println(facadeNode + ",");
						}
					}

				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

			pompTextWriter.close();

		} catch (JsonParseException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	/**
	 * An independent method for copying from pompeiiPropertyData.txt to
	 * pompeiiPropertyData.js with necessary js-specific components. Copies the data
	 * from pompeiiPropertyData.txt to updateEschebach.js in between the [ ] First,
	 * creates and writes to a textFile. Then, saves it as a .js file by renaming
	 * it.
	 * 
	 * @throws FileNotFoundException
	 * @throws UnsupportedEncodingException
	 */
	private static void copyToJavascriptFiles() {
		Utils.writeJavaScriptPropertyFile(HERC_SECTION_JAVASCRIPT_FILE, HERC_JS_FILE_HEADER,
				HERC_SECTION_DATA_TXT_FILE);
		Utils.writeJavaScriptPropertyFile(POMP_SECTION_JAVASCRIPT_FILE,
				POMP_JS_FILE_HEADER, POMP_SECTION_DATA_TXT_FILE);
	}

	private static void init() {
		Properties prop = edu.wlu.graffiti.data.main.Utils.getConfigurationProperties();
		try {
			Class.forName(prop.getProperty("db.driverClassName"));
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		try {
			dbCon = DriverManager.getConnection(prop.getProperty("db.url"), prop.getProperty("db.user"),
					prop.getProperty("db.password"));
			getId = dbCon.prepareStatement(GET_SECTION_ID);
			getCount = dbCon.prepareStatement(GET_COUNT_ON_SECTION);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private static void destroy() {
		try {
			getCount.close();
			getId.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
