package edu.wlu.graffiti.data.setup.maps;

import java.util.Iterator;
import java.util.Properties;

import javax.annotation.Resource;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import edu.wlu.graffiti.dao.GraffitiDao;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * This program translates each insula and its features from the SQL database
 * and json shape files to pompeiiPropertyData.txt, pompeiiInsulaData.js. Then,
 * the data can be used to efficiently provide data to geoJson for use in the
 * maps of Pompeii and Herculaneum.
 * 
 * @author Hammad Ahmad
 * @author Sara Sprenkle
 * @author Abby Nason
 * @author Trevor Stalnaker
 */
public class CreateInsulaJSFilesForMap {

	private static final String POMPEII_JS_FILE_HEADER = "src/main/resources/map_starter_text/pompeiiInsulaDataFirst.txt";
	private static final String POMPEII_INSULA_JSON_FILE = "src/main/resources/geoJSON/pompeii_insula.geojson";
	private static final String POMPEII_INSULA_DATA_TXT_FILE = "src/main/resources/map_starter_text/pompeiiInsulaData.txt";
	private static final String POMPEII_INSULA_JAVASCRIPT_FILE = "src/main/webapp/resources/js/pompeiiInsulaData.js";

	final static String SELECT_PROPERTIES_ON_INSULA = "SELECT count(*) FROM inscriptions" + " WHERE property_id IN ("
			+ "SELECT properties.id FROM properties " + "LEFT JOIN insula ON properties.insula_id=insula.id "
			+ "LEFT JOIN cities ON insula.modern_city=cities.name "
			+ "WHERE UPPER(modern_city) = UPPER(?) and insula.short_name = ? " + ")";

	final static String GET_INSULA_ID = "SELECT id, full_name from insula where short_name = ?";
	
	// Fields in the original JSON files that we don't want included in the generated Java Script
	private static String[] FIELDS_TO_REMOVE = {"FCLASS_COD","LOCATION_I","NAME","FCLASS_NAM","FCLASS_ALI",
												"AUX_ORIG_O","AUX_ORIG_H","GIS_ID","PRIMARY_DO","SECONDARY_",
												"FUNCTION_E","IMG_Link1","IMG_Link2","IMG_Link3","IMG_Link4",
												"IMG_Link5","IMG_Link6","IMG_Link7","IMG_Link8","IMG_Link9",
												"IMG_Link10","Shape__Are","Shape__Len","Shape_Length","Shape_Area",
												"OBJECTID_1","OBJECTID"};

	private static Connection dbCon;
	private static PreparedStatement selectPropertiesStatement;
	private static PreparedStatement getIdStatement;

	@Resource
	private static GraffitiDao graffitiDaoObject;

	public static void main(String args[]) {
		storeInsulae();
	}

	public static void storeInsulae() {
		init();
		storePompeii();
		Utils.writeJavaScriptPropertyFile(POMPEII_INSULA_JAVASCRIPT_FILE,
				POMPEII_JS_FILE_HEADER, POMPEII_INSULA_DATA_TXT_FILE);
	}

	/**
	 * Sets up the database connection and statements
	 */
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
			selectPropertiesStatement = dbCon.prepareStatement(SELECT_PROPERTIES_ON_INSULA);
			getIdStatement = dbCon.prepareStatement(GET_INSULA_ID);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Parses the original geoJSON data file. Merges insula data from database with
	 * geoJSON data and stores the [merged] data for Pompeii in
	 * pompeiiInsulaData.txt
	 */
	private static void storePompeii() {

		try {
			PrintWriter pompeiiTextWriter = new PrintWriter(POMPEII_INSULA_DATA_TXT_FILE, "UTF-8");

			// creates objects to parse the original GeoJSON file
			JsonFactory pompeiiJsonFactory = new JsonFactory();
			JsonParser pompeiiJsonParser = pompeiiJsonFactory.createParser(new File(POMPEII_INSULA_JSON_FILE));
			ObjectMapper pompeiiMapper = new ObjectMapper();
			JsonNode pompeiiRoot = pompeiiMapper.readTree(pompeiiJsonParser);
			// this accesses the 'features' level of the GeoJSON document
			JsonNode pompeiiFeaturesNode = pompeiiRoot.path("features");

			// iterates over the features node
			Iterator<JsonNode> featureIterator = pompeiiFeaturesNode.elements();

			while (featureIterator.hasNext()) {
				JsonNode featureNode = featureIterator.next();

				JsonNode nameNode = featureNode.findValue("NAME");
				if (nameNode == null) {
					continue;
				}

				String name = nameNode.textValue();
				if (name == null || !name.contains(".")) {
					continue;
				}

				// This was broken and I couldn't figure out why
				// Parse the geometry and get rid of the z coordinates
				//Polygon p = Utils.parseGeometryAndRemoveCoordinates(featureNode);

				try {
					// Get the insula names from the database and store in the final geoJSON file.
					selectPropertiesStatement.setString(1, "Pompeii");
					selectPropertiesStatement.setString(2, name);

					getIdStatement.setString(1, name);

					ResultSet propertyResultSet = selectPropertiesStatement.executeQuery();
					ResultSet idResultSet = getIdStatement.executeQuery();

					int id = -1;
					String fullName = "";

					if (idResultSet.next()) {
						id = idResultSet.getInt("id");
						fullName = idResultSet.getString("full_name");
					}

					if (propertyResultSet.next()) {
						int numberOfGraffiti = propertyResultSet.getInt(1);

						ObjectNode insulaNode = (ObjectNode) featureNode;
						ObjectNode insulae = (ObjectNode) insulaNode.path("properties");

						if (id == -1) {
							continue;
						}

						insulae.put("insula_id", id);
						insulae.put("insula_short_name", name);
						insulae.put("insula_full_name", fullName);
						insulae.put("number_of_graffiti", numberOfGraffiti);
						
						for (String field : FIELDS_TO_REMOVE) {
							insulae.remove(field);
						}
						
//						String jsonPoly = new ObjectMapper().writeValueAsString(p);
//						JsonNode updatedGeometry = new ObjectMapper().readTree(jsonPoly);
//						insulaNode.replace("geometry", updatedGeometry);

						pompeiiTextWriter.println(insulaNode + ",");
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

}
