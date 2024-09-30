package edu.wlu.graffiti.data.setup;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import edu.wlu.graffiti.data.main.DBInteraction;

/**
 * Imports data from the featuredGraffiti.csv file into the database.
 * 
 * Each element in the CSV file has four elements: graffiti_id, theme(s),
 * commentary, preferred_image. Separate themes with the "/" character to
 * provide more than one, white space doesn't matter here. Capitalization
 * doesn't matter for themes. The preferred_image can be provided as the last 6
 * digits of the graffiti_id.
 * 
 * Example:EDR152963, FIGURAL / travel/Grammar, example commentary, 152963
 * 
 * @author Hammad Ahmad
 * @author Trevor Stalnaker
 */

public class InsertFeaturedGraffiti extends DBInteraction {

	private static final int PREFERRED_IMAGE_LOCATION_IN_CSV = 4;
	private static final int COMMENTARY_LOCATION_IN_CSV = 3;
	private static final int THEME_LOCATION_IN_CSV = 2;
	private static final int GRAFFITI_ID_LOCATION_IN_CSV = 0;
	private static final String INSERT_FEATURED_GRAFFITI = "INSERT INTO graffititothemes " + "(graffito_id, theme_id) "
			+ "VALUES (?, ?)";
	private static final String SET_INSCRIPTION_AS_THEMED = "UPDATE inscriptions " + "SET is_themed=true "
			+ "where graffiti_id=(?)";
	private static final String GET_THEME_ID = "SELECT theme_id FROM themes WHERE name = ?";

	private static final String INSERT_FEATURED_GRAFFITI_INFO = "INSERT INTO featured_graffiti_info "
			+ "(graffiti_id, commentary, preferred_image) " + "VALUES (?, ?, ?)";

	private static final String SET_GREATEST_FIGURAL_HIT = "UPDATE inscriptions " + "SET is_featured_figural=true "
			+ "WHERE graffiti_id=(?)";

	public static final String UPDATE_GH_INFO = "UPDATE featured_graffiti_info SET commentary = ?, preferred_image = ? WHERE graffiti_id=?";
	public static final String SELECT_GH_INFO = "SELECT * from featured_graffiti_info where graffiti_id = ?";
	public static final String INSERT_GH_INFO = "INSERT INTO featured_graffiti_info VALUES (?, ?, ?)";

	private static final String GET_FEATURED_FIGURAL_ID = "SELECT theme_id FROM themes WHERE name='Figural'";

	public static void main(String[] args) {
		InsertFeaturedGraffiti inserter = new InsertFeaturedGraffiti();
		inserter.runDBInteractions();
	}

	public void insertFeaturedGraffiti() {

		try {

			// Create Prepared Statements
			PreparedStatement pstmt = dbCon.prepareStatement(INSERT_FEATURED_GRAFFITI);
			PreparedStatement pstmt2 = dbCon.prepareStatement(SET_INSCRIPTION_AS_THEMED);
			PreparedStatement pstmt3 = dbCon.prepareStatement(GET_THEME_ID);
			PreparedStatement pstmt4 = dbCon.prepareStatement(INSERT_FEATURED_GRAFFITI_INFO);
			PreparedStatement pstmt5 = dbCon.prepareStatement(SET_GREATEST_FIGURAL_HIT);
			PreparedStatement pstmt6 = dbCon.prepareStatement(GET_FEATURED_FIGURAL_ID);

			// Determine the ID for figural graffiti
			int figID = 0;
			ResultSet figRS = pstmt6.executeQuery();
			if (figRS.next()) {
				figID = figRS.getInt("theme_id");
			}

			// Read in the data from the CSV File
			Reader in = new FileReader("data/AGPData/featured_graffiti.csv");
			Iterable<CSVRecord> records = CSVFormat.EXCEL.parse(in);
			for (CSVRecord record : records) {

				// Clean data in CSV File and save to Strings
				String graffiti_id = Utils.cleanData(record.get(GRAFFITI_ID_LOCATION_IN_CSV));
				String theme = Utils.cleanData(record.get(THEME_LOCATION_IN_CSV));
				String commentary = Utils.cleanData(record.get(COMMENTARY_LOCATION_IN_CSV));
				String image = Utils.cleanData(record.get(PREFERRED_IMAGE_LOCATION_IN_CSV));

				if (!graffiti_id.startsWith("#")) {

					LinkedList<Integer> themeIds = new LinkedList<Integer>();
					int themeId;

					// Split theme names about the '/' character
					for (String t : theme.split("/")) {

						// Normalize theme string to ensure theme is found if it exists
						t = t.replaceAll("\\s", ""); // Removes white spaces from the string
						t = String.valueOf(t.charAt(0)).toUpperCase() + t.substring(1, t.length()).toLowerCase();

						// Find the theme_id for the given record using normalized theme string
						pstmt3.setString(1, t);
						ResultSet rs = pstmt3.executeQuery();

						// Add theme_ids to themeIds if they exist and are not already present in list
						while (rs.next()) {
							themeId = rs.getInt("theme_id");
							if (!themeIds.contains(themeId) && themeId != 0) {
								themeIds.add(themeId);
							}
						}
					}
					// Verify that there is at least one theme_id and update database
					if (themeIds.size() != 0) {

						// Insert data into featured_graffiti_info
						pstmt4.setString(1, graffiti_id);
						pstmt4.setString(2, commentary.replaceAll("\n", "<br/>"));
						pstmt4.setString(3, image);
						pstmt4.executeUpdate();

						// Set Inscription as themed
						pstmt2.setString(1, graffiti_id);
						pstmt2.executeUpdate();

						// Iterate through themes and update the database
						for (int id : themeIds) {

							// Insert into graffititothemes table
							pstmt.setString(1, (String) graffiti_id);
							pstmt.setInt(2, id);
							pstmt.executeUpdate();

							// Sets is_figural_featured to true in the database for the
							// given graffiti_id if figural is the theme
							if (id == figID) {
								pstmt5.setString(1, graffiti_id);
								pstmt5.executeUpdate();
							}
						}
					} // else {System.out.println("It failed");} //Used for testing purposes
				}

			}

			// Close statements, connections, and file readers
			pstmt.close();
			pstmt2.close();
			pstmt3.close();
			pstmt4.close();
			pstmt5.close();
			in.close();
		} catch (IOException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public void run() {
		insertFeaturedGraffiti();
	}

}
