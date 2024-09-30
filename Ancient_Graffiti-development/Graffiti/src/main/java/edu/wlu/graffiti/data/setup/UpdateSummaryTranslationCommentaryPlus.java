package edu.wlu.graffiti.data.setup;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import edu.wlu.graffiti.data.main.DBInteraction;

/**
 * Updates caption, translation (of text), commentary, and langner reference
 * from spreadsheet into database. For textual graffiti.
 * 
 * @author Sara Sprenkle
 *
 */
public class UpdateSummaryTranslationCommentaryPlus extends DBInteraction {

	private static final String HERCULANEUM_SUMMARY_LOC = "data/AGPData/herc_summary.csv";
	private static final String POMPEII_SUMMARY_LOC = "data/AGPData/pompeii_summary.csv";

	private static final String GET_CIL = "SELECT cil FROM inscriptions WHERE graffiti_id=?";

	private static final String UPDATE_ANNOTATION_STMT = "UPDATE inscriptions "
			+ "SET caption = ?, content_translation = ?, commentary = ?, langner = ?, "
			+ "contributors = ?, update_of_cil = ?, cil = ? WHERE graffiti_id = ? ";

	// public static final String UPDATE_PREFERRED_IMAGE = "UPDATE
	// featured_graffiti_info SET preferred_image = ? WHERE graffiti_id=?";

	public static void main(String[] args) {
		System.out.println("Updating Translations, Commentary, etc...");
		UpdateSummaryTranslationCommentaryPlus updater = new UpdateSummaryTranslationCommentaryPlus();
		updater.runDBInteractions();
		System.out.println("Done");
	}

	@Override
	public void run() {
		updateAllInscriptions();
	}

	public void updateAllInscriptions() {
		updateInscriptions(HERCULANEUM_SUMMARY_LOC);
		updateInscriptions(POMPEII_SUMMARY_LOC);
	}

	/**
	 * 
	 * @param datafileName CSV file containing the info in the required format
	 */
	private void updateInscriptions(String datafileName) {
		try {
			PreparedStatement pstmt = dbCon.prepareStatement(UPDATE_ANNOTATION_STMT);

			Reader in = null;
			Iterable<CSVRecord> records;
			try {
				in = new FileReader(datafileName);
				records = CSVFormat.EXCEL.parse(in);
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
				return;
			} catch (IOException e1) {
				e1.printStackTrace();
				return;
			}

			for (CSVRecord record : records) {
				String graffiti_id = Utils.cleanData(record.get(0));

				if (graffiti_id.equals(""))
					continue;

				// Get the CIL in the database
				PreparedStatement getCIL = dbCon.prepareStatement(GET_CIL);
				getCIL.setString(1, graffiti_id);
				ResultSet rs = getCIL.executeQuery();
				String cil_in_database = "";
				if (rs.next()) {
					cil_in_database = rs.getString("cil");
				}

				String cil = Utils.cleanData(record.get(1));
				String langner = Utils.cleanData(record.get(3));
				String summary = Utils.cleanData(record.get(6));
				String translation = Utils.cleanData(record.get(7));
				String commentary = Utils.cleanData(record.get(8));
				String update_of_cil = Utils.cleanData(record.get(9));
				String contributors = Utils.cleanData(record.get(10));

				if (!cil_in_database.equals("")) {
					cil = cil_in_database;
				}

				boolean update;
				if (update_of_cil.toLowerCase().equals("true") || update_of_cil.toLowerCase().equals("t")) {
					update = true;
				} else {
					update = false;
				}

				pstmt.setString(1, summary);
				pstmt.setString(2, translation);
				pstmt.setString(3, commentary);
				pstmt.setString(4, langner);
				pstmt.setString(5, contributors);
				pstmt.setBoolean(6, update);
				pstmt.setString(7, cil);
				pstmt.setString(8, graffiti_id);
				try {
					pstmt.executeUpdate();
				} catch (SQLException e) {
					System.out.println("Error on: " + graffiti_id);
					e.printStackTrace();
				}

			}

			pstmt.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
