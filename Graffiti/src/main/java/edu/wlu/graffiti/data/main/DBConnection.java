/**
 * 
 */
package edu.wlu.graffiti.data.main;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import edu.wlu.graffiti.data.setup.Utils;

/**
 * Class that handles making the database connection, based on the
 * configuration.properties file.
 * 
 * @author Sara Sprenkle
 *
 */
public class DBConnection {

	protected String DB_DRIVER;
	protected String DB_URL;
	protected String DB_USER;
	protected String DB_PASSWORD;
	protected Connection dbCon;
	
	public DBConnection() {
		init();
	}

	/**
	 * Set the configuration properties from the configuration.properties file.
	 */
	protected void setConfigurationProperties() {
		Properties prop = Utils.getConfigurationProperties();

		DB_DRIVER = prop.getProperty("db.driverClassName");
		DB_URL = prop.getProperty("db.url");
		DB_USER = prop.getProperty("db.user");
		DB_PASSWORD = prop.getProperty("db.password");
	}

	/**
	 * Initialize the database connection, based on the configuration properties
	 */
	protected void init() {
		setConfigurationProperties();

		try {
			Class.forName(DB_DRIVER);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		try {
			dbCon = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Close the database connection.
	 */
	protected void close() {
		try {
			dbCon.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
