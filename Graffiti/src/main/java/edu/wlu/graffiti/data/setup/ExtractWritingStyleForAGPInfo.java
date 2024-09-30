/**
 * 
 */

package edu.wlu.graffiti.data.setup;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import edu.wlu.graffiti.controller.GraffitiController;
import edu.wlu.graffiti.data.main.DBInteraction;

/**
 * This class extracts the writing_style from the EDR inscriptions and translate
 * the Italian writing style into English for AGP. Can be called as standalone
 * application or from another script. Reads database information from the
 * configuration.properties file.
 * 
 * @author sprenkle
 * 
 */
public class ExtractWritingStyleForAGPInfo extends DBInteraction {

	public static final String CHARCOAL = "charcoal";

	public static final String GRAFFITO_INCISED = GraffitiController.WRITING_STYLE_GRAFFITI_INSCRIBED;

	private static final String UPDATE_ANNOTATION_STMT = "UPDATE inscriptions "
			+ "SET writing_style_in_english = ? WHERE graffiti_id=?";

	final static String SELECT_GRAFFITI = "SELECT writing_style, graffiti_id FROM inscriptions";

	private static PreparedStatement updateWritingStyleStmt;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ExtractWritingStyleForAGPInfo extractor = new ExtractWritingStyleForAGPInfo();
		extractor.runDBInteractions();
	}

	@Override
	public void run() {
		updateWritingStyle();
	}

	public void updateWritingStyle() {

		try {
			updateWritingStyleStmt = dbCon.prepareStatement(UPDATE_ANNOTATION_STMT);

			PreparedStatement extractData = dbCon.prepareStatement(SELECT_GRAFFITI);

			ResultSet rs = extractData.executeQuery();

			while (rs.next()) {
				String writingStyle = rs.getString("writing_style");
				String graffiti_id = rs.getString("graffiti_id");

				String translatedWritingStyle = translateWritingStyle(writingStyle);

				updateAnnotation(graffiti_id, translatedWritingStyle);

			}

			rs.close();
			extractData.close();
			updateWritingStyleStmt.close();
			dbCon.close();

		} catch (SQLException e1) {
			e1.printStackTrace();
		}
	}

	public static String translateWritingStyle(String writingStyle) {
		String translatedWritingStyle = writingStyle;
		// make this into a switch statement?
		if (writingStyle.toLowerCase().startsWith("litt. scariph")) {
			translatedWritingStyle = GRAFFITO_INCISED;
		} else if (writingStyle.equalsIgnoreCase("cetera/carbone")) {
			translatedWritingStyle = CHARCOAL;
		} else if (writingStyle.equalsIgnoreCase("carbone")) {
			translatedWritingStyle = CHARCOAL;
		} else if (writingStyle.toLowerCase().startsWith("cetera, carbone")) {
			translatedWritingStyle = CHARCOAL;
		} else if (writingStyle.toLowerCase().startsWith("cetera carbone")) {
			translatedWritingStyle = CHARCOAL;
		} else if (writingStyle.startsWith("cetera; lapide rubro")) {
			translatedWritingStyle = "other; 'red rock'";
		} else if (writingStyle.startsWith("cetera lapide rubro")) {
			translatedWritingStyle = "other; 'red rock'";
		} else if (writingStyle.startsWith("cetera; rubrica")) {
			translatedWritingStyle = "other; 'red substance'";
		} else if (writingStyle.startsWith("scalpro")) {
			translatedWritingStyle = "chisel";
		} else if (writingStyle.equalsIgnoreCase("pictura")) {
			translatedWritingStyle = "painted";
		}
		else {
			translatedWritingStyle = writingStyle;
		}
		return translatedWritingStyle;
	}

	/**
	 * 
	 * 
	 * @param graffiti_id
	 * @param translatedStyle TODO
	 */
	private static void updateAnnotation(String graffiti_id, String translatedStyle) {
		try {
			updateWritingStyleStmt.setString(1, translatedStyle);
			updateWritingStyleStmt.setString(2, graffiti_id);
			updateWritingStyleStmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
