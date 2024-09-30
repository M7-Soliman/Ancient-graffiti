package edu.wlu.graffiti.data.export;

import java.io.FileReader;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

import edu.wlu.graffiti.data.setup.Utils;

/**
 * Class that exports facade data
 * 
 * @author Trevor Stalnaker
 */
public class ExportFacadeData {

	private static final Object[] FILE_HEADER = { "EDR ID", "Locus Inventionis" };

	private static final String NEW_LINE_SEPARATOR = "\n";

	public static void main(String[] args) {
		readFacadeFromEDR();
	}

	private static void readFacadeFromEDR() {

		try {

			System.out.println("Reading in EDR Data from file...");

			// Set up the writer to export to csv
			PrintWriter writer = new PrintWriter("data/AGPData/facades.csv");

			StringBuilder stringBuilder = new StringBuilder();
			CSVPrinter csvFilePrinter = null;
			CSVFormat csvFileFormat = CSVFormat.DEFAULT.withRecordSeparator(NEW_LINE_SEPARATOR);

			csvFilePrinter = new CSVPrinter(stringBuilder, csvFileFormat);
			csvFilePrinter.printRecord(FILE_HEADER);

			// Read in the data from the CSV File

			Reader in = new FileReader("data/EDRData/epigr.csv");

			Iterable<CSVRecord> records = CSVFormat.EXCEL.parse(in);

			for (CSVRecord record : records) {

				// Clean data in CSV File and save to Strings

				String edr_id = Utils.cleanData(record.get(0));

				String location = Utils.cleanData(record.get(5));

				Matcher facade_matcher = Pattern.compile("[Ff]a[cç]ade[s]?").matcher(location);
				if (facade_matcher.find()) {

					List<Object> termRecord = new ArrayList<Object>();

					// fill in the fields
					termRecord.add(edr_id);
					termRecord.add(location);

					// write the inscription record
					csvFilePrinter.printRecord(termRecord);
				}
			}

			csvFilePrinter.close();
			writer.print(stringBuilder.toString());

			System.out.println("Facade Data Successfully Exported...");

			// Close statements, connections, and file readers
			in.close();
			writer.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
