/**
 * 
 */

package edu.wlu.graffiti.data.setup;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import edu.wlu.graffiti.data.main.DBInteraction;

/**
 * This class extract the writing_style from the EDR inscription database table
 * and translate the Italian writing style into English for the AGP Info. Reads
 * database information from the configuration.properties file. Can be called as
 * a standalone script or from another script.
 * 
 * @author Sara Sprenkle
 * @author Trevor Stalnaker
 * 
 */
public class ExtractEDRLanguageForAGPInfo extends DBInteraction {

	private static final String UPDATE_LANG_IN_ENGLISH = "UPDATE inscriptions SET lang_in_english = ? "
			+ "WHERE language= ?";

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ExtractEDRLanguageForAGPInfo extractor = new ExtractEDRLanguageForAGPInfo();
		extractor.runDBInteractions();
	}

	@Override
	public void run() {
		updateAGPLanguage();
	}

	public void updateAGPLanguage() {

		try {
			PreparedStatement updateLang = dbCon.prepareStatement(UPDATE_LANG_IN_ENGLISH);
			updateLang.setString(1, "Latin");
			updateLang.setString(2, "latina");
			updateLang.executeUpdate();
			updateLang.setString(1, "Greek");
			updateLang.setString(2, "graeca");
			updateLang.executeUpdate();
			updateLang.setString(1, "Latin/Greek");
			updateLang.setString(2, "latina-graeca");
			updateLang.executeUpdate();
			updateLang.setString(1, "other");
			updateLang.setString(2, "alia");
			updateLang.executeUpdate();
			updateLang.close();
			dbCon.close();

		} catch (SQLException e1) {
			e1.printStackTrace();
		}
	}

}
