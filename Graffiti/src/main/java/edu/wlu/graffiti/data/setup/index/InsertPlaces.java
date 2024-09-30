package edu.wlu.graffiti.data.setup.index;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import edu.wlu.graffiti.data.main.DBInteraction;
import edu.wlu.graffiti.data.setup.Utils;

/**
 * Adds place data to the database from spreadsheets
 * 
 * @author Trevor Stalnaker
 * @author Grace MacDonald
 * 
 * 
 */
public class InsertPlaces extends DBInteraction {

	private static final String INSERT_PLACE = "INSERT INTO places (name) VALUES (?)";

	public static void main(String[] args) {
		InsertPlaces ip = new InsertPlaces();
		ip.runDBInteractions();
		// insertPlaces();
		// System.out.println("Places indexed from file");
	}

	public void run() {
		insertPlaces();
		System.out.println("Places indexed from file");
	}

	public void insertPlaces() {

		try {
			// Create Prepared Statements
			PreparedStatement pstmt = dbCon.prepareStatement(INSERT_PLACE);

			// Read in the data from the CSV File
			Reader in = new FileReader("data/AGPData/places.csv");
			Iterable<CSVRecord> records = CSVFormat.EXCEL.parse(in);
			for (CSVRecord record : records) {
				// Clean data in CSV File and save to Strings
				String name = Utils.cleanData(record.get(0));
				// String name_type = Utils.cleanData(record.get(1));
				// String person_type = Utils.cleanData(record.get(2));
				// String gender = Utils.cleanData(record.get(3));
				if (!name.equals("Place")) {
					pstmt.setString(1, name);
					pstmt.executeUpdate();
				}
			}
			// Close statements, connections, and file readers
			pstmt.close();
			in.close();
			// newDBCon.close();

		} catch (IOException | SQLException e) {
			e.printStackTrace();
		}
	}
}
