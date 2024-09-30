package edu.wlu.graffiti.data.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.RowMapper;

/**
 * 
 * @author Jared Cordova
 *
 */
public final class FiguralCaptionRowMapper implements RowMapper<List<String>> {
	public List<String> mapRow(final ResultSet rs, final int rowNum) throws SQLException {
		List<String> figuralCaptions = new ArrayList<String>();
		figuralCaptions.add(rs.getString(1));
		figuralCaptions.add(rs.getString(2));
		figuralCaptions.add(rs.getString(3));
		return figuralCaptions;
	}

}
