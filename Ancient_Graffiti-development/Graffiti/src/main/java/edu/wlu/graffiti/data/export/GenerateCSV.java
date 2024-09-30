/**
 * 
 */
package edu.wlu.graffiti.data.export;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.text.StringEscapeUtils;

import edu.wlu.graffiti.bean.Inscription;
import edu.wlu.graffiti.bean.PoeticInfo;
import edu.wlu.graffiti.bean.Property;
import edu.wlu.graffiti.bean.Segment;
import edu.wlu.graffiti.bean.Street;

/**
 * This class serializes Inscription objects and returns a string in CSV format to represent
 * the objects.
 * 
 * @author Hammad Ahmad
 * @author Trevor Stalnaker
 * @editor Ana Estrada
 * @editor Grace MacDonald
 *
 */
public class GenerateCSV {
	
	private static final String NEW_LINE_SEPARATOR = "\n";
	
	// the fields	
	private static final Object[] FILE_HEADER = {"agpId","cityName","cityPleiadesId","content",
			"sourceFindspot", "dateBeginning", "dateEnd","languageInEnglish","writingStyleInEnglish"};
	
	private static final Object[] FILE_HEADER_PROPERTY = {"city", "insula", " number", "name",
			"type", "link"};
	
	private static final Object[] FILE_HEADER_SEGMENT = {"city","street","name","link"};
	
	private static final Object[] FILE_HEADER_STREET = {"city","street","link"};
	
	private static final Object[] FILE_HEADER_MISSING_INFO = {"EDR", "CIL", "Caption", "Content", "Translation", "Bibliography"};
		
	private static final Object[] FILE_HEADER_MISSING_FIGURAL_INFO = {"EDR", "CIL", "Langner", "Caption", "Content", 
			"Latin Description", "English Description", "Bibliography"};
	
	private static final Object[] FILE_HEADER_MISSING_FIGURAL = {"EDR", "CIL", "Langner", "Content", "Caption", "Ancient City"};
	
	private static final Object[] FILE_HEADER_LANGNER = {"EDR", "CIL", "Langner", "Description", "Translation", "Address"};
	
	private static final Object[] FILE_HEADER_POETRY = {"EDR", "Text", "Apparatus", "Meter", "Author" };
	
	private static final Object[] FILE_HEADER_FIGURAL = {"EDR", "CIL", "Langner", "Ancient city","English Property Name", 
			"Figural Category", "Caption", "Content", "Description in Latin", "Description in English", "Address", "Commentary", "Contributors"};
	
	private static final Object[] FILE_HEADER_TEXTUAL = {"EDR", "CIL", "Langner", "Caption", "Content", 
			"Translation", "Address", "Commentary", "Contributors"};
	
	private static final Object[] FILE_HEADER_MISSING_FINDSPOT = {"EDR", "CIL", "Content", "Findspot"};
	
	private static final Object[] FILE_HEADER_FIGURAL_CAPTION_OCCURRENCES = {"Caption", "Occurrences"};
	
	//citation
	private static final Object[] CITATION = {"Citation: The Ancient Graffiti Project, http://ancientGraffiti.org/ [accessed: " + new java.text.SimpleDateFormat("dd MMM yyyy").format(new java.util.Date()) + "]"};
	
	/**
	 * Serializes a list of inscriptions to CSV.
	 * 
	 * @param inscriptions The list of inscription
	 * @return the string representation in CSV format
	 */
	public String serializeInscriptionsToCSV(List<Inscription> inscriptions) {
		
		StringBuilder stringBuilder = new StringBuilder();
		CSVPrinter csvFilePrinter = null;
		
		CSVFormat csvFileFormat = CSVFormat.DEFAULT.withRecordSeparator(NEW_LINE_SEPARATOR);
		
		try {
			csvFilePrinter = new CSVPrinter(stringBuilder, csvFileFormat);
			csvFilePrinter.printRecord(CITATION);
			csvFilePrinter.printRecord(FILE_HEADER);
			for(Inscription i : inscriptions) {
				writeInscriptionToCSV(i, csvFilePrinter);
			}
			csvFilePrinter.close();
			
			return stringBuilder.toString();
			
		} catch (IOException e) {
			e.printStackTrace();
		} 
		
		return "";
	}
	/**
	 * Serializes an inscription to CSV.
	 * 
	 * @param i The inscription
	 * @return the string representation in CSV format
	 */
	public String serializeToCSV(Inscription i) {
		
		StringBuilder stringBuilder = new StringBuilder();
		CSVPrinter csvFilePrinter = null;
		
		CSVFormat csvFileFormat = CSVFormat.DEFAULT.withRecordSeparator(NEW_LINE_SEPARATOR);
		
		try {
			csvFilePrinter = new CSVPrinter(stringBuilder, csvFileFormat);
			csvFilePrinter.printRecord(CITATION);
			csvFilePrinter.printRecord(FILE_HEADER);
			writeInscriptionToCSV(i, csvFilePrinter);
			csvFilePrinter.close();
			
			return stringBuilder.toString();
			
		} catch (IOException e) {
			e.printStackTrace();
		} 
		
		return "";	
	}

