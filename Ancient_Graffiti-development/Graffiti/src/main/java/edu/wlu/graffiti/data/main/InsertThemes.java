package edu.wlu.graffiti.data.main;

import java.io.FileReader;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Properties;

/**
 * Insert the themes for featured graffiti
 * 
 * @author Hammad Ahmad
 *
 */
public class InsertThemes extends DBInteraction {

	private static final String THEMES_DATA_FILE = "data/themes.prop";

	private static final String INSERT_THEMES = "INSERT INTO themes " + "(name, description) " + "VALUES (?, ?)";

	public static void main(String[] args) {
		InsertThemes it = new InsertThemes();
		it.runDBInteractions();
	}

	@Override
	public void run() {
		insertThemes();
	}

	private void insertThemes() {
		try {
			PreparedStatement pstmt = dbCon.prepareStatement(INSERT_THEMES);
			Properties themes = new Properties();

			themes.load(new FileReader(THEMES_DATA_FILE));
			Enumeration<Object> propKeys = themes.keys();

			while (propKeys.hasMoreElements()) {

				Object themeType = propKeys.nextElement();
				Object themeDescription = themes.get(themeType);
				System.out.println(themeType + ": " + themeDescription);

				pstmt.setString(1, (String) themeType);
				pstmt.setString(2, (String) themeDescription);
				pstmt.executeUpdate();
			}
			pstmt.close();
		} catch (IOException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
