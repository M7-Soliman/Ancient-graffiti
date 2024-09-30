/**
 * 
 */
package edu.wlu.graffiti.data.setup;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author sprenkle
 *
 */
public class Utils {

	/**
	 * Cleans the data coming from the CSV file, removing the quotes and the
	 * extra spaces.
	 * 
	 * @param string
	 * @return
	 */
	public static String cleanData(String string) {
		return string.replace("\"", "").trim();
	}
	
	//Standardize the names of the drawing categories
	public static String standardizeDrawingNames(String name) {
		String retString = "";
		if (!name.equals("")) {
			String[] temp = name.split(" ");
			for (int i = 0; i < temp.length; i++ ) {
				retString += temp[i].substring(0,1).toLowerCase() + temp[i].substring(1);
				if (i < temp.length-1) {
					retString += " ";
				}
			}
			retString = retString.substring(0,1).toUpperCase() + retString.substring(1);
			if (!retString.equals("Other") && (retString.charAt(retString.length()-1) != 's')) {
				retString += "s";
			}
		}
		return retString;
	}

	public static Properties getConfigurationProperties() {
		InputStream inputStream = null;
		Properties prop = null;
		//System.out.println("Stage 1 utils");
		try {
			prop = new Properties();
			String propFileName = "configuration.properties";
			//System.out.println("Stage 2 utils");
			inputStream = Utils.class.getClassLoader().getResourceAsStream(propFileName);
			//System.out.println("Stage 2.5 util");
			//System.out.println(inputStream);
			prop.load(inputStream);
			//System.out.println(prop);
			inputStream.close();
			//System.out.println("Stage 3 utils");
		} catch (FileNotFoundException f) {
			System.err.println("property file 'configuration.properties' not found in the classpath");
		} catch (Exception e) {
			System.out.println("Exception: " + e);
		}

		return prop;
	}

}
