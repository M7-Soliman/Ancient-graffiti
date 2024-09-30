package edu.wlu.graffiti.data.setup.index;

/**
 * Inserts neccessary information into the database, for future indexing
 * 
 * @author Trevor Stalnaker
 *
 */
public class PrepareIndexData {
	
	public static void main(String args[]) {
		InsertNames.main(args);
		InsertPlaces.main(args);
		ImportLemmaTable.main(args);
	}

}
