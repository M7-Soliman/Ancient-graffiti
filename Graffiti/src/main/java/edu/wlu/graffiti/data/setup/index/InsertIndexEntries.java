package edu.wlu.graffiti.data.setup.index;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.wlu.graffiti.data.main.DBInteraction;
import edu.wlu.graffiti.data.setup.TransformEDRContentToEpiDoc;

/**
 * This class parses through the content field in the database and populates the
 * index and terms tables
 * 
 * @author Trevor Stalnaker
 * @author Pepe Estrada Hamm
 * @author Grace MacDonald
 */
public class InsertIndexEntries extends DBInteraction {
	// Queries to Reset the Tables
	public static String RESTART_TABLE_SEQUENCE = "ALTER SEQUENCE terms_id_seq RESTART";
	public static String CLEAR_INDEX = "DELETE FROM terms";

	// Database queries
	// IMPORTANT NOTICE
	// The end of this query (WHERE ancient_city=...) is a temporary hack to only
	// get results from certain regions
	// This part of the query should be removed for further testing and deployments
	// Everything else should still function properly after this small component is
	// removed
	private static final String GET_CONTENT = "SELECT graffiti_id, content FROM inscriptions";// WHERE
																								// ancient_city='Herculaneum'";

	private static final String INSERT_TERM = "INSERT INTO terms (term, category, language, part_of_speech, sort_key, display) "
			+ "VALUES (?,?,?,?,?,?)";

	private static final String INSERT_INDEX_ENTRY = "INSERT INTO index (term_id, graffiti_id, hit, content) VALUES (?,?,?,?)";

	private static final String GET_TERM_ID = "SELECT term_id FROM terms WHERE term=? AND NOT category='figural-terms'";

	private static final String GET_FIGURAL_TERM_ID = "SELECT term_id FROM terms WHERE term=? AND category='figural-terms'";

	private static final String GET_LEMMA = "SELECT lemma FROM lemma WHERE term=?";

	private static final String SET_LEMMA = "INSERT INTO lemma (term, lemma) VALUES (?,?)";

	private static final String IS_NAME = "SELECT COUNT(*) as count FROM names WHERE name=?";

	private static final String IS_IN_LEMMA = "SELECT lemma FROM lemma WHERE term=?";

	private static final String IS_PLACE = "SELECT COUNT(*) as count FROM places WHERE name=?";

	// Create the list of existing terms
	private static ArrayList<String> termLyst = new ArrayList<String>();
	private static ArrayList<String> figTermLyst = new ArrayList<String>();

	// Create lists for terms that require manual review
	private static ArrayList<String> reviewLyst = new ArrayList<String>();
	private static ArrayList<String> potentialNamesLyst = new ArrayList<String>();

	// Runs from main to mimic the flow of similar classes
	public static void main(String[] args) {
		InsertIndexEntries ie = new InsertIndexEntries();
		ie.runDBInteractions();
	}

