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
 * 
 * Reads column information from a spreadsheet and inserts it into the database
 * Maps Roman numerals to decimal numbers.
 * 
 * @author Trevor Stalnaker
 * 
 */
public class InsertColumns extends DBInteraction {

	public static final String COLUMNS_DATA_CSV = "data/AGPData/columns.csv";

	private static String INSERT_COLUMNS = "INSERT INTO columns (roman_numeral, decimal_number) VALUES (?,?)";

	public static void main(String[] args) {
		InsertColumns inserter = new InsertColumns();
		inserter.runDBInteractions();
	}

	public void insertColumns() {
		try {
			// Create Prepared Statements
			PreparedStatement pstmt = dbCon.prepareStatement(INSERT_COLUMNS);

			// Read in the data from the CSV File
			Reader in = new FileReader(COLUMNS_DATA_CSV);
			Iterable<CSVRecord> records = CSVFormat.EXCEL.parse(in);
			for (CSVRecord record : records) {
				// Clean data in CSV File and save to Strings
				String roman_numeral = Utils.cleanData(record.get(0));
				int decimal = Integer.parseInt(Utils.cleanData(record.get(1)));
				pstmt.setString(1, roman_numeral);
				pstmt.setInt(2, decimal);
				pstmt.executeUpdate();
			}
			// Close statements, connections, and file readers
			pstmt.close();
			in.close();
		} catch (IOException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		insertColumns();
		System.out.println("Columns Imported");
	}
}
