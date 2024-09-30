package edu.wlu.graffiti.data.setup;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import edu.wlu.graffiti.data.main.DBInteraction;

/**
 * 
 * @author Trevor Stalnaker
 * 
 */
public class InsertOnSiteContributors extends DBInteraction {

	private static final String CONTRIBUTORS_CSV_LOC = "data/AGPData/contributors.csv";

	private static String INSERT_CONTRIBUTORS = "UPDATE inscriptions SET contributors=? WHERE graffiti_id=?";

	public static void main(String[] args) {
		InsertOnSiteContributors inserter = new InsertOnSiteContributors();
		inserter.runDBInteractions();
	}
	
	@Override
	public void run() {
		insertContributors();
	}

	public void insertContributors() {

		try {
			// Create Prepared Statements
			PreparedStatement pstmt = dbCon.prepareStatement(INSERT_CONTRIBUTORS);
			// Read in the data from the CSV File
			Reader in;
			in = new FileReader(CONTRIBUTORS_CSV_LOC);
			Iterable<CSVRecord> records = CSVFormat.EXCEL.parse(in);
			for (CSVRecord record : records) {
				// Clean data in CSV File and save to Strings
				String graffiti_id = Utils.cleanData(record.get(0));
				String contributors = Utils.cleanData(record.get(3));
				if (!graffiti_id.equals("")) {
					pstmt.setString(2, graffiti_id);
					pstmt.setString(1, contributors);
					pstmt.executeUpdate();
				}
			}
			pstmt.close();
			in.close();
		} catch (IOException | SQLException e) {
			e.printStackTrace();
		}
	}
	
}
