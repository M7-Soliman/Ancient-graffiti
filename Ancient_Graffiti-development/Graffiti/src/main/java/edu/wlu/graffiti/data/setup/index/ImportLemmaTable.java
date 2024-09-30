package edu.wlu.graffiti.data.setup.index;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import edu.wlu.graffiti.data.main.DBInteraction;
import edu.wlu.graffiti.data.setup.Utils;

/**
 * Class that imports the lemma table used for indexing
 * 
 * @author Trevor Stalnaker
 * @author Grace MacDonald
 * 
 */
public class ImportLemmaTable extends DBInteraction {
	private static final String INSERT_LEMMA = "INSERT INTO lemma (term, lemma) VALUES (?, ?)";

	private static final String AMMEND_LEMMA = "UPDATE lemma SET lemma=? WHERE term=?";

	private static final String CHECK_FOR_LEMMA = "SELECT lemma FROM lemma WHERE term=?";

	public static void main(String[] args) {
		ImportLemmaTable lt = new ImportLemmaTable();

		lt.runDBInteractions();
		// importLemma();
		// ammendLemma("Ammending Lemma Table...", "Lemma Table Successfully
		// Ammended...", "lemma_ammendments");
		// ammendLemma("Adding Names to Lemma Table...", "Names Successfully Added...",
		// "lemma_names");
		// ammendLemma("Adding Places to Lemma Table...", "Places Successfully
		// Added...", "lemma_places");
	}

	@Override
	public void run() {
		importLemma();
		// ammendLemma("Ammending Lemma Table...", "Lemma Table Successfully
		// Ammended...", "lemma_ammendments");
		ammendLemma("Adding Names to Lemma Table...", "Names Successfully Added...", "lemma_names");
		ammendLemma("Adding Places to Lemma Table...", "Places Successfully Added...", "lemma_places");
	}

	private void importLemma() {

		try {

			System.out.println("Importing Lemma Table...");

			// Create Prepared Statements
			PreparedStatement pstmt = dbCon.prepareStatement(INSERT_LEMMA);

			// Read in the data from the CSV File
			Reader in = new FileReader("data/AGPData/lemma.csv");
			Iterable<CSVRecord> records = CSVFormat.EXCEL.parse(in);
			for (CSVRecord record : records) {
				// Clean data in CSV File and save to Strings
				String term = Utils.cleanData(record.get(0));
				String lemma = Utils.cleanData(record.get(1));

				if (!term.equals("term")) {
					pstmt.setString(1, term);
					pstmt.setString(2, lemma);
					pstmt.executeUpdate();
				}
			}

			System.out.println("Lemma Table Successfully Imported...");

			// Close statements, connections, and file readers
			pstmt.close();
			in.close();
			// dbCon.close();

		} catch (IOException | SQLException e) {
			e.printStackTrace();
		}
	}

	private void ammendLemma(String start_message, String end_message, String fileName) {
		try {

			System.out.println(start_message);

			// Create Prepared Statements
			PreparedStatement pstmt = dbCon.prepareStatement(AMMEND_LEMMA);
			PreparedStatement chk_stmt = dbCon.prepareStatement(CHECK_FOR_LEMMA);
			PreparedStatement pstmt_insert = dbCon.prepareStatement(INSERT_LEMMA);

			// Read in the data from the CSV File
			Reader in = new FileReader("data/AGPData/" + fileName + ".csv");
			Iterable<CSVRecord> records = CSVFormat.EXCEL.parse(in);
			for (CSVRecord record : records) {
				// Clean data in CSV File and save to Strings
				String term = Utils.cleanData(record.get(0));
				String lemma = Utils.cleanData(record.get(1));

				if (!term.equals("term")) {
					chk_stmt.setString(1, term);
					ResultSet rs = chk_stmt.executeQuery();
					if (rs.next()) {
						pstmt.setString(1, lemma);
						pstmt.setString(2, term);
						pstmt.executeUpdate();
					} else {
						pstmt_insert.setString(1, term);
						pstmt_insert.setString(2, lemma);
						pstmt_insert.executeUpdate();
					}
					rs.close();
				}

			}

			System.out.println(end_message);

			// Close statements, connections, and file readers
			pstmt.close();
			pstmt_insert.close();
			in.close();
			// dbCon.close();

		} catch (IOException | SQLException e) {
			e.printStackTrace();
		}
	}

}
