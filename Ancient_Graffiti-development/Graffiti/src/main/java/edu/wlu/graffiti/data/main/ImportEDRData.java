package edu.wlu.graffiti.data.main;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import edu.wlu.graffiti.bean.Insula;
import edu.wlu.graffiti.bean.Property;
import edu.wlu.graffiti.data.setup.HandleFindspotsWithoutAddresses;
import edu.wlu.graffiti.data.setup.InsertContributors;
import edu.wlu.graffiti.data.setup.TransformEDRContentToEpiDoc;
import edu.wlu.graffiti.data.setup.Utils;

/**
 * Import the data from EDR. This data includes
 * <ul>
 * <li>most of the inscriptions' metadata
 * <li>the inscriptions' content
 * <li>the inscriptions' bibliography
 * <li>the inscriptions' apparatus criticus
 * <li>the photos for the inscriptions
 * <li>the contributors to the inscriptions
 * </ul>
 * 
 * @author Sara Sprenkle
 * @author Trevor Stalnaker
 */
public class ImportEDRData extends DBInteraction {

	/* Location of data in the EDR CSV file. */
	private static final int LOCATION_OF_WRITING_STYLE = 9;
	private static final int LOCATION_OF_LANGUAGE = 10;
	
	
	private static final String INSERT_POETRY_STATEMENT = "INSERT INTO poetic_graffiti_info " 
			+ "(graffiti_id, meter, author, confirmed)"
			+ " VALUES (?, ?, ?, ?)";
	
	private static final String UPDATE_POETRY_STATEMENT = "UPDATE poetic_graffiti_info SET"
			+ " meter=?, author=?, confirmed=?" 
			+ " WHERE graffiti_id= ?";
	

	private static final String INSERT_INSCRIPTION_STATEMENT = "INSERT INTO inscriptions "
			+ "(graffiti_id, ancient_city, find_spot, measurements, writing_style, \"language\", date_beginning, date_end, date_explanation, is_poetic) "
			+ "VALUES (?,?,?,?,?,?,?,?,?,?)";

	private static final String UPDATE_INSCRIPTION_STATEMENT = "UPDATE inscriptions SET "
			+ "ancient_city=?, find_spot=?, measurements=?, writing_style=?, \"language\"=?, date_beginning=?, date_end=?, date_explanation=?, is_poetic=?"
			+ "WHERE graffiti_id = ?";

	private static final String CHECK_INSCRIPTION_STATEMENT = "SELECT COUNT(*) FROM inscriptions"
			+ " WHERE graffiti_id = ?";

	private static final String CHECK_POETRY_STATEMENT = "SELECT COUNT(*) FROM poetic_graffiti_info"
			+ " WHERE graffiti_id = ?";
	
	public static final String UPDATE_PROPERTY = "UPDATE inscriptions SET " + "property_id = ? WHERE graffiti_id = ?";

	private static final String UPDATE_CONTENT = "UPDATE inscriptions SET " + "content = ? WHERE graffiti_id = ?";
	private static final String UPDATE_CIL = "UPDATE inscriptions SET " + "cil = ? WHERE graffiti_id = ?";

	private static final String UPDATE_CONTENT_EPIDOC = "UPDATE inscriptions SET "
			+ "content_epidocified = ? WHERE graffiti_id = ?";
	private static final String UPDATE_BIB = "UPDATE inscriptions SET " + "bibliography = ? WHERE graffiti_id = ?";
	private static final String UPDATE_APPARATUS = "UPDATE inscriptions SET " + "apparatus = ? WHERE graffiti_id = ?";
	private static final String SET_ON_FACADE = "UPDATE inscriptions SET on_facade = true WHERE graffiti_id = ?";
	private static final String SET_SEGMENT_ID = "UPDATE inscriptions SET segment_id = ? WHERE graffiti_id = ?";
	private static final String SELECT_INSULA_AND_PROPERTIES = "SELECT *, insula.id as insula_id, properties.id as property_id from insula, properties where insula_id = insula.id";
	private static final String INSERT_PHOTO_STATEMENT = "INSERT INTO photos (graffiti_id, photo_id) "
			+ "VALUES (?, ?)";

