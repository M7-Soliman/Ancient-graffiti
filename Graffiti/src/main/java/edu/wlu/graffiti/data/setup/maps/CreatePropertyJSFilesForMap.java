package edu.wlu.graffiti.data.setup.maps;

import java.util.Iterator;
import java.util.Properties;

import javax.annotation.Resource;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import edu.wlu.graffiti.dao.FindspotDao;
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
 * This program translates each property and its features from the SQL database
 * and json shape files to pompeiiPropertyData.txt, pompeiiPropertyData.js,
 * herculaneumPropertyData.txt, and herculaneumPropertyData.js. Then, the data
 * can be used to efficiently provide data to geoJson for use in the maps of
 * Pompeii and Herculaneum.
 * 
 * @author Alicia Martinez - v1.0
 * @author Kelly McCaffrey -Created functionality for getting the number of
 *         Graffiti and automating the process of copying to
 *         pompeiiPropertyData.js -Also added all functionality for the
 *         Herculaneum map and insula information such as insula name and insula
 *         id to the .txt and .js files.
 * @author Sara Sprenkle - refactored code to make it easier to change later;
 * @author Trevor Stalnaker - edited the code to be more uniform with other scripts and 
 * 				added code to remove unwanted fields from the final JavaScript
 */
public class CreatePropertyJSFilesForMap {
	
	private static final String OSM_ID_KEY = "osm_id";
	private static final String OSM_WAY_ID_KEY = "osm_way_id";

	private static final String HERC_JS_FILE_HEADER = "src/main/resources/map_starter_text/herculaneumPropertyDataFirst.txt";
	private static final String HERC_PROPERTY_JSON_FILE = "src/main/resources/geoJSON/herculaneum_properties.geojson";
	private static final String HERC_PROPERTY_DATA_TXT_FILE = "src/main/resources/map_starter_text/herculaneumPropertyData.txt";
	private static final String HERC_PROPERTY_JAVASCRIPT_FILE = "src/main/webapp/resources/js/herculaneumPropertyData.js";
	
	private static final String POMPEII_JS_FILE_HEADER = "src/main/resources/map_starter_text/pompeiiPropertyDataFirst.txt";
	private static final String POMPEII_PROPERTY_JSON_FILE = "src/main/resources/geoJSON/agp_pompeii_properties.json";
	private static final String POMPEII_PROPERTY_DATA_TXT_FILE = "src/main/resources/map_starter_text/pompeiiPropertyData.txt";
	private static final String POMPEII_PROPERTY_JAVASCRIPT_FILE = "src/main/webapp/resources/js/pompeiiPropertyData.js";

	final static String SELECT_PROPERTY = FindspotDao.SELECT_BY_CITY_AND_INSULA_AND_PROPERTY_STATEMENT;

	final static String SELECT_BY_OSM_WAY_ID = FindspotDao.SELECT_BY_OSM_WAY_ID_STATEMENT;

	final static String SELECT_BY_OSM_ID = FindspotDao.SELECT_BY_OSM_ID_STATEMENT;

	final static String SELECT_BY_CITY_AND_INSULA = FindspotDao.SELECT_BY_PROPERTY_ID_STATEMENT;

	final static String GET_NUMBER = GraffitiDao.FIND_BY_PROPERTY;

	final static String GET_PROPERTY_TYPE = "SELECT * FROM properties, propertytypes,"
			+ " propertytopropertytype WHERE properties.id = propertytopropertytype.property_id"
			+ " AND propertytypes.id = propertytopropertytype.property_type AND properties.id = ?";
	
	// Fields in the original JSON files that we don't want included in the generated Java Script
	private static String[] HERC_FIELDS_TO_REMOVE = {"osm_way_id","highway","waterway","aerialway","barrier","man_made",
												"z_order","other_tags","aeroway","amenity","admin_level","boundary",
												"building","craft","geological","historic","land_area","landuse","leisure",
												"military","natural","office","place","shop","sport","tourism","osm_id",
												"type","name"};
	
