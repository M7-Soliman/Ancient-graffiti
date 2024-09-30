package edu.wlu.graffiti.data.setup;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import edu.wlu.graffiti.data.main.DBInteraction;

public class RemoveSegmentsWithNoGraffiti extends DBInteraction {
	
	final static String GET_SECTIONS_STATEMENT = "SELECT id FROM segments";
	
	final static String GET_COUNT_ON_SECTION = "SELECT COUNT(*) as count FROM inscriptions "
			+ "LEFT JOIN segments ON inscriptions.segment_id=segments.id "
			+ "WHERE inscriptions.on_facade=true AND segments.id=?";
	
	private static String REMOVE_SECTION = "DELETE FROM segments WHERE id=?";
	
	public static void main(String args[]) {
		RemoveSegmentsWithNoGraffiti remover = new RemoveSegmentsWithNoGraffiti();
		remover.runDBInteractions();
	}
	
	@Override
	public void run() {
		removeSections();
	}
	
	public void removeSections() {
		try {
			
			PreparedStatement getCount = dbCon.prepareStatement(GET_COUNT_ON_SECTION);
			PreparedStatement remove = dbCon.prepareStatement(REMOVE_SECTION);
			
			Statement stmt = dbCon.createStatement();
			ResultSet rs = stmt.executeQuery(GET_SECTIONS_STATEMENT);
			while (rs.next()) {
				int ID = rs.getInt("id");
				getCount.setInt(1, ID);
				ResultSet rs2 = getCount.executeQuery();
				if (rs2.next()) {
					if (rs2.getInt("count") == 0) {
						remove.setInt(1, ID);
						remove.execute();
					}
				}
			}
			
			stmt.close();
			getCount.close();
			remove.close();
		}
		catch (SQLException e){}
	}

}
