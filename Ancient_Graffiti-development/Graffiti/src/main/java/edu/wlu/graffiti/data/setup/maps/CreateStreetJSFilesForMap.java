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
 * Creates the JavaScript files for streets
 * 
 * @author Trevor Stalnaker
 * 
 */
public class CreateStreetJSFilesForMap {

	private static final String HERC_JS_FILE_HEADER = "src/main/resources/map_starter_text/hercStreetDataFirst.txt";
	private static final String HERC_STREET_JSON_FILE = "src/main/resources/geoJSON/herculaneum_streets.json";
	private static final String HERC_STREET_DATA_TXT_FILE = "src/main/resources/map_starter_text/hercStreetData.txt";
	private static final String HERC_STREET_JAVASCRIPT_FILE = "src/main/webapp/resources/js/herculaneumStreetData.js";

	private static final String POMPEII_JS_FILE_HEADER = "src/main/resources/map_starter_text/pompeiiFacadeDataFirst.txt";
	private static final String POMPEII_STREET_JSON_FILE = "src/main/resources/geoJSON/pompeii_facades.geojson";
	private static final String POMPEII_STREET_DATA_TXT_FILE = "src/main/resources/map_starter_text/pompeiiFacadeData.txt";
	private static final String POMPEII_STREET_JAVASCRIPT_FILE = "src/main/webapp/resources/js/pompeiiFacadeData.js";

	final static String GET_COUNT_ON_SECTION = "SELECT COUNT(*) as count FROM inscriptions "
			+ "LEFT JOIN segments ON inscriptions.segment_id=segments.id "
			+ "WHERE inscriptions.on_facade=true AND segments.street_id=?";

	final static String GET_STREET_ID = "SELECT id, street_name FROM streets WHERE UPPER(street_name) = ?";

	static Connection dbCon;

	private static PreparedStatement getCount;
	private static PreparedStatement getStreetId;

	@Resource
	private static GraffitiDao graffitiDao;

	public static void main(String args[]) {
		storeFacades();
	}

	public static void storeFacades() {
		init();
		storeHercStreets();
		storePompeiiStreets();
		copyToJavascriptFiles();
		destroy();
	}