	/**
	 * Writes individual fields from an inscription to the CSV export.
	 * 
	 * @param i The inscription
	 * @param csvFilePrinter The file printer
	 * @throws IOException
	 */
	private void writeInscriptionToCSV(Inscription i, CSVPrinter csvFilePrinter) throws IOException {
		
		List<Object> inscriptionRecord = new ArrayList<Object>();
		
		// fill in the fields
		inscriptionRecord.add(i.getAgpId());
		if(i.getOnFacade()) {
			if (i.getSegment()==null) {
				inscriptionRecord.add(i.getAncientCity());
				inscriptionRecord.add("");
			}
			else {
				inscriptionRecord.add(i.getSegment().getStreet().getCity().getName());
				inscriptionRecord.add(i.getSegment().getStreet().getCity().getPleiadesId());
			}
		}else {
			inscriptionRecord.add(i.getProperty().getInsula().getCity().getName());
			inscriptionRecord.add(i.getProperty().getInsula().getCity().getPleiadesId());
		}
		inscriptionRecord.add(i.getContent());
		inscriptionRecord.add(i.getSourceFindSpot());
		inscriptionRecord.add(i.getDateBeginning());
		inscriptionRecord.add(i.getDateEnd());
		inscriptionRecord.add(i.getLanguageInEnglish());
		inscriptionRecord.add(i.getWritingStyleInEnglish());
		
		// not adding these fields for now... might change in the future
		/**
		inscriptionRecord.add(i.getGraffitiId());
		inscriptionRecord.add(i.getBibliography());
		inscriptionRecord.add(i.getApparatus());
		inscriptionRecord.add(i.getCaption());
		inscriptionRecord.add(i.getCommentary());
		inscriptionRecord.add(i.getContentTranslation());
		inscriptionRecord.add(i.getWritingStyleInEnglish());
		inscriptionRecord.add(i.getLanguageInEnglish());
		inscriptionRecord.add(i.getGraffitoHeight());
		inscriptionRecord.add(i.getGraffitoLength());
		inscriptionRecord.add(i.getMinLetterHeight());
		inscriptionRecord.add(i.getMaxLetterHeight());
		inscriptionRecord.add(i.getMinLetterWithFlourishesHeight());
		inscriptionRecord.add(i.getMaxLetterWithFlourishesHeight());
		inscriptionRecord.add(i.getCil());
		inscriptionRecord.add(i.getLangner());
		inscriptionRecord.add(i.getProperty().getPropertyNumber());
		inscriptionRecord.add(i.getProperty().getPropertyName());
		inscriptionRecord.add(i.getProperty().getPleiadesId());
		inscriptionRecord.add(i.getProperty().getItalianPropertyName());
		inscriptionRecord.add(i.getProperty().getCommentary());
		inscriptionRecord.add(i.getProperty().getInsula().getShortName());
		inscriptionRecord.add(i.getProperty().getInsula().getFullName());
		inscriptionRecord.add(i.getProperty().getInsula().getCity().getDescription());
		inscriptionRecord.add(Integer.toString(i.getId()));
		inscriptionRecord.add(i.getAncientCity());
		inscriptionRecord.add(i.getFindSpot());
		inscriptionRecord.add(Integer.toString(i.getFindSpotPropertyID()));
		inscriptionRecord.add(i.getMeasurements());
		inscriptionRecord.add(i.getLanguage());
		inscriptionRecord.add(i.getWritingStyle());
		inscriptionRecord.add(i.getApparatusDisplay());
		inscriptionRecord.add(Integer.toString(i.getNumberOfImages()));
		*/
		
		// write the inscription record
		csvFilePrinter.printRecord(inscriptionRecord);
	}
	
