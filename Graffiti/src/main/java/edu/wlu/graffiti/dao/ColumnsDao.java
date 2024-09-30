package edu.wlu.graffiti.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import edu.wlu.graffiti.bean.Column;
import edu.wlu.graffiti.data.rowmapper.ColumnRowMapper;

/**
 * 
 * @author Trevor Stalnaker
 *
 */

@Repository
public class ColumnsDao {
	
	@Autowired
	private JdbcTemplate jdbcTemplate;

	private static final String SELECT_STATEMENT = "SELECT * FROM columns ORDER BY decimal ASC";
	private static final String SELECT_BY_ID = "SELECT * FROM columns WHERE id=?";
	
	private List<Column> columns = null;

	public List<Column> getColumns() {
		columns = jdbcTemplate.query(SELECT_STATEMENT, new ColumnRowMapper());
		return columns;
	}
	
	public Column getColumnByID(int id){
		columns = jdbcTemplate.query(SELECT_BY_ID, new ColumnRowMapper(), id);
		return columns.get(0);
	}
}
