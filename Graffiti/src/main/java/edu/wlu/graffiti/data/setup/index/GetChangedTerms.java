package edu.wlu.graffiti.data.setup.index;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * This class takes results of fully processing the names and creates a table to
 * simulate results of new end lemmatization process
 * 
 * @author Grace MacDonald
 */
public class GetChangedTerms {

	private static String output = "";

	public static void main(String[] args) {
		try {
			getChanged("lemlatNamesProcessed.csv");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			getChanged("lemlatBaseProcessed.csv");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			printToFile("changedTerms", output);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void getChanged(String file) throws IOException {

		try (BufferedReader csvReader = new BufferedReader(new FileReader(
				System.getProperty("user.dir") + "/src/main/resources/indexing/processed_terms/" + file))) {
			String row;
			while ((row = csvReader.readLine()) != null) {
				String[] temp = row.split("\n");
				String[] data = temp[0].split(",");

				if (!data[1].equalsIgnoreCase(data[2])) {
					output = output + "\n" + data[1] + " " + data[2] + " " + data[3];
//					System.out.println("OG: " + data[1] + " PROCESSED: " + data[2]);

				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private static void printToFile(String file, String str) throws FileNotFoundException {
		PrintWriter writer = new PrintWriter("src/main/resources/indexing/" + file + ".txt");

		writer.print("Input, processed, lemma\n");
		writer.print(str);
		writer.close();
	}
}
