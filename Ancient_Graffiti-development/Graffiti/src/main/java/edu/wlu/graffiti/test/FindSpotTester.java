package edu.wlu.graffiti.test;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FindSpotTester {
	
	private static List<Pattern> patternList;
	
	private static String findspots = "    Ercolano (Napoli), Insula V.35, Casa del Gran Portale, facade \n" + 
			"\n" + 
			"    Ercolano (Napoli), Insula V.1, Casa Sannitica, facade\n" + 
			"\n" + 
			"    Ercolano (Napoli), R of Insula V.1, Facade\n" + 
			"\n" + 
			"    Pompei (Napoli), Regio VII.9.67, Aedificium Eumachiae, facade <-- this pattern should be flagged for the classicists to inspect\n" + 
			"\n" + 
			"    Pompei (Napoli), Regio VI.15.8-9, facade\n" + 
			"\n" + 
			"    Pompei (Napoli), Regio VII.9.1, facade\n" + 
			"\n" + 
			"    one number usually means it's on a corner; should say which side it's on\n" + 
			"\n" + 
			"    Pompei (Napoli), R of Regio IX.13.6, Facade\n" + 
			"\n" + 
			"    Pompei (Napoli), Regio V.2.d-c, Facade (R of V.2.d)\n" + 
			"\n" + 
			"    Pompei (Napoli), Regio VIII.5.19, facade\n" + 
			"\n" + 
			"    Pompei (Napoli), Regio VIII.5.7-8, facade\n" + 
			"\n" + 
			"    Pompei (Napoli), Regio VIII.5.23-24, facade\n" + 
			"\n" + 
			"    Pompei (Napoli), Regio II.7.1, Campus ad Amphitheatrum, R of 2.7.1, Facade";
	
	
	public static void main(String args[]) {
		patternList = new ArrayList<Pattern>();
		patternList.add(Pattern.compile("^.* \\((\\w*\\.\\w*.\\w*)\\)"));
		patternList.add(Pattern.compile("^\\w+ \\(\\w+\\),? ([\\w'.-]* )* ?\\(?([\\w',-\\.]*)\\)?(, [\\s\\w-,'.\\(\\)]*)?$"));
		//System.out.println(convertFindSpotToAddress("Ercolano (Napoli), Insula V.35, Casa del Gran Portale"));
		parseFindspot(findspots);
		// TODO: Need to update the pattern to handle Insula Orientalis I
		
		//System.out.print(convertFindSpotToAddress("Ercolano (Napoli), Insula VI.1,7-10, Terme Centrali (Terme Femminili)"));
	}
	
	private static void updateAGPMetadata(String edrId, String ancient_city, String findSpot) throws SQLException {

		if (findSpot.contains("facade") || findSpot.contains("Facade")) {
			System.err.println(edrId + " is on a facade...  Can't currently handle");
			return;
		}

		String address = convertFindSpotToAddress(findSpot);

		// TODO
		// we're going to skip these because I can't handle them yet.

		if (!address.contains(".")) {
			System.err.println(edrId + ": Couldn't handle address: " + address);
			return;
		}

		String insula = "";
		String propertyNum = "";

		if (ancient_city.equals("Pompeii")) {
			insula = address.substring(0, address.lastIndexOf('.'));
		} else {
			insula = address.substring(0, address.indexOf('.'));
		}
		propertyNum = address.substring(address.lastIndexOf('.') + 1);
		
	}

	
	private static String convertFindSpotToAddress(String findSpot) {
		// Example: Pompei (Napoli) VII.12.18-20, Lupanare, cella b
		// Example: Ercolano (Napoli), Insula III.11, Casa del Tramezzo di Legno

		// Hack to handle Insula Orientalis special addresses
		if (findSpot.contains("Insula Orientalis ")) {
			findSpot = findSpot.replace("Insula Orientalis ", "InsulaOrientalis");
		}

		Matcher matcher = patternList.get(0).matcher(findSpot);
		if (matcher.matches()) {
			return matcher.group(1);
		}

		matcher = patternList.get(1).matcher(findSpot);
		if (matcher.matches()) {
			return matcher.group(2);
		} else {
			return findSpot;
		}
	}
	
	public static String parseFindspot(String findspot) {
		Matcher matcher = Pattern.compile( 	"([^\\s]+[ ]\\([^\\s]+\\))\\,[ ]?(([R|L]) of[ ]?)?"
				+ "((Insula | Regio)[ ]?[^\\s\\,]+)\\,[ ]?([^\\,]+)?(\\,[ ]?([R|L] of [^\\s\\,]+)"
				+ "[ ]?)?[\\,]?[ ][f|F]acade([ ]\\(([R|L] of [^\\s]+)\\))?").matcher(findspot);
		while (matcher.find()){
			String city = matcher.group(1);
			String insula = matcher.group(4).trim();
			String relative_location= "None";
			String property_name = "None";
			if (matcher.group(3) != null) {
				relative_location = matcher.group(2) + insula;
			}
			if (matcher.group(8) != null) {
				relative_location = matcher.group(8);
			}
			if (matcher.group(10) != null) {
				relative_location = matcher.group(10);
			}
			if (matcher.group(6) != null) {
				property_name = matcher.group(6);
			}
			System.out.println("City: " + city);
			System.out.println("Insula/Regio: " + insula);
			System.out.println("Property Name: " + property_name);
			System.out.println("Relative Findspot: " + relative_location);
			System.out.println();
		}
		
		
		return "";
	}
	
	public static String getEDRDirectory() {
		//{i.edrId.substring(3,6)}/${i.FeaturedGraffitiInfo.preferredImage
		String image_id = "001023";//this.preferredImage.substring(3,6);
		int i = 0;
		while (image_id.charAt(i) == '0') {
			i++;
		}
		return image_id.substring(i) + "/";// + this.preferredImage;
	}
	
}