	/**
	 * Serializes a list of properties to CSV.
	 * 
	 * @param properties The list of properties
	 * @return the string representation in CSV format
	 */
	public String serializePropertiesToCSV(List<Property> properties) {
		
		StringBuilder stringBuilder = new StringBuilder();
		CSVPrinter csvFilePrinter = null;
		
		CSVFormat csvFileFormat = CSVFormat.DEFAULT.withRecordSeparator(NEW_LINE_SEPARATOR);
		
		try {
			csvFilePrinter = new CSVPrinter(stringBuilder, csvFileFormat);
			csvFilePrinter.printRecord(CITATION);
			csvFilePrinter.printRecord(FILE_HEADER_PROPERTY);
			for(Property p : properties) {
				writePropertyToCSV(p, csvFilePrinter);
			}
			csvFilePrinter.close();
			
			return stringBuilder.toString();
			
		} catch (IOException e) {
			e.printStackTrace();
		} 
		
		return "";
	}
	
	public String serializeSegmentsToCSV(List<Segment> segments) {
		
		StringBuilder stringBuilder = new StringBuilder();
		CSVPrinter csvFilePrinter = null;
		
		CSVFormat csvFileFormat = CSVFormat.DEFAULT.withRecordSeparator(NEW_LINE_SEPARATOR);
		
		try {
			csvFilePrinter = new CSVPrinter(stringBuilder, csvFileFormat);
			csvFilePrinter.printRecord(CITATION);
			csvFilePrinter.printRecord(FILE_HEADER_SEGMENT);
			for(Segment s : segments) {
				writeSegmentToCSV(s, csvFilePrinter);
			}
			csvFilePrinter.close();
			
			return stringBuilder.toString();
			
		} catch (IOException e) {
			e.printStackTrace();
		} 
		
		return "";
	}
	
	/**
	 * Serializes a property to CSV.
	 * 
	 * @param p The property
	 * @return the string representation in CSV format
	 */
	public String serializeToCSV(Property p) {
		
		StringBuilder stringBuilder = new StringBuilder();
		CSVPrinter csvFilePrinter = null;
		
		CSVFormat csvFileFormat = CSVFormat.DEFAULT.withRecordSeparator(NEW_LINE_SEPARATOR);
		
		try {
			csvFilePrinter = new CSVPrinter(stringBuilder, csvFileFormat);
			csvFilePrinter.printRecord(CITATION);
			csvFilePrinter.printRecord(FILE_HEADER_PROPERTY);
			writePropertyToCSV(p, csvFilePrinter);
			csvFilePrinter.close();
			
			return stringBuilder.toString();
			
		} catch (IOException e) {
			e.printStackTrace();
		} 
		
		return "";	
	}
	
public String serializeStreetsToCSV(List<Street> streets) {
		
		StringBuilder stringBuilder = new StringBuilder();
		CSVPrinter csvFilePrinter = null;
		
		CSVFormat csvFileFormat = CSVFormat.DEFAULT.withRecordSeparator(NEW_LINE_SEPARATOR);
		
		try {
			csvFilePrinter = new CSVPrinter(stringBuilder, csvFileFormat);
			csvFilePrinter.printRecord(CITATION);
			csvFilePrinter.printRecord(FILE_HEADER_STREET);
			for(Street s : streets) {
				writeStreetToCSV(s, csvFilePrinter);
			}
			csvFilePrinter.close();
			
			return stringBuilder.toString();
			
		} catch (IOException e) {
			e.printStackTrace();
		} 
		
		return "";
	}