	private static void storeHercStreets() {
		try {
			PrintWriter hercTextWriter = new PrintWriter(HERC_STREET_DATA_TXT_FILE, "UTF-8");

			// creates necessary objects to parse the original GeoJSON file
			ObjectMapper mapper = new ObjectMapper();
			JsonFactory jsonFactory = new JsonFactory();
			JsonParser jsonParser = jsonFactory.createParser(new File(HERC_STREET_JSON_FILE));

			// this accesses the 'features' level of the GeoJSON document
			JsonNode root = mapper.readTree(jsonParser);
			JsonNode featuresNode = root.path("features");

			// iterates over the features node
			Iterator<JsonNode> featureIterator = featuresNode.elements();

			while (featureIterator.hasNext()) {
				JsonNode featureNode = featureIterator.next();

				// Get the section's name
				JsonNode streetNode = featureNode.findValue("Street");
				if (streetNode == null) {
					continue;
				}
				String street = streetNode.textValue();
				System.out.println("Street Name: " + street);

				// Parse the geometry and get rid of the z coordinates
				Polygon p = Utils.parseGeometryAndRemoveCoordinates(featureNode);

				try {
					
					int street_id = -1;
					int numberOfGraffiti = 0;

					getStreetId.setString(1, street.toUpperCase());
					ResultSet idResultSet = getStreetId.executeQuery();
					if (idResultSet.next()) {
						street_id = idResultSet.getInt("id");
						System.out.println("Street ID: " + street_id);
					}

					getCount.setInt(1, street_id);
					ResultSet rs = getCount.executeQuery();
					if (rs.next()) {
						if (street_id == -1) {
							continue;
						}

						numberOfGraffiti = rs.getInt("count");

						System.out.println("Count: " + numberOfGraffiti);
						System.out.println();

						ObjectNode facadeNode = (ObjectNode) featureNode;
						ObjectNode facades = (ObjectNode) facadeNode.path("properties");

						// Add desired fields to the JSON file
						facades.put("street_name", street);
						facades.put("street_id", street_id);
						facades.put("number_of_graffiti", numberOfGraffiti);

						// Remove unwanted fields from the original JSON file
						String[] fields = { "amenity", "operator", "historic", "name", "tourism", "building",
								"man_made", "addr_house", "name_en", "heritage_o", "wikipedia", "osm_id", "ref_whc",
								"name_fr", "whc_inscri", "name_it", "heritage", "whc_criter", "name_es", "wikidata",
								"tourist_bu", "parking", "addr_city", "addr_stree", "leisure", "landuse", "commercial",
								"website", "Street" };
						for (String field : fields) {
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

	private static void storePompeiiStreets() {
		try {
			PrintWriter pompeiiTextWriter = new PrintWriter(POMPEII_STREET_DATA_TXT_FILE, "UTF-8");

			// creates necessary objects to parse the original GeoJSON file
			ObjectMapper pompeiiMapper = new ObjectMapper();
			JsonFactory pompeiiJsonFactory = new JsonFactory();
			JsonParser pompeiiJsonParser = pompeiiJsonFactory.createParser(new File(POMPEII_STREET_JSON_FILE));
			// this accesses the 'features' level of the GeoJSON document
			JsonNode pompeiiRoot = pompeiiMapper.readTree(pompeiiJsonParser);
			JsonNode pompeiiFeaturesNode = pompeiiRoot.path("features");

			// iterates over the features node
			Iterator<JsonNode> featureIterator = pompeiiFeaturesNode.elements();

			while (featureIterator.hasNext()) {
				JsonNode featureNode = featureIterator.next();

				JsonNode nameNode = featureNode.findValue("Street");

				if (nameNode == null) {
					continue;
				}

				String name = nameNode.textValue();
				
				// Skip over streets with a null value for street name
				if (name == null) {
					continue;
				}
				
				name = name.toUpperCase();
				//name = name.replaceAll("'", "''");
				System.out.println("Street Name: " + name);

				// Parse the geometry and get rid of the z coordinates
				Polygon p = Utils.parseGeometryAndRemoveCoordinates(featureNode);

				try {

					getStreetId.setString(1, name);

					ResultSet idResultSet = getStreetId.executeQuery();

					int street_id = -1;
					String street_name = "";
					int numberOfGraffiti = 0;

					if (idResultSet.next()) {
						street_id = idResultSet.getInt("id");
						street_name = idResultSet.getString("street_name");
					}

					getCount.setInt(1, street_id);
					ResultSet rs = getCount.executeQuery();

					if (rs.next()) {

						if (street_id == -1) {
							continue;
						}

						numberOfGraffiti = rs.getInt("count");

						System.out.println("Count: " + numberOfGraffiti);
						System.out.println();

						ObjectNode facadeNode = (ObjectNode) featureNode;
						ObjectNode facades = (ObjectNode) facadeNode.path("properties");

						// Add desired fields to the JSON file
						facades.put("street_id", street_id);
						facades.put("street_name", street_name);
						facades.put("number_of_graffiti", numberOfGraffiti);

						// Remove unwanted fields from the original JSON file
						String[] fields = { "NAME_1", "FCLASS_NAME", "FCLASS_ALIAS", "AUX_ORIG_LAYER", "AUX_ORIG_OBJID",
								"AUX_ORIG_HANDLE", "LOCATION_ID", "FCLASS_CODE" };
						for (String field : fields) {
							facades.remove(field);
						}

						String jsonPoly = new ObjectMapper().writeValueAsString(p);
						JsonNode updatedGeometry = new ObjectMapper().readTree(jsonPoly);

						facadeNode.replace("geometry", updatedGeometry);

						pompeiiTextWriter.println(facadeNode + ",");
					}

				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

			pompeiiTextWriter.close();

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
		Utils.writeJavaScriptPropertyFile(HERC_STREET_JAVASCRIPT_FILE, HERC_JS_FILE_HEADER, HERC_STREET_DATA_TXT_FILE);
		Utils.writeJavaScriptPropertyFile(POMPEII_STREET_JAVASCRIPT_FILE, POMPEII_JS_FILE_HEADER,
				POMPEII_STREET_DATA_TXT_FILE);
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
			getStreetId = dbCon.prepareStatement(GET_STREET_ID);
			getCount = dbCon.prepareStatement(GET_COUNT_ON_SECTION);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private static void destroy() {
		try {
			getStreetId.close();
			getCount.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
