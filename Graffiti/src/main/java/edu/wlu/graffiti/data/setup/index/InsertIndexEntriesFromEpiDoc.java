package edu.wlu.graffiti.data.setup.index;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import edu.wlu.graffiti.data.setup.EpiDocReader;
import edu.wlu.graffiti.data.setup.TransformEpiDocToContent;
import edu.wlu.graffiti.data.setup.Utils;

/**
 * This class is no longer used, but was useful in extracting name and place
 * information automatically
 * 
 * @author Trevor Stalnaker
 */
public class InsertIndexEntriesFromEpiDoc {
	private static String DB_DRIVER;
	private static String DB_URL;
	private static String DB_USER;
	private static String DB_PASSWORD;
	private static Document dom;
	static Connection newDBCon;

	private static final String INSERT_TERM = "INSERT INTO terms (term, category, language, part_of_speech) VALUES (?,?,?,?)";

	private static final String INSERT_INDEX_ENTRY = "INSERT INTO index (term_id, graffiti_id, hit, content) VALUES (?,?,?,?)";

	private static final String REMOVE_INDEX_ENTRY = "DELETE FROM index WHERE term_id=? AND graffiti_id=?";

	private static final String GET_TERM_ID = "SELECT term_id FROM terms WHERE term=?";

	private static final String GET_TERM_COUNT = "SELECT COUNT(term_id) FROM index WHERE term_id=?";

	private static final String REMOVE_TERM = "DELETE FROM terms WHERE term_id=?";

	private static final String GET_LANG_AND_POS = "SELECT language, part_of_speech FROM terms WHERE term=?";

	private static final String INSERT_NAME = "INSERT INTO names (name, name_type, person_type, gender) VALUES (?,?,?,?)";

	public static void main(String[] args) {
		readInData();
	}

	public static void readInData() {
		init();
		File file = new File("../Graffiti/data/AGPData/all.xml");
		parseEpidoc(file);
	}

