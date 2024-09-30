/**
 * 
 */
package edu.wlu.graffiti.data.setup.maps;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.Scanner;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

/**
 * For our maps, we need JavaScript files that contain variables that contain
 * the GeoJSON data. We can autogenerate these files from the GeoJSON files.
 * 
 * These are the "static" files that don't require info from the database.
 * 
 * @author Sara Sprenkle
 *
 */
public class CreateDisplayOnlyJSFilesForMap {

	public static String GEO_JSON_FILE_LOC = "src/main/resources/geoJSON";
	public static String JS_FILE_LOC = "src/main/webapp/resources/js";
	public static String MAP_DATA_CSV_LOC = "data/mapdata.csv";

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		createJavaScriptVariableFilesForMap();
	}

	private static void createJavaScriptVariableFilesForMap() {
		// for each data type, call createFile
		Reader in;
		try {
			in = new FileReader(MAP_DATA_CSV_LOC);
			Iterable<CSVRecord> records = CSVFormat.EXCEL.parse(in);

			for (CSVRecord record : records) {
				String dataName = record.get(0).trim();
				if( dataName.startsWith("#")) {
					continue;
				}
				String varName = record.get(1).trim();
				createFile(dataName, varName);
			}
			in.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	private static void createFile(String geoJSONDataName, String jsVariableName) {
		File geoJSONFile = new File(GEO_JSON_FILE_LOC + "/" + geoJSONDataName + ".geojson");
		File javaScriptFile = new File(JS_FILE_LOC + "/" + jsVariableName + ".js");

		Scanner geoJSONReader;
		PrintWriter javaScriptWriter;
		try {
			
			geoJSONReader = new Scanner(geoJSONFile);
			javaScriptWriter = new PrintWriter(javaScriptFile);
			// create the variable
			javaScriptWriter.print("var ");
			javaScriptWriter.print(jsVariableName);
			javaScriptWriter.print(" = ");
			
			while( geoJSONReader.hasNextLine() ) {
				javaScriptWriter.print(geoJSONReader.nextLine());
			}
			geoJSONReader.close();

			// add a semicolon to complete the statement
			javaScriptWriter.print(";");
			
			javaScriptWriter.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
