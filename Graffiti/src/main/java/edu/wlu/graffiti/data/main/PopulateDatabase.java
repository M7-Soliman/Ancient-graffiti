/**
 * 
 */
package edu.wlu.graffiti.data.main;

import edu.wlu.graffiti.data.setup.InsertStreetsAndSegments;
import edu.wlu.graffiti.data.setup.RemoveSegmentsWithNoGraffiti;
import edu.wlu.graffiti.data.setup.StoreAllExistingEDRNumbers;

/**
 * Populate the database with the (not-fixed) data.
 * 
 * @author Sara Sprenkle
 */
public class PopulateDatabase {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		InsertCitiesAndInsula.main(args);
		InsertProperties.main(args);
		InsertStreetsAndSegments.main(args);
		
		ImportEDRData.main(args);
		ConvertEDRToAGP.main(args);
		
		ImportSmyrnaInscriptions.main(args);
		UpdateAGPInfo.main(args);
		
		// Clean up the database and remove sections with no graffiti
		RemoveSegmentsWithNoGraffiti.main(args);
		
		// If you want to get rid of the inscriptions that don't have findspots
		CleanUpDBForDeployment.main(args);
		
		// Store a list of all existing EDR IDs (for future reference)
		StoreAllExistingEDRNumbers.main(args);
		
		
		System.out.println("Populating Done!");
	}

}
