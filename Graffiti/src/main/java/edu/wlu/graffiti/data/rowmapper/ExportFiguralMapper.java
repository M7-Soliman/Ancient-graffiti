package edu.wlu.graffiti.data.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.RowMapper;

/**
 * For unidentified figural data points
 * 
 * @author Ana Estrada and Scott Walters
 * @editor Trevor Stalnaker
 *
 */
public final class ExportFiguralMapper implements RowMapper<List<String>> {
	public List<String> mapRow(final ResultSet rs, final int rowNum) throws SQLException {
		List<String> unidentifiedFig = new ArrayList<String>();
		unidentifiedFig.add(rs.getString(1));
		unidentifiedFig.add(rs.getString(2));
		unidentifiedFig.add(rs.getString(3));
		unidentifiedFig.add(rs.getString(4));
		unidentifiedFig.add(rs.getString(5));
		return unidentifiedFig;
	}

}