	/**
	 * Writes individual fields from an property to the CSV export.
	 * 
	 * @param p The property
	 * @param csvFilePrinter The file printer
	 * @throws IOException
	 */
	private void writePropertyToCSV(Property p, CSVPrinter csvFilePrinter) throws IOException {
		
		List<Object> propertyRecord = new ArrayList<Object>();
		
		// fill in the fields
		propertyRecord.add(p.getInsula().getCity().getName());
		propertyRecord.add(p.getInsula().getShortName());
		propertyRecord.add(p.getPropertyNumber());
		propertyRecord.add(p.getPropertyName());
		propertyRecord.add(p.getPropertyTypesAsString());
		propertyRecord.add(p.getUri());
		
		// write the inscription record
		csvFilePrinter.printRecord(propertyRecord);
	}
	
	private void writeSegmentToCSV(Segment s, CSVPrinter csvFilePrinter) throws IOException {
		
		List<Object> segRecord = new ArrayList<Object>();
		
		// fill in the fields
		segRecord.add(s.getStreet().getCity().getName());
		segRecord.add(s.getStreet().getStreetName());
		segRecord.add(s.getSegmentName());
		segRecord.add("ancientgraffiti.org/Graffiti/segments/" + s.getUri());
		
		// write the inscription record
		csvFilePrinter.printRecord(segRecord);
	}
	
	private void writeStreetToCSV(Street s, CSVPrinter csvFilePrinter) throws IOException {
		
		List<Object> segRecord = new ArrayList<Object>();
		
		// fill in the fields
		segRecord.add(s.getCity().getName());
		segRecord.add(s.getStreetName());
		segRecord.add("ancientgraffiti.org/Graffiti/segments/" + s.getUri());
		
		// write the inscription record
		csvFilePrinter.printRecord(segRecord);
	}
	
	public String abstractSerializeAdminCSV(Object[] file_header, List<List<String>> data) {
		try {
			StringBuilder stringBuilder = new StringBuilder();
			CSVPrinter csvFilePrinter = null;
			CSVFormat csvFileFormat = CSVFormat.DEFAULT.withRecordSeparator(NEW_LINE_SEPARATOR);
			csvFilePrinter = new CSVPrinter(stringBuilder, csvFileFormat);
			csvFilePrinter.printRecord(file_header);
			for (List<String> row : data) {
				// Unescape the HTML characters in every row to remove unseemly output like '&#7090;'
				List<String> temp = new ArrayList<String>();
				for (String s : row) {
					temp.add(StringEscapeUtils.unescapeHtml4(s));
				}
				csvFilePrinter.printRecord(temp);	
			}
			csvFilePrinter.close();
			return stringBuilder.toString();
		} catch (IOException e) {
			e.printStackTrace();
		} 
			return "";
	}
	
	// Admin CSV Exports dealing with missing or empty fields
	
	public String serializeMissingInfoToCSV(List<List<String>> data){
		return abstractSerializeAdminCSV(FILE_HEADER_MISSING_INFO, data);
	}
	
	public String serializeMissingFiguralInfoToCSV(List<List<String>> data){
		return abstractSerializeAdminCSV(FILE_HEADER_MISSING_FIGURAL_INFO, data);
	}
	
	public String serializeUnrecognizedFiguralToCSV(List<List<String>> data){
		return abstractSerializeAdminCSV(FILE_HEADER_MISSING_FIGURAL, data);
	}
	
	public String serializeLangnerInfoToCSV(List<List<String>> data){
		return abstractSerializeAdminCSV(FILE_HEADER_LANGNER, data);
	}
	public String serializePoetryToCSV(List<List<String>> data){
		return abstractSerializeAdminCSV(FILE_HEADER_POETRY, data);
	}
	
	
	public String serializeFiguralInscriptionsToCSV(List<List<String>> data){
		return abstractSerializeAdminCSV(FILE_HEADER_FIGURAL, data);
	}
	
	public String serializeTextualInscriptionsToCSV(List<List<String>> data){
		return abstractSerializeAdminCSV(FILE_HEADER_TEXTUAL, data);
	}
	
	public String serializeMissingFindspotToCSV(List<List<String>> data) {
		return abstractSerializeAdminCSV(FILE_HEADER_MISSING_FINDSPOT, data);
	}
	
	public String serializeFiguralCaptionsToCSV(List<List<String>> data) {
		return abstractSerializeAdminCSV(FILE_HEADER_FIGURAL_CAPTION_OCCURRENCES, data);
	}
}

