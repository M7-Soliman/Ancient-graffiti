/**
 * 
 */
package edu.wlu.graffiti.data.main;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

/**
 * Reset everything in the database based on the config file.
 * 
 * @author Sara Sprenkle
 *
 */
public class ResetData extends DBInteraction {

	private static final String DB_TABLE_NAMES_FILENAME = "data/db_table_names.txt";
	private static final String DB_SEQUENCE_NAMES_FILENAME = "data/db_sequence_names.txt";

	private static final String DELETE_TABLE_SQL = "DELETE FROM ";
	private static final String RESET_SEQUENCE_SQL_PREFIX = "ALTER SEQUENCE ";
	private static final String RESET_SEQUENCE_SQL_SUFFIX = " restart;";

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ResetData reset = new ResetData();
		reset.runDBInteractions();
	}

	/**
	 * Resets all data in the database, including deleting the data from the tables
	 * and restarting the sequences
	 * 
	 * @throws SQLException
	 * @throws FileNotFoundException
	 */
	public void run() {
		try {
			deleteDataFromTables();
		} catch (FileNotFoundException | SQLException e) {
			e.printStackTrace();
			return;
		}
		System.out.println("Deleted contents of all tables in the database");
		try {
			resetSequences();
		} catch (FileNotFoundException | SQLException e) {
			e.printStackTrace();
			return;
		}
		System.out.println("Reset all sequences in the database");
	}

	/**
	 * Deletes data from the tables
	 * 
	 * @throws SQLException
	 * @throws FileNotFoundException
	 */
	private void deleteDataFromTables() throws SQLException, FileNotFoundException {
		try {
			Statement restart = dbCon.createStatement();

			Scanner reader = new Scanner(new File(DB_TABLE_NAMES_FILENAME));
			while (reader.hasNextLine()) {
				String line = reader.nextLine();
				String table = line.trim();
				restart.addBatch(DELETE_TABLE_SQL + table + ";");
			}
			reader.close();

			restart.executeBatch();
			restart.close();
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			throw e;
		}
	}

	/**
	 * Resets the sequences
	 * 
	 * @throws SQLException
	 * @throws FileNotFoundException
	 */
	private void resetSequences() throws SQLException, FileNotFoundException {
		try {
			Statement restart = dbCon.createStatement();
			Scanner reader = new Scanner(new File(DB_SEQUENCE_NAMES_FILENAME));
			while (reader.hasNextLine()) {
				String line = reader.nextLine();
				String sequence = line.trim();
				restart.addBatch(RESET_SEQUENCE_SQL_PREFIX + sequence + RESET_SEQUENCE_SQL_SUFFIX);
			}
			reader.close();
			restart.executeBatch();
			restart.close();
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			throw e;
		}
	}

}
