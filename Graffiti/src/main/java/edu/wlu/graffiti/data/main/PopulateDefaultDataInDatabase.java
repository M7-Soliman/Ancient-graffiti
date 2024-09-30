/**
 * 
 */
package edu.wlu.graffiti.data.main;

import edu.wlu.graffiti.data.setup.InsertColumns;
import edu.wlu.graffiti.data.setup.index.PrepareIndexData;

/**
 * Populate the database with the data that we don't expect to change (much).
 * This includes the property types, drawing tags, and the featured graffiti themes
 * 
 * @author Sara Sprenkle
 */
public class PopulateDefaultDataInDatabase {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		InsertPropertyTypes.main(args);
		InsertDrawingTags.main(args);
		InsertThemes.main(args);
		//PrepareIndexData.main(args);
		InsertColumns.main(args);
		
		System.out.println("Populating default data done!");
	}

}
