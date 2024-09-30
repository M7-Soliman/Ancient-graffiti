package edu.wlu.graffiti.data.main;

import java.io.FileReader;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Properties;

/**
 * Inserts the drawing tags (categories) into the appropriate database table.
 * 
 * @author Sara Sprenkle
 *
 */
public class InsertDrawingTags extends DBInteraction {

	private static final String DRAWING_TAGS_DATA_FILE = "data/drawingTags.prop";

	private static final String INSERT_DRAWING_TAG = "INSERT INTO drawing_tags " + "(name, description) "
			+ "VALUES (?,?)";

	public static void main(String[] args) {
		InsertDrawingTags idt = new InsertDrawingTags();
		idt.runDBInteractions();
	}

	@Override
	public void run() {
		insertDrawingTags();
	}

	private void insertDrawingTags() {
		try {
			PreparedStatement pstmt = dbCon.prepareStatement(INSERT_DRAWING_TAG);
			Properties drawingTags = new Properties();

			drawingTags.load(new FileReader(DRAWING_TAGS_DATA_FILE));
			Enumeration<Object> propKeys = drawingTags.keys();

			while (propKeys.hasMoreElements()) {

				Object drawingType = propKeys.nextElement();
				Object drawingDescription = drawingTags.get(drawingType);
				System.out.println(drawingType + ": " + drawingDescription);

				pstmt.setString(1, (String) drawingType);
				pstmt.setString(2, (String) drawingDescription);
				pstmt.executeUpdate();
			}
			pstmt.close();
		} catch (IOException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
