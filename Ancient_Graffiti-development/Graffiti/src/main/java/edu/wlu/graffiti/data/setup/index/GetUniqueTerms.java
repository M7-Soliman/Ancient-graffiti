package edu.wlu.graffiti.data.setup.index;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Takes the cleanedTerm CSV and writes a txt file with the unique terms to make
 * lemmatizing faster
 * 
 * @author Grace MacDonald
 */
public class GetUniqueTerms {

	private static final String INDEX_RESOURCE_DIR = "/src/main/resources/indexing/";
	private static String uniqueTerms = "";

	// Runs from main to mimic the flow of similar classes
	public static void main(String[] args) {
		String city = args[0];

		try {
			getTerms(city + "CleanedTerms.csv");
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			printToFile(city + "UniqueTerms", uniqueTerms);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void getTerms(String file) throws IOException {
		try (BufferedReader csvReader = new BufferedReader(
				new FileReader(System.getProperty("user.dir") + INDEX_RESOURCE_DIR + file))) {
			String row;
			while ((row = csvReader.readLine()) != null) {
				String[] temp = row.split("\n");
				String[] data = temp[0].split(",");
				if (data.length == 2) {
					String content = data[1];

					if (!uniqueTerms.contains(content)) {
						uniqueTerms += content + "\n";
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private static void printToFile(String file, String str) throws FileNotFoundException {
		PrintWriter writer = new PrintWriter(INDEX_RESOURCE_DIR + file + ".txt");
		writer.print(str);
		writer.close();
	}
}
