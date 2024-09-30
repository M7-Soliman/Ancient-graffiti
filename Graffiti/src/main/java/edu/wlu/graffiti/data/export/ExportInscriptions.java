package edu.wlu.graffiti.data.export;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import edu.wlu.graffiti.data.setup.Utils;

/**
 * This class gathers key inscriptions data from the SQL database and sends it to inscriptions.csv
 * 
 * @editor John Schleider
 */

public class ExportInscriptions {
	
	private static final String NEW_LINE_SEPARATOR = "\n";
	
	private static final String GET_LEMMA= "SELECT id, graffiti_id, caption, language, content, commentary FROM inscriptions";
	
	private static final Object[] FILE_HEADER = {"id","graffiti_id", "commentary"};
	
	private static String DB_DRIVER;
	private static String DB_URL;
	private static String DB_USER;
	private static String DB_PASSWORD;
	static Connection newDBCon;
	
	/**
	 * Creates a CSV with core inscriptions data
	 * (Sends the string created in serializeToCSV() to a csv in data/AGPData/inscriptions.csv)
	 * 
	 * @param args
	 * @throws FileNotFoundException
	 */
	public static void main(String[] args) throws FileNotFoundException {
		PrintWriter writer = new PrintWriter("data/AGPData/inscriptions.csv");
		System.out.println(serializeToCSV());
		writer.print(serializeToCSV());
		writer.close();
	}
	
	/**
	 * Gathers core inscriptions data from the database and writes it to a string like a CSV
	 * 
	 * @return
	 */
	public static String serializeToCSV(){
		init();
		try {
			StringBuilder stringBuilder = new StringBuilder();
			CSVPrinter csvFilePrinter = null;
			
			CSVFormat csvFileFormat = CSVFormat.DEFAULT.withRecordSeparator(NEW_LINE_SEPARATOR);
			
			csvFilePrinter = new CSVPrinter(stringBuilder, csvFileFormat);
			csvFilePrinter.printRecord(FILE_HEADER);
			
			Statement stmt = newDBCon.createStatement();
			ResultSet rs = stmt.executeQuery(GET_LEMMA);
			while (rs.next()) {
				
				List<Object> termRecord = new ArrayList<Object>();
				for(int i = 0; i < termRecord.size(); i++) {
				// fill in the fields
				termRecord.add(rs.getString(i));
		
				}
				// write the inscription record
				csvFilePrinter.printRecord(termRecord);
				csvFilePrinter.close();							
			}
			return stringBuilder.toString();
		} catch (IOException | SQLException e) {
			e.printStackTrace();
		} 
			return "";
	}
	
	/**
	 * Connects to database with given password, username, URL, and driver
	 */
	private static void init() {
		getConfigurationProperties();

		try {
			Class.forName(DB_DRIVER);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		try {
			newDBCon = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Sets the password, username, driver, and URL for SQL database connection
	 */
	public static void getConfigurationProperties() {
		Properties prop = Utils.getConfigurationProperties();

		DB_DRIVER = prop.getProperty("db.driverClassName");
		DB_URL = prop.getProperty("db.url");
		DB_USER = prop.getProperty("db.user");
		DB_PASSWORD = prop.getProperty("db.password");
	}

}
