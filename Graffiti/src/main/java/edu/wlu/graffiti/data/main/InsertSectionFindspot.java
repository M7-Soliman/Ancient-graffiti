package edu.wlu.graffiti.data.main;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

public class InsertSectionFindspot extends DBConnection {

	private static String GET_SECTIONS_STATEMENT = "SELECT segment_name, display_name, street_name, city FROM "
			+ "segments JOIN streets ON segments.street_id=streets.id";

	private static String GET_SECTION_BY_NAME = "SELECT id FROM segments WHERE segment_name=?";

	private PreparedStatement insertionStmt;

	public InsertSectionFindspot() {
		super();
		try {
			insertionStmt = dbCon.prepareStatement(GET_SECTION_BY_NAME);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void main(String args[]) {
		// List<String> r1 = Arrays.asList("15","17");
		// List<String> r2 = Arrays.asList("11","19");
		// System.out.println(rangeInRange(r1, r2));
		// System.out.println(getSection("I","4","North side"));
		InsertSectionFindspot isf = new InsertSectionFindspot();
		System.out.println(isf.findSectionID("Pompei (Napoli), Regio VI.9 o VI.10, Via di Mercurio, Facade "));
		System.out.println(isf.findSectionID("Ercolano (Napoli), Decumanus Inferior (locus incertus)"));
		System.out.println(isf.findSectionID("Pompei (Napoli), Regio II.4-6, Facade, Praedia di Giulia Felice"));
		System.out.println(isf.findSectionID("Pompei (Napoli), Regio II.3.5, R of, Facade"));
		System.out.println(isf.findSectionID("Pompeii, Via dell'Abbondanza (IX.11.1-8)"));
		isf.close();
	}

	public int findSectionID(String findspot) {
		try {
			insertionStmt.setString(1, findSection(findspot));
			ResultSet rs = insertionStmt.executeQuery();
			if (rs.next()) {
				return rs.getInt("id");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	/*
	 * Given the findspot of an inscription, this method returns the segment in the
	 * database that that inscription is found on
	 */
	private String findSection(String findspot) {

		// Initialize variables
		String regio = "";
		String insula = "";
		String props = "";

		// If the inscription was found in Pompeii (ie it has a regio component)
		if (findspot.contains("Pompei")) {
			// Check if the findspot contains a range of properties
			Matcher section_matcher = Pattern.compile("([IVX]+)\\.([0-9]+)\\.([0-9a-g]+(\\-[0-9a-g]+)?)")
					.matcher(findspot);
			if (section_matcher.find()) {
				regio = section_matcher.group(1);
				insula = section_matcher.group(2);
				props = section_matcher.group(3);
			}
			// Handle the special case of the uncertainties of inscriptions on either VI.9
			// or VI.10
			else if (findspot.contains("VI.9 o VI.10")) {
				return "VI.9 o VI.10";
			}
			// Or if it describes the findspot with relative cardinal directions
			else {
				Matcher side_matcher = Pattern.compile("([Ss](outh)?|[Nn](orth)?|[Ee](ast)?|[Ww](est)?) [Ss]ide")
						.matcher(findspot);
				if (side_matcher.find()) {

					// Save the side information
					props = side_matcher.group(0);

					// Get the regio and insula of the inscripion
					Matcher regIns_matcher = Pattern.compile("([IVX]+)\\.([0-9]+)").matcher(findspot);
					if (regIns_matcher.find()) {
						regio = regIns_matcher.group(1);
						insula = regIns_matcher.group(2);
					}
				}
			}
		}

		// If the inscription was fonund in Herculaneum (ie there is no regio component)
		if (findspot.contains("Ercolano")) {
			Matcher section_matcher = Pattern.compile("([IVX]+)\\.([0-9]+(\\-[0-9]+)?)").matcher(findspot);
			if (section_matcher.find()) {
				insula = section_matcher.group(1);
				props = section_matcher.group(2);
			}
			// Handle the special case for the decumano massimo
			else if (findspot.contains("Decumano Massimo")) {
				return "Decumanus Maximus";
			} else if (findspot.contains("Decumanus Inferior (locus incertus)")) {
				return "Decumanus Inferior Uncertain";
			}
		}

		// Handle the road in Stabiae
		if (findspot.contains("strada a nord di Villa San Marco")) {
			return "strada a nord di Villa San Marco";
		}

		return getSection(regio, insula, props);
	}

	/*
	 * This method maps a section given as a regio, insula, and a range of
	 * properties to a segment in the database
	 */
	private String getSection(String regio, String insula, String props) {

		try {
			// Execute the query to search for all sections in database
			Statement stmt = dbCon.createStatement();
			ResultSet rs = stmt.executeQuery(GET_SECTIONS_STATEMENT);

			// Iterate through the street sections in the database
			while (rs.next()) {

				// Extract variables from the database
				String segmentName = rs.getString("segment_name");
				String displayName = rs.getString("display_name");
				String city = rs.getString("city");

				// Break the section into its constituent parts
				ArrayList<String> segment_parts = new ArrayList<String>(Arrays.asList(segmentName.split("\\.")));

				// If there is a comma in the properties part of the findspot, check each
				// distinct range for a match
				for (String segPart : segment_parts.get(segment_parts.size() - 1).split(",")) {

					// Set the regio as the empty string if the city is Herculaneum
					if (segment_parts.size() == 2 && city.equals("Herculaneum")) {
						segment_parts.add(0, "");
					}

					// Only check segments that are fully formed and in the right regio and insula
					if (segment_parts.size() == 3 && segment_parts.get(0).equals(regio)
							&& segment_parts.get(1).equals(insula)) {

						// Determine which segments sections described with cardinal directions / sides
						// belong to
						Matcher side_matcher = Pattern
								.compile("([Ss](outh)?|[Nn](orth)?|[Ee](ast)?|[Ww](est)?) [Ss]ide").matcher(props);
						if (side_matcher.find()) {
							String name = "";
							if (!regio.equals("")) {
								name += regio + ".";
							}
							name += insula + " (" + side_matcher.group(0).substring(0, 1).toUpperCase() + " side)";

							if (displayName.equals(name)) {
								return segmentName;
							}
						}

						// Check if the range on the section in question falls with the current
						// segment's range
						if (rangeInRange(Arrays.asList(props.split("\\-")), Arrays.asList(segPart.split("\\-")))) {

							// If the section's range is within the segment's range
							// return the name of the segment
							return segmentName;
						}

					}
				}

			}

			stmt.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return "";
	}

	/*
	 * A method that takes two ranges as lists of strings and determines if the
	 * first range is contained within the second. The lists are strings because
	 * some of the ranges for facades take the form 'a-g'
	 */
	private static boolean rangeInRange(List<String> r1, List<String> r2) {

		// Declare variables
		int upperBound, lowerBound, uBound, lBound;
		String upper, lower, u, l;

		// Determine if a lower and upper bound were passed and act accordingly
		if (r2.size() == 1) {
			lower = r2.get(0);
			upper = lower;
		} else {
			lower = r2.get(0);
			upper = r2.get(1);
		}

		// Determine if a lower and upper bound were passed and act accordingly
		if (r1.size() == 1) {
			l = r1.get(0);
			u = l;
		} else {
			l = r1.get(0);
			u = r1.get(1);
		}

		// Convert the strings to the appropriate representations
		lowerBound = convertForRange(lower);
		upperBound = convertForRange(upper);
		lBound = convertForRange(l);
		uBound = convertForRange(u);

		return (lowerBound <= lBound && lBound <= upperBound) && (lowerBound <= uBound && uBound <= upperBound);

	}

	/*
	 * A private method that converts strings into ints Strings that represent the
	 * decimals are converted directly to integers, but strings representing
	 * alphabet characters are converted to their ascii values
	 */
	private static int convertForRange(String s) {
		int conversion;
		if (StringUtils.isNumeric(s)) {
			conversion = Integer.parseInt(s);
		} else {
			conversion = (int) s.charAt(0);
		}
		return conversion;
	}

}
