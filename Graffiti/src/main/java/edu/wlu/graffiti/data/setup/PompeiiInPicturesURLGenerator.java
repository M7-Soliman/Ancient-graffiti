/**
 * 
 */
package edu.wlu.graffiti.data.setup;

import java.util.Map;
import java.util.TreeMap;

/**
 * Handles automatically generating the Pompeii In Pictures URL for properties
 */
public class PompeiiInPicturesURLGenerator {

	private static final Map<String, Integer> numerals = new TreeMap<String, Integer>();
	static {
		numerals.put("I", 1);
		numerals.put("II", 2);
		numerals.put("III", 3);
		numerals.put("IV", 4);
		numerals.put("V", 5);
		numerals.put("VI", 6);
		numerals.put("VII", 7);
		numerals.put("VIII", 8);
		numerals.put("IX", 9);
		numerals.put("X", 10);
	}

	/**
	 * Precondition: Call only on Pompeii graffiti
	 */
	public static String generatePIPURL(String regio, String insulaNum, String propertyNumber) {

		String region = String.valueOf(numerals.get(regio));

		if (insulaNum.length() == 1)
			insulaNum = "0" + insulaNum;

		if (propertyNumber.length() == 1)
			propertyNumber = "0" + propertyNumber;

		return "https://pompeiiinpictures.com/pompeiiinpictures/R" + region + "/" + region + " " + insulaNum + " "
				+ propertyNumber + ".htm";
	}

	/**
	 * Convert a Roman numeral to the associated number
	 * 
	 * @param romanNumeral the Roman numeral to convert
	 * @return
	 */
	public static int convertRomanNumberToNumber(String romanNumeral) {
		if (numerals.containsKey(romanNumeral)) {
			return numerals.get(romanNumeral);
		}
		return 0;
	}
	
	public static void main(String[] args) {
		// Test the URL generator
		
		String url = generatePIPURL("I", "8", "19");
		System.out.println(url);
	}
}
