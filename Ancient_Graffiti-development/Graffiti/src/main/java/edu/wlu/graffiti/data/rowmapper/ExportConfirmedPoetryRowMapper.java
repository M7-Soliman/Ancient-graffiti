package edu.wlu.graffiti.data.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.RowMapper;

/**
 * 
 * @author Jack Sorenson
 *
 */
public final class ExportConfirmedPoetryRowMapper implements RowMapper<List<String>> {
	public List<String> mapRow(final ResultSet rs, final int rowNum) throws SQLException {
		List<String> poem = new ArrayList<String>();
		poem.add(rs.getString(1)); //EDR
		poem.add(rs.getString(2)); //Apparatus
		poem.add(rs.getString(3)); //Text
		poem.add(rs.getString(4)); //Meter in original language
		poem.add(rs.getString(5)); //Author

		return poem;
	}
}
