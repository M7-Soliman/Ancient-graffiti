package edu.wlu.graffiti.data.main;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

/**
 * Creates the table of property types, based on the data/property_types.csv
 * file
 * 
 * @author Sara Sprenkle
 * @author Abby Nason
 *
 */
public class InsertPropertyTypes extends DBInteraction {

	private static final String PROPERTY_TYPES_DATA_FILE = "data/property_types.csv";

	private static final String INSERT_PROPERTY_TYPE = "INSERT INTO propertyTypes "
			+ "(name, commentary, parent_id, is_parent) VALUES (?,?,?,?)";

	private static final String LOOKUP_PROPTYPE_ID = "SELECT id FROM propertytypes WHERE name=?";

	private PreparedStatement selectPropTypeStmt;

	public static void main(String[] args) {
		InsertPropertyTypes ipt = new InsertPropertyTypes();
		ipt.runDBInteractions();
	}

	@Override
	public void run() {
		insertPropertyTypes();
	}

	private void insertPropertyTypes() {
		try {
			PreparedStatement pstmt = dbCon.prepareStatement(INSERT_PROPERTY_TYPE);
			selectPropTypeStmt = dbCon.prepareStatement(LOOKUP_PROPTYPE_ID);

			Reader in = new FileReader(PROPERTY_TYPES_DATA_FILE);
			Iterable<CSVRecord> records = CSVFormat.EXCEL.parse(in);

			// loop through parent property types
			for (CSVRecord record : records) {
				String parentName = Utils.cleanData(record.get(0));
				String[] subcategories = Utils.cleanData(record.get(1)).split(",");
				String commentary = Utils.cleanData(record.get(2));
				int parent_id = 0;
				boolean is_parent = true;

				pstmt.setString(1, parentName);
				pstmt.setString(2, commentary);
				pstmt.setInt(3, parent_id);
				pstmt.setBoolean(4, is_parent);

				try {
					System.out.println(parentName + " " + parent_id);
					pstmt.executeUpdate();
				} catch (SQLException e) {
					e.printStackTrace();
				}

				// loop through children of this parent property type
				commentary = "";
				is_parent = false;
				for (String sub : subcategories) {
					// if parent has children in subcategory portion of csv
					if (!sub.equals("")) {
						parent_id = locatePropertyTypeId(parentName);
						pstmt.setString(1, sub);
						pstmt.setString(2, commentary);
						pstmt.setInt(3, parent_id);
						pstmt.setBoolean(4, is_parent);
						try {
							System.out.println(sub + " " + parent_id);
							pstmt.executeUpdate();
						} catch (SQLException e) {
							e.printStackTrace();
						}
					}
				}
			}

			in.close();
			pstmt.close();
		} catch (IOException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private int locatePropertyTypeId(String name) {
		int propID = 0;
		try {
			selectPropTypeStmt.setString(1, name);

			ResultSet propRS = selectPropTypeStmt.executeQuery();
			if (propRS.next()) {
				propID = propRS.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return propID;
	}

}
