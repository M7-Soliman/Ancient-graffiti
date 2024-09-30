package edu.wlu.graffiti.data.main;

import edu.wlu.graffiti.data.setup.maps.CreateAGPJSON;
import edu.wlu.graffiti.data.setup.maps.CreateColumnJSFilesForMap;
import edu.wlu.graffiti.data.setup.maps.CreateDisplayOnlyJSFilesForMap;
import edu.wlu.graffiti.data.setup.maps.CreateInsulaJSFilesForMap;
import edu.wlu.graffiti.data.setup.maps.CreatePropertyJSFilesForMap;
import edu.wlu.graffiti.data.setup.maps.CreateStreetJSFilesForMap;
import edu.wlu.graffiti.data.setup.maps.CreateStreetSectionJSFilesForMap;

/**
 * Runs scripts necessary to create our map files
 * 
 * @author Trevor Stalnaker
 */
public class MakeMaps {

	public static void main(String[] args) {

		System.out.println("Generating JS Files from GeoJson...");
		CreateAGPJSON.main(args);
		CreateDisplayOnlyJSFilesForMap.main(args);
		CreatePropertyJSFilesForMap.main(args);
		CreateInsulaJSFilesForMap.main(args);
		CreateStreetJSFilesForMap.main(args);
		CreateStreetSectionJSFilesForMap.main(args);
		CreateColumnJSFilesForMap.main(args);
		System.out.println("JS File Generation Complete!");

	}
}