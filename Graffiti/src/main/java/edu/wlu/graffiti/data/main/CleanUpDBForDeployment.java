/**
 * 
 */
package edu.wlu.graffiti.data.main;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Remove the graffiti whose properties/findspots are not found in our database
 * 
 * @author Sara Sprenkle
 */
public class CleanUpDBForDeployment extends DBInteraction {

	public static String DELETE_INSCRIPTIONS = "DELETE FROM inscriptions WHERE property_id IS NULL AND on_facade IS NULL AND graffiti_id in (select inscriptions.graffiti_id from inscriptions where ancient_city='Pompeii');";

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		CleanUpDBForDeployment cleanUp = new CleanUpDBForDeployment();
		cleanUp.runDBInteractions();
	}

	/**
	 * The database doesn't have all the possible find spots. We only want the
	 * inscriptions whose findspots are in the database.
	 */
	private void removeInscriptionsWithoutKnownFindSpot() {
		try {
			PreparedStatement deleteAGPInfo = dbCon.prepareStatement(DELETE_INSCRIPTIONS);
			deleteAGPInfo.execute();
			deleteAGPInfo.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}


	@Override
	public void run() {
		removeInscriptionsWithoutKnownFindSpot();
	}

}
