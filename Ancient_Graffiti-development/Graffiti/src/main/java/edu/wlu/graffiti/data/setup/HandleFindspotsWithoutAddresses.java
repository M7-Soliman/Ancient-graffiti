package edu.wlu.graffiti.data.setup;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import edu.wlu.graffiti.data.main.DBInteraction;

/**
 * This class looks up the properties for graffiti whose findspots aren't the
 * typical addresses. Requires a hardcod
 * 
 * Can be called as stand-alone script or from other classes.
 * 
 * @author Sara Sprenkle
 * 
 */
public class HandleFindspotsWithoutAddresses extends DBInteraction {

	private static final String SELECT_PROPERTY = "select id from properties where property_name = ?";
	private static final String SELECT_PROPERTY_BY_ITALIAN_PROPERTY_NAME = "select id from properties where italian_property_name = ?";

	private static final String ATYPICAL_FINDSPOT_FILE = "data/AGPData/atypical_findspots.csv";

	private static PreparedStatement selectPropertyStmt;
	private static PreparedStatement selectByItalianPropertyNameStmt;

	/**
	 * The array of findspots that don't have typical addresses.
	 */
	private static String[] noAddressFindSpots;
	// = { "Terme Suburbane", "Rampa", "Decumano Massimo", "Castellum Aquae" };

	public static Map<String, Integer> findspotNameToPropertyID;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		HandleFindspotsWithoutAddresses handler = new HandleFindspotsWithoutAddresses();
		handler.runDBInteractions();
	}

	@Override
	public void run() {
		populateFindSpotPropertyIdsMapping();
	}

	/**
	 * Creates a mapping between find spots without addresses and their property ids
	 */
	public void populateFindSpotPropertyIdsMapping() {

		// Read atypical findspots
		Reader in;
		try {
			in = new FileReader(ATYPICAL_FINDSPOT_FILE);
			Iterable<CSVRecord> records = CSVFormat.EXCEL.parse(in);
			List<String> atypicalFindSpots = new ArrayList<>();
			for (CSVRecord record : records) {
				String atypicalFindspot = Utils.cleanData(record.get(0));
				atypicalFindSpots.add(atypicalFindspot);
			}
			noAddressFindSpots = atypicalFindSpots.toArray(new String[0]);
			in.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		findspotNameToPropertyID = new HashMap<>();

		try {
			selectPropertyStmt = dbCon.prepareStatement(SELECT_PROPERTY);
			selectByItalianPropertyNameStmt = dbCon.prepareStatement(SELECT_PROPERTY_BY_ITALIAN_PROPERTY_NAME);

			for (String findspot : noAddressFindSpots) {
				int propertyId = 0;

				selectPropertyStmt.setString(1, findspot);

				ResultSet rs = selectPropertyStmt.executeQuery();

				if (rs.next()) {
					propertyId = rs.getInt(1);
					findspotNameToPropertyID.put(findspot, propertyId);
					// found a match; move on to the next find spot
					continue;
				} else {
					System.out.println("Error looking up " + findspot + " by property_name; try italian property name");
				}
				rs.close();

				// Try again with the Italian name ...
				selectByItalianPropertyNameStmt.setString(1, findspot);
				rs = selectByItalianPropertyNameStmt.executeQuery();

				if (rs.next()) {
					propertyId = rs.getInt(1);
					findspotNameToPropertyID.put(findspot, propertyId);
				} else {
					System.out.println("Error looking up " + findspot + " by italian_property_name");
				}

			}
			System.out.println("FindSpot : propertyID: " + findspotNameToPropertyID);
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

}