	private static final String SET_PRECISE_LOCATION = "UPDATE inscriptions SET precise_location=? WHERE graffiti_id=?";
	private static final String SET_COLUMN_INFO = "UPDATE inscriptions SET on_column=true, column_id=? WHERE graffiti_id=?";
	private static final String GET_COLUMN_ID = "SELECT id FROM columns WHERE roman_numeral=?";

	private static final String GET_STABIAE_PROPERTY_ID = "SELECT id FROM properties WHERE property_name=?";

	private static PreparedStatement insertPStmt;
	private static PreparedStatement insertPoetryStmt;

	private static PreparedStatement updatePStmt;
	private static PreparedStatement updatePoetryStmt;
	private static PreparedStatement selPStmt;
	private static PreparedStatement poemSelPStmt;
	private static PreparedStatement updatePropertyStmt;
	private static PreparedStatement updateApparatusStmt;
	private static PreparedStatement setFacade;
	private static PreparedStatement setSegment;
	private static PreparedStatement setPreciseLocation;
	private static PreparedStatement setColumnInfo;
	private static PreparedStatement getColumnId;
	private static PreparedStatement getPropertyId;
	private static PreparedStatement insertPhotoStmt;

	private static Map<String, HashMap<String, Insula>> cityToInsulaMap;

	private static Map<Integer, HashMap<String, Property>> insulaToPropertyMap;

	private static List<Pattern> patternList;
	private InsertSectionFindspot sectionFindspotInserter;

	public static void main(String[] args) {
		ImportEDRData importer = new ImportEDRData();
		importer.runDBInteractions();
	}
	
	public ImportEDRData() {
		super();
		sectionFindspotInserter = new InsertSectionFindspot();
	}
	
	@Override
	public void run() {
		try {
			readPropertiesAndInsula();
			HandleFindspotsWithoutAddresses handleFindspotsWithoutAddresses = new HandleFindspotsWithoutAddresses();
			handleFindspotsWithoutAddresses.populateFindSpotPropertyIdsMapping();
			
			updatePropertyStmt = dbCon.prepareStatement(UPDATE_PROPERTY);
			insertPhotoStmt = dbCon.prepareStatement(INSERT_PHOTO_STATEMENT);

			updateInscriptions("data/EDRData/epigr.csv");
			updateContent("data/EDRData/testo_epigr.csv");
			updateBibliography("data/EDRData/editiones.csv");
			updateApparatus("data/EDRData/apparatus.csv");
			updatePhotoInformation("data/EDRData/foto.csv");

			System.out.println("\nOn to Camodeca...\n");

			// do again for Camodeca
			updateInscriptions("data/AGPData/camodeca_epigr.csv");
			updateContent("data/AGPData/camodeca_testo.csv");
			updateBibliography("data/AGPData/camodeca_editiones.csv");
			updateApparatus("data/AGPData/camodeca_apparatus.csv");

			InsertContributors insertContributors = new InsertContributors();
			// insert contributor information from the ammini.csv file
			insertContributors.insertContributors();
			
			patternList = new ArrayList<Pattern>();
			patternList.add(Pattern.compile("^.* \\((\\w*\\.\\w*.\\w*)\\)"));

			patternList.add(
					Pattern.compile("^\\w+ \\(\\w+\\),? ([\\w'.-]* )* ?\\(?([\\w',-\\.]*)\\)?(, [\\s\\w-,'.\\(\\)]*)?$"));
			// TODO: Need to update the pattern to handle Insula Orientalis I

			// New Regular Expression: Group 3 holds the information that we want about the
			// insula
			// "(Insula([ ]Orientalis)?[ ])?([VIXM]+(.[\\d\\-a-z]+)+[^,\\(\\).]?)"

			// close this object's connection
			sectionFindspotInserter.close();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}		
	}


