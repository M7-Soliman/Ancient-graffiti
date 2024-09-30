package edu.wlu.graffiti.data.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import edu.wlu.graffiti.bean.IndexTerm;

public class IndexTermRowMapper implements RowMapper<IndexTerm> {

/**	
 * @author: Bancks Holmes
 * @author: Trevor Stalnaker
 * 
 */
	
	public IndexTerm mapRow(ResultSet rs, int rowNum) throws SQLException {
		final IndexTerm indexterm = new IndexTerm();
		indexterm.setTermID(rs.getInt("term_id"));
		indexterm.setTerm(rs.getString("term"));
		indexterm.setCategory(rs.getString("category"));
		indexterm.setLanguage(rs.getString("language"));
		indexterm.setDisplay(rs.getBoolean("display"));	
		return indexterm;
	}
}