	public void run() {
		insertIndexEntries();

		// Create a list of terms for the classicists to review
		try {
			String str = "";
			for (String item : potentialNamesLyst) {
				str += item + "\n";
			}
			printToFile("namesLyst", str);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public void insertIndexEntries() {
		try {

			// Clear the Indices and Restart the Table Sequence
			PreparedStatement pstmt1 = dbCon.prepareStatement(CLEAR_INDEX);
			PreparedStatement pstmt2 = dbCon.prepareStatement(RESTART_TABLE_SEQUENCE);
			pstmt1.executeUpdate();
			pstmt2.executeUpdate();
			pstmt1.close();
			pstmt2.close();

			// Create Prepared Statements
			PreparedStatement pstmt_term = dbCon.prepareStatement(INSERT_TERM);
			PreparedStatement pstmt_index = dbCon.prepareStatement(INSERT_INDEX_ENTRY);
			PreparedStatement pstmt_get_term_id = dbCon.prepareStatement(GET_TERM_ID);
			ResultSet graffiti = dbCon.createStatement().executeQuery(GET_CONTENT);

			while (graffiti.next()) {

				String graffiti_id = graffiti.getString(1);
				String content = graffiti.getString(2);

				// Standardize angle brackets and normalize characters
				content = content.replaceAll("&#12296;", "&#60;").replaceAll("&#12297;", "&#62;");
				content = TransformEDRContentToEpiDoc.normalize(content);

				// Save a copy of the original content
				String original_content = content;

				// Join words that were continued across lines
				content = content.replaceAll("[\\s]*=[\\s]?\\n[\\s]*", "");
				// content = content.replaceAll("=\\n", "");

				// Replace line breaks with spaces
				content = content.replaceAll("\\n", " ");
				// Add a space to the beginning and end for regex reasons
				content = " " + content + " ";
				// Convert Angle brackets to a more usable form
				content = content.replaceAll("&#60;", "<").replaceAll("&#62;", ">");
				// Remove punctuation (excluding ? marks), also have to escape the period
				content = content.replaceAll("\\.", "").replaceAll(",", "");
				// Remove span tags
				content = content.replaceAll("</span>", "");

				String testRegex = "(?<= )[\\[][^\\[\\]]+[\\]]|\\(\\(:[^\\(\\)]+\\)\\)|[^\\s]+ \\(:[^\\():]+\\)|<:[^<>]+>|[^\\s]+(?= )";
				String chunkDoubleBrackets = "(\\[\\[|〚)[^\\(\\[\\)\\]]+(\\]\\]|〛)";
				String chunkSquareStraddleOut = "\\[[^\\[\\]]+\\][^\\s\\[\\]]+\\[[^\\[\\]]+\\]";
				String chunkSquare = "[^\\s\\[\\]]*\\[[^\\[\\]]+\\][^\\s\\[\\]]*";
				String chunkFig = "\\(\\(:[^:]+\\)\\)";
				String chunkSpelling = "[^\\s]+ \\(:[^\\(\\):]+\\)";
				String chunkAngle = "<:[^<>]+>";
				String chunkUnknownLine = " - - - - - - | ------ ";
				String chunkSymbols = "\\(\\([^\\(\\):]+\\)\\)"; // This doesn't appear to be working quite right
				String chunkOr = "\\(or [^\\s]+\\)"; // This doesn't appear to be working quite right
				String chunkAbbr = "((\\([^\\(\\)\\:]+\\))*[^\\s\\(\\)\\:]+(\\([^\\(\\)\\:]+\\))*)(?= )";
				String chunkMarkup = "<:[^><]+>";
				String chunkOther = "[^\\s]+";
				// The ordering that each regex is applied is incredibly IMPORTANT
				String regex = "(?<= )" + chunkSpelling + "|" + chunkDoubleBrackets + "|" + chunkSquareStraddleOut + "|"
						+ chunkSquare + "|" + chunkFig + "|" + chunkAngle + "|" + chunkUnknownLine + "|" + chunkSymbols
						+ "|" + chunkOr + "|" + chunkAbbr + "|" + chunkMarkup + "|" + chunkOther + "(?= )";
				Matcher matcher = Pattern.compile(regex).matcher(content);
				while (matcher.find()) {

					String term = matcher.group(0);

					if (term.contains("cineδu(s) (:cinaedus)")) {
						System.out.println(term);
					}

					// Some terms actually contain multiple terms...
					ArrayList<String> terms = new ArrayList<String>();

					if (term.contains("<")) {
						// Convert angle brackets back to html safe characters
						term = term.replaceAll("<", "&#60;").replaceAll(">", "&#62;");
					}

					// Check for multiple terms in the corrected spelling
					if (term.matches("[^\\s]+ \\(:(([^\\(\\):]+[ ])+[^\\(\\):]+)\\)")) {
						Matcher spellMatcher = Pattern.compile("\\(:(([^\\(\\):]+[ ])+[^\\(\\):]+)\\)").matcher(term);
						if (spellMatcher.find()) {
							String temp = spellMatcher.group(1);
							for (String t : temp.split(" ")) {
								terms.add(t);
							}
						}
					} else if (term.matches("(\\([^\\(\\)\\:]+\\))*[^\\s\\(\\)\\:]+(\\([^\\(\\)\\:]+\\))*")) {
						for (String t : term.split(" ")) {
							t = t.replace(")", "").replace("(", "");
							terms.add(t);
						}
					} else {
						terms.add(term);
					}

					// Save original term
					String hit = term;
					String category = "";
					boolean display = false;

					for (String t : terms) {

						// Clean the term
						term = cleanTerm(t);

						// Check that the term isn't empty
						if (!term.matches("\\s") && !term.equals("")) {

							// Check that the term is not already in the terms list
							if (!termLyst.contains(term)) {

								pstmt_term.setString(1, term);

								Matcher finalMatcher = Pattern.compile("[\\[〚][\\+0-9\\sa-z\\?]+[\\]〛]").matcher(term);

								// Set the category of the term
								if (term.startsWith("((:") && term.endsWith("))")) {
									display = true;
									category = "figural";
									// Remove excess spaces to improve matching
									term = term.replaceAll("[ ]+", " ");
								} else if (term.startsWith("&#60;:") && term.endsWith("&#62;")) {
									display = false;
									category = "editorial_markup";
								} else if (term.startsWith("((") && term.endsWith("))")) {
									display = false;
									category = "symbol";
								} else if (term.matches("[\\+-\\[\\]\\〚\\〛\\s\\?·0-9]+") || finalMatcher.find()
										|| term.matches(
												"(^\\[[- ]+\\][\\s\\S]+\\[[- ]+\\]$)|(^\\[[- a-z\\?]+\\][\\s\\S]+$)|(^[\\s\\S]+\\[[- a-z\\?]+\\]$)")
										|| term.contains("+") || term.contains("-") || term.contains("&#60;")
										|| term.contains("&#62;")) {

									// Check if the term is a name --Eventually replace this with an IS_IN_LEMMA
									// query
									PreparedStatement pstmt_is_lemma = dbCon.prepareStatement(IS_IN_LEMMA);
									pstmt_is_lemma.setString(1, term);
									ResultSet rs_lemma = pstmt_is_lemma.executeQuery();
									if (rs_lemma.next()) {
										term = rs_lemma.getString("lemma");
										display = true;
										category = "people"; // this should be generalized eventually
									} else {
										display = false;
										category = "markup";
									}
								} else {
									term = lemmatize(term);

									// Check if the term is a name
									PreparedStatement pstmt_is_name = dbCon.prepareStatement(IS_NAME);
									pstmt_is_name.setString(1, term);
									ResultSet rs_name = pstmt_is_name.executeQuery();

									// Check if the term is a place
									PreparedStatement pstmt_is_place = dbCon.prepareStatement(IS_PLACE);
									pstmt_is_place.setString(1, term);
									ResultSet rs_place = pstmt_is_place.executeQuery();

									display = true;

									if (rs_name.next() && rs_name.getInt("count") > 0) {
										category = "people";
									} else if (rs_place.next() && rs_place.getInt("count") > 0) {
										category = "places";
									} else {
										category = "terms";
									}
								}

								// Get the language of the term based on its characters
								String block;
								ArrayList<String> blockLyst = new ArrayList<String>();
								for (int i = 0; i < term.length(); i++) {
									block = Character.UnicodeBlock.of(term.charAt(i)).toString();
									if (block.equals("GREEK") | block.equals("GREEK_EXTENDED")) {
										block = "greek";
									} else if (block.equals("BASIC_LATIN")) {
										block = "latin";
									} else {
										block = "";
									}
									if (!blockLyst.contains(block) && !block.equals("")) {
										blockLyst.add(block);
									}
								}
								String lang = String.join("-", blockLyst);
								if (lang.equals("latin-greek")) {
									lang = "greek-latin";
								}

								// Check the term list once more becuase of lemmatization
								if (!termLyst.contains(term)) {
									// Add the term to the terms list
									termLyst.add(term);
									pstmt_term.setString(1, term);
									pstmt_term.setString(2, category);
									pstmt_term.setString(3, lang);
									pstmt_term.setString(4, "place_holder");
									pstmt_term.setString(5, getSortableTerm(term));
									if (display) {
										pstmt_term.setBoolean(6, display);
									} else {
										pstmt_term.setNull(6, java.sql.Types.BOOLEAN);
									}
									System.out.println(term);
									pstmt_term.executeUpdate();
								}

								// Add the term to the terms list
								termLyst.add(term);

							}
							// Set the graffiti id
							pstmt_index.setString(2, graffiti_id);

							// Check for terms containing regex characters
							Matcher bold_matcher = Pattern.compile("[\\(\\[\\]\\)\\+\\.\\{\\}]").matcher(hit);

							String bold_regex = "((^|(?<= )|(?<=\\n))" + hit + "($|(?= )|(?=\\n)|(?=,)|(?=\\.)))|"
									+ "((^|(?<= )|(?<=\\n))" + escape(hit.charAt(0)) + "[^\\s]*[ ]?=[ ]?\\n[ ]?[^\\s]*"
									+ escape(hit.charAt(hit.length() - 1)) + "($|(?= )|(?=\\n)))";

							// Find the term id for a given term
							pstmt_get_term_id.setString(1, term);
							ResultSet rs = pstmt_get_term_id.executeQuery();
							if (rs.next()) {
								pstmt_index.setInt(1, rs.getInt("term_id"));
								pstmt_index.setString(3, hit);

								if (bold_matcher.find()) {
									pstmt_index.setString(4,
											original_content.replace(hit, "<strong>" + hit + "</strong>"));
								} else {
									pstmt_index.setString(4,
											original_content.replaceAll(bold_regex, "<strong>" + hit + "</strong>"));
								}
								pstmt_index.executeUpdate();
							}

							// Catch figural graffiti that have identical latin inscriptions
							if (term.startsWith("((:") && term.endsWith("))")) {
								addFiguralEntries(term, graffiti_id);
							}
						}
					}
				}
			}
			System.out.println("Index Loaded From Contents");
			pstmt_index.close();
			pstmt_term.close();
			// newDBCon.close();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	// Escape a character only if it has regular expression meaning
	public static String escape(char c) {
		String temp = Character.toString(c);
		Matcher escaper = Pattern.compile("[\\[\\]\\(\\)\\+\\-\\?\\{\\}]").matcher(temp);
		if (escaper.find()) {
			return "\\" + temp;
		}
		return temp;
	}

	public static String getSortableTerm(String term) {
		return Normalizer.normalize(term, Normalizer.Form.NFD).replaceAll("[\\u0300-\\u036F]", "");
	}

	public static String cleanTerm(String term) {

		// Standardize the term by removing special unicode characters
		term = term.replaceAll("\u0323", "").replaceAll("\u0332", "").replaceAll("\u0302", "").replaceAll("\u0331", "");

		// Replace nonstandard characters (Some of these look the same, but they AREN'T)
		term = term.replace("ì", "i").replace("Ì", "I").replace("ì", "i").replace("í", "i");

		// Remove Non-sensical (Uppercase Characters)
		if (term.matches("[A-Z·]+")) {
			term = "";
		}
		if (term.matches("([A-Z·]+\\[[- ]+\\][A-Z·]*)|([A-Z·]*\\[[- ]+\\][A-Z·]+)")) {
			term = "";
		}

		// Remove term if it is only \ characters
		if (term.matches("[\\\\]+")) {
			term = "";
		}

		// Remove surplus characters
		if (term.matches("\\{.+\\}")) {
			term = "";
		}
		term = term.replaceAll("\\{[^\\{\\}]+\\}", "");

		// Remove (?)s
		term = term.replaceAll("\\(\\?\\)", "");

		// Determine if the term is potentially a name
		Boolean addToNames = term.matches("[A-Z][a-z]+");

		// Convert the term to all lowercase
		term = term.toLowerCase();

		// Merge non-standard spellings with their appropriate form
		if (term.matches("[^\\s]+[ ]?\\(:[^\\(\\):]+\\)")) {
			Matcher spellMatcher = Pattern.compile("\\(:([^\\(\\)]+)\\)").matcher(term);
			if (spellMatcher.find()) {
				term = spellMatcher.group(1).replaceAll("\\?", "");
			}
		}

		// Remove abbreviation notation if term is not figural or non-standard spelling
		// if (term.matches("[^\\)\\(]+\\([^:\\)\\(\\?]+\\?*\\)")){
		if (term.matches("((\\([^\\(\\)\\:]+\\))*[^\\(\\)\\:]*(\\([^\\(\\)\\:]+\\))*)+")) {
			term = term.replaceAll("\\)", "").replaceAll("\\(", "").replaceAll("\\?", "");
		}

		// Remove alternatives with or notation
		if (term.matches("\\(or [^\\s]+\\)")) {
			term = "";
		}

		Matcher bracketMatcher = Pattern.compile("\\[[^\\s-\\?\\+\\]\\[]+\\]").matcher(term);
		while (bracketMatcher.find()) {
			String removeBrackets = bracketMatcher.group(0).replaceAll("\\[", "").replaceAll("]", "");
			term = term.replace(bracketMatcher.group(0), removeBrackets);
		}

		// Remove [---] from beginning and end of terms
//		Matcher lostMatcher = Pattern.compile("(^\\[[-]+\\])|(\\[[-]+\\]$)").matcher(term);
//		while (lostMatcher.find()){
//			String m1 = lostMatcher.group(1);
//			String m2 = lostMatcher.group(2);
//			int start = 0;
//			int end = term.length();
//			if (m1 != null) {start = m1.length();}
//			if (m2 != null) {end = end - m2.length();}
//			term = term.substring(start, end);
//		}

		// Remove any lingering colons from the end of terms
		if (!term.contentEquals("") && term.indexOf(":") == term.length() - 1) {
			term = term.substring(0, term.length() - 1);
		}

		// Remove quotations (not just the marks, but actual quotations)
		if (term.startsWith("'") && term.endsWith("'")) {
			term = term.substring(1, term.length() - 1);
		}

		// Remove lingering pipe characters
		if (term.contains("|")) {
			term = term.replace("|", "");
		}

		if (addToNames && !potentialNamesLyst.contains(term)) {
			potentialNamesLyst.add(term);
		}

		return term;
	}

	private String lemmatize(String term) {
		try {

			// Check if the term is already in the lemma table
			PreparedStatement pstmt_get_lemma = dbCon.prepareStatement(GET_LEMMA);
			pstmt_get_lemma.setString(1, term);
			ResultSet rs_lemma = pstmt_get_lemma.executeQuery();

			if (rs_lemma.next()) {
				return rs_lemma.getString("lemma");
			} else {
				// Get the lemma for the term according to Exeter's WordNet
				String lemma = getLatinLemma(term);
				// If no lemma was found, use the term as the lemma
				if (lemma.contentEquals("")) {
					reviewLyst.add(term);
					lemma = term;
				}
				// Save the result to the lemma table (for caching)
				PreparedStatement pstmt_set_lemma = dbCon.prepareStatement(SET_LEMMA);
				pstmt_set_lemma.setString(1, term);
				pstmt_set_lemma.setString(2, lemma);
				pstmt_set_lemma.executeUpdate();
				term = lemma;
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return term;
	}

	/**
	 * Returns the lemma for a given word using Exeter's Latin WordNet
	 * 
	 * If a lemma is not found, then the empty string is returned
	 */
	private static String getLatinLemma(String word) {
		String lemma = "";
		String morpho;
		String uri;
		String url = "https://latinwordnet.exeter.ac.uk/lemmatize/" + word + "/?format=json";
		try {
			// Read in the json information from the given url
			BufferedReader in = new BufferedReader(new InputStreamReader(new URL(url).openStream()));
			String json = "";
			String input;
			while ((input = in.readLine()) != null) {
				json += "\n" + (input);
			}
			in.close();
			// Convert the grabbed json string to a tree of nodes
			json = json.substring(2, json.length() - 1);
			JsonNode rootNode = new ObjectMapper().readTree(json);
			JsonNode lemmaNode = rootNode.path("lemma");
			lemma = lemmaNode.path("lemma").asText();
			morpho = lemmaNode.path("morpho").asText();
			uri = lemmaNode.path("uri").asText();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return lemma;
	}

	private void addFiguralEntries(String term, String graffiti_id) {
		try {
			PreparedStatement pstmt_fig_term = dbCon.prepareStatement(INSERT_TERM);
			PreparedStatement pstmt_get_term_id_by_cat = dbCon.prepareStatement(GET_FIGURAL_TERM_ID);
			PreparedStatement pstmt_index = dbCon.prepareStatement(INSERT_INDEX_ENTRY);

			// Save each word in the figural tag as its own unique term
			String[] fig_terms = term.replaceAll("\\(\\(:", "").replaceAll("\\)\\)", "").replaceAll("[\\?\\.,]", "")
					.replaceAll("\\(", "").replaceAll("\\)", "").replaceAll("=", "").split(" ");
			for (String fig_term : fig_terms) {
				String fig_lemma = lemmatize(fig_term);

				if (!figTermLyst.contains(fig_lemma)) {
					figTermLyst.add(fig_lemma);
					pstmt_fig_term.setString(1, fig_lemma);
					pstmt_fig_term.setString(2, "figural-terms");
					pstmt_fig_term.setString(3, "latin");
					pstmt_fig_term.setString(4, "place_holder");
					pstmt_fig_term.setString(5, getSortableTerm(fig_lemma));
					pstmt_fig_term.setBoolean(6, true);
					pstmt_fig_term.executeUpdate();
				}

				// Save a version of the term with spaces on either side
				// and remove the ((:)) formatting to standardize
				String formattedTerm = " " + term.substring(3, term.length() - 2) + " ";

				// Create a regex for finding the term in the content to bold it
				String fig_regex = "(?<=[ \\(=])" + fig_term + "(?=[ \\?\\)\\.])";

				// Find the term id for a given term
				pstmt_get_term_id_by_cat.setString(1, fig_lemma);
				ResultSet rs_fig = pstmt_get_term_id_by_cat.executeQuery();
				if (rs_fig.next()) {
					pstmt_index.setInt(1, rs_fig.getInt("term_id"));
					pstmt_index.setString(2, graffiti_id);
					pstmt_index.setString(3, fig_term);
					pstmt_index.setString(4, "((:"
							+ formattedTerm.replaceAll(fig_regex, "<strong>" + fig_term + "</strong>").trim() + "))");
					pstmt_index.executeUpdate();
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private static void printToFile(String file, String str) throws FileNotFoundException {
		PrintWriter writer = new PrintWriter("src/main/resources/" + file + ".txt");
		writer.println("File: " + file + ".txt");
		Calendar cal = Calendar.getInstance();
		writer.println("Date: " + (cal.get(Calendar.MONTH) + 1) + "/" + cal.get(Calendar.DAY_OF_MONTH) + "/"
				+ cal.get(Calendar.YEAR));
		writer.print("\n");
		writer.print(str);
		writer.close();
	}
}