	private static String[] POMP_FIELDS_TO_REMOVE = {"OBJECTID_1","OBJECTID","GIS_PROPER","PinP_Addre","PinP_URL",
													 "CTP_Page_N","EntrancePr","EntranceSe","Name_1","Name_2","Name_3",
													 "Name_4","Name_5","Name_6","Name_7","Name_8","Name_9","Name_10","Name_11",
													 "Name_12","Name_13","Name_14","Ref_Name_1","Ref_Name_2","Ref_Name_3","Ref_Name_4",
													 "Ref_Name_5","Ref_Name_6","Ref_Name_7","Ref_Name_8","Ref_Name_9","Ref_Name_10",
													 "Ref_Name_11","Ref_Name_12","Ref_Name_13","Notes","Refs","LOCATION_I","AUX_ORIG_H",
													 "FCLASS_NAM","Names_","Shape__Are","Shape__Len","FCLASS_COD","NAME","FCLASS_ALI",
													 "AUX_ORIG_O","GIS_ID","PRIMARY_DO","SECONDARY_",
													 "FUNCTION_E","IMG_Link1","IMG_Link2","IMG_Link3","IMG_Link4",
													 "IMG_Link5","IMG_Link6","IMG_Link7","IMG_Link8","IMG_Link9",
													 "IMG_Link10","Shape_Length","Shape_Area","Ref_Nam_10","Ref_Nam_11",
													 "Ref_Nam_12","Ref_Nam_13"};

	static Connection dbCon;

	private static PreparedStatement selectPropertyStatement;
	private static PreparedStatement getPropertyTypeStatement;
	private static PreparedStatement getNumberStatement;
	private static PreparedStatement osmWayIdSelectionStatement;
	private static PreparedStatement osmIdSelectionStatement;
	private static PreparedStatement selectCityAndInsulaStatement;

	@Resource
	private static GraffitiDao graffitiDaoObject;

	public static void main(String args[]) {
		storeProperties();
	}

	/**
	 * Store properties from Herculaneum and Pompeii, with their shapes and graffiti
	 * info, into their respective files.
	 * 
	 * @throws SQLException
	 */
	public static void storeProperties() {
		init();
		storeHerculaneum();
		storePompeii();
		copyToJavascriptFiles();
	}

