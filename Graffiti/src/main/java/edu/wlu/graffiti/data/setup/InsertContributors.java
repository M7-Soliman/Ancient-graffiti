package edu.wlu.graffiti.data.setup;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import edu.wlu.graffiti.data.main.DBInteraction;

/**
 * Imports data from the ammini.csv file into the database. The ammini.csv file
 * contains information about contributors and the last revisions for graffiti.
 * 
 * @author Trevor Stalnaker
 * 
 */
public class InsertContributors extends DBInteraction {

	private static final String SET_PRINCIPLE_CONTRIBUTORS = "UPDATE inscriptions SET principle_contributors=? WHERE graffiti_id=?";
	private static final String SET_LAST_REVISION = "UPDATE inscriptions SET last_revision=? WHERE graffiti_id=?";
	private static final String SET_EDITOR = "UPDATE inscriptions SET EDITOR=? WHERE graffiti_id=?";

	public static final String EDITOR = "Rebecca Benefiel";

	private static HashMap<String, String> namesMap = new HashMap<String, String>();

	public static void main(String[] args) {
		InsertContributors inserter = new InsertContributors();
		inserter.runDBInteractions();
	}

	public void insertContributors() {

		try {
			// Create Prepared Statements
			PreparedStatement pstmt = dbCon.prepareStatement(SET_PRINCIPLE_CONTRIBUTORS);
			PreparedStatement pstmt2 = dbCon.prepareStatement(SET_LAST_REVISION);
			PreparedStatement pstmt3 = dbCon.prepareStatement(SET_EDITOR);

			// Read in the data from the CSV File
			Reader in = new FileReader("data/EDRData/ammini.csv");
			Iterable<CSVRecord> records = CSVFormat.EXCEL.parse(in);
			for (CSVRecord record : records) {
				// Clean data in CSV File and save to Strings
				String graffiti_id = Utils.cleanData(record.get(0));
				String primaryContributor1 = Utils.cleanData(record.get(1));
				String primaryContributor2 = Utils.cleanData(record.get(2));
				String primaryContributor3 = Utils.cleanData(record.get(3));
				// until we decide to use this...
				// String creationDate = Utils.cleanData(record.get(4));
				String lastModifiedDate = Utils.cleanData(record.get(7));

				// Normalize and combine the names of contributors with commas
				String names = "";
				String[] nameSplit = primaryContributor1.split(" ");
				for (int i = 0; i < nameSplit.length; i++) {
					String name = nameSplit[i];
					if (namesMap.containsKey(name)) {
						names += namesMap.get(name);
					} else {
						names += String.valueOf(name.charAt(0)).toUpperCase() + name.substring(1).toLowerCase();
					}
					if (i < nameSplit.length - 1) {
						names += " ";
					}
				}
				if (!primaryContributor2.equals("")) {
					nameSplit = primaryContributor2.split(" ");
					names += ", ";
					for (int i = 0; i < nameSplit.length; i++) {
						String name = nameSplit[i];
						if (namesMap.containsKey(name)) {
							names += namesMap.get(name);
						} else {
							names += String.valueOf(name.charAt(0)).toUpperCase() + name.substring(1).toLowerCase();
						}
						if (i < nameSplit.length - 1) {
							names += " ";
						}
					}
				}
				if (!primaryContributor3.equals("")) {
					nameSplit = primaryContributor3.split(" ");
					names += ", ";
					for (int i = 0; i < nameSplit.length; i++) {
						String name = nameSplit[i];
						if (namesMap.containsKey(name)) {
							names += namesMap.get(name);
						} else {
							names += String.valueOf(name.charAt(0)).toUpperCase() + name.substring(1).toLowerCase();
						}
						if (i < nameSplit.length - 1) {
							names += " ";
						}
					}
				}

				// Format Date
				String date = "";
				Pattern pattern = Pattern.compile("(20[0-9]{1,2})-([0-9]{1,2})-([0-9]{1,4})");
				Matcher matcher = pattern.matcher(lastModifiedDate);
				if (matcher.find()) {
					date = matcher.group(1) + "-";
					if (matcher.group(1).length() < 2) {
						date += "0";
					}
					date += matcher.group(2) + "-";
					if (matcher.group(2).length() < 2) {
						date += "0";
					}
					date += matcher.group(3);
				}

				pstmt.setString(1, names);
				pstmt.setString(2, graffiti_id);
				pstmt.executeUpdate();

				pstmt2.setString(1, date);
				pstmt2.setString(2, graffiti_id);
				pstmt2.executeUpdate();

				pstmt3.setString(1, EDITOR);
				pstmt3.setString(2, graffiti_id);
				pstmt3.executeUpdate();

			}
			// Close statements, connections, and file readers
			pstmt.close();
			pstmt2.close();
			pstmt3.close();
			in.close();
		} catch (IOException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Build map to translate unusual names to proper formatting
	 */
	private static void buildMap() {
		String[] namesInFile = { "DIBIASIE" };
		String[] correctFormatting = { "DiBiasie" };
		for (int i = 0; i < namesInFile.length; i++) {
			namesMap.put(namesInFile[i], correctFormatting[i]);
		}
	}

	@Override
	public void run() {
		buildMap();
		insertContributors();
		System.out.println("EDR contributors import done");
	}

}
