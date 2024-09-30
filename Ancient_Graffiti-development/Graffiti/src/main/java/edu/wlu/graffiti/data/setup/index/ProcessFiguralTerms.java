package edu.wlu.graffiti.data.setup.index;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * This class is trying to obtain a list of terms which can be lemmatized and
 * given a part of speech
 * 
 * @author Grace MacDonald
 */
public class ProcessFiguralTerms {

	private static final String INDEX_RESOURCE_DIR = "/src/main/resources/indexing/";
	private static ArrayList<String[]> figTermWords = new ArrayList<String[]>();

	// Runs from main to mimic the flow of similar classes
	public static void main(String[] args) {
		String city = args[0];

		try {
			iterateThroughTerms(city + "Figural.csv");
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			printToCSV(city + "FiguralTermWords", figTermWords);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void iterateThroughTerms(String file) throws IOException {
		try (BufferedReader csvReader = new BufferedReader(
				new FileReader(System.getProperty("user.dir") + INDEX_RESOURCE_DIR + file))) {
			String row;
			while ((row = csvReader.readLine()) != null) {
				String[] temp = row.split("\n");
				String[] data = temp[0].split(",");

				String[] content = data[1].split(" ");

				for (String c : content) {
					String term = cleanFiguralTerm(c);
					String[] figD = { data[0], term };
					figTermWords.add(figD);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static String cleanFiguralTerm(String term) {

		term = term.replaceAll("[\\p{Punct}&&[^?]]", "");

		return term;
	}

	private static void printToCSV(String fileName, ArrayList<String[]> data) throws IOException {
		FileWriter csvWriter = new FileWriter(INDEX_RESOURCE_DIR + fileName + ".csv");

		for (String[] d : data) {
			csvWriter.append(String.join(",", d));
			csvWriter.append("\n");
		}

		csvWriter.close();
	}
}
