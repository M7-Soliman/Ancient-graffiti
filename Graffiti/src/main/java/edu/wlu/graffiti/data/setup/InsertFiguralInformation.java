package edu.wlu.graffiti.data.setup;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import edu.wlu.graffiti.bean.DrawingTag;
import edu.wlu.graffiti.data.main.DBInteraction;

/**
 * Inserts Description, Translations, Drawing Tags, Captions of figural graffiti
 * from spreadsheet into database
 * 
 * @author Sara Sprenkle
 *
 */
public class InsertFiguralInformation extends DBInteraction {

	// These are the locations of the data within the CSV file
	private static final int LOCATION_OF_GRAFFITI_ID = 0;
	private static final int LOCATION_OF_DRAWING_TAGS = 9;
	private static final int LOCATION_OF_LATIN_DESCRIPTION = 11;
	private static final int LOCATION_OF_ENGLISH_DESCRIPTION = 12;

	private static final String GET_CIL = "SELECT cil FROM inscriptions WHERE graffiti_id=?";

	private static final String INSERT_DESC_TRANS = "INSERT INTO figural_graffiti_info (graffiti_id, description_in_latin, description_in_english) VALUES (?, ?, ?)";

	private static final String INSERT_DRAWING_TAG_MAPPING = "INSERT INTO graffitotodrawingtags(graffito_id, drawing_tag_id) "
			+ "VALUES (?,?)";
	private static final String UPDATE_FIGURAL_COMPONENT = "UPDATE inscriptions SET has_figural_component=True WHERE graffiti_id=?";
	private static final String UPDATE_ANNOTATION_STMT = "UPDATE inscriptions "
			+ "SET caption = ?, langner = ?, cil = ? WHERE graffiti_id = ? ";

	public static void main(String[] args) {
		InsertFiguralInformation inserter = new InsertFiguralInformation();
		inserter.runDBInteractions();
	}
	
	@Override
	public void run() {
		insertFiguralInfo();
	}

	public void insertFiguralInfo() {
		updateFiguralInfo("data/AGPData/herc_figural.csv");
		updateFiguralInfo("data/AGPData/pompeii_figural.csv");
	}

	private void updateFiguralInfo(String datafileName) {
		PreparedStatement annotationUpdateStmt = null;
		PreparedStatement insertFigInfo = null;

		PreparedStatement dtStmt = null;
		PreparedStatement figCompStmt = null;
		Map<String, DrawingTag> drawingTags = getDrawingTags();

		Iterable<CSVRecord> records;

		try {
			Reader in = new InputStreamReader(new FileInputStream(datafileName), "UTF-8");
			records = CSVFormat.EXCEL.parse(in);
		} catch (IOException e1) {
			e1.printStackTrace();
			return;
		}

		try {
			insertFigInfo = dbCon.prepareStatement(INSERT_DESC_TRANS);
			annotationUpdateStmt = dbCon.prepareStatement(UPDATE_ANNOTATION_STMT);
			dtStmt = dbCon.prepareStatement(INSERT_DRAWING_TAG_MAPPING);
			figCompStmt = dbCon.prepareStatement(UPDATE_FIGURAL_COMPONENT);

			for (CSVRecord record : records) {
				String graffiti_id = Utils.cleanData(record.get(LOCATION_OF_GRAFFITI_ID));
				String cil = Utils.cleanData(record.get(1));
				String langner = Utils.cleanData(record.get(3));
				String summary = Utils.cleanData(record.get(10));
				String description_in_english = Utils.cleanData(record.get(LOCATION_OF_ENGLISH_DESCRIPTION));
				String description_in_latin = Utils.cleanData(record.get(LOCATION_OF_LATIN_DESCRIPTION));
				String drawingTag = Utils.cleanData(record.get(LOCATION_OF_DRAWING_TAGS));

				// Get the CIL in the database
				PreparedStatement getCIL = dbCon.prepareStatement(GET_CIL);
				getCIL.setString(1, graffiti_id);
				ResultSet rs = getCIL.executeQuery();
				String cil_in_database = "";
				if (rs.next()) {
					cil_in_database = rs.getString("cil");
				}

				if (!cil_in_database.equals("")) {
					cil = cil_in_database;
				}

				try {
					insertFigInfo.setString(1, graffiti_id);
					insertFigInfo.setString(2, description_in_latin);
					insertFigInfo.setString(3, description_in_english);

					figCompStmt.setString(1, graffiti_id);

					insertFigInfo.executeUpdate();
					figCompStmt.executeUpdate();

					annotationUpdateStmt.setString(1, summary);
					annotationUpdateStmt.setString(2, langner);
					annotationUpdateStmt.setString(3, cil);
					annotationUpdateStmt.setString(4, graffiti_id);

					annotationUpdateStmt.executeUpdate();

				} catch (SQLException e) {
					System.err.println("Error updating figural info for " + graffiti_id);

					e.printStackTrace();
				}

				if (!drawingTag.isEmpty()) {
					String[] tags = drawingTag.split(", ");

					for (String tag : tags) {
						if (drawingTags.containsKey(tag)) {
							DrawingTag dt = drawingTags.get(tag);
							try {
								dtStmt.setString(1, graffiti_id);
								dtStmt.setInt(2, dt.getId());
								dtStmt.executeUpdate();
							} catch (SQLException e) {
								System.err.println("Graffiti id: " + graffiti_id);
								System.err.println(dt);
								e.printStackTrace();
							}
						} else {
							System.err.println(tag + " not one of the tags for " + graffiti_id);
						}
					}
				}
			}
			insertFigInfo.close();
			figCompStmt.close();
			dtStmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	private Map<String, DrawingTag> getDrawingTags() {
		Map<String, DrawingTag> drawingTags = new HashMap<String, DrawingTag>();

		try {
			PreparedStatement pstmt = dbCon.prepareStatement("SELECT id, name, description FROM drawing_tags");
			ResultSet rs = pstmt.executeQuery();

			while (rs.next()) {
				DrawingTag dt = new DrawingTag();
				dt.setId(rs.getInt(1));
				dt.setName(rs.getString(2));
				dt.setDescription(rs.getString(3));
				drawingTags.put(rs.getString(2), dt);
			}

			rs.close();
			pstmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return drawingTags;
	}

}
