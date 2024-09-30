package edu.wlu.graffiti.test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Properties;

import edu.wlu.graffiti.data.setup.Utils;

public class FacadeTester {

	private static String INSERT_PROPERTIES_ON_STREET = "INSERT INTO property_on_streets (street_id, property_id, side_of_street) VALUES (?, ?, ?)";
	
	private static String INSERT_GRAFFITI_INTO_EDR = "INSERT INTO edr_inscriptions (edr_id) VALUES (?)";
	
	private static String INSERT_GRAFFITI_INTO_AGP = "INSERT INTO agp_inscription_info (edr_id, property_id, segment_id, on_facade) "
			+ "VALUES (?, ?, ?, true)";
	//private static String GET_PROPERTIES_ON_STREET = "SELECT property_id FROM property_on_streets WHERE street_id=?";
	
	//private static String GET_EDR_IDS_FOR_PROPERTY = "SELECT edr_id FROM agp_inscription_info WHERE property_id=? AND on_facade=true";	
	
	private static String DB_DRIVER;
	private static String DB_URL;
	private static String DB_USER;
	private static String DB_PASSWORD;
	static Connection newDBCon;
	
	public static void main(String args[]) {
		init();
		//insertPropertiesOnSegment();
		//insertGraffitiOnProperty("EDR444450", 428, 3);
		ArrayList<String> ids = getFacades();
		//ArrayList<String> ids = getFacadesOnStreet(1);
		ArrayList<String> ids_byStreet1 = getFacadesByStreet(1);
		ArrayList<String> ids_bySegmentA = getFacadesBySegment(1);
		ArrayList<String> ids_bySegmentB = getFacadesBySegment(3);
		ArrayList<String> ids_byStreet2 = getFacadesByStreet(2); //This is an imaginary street
		//ArrayList<String> ids_bySideOfStreet_left = getFacadesBySideOfTheStreet(1, "Left");
		//ArrayList<String> ids_bySideOfStreet_right = getFacadesBySideOfTheStreet(1, "Right");
		ArrayList<String> ids_bySideOfSegment_left = getFacadesBySideOfSegment(1, "Left");
		ArrayList<String> ids_bySideOfSegment_right = getFacadesBySideOfSegment(1, "Right");
		ArrayList<String> ids_bySideOfStreet_left = getFacadesBySideOfTheStreet(1, "Left");
		ArrayList<String> ids_bySideOfStreet_right = getFacadesBySideOfTheStreet(1, "Right");
		ArrayList<String> ids_byProperty = getFacadesByProperty(420);
		ArrayList<String> ids_byInsula = getFacadesByInsula(605);
		ArrayList<String> ids_byCity_pompeii = getFacadesByCity("Pompeii");
		ArrayList<String> ids_byCity_herc = getFacadesByCity("Herculaneum");
		ArrayList<Integer> propertiesWithFacades = getPropertiesWithFacades();
		ArrayList<Integer> streetsWithFacades = getStreetsWithFacades();
		ArrayList<Integer> insulaeWithFacades = getInsulaeWithFacades();
		ArrayList<Integer> segmentsWithFacades = getSegmentsWithFacades();
		ArrayList<String> citiesWithFacades = getCitiesWithFacades();
		System.out.println("All Graffiti on Facades: " + ids);
		System.out.println();
		System.out.println("Streets with Facades: " + streetsWithFacades);
		System.out.println("Segments with Facades: " + segmentsWithFacades);
		System.out.println("Cities with Facades: " + citiesWithFacades);
		System.out.println("Insulae with Facades: " + insulaeWithFacades);
		System.out.println("Properties with Facades: " + propertiesWithFacades);
		System.out.println();
		System.out.println("All Graffiti on Street 1: " + ids_byStreet1);
		System.out.println("All Graffiti on Street 2: " + ids_byStreet2);
		System.out.println("All Graffiti on Segment A: " + ids_bySegmentA);
		System.out.println("All Graffiti on Segment B: " + ids_bySegmentB);
		System.out.println("Graffiti on Left of Segemnt A: " + ids_bySideOfSegment_left);
		System.out.println("Graffiti on Right of Segment B: " + ids_bySideOfSegment_right);
		System.out.println("Graffiti on Left of Street 1: " + ids_bySideOfStreet_left);
		System.out.println("Graffiti on Right of Street 1: " + ids_bySideOfStreet_right);
		System.out.println("Graffiti on Property 420: " + ids_byProperty);
		System.out.println("Graffiti on Insula 605: " + ids_byInsula);
		System.out.println("Graffiti on City Pompeii: " + ids_byCity_pompeii);
		System.out.println("Graffiti on City Herculaneum: " + ids_byCity_herc);
	}
	
