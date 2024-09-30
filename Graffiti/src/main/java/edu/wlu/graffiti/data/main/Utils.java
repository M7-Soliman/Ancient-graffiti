package edu.wlu.graffiti.data.main;

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

	public static Properties getConfigurationProperties() {
		InputStream inputStream = null;
		Properties prop = null;
		try {
			prop = new Properties();
			String propFileName = "configuration.properties";
			inputStream = Utils.class.getClassLoader().getResourceAsStream(propFileName);
			if( inputStream == null ) {
				System.err.println("Error: likely property file '" + propFileName + "' not found in the classpath");
				throw new Exception("Error: likely property file '" + propFileName + "' not found in the classpath");
			}
			prop.load(inputStream);
			inputStream.close();
		} catch (Exception e) {
			System.err.println("Exception: " + e);
			e.printStackTrace();
		}

		return prop;
	}
}
