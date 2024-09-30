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
public final class ExportLangnerMapper implements RowMapper<List<String>> {
	public List<String> mapRow(final ResultSet rs, final int rowNum) throws SQLException {
		List<String> langner = new ArrayList<String>();
		langner.add(rs.getString(1));
		langner.add(rs.getString(2));
		langner.add(rs.getString(3));
		langner.add(rs.getString(4));
		langner.add(rs.getString(5));
		langner.add(rs.getString(6));
		return langner;
	}
}