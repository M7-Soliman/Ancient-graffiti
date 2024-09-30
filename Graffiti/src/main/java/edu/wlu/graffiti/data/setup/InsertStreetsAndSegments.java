package edu.wlu.graffiti.data.setup;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import edu.wlu.graffiti.data.main.DBInteraction;

/**
 * 
 * @author Trevor Stalnaker
 * 
 *         Reads in street and segment data from spreadsheets and populates the
 *         database accordingly
 *
 */
public class InsertStreetsAndSegments extends DBInteraction {

	private static final String SEGMENTS_TO_GRAFFITI_DATA_CSV = "data/AGPData/segments2graffiti.csv";
	public static final String SEGMENTS_DATA_CSV = "data/AGPData/all_sections.csv";
	public static final String STREETS_DATA_CSV = "data/AGPData/all_streets.csv";
	private static String DELETE_SEGMENTS = "DELETE FROM segments";
	private static String DELETE_STREETS = "DELETE FROM streets";
	private static String RESET_STREET_SEQUENCE = "ALTER SEQUENCE street_id_seq RESTART";
	private static String RESET_SEGMENT_SEQUENCE = "ALTER SEQUENCE segment_id_seq RESTART";

	private static String INSERT_STREET = "INSERT INTO streets (street_name, city) VALUES (?,?)";
	private static String INSERT_SEGMENT = "INSERT INTO segments (segment_name, street_id, display_name, hidden) VALUES (?,?,?,?)";
	private static String SET_SEGMENT_ID = "UPDATE inscriptions SET segment_id=? WHERE graffiti_id=?";

	private static String GET_STREET_ID = "SELECT id FROM streets WHERE street_name=?";
	private static String GET_SEGMENT_ID = "SELECT id FROM segments WHERE segment_name=?";

	public static void main(String[] args) {
		InsertStreetsAndSegments isas = new InsertStreetsAndSegments();
		isas.runDBInteractions();
	}
	
	@Override
	public void run() {
		System.out.println("Importing Streets and Segments...");
		insertStreets();
		insertSegments();
		System.out.println("Done importing streets and segments!");
	}

	private void insertStreets() {
		try {

			// Restart the streets and segments tables
			// Mostly for testing purposes
			Statement restart = dbCon.createStatement();
			restart.addBatch(DELETE_SEGMENTS);
			restart.addBatch(DELETE_STREETS);
			restart.addBatch(RESET_SEGMENT_SEQUENCE);
			restart.addBatch(RESET_STREET_SEQUENCE);
			restart.executeBatch();
			restart.close();

			// Create Prepared Statements
			PreparedStatement pstmt = dbCon.prepareStatement(INSERT_STREET);

			// Read in the data from the CSV File
			Reader in = new FileReader(STREETS_DATA_CSV);
			Iterable<CSVRecord> records = CSVFormat.EXCEL.parse(in);
			for (CSVRecord record : records) {

				// Clean data in CSV File and save to Strings
				String street = Utils.cleanData(record.get(0));
				String city = Utils.cleanData(record.get(1));

				// Add Street to the database
				pstmt.setString(1, street);
				pstmt.setString(2, city);
				pstmt.executeUpdate();

			}
			// Close statements, connections, and file readers
			pstmt.close();
			in.close();
		} catch (IOException | SQLException e) {
			e.printStackTrace();
		}
	}

	private void insertSegments() {
		try {

			// Create Prepared Statements
			PreparedStatement pstmt = dbCon.prepareStatement(INSERT_SEGMENT);
			PreparedStatement pstmt2 = dbCon.prepareStatement(GET_STREET_ID);

			// Read in the data from the CSV File
			Reader in = new FileReader(SEGMENTS_DATA_CSV);
			Iterable<CSVRecord> records = CSVFormat.EXCEL.parse(in);
			for (CSVRecord record : records) {

				// Clean data in CSV File and save to Strings
				String segment = Utils.cleanData(record.get(0));
				String street = Utils.cleanData(record.get(1));
				String display_name = Utils.cleanData(record.get(2));
				if (display_name.equals("")) {
					display_name = segment;
				}
				Boolean hidden = Boolean.parseBoolean(Utils.cleanData(record.get(3)));

				pstmt2.setString(1, street);
				ResultSet rs = pstmt2.executeQuery();
				if (rs.next()) {
					// Add Segment to the database
					pstmt.setString(1, segment);
					pstmt.setInt(2, rs.getInt("id"));
					pstmt.setString(3, display_name);
					pstmt.setBoolean(4, hidden);
					pstmt.executeUpdate();
				}
				rs.close();
			}
			// Close statements, connections, and file readers
			pstmt.close();
			pstmt2.close();
			in.close();

		} catch (IOException | SQLException e) {
			e.printStackTrace();
		}
	}

}
