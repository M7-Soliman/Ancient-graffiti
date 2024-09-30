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
 * Adds name data to the database from spreadsheets
 * 
 * @author Trevor Stalnaker
 * @author Grace MacDonald
 *
 */
public class InsertNames extends DBInteraction {

	private static final String INSERT_NAME = "INSERT INTO names (name, name_type, person_type, gender) VALUES (?,?,?,?)";

	public static void main(String[] args) {
		InsertNames in = new InsertNames();
		in.runDBInteractions();
		// insertNames();
		// System.out.println("Names indexed from file");
	}

	public void run() {
		insertNames();
		System.out.println("Names indexed from file");

	}

	public void insertNames() {
		try {
			// Create Prepared Statements
			PreparedStatement pstmt = dbCon.prepareStatement(INSERT_NAME);

			// Read in the data from the CSV File
			Reader in = new FileReader("data/AGPData/names.csv");
			Iterable<CSVRecord> records = CSVFormat.EXCEL.parse(in);
			for (CSVRecord record : records) {
				// Clean data in CSV File and save to Strings
				String name = Utils.cleanData(record.get(0));
				String name_type = Utils.cleanData(record.get(1));
				String person_type = Utils.cleanData(record.get(2));
				String gender = Utils.cleanData(record.get(3));

				if (!name.equals("Name")) {
					pstmt.setString(1, name);
					pstmt.setString(2, name_type);
					pstmt.setString(3, person_type);
					pstmt.setString(4, gender);
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
