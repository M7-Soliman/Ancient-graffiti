package edu.wlu.graffiti.data.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.RowMapper;

/**
 * 
 * @author Ana Estrada and Scott Walters
 * @author Trevor Stalnaker
 *
 */
public final class ExportMissingFiguralInfoRowMapper implements RowMapper<List<String>> {
	public List<String> mapRow(final ResultSet rs, final int rowNum) throws SQLException {
		List<String> missing = new ArrayList<String>();
		missing.add(rs.getString(1));
		missing.add(rs.getString(2));
		missing.add(rs.getString(3));
		missing.add(rs.getString(4));
		missing.add(rs.getString(5));
		missing.add(rs.getString(6));
		missing.add(rs.getString(7));
		missing.add(rs.getString(8));
		return missing;
	}
}