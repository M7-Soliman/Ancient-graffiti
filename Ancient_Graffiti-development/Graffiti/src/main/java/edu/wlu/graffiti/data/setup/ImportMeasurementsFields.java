package edu.wlu.graffiti.data.setup;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import edu.wlu.graffiti.data.main.DBInteraction;

/**
 * Class to update the measurements field that I had screwed up.
 * 
 * @author Sara Sprenkle
 *
 */
public class ImportMeasurementsFields extends DBInteraction {

	private static final String UPDATE_INSCRIPTION_STATEMENT = "UPDATE inscriptions SET "
			+ "height_from_ground=?, graffito_height=?, graffito_length=?, letter_height_min=?, letter_height_max=?, letter_with_flourishes_height_min=?, letter_with_flourishes_height_max=?  WHERE graffiti_id = ?";

	private static PreparedStatement updateMeasurements;

	public static void main(String[] args) {
		ImportMeasurementsFields importer = new ImportMeasurementsFields();
		importer.runDBInteractions();
	}

	@Override
	public void run() {
		String datafileName = "data/agp_measurements.csv";

		try {
			updateMeasurements = dbCon.prepareStatement(UPDATE_INSCRIPTION_STATEMENT);
			updateMeasurements(datafileName);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void updateMeasurements(String datafileName) throws SQLException {
		Reader in = null;
		Iterable<CSVRecord> records;
		try {
			in = new FileReader(datafileName);
			records = CSVFormat.EXCEL.parse(in);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return;
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return;
		}

		records.iterator().next(); // skip the first row?

		Set<String> cils = new HashSet<>();

		for (CSVRecord record : records) {
			String cil = Utils.cleanData(record.get(0));

			String graffiti_id = Utils.cleanData(record.get(1));
			if (graffiti_id.equals("") || graffiti_id.equals("0")) {
				continue;
			}
			graffiti_id = "EDR" + graffiti_id;
			String height_from_ground = Utils.cleanData(record.get(6));
			String graffito_height = Utils.cleanData(record.get(2));
			String graffito_length = Utils.cleanData(record.get(3));
			String letterRange = Utils.cleanData(record.get(4));
			String letterRangeWithFlourishes = Utils.cleanData(record.get(5));
			String letterMin = "", letterMax = "";
			String letterFlMin = "", letterFlMax = "";
			if (letterRange.contains("-")) {
				String[] letterComponents = letterRange.split("-");
				letterMin = letterComponents[0];
				letterMax = letterComponents[1];
			}
			if (letterRangeWithFlourishes.contains("-")) {
				String[] letterComponents = letterRangeWithFlourishes.split("-");
				letterFlMin = letterComponents[0];
				letterFlMax = letterComponents[1];
			}
			updateMeasurements.setString(1, height_from_ground);
			updateMeasurements.setString(2, graffito_height);
			updateMeasurements.setString(3, graffito_length);
			updateMeasurements.setString(4, letterMin);
			updateMeasurements.setString(5, letterMax);
			updateMeasurements.setString(6, letterFlMin);
			updateMeasurements.setString(7, letterFlMax);
			updateMeasurements.setString(8, graffiti_id);

			int rowsUpdated = updateMeasurements.executeUpdate();
			if (rowsUpdated > 0) {
				// System.out.println(graffiti_id);
				;
			}

			if (cils.contains(cil)) {
				System.out.println("duplicate: " + cil);
			}

			cils.add(cil);

		}
		updateMeasurements.close();

	}

}
