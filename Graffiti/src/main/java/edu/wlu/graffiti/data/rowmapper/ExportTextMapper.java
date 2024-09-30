package edu.wlu.graffiti.data.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.RowMapper;

/**
 * 
 * @author Ana Estrada and Scott Walters
 * @authoor Grace MacDonald
 *
 */
public final class ExportTextMapper implements RowMapper<List<String>> {
	public List<String> mapRow(final ResultSet rs, final int rowNum) throws SQLException {
		List<String> textData = new ArrayList<String>();
		textData.add(rs.getString(1));
		textData.add(rs.getString(2));
		textData.add(rs.getString(3));
		textData.add(rs.getString(4));
		textData.add(rs.getString(5));
		textData.add(rs.getString(6));
		textData.add(rs.getString(7));
		textData.add(rs.getString(8));
		textData.add(rs.getString(9));
		return textData;
	}

}