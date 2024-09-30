package edu.wlu.graffiti.data.setup.index;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import edu.wlu.graffiti.data.main.DBInteraction;

/**
 * This class merges the lemma csvs with the term's EDR number and graffiti
 * content, to make it easier to asses
 * 
 * @author Grace MacDonald
 */
public class GetLemmaAndContent extends DBInteraction {

	private static final String GET_CONTENT = "SELECT content from inscriptions WHERE graffiti_id=?";
	private static PreparedStatement contentStmt;
	private static String city = "herc";

	// Runs from main to mimic the flow of similar classes
	public static void main(String[] args) {

		GetLemmaAndContent glc = new GetLemmaAndContent();
		glc.runDBInteractions();

	}

	public void run() {

		try {
			String lemBase = merge(city + "CleanedTerms.csv", city + "LEMLATBase.csv");

			try {
				printToCSV(city + "LEMLATBase_EDR", lemBase);
			} catch (IOException e) {
				e.printStackTrace();
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			String lemNames = merge(city + "CleanedTerms.csv", city + "LEMLATNames.csv");

			try {
				printToCSV(city + "LEMLATNames_EDR", lemNames);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			String cltk = merge(city + "CleanedTerms.csv", city + "CLTKLemma.csv");

			try {
				printToCSV(city + "CLTK_EDR", cltk);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			contentStmt = dbCon.prepareStatement(GET_CONTENT);

			String lemBase = findContent(city + "LEMLATBase_EDR.csv");

			try {
				printToCSV(city + "LEMLATBase_content", lemBase);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
		}

		try {
			contentStmt = dbCon.prepareStatement(GET_CONTENT);

			String lemNames = findContent(city + "LEMLATNames_EDR.csv");

			try {
				printToCSV(city + "LEMLATNames_content", lemNames);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
		}

		try {
			contentStmt = dbCon.prepareStatement(GET_CONTENT);

			String cltk = findContent(city + "CLTK_EDR.csv");

			try {
				printToCSV(city + "CLTK_content", cltk);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
		}

	}

	public static String findContent(String file) {
		String out = "";
		try (BufferedReader csvReader = new BufferedReader(
				new FileReader(System.getProperty("user.dir") + "/src/main/resources/indexing/" + file))) {

			String row;
			while ((row = csvReader.readLine()) != null) {
				String[] temp = row.split("\n");
				String[] data = temp[0].split(",");

				String edr = data[0];
				String content = lookupID(edr).replaceAll(",", "").replaceAll("\n", " ");
				System.out.println(content);
				out = out + row + "," + content + "\n";
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return out;
	}

	public static String lookupID(String edr) {
		String content = "";
		try {
			contentStmt.setString(1, edr);

			ResultSet c = contentStmt.executeQuery();
			if (c.next()) {
				content = c.getString(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return content;
	}

	public static String merge(String termFile, String lemmaFile) throws IOException {
		String output = "";
		try (BufferedReader csvReader = new BufferedReader(
				new FileReader(System.getProperty("user.dir") + "/src/main/resources/indexing/" + termFile))) {
			String row;
			while ((row = csvReader.readLine()) != null) {
				String[] temp = row.split("\n");
				String[] data = temp[0].split(",");

				try (BufferedReader read = new BufferedReader(
						new FileReader(System.getProperty("user.dir") + "/src/main/resources/indexing/" + lemmaFile))) {
					String rowTest;
					while ((rowTest = read.readLine()) != null) {
						String[] temp2 = rowTest.split("\n");
						String[] lemma = temp2[0].split(",");
						if (data[1].equals(lemma[0])) {

							output = output + data[0] + "," + rowTest + "\n";

						}

					}

				} catch (IOException e) {
					e.printStackTrace();
				}

			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		return output;
	}

	private static void printToCSV(String fileName, String data) throws IOException {
		FileWriter csvWriter = new FileWriter("src/main/resources/indexing/" + fileName + ".csv");

		csvWriter.append(data);

		csvWriter.close();
	}
}
