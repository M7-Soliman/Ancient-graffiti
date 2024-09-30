package edu.wlu.graffiti.data.setup;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

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

public class InsertMoreFeaturedGraffiti extends DBInteraction {

	private static final int GRAFFITI_ID_LOCATION_IN_CSV = 0;
	private static final int CATEGORY_LOCATION_IN_CSV = 1;
	private static final int TRANSLATION_COMMENTARY_LOCATION_IN_CSV = 2;
	private static final int CONTENT_IMAGE_LOCATION_IN_CSV = 3;
	private static final int CIL_LOCATION_IN_CSV = 4;
	private static final int COMMENTARY_LOCATION_IN_CSV = 5;
	private static final int IMAGE_LOCATION_IN_CSV = 6;

	private static final String INSERT_FEATURED_GRAFFITI = "INSERT INTO more_graffititothemes "
			+ "(graffito_id, theme_id) " + "VALUES (?, ?)";

	private static final String GET_THEME_ID = "SELECT theme_id FROM themes WHERE name = ?";

	private static final String INSERT_FEATURED_GRAFFITI_INFO = "INSERT INTO more_featured_graffiti_info "
			+ "(graffiti_id, content, translation, cil) " + "VALUES (?, ?, ?, ?)";

	private static final String INSERT_FEATURED_FIGURAL_GRAFFITI_INFO = "INSERT INTO more_featured_graffiti_info "
			+ "(graffiti_id, image, commentary, cil) " + "VALUES (?, ?, ?, ?)";

	public static final String UPDATE_GH_INFO = "UPDATE featured_graffiti_info SET commentary = ?, preferred_image = ? WHERE graffiti_id=?";
	public static final String SELECT_GH_INFO = "SELECT * from featured_graffiti_info where graffiti_id = ?";
	public static final String INSERT_GH_INFO = "INSERT INTO featured_graffiti_info VALUES (?, ?, ?)";

	public static void main(String[] args) {
		InsertMoreFeaturedGraffiti inserter = new InsertMoreFeaturedGraffiti();
		inserter.runDBInteractions();
	}
	
	@Override
	public void run() {
		insertFeaturedGraffiti();
	}

	public void insertFeaturedGraffiti() {

		try {

			// Create Prepared Statements
			PreparedStatement pstmt = dbCon.prepareStatement(INSERT_FEATURED_GRAFFITI);
			PreparedStatement pstmt3 = dbCon.prepareStatement(GET_THEME_ID);
			PreparedStatement pstmt4 = dbCon.prepareStatement(INSERT_FEATURED_GRAFFITI_INFO);
			PreparedStatement pstmt5 = dbCon.prepareStatement(INSERT_FEATURED_FIGURAL_GRAFFITI_INFO);

			// Read in the data from the CSV File
			Reader in = new FileReader("data/AGPData/more_featured_graffiti.csv");
			Iterable<CSVRecord> records = CSVFormat.EXCEL.parse(in);
			for (CSVRecord record : records) {

				// Clean data in CSV File and save to Strings
				String graffiti_id = Utils.cleanData(record.get(GRAFFITI_ID_LOCATION_IN_CSV));
				String theme = Utils.cleanData(record.get(CATEGORY_LOCATION_IN_CSV));
				String content = Utils.cleanData(record.get(CONTENT_IMAGE_LOCATION_IN_CSV));
				String translation = Utils.cleanData(record.get(TRANSLATION_COMMENTARY_LOCATION_IN_CSV));
				String cil = Utils.cleanData(record.get(CIL_LOCATION_IN_CSV));

				if (!graffiti_id.equals("")) {

					List<Integer> themeIds = new LinkedList<Integer>();
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
						if (theme.toLowerCase().equals("figural")) {
							pstmt5.setString(1, graffiti_id);
							pstmt5.setString(2, content);
							pstmt5.setString(3, translation);
							pstmt5.setString(4, cil);
							pstmt5.executeUpdate();
						} else {
							pstmt4.setString(1, graffiti_id);
							pstmt4.setString(2, content);
							pstmt4.setString(3, translation);
							pstmt4.setString(4, cil);
							pstmt4.executeUpdate();
						}

						try {
							// Iterate through themes and update the database
							for (int id : themeIds) {
								// Insert into more_graffititothemes table
								pstmt.setString(1, (String) graffiti_id);
								pstmt.setInt(2, id);
								pstmt.executeUpdate();
							}
						} catch (SQLException e) {
							System.out.println("Likely duplicate theme; continue...");
							e.printStackTrace();
						}
					} // else {System.out.println("It failed");} //Used for testing purposes
				}
			}
			// Close statements, connections, and file readers
			pstmt.close();
			pstmt3.close();
			pstmt4.close();
			in.close();
		} catch (IOException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