	private static void insertPropertiesOnStreet() {
		try {
			PreparedStatement pstmt = newDBCon.prepareStatement(INSERT_PROPERTIES_ON_STREET);
			//Insert Properties on the street that are part of Insula VI.14
			pstmt.setInt(1, 605);
			for (int i= 400; i < 409; i++) {
				pstmt.setInt(2, i);
				pstmt.setString(3, "Left");
				pstmt.executeUpdate();
			}
			//Insert Properties on the street that are part of Insula V.1
			pstmt.setInt(1, 606);
			for (int i= 413; i < 427; i++) {
				pstmt.setInt(2, i);
				pstmt.setString(3, "Right");
				pstmt.executeUpdate();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	
	private static void insertGraffitiOnProperty(String edrNumber, Integer property_id, Integer segment_id) {
		try {
			PreparedStatement pstmt = newDBCon.prepareStatement(INSERT_GRAFFITI_INTO_EDR);
			pstmt.setString(1, edrNumber);
			pstmt.executeUpdate();
			pstmt = newDBCon.prepareStatement(INSERT_GRAFFITI_INTO_AGP);
			pstmt.setString(1, edrNumber);
			pstmt.setInt(2, property_id);
			pstmt.setInt(3, segment_id);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	//This method results in an inaccurate listing of graffiti in some cases (use only to demonstrate potential pitfalls)
	private static ArrayList<String> getFacadesOnStreet(Integer streetId){
		ArrayList<String> edrIds = new ArrayList<String>();
		try {
			Statement stmt1 = newDBCon.createStatement();
			Statement stmt2 = newDBCon.createStatement();
			ResultSet rs = stmt1.executeQuery("SELECT property_id FROM property_on_streets WHERE street_id=" + streetId.toString());
			while(rs.next()) {
				Integer propId = rs.getInt("property_id");
				edrIds.addAll(getFacadesByProperty(propId));
//				ResultSet rs2 = stmt2.executeQuery("SELECT edr_id FROM agp_inscription_info WHERE property_id=" + propId.toString() + "AND on_facade=true");
//				while(rs2.next()) {
//					edrIds.add(rs2.getString("edr_id"));
//				}
//				rs2.close();
			}
			rs.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return edrIds;
	}
	
	private static void insertPropertiesOnSegment() {
		try {
			Statement stmt = newDBCon.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT property_id, side_of_street FROM property_on_streets WHERE street_id=1");
			while(rs.next()) {
				Integer propId = rs.getInt("property_id");
				String side = rs.getString("side_of_street");
				PreparedStatement pstmt = newDBCon.prepareStatement("INSERT INTO property_on_segments (seg_id, property_id, side_of_segment) VALUES (1, ?, ?)");
				pstmt.setInt(1, propId);
				pstmt.setString(2, side);
				pstmt.executeUpdate();
			}
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	//Return all the graffiti on facades in the data base
	private static ArrayList<String> getFacades(){
		ArrayList<String> edrIds = new ArrayList<String>();
		try {
			Statement stmt = newDBCon.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT edr_id FROM agp_inscription_info WHERE on_facade=true");
			while(rs.next()) {
				edrIds.add(rs.getString("edr_id"));
			}
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return  edrIds;
	}

	//Return All the Graffiti Written on Facades of this street (Uses the new segmentation method)
	private static ArrayList<String> getFacadesByStreet(Integer street_id){
		ArrayList<String> edrIds = new ArrayList<String>();
		try {
			Statement stmt = newDBCon.createStatement();
			Statement stmt2 = newDBCon.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT id FROM segments WHERE street_id=" + street_id.toString());
			while(rs.next()) {
				Integer segId = rs.getInt("id");
				ResultSet rs2 = stmt2.executeQuery("SELECT edr_id FROM agp_inscription_info WHERE segment_id=" + segId.toString() + "AND on_facade=true ");
				while(rs2.next()) {
					edrIds.add(rs2.getString("edr_id"));
				}
				rs2.close();
			}
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return  edrIds;
	}
	
	//Return All the Graffiti Written on Facades of this street (Uses street_id in agp_inscription_info)
	private static ArrayList<String> getFacadesBySegment(Integer segment_id){
		ArrayList<String> edrIds = new ArrayList<String>();
		try {
			Statement stmt = newDBCon.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT edr_id FROM agp_inscription_info WHERE segment_id=" + segment_id.toString() + 
					" AND on_facade=true");
			while(rs.next()) {
				edrIds.add(rs.getString("edr_id"));
			}
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return  edrIds;
	}
	
	//Return All the Graffiti Written on Facades on the provided side of this street
	//Checks the street_id in agp_inscription_info after finding all the properties to prevent errors
	//resulting from buildings found on corners or that have faces on more than one street.
	private static ArrayList<String> getFacadesBySideOfTheStreet(Integer street_id, String side){
		ArrayList<String> edrIds = new ArrayList<String>();
		try {
			Statement stmt1 = newDBCon.createStatement();
			ResultSet rs = stmt1.executeQuery("SELECT id FROM segments WHERE street_id=" + street_id.toString());
			while(rs.next()) {
				Integer segId = rs.getInt("id");
				edrIds.addAll(getFacadesBySideOfSegment(segId, side));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return  edrIds;
	}
	
	//Return All the Graffiti Written on Facades on the provided side of this segment
	private static ArrayList<String> getFacadesBySideOfSegment(Integer segment_id, String side){
		ArrayList<String> edrIds = new ArrayList<String>();
		try {
			Statement stmt1 = newDBCon.createStatement();
			Statement stmt2 = newDBCon.createStatement();
			ResultSet rs = stmt1.executeQuery("SELECT property_id FROM property_on_segments WHERE seg_id=" + segment_id.toString() + 
					" AND side_of_segment='" + side +"'");
			while(rs.next()) {
				Integer propId = rs.getInt("property_id");
				ResultSet rs2 = stmt2.executeQuery("SELECT edr_id FROM agp_inscription_info WHERE property_id=" + propId.toString() + "AND on_facade=true "+
				"AND segment_id=" + segment_id.toString());
				while(rs2.next()) {
					edrIds.add(rs2.getString("edr_id"));
				}
				rs2.close();
			}
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return  edrIds;
	}
	
	//Return all the graffiti on the provided property
	private static ArrayList<String> getFacadesByProperty(Integer propId){
		ArrayList<String> edrIds = new ArrayList<String>();
		try {
			Statement stmt = newDBCon.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT edr_id FROM agp_inscription_info WHERE property_id=" + propId.toString() + 
					" AND on_facade=true");
			while(rs.next()) {
				edrIds.add(rs.getString("edr_id"));
			}
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return  edrIds;
	}
	
	//Return all the graffiti on the provided property
	private static ArrayList<String> getFacadesByInsula(Integer insulaId){
		ArrayList<String> edrIds = new ArrayList<String>();
		try {
			Statement stmt = newDBCon.createStatement();
			Statement stmt2 = newDBCon.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT id FROM properties WHERE insula_id=" + insulaId.toString());
			while(rs.next()) {
				Integer propId = rs.getInt("id");
				edrIds.addAll(getFacadesByProperty(propId));
//				ResultSet rs2 = stmt2.executeQuery("SELECT edr_id FROM agp_inscription_info WHERE property_id=" + propId.toString() + "AND on_facade=true");
//				while(rs2.next()) {
//					edrIds.add(rs2.getString("edr_id"));
//				}
//				rs2.close();
			}
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return  edrIds;
	}
	
	//Return all the graffiti on the provided property
	private static ArrayList<String> getFacadesByCity(String cityName){
		ArrayList<String> edrIds = new ArrayList<String>();
		try {
			Statement stmt = newDBCon.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT id FROM insula WHERE modern_city='" + cityName + "'"); 
			while(rs.next()) {
				Integer insulaId = rs.getInt("id");
				edrIds.addAll(getFacadesByInsula(insulaId));
			}
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return  edrIds;
	}
	
	//Returns all properties that have graffiti on facades
	private static ArrayList<Integer> getPropertiesWithFacades(){
		ArrayList<Integer> propIds = new ArrayList<Integer>();
		Integer id;
		try {
			Statement stmt = newDBCon.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT property_id FROM agp_inscription_info WHERE on_facade=true");
			while(rs.next()) {
				id = rs.getInt("property_id");
				if (!propIds.contains(id)) {
					propIds.add(id);
				}
			}
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return  propIds;
	}
	
	//Returns all streets that have graffiti on facades
	private static ArrayList<Integer> getStreetsWithFacades(){
		ArrayList<Integer> streetIds = new ArrayList<Integer>();
		Integer id;
		try {
			Statement stmt = newDBCon.createStatement();
			Statement stmt2 = newDBCon.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT segment_id FROM agp_inscription_info WHERE on_facade=true");
			while(rs.next()) {
				Integer segId = rs.getInt("segment_id");
				ResultSet rs2 = stmt2.executeQuery("SELECT street_id FROM segments WHERE id=" + segId.toString());
				while(rs2.next()) {
					id = rs2.getInt("street_id");
					if (!streetIds.contains(id)) {
						streetIds.add(id);
					}
				}
				rs2.close();
			}
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return  streetIds;
	}
	
	//Returns all segments that have graffiti on facades
	private static ArrayList<Integer> getSegmentsWithFacades(){
		ArrayList<Integer> segmentIds = new ArrayList<Integer>();
		Integer id;
		try {
			Statement stmt = newDBCon.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT segment_id FROM agp_inscription_info WHERE on_facade=true");
			while(rs.next()) {
				id = rs.getInt("segment_id");
				if (!segmentIds.contains(id)) {
					segmentIds.add(id);
				}
			}
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return  segmentIds;
	}
	
	//Returns all streets that have graffiti on facades
	private static ArrayList<Integer> getInsulaeWithFacades(){
		ArrayList<Integer> insulaIds = new ArrayList<Integer>();
		Integer id;
		try {
			Statement stmt = newDBCon.createStatement();
			Statement stmt2 = newDBCon.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT property_id FROM agp_inscription_info WHERE on_facade=true");
			while(rs.next()) {
				Integer propId = rs.getInt("property_id");
				ResultSet rs2 = stmt2.executeQuery("SELECT insula_id FROM properties WHERE id=" + propId.toString());
				while(rs2.next()) {
					id = rs2.getInt("insula_id");
					if (!insulaIds.contains(id)) {
						insulaIds.add(id);
					}
				}
				rs2.close();
			}
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return  insulaIds;
	}
	
	//Return all cities that have graffiti on facades
	private static ArrayList<String> getCitiesWithFacades(){
		ArrayList<String> cityNames = new ArrayList<String>();
		String name;
		try {
			Statement stmt = newDBCon.createStatement();
			ArrayList<Integer> insulaeWithFacades= getInsulaeWithFacades();
			for (int i = 0; i < insulaeWithFacades.size(); i++) {
				ResultSet rs = stmt.executeQuery("SELECT modern_city FROM insula WHERE id=" + insulaeWithFacades.get(i).toString());
				while(rs.next()) {
					name = rs.getString("modern_city");
					if (!cityNames.contains(name)) {
						cityNames.add(name);
					}
				}
				rs.close();
			}	
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return  cityNames;
	}
	
	//Methods for database operations
	
	private static void init() {
		getConfigurationProperties();

		try {
			Class.forName(DB_DRIVER);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		try {
			newDBCon = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void getConfigurationProperties() {
		Properties prop = Utils.getConfigurationProperties();

		DB_DRIVER = prop.getProperty("db.driverClassName");
		DB_URL = prop.getProperty("db.url");
		DB_USER = prop.getProperty("db.user");
		DB_PASSWORD = prop.getProperty("db.password");
	}
	
}
