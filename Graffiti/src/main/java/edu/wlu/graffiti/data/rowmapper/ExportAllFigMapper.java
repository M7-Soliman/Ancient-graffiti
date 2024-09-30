package edu.wlu.graffiti.data.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.RowMapper;

/**
 * 
 * @author Ana Estrada and Scott Walters
 *
 */
public final class ExportAllFigMapper implements RowMapper<List<String>> {
	public List<String> mapRow(final ResultSet rs, final int rowNum) throws SQLException {
		List<String> allFigural = new ArrayList<String>();

		for (int ndx = 1; ndx < 14; ndx++) {
			allFigural.add(rs.getString(ndx));
		}
		return allFigural;
	}
}