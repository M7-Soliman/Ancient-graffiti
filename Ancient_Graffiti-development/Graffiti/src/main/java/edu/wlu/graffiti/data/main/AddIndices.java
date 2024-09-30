package edu.wlu.graffiti.data.main;

import edu.wlu.graffiti.data.setup.index.AddPOStoIndex;
import edu.wlu.graffiti.data.setup.index.InsertIndexEntries;

/**
 * Runs scripts necessary to add indices to the database
 * 
 * @author Trevor Stalnaker
 */
public class AddIndices {

	public static void main(String[] args) {
		System.out.println("Adding Indices...");
		InsertIndexEntries.main(args);
		System.out.println("Indices Added");
		AddPOStoIndex.main(args);
	}
}
