package edu.wlu.graffiti.data.setup.index;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Cleans textual terms and creates a CSV, currently hercCleanedTerms.csv which
 * contains the terms to be lemmatized
 * 
 * @author Grace MacDonald
 */
public class ProcessTextualTerms {

	private static ArrayList<String[]> missingFig = new ArrayList<String[]>();
	private static ArrayList<String[]> markup = new ArrayList<String[]>();
	private static ArrayList<String[]> symbolInfo = new ArrayList<String[]>();
	private static ArrayList<String[]> misc = new ArrayList<String[]>();
	private static ArrayList<String[]> termData = new ArrayList<String[]>();
	private static ArrayList<String[]> questionMark = new ArrayList<String[]>();
	private static ArrayList<String[]> correctSpelling = new ArrayList<String[]>();

	// Runs from main to mimic the flow of similar classes
	public static void main(String[] args) {
		String city = args[0];

		iterateThroughTerms(city + "Textual.csv");

		try {
			printToCSV(city + "CleanedTerms", termData);
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			printToCSV(city + "Markup", markup);
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			printToCSV(city + "Symbols", symbolInfo);
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			printToCSV(city + "QuestionMark", questionMark);
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			printToCSV(city + "CorrectSpellings", correctSpelling);
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			printToCSV(city + "Misc", misc);
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			addToFile(city + "Figural", missingFig);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void iterateThroughTerms(String file) {
		try (BufferedReader csvReader = new BufferedReader(
				new FileReader(System.getProperty("user.dir") + "/src/main/resources/indexing/" + file))) {
			String row;
			while ((row = csvReader.readLine()) != null) {
				String[] temporary = row.split("\n");
				String[] data = temporary[0].split(",");

				String content = data[1];

				content = content.replace("[", "").replace("]", "");

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
						Boolean change = false;
						for (String t : term.split(" ")) {
							String newT = "";

							if (matcher.find()) {
								String next = matcher.group(0);

								if (next.startsWith("(:") && next.endsWith(")")) {
									change = true;
									newT = t + " " + next;
								} else {
									terms.add(next);
								}
							}

							if (change == true) {
								terms.add(newT);
							} else {
								t = t.replace(")", "").replace("(", "");
								terms.add(t);
							}
						}
					} else {
						terms.add(term);
					}

					for (String t : terms) {
						Matcher finalMatcher = Pattern.compile("[\\[〚][\\+0-9\\sa-z\\?]+[\\]〛]").matcher(t);

						// Set the category of the term
						if (t.startsWith("((:") && t.endsWith("))")) {
							t = t.replaceAll("[ ]+", " ");
							String[] figD = { data[0], t };
							missingFig.add(figD);
						} else if (t.startsWith("&#60;:") && t.endsWith("&#62;")) {
							String[] markupD = { data[0], t };
							markup.add(markupD);
						} else if (term.startsWith("((") && term.endsWith("))")) {
							String[] symbolD = { data[0], t };
							symbolInfo.add(symbolD);
						} else if (t.matches("[\\+-\\[\\]\\〚\\〛\\s\\?·0-9]+") || finalMatcher.find() || t.matches(
								"(^\\[[- ]+\\][\\s\\S]+\\[[- ]+\\]$)|(^\\[[- a-z\\?]+\\][\\s\\S]+$)|(^[\\s\\S]+\\[[- a-z\\?]+\\]$)")
								|| t.contains("+") || t.contains("-") || t.contains("&#60;") || t.contains("&#62;")
								|| t.contains("<") || t.contains(">")) {

							String[] miscD = { data[0], t };
							misc.add(miscD);
						} else {
							String cleanedTerm = cleanTerm(t, data[0]);

							if (cleanedTerm.contains("?")) {
								String[] q = { data[0], cleanedTerm };
								questionMark.add(q);
							}

							else {
								String[] termD = { data[0], cleanedTerm };
								termData.add(termD);
							}
						}
					}
				}

			}

			csvReader.close();
		}

		catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String cleanTerm(String term, String EDR) {

		term = term.replaceAll("\u0323", "").replaceAll("\u0332", "").replaceAll("\u0302", "").replaceAll("\u0331", "");

		// Replace nonstandard characters (Some of these look the same, but they AREN'T)
		term = term.replace("ì", "i").replace("Ì", "I").replace("ì", "i").replace("í", "i").replace("Í", "I");

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
				String[] x = { EDR, term };
				correctSpelling.add(x);
				term = spellMatcher.group(1); // .replaceAll("\\?", "");
			}
		}

		term = term.replace("(", "").replace(")", "").replace(":", "");

		term = term.replace("`", "").replace("´", "");

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

	private static void addToFile(String file, ArrayList<String[]> data) throws IOException {
		FileWriter csvWriter = new FileWriter("src/main/resources/indexing/" + file + ".csv", true);
		for (String[] d : data) {
			csvWriter.append(String.join(",", d));
			csvWriter.append("\n");
		}

		csvWriter.close();
	}
}
