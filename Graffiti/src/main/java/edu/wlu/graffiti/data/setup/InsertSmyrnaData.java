package edu.wlu.graffiti.data.setup;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import edu.wlu.graffiti.data.main.DBInteraction;

/**
 * Class that imports supplemental Smyrna Data
 * 
 * @author Trevor Stalnaker
 */
public class InsertSmyrnaData extends DBInteraction {

	public static final String SMYRNA_DATA_FILE = "data/AGPData/smyrna_summary.csv";
	// Things we want to add: drawing category, caption, latin desc, english desc
	private static final String SET_CAPTION = "UPDATE inscriptions SET caption=? WHERE graffiti_id=?";
	private static final String SET_FIG_COMP = "UPDATE inscriptions SET has_figural_component = true WHERE graffiti_id=?";
	private static final String SET_FIG_INFO = "INSERT INTO figural_graffiti_info (description_in_latin, description_in_english, "
			+ "graffiti_id) VALUES (?, ?, ?)";
	private static final String UPDATE_FIG_CONTENT = "UPDATE inscriptions SET content=? WHERE graffiti_id=?";

	private static final String SET_WRITING_STYLE = "UPDATE inscriptions SET writing_style=?, writing_style_in_english=? WHERE "
			+ " graffiti_id=?";

	private static final String SET_DRAWING_TAG = "INSERT INTO graffitotodrawingtags (graffito_id, drawing_tag_id) VALUES (?,?)";
	private static final String GET_DRAWING_TAG_ID = "SELECT id FROM drawing_tags WHERE name=?";
	private static final String SET_INSC_INFO = "UPDATE inscriptions SET editor=?, principle_contributors=?, last_revision =? "
			+ "WHERE graffiti_id=?";

	private static final String GET_SMYRNA_IDS = "SELECT graffiti_id FROM inscriptions WHERE graffiti_id LIKE 'SMY%'";
	private static final String REMOVE_SMYRNA_TAG_MAPPING = "DELETE FROM graffitotodrawingtags WHERE graffito_id LIKE 'SMY%'";
	private static final String REMOVE_DRAWING_INFO = "DELETE FROM figural_graffiti_info WHERE graffiti_id LIKE 'SMY%'";

	private static final int STANDARD_ID_LENGTH = 9;

	// Set Editor
	private static String editor = "Roger S. Bagnall";
	private static String contributor = "Roger S. Bagnall";
	private static String lastRevision = "2016-10-03";

	public static void main(String[] args) {
		InsertSmyrnaData inserter = new InsertSmyrnaData();
		inserter.runDBInteractions();
	}
	
	@Override
	public void run() {
		insertSmyrnaData();

	}

