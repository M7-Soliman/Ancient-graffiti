package edu.wlu.graffiti.data.setup;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import edu.wlu.graffiti.data.main.DBInteraction;

public class StoreAllExistingEDRNumbers extends DBInteraction {

	private static final String INSERT_EXISTING_EDR_ID = "INSERT INTO existing_edr_ids (edr_id) VALUES (?)";

	private static final String[] files = { "data/EDRData/epigr.csv", "data/AGPData/camodeca_epigr.csv" };

	private static PreparedStatement existingPstmt;

	public static void main(String[] args) {
		StoreAllExistingEDRNumbers storer = new StoreAllExistingEDRNumbers();
		storer.runDBInteractions();
	}

	@Override
	public void run() {
		storeEDRNumbersFromSpreadsheets();
	}
	
	public void storeEDRNumbersFromSpreadsheets() {
		try {
			existingPstmt = dbCon.prepareStatement(INSERT_EXISTING_EDR_ID);
			for (String file : files) {
				Reader in = new FileReader(file);
				Iterable<CSVRecord> records = CSVFormat.EXCEL.parse(in);
				for (CSVRecord record : records) {
					String edrID = Utils.cleanData(record.get(0));
					existingPstmt.setString(1, edrID);
					existingPstmt.executeUpdate();
				}
			}
			existingPstmt.close();
		} catch (SQLException | IOException e) {
			e.printStackTrace();
		}
	}

}
