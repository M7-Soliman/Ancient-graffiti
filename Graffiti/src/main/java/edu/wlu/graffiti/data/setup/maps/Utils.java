package edu.wlu.graffiti.data.setup.maps;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.geojson.GeoJsonObject;
import org.geojson.LngLatAlt;
import org.geojson.Polygon;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Utils {
	
	/*
	public static Properties getConfigurationProperties() {
		InputStream inputStream = null;
		Properties prop = null;
		try {
			prop = new Properties();
			String propFileName = "configuration.properties";
			inputStream = Utils.class.getClassLoader().getResourceAsStream(propFileName);
			prop.load(inputStream);
			inputStream.close();
		} catch (FileNotFoundException f) {
			System.err.println("property file 'configuration.properties' not found in the classpath");
		} catch (Exception e) {
			System.out.println("Exception: " + e);
		}
		return prop;
	}
	*/

	/**
	 * Copies generated geoJSON data to the JavaScript file for use in our
	 * interactive map. Copies the initJavaScriptFile into the finalJavaScriptFile
	 * and then copies the data from the data file to the JavaScript file in between
	 * the [ ].
	 * 
	 * @param finalJavaScriptFileLoc the final generated JavaScript file's location,
	 *                               for use in our interactive map
	 * @param initJavaScriptFileLoc  the starting JavaScript file, to be filled in
	 *                               with the data from the data file
	 * @param geoJSONDataFileLoc     the location of the file containing the geoJSON
	 *                               data for use in the interactive map.
	 */
	public static void writeJavaScriptPropertyFile(String finalJavaScriptFileLoc, 
												   String initJavaScriptFileLoc,
												   String dataFile) {
		try {
			PrintWriter jsWriter = new PrintWriter(finalJavaScriptFileLoc, "UTF-8");

			Scanner initJSReader = new Scanner(new File(initJavaScriptFileLoc));
			while (initJSReader.hasNext()) {
				String content = initJSReader.nextLine();
				jsWriter.println(content);
			}
			Scanner dataReader = new Scanner(new File(dataFile));
			String content;
			while (dataReader.hasNext()) {
				content = dataReader.nextLine();
				jsWriter.println(content);
			}
			jsWriter.println("]};");
			initJSReader.close();
			dataReader.close();
			jsWriter.close();
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}

	}
	
	/**
	 * Simplifies a geoJSON polygon object by removing the Z-coordinates and
	 * keeping only the x and y coordinates
	 * 
	 * @param featureNode
	 * @return Polygon with Z-coordinate removed
	 */
	public static Polygon parseGeometryAndRemoveCoordinates(JsonNode featureNode) throws JsonProcessingException {
		JsonNode geometryNode = featureNode.findValue("geometry");
		JsonParser coordParse = geometryNode.traverse();
		Polygon p = null;

		GeoJsonObject object;
		try {
			object = new ObjectMapper().readValue(coordParse, GeoJsonObject.class);

			if (object instanceof Polygon) {
				p = (Polygon) object;

				// go through the coordinates, removing the z-coordinate
				List<List<LngLatAlt>> newCoordList = new ArrayList<List<LngLatAlt>>();
				for (List<LngLatAlt> coordList : p.getCoordinates()) {
					List<LngLatAlt> aList = new ArrayList<LngLatAlt>();
					for (LngLatAlt coord : coordList) {
						LngLatAlt newCoord = new LngLatAlt(coord.getLongitude(), coord.getLatitude());
						aList.add(newCoord);
					}
					newCoordList.add(aList);
				}
				p.setCoordinates(newCoordList);
			}
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return p;
	}
	
}
