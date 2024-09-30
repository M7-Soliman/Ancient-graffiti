package edu.wlu.graffiti.data.setup;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.wlu.graffiti.data.main.DBInteraction;

/**
 * Reads in xml files, parses them, and puts their information into the database
 * appropriately.
 * 
 * Set DEBUG_OUTPUT flag to true if you want more output.
 *
 * @author Trevor Stalnaker
 *
 */
public class ReadFromEpidoc extends DBInteraction {

	private static final String SMYRNA_EPIDOCS_LOC = "../Graffiti/data/smyrna_epidocs";
	private static final String SMYRNA_FINDSPOT = "Basilica of the Agora of Smyrna, Izmir, Turkey";
	private static Document dom;
	private static final boolean DEBUG_OUTPUT = true; // incomplete use of this flag.

	private static final String INSERT_INTO_INSCRIPTIONS = "INSERT INTO inscriptions" + "(graffiti_id, commentary, "
			+ "content_translation, writing_style_in_english, graffito_height, graffito_length, letter_height_min, letter_height_max,"
			+ "lang_in_english, content_epidocified, caption,"
			+ "ancient_city, bibliography, date_beginning, date_end, content, find_spot, property_id, support_desc, layout_desc, handnote_desc, apparatus, apparatus_displayed) "
			+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

	private static final String CLEAR_SMYRNA_IMAGES = "DELETE FROM photos WHERE graffiti_id LIKE 'SMY%'";

	private static final String ADD_SMYRNA_IMAGE = "INSERT INTO photos (graffiti_id, photo_id) VALUES (?,?)";

	private static final String CLEAR_DATABASE_FOR_UPLOAD = "DELETE FROM inscriptions WHERE graffiti_id LIKE 'SMY%'";

	private static final String GET_PROPERTY_ID = "SELECT properties.id FROM properties JOIN insula ON "
			+ "properties.insula_id=insula.id WHERE property_name='Basilica' AND short_name='Agora' AND "
			+ "modern_city='Smyrna'";

	private static ArrayList<String> missingImages = new ArrayList<>();
	private static final String BASE_URL_OF_SMYRNA_IMAGES = "https://images.isaw.nyu.edu/collections/smyrna_basilica_graffiti/";

	public static void main(String[] args) {
		ReadFromEpidoc reader = new ReadFromEpidoc();
		reader.runDBInteractions();
	}

