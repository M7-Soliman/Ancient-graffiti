package edu.wlu.graffiti.data.setup;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import edu.wlu.graffiti.data.main.DBInteraction;

/**
 * This class looks at the apparatus field and adds links to EDR for entries, as
 * appropriate.
 * 
 * Can be called as stand-alone script or from other classes.
 * 
 * @author Sara Sprenkle
 * 
 */
public class AddEDRLinksToApparatus extends DBInteraction {

	final static String SELECT_GRAFFITI = "SELECT apparatus, graffiti_id from inscriptions";

	private static final String UPDATE_APPARATUS_TO_DISPLAY = "UPDATE inscriptions SET apparatus_displayed = ? WHERE graffiti_id = ?";

	private static final String URL_BASE = "http://ancientgraffiti.org/Graffiti/graffito/AGP-";

	private PreparedStatement updateApparatusStmt;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		AddEDRLinksToApparatus linkAdder = new AddEDRLinksToApparatus();
		linkAdder.runDBInteractions();
	}

	@Override
	public void run() {
		addEDRLinksToApparatus();
	}

	public void addEDRLinksToApparatus() {

		try {
			updateApparatusStmt = dbCon.prepareStatement(UPDATE_APPARATUS_TO_DISPLAY);

			// Get list of edrIDs
			PreparedStatement extractData = dbCon.prepareStatement(SELECT_GRAFFITI);
			ResultSet rs = extractData.executeQuery();

			// Update apparatus for each entry where EDR entries are in the
			// apparatus.
			while (rs.next()) {
				String graffiti_id = rs.getString("graffiti_id");
				String apparatus = rs.getString("apparatus");
				System.out.println("Updating apparatus for " + graffiti_id);
				String displayApparatus = addLinks(apparatus);
				updateDisplayApparatus(graffiti_id, displayApparatus);
			}
			rs.close();
			extractData.close();
			updateApparatusStmt.close();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
	}

	/**
	 * Helper method to modify the apparatus to include links
	 * 
	 * @param apparatus
	 * @return the apparatus so that it contains links to the other entries
	 *         referenced
	 */
	private static String addLinks(String apparatus) {
		if (apparatus.contains("EDR")) {
			String[] components = apparatus.split("\\s");
			StringBuilder displayApparatus = new StringBuilder();
			for (String component : components) {
				if (component.startsWith("EDR")) {
					// know the EDR id is 9 characters
					String graffiti_id = "";
					if (component.length() >= 9) {
						graffiti_id = component.substring(0, 9);
					} else {
						graffiti_id = component;
						System.out.println("This edrID is too short to update apparatus: " + component);
					}
					displayApparatus.append(" <a href=\"");
					displayApparatus.append(URL_BASE);
					// clean up the component -- remove punctuation
					displayApparatus.append(graffiti_id);
					displayApparatus.append("\" title=\"See Details\">");
					displayApparatus.append(component);
					displayApparatus.append("</a> ");
				} else {
					displayApparatus.append(component + " ");
				}
			}
			return displayApparatus.toString();
		} else {
			return apparatus;
		}
	}

	/**
	 * @param graffiti_id
	 * @param apparatusDisplay
	 */
	private void updateDisplayApparatus(String graffiti_id, String apparatusDisplay) {
		try {
			updateApparatusStmt.setString(1, apparatusDisplay);
			updateApparatusStmt.setString(2, graffiti_id);
			updateApparatusStmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}