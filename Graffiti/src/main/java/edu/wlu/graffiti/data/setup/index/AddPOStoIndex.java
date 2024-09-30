package edu.wlu.graffiti.data.setup.index;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.wlu.graffiti.data.main.DBInteraction;
//import edu.wlu.graffiti.data.export.ExportTerms;
import edu.wlu.graffiti.data.setup.Utils;

/**
 * 
 * @author Trevor Stalnaker
 * @editor Grace MacDonald
 * 
 * Finds part of speech information for terms and adds it to the database
 *
 */
public class AddPOStoIndex extends DBInteraction {
	
	private static final String POS_CSV_LOCATION = "data/AGPData/pos.csv";
	
	private static final String GET_LATIN_TERMS = "SELECT term_id, term FROM terms WHERE (category='terms' OR "
			+ "category='figural-terms') AND language='latin' AND part_of_speech='place_holder' ORDER BY term ASC";
	
	private static final String UPDATE_POS = "UPDATE terms SET part_of_speech=? WHERE term_id=?";
	
	private static final String UPDATE_POS_BY_TERM = "UPDATE terms SET part_of_speech=? WHERE term=?";
	
	private static HashMap<String, String> posMap = new HashMap<String, String>();
	

	
	public static void main(String[] args) {
		AddPOStoIndex ap = new AddPOStoIndex();
		ap.initializePosMap();
		
		ap.runDBInteractions();
		//ap.applyAGPPOS();
		//It seems like this may be partially broken
		// Perhaps the wordnet was updated
		//setPosWithWordNet(); 
//		try {
//			// Export Terms for Python File
//			System.out.println("Exporting Terms...");
//			ExportTerms.main(args);
//			// Feed Terms to the Python File
//			System.out.println("Getting Part of Speech Info...");
//			getPos();
//			// Add Part of Speech Info to Database
//			System.out.println("Saving Part of Speech Info...");
//			addPos();
//			System.out.println("Part of Speech Import Done!");
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		}
	}
	
	@Override
	public void run() {
		applyAGPPOS();
	}
	
	public void applyAGPPOS() {
		try {
			//Create Prepared Statement
			PreparedStatement pstmt = dbCon.prepareStatement(UPDATE_POS_BY_TERM);
			
			//Read in the data from the CSV File
			Reader in;	
			in = new FileReader(POS_CSV_LOCATION);
			Iterable<CSVRecord> records = CSVFormat.EXCEL.parse(in);
			for (CSVRecord record : records) {
				//Clean data in CSV File and save to Strings
				String term = Utils.cleanData(record.get(0));
				String pos = Utils.cleanData(record.get(1));
				pstmt.setString(1, pos);
				pstmt.setString(2, term);
				pstmt.executeUpdate();
			}
			
			pstmt.close();
		}
		catch (IOException | SQLException e) {}
	}
	
	public void setPosWithWordNet() {
		try {
			PreparedStatement pstmt = dbCon.prepareStatement(UPDATE_POS);
			
			//Get all of the terms from the database
			Statement stmt = dbCon.createStatement();
			ResultSet rs = stmt.executeQuery(GET_LATIN_TERMS);
			
			while (rs.next()) {
				String morpho;
				int term_id = rs.getInt("term_id");
				String term = rs.getString("term");
				String url = "https://latinwordnet.exeter.ac.uk/lemmatize/" + term + "/?format=json";
				try {
					// Read in the json information from the given url
					BufferedReader in = new BufferedReader(new InputStreamReader(new URL(url).openStream()));
					String json = "";
					String input;
			        while ((input = in.readLine()) != null) {json += "\n" +(input);}
			        in.close();
			        // Convert the grabbed json string to a tree of nodes
			        json = json.substring(2, json.length()-1); 
			        JsonNode rootNode = new ObjectMapper().readTree(json);
			        JsonNode lemmaNode = rootNode.path("lemma");
			        morpho = lemmaNode.path("morpho").asText();
			     
			        // Check if morphological information was found
			        if (!morpho.equals("")) {
			        	
			        	// Find the part of speech from the mapping
			        	String pos = Character.toString(morpho.charAt(0));
			        	String part_of_speech = posMap.get(pos);
			        	
			        	// Update the database
			        	pstmt.setString(1, part_of_speech);
			        	pstmt.setInt(2, term_id);
			        	pstmt.executeUpdate();
			        }
			        else {
			        	//System.out.println("No Morphological information found for " + term);
			        }
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			pstmt.close();
			stmt.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}

	private void initializePosMap() {
		List<String> keys = Arrays.asList("n","v","t","a","d","c","r","p","m","i","e","u");
		List<String> values = Arrays.asList("noun","verb","participle","adjective","adverb",
				"conjunction","preposition","pronoun","numeral","interjection","exclamation",
				"punctuation");
		for (int i=0; i < keys.size(); i++) {
			posMap.put(keys.get(i), values.get(i));
		}
	}	
}