	/**
	 * Set up the program to talk to the database
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
			selectCityAndInsulaStatement = dbCon.prepareStatement(SELECT_BY_CITY_AND_INSULA);
			osmWayIdSelectionStatement = dbCon.prepareStatement(SELECT_BY_OSM_WAY_ID);
			osmIdSelectionStatement = dbCon.prepareStatement(SELECT_BY_OSM_ID);
			selectPropertyStatement = dbCon.prepareStatement(SELECT_PROPERTY);
			getPropertyTypeStatement = dbCon.prepareStatement(GET_PROPERTY_TYPE);
			getNumberStatement = dbCon.prepareStatement(GET_NUMBER);
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Parses the original Herculaneum properties geoJSON data file. Merges property
	 * data from database with geoJSON data and stores the [merged] data for
	 * Herculaneum in herculaneumPropertyData.txt
	 * 
	 * @throws SQLException
	 */
	private static void storeHerculaneum() {
		try {
			// creates the file we will later write the updated graffito to.
			PrintWriter herculaneumTextWriter = new PrintWriter(HERC_PROPERTY_DATA_TXT_FILE, "UTF-8");

			// parse the geoJSON file
			ObjectMapper herculaneumMapper = new ObjectMapper();
			JsonFactory herculaneumJsonFactory = new JsonFactory();
			JsonParser herculaneumJsonParser = herculaneumJsonFactory.createParser(new File(HERC_PROPERTY_JSON_FILE));

			JsonNode herculaneumRoot = herculaneumMapper.readTree(herculaneumJsonParser);
			JsonNode herculaneumFeaturesNode = herculaneumRoot.path("features");

			Iterator<JsonNode> herculaneumIterator = herculaneumFeaturesNode.elements();

			while (herculaneumIterator.hasNext()) {

				JsonNode hercProperty = herculaneumIterator.next();

				JsonNode osmWayNode = hercProperty.findValue(OSM_WAY_ID_KEY);
				JsonNode osmNode = hercProperty.findValue(OSM_ID_KEY);

				if (osmWayNode != null && osmWayNode.textValue() != null) {
					String osm_way_id = osmWayNode.textValue();
					try {
						osmWayIdSelectionStatement.setString(1, osm_way_id);

						ResultSet propertyRS = osmWayIdSelectionStatement.executeQuery();

						if (propertyRS.next()) {
							writeHerculaneumPropertyInfoToFile(herculaneumTextWriter, hercProperty, propertyRS);
						} else {
							System.out.println("Property with osm way id " + osm_way_id + " not in database.");
						}
					} catch (SQLException e) {
						e.printStackTrace();
					}
				} else if (osmNode != null) {
					String osm_id = osmNode.textValue();
					try {
						osmIdSelectionStatement.setString(1, osm_id);

						ResultSet propertyRS = osmIdSelectionStatement.executeQuery();

						if (propertyRS.next()) {
							writeHerculaneumPropertyInfoToFile(herculaneumTextWriter, hercProperty, propertyRS);
						} else {
							System.out.println("Property with osm id " + osm_id + " not in database.");
						}
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			}
			herculaneumTextWriter.close();
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void writeHerculaneumPropertyInfoToFile(PrintWriter herculaneumTextWriter, JsonNode hercProperty,
			ResultSet rs) throws SQLException {
		int propertyId = rs.getInt("id");

		String propertyNumber = rs.getString("property_number");
		String propertyName = rs.getString("property_name");
		String addProperties = rs.getString("additional_properties");
		String italPropName = rs.getString("italian_property_name");
		String insulaId = rs.getString("insula_id");
		String propertyAddress = "";

		ObjectNode graffito = (ObjectNode) hercProperty;
		ObjectNode properties = (ObjectNode) graffito.path("properties");

		selectCityAndInsulaStatement.setInt(1, propertyId);

		ResultSet insulaResultSet = selectCityAndInsulaStatement.executeQuery();

		if (insulaResultSet.next()) {
			String shortInsulaName = insulaResultSet.getString("short_name");
			String fullInsulaName = insulaResultSet.getString("full_name");

			properties.put("short_insula_name", shortInsulaName);
			properties.put("full_insula_name", fullInsulaName);
			propertyAddress += shortInsulaName + "." + propertyNumber;
		}

		getNumberStatement.setInt(1, propertyId);
		ResultSet numberOnPropResultSet = getNumberStatement.executeQuery();
		int numberOfGraffitiOnProperty = 0;
		if (numberOnPropResultSet.next()) {
			numberOfGraffitiOnProperty = numberOnPropResultSet.getInt(1);
		}

		getPropertyTypeStatement.setInt(1, propertyId);
		ResultSet resultset = getPropertyTypeStatement.executeQuery();
		String propertyType = "";
		if (resultset.next()) {
			propertyType = resultset.getString("name");
		}
		
		properties.put("Property_Id", propertyId);
		properties.put("Number_Of_Graffiti", numberOfGraffitiOnProperty);
		properties.put("Property_Name", propertyName);
		properties.put("Additional_Properties", addProperties);
		properties.put("Italian_Property_Name", italPropName);
		properties.put("insula_id", insulaId);
		properties.put("Property_Address", propertyAddress);

		properties.put("Property_Type", propertyType);
		
		for (String field : HERC_FIELDS_TO_REMOVE) {
			properties.remove(field);
		}
		
		// write the newly updated graffito to text file
		herculaneumTextWriter.println(graffito + ",");
	}

	/**
	 * Parses the original Pompeii properties geoJSON data file. Merges property
	 * data from database with geoJSON data and stores the [merged] data for Pompeii
	 * in pompeiiPropertyData.txt
	 * 
	 * @throws SQLException
	 */
	private static void storePompeii() {

		try {
			PrintWriter pompeiiTextWriter = new PrintWriter(POMPEII_PROPERTY_DATA_TXT_FILE, "UTF-8");

			// creates necessary objects to parse the original GeoJSON file
			ObjectMapper pompeiiMapper = new ObjectMapper();
			JsonFactory pompeiiJsonFactory = new JsonFactory();
			JsonParser pompeiiJsonParser = pompeiiJsonFactory.createParser(new File(POMPEII_PROPERTY_JSON_FILE));
			JsonNode pompeiiRoot = pompeiiMapper.readTree(pompeiiJsonParser);
			// this accesses the 'features' level of the GeoJSON document
			JsonNode pompeiiFeaturesNode = pompeiiRoot.path("features");

			// iterates over the features node
			Iterator<JsonNode> featureIterator = pompeiiFeaturesNode.elements();
			
			while (featureIterator.hasNext()) {
				JsonNode featureNode = featureIterator.next();
				//System.out.println("***" + featureNode);

				String address = extractPompeiiPropertyAddress(featureNode);
				if (address.equals("")) {
					//System.out.println("No address found???");
					continue;
				}

				String[] parts = address.split("\\.");

				String pt1 = parts[0];
				String pt2 = parts[1];
				String pt3;
				if (parts.length == 3) {
					pt3 = parts[2];
				} else {
					pt3 = "";
				}

				String insulaName = pt1 + "." + pt2;
				String propertyNum = pt3;

				//System.out.println(insulaName + "." + propertyNum);

				// Parse the geometry and get rid of the z coordinates
				//Polygon p = Utils.parseGeometryAndRemoveCoordinates(featureNode);

				try {
					// Get the insula names from the database and store.
					selectPropertyStatement.setString(1, "Pompeii");
					selectPropertyStatement.setString(2, insulaName);
					selectPropertyStatement.setString(3, propertyNum);

					ResultSet propertyResultSet = selectPropertyStatement.executeQuery();

					if (propertyResultSet.next()) {

						int propertyId = propertyResultSet.getInt("id");
						selectCityAndInsulaStatement.setInt(1, propertyId);
						
						

						ResultSet insulaResultSet = selectCityAndInsulaStatement.executeQuery();

						ObjectNode propertyNode = (ObjectNode) featureNode;
						ObjectNode properties = (ObjectNode) propertyNode.path("properties");

						if (insulaResultSet.next()) {
							
							String shortInsulaName = insulaResultSet.getString("short_name");
							String fullInsulaName = insulaResultSet.getString("full_name");

							properties.put("short_insula_name", shortInsulaName);
							properties.put("full_insula_name", fullInsulaName);
							
						}

						String propertyName = propertyResultSet.getString("property_name");
						String addProperties = propertyResultSet.getString("additional_properties");
						String italPropName = propertyResultSet.getString("italian_property_name");
						String insulaDescription = propertyResultSet.getString("description");
						String insulaPleiadesId = propertyResultSet.getString("insula_pleiades_id");
						String propPleiadesId = propertyResultSet.getString("property_pleiades_id");
						String insulaId = propertyResultSet.getString("insula_id");

						getNumberStatement.setInt(1, propertyId);
						ResultSet numberOnPropResultSet = getNumberStatement.executeQuery();
						int numberOfGraffitiOnProperty = 0;
						if (numberOnPropResultSet.next()) {
							numberOfGraffitiOnProperty = numberOnPropResultSet.getInt(1);
						}

						getPropertyTypeStatement.setInt(1, propertyId);
						ResultSet resultset = getPropertyTypeStatement.executeQuery();
						String propertyType = "";
						if (resultset.next()) {
							propertyType = resultset.getString("name");
						}
						
						// Clean up JSON by converting PRIMARY_DO to PRIMARY_DOOR
						JsonNode pdoor = properties.findValue("PRIMARY_DO");
						if(pdoor != null) {
							String primary = pdoor.textValue();
							properties.put("PRIMARY_DOOR", primary);
						}

						properties.put("Property_Id", propertyId);
						properties.put("Number_Of_Graffiti", numberOfGraffitiOnProperty);
						properties.put("Property_Name", propertyName);
						properties.put("Additional_Properties", addProperties);
						properties.put("Italian_Property_Name", italPropName);
						properties.put("Insula_Description", insulaDescription);
						properties.put("Insula_Pleiades_Id", insulaPleiadesId);
						properties.put("Property_Pleiades_Id", propPleiadesId);
						properties.put("Property_Type", propertyType);
						properties.put("insula_id", insulaId);

						for (String field : POMP_FIELDS_TO_REMOVE) {
							properties.remove(field);
						}

						//String jsonPoly = new ObjectMapper().writeValueAsString(p);
						//JsonNode updatedGeometry = new ObjectMapper().readTree(jsonPoly);
						//propertyNode.replace("geometry", updatedGeometry);

						pompeiiTextWriter.println(propertyNode + ",");
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
	 * Extract the Pompeii property address from the GeoJSON node and return it as a
	 * String
	 * 
	 * @param featureNode
	 * @return the String representing the property's address in Pompeii
	 */
	private static String extractPompeiiPropertyAddress(JsonNode featureNode) {
		JsonNode primaryDONode = featureNode.findValue("PRIMARY_DO");
		if (primaryDONode == null) {
			primaryDONode = featureNode.findValue("PRIMARY_DOOR");
			if (primaryDONode == null) {
				System.out.println("nope");
				// Needs to use the other field...
				primaryDONode = featureNode.findValue("PinP_Addre");
			}
		}
		String primaryDO = primaryDONode.textValue();
		if( primaryDO == null || primaryDO.equals("null") ) {
			primaryDO = featureNode.findValue("PinP_Addre").textValue();
		}
		
		if (primaryDO == null || !primaryDO.contains(".")) {
			return "";
		}
		return primaryDO;
	}

	/**
	 * Copies from pompeiiPropertyData.txt to pompeiiPropertyData.js with necessary
	 * js-specific components. Copies the data from pompeiiPropertyData.txt to
	 * updateEschebach.js in between the [ ] First, creates and writes to a
	 * textFile. Then, saves it as a .js file by renaming it.
	 */
	private static void copyToJavascriptFiles() {
		Utils.writeJavaScriptPropertyFile(POMPEII_PROPERTY_JAVASCRIPT_FILE, POMPEII_JS_FILE_HEADER,
				POMPEII_PROPERTY_DATA_TXT_FILE);
		Utils.writeJavaScriptPropertyFile(HERC_PROPERTY_JAVASCRIPT_FILE, HERC_JS_FILE_HEADER,
				HERC_PROPERTY_DATA_TXT_FILE);
	}

}
