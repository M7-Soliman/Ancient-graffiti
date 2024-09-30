package edu.wlu.graffiti.data.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import edu.wlu.graffiti.bean.Column;
/**
 * 
 * @author Trevor Stalnaker
 *
 */
public final class ColumnRowMapper implements RowMapper<Column> {
	
	public Column mapRow(final ResultSet resultSet, final int rowNum) throws SQLException {
		final Column column = new Column();
		int id = resultSet.getInt("id");
		int decimal = resultSet.getInt("decimal_number");
		String numeral  = resultSet.getString("roman_numeral");
		column.setId(id);
		column.setRomanNumeral(numeral);
		column.setDecimal(decimal);
		return column;
	}
}