	public static void parseEpidoc(File file) {
		try {

			// Create Prepared Statements
			PreparedStatement pstmt_term = newDBCon.prepareStatement(INSERT_TERM);
			PreparedStatement pstmt_index = newDBCon.prepareStatement(INSERT_INDEX_ENTRY);
			PreparedStatement pstmt_remove = newDBCon.prepareStatement(REMOVE_INDEX_ENTRY);
			PreparedStatement pstmt_get_term_id = newDBCon.prepareStatement(GET_TERM_ID);
			PreparedStatement pstmt_remove_term = newDBCon.prepareStatement(REMOVE_TERM);
			PreparedStatement pstmt_get_term_count = newDBCon.prepareStatement(GET_TERM_COUNT);
			PreparedStatement pstmt_get_lang_pos = newDBCon.prepareStatement(GET_LANG_AND_POS);
			// PreparedStatement pstmt_name = newDBCon.prepareStatement(INSERT_NAME);

			// Create a document factory
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

			// Using factory get an instance of document builder
			DocumentBuilder db;
			db = dbf.newDocumentBuilder();

			// parse using builder to get DOM representation of the XML file
			dom = db.parse(file);

			// Create a document reader for the dom
			EpiDocReader reader = new EpiDocReader(dom);
			reader.setRoot(dom, "Inscriptions");

			ArrayList<String> terms = new ArrayList<String>();

			ArrayList<Node> inscriptions = reader.getNodesByTag("TEI");

			PrintWriter writer = new PrintWriter("data/AGPData/epinames.csv");

			StringBuilder stringBuilder = new StringBuilder();
			CSVPrinter csvFilePrinter = null;
			CSVFormat csvFileFormat = CSVFormat.DEFAULT.withRecordSeparator("\n");

			csvFilePrinter = new CSVPrinter(stringBuilder, csvFileFormat);

			for (Node inscription : inscriptions) {

				String graffiti_id = reader.getNodesByTagAndAncestor("title", inscription).get(0).getTextContent()
						.replaceAll("AGP-", "");

				Node content = reader.getNodesByTagAtrributeAndAncestor("div", "type", "edition", inscription).get(0);

				String entry = TransformEpiDocToContent.translateContent(content, reader);

				// Get Information for the People Index

//				ArrayList<Node> names = reader.getNodesByTagAndAncestor("persName", content);
//				
//				String nymRef = "", personName = "", persName_type = "", name_type = "";
//				if (names != null && names.size() != 0) {
//					for (Node name : names) {
//						ArrayList<Node> children = reader.getDirectChildrenExcludingText(name);
//						persName_type = reader.getAttributeValueForNode(name, "type");
//						for (Node child : children) {
//							String tag = ((Element) child).getTagName();
//							if (tag.equals("name")) {
//								nymRef = reader.getAttributeValueForNode(child, "nymRef");
//								name_type = reader.getAttributeValueForNode(child, "type");
//								personName = TransformEpiDocToContent.translateContent(name, reader);
//							}
//						}
//						
//						// Add the term to the table if it doesn't already exist
//						String term = nymRef.replace("nametable.xml#", "");
//						String lemma = term;
//						if (!terms.contains(term)) {
//							terms.add(term);
//							pstmt_term.setString(1, term);
//							pstmt_term.setString(2, "people");
//							
//							//Find the term id for a given term
//							pstmt_get_lang_pos.setString(1, InsertIndexEntries.cleanTerm(personName.trim()));
//							ResultSet rs = pstmt_get_lang_pos.executeQuery();
//							if (rs.next()) {
//								pstmt_term.setString(3, rs.getString("language"));
//								//pstmt_term.setString(4, rs.getString("part_of_speech"));
//								pstmt_term.setString(4, "noun");
//							}
//							
//							pstmt_term.executeUpdate();
//							
//							// Add the name to the names table
//							
//							if (persName_type.equals("god")) {persName_type = "divine";}
//							
//							//pstmt_name.setString(1, term);
//							//pstmt_name.setString(2, name_type);
//							//pstmt_name.setString(3, persName_type);
//							//pstmt_name.setString(4, "review");
//							//pstmt_name.executeUpdate();
//						}
//
//						// Set the hit equal to the person's name as it was written
//						String hit = personName;
//						
//						List<Object> termRecord = new ArrayList<Object>();
//						
//						// fill in the fields
//						termRecord.add(InsertIndexEntries.cleanTerm(hit.trim()));
//						termRecord.add(lemma);
//
//						// write the inscription record
//						csvFilePrinter.printRecord(termRecord);	
//						
//						// Set the graffiti id
//						pstmt_index.setString(2, graffiti_id);
//						pstmt_remove.setString(2, graffiti_id);
//						
//						//Find the term id for a given term
//						pstmt_get_term_id.setString(1, term);
//						ResultSet rs = pstmt_get_term_id.executeQuery();
//						if (rs.next()) {
//							pstmt_index.setInt(1, rs.getInt("term_id"));
//							pstmt_index.setString(3, hit);
//							pstmt_index.setString(4, entry.replace(hit, "<strong>" + hit + "</strong>"));
//							pstmt_index.executeUpdate();
//						}
//						
//						//Remove previous references to term
//						String t = InsertIndexEntries.cleanTerm(hit.trim());
//						pstmt_get_term_id.setString(1, t);
//						ResultSet rs2 = pstmt_get_term_id.executeQuery();
//						if (rs2.next()) {
//							pstmt_remove.setInt(1, rs2.getInt("term_id"));
//							pstmt_remove.executeUpdate();
//							
//							pstmt_get_term_count.setInt(1, rs2.getInt("term_id"));
//							ResultSet rs3 = pstmt_get_term_count.executeQuery();
//							if (rs3.next()) {
//								if (rs3.getInt(1) == 0) {
//									pstmt_remove_term.setInt(1, rs2.getInt("term_id"));
//									pstmt_remove_term.executeUpdate();
//								}
//							}
//						}		
//					}
//				}

				// Get Information for the Places Index
				List<Node> places = reader.getNodesByTagAndAncestor("placeName", content);
				String lemma = "", placeName = "";
				if (places != null && places.size() != 0) {
					for (Node place : places) {
						List<Node> children = reader.getDirectChildrenExcludingText(place);
						for (Node child : children) {
							String tag = ((Element) child).getTagName();
							if (tag.equals("w")) {
								lemma = reader.getAttributeValueForNode(child, "lemma");
								placeName = TransformEpiDocToContent.translateContent(place, reader);
							}
						}

						// Add the term to the table if it doesn't already exist
						String term = lemma;
						if (!terms.contains(term)) {
							terms.add(term);
							pstmt_term.setString(1, term);
							pstmt_term.setString(2, "places");

							// Find the term id for a given term
							pstmt_get_lang_pos.setString(1, InsertIndexEntries.cleanTerm(placeName.trim()));
							ResultSet rs = pstmt_get_lang_pos.executeQuery();
							if (rs.next()) {
								pstmt_term.setString(3, rs.getString("language"));
								// pstmt_term.setString(4, rs.getString("part_of_speech"));
								pstmt_term.setString(4, "noun");
							}

							// pstmt_term.executeUpdate();
						}

						// Set the hit equal to the places's name as it was written
						String hit = placeName;

						List<Object> termRecord = new ArrayList<Object>();

//						// fill in the fields
						termRecord.add(InsertIndexEntries.cleanTerm(hit.trim()));
						termRecord.add(lemma);

//						// write the inscription record
						csvFilePrinter.printRecord(termRecord);

						// Set the graffiti id
						pstmt_index.setString(2, graffiti_id);
						pstmt_remove.setString(2, graffiti_id);

						// Find the term id for a given term
						pstmt_get_term_id.setString(1, term);
						ResultSet rs = pstmt_get_term_id.executeQuery();
						if (rs.next()) {
							pstmt_index.setInt(1, rs.getInt("term_id"));
							pstmt_index.setString(3, hit);
							pstmt_index.setString(4, entry.replace(hit, "<strong>" + hit + "</strong>"));
							pstmt_index.executeUpdate();

						}

						// Remove previous references to term
						String t = InsertIndexEntries.cleanTerm(hit.trim());
						pstmt_get_term_id.setString(1, t);
						ResultSet rs2 = pstmt_get_term_id.executeQuery();
						if (rs2.next()) {
							pstmt_remove.setInt(1, rs2.getInt("term_id"));
							pstmt_remove.executeUpdate();

							pstmt_get_term_count.setInt(1, rs2.getInt("term_id"));
							ResultSet rs3 = pstmt_get_term_count.executeQuery();
							if (rs3.next()) {
								if (rs3.getInt(1) == 0) {
									pstmt_remove_term.setInt(1, rs2.getInt("term_id"));
									pstmt_remove_term.executeUpdate();
								}
							}
						}
					}
				}
			}

			csvFilePrinter.close();
			writer.print(stringBuilder.toString());

			System.out.println("Name Data Successfully Exported...");

			// Close statements, connections, and file readers
			writer.close();

		} catch (ParserConfigurationException | SAXException | IOException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void init() {
		getConfigurationProperties();

		try {
			Class.forName(DB_DRIVER);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		try {
			newDBCon = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public static void getConfigurationProperties() {
		Properties prop = Utils.getConfigurationProperties();

		DB_DRIVER = prop.getProperty("db.driverClassName");
		DB_URL = prop.getProperty("db.url");
		DB_USER = prop.getProperty("db.user");
		DB_PASSWORD = prop.getProperty("db.password");
	}
}
