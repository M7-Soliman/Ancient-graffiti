package edu.wlu.graffiti.data.main;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import edu.wlu.graffiti.data.setup.Utils;

public class InsertCitiesAndInsula extends DBInteraction {

	private static final String INSERT_INSULA_STMT = "INSERT INTO insula "
			+ "(modern_city, short_name, full_name) VALUES (?,?,?)";
	private static final String INSERT_CITY_STMT = "INSERT INTO cities " + "(name, pleiades_id) VALUES (?,?)";

	public static void main(String[] args) {
		InsertCitiesAndInsula cAndI = new InsertCitiesAndInsula();
		cAndI.runDBInteractions();
	}

	public void run() {
		insertCities("data/cities.csv");
		insertInsulae("data/insulae/herculaneum_insulae.csv");
		insertInsulae("data/insulae/pompeii_insulae.csv");
		insertInsulae("data/insulae/stabiae_insulae.csv");
		insertInsulae("data/insulae/smyrna_insulae.csv");
		try {
			dbCon.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Inserts city data into the database from a city CSV file.
	 * 
	 * @param datafileName the path to the CSV file containing the city data.
	 * @return None.
	 */
	private void insertCities(String datafileName) {
		try {
			PreparedStatement pstmt = dbCon.prepareStatement(INSERT_CITY_STMT);

			Reader in = new FileReader(datafileName);

			Iterable<CSVRecord> records = CSVFormat.EXCEL.withCommentMarker('#').parse(in);

			for (CSVRecord record : records) {
				if (!record.hasComment()) {
					String modernCity = Utils.cleanData(record.get(0));
					String pleiadesID = Utils.cleanData(record.get(1));

					pstmt.setString(1, modernCity);
					pstmt.setString(2, pleiadesID);
					try {
						System.out.println(modernCity + " " + pleiadesID);
						pstmt.executeUpdate();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				} else {
					System.out.println("comment");
				}
			}

			in.close();
			pstmt.close();

		} catch (SQLException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * Inserts insula data into the database from an insula CSV file.
	 * 
	 * @param datafileName the path to the CSV file containing the insula data.
	 * @return None.
	 */
	
	private void insertInsulae(String datafileName) {
		try {
			PreparedStatement pstmt = dbCon.prepareStatement(INSERT_INSULA_STMT);

			Reader in = new FileReader(datafileName);
			Iterable<CSVRecord> records = CSVFormat.EXCEL.parse(in);
			for (CSVRecord record : records) {
				String modernCity = Utils.cleanData(record.get(0));
				String insula = Utils.cleanData(record.get(1));
				String fullname = Utils.cleanData(record.get(2));
				// String pleaides_id = Utils.cleanData(record.get(3));

				pstmt.setString(1, modernCity);
				pstmt.setString(2, insula);
				pstmt.setString(3, fullname);
				// pstmt.setString(4, pleaides_id);
				try {
					System.out.println(modernCity + " " + insula);
					pstmt.executeUpdate();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

			in.close();
			pstmt.close();

		} catch (SQLException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
