/**
 * 
 */
package edu.wlu.graffiti.test;

import edu.wlu.graffiti.data.main.ImportEDRData;

/**
 * @author sprenkle
 *
 */
public class TestExtractFindSpots {
	
	public static void main(String[] args) {
		ImportEDRData.main(args);
		
		String address = ImportEDRData.convertFindSpotToAddress("Lupanare, cella d, (VII.12.18-20)");
		System.out.println(address);
	}

}
