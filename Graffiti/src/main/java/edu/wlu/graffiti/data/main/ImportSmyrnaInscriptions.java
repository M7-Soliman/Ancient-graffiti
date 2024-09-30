package edu.wlu.graffiti.data.main;

import edu.wlu.graffiti.data.setup.InsertSmyrnaData;
import edu.wlu.graffiti.data.setup.ReadFromEpidoc;

/**
 * 
 * @author Trevor Stalnaker
 * 
 * Runs scripts necessary to add Smyrna data to the database
 *
 */
public class ImportSmyrnaInscriptions {
	
	public static void main(String[] args) {
		ReadFromEpidoc.main(args);
		InsertSmyrnaData.main(args);	
	}	
}
