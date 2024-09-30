package edu.wlu.graffiti.data.setup.index;

import java.io.FileWriter;
import java.io.IOException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.wlu.graffiti.data.main.DBInteraction;
import edu.wlu.graffiti.data.setup.TransformEDRContentToEpiDoc;

/**
 * Connects to database and generates terms to be processed based on whether
 * figural or not Also, calls ProcessTextualTerms and ProcessFiguralTerms so
 * that everything is run in the correct order
 * 
 * @author Grace MacDonald
 */
public class GenerateTerms extends DBInteraction {

	private static final String GET_CONTENT = "SELECT graffiti_id, content FROM inscriptions WHERE ancient_city='Herculaneum'";

	// Create the list of exisiting terms
	private static ArrayList<String[]> termData = new ArrayList<String[]>();
	private static ArrayList<String[]> figTermData = new ArrayList<String[]>();
	private static String city = "herc";

	// Runs from main to mimic the flow of similar classes
	public static void main(String[] args) {

		String[] passedArgs = { city };

		System.out.println(passedArgs[0]);

		GenerateTerms ie = new GenerateTerms();
		System.out.println("Generating terms...");

		ie.runDBInteractions();

		System.out.println("Terms generated!");

		System.out.println("Processing textual terms....");
		ProcessTextualTerms.main(passedArgs);
		System.out.println("Textual terms processed!");

		System.out.println("Processing figural terms....");
		ProcessFiguralTerms.main(passedArgs);
		System.out.println("Figural terms processed!");

		System.out.println("Generating unique term list...");
		GetUniqueTerms.main(passedArgs);
		System.out.println("Unique terms list generated!");

	}

	public void run() {
		getTermsFromDB();
	}

	public void getTermsFromDB() {
		try {

			ResultSet graffiti = dbCon.createStatement().executeQuery(GET_CONTENT);

			while (graffiti.next()) {

				String graffiti_id = graffiti.getString(1);
				String content = graffiti.getString(2);

				// Standardize angle brackets and normalize characters
				content = content.replaceAll("&#12296;", "&#60;").replaceAll("&#12297;", "&#62;");
				content = TransformEDRContentToEpiDoc.normalize(content);

				// Save a copy of the original content
				// String original_content = content;

				// Join words that were continued across lines
				content = content.replaceAll("[\\s]*=[\\s]?\\n[\\s]*", "");
				// content = content.replaceAll("=\\n", "");

				// Replace line breaks with spaces
				content = content.replaceAll("\\n", " ");

				// Convert Angle brackets to a more usable form
				content = content.replaceAll("&#60;", "<").replaceAll("&#62;", ">");
				// Remove punctuation (excluding ? marks), also have to escape the period
				content = content.replaceAll("\\.", "").replaceAll(",", "");
				// Remove span tags
				content = content.replaceAll("</span>", "");

				content = content.replaceAll("'", "");

				String term = cleanTerm(content);

				term = term.replaceAll("\\s+$", "");

				// Check that the term isn't empty
				if (!term.matches("\\s") && !term.equals("")) {
					if (term.startsWith("((:") && term.endsWith("))")) {
						String[] figT = { graffiti_id, term };

						figTermData.add(figT);
					}

					else {
						String[] termD = { graffiti_id, term };

						termData.add(termD);
					}
				}

			}

			try {
				printToCSV(city + "Textual", termData);
			} catch (IOException e) {
				e.printStackTrace();
			}

			try {
				printToCSV(city + "Figural", figTermData);
			} catch (IOException e) {
				e.printStackTrace();
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

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

		// Merge non-standard spellings with their appropriate form
		if (term.matches("[^\\s]+[ ]?\\(:[^\\(\\):]+\\)")) {
			Matcher spellMatcher = Pattern.compile("\\(:([^\\(\\)]+)\\)").matcher(term);
			if (spellMatcher.find()) {
				term = spellMatcher.group(1); // .replaceAll("\\?", "");
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

		return term;
	}

	private static void printToCSV(String fileName, ArrayList<String[]> data) throws IOException {
		FileWriter csvWriter = new FileWriter("src/main/resources/indexing/" + fileName + ".csv");

		for (String[] d : data) {
			csvWriter.append(String.join(",", d));
			csvWriter.append("\n");
		}

		csvWriter.close();
	}
}