	@Override
	public void run() {
		try {
			// Clear database for Smyrna
			PreparedStatement pstmt3 = dbCon.prepareStatement(CLEAR_SMYRNA_IMAGES);
			PreparedStatement pstmt5 = dbCon.prepareStatement(CLEAR_DATABASE_FOR_UPLOAD);
			pstmt5.executeUpdate();
			pstmt3.executeUpdate();
			pstmt3.close();
			pstmt5.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		readInData();
		// for testing
		// parseEpidocFile("d6.1.xml");
	}

	public void readInData() {
		File[] xmlFiles = getSmyrnaEpidocs();
		for (File file : xmlFiles) {
			parseEpidoc(file);
		}
		System.out.println("\nImport Complete");
		System.out.println(xmlFiles.length + " files successfully loaded");
		System.out.println("Graffiti missing images:");
		for (int i = 0; i < missingImages.size(); i++) {
			System.out.println(i + ".) " + missingImages.get(i));
		}
	}

	public static File[] getSmyrnaEpidocs() {
		return new File(SMYRNA_EPIDOCS_LOC).listFiles();
	}

	public void parseEpidocFile(String file) {
		parseEpidoc((new File(SMYRNA_EPIDOCS_LOC + "/" + file)));
	}

	// Code that extracts fields from epidoc and saves them to the database
	public void parseEpidoc(File file) {

		// Fields in inscriptions
		String graffiti_id, ancient_city, content, bibliography, date_beginning, date_end;
		String apparatus = "";

		// Fields in inscriptions
		String comment, writing_style_in_english, graffito_height, graffito_length;
		String content_translation = "", letter_height_min = "", letter_height_max = "", lang_in_english, summary = "",
				support_desc = "", layout_desc = "", hand_desc = "";
		// String height_from_ground;
		// String individual_letter_heights, letter_with_flourishes_height_min,
		// letter_with_flourishes_height_max;

		try {
			// Create Prepared Statements
			PreparedStatement pstmt = dbCon.prepareStatement(INSERT_INTO_INSCRIPTIONS);
			PreparedStatement pstmt4 = dbCon.prepareStatement(ADD_SMYRNA_IMAGE);
			PreparedStatement pstmt2 = dbCon.prepareStatement(GET_PROPERTY_ID);

			// Create a document factory
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

			// Using factory get an instance of document builder
			DocumentBuilder db = dbf.newDocumentBuilder();

			// parse using builder to get DOM representation of the XML file
			dom = db.parse(new InputSource(new InputStreamReader(new FileInputStream(file), "UTF-8")));

			// Create a document reader for the dom
			EpiDocReader reader = new EpiDocReader(dom);

			// Save the file as a string
			String fileAsString = file.toString();

			if (DEBUG_OUTPUT) {
				System.out.println("\n\nFile: " + fileAsString);
			}
			// Create Distinct ID for the current graffito
			graffiti_id = createGraffitiIDFromSmyrnaInfo(fileAsString);
			System.out.println("\nExtracting data for " + graffiti_id);
			pstmt.setString(1, graffiti_id);

			pstmt.setString(17, SMYRNA_FINDSPOT);
			ResultSet rs = pstmt2.executeQuery();
			if (rs.next()) {
				int propId = rs.getInt("id");
				pstmt.setInt(18, propId);
			} else {
				System.err.println("Error getting the smyrna findspot.");
			}
			pstmt2.close();

			// Find the language the current graffito is written in in English
			lang_in_english = "";
			// FIX: by default, the graffito are marked as Greek, but the drawings aren't
			// Greek; should have no language for drawings
			if (!graffiti_id.contains("D")) {
				Node editionNode = reader.getNodeByTagAndAttribute("div", "type", "edition");
				if (editionNode != null) {
					String lang = reader.getAttributeValueForNode(editionNode, "xml:lang");
					String[] languages = lang.split(" ");
					for (int i = 0; i < languages.length; i++) {
						if (languages[i].equals("la")) {
							lang_in_english += "Latin";
						}
						if (languages[i].equals("grk") || languages[i].equals("grc")) {
							lang_in_english += "Greek";
						}
						if (i < languages.length - 1) {
							lang_in_english += "/";
						}
					}
				}
			}

			if (DEBUG_OUTPUT) {
				System.out.println("Formatted Lang: " + lang_in_english);
			}
			pstmt.setString(9, lang_in_english);

			// Find the origPlace element of the current graffito
			ancient_city = reader.getTextFromTag("origPlace");
			if (DEBUG_OUTPUT) {
				System.out.println("Ancient City: " + ancient_city);
			}
			pstmt.setString(12, ancient_city);

			// Find the bibliography of the current graffito
			bibliography = getBibliography(reader);
			pstmt.setString(13, bibliography);

			// Find the date information of the current graffito
			Node dateNode = reader.getNodeByTag("origDate");
			if (dateNode != null) {
				date_beginning = reader.getAttributeValueForNode(dateNode, "notBefore-custom");
				if (DEBUG_OUTPUT) {
					System.out.println("Start Date: " + date_beginning);
				}
				pstmt.setString(14, date_beginning);
				date_end = reader.getAttributeValueForNode(dateNode, "notAfter-custom");
				if (DEBUG_OUTPUT) {
					System.out.println("End Date: " + date_end);
				}
				pstmt.setString(15, date_end);
			}

			// Find the commentary of the current graffito
			comment = reader.getContent(reader.getNodeByTagAndParentAttribute("ab", "div", "type", "commentary"));
			comment += reader.getContent(reader.getNodeByTagAndParentAttribute("p", "div", "type", "commentary"));
			if (DEBUG_OUTPUT) {
				System.out.println("Commentary: " + comment);
			}
			pstmt.setString(2, comment);

			// Find and print the translation of the current graffito
			content_translation = reader.getTextFromTagAndParentAttribute("ab", "div", "type", "translation");
			content_translation += reader.getTextFromTagAndParentAttribute("p", "div", "type", "translation");
			if (DEBUG_OUTPUT) {
				System.out.println("Translation: " + content_translation);
			}
			pstmt.setString(3, content_translation);

			// Find the writing style of the current graffito
			writing_style_in_english = reader.getTextFromTag("rs");
			if (DEBUG_OUTPUT) {
				System.out.println("Writing Style: " + writing_style_in_english);
			}
			// Temporary Code to handle errors caused by differences in conventions (This
			// will need to be remedied in the database)
			if (writing_style_in_english.length() > 30) {
				writing_style_in_english = "";
			}
			pstmt.setString(4, writing_style_in_english);

			// Find the length and height of the current graffito
			graffito_height = reader.getTextFromTagAndParent("height", "dimensions");
			graffito_length = reader.getTextFromTagAndParent("width", "dimensions");
			System.out.println("Graffito Height: " + graffito_height);
			System.out.println("Graffio Length: " + graffito_length);
			pstmt.setString(5, graffito_height);
			pstmt.setString(6, graffito_length);

			// Find the letter height information of the current graffito
			ArrayList<Node> letterNodes = reader.getNodesByTagAndParent("height", "handNote");
			if (letterNodes.size() != 0) {
				for (Node node : letterNodes) {
					String attr = reader.getAttributeValueForNode(node, "scope");
					if (attr.equals("letter")) {
						if (reader.hasAttribute(node, "min") && reader.hasAttribute(node, "max")) {
							letter_height_min = reader.getAttributeValueForNode(node, "min");
							letter_height_max = reader.getAttributeValueForNode(node, "max");
						} else {
							String height_info = node.getTextContent();
							if (height_info.contains(",")) {
								String[] minMax = height_info.split(",");
								letter_height_min = minMax[0];
								letter_height_max = minMax[1];
							} else {
								letter_height_min = height_info;
								letter_height_max = letter_height_min;
							}
						}

					}
					if (attr.equals("individualletter")) {
						// TO-DO
					}
				}
			} else {
				letter_height_min = "";
				letter_height_max = "";
			}
			System.out.println("Min: " + letter_height_min);
			System.out.println("Max: " + letter_height_max);
			pstmt.setString(7, letter_height_min);
			pstmt.setString(8, letter_height_max);

			// Find the content of the current graffito
			content = reader.getContent(reader.getNodeByTagAndAttribute("div", "type", "edition"));
			System.out.println("Content: \n" + content);
			pstmt.setString(16, content);
			
			
			// TODO: Check on this; it doesn't seem like the epidocified content is right, e.g., d6.1

			// Find the epidocified content of the current graffito
			String epidoc = extractContentEpiDoc(fileAsString);
			if (DEBUG_OUTPUT) {
				System.out.println("Epidocified: " + epidoc);
			}
			pstmt.setString(10, epidoc);

			// Find title information of current graffito and save it as summary (for
			// Smyrna)
			summary = reader.getTextFromTagAndParent("title", "titleStmt").replaceAll("[^\\s]+\\.[0-9]+\\.\\:[ ]", "");
			System.out.println("Summary: " + summary);
			pstmt.setString(11, summary);

			// Find the support description
			support_desc = reader.getTextFromTagAndParent("support", "supportDesc");
			System.out.println("Support: " + support_desc);
			pstmt.setString(19, support_desc);

			// Find the layout description
			String[] temp = reader.getTextFromTagAndParent("layout", "layoutDesc").split("\n");
			for (String element : temp) {
				layout_desc += " " + element.trim();
			}
			if (!layout_desc.contains("TK")) {
				System.out.println("Layout: " + layout_desc);
			}
			pstmt.setString(20, layout_desc);

			// Find the hand note
			temp = reader.getTextFromTagAndParent("handNote", "handDesc").split("\n");
			for (String element : temp) {
				hand_desc += " " + element.trim();
			}
			System.out.println("Hand Note: " + hand_desc);
			pstmt.setString(21, hand_desc);

			apparatus = reader.getTextFromTagAndParentAttribute("ab", "div", "type", "apparatus");
			if (DEBUG_OUTPUT) {
				System.out.println("apparatus: " + apparatus);
			}

			// set the apparatus and apparatus_display to the same thing
			pstmt.setString(22, apparatus);
			pstmt.setString(23, apparatus);

			// Execute updates and close prepared statements
			pstmt.executeUpdate();
			pstmt.close();

			// Get the images for the current graffito (has to go after the update)
			String fileID = fileAsString.replace("../Graffiti/data/smyrna_epidocs/", "").replaceAll(".xml", "");
			List<String> images = getImageIds(fileID);
			System.out.println("Image Count: " + images.size());
			if (images.size() == 0) {
				missingImages.add(graffiti_id);
			}
			for (String image : images) {
				pstmt4.setString(1, graffiti_id);
				pstmt4.setString(2, image);
				pstmt4.executeUpdate();
			}
			pstmt4.close();

		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}
	}

	private String getBibliography(EpiDocReader reader) {
		String bibliography;
		bibliography = reader.getTextFromTagWithAttribute("div", "type", "bibliography");
		bibliography = bibliography.trim();
		bibliography = bibliography.replace("\n", "<br/>");
		System.out.println("Bibliography: " + bibliography);
		return bibliography;
	}

	/**
	 * Create the graffiti id, based on the file name
	 * 
	 * @param fileAsString
	 * @return
	 */
	private String createGraffitiIDFromSmyrnaInfo(String fileAsString) {
		String graffiti_id;
		graffiti_id = "";
		String fileName = fileAsString.replaceAll("\\.", "").replaceAll("/Graffiti/data/smyrna_epidocs/", "")
				.replaceAll("\\\\Graffiti\\\\data\\\\smyrna_epidocs\\\\", "").replaceAll("xml", "").toUpperCase();
		Matcher matcher = Pattern.compile("([A-Za-z]+)([0-9]+)").matcher(fileName);
		graffiti_id = "SMY";
		if (matcher.find()) {
			if (DEBUG_OUTPUT) {
				System.out.println("FileName:" + fileName);
				System.out.println("FileName Length: " + fileName.length());
			}
			int zeroCount = 9 - (fileName.length() + 3);
			graffiti_id += matcher.group(1);
			while (zeroCount > 0) {
				graffiti_id += "0";
				zeroCount--;
			}
			graffiti_id += matcher.group(2);
		}
		if (DEBUG_OUTPUT) {
			System.out.println("Graffiti ID: " + graffiti_id);
		}
		return graffiti_id;
	}

	/** Extracts content (the text) from the epidoc file */
	private static String extractContentEpiDoc(String file) throws IOException {
		String retString = "";
		String epiDocAsString = Files.lines(Paths.get(file)).collect(Collectors.joining("\n"));
		Matcher matcher = Pattern.compile("\\<div type=\"edition\"[\\w\\W]+\\<div type=\"translation\"\\>")
				.matcher(epiDocAsString);
		if (matcher.find()) {
			retString = matcher.group(0)
					.replaceAll("(\\</ab\\>[\\s]*)?\\</div\\>[\\s]*\\<div type=\"translation\"\\>", "")
					.replaceAll("\\<div type=\"edition\"[\\s\\S]*?\\>[\\s]*(\\<ab\\>)?", "").replaceAll("\\n", "")
					.replaceAll("[\\s]{2,}", " ");
		}
		return retString;
	}

	/**
	 * Generates the URL at NYU for the images
	 * 
	 * @param id the graffito's id
	 * @return the URL for this graffito
	 */
	private static String generateURL(String id) {
		String baysORpiers = "bays/";
		String location = "";
		String landing = "";
		String end = "index.json";
		Matcher matcher = Pattern.compile("[dt]([a-z]?)([\\d]*)\\.([\\d]+)").matcher(id);
		if (matcher.find()) {
			landing = matcher.group(0).replace(".", "_");
			location = matcher.group(2);
			if (location.length() == 1) {
				location = "0" + location;
			}
			if (matcher.group(1).equals("g")) {
				location = "g" + location;
			}
			if (matcher.group(1).equals("p")) {
				baysORpiers = "piers/";
				while (location.length() < 3) {
					location = "0" + location;
				}
				location = "p" + location;
			}
			if (matcher.group(1).equals("x")) {
				baysORpiers = "misc";
			}
		}
		String url = BASE_URL_OF_SMYRNA_IMAGES + baysORpiers + location + "/" + landing + "/" + end;
		System.out.println(url);
		return url;
	}

	/** Retrieves the names of images for inscription */
	private static List<String> getImageIds(String id) {
		List<String> names = new ArrayList<>();
		JsonNode rootNode;
		String url = generateURL(id);
		try {
			URL https_url = new URL(url);
			HttpsURLConnection con = (HttpsURLConnection) https_url.openConnection();

			rootNode = new ObjectMapper().readTree(con.getInputStream());
			JsonNode contentsNode = rootNode.path("subsections");
			Iterator<JsonNode> elements = contentsNode.elements();
			while (elements.hasNext()) {
				JsonNode mapNode = elements.next();
				JsonNode name = mapNode.path("uri");
				names.add(name.toString().replaceAll("\"", ""));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return names;
	}

	static {
		final TrustManager[] trustAllCertificates = new TrustManager[] { new X509TrustManager() {
			@Override
			public X509Certificate[] getAcceptedIssuers() {
				return null; // Not relevant.
			}

			@Override
			public void checkClientTrusted(X509Certificate[] certs, String authType) {
				// Do nothing. Just allow them all.
			}

			@Override
			public void checkServerTrusted(X509Certificate[] certs, String authType) {
				// Do nothing. Just allow them all.
			}
		} };

		try {
			SSLContext sc = SSLContext.getInstance("SSL");
			sc.init(null, trustAllCertificates, new SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
		} catch (GeneralSecurityException e) {
			throw new ExceptionInInitializerError(e);
		}
	}

}
