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
 * 
 * @author Trevor Stalnaker
 *
 * Exports the lemma table (used for indexing) as a csv file
 */
public class ExportLemma {
	
	private static final String NEW_LINE_SEPARATOR = "\n";
	
	private static final String GET_LEMMA= "SELECT term, lemma FROM lemma ORDER BY lemma";
	
	private static final Object[] FILE_HEADER = {"term","lemma"};
	
	private static String DB_DRIVER;
	private static String DB_URL;
	private static String DB_USER;
	private static String DB_PASSWORD;
	static Connection newDBCon;
	
	public static void main(String[] args) throws FileNotFoundException {
		PrintWriter writer = new PrintWriter("data/AGPData/lemma.csv");
		System.out.println(serializeToCSV());
		writer.print(serializeToCSV());
		writer.close();
	}
	
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
				
				// fill in the fields
				termRecord.add(rs.getString(1));
				termRecord.add(rs.getString(2));
				
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

	public static void getConfigurationProperties() {
		Properties prop = Utils.getConfigurationProperties();

		DB_DRIVER = prop.getProperty("db.driverClassName");
		DB_URL = prop.getProperty("db.url");
		DB_USER = prop.getProperty("db.user");
		DB_PASSWORD = prop.getProperty("db.password");
	}
	
}