	private void readPropertiesAndInsula() throws SQLException {
		System.out.println("Reading in properties and insula");
		cityToInsulaMap = new HashMap<String, HashMap<String, Insula>>();
		insulaToPropertyMap = new HashMap<Integer, HashMap<String, Property>>();

		Statement infoStmt = dbCon.createStatement();

		ResultSet rs = infoStmt.executeQuery(SELECT_INSULA_AND_PROPERTIES);

		while (rs.next()) {
			String modernCity = rs.getString("modern_city");
			String insName = rs.getString("short_name");
			String propNum = rs.getString("property_number");
			String propName = rs.getString("property_name");
			int insID = rs.getInt("insula_id");
			int propID = rs.getInt("property_id");

			if (!cityToInsulaMap.containsKey(modernCity)) {
				cityToInsulaMap.put(modernCity, new HashMap<String, Insula>());
			}
			Insula ins = new Insula(insID, modernCity, insName, "");
			cityToInsulaMap.get(modernCity).put(insName, ins);

			Property p = new Property(propID, propNum, propName, ins);

			if (!insulaToPropertyMap.containsKey(insID)) {
				insulaToPropertyMap.put(insID, new HashMap<String, Property>());
			}

			insulaToPropertyMap.get(insID).put(propNum, p);

		}
		rs.close();
		infoStmt.close();

		for (String city : cityToInsulaMap.keySet()) {
			System.out.println("city: " + city);
			for (String insulaName : cityToInsulaMap.get(city).keySet()) {
				System.out.println("    - " + insulaName + ": " + cityToInsulaMap.get(city).get(insulaName));
			}
		}

	}

