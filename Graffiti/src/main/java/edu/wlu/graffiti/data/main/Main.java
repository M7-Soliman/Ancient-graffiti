package edu.wlu.graffiti.data.main;

/**
 * Script to generate the data and maps for AGP
 */
public class Main {

	public static void main(String[] args) {
		ResetData.main(args);
		System.out.println("Setting up AGP data: ");
		PopulateDefaultDataInDatabase.main(args);
		PopulateDatabase.main(args);
		// Only index terms left after the database has been cleaned
		// AddIndices.main(args);
		AddInscriptionsToElasticSearch.main(args);
		MakeMaps.main(args);
		System.out.println("Done setting up application's data and maps!");
	}
}