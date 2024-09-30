package edu.wlu.graffiti.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import edu.wlu.graffiti.data.rowmapper.UniqueRowMapper;
import edu.wlu.graffiti.data.setup.index.InsertIndexEntries;
import edu.wlu.graffiti.data.setup.Utils;

/**
 * Class to extract word types from the DB
 * 
 * @author Bancks Holmes, modifications by Emily Cohen and Joe Wen 
 * 
 */

@Repository
public class DisplayTermsDao {

	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	private static final String SELECT_DISPLAY = "SELECT DISTINCT term " + " FROM terms WHERE display = TRUE ORDER BY term";
	private static final String SELECT_BUFFER = "SELECT DISTINCT term " + " FROM terms WHERE display IS NULL ORDER BY term";
	private static final String SELECT_IGNORE = "SELECT DISTINCT term " + " FROM terms WHERE display = FALSE ORDER BY term";
	static Connection newDBCon;
	private static String DB_DRIVER;
	private static String DB_URL;
	private static String DB_USER;
	private static String DB_PASSWORD;


	private List<String> DisplayTerms = null;
	private List<String> BufferTerms = null;
	private List<String> IgnoreTerms = null;
	
	public List<String> getIndexTerms() {
		DisplayTerms = jdbcTemplate.query(SELECT_DISPLAY, new UniqueRowMapper());
		return DisplayTerms;
	}
	
	public List<String> getBufferTerms() {
		BufferTerms = jdbcTemplate.query(SELECT_BUFFER, new UniqueRowMapper());
		return BufferTerms;
	}

	public List<String> getIgnoreTerms() {
		IgnoreTerms = jdbcTemplate.query(SELECT_IGNORE, new UniqueRowMapper());
		return IgnoreTerms;
	}
	
	public void setIndexTerms(String[] list) throws SQLException {
		init();
		PreparedStatement ps = newDBCon.prepareStatement("UPDATE terms SET display = TRUE WHERE term =?;");
		for (String term: list) {
			ps.setString(1, stringToMixedDecimalAscii(term));
			ps.executeUpdate();	
		}
	}
	
	public void setBufferTerms(String[] list) throws SQLException {
		init();
		PreparedStatement ps = newDBCon.prepareStatement("UPDATE terms SET display = NULL WHERE term =?;");
		for (String term: list) {
			ps.setString(1, stringToMixedDecimalAscii(term));
			ps.executeUpdate();			
		}
	}
	public void setIgnoreTerms(String[] list) throws SQLException {
		init();
		PreparedStatement ps = newDBCon.prepareStatement("UPDATE terms SET display = FALSE WHERE term =?;");
		for (String term: list) {
			ps.setString(1, stringToMixedDecimalAscii(term));
			ps.executeUpdate();			
		}
	}
	/*
	* This function converts the rendered ascii characters to the decimal notation used by the database.
	* Without this function searches with special characters would not function
	*/
	public static String stringToMixedDecimalAscii(String term) {
		String cleanTerm="";
		for(int i=0;i<term.length();i++) {
			char character = term.charAt(i);
			if((int)character>126) {
				cleanTerm+="&#"+(int)character+";";
			}
			else {
				cleanTerm+=character;
			}
		}
		return cleanTerm;
	}
	public void populateIndex() {
		InsertIndexEntries.main(null);
	}
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