	public void insertSmyrnaData() {

		// A list for inscriptions that are successfully updated
		List<String> updated = new ArrayList<String>();

		// A list for inscriptions that do not have IDs of 9
		List<String> nonStdLen = new ArrayList<String>();

		// Get a list of all of the Smyrna Inscriptions currently in the database
		List<String> smyrnaInscriptions = new ArrayList<String>();

		// Count of inscriptions not in the database
		int noDatCount = 0;

		// Count of inscriptions in the database but not the spreadsheet
		int noSpreadCount = 0;

		System.out.println("Importing Supplemental Smyrna Data...");

		try {

			PreparedStatement checkstmt = dbCon.prepareStatement(GET_SMYRNA_IDS);
			ResultSet checkrs = checkstmt.executeQuery();
			while (checkrs.next()) {
				smyrnaInscriptions.add(checkrs.getString("graffiti_id"));
			}
			System.out.println(smyrnaInscriptions.size() + " Smyrna Inscriptions Currently in Database");
			checkstmt.close();

			// Create Prepared Statements
			PreparedStatement setCaptionStmt = dbCon.prepareStatement(SET_CAPTION);
			PreparedStatement setInfoStmt = dbCon.prepareStatement(SET_INSC_INFO);
			PreparedStatement getDrawingTagStmt = dbCon.prepareStatement(GET_DRAWING_TAG_ID);
			PreparedStatement rmstmt = dbCon.prepareStatement(REMOVE_SMYRNA_TAG_MAPPING);
			PreparedStatement setDrawingTagStmt = dbCon.prepareStatement(SET_DRAWING_TAG);
			PreparedStatement figInfoStmt = dbCon.prepareStatement(SET_FIG_INFO);
			PreparedStatement figCompStmt = dbCon.prepareStatement(SET_FIG_COMP);
			PreparedStatement removeDrawingInfoStmt = dbCon.prepareStatement(REMOVE_DRAWING_INFO);
			PreparedStatement updateFiguralContentStmt = dbCon.prepareStatement(UPDATE_FIG_CONTENT);
			PreparedStatement setWritingStyleStmt = dbCon.prepareStatement(SET_WRITING_STYLE);

			// Clear Smyrna entries from graffiti to drawing tag mapping to prevent
			// duplicates
			rmstmt.executeUpdate();
			removeDrawingInfoStmt.executeUpdate();

			// Read in the data from the CSV File
			Reader in = new FileReader(SMYRNA_DATA_FILE);
			Iterable<CSVRecord> records = CSVFormat.EXCEL.parse(in);
			for (CSVRecord record : records) {
				// Clean data in CSV File and save to Strings
				String graffiti_id = Utils.cleanData(record.get(0));
				String drawingCat = Utils.cleanData(record.get(9));
				String caption = Utils.cleanData(record.get(10));

				// Data from Spreadsheet used for debugging
				// String bookId = Utils.cleanData(record.get(1));

				String latDesc = Utils.cleanData(record.get(11));
				String engDesc = Utils.cleanData(record.get(12));
				engDesc = engDesc.replaceAll("\\(\\(:", "").replaceAll("\\)\\)", "");

				String writing_style = Utils.cleanData(record.get(13));
				String writing_in_english = ExtractWritingStyleForAGPInfo.translateWritingStyle(writing_style);

				// Check for naming errors in the Spreadsheet
				if (graffiti_id.length() != STANDARD_ID_LENGTH) {
					nonStdLen.add(graffiti_id);
					System.err.println("NonStandard ID: " + graffiti_id);
				}
				// If the inscription is in the database
				else if (smyrnaInscriptions.contains(graffiti_id)) {

					// Make sure their is a caption to update
					if (!caption.equals("")) {
						setCaptionStmt.setString(1, caption);
						setCaptionStmt.setString(2, graffiti_id);
						setCaptionStmt.executeUpdate();
					}

					setInfoStmt.setString(1, editor);
					setInfoStmt.setString(2, contributor);
					setInfoStmt.setString(3, lastRevision);
					setInfoStmt.setString(4, graffiti_id);
					setInfoStmt.executeUpdate();

					setWritingStyleStmt.setString(1, writing_style);
					setWritingStyleStmt.setString(2, writing_in_english);
					setWritingStyleStmt.setString(3, graffiti_id);
					setWritingStyleStmt.executeUpdate();

					if (!drawingCat.equals("")) {

						// Set has_figural_component to true
						figCompStmt.setString(1, graffiti_id);
						figCompStmt.executeUpdate();

						// Split if their are multiple categories
						String[] cats = drawingCat.split(";");
						for (String item : cats) {
							String cat = Utils.standardizeDrawingNames(item.trim());
							// Find the drawing_tag_id for the given record
							getDrawingTagStmt.setString(1, cat);
							ResultSet rs = getDrawingTagStmt.executeQuery();

							// Add theme_ids to themeIds if they exist and are not already present in list
							int tagId = 0;
							while (rs.next()) {
								tagId = rs.getInt("id");

							}
							setDrawingTagStmt.setString(1, graffiti_id);
							setDrawingTagStmt.setInt(2, tagId);
							setDrawingTagStmt.executeUpdate();
						}
					}

					if (!latDesc.equals("")) {
						figInfoStmt.setString(1, latDesc);
						figInfoStmt.setString(2, engDesc);
						figInfoStmt.setString(3, graffiti_id);
						figInfoStmt.executeUpdate();
						updateFiguralContentStmt.setString(1, latDesc);
						updateFiguralContentStmt.setString(2, graffiti_id);
						updateFiguralContentStmt.executeUpdate();
					}

					updated.add(graffiti_id);
				} else {
					System.out.println("Unknown ID: " + graffiti_id);
					// System.out.println(bookId);
					noDatCount += 1;
				}
			}

			// Determine inscriptions that are in the database but not spreadsheet
			for (String id : smyrnaInscriptions) {
				if (!updated.contains(id)) {
					System.out.println("Not Updated: " + id);
					noSpreadCount += 1;
				}
			}

			// Print report
			System.out.println(
					updated.size() + " of " + smyrnaInscriptions.size() + " Inscriptions Successfully Updated");
			System.out.println("Inscriptions with IDs of Non-Standard Length: " + nonStdLen.size());
			System.out.println("Inscriptions Not in Database: " + noDatCount);
			System.out.println("Inscriptions Not in Spreadsheet: " + noSpreadCount);

			// Close statements, connections, and file readers
			setCaptionStmt.close();
			setInfoStmt.close();
			getDrawingTagStmt.close();
			setDrawingTagStmt.close();
			figInfoStmt.close();
			figCompStmt.close();
			setWritingStyleStmt.close();
			rmstmt.close();
			in.close();
			dbCon.close();

		} catch (IOException | SQLException e) {
			e.printStackTrace();
		}

	}

}
