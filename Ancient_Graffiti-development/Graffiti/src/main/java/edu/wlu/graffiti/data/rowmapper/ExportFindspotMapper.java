package edu.wlu.graffiti.data.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.RowMapper;

/**
 * 
 * @author Grace MacDonald
 *
 */
public final class ExportFindspotMapper implements RowMapper<List<String>> {
	public List<String> mapRow(final ResultSet rs, final int rowNum) throws SQLException {
		List<String> findspot = new ArrayList<String>();
		findspot.add(rs.getString(1));
		findspot.add(rs.getString(2));
		findspot.add(rs.getString(3));
		findspot.add(rs.getString(4));
		return findspot;
	}

}