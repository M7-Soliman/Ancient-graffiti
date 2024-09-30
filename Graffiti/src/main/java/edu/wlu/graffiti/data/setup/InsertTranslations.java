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
 * Imports data from the translation_graffiti.csv file into the database.
 * 
 * @author Trevor Stalnaker
 * 
 */

public class InsertTranslations extends DBInteraction {
	
	private static final String SET_TRANSLATION_featured = "UPDATE inscriptions SET is_featured_translation=true WHERE graffiti_id=?";


	public static void main(String[] args) {
		InsertTranslations inserter = new InsertTranslations();
		inserter.runDBInteractions();
	}
	
	@Override
	public void run() {
		insertTranslationGraffiti();
	}

	public void insertTranslationGraffiti() {
		
		try {
			PreparedStatement pstmt = dbCon.prepareStatement(SET_TRANSLATION_featured);

			//Read in the data from the CSV File
			Reader in = new FileReader("data/AGPData/translation_graffiti.csv");
			Iterable<CSVRecord> records = CSVFormat.EXCEL.parse(in);
			for (CSVRecord record : records) {
				//Clean data in CSV File and save to Strings
				String graffiti_id = Utils.cleanData(record.get(0));
				pstmt.setString(1, graffiti_id);
				pstmt.executeUpdate();
			}
			pstmt.close();
			in.close();
		} catch (IOException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
