package edu.wlu.graffiti.data.setup.maps;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Properties;

import org.geojson.Polygon;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * 
 * @author Trevor Stalnaker
 *
 *         Creates the JavaScript file for columns in insula II.7
 */
public class CreateColumnJSFilesForMap {

	private static final String COLUMN_JS_FILE_HEADER = "src/main/resources/map_starter_text/columnDataFirst.txt";
	private static final String COLUMN_JSON_FILE = "src/main/resources/geoJSON/columns.json";
	private static final String COLUMN_DATA_TXT_FILE = "src/main/resources/map_starter_text/columnData.txt";
	private static final String COLUMN_JAVASCRIPT_FILE = "src/main/webapp/resources/js/columnData.js";

	final static String GET_COLUMN_INFO = "SELECT * FROM columns WHERE decimal_number = ?";

	final static String GET_COUNT_ON_COLUMN = "SELECT COUNT(*) as count FROM inscriptions "
			+ "LEFT JOIN columns ON inscriptions.column_id=columns.id "
			+ "WHERE inscriptions.on_column=true AND columns.id=?";

	static Connection dbCon;

	private static PreparedStatement getCount;
	private static PreparedStatement getInfo;

	public static void main(String args[]) {
		init();
		storeColumns();
		copyToJavascriptFiles();
		destroy();
	}

	private static void storeColumns() {
		try {
			PrintWriter colTextWriter = new PrintWriter(COLUMN_DATA_TXT_FILE, "UTF-8");

			// creates necessary objects to parse the original GeoJSON file
			ObjectMapper mapper = new ObjectMapper();
			JsonFactory jsonFactory = new JsonFactory();
			JsonParser jsonParser = jsonFactory.createParser(new File(COLUMN_JSON_FILE));

			// this accesses the 'features' level of the GeoJSON document
			JsonNode root = mapper.readTree(jsonParser);
			JsonNode featuresNode = root.path("features");

			// iterates over the features node
			Iterator<JsonNode> featureIterator = featuresNode.elements();

			while (featureIterator.hasNext()) {
				JsonNode featureNode = featureIterator.next();

				// Get the column number
				JsonNode colNode = featureNode.findValue("column_number");
				if (colNode == null) {
					continue;
				}
				int number = Integer.parseInt(colNode.textValue());
				System.out.println("Column Number: " + number);

				// Parse the geometry and get rid of the z coordinates
				Polygon p = Utils.parseGeometryAndRemoveCoordinates(featureNode);

				try {

					int column_id = -1;
					int numberOfGraffiti = 0;
					String numeral = "";

					getInfo.setInt(1, number);
					ResultSet rs = getInfo.executeQuery();
					if (rs.next()) {
						column_id = rs.getInt("id");
						numeral = rs.getString("roman_numeral");
						System.out.println("Numeral: " + numeral);
						System.out.println("Column ID: " + column_id);
					}

					getCount.setInt(1, column_id);
					ResultSet rs2 = getCount.executeQuery();
					if (rs2.next()) {
						if (column_id == -1) {
							continue;
						}

						numberOfGraffiti = rs2.getInt("count");

						System.out.println("Count: " + numberOfGraffiti);
						System.out.println();

						ObjectNode columnNode = (ObjectNode) featureNode;
						ObjectNode cols = (ObjectNode) columnNode.path("properties");

						cols.put("column_id", column_id);
						cols.put("numeral", numeral);
						cols.put("number_of_graffiti", numberOfGraffiti);

						String jsonPoly = new ObjectMapper().writeValueAsString(p);
						JsonNode updatedGeometry = new ObjectMapper().readTree(jsonPoly);

						columnNode.replace("geometry", updatedGeometry);

						colTextWriter.println(columnNode + ",");
					}

				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

			colTextWriter.close();

		} catch (JsonParseException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	private static void copyToJavascriptFiles() {
		Utils.writeJavaScriptPropertyFile(COLUMN_JAVASCRIPT_FILE, COLUMN_JS_FILE_HEADER, COLUMN_DATA_TXT_FILE);
	}

	private static void init() {
		// Sets database url using the configuration file.
		Properties prop = edu.wlu.graffiti.data.main.Utils.getConfigurationProperties();
		try {
			Class.forName(prop.getProperty("db.driverClassName"));
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		try {
			dbCon = DriverManager.getConnection(prop.getProperty("db.url"), prop.getProperty("db.user"),
					prop.getProperty("db.password"));
			getInfo = dbCon.prepareStatement(GET_COLUMN_INFO);
			getCount = dbCon.prepareStatement(GET_COUNT_ON_COLUMN);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private static void destroy() {
		try {
			getCount.close();
			getInfo.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