	/**
	 * Update the apparatus information in each of the inscription entries.
	 * 
	 * @param apparatusFileName
	 */
	private void updateApparatus(String apparatusFileName) {
		String edrID = "";
		try {
			updateApparatusStmt = dbCon.prepareStatement(UPDATE_APPARATUS);

			Reader in = new InputStreamReader(new FileInputStream(apparatusFileName), "UTF-8");
			Iterable<CSVRecord> records = CSVFormat.EXCEL.parse(in);
			for (CSVRecord record : records) {
				edrID = Utils.cleanData(record.get(0));
				if (record.size() == 1) { // skip if
					continue;
				}
				String apparatus = Utils.cleanData(record.get(1));

				try {
					selPStmt.setString(1, edrID);

					ResultSet rs = selPStmt.executeQuery();

					int count = 0;

					if (rs.next()) {
						count = rs.getInt(1);
					} else {
						System.err.println(edrID
								+ ":\nSomething went wrong with the SELECT statement in updating apparatus!");
					}

					if (count == 1) {
						apparatus = apparatus.trim();
						updateApparatusStmt.setString(1, apparatus);
						updateApparatusStmt.setString(2, edrID);

						int updated = updateApparatusStmt.executeUpdate();
						if (updated != 1) {
							System.err.println("\nSomething went wrong with apparatus for " + edrID);
							System.err.println(apparatus);
						}
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		} catch (IOException | SQLException e) {
			System.err.println("\nSomething went wrong with apparatus for " + edrID);
			e.printStackTrace();
		}
	}

	/**
	 * Updates the bibliography field in the database, using the EDR CSV export
	 * file. Also handles that the AGP link may be in the bibliography and should be
	 * removed.
	 * 
	 * @param bibFileName
	 */
	private void updateBibliography(String bibFileName) {
		try {
			PreparedStatement updateBibStmt = dbCon.prepareStatement(UPDATE_BIB);
			PreparedStatement updateCilStmt = dbCon.prepareStatement(UPDATE_CIL);

			Reader in = new FileReader(bibFileName);
			Iterable<CSVRecord> records = CSVFormat.EXCEL.parse(in);
			for (CSVRecord record : records) {
				String edrID = Utils.cleanData(record.get(0));
				String bib = Utils.cleanData(record.get(1));

				try {
					selPStmt.setString(1, edrID);

					ResultSet rs = selPStmt.executeQuery();

					int count = 0;

					if (rs.next()) {
						count = rs.getInt(1);
					} else {
						System.err.println(
								edrID + ":\nSomething went wrong with the SELECT statement in updating inscriptions!");
					}

					if (count == 1) {
						updateBibStmt.setString(1, bib);
						updateBibStmt.setString(2, edrID);

						int updated = updateBibStmt.executeUpdate();
						if (updated != 1) {
							System.err.println("\nSomething went wrong with bibliography for " + edrID);
							System.err.println(bib);
						} else {
							updateCilStmt.setString(1, extractCIL(bib));
							updateCilStmt.setString(2, edrID);
							updateCilStmt.executeUpdate();
						}
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}

			}
			updateBibStmt.close();
			updateCilStmt.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
	}

	/**
	 * Updates the content field of database, based on the EDR CSV export file. Also
	 * creates a "starter" epidoc content field for agp_inscriptions_info based on
	 * the content.
	 * 
	 * @param contentFileName
	 */
	private void updateContent(String contentFileName) {
		try {
			PreparedStatement updateContentStmt = dbCon.prepareStatement(UPDATE_CONTENT);
			PreparedStatement updateEpidocStmt = dbCon.prepareStatement(UPDATE_CONTENT_EPIDOC);

			Reader in = new InputStreamReader(new FileInputStream(contentFileName), "UTF-8");
			Iterable<CSVRecord> records = CSVFormat.EXCEL.parse(in);
			for (CSVRecord record : records) {
				String eagleID = Utils.cleanData(record.get(0));
				String content = Utils.cleanData(record.get(1));

				try {
					int count = 0;
					content = cleanContent(content);
					selPStmt.setString(1, eagleID);

					ResultSet rs = selPStmt.executeQuery();

					if (rs.next()) {
						count = rs.getInt(1);
					} else {
						System.err.println(eagleID
								+ ":\nSomething went wrong with the SELECT statement in updating inscriptions!");
					}
					if (count == 1) {
						updateContentStmt.setString(1, content);
						updateContentStmt.setString(2, eagleID);

						int updated = updateContentStmt.executeUpdate();
						if (updated != 1) {
							System.err.println("\nSomething went wrong with content for " + eagleID);
							System.err.println(content);
						} else {
							updateEpidocStmt.setString(1,
									TransformEDRContentToEpiDoc.transformContentToEpiDoc(content));
							updateEpidocStmt.setString(2, eagleID);
							updateEpidocStmt.executeUpdate();
						}
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
	}

	public static String extractCIL(String bib) {
		Pattern pattern = Pattern.compile("CIL\\s04\\,*\\s0*([1-9]*[0-9]+\\w*)");
		Matcher matcher = pattern.matcher(bib);
		if (matcher.find()) {
			return matcher.group(1);
		}
		return "";
	}

	/**
	 * Replaces the <br>
	 * tags with newlines. We handle the line breaks in our code.
	 * 
	 * @param content
	 * @return
	 */
	private static String cleanContent(String content) {
		// doing this because some inscriptions have uppercase BR tags in their contents
		// for some reason
		return content.replaceAll("\\<br\\>|\\<BR\\>", "\n");
		// return content.replace("<br>", "\n");
	}

	private void updateInscriptions(String datafileName) {
		try {

			insertPStmt = dbCon.prepareStatement(INSERT_INSCRIPTION_STATEMENT);
			updatePStmt = dbCon.prepareStatement(UPDATE_INSCRIPTION_STATEMENT);
			selPStmt = dbCon.prepareStatement(CHECK_INSCRIPTION_STATEMENT);
			
			insertPoetryStmt = dbCon.prepareStatement(INSERT_POETRY_STATEMENT);
			updatePoetryStmt = dbCon.prepareStatement(UPDATE_POETRY_STATEMENT);
			poemSelPStmt = dbCon.prepareStatement(CHECK_POETRY_STATEMENT);

			Reader in = new FileReader(datafileName);
			Iterable<CSVRecord> records = CSVFormat.EXCEL.parse(in);
			for (CSVRecord record : records) {
				String eagleID = Utils.cleanData(record.get(0));
				String ancient_city = Utils.cleanData(record.get(3));

				// Clean the name of the city if it is Stabiae
				if (ancient_city.equals("Nuceria Alfaterna (Stabiae)")) {
					ancient_city = "Stabiae";
				}

				if (!cityToInsulaMap.containsKey(ancient_city)) {
					// System.err.println(eagleID + ": city " + ancient_city + " not found");
					continue;
				}

				String findSpot = Utils.cleanData(record.get(5));
				String dateBeginning = Utils.cleanData(record.get(15));
				String dateEnd = Utils.cleanData(record.get(16));
				String dateExplanation = Utils.cleanData(record.get(22));
				String alt = Utils.cleanData(record.get(17));
				String lat = Utils.cleanData(record.get(18));
				String littAlt = Utils.cleanData(record.get(20));

				String measurements = createMeasurementField(alt, lat, littAlt);

				String writingStyle = Utils.cleanData(record.get(LOCATION_OF_WRITING_STYLE));
				String language = Utils.cleanData(record.get(LOCATION_OF_LANGUAGE));
				
				String meter ="";
				String author ="";
				boolean confirmed=false;
				
				// Getting the data about the poems
				String poemOriginal = "";
				if (record.values().length == 26) {
					poemOriginal = Utils.cleanData(record.get(25));
				}
				
				boolean isPoetic = false;
				
				if (poemOriginal.equals("1")) {

					isPoetic = true;
					meter = Utils.cleanData(record.get(24));
					
				}
					
				
				selPStmt.setString(1, eagleID);

				ResultSet rs = selPStmt.executeQuery();

				int count = 0;

				if (rs.next()) {
					count = rs.getInt(1);
				} else {
					System.err.println(
							eagleID + ":\nSomething went wrong with the SELECT statement in updating inscriptions!");
				}

				int successUpdate = 0;

				if (count == 0) {
					successUpdate = insertEagleInscription(eagleID, ancient_city, findSpot, measurements, writingStyle,
							language, dateBeginning, dateEnd, dateExplanation, isPoetic);
				} else {
					successUpdate = updateEagleInscription(eagleID, ancient_city, findSpot, measurements, writingStyle,
							language, dateBeginning, dateEnd, dateExplanation, isPoetic);
				}
				
				if (successUpdate == 1 && isPoetic) {
					insertPoetryInformation(eagleID, meter, author, confirmed);
				}

				// update AGP Metadata
				if (successUpdate == 1) {
					updateAGPMetadata(eagleID, ancient_city, findSpot);
				} else {
					System.err.println("Error updating/inserting " + eagleID);
				}

			}

			in.close();
			insertPStmt.close();

		} catch (SQLException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Update the AGP Metadata for this inscription
	 * 
	 * @param edrId
	 * @param ancient_city
	 * @param findSpot
	 * @throws SQLException
	 */
	private void updateAGPMetadata(String edrId, String ancient_city, String findSpot) throws SQLException {

		String test_id = "";
		boolean flag = false;

		setPreciseLocation = dbCon.prepareStatement(SET_PRECISE_LOCATION);

		// Determine if the inscription is on a facade, then treat accordingly
		setFacade = dbCon.prepareStatement(SET_ON_FACADE);
		setSegment = dbCon.prepareStatement(SET_SEGMENT_ID);
		String facadeRegex = "([Ff]a[Ã]?[cç§]ade[s]?)|(Decumano Massimo)|(Decumanus Inferior)|(strada a nord di Villa San Marco)";
		Matcher facade_matcher = Pattern.compile(facadeRegex).matcher(findSpot);
		if (facade_matcher.find()) {

			// Set on_facade to true for inscription
			setFacade.setString(1, edrId);
			setFacade.executeUpdate();

			// Add the segment id to the inscription
			int segID = sectionFindspotInserter.findSectionID(findSpot);
			if (segID != 0) {
				setSegment.setInt(1, segID);
				setSegment.setString(2, edrId);
				setSegment.executeUpdate();
			} else {
				System.err.println("*** Could not find section find spot for " + edrId + ": " + findSpot);
			}

			// Add precise location information to the inscription
			String location = findSpot.substring(findSpot.indexOf(",") + 2).replace("Regio ", "");
			setPreciseLocation.setString(1, location);
			setPreciseLocation.setString(2, edrId);
			setPreciseLocation.executeUpdate();

			// on a facade... Facades don't have properties so can end here
			return;
		}

		setColumnInfo = dbCon.prepareStatement(SET_COLUMN_INFO);
		getColumnId = dbCon.prepareStatement(GET_COLUMN_ID);

		// Determine if the inscription was written on a column
		if (findSpot.contains("col.")) {

			String location = findSpot.substring(findSpot.indexOf("col."));
			setPreciseLocation.setString(1, location);
			setPreciseLocation.setString(2, edrId);
			setPreciseLocation.executeUpdate();

			String col_regex = "[A-Z]+";
			Matcher matcher = Pattern.compile(col_regex).matcher(location);
			int col_id = 0;
			if (matcher.find()) {
				String col = matcher.group(0);
				getColumnId.setString(1, col);
				ResultSet rs = getColumnId.executeQuery();
				if (rs.next()) {
					col_id = rs.getInt("id");
				}
			}
			setColumnInfo.setInt(1, col_id);
			setColumnInfo.setString(2, edrId);
			setColumnInfo.executeUpdate();
			System.out.println(edrId + " is on a column.  Additional information stored...");
		}

		String address = convertFindSpotToAddress(findSpot);

		if (edrId.equals(test_id) || flag) {
			System.out.println("EDR Find Spot: " + findSpot);
			System.out.println("Address: " + address);
		}

		// Handle inscriptions from Stabiae separately
		getPropertyId = dbCon.prepareStatement(GET_STABIAE_PROPERTY_ID);
		if (ancient_city.equals("Stabiae")) {

			getPropertyId.setString(1, address);
			ResultSet rs = getPropertyId.executeQuery();
			int prop_id = 0;
			if (rs.next()) {
				prop_id = rs.getInt("id");
			}
			if (prop_id != 0) {
				updatePropertyIdForGraffito(edrId, prop_id);
			} else {
				System.err.println(edrId + ": Stabiae Address isn't a property address: " + address);
			}
			return;
		}

		// These find spots don't contain an address
		if (!address.contains(".")) {
			System.err.println(edrId + ": Address isn't a property address: " + address);
			// TODO
			// we're going to skip these because I can't handle them yet.
			if (!findSpot.contains(")")) {
				return;
			}

			// try to find the property id
			address = removeCityFromFindSpot(findSpot);
			System.out.println("After city removal: " + address);
			System.err.println("See if matches a known property without an address: " + address);

			for (String possibleFindSpot : HandleFindspotsWithoutAddresses.findspotNameToPropertyID.keySet()) {
				if (address.startsWith(possibleFindSpot)) {
					int propertyId = HandleFindspotsWithoutAddresses.findspotNameToPropertyID.get(possibleFindSpot);
					// update property info
					updatePropertyIdForGraffito(edrId, propertyId);
					return;
				}
			}

			System.err.println("Still couldn't handle: " + address);
			return;
		}

		if (ancient_city.equals("Pompeii") || ancient_city.equals("Herculaneum")) {

			String insula = "";
			String propertyNum = "";

			if (ancient_city.equals("Pompeii")) {
				insula = address.substring(0, address.lastIndexOf('.'));
			} else if (ancient_city.equals("Herculaneum")) {
				insula = address.substring(0, address.indexOf('.'));
			}
			propertyNum = address.substring(address.lastIndexOf('.') + 1);

			if (edrId.equals(test_id) || flag) {
				System.out.println("Insula Number: " + insula);
				System.out.println("Property Number: " + propertyNum);
			}

			if (!cityToInsulaMap.get(ancient_city).containsKey(insula)) {
				System.err.println("insula not found in city: " + edrId + ": Insula " + insula + " in " + ancient_city
						+ ", " + address);
				return;
			}

			int insulaID = cityToInsulaMap.get(ancient_city).get(insula).getId();

			if (edrId.equals(test_id) || flag) {
				System.out.println("EDR ID: " + edrId);
				System.out.println("Insula ID: " + insulaID);
			}

			if (!insulaToPropertyMap.get(insulaID).containsKey(propertyNum)) {
				System.err.println(edrId + ": Property " + propertyNum + " in Insula " + insula + " in " + ancient_city
						+ " not found.  Orig Findspot: " + findSpot + " address: " + address);
				return;
			}

			int propertyID = insulaToPropertyMap.get(insulaID).get(propertyNum).getId();

			if (edrId.equals(test_id) || flag) {
				System.out.println("Property ID: " + propertyID);
			}

			// update property info
			updatePropertyIdForGraffito(edrId, propertyID);
		}

	}

	public boolean updatePropertyIdForGraffito(String edrId, int propertyID) throws SQLException {
		updatePropertyStmt.setInt(1, propertyID);
		updatePropertyStmt.setString(2, edrId);

		int response = updatePropertyStmt.executeUpdate();

		if (response != 1) {
			System.err.println("WHAT? " + edrId);
			return false;
		}

		return true;
	}

	/**
	 * Try to remove the city info from the find spot. Assumes format "Modern City
	 * (Region), location". If the find spot is not in this format, unclear what
	 * should happen.
	 * 
	 * @param findSpot original find spot text from EDR
	 * @return the find spot without the city
	 */
	private static String removeCityFromFindSpot(String findSpot) {
		// Example: Ercolano (Napoli), Decumano Massimo
		int firstCommaPos = findSpot.indexOf(',');
		if (firstCommaPos == -1) { // no comma in the findspot
			return findSpot;
		}
		// start after the , and space
		return findSpot.substring(firstCommaPos + 2, findSpot.length());
	}

	/**
	 * Parses the findspot for the address.
	 * 
	 * @param findSpot
	 * @return
	 */
	public static String convertFindSpotToAddress(String findSpot) {
		// Example: Pompei (Napoli) VII.12.18-20, Lupanare, cella b
		// Example: Ercolano (Napoli), Insula III.11, Casa del Tramezzo di Legno
		// Example: Castellammare di Stabia (Napoli), Villa San Marco, ambiente 5

		String regex = "(Insula([ ]Orientalis)?[ ])?\\(?([VIX]+([\\.,][\\d\\-a-z]+)*)([ ,\\)]|$)";
		String stabiaeRegex = "\\(Napoli\\), ([a-zA-z\\s]+), [a-zA-Z0-9\\s]";
		Matcher matcher = Pattern.compile(regex).matcher(findSpot);
		Matcher stabiaeMatcher = Pattern.compile(stabiaeRegex).matcher(findSpot);
		if (matcher.find()) {
			String match = matcher.group(3);
			if (matcher.group(1) != null && matcher.group(1).contains("Insula Orientalis")) {
				match = "InsulaOrientalis" + match;
			}
			return match;
		} else if (stabiaeMatcher.find()) {
			return stabiaeMatcher.group(1);
		} else {
			// Matcher matcher2 = Pattern.compile("Ercolano[ ]\\(Napoli\\),?[
			// ]([\\s\\w]+)").matcher(findSpot);
			Matcher matcher2 = Pattern.compile("[A-Z][a-z]+[ ]\\(Napoli\\),[ ](([ ]?([^\\s,\\(\\)]+))+)")
					.matcher(findSpot);
			if (matcher2.find()) {
				return matcher2.group(1);
			}
			return findSpot;
		}
	}

	public static String oldconvertFindSpotToAddress(String findSpot) {

		patternList = new ArrayList<Pattern>();
		patternList.add(Pattern.compile("^.* \\((\\w*\\.\\w*.\\w*)\\)"));

		patternList.add(
				Pattern.compile("\\w+ \\(\\w+\\),? ([\\w'.-]* )* ?\\(?([\\w',-\\.]*)\\)?(, [\\s\\w-,'.\\(\\)]*)?"));

		// Hack to handle Insula Orientalis special addresses
		if (findSpot.contains("Insula Orientalis ")) {
			findSpot = findSpot.replace("Insula Orientalis ", "InsulaOrientalis");
		}

		Matcher matcher = patternList.get(0).matcher(findSpot);
		if (matcher.matches()) {
			// System.err.println("matched first");
			return matcher.group(1);
		}

		matcher = patternList.get(1).matcher(findSpot);
		if (matcher.matches()) {
			// System.err.println("matched second");
			return matcher.group(2);
		} else {
			return findSpot;
		}
	}

	private static int insertEagleInscription(String eagleID, String ancient_city, String findSpot, String measurements,
			String writingStyle, String language, String dateBeginning, String dateEnd, String dataExplanation, boolean isPoetic)
			throws SQLException {
		insertPStmt.setString(1, eagleID);
		insertPStmt.setString(2, ancient_city);
		insertPStmt.setString(3, findSpot);
		insertPStmt.setString(4, measurements);
		insertPStmt.setString(5, writingStyle);
		insertPStmt.setString(6, language);
		insertPStmt.setString(7, dateBeginning);
		insertPStmt.setString(8, dateEnd);
		insertPStmt.setString(9, dataExplanation);
		insertPStmt.setBoolean(10, isPoetic);

		try {
			return insertPStmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}
	

	private static int updateEagleInscription(String eagleID, String ancient_city, String findSpot, String measurements,
			String writingStyle, String language, String dateBeginning, String dateEnd, String dateExplanation, boolean isPoetic)
			throws SQLException {
		updatePStmt.setString(1, ancient_city);
		updatePStmt.setString(2, findSpot);
		updatePStmt.setString(3, measurements);
		updatePStmt.setString(4, writingStyle);
		updatePStmt.setString(5, language);
		updatePStmt.setString(6, dateBeginning);
		updatePStmt.setString(7, dateEnd);
		updatePStmt.setString(8, dateExplanation);
		updatePStmt.setBoolean(9, isPoetic);
		updatePStmt.setString(10, eagleID);

		try {
			return updatePStmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	//Method that updates the table containing information about poetry
	private static int insertPoetryInformation(String eagleID, String meter, String author, boolean confirmed) throws SQLException {
		
		insertPoetryStmt.setString(1, eagleID);
		insertPoetryStmt.setString(2, meter);
		insertPoetryStmt.setString(3, author);
		insertPoetryStmt.setBoolean(4, confirmed);
		
		try {
			return insertPoetryStmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return 0;
	}
	
	

	/**
	 * Insert the photo information into database
	 * 
	 * @param string
	 */
	private void updatePhotoInformation(String dataFileName) {
		try {
			Reader in = new FileReader(dataFileName);
			Iterable<CSVRecord> records = CSVFormat.EXCEL.parse(in);
			for (CSVRecord record : records) {
				String eagleID = Utils.cleanData(record.get(0));
				eagleID = eagleID.toUpperCase();
				String photoID = Utils.cleanData(record.get(1));
				insertPhotoInformation(eagleID, photoID);
			}

			in.close();
			insertPStmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void insertPhotoInformation(String eagleID, String photoID) throws SQLException {
		insertPhotoStmt.setString(1, eagleID);
		insertPhotoStmt.setString(2, photoID);

		try {
			insertPhotoStmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private static String createMeasurementField(String alt, String lat, String littAlt) {
		// Example: alt.: 2.50 lat.: 7.50 litt. alt.: 2-2,5

		StringBuffer sb = new StringBuffer();
		sb.append("height: ");
		sb.append(alt);
		sb.append(" width: ");
		sb.append(lat);
		sb.append(" letter height: ");
		sb.append(littAlt);

		return sb.toString();
	}
	
}
