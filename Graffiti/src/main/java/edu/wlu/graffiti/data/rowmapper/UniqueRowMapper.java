package edu.wlu.graffiti.data.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
/**
* @author Pepe Estrada Hamm
* This class is designed to return a list of terms from a query to the graffiti_index database
*/
public class UniqueRowMapper implements RowMapper<String> {
	public String mapRow(ResultSet rs, int rowNum) throws SQLException {
		// TODO Auto-generated method stub 
		return rs.getString("term");
	}